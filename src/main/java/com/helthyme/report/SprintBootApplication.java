package com.helthyme.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.helthyme.report")
public class SprintBootApplication {
    public static void main(String[] args) {
        SpringApplication.run(SprintBootApplication.class, args);
    }
}
