package com.anonymous_diary.ad_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class AnonymousDiaryBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnonymousDiaryBackendApplication.class, args);
	}

}
