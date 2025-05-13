package com.example.masIntg;

import org.springframework.context.ApplicationContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.masIntg.entity.MyComponent;

@SpringBootApplication
public class MasApplnApplication {

	public static void main(String[] args) {
		SpringApplication.run(MasApplnApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			
			MyComponent myc = ((BeanFactory) ctx).getBean(MyComponent.class);
			myc.print();
		};
	}

}
