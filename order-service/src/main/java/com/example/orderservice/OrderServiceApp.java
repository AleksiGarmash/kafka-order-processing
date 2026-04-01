package com.example.orderservice;

import ch.qos.logback.core.OutputStreamAppender;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(OutputStreamAppender.class);
    }
}
