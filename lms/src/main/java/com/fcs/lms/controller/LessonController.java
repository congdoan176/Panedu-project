package com.fcs.lms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LessonController {
	@GetMapping(value = "/l")
	public String listLesson() {
		return "views/lesson/index";
	}
}
