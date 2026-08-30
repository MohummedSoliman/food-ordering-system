package com.mohamed.customer.service.domain.ports.output.repository;

import com.mohamed.customer.service.domain.entity.Customer;

public interface CustomerRepository {

    Customer createCustomer(Customer customer);
}
