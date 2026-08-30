package com.mohamed.customer.service.dataaccess.mapper;

import com.mohamed.customer.service.dataaccess.entity.CustomerEntity;
import com.mohamed.customer.service.domain.entity.Customer;
import com.mohamed.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {

    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return new Customer(
                new CustomerId(customerEntity.getId())
                , customerEntity.getUsername()
                , customerEntity.getFirstName()
                , customerEntity.getLastName());
    }

    public CustomerEntity customerToCustomerEntity(Customer customer) {
        return CustomerEntity.builder()
                .id(customer.getId().getValue())
                .username(customer.getUsername())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .build();
    }
}
