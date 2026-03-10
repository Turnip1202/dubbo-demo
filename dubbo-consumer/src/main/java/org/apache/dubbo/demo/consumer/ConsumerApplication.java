package org.apache.dubbo.demo.consumer;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EnableDubbo
public class ConsumerApplication {
    public static void main(String[] args) {
        System.out.println("Starting Dubbo consumer...");
        SpringApplication.run(ConsumerApplication.class, args);
        System.out.println("Dubbo consumer started successfully!");
        System.out.println("REST API available at: http://localhost:8080");
        System.out.println("Endpoints:");
        System.out.println("  GET /hello/{name}    - Call sayHello service");
        System.out.println("  GET /goodbye/{name}  - Call sayGoodbye service");
        System.out.println("  GET /status          - Check consumer status");
    }
}