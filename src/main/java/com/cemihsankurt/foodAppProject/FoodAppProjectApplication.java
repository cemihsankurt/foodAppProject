package com.cemihsankurt.foodAppProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
@EntityScan("com.cemihsankurt.foodAppProject.entity")
public class FoodAppProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodAppProjectApplication.class, args);
	}

}
