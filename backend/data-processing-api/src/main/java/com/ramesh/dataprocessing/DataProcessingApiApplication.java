package com.ramesh.dataprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class DataProcessingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataProcessingApiApplication.class, args);
    }

}
