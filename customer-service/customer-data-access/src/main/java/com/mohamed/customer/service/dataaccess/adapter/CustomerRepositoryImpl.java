package com.mohamed.customer.service.dataaccess.adapter;

import com.mohamed.customer.service.dataaccess.entity.CustomerEntity;
import com.mohamed.customer.service.dataaccess.mapper.CustomerDataAccessMapper;
import com.mohamed.customer.service.dataaccess.repository.CustomerJpaRepository;
import com.mohamed.customer.service.domain.entity.Customer;
import com.mohamed.customer.service.domain.ports.output.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerDataAccessMapper mapper;
    private final CustomerJpaRepository repository;

    @Override
    public Customer createCustomer(Customer customer) {
        CustomerEntity customerEntity = mapper.customerToCustomerEntity(customer);
        return mapper.customerEntityToCustomer(
                repository.save(customerEntity)
        );
    }
}
