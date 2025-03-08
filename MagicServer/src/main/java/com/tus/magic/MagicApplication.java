package com.tus.magic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MagicApplication {

	public static void main(String[] args) {
		SpringApplication.run(MagicApplication.class, args);
	}
}
