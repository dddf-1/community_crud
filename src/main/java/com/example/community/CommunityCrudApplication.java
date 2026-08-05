package com.example.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication

public class CommunityCrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityCrudApplication.class, args);
    }
}