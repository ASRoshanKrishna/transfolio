package com.transfolio.transfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class TransfolioApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransfolioApplication.class, args);
	}

	@GetMapping
	public static String printHello() {
		return "Hello World!";
	}
}
