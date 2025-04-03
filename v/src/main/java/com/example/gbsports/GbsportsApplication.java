package com.example.gbsports;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import vn.payos.PayOS;

@SpringBootApplication
public class GbsportsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GbsportsApplication.class, args);
    }

}
