package com.mumyungdiary.mmd_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MmdBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MmdBackendApplication.class, args);
	}

}
