package com.svt;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.svt.utils.fileShare.MyComponent;

@SpringBootApplication
public class SvtModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SvtModuleApplication.class, args);
	}
	
//	@Bean
//	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//		return args -> {
//			
//			MyComponent myc = ((BeanFactory) ctx).getBean(MyComponent.class);
//			myc.print();
//		};
//	}

}
