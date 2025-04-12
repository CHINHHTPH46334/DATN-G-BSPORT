package com.example.gbsports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
//@EnableCaching
public class GbsportsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GbsportsApplication.class, args);
    }

}
