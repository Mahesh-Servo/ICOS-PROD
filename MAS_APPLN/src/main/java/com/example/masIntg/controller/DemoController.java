package com.example.masIntg.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/API")
public class DemoController {
	
	@GetMapping("/hello")
	public String DemoEx()
	{
		return "Hello World";
		
	}
	

	
}
