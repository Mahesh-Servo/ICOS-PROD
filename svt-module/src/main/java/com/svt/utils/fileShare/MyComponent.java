package com.svt.utils.fileShare;

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
		System.out.println("MyComponent.print()" + userhome);
		System.out.println("svt-module.mycomponent.path user.home= " + userhome);
		logger.info("svt-module.mycomponent.path user.home= " + userhome);

	}

}
