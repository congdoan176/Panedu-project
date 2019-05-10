package com.fcs.lms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import com.fcs.lms.entity.Category;
import com.fcs.lms.entity.Course;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QuerySnapshot;

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
	@PostMapping(value = "/c/create")
	public String addCategory(@Valid Category category, String name, String url, Model model) {
		
		Firestore db = FirestoreOptions.getDefaultInstance().getService();
		Map<String, Object> categories = new HashMap<>();
		categories.put("name", name);
		categories.put("url", url);
		ApiFuture<DocumentReference> addedDocRef = db.collection("categories").add(categories);
		
		LOGGER.info("Show Category :"+ categories);
		
 		return "views/category/index";
	}
	
	@GetMapping(value = "/c/create")
	public String formCreat() {
		return "views/category/create";
	}
	
	@PutMapping(value = "/c/edit")
	public String updateCategory() {
		return null;
	}
	@GetMapping(value = "/c/delete/{id}")
	public String deleteCategory() {
		return null;
	}
}
