package com.taxiproject.mytaxi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MytaxiApplication {

	public static void main(String[] args) {

		SpringApplication.run(MytaxiApplication.class, args);
	}

	@GetMapping
	public String helloWorld() {
		return "Hello, world";
	}
}
