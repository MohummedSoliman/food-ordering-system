package com.mohamed.customer.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories(value = {"com.mohamed.customer.service.dataaccess", "com.mohamed.dataaccess"})
@EntityScan(value = {"com.mohamed.customer.service.dataaccess", "com.mohamed.dataaccess"})
@SpringBootApplication(scanBasePackages = "com.mohamed")
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
