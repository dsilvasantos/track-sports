package com.example.livesportstracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LiveSportsTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiveSportsTrackerApplication.class, args);
    }
}