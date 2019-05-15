package com.fcs.lms.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JacksonInject.Value;
import com.fcs.lms.entity.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

@Controller
public class CategoriesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CategoriesController.class);
	
	//lay ra danh sach category
	
	@GetMapping(value = "/c")
	public String getList(Model model) throws InterruptedException, ExecutionException {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		ApiFuture<QuerySnapshot> queryCategory = db.collection("categories").get();
		ApiFuture<QuerySnapshot> queryCategory1 = null;
		List<Category> categories = queryCategory.get().toObjects(Category.class);
		for (Category category : categories) {
			queryCategory1 = db.collection("categories").whereEqualTo("name", category.getName()).get();
		}
		model.addAttribute("categories", categories);
		return "views/category/index";
		
	}
	
	// tao moi category
	@RequestMapping(value = "/c/create",method = RequestMethod.POST)
	public void addCategory(@Valid Category category, String name, String url, Model model, HttpServletResponse resp) throws IOException{
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		Map<String, Object> categories = new HashMap<>();
		categories.put("name", name);
		categories.put("url", url);
		ApiFuture<DocumentReference> addedDocRef = db.collection("categories").add(categories);
		LOGGER.info("Show Category :"+ categories);
		resp.sendRedirect("/c");
	}
	
	@GetMapping(value = "/c/create")
	public String formCreat() {
		return "views/category/create";
	}
	
	@GetMapping(value = "c/edit/{url}")
	public String showUpdateForm(@PathVariable("url") String url, Model model, HttpServletResponse resp,HttpServletRequest req) throws InterruptedException, ExecutionException {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		ApiFuture<QuerySnapshot> future =
			    db.collection("categories").whereEqualTo("url", url).get();
			// future.get() blocks on response
			List<QueryDocumentSnapshot> documents = future.get().getDocuments();
			Category category = new Category();
			for (DocumentSnapshot document : documents) {
			  System.out.println(document.getId());
			  if (document.exists()) {
				  // convert document to POJO
				  category = document.toObject(Category.class);
				  
				  System.out.println(category);
				} else {
				  System.out.println("No such document!");
				}
			}
			model.addAttribute("category", category);
			LOGGER.info("Category :"+ category);
			return "views/category/edit";
	}
	@RequestMapping(value = "/c/update/{url}",method = RequestMethod.POST)
	public void updateCategory(@PathVariable("url") String url, String name,Model model, HttpServletResponse resp) throws IOException, InterruptedException, ExecutionException {
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
//		ApiFuture<QuerySnapshot> future =
//			    db.collection("categories").whereEqualTo("url", url).get();
		DocumentReference documentRe = db.collection("categories").document(url);
		
		// Confirm that data has been successfully saved by blocking on the operation
		LOGGER.info("-----------" + documentRe);
		ApiFuture<DocumentSnapshot> future = documentRe.get();
		// ...
		// future.get() blocks on response
		DocumentSnapshot document = future.get();
		if (document.exists()) {
			Map<String, Object> upCategory = new HashMap<>();
			upCategory.put("name",name);
			upCategory.put("url",url);
			LOGGER.info("Update*********** "+ upCategory);
			model.addAttribute("category",upCategory);
			
			ApiFuture<WriteResult> futureCategory = documentRe.update(upCategory);
		} else {
		  System.out.println("No such document!");
		}
				
		resp.sendRedirect("/c");
	}
	@GetMapping(value = "/c/delete/{id}")
	public String deleteCategory() {
		return null;
	}
}
