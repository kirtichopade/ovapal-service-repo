package com.ovapal;

// Import necessary Spring Boot classes
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// Marks this class as the main entry point of the Spring Boot application
@SpringBootApplication
@ComponentScan(basePackages = "com.ovapal")
@EntityScan("com.ovapal.entity")
@EnableJpaRepositories("com.ovapal.repository")
public class OvaPalApplication {
    public static void main(String[] args) {
        // Launches the Spring Boot application
        SpringApplication.run(OvaPalApplication.class, args);
    }
} 