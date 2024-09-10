package com.example.SomeOne;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SomeOneApplication {

	public static void main(String[] args) {
		SpringApplication.run(SomeOneApplication.class, args);
	}

}
