package com.example.realtimeproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class RealTimeProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealTimeProjectApplication.class, args);
    }

}
