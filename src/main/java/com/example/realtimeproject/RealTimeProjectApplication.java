package com.example.realtimeproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RealTimeProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealTimeProjectApplication.class, args);
    }

}
