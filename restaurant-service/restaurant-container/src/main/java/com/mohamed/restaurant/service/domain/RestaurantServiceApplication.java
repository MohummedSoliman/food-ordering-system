package com.mohamed.restaurant.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"com.mohamed.restaurant.service.dataaccess", "com.mohamed.dataaccess"})
@EntityScan(basePackages = {"com.mohamed.restaurant.service.dataacess", "com.mohamed.dataaccess"})
@SpringBootApplication(scanBasePackages = "com.mohamed")
public class RestaurantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }
}
