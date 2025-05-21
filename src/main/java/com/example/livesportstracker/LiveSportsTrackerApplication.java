package com.example.livesportstracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.livesportstracker.interfaces.client")
@EnableScheduling
public class LiveSportsTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiveSportsTrackerApplication.class, args);
    }
}