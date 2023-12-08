package com.ysb.jwgio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication
public class JwGioApplication {


	public static void main(String[] args) {
		SpringApplication.run(JwGioApplication.class, args);
	}

}
