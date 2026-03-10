package org.apache.dubbo.demo.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ProviderApplication {
    public static void main(String[] args) {
        System.out.println("Starting Dubbo provider...");
        SpringApplication.run(ProviderApplication.class, args);
        System.out.println("Dubbo provider started successfully!");
        System.out.println("Service: org.apache.dubbo.demo.api.DemoService");
        System.out.println("Protocol: tri on port 50051");
    }
}