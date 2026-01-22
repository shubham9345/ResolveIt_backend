package com.org.ResolveIt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class ResolveItApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResolveItApplication.class, args);
	}

}
