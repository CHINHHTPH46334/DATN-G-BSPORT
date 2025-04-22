package com.example.gbsports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class GbsportsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GbsportsApplication.class, args);
    }

}
