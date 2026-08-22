package com.mohamed.order.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories(basePackages = {"com.mohamed.order.service.dataaccess", "com.mohamed.dataaccess"})
@EntityScan(basePackages = {"com.mohamed.order.service.dataaccess", "com.mohamed.dataaccess"})
@SpringBootApplication(scanBasePackages = "com.mohamed")
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
