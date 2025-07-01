package com.LsmFiServices.Utility;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyComponent {

    @Value("${app.user.home}")
    private String userHome;

    public String print() {
	String message = "LsmFiServices-1.0.1 application is started on -->" + LocalDateTime.now()
		+ " and User Home :: " + userHome;
	return message;
    }
}
