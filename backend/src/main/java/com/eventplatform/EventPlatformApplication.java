package com.eventplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.eventplatform.repository")
public class EventPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventPlatformApplication.class, args);
    }

}