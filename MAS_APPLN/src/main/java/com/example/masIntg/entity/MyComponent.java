package com.example.masIntg.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyComponent {

	private static final Logger logger = LoggerFactory.getLogger(MyComponent.class);

	
	@Value("${app.user.home}")
	private String userhome;
	
	public void print() {
		System.out.println("User home directory path AAAAAAAAAAAAAAAAAAA:: "+userhome);
		logger.info("User home directory path AAAAAAAAAAAAAAAAAAA:: "+userhome);

	}
	
}
