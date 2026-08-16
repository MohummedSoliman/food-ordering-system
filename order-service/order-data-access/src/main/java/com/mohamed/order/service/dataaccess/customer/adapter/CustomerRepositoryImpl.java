package com.mohamed.order.service.dataaccess.customer.adapter;

import com.mohamed.order.service.dataaccess.customer.entity.CustomerEntity;
import com.mohamed.order.service.dataaccess.customer.mapper.CustomerDataAccessMapper;
import com.mohamed.order.service.dataaccess.customer.repository.CustomerJpaRepository;
import com.mohamed.order.service.domain.entity.Customer;
import com.mohamed.order.service.domain.ports.output.repository.CustomerRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper mapper;

    public CustomerRepositoryImpl(CustomerJpaRepository customerJpaRepository,
                                  CustomerDataAccessMapper mapper) {
        this.customerJpaRepository = customerJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Customer> findCustomerById(UUID customerId) {
        return this.customerJpaRepository.findById(customerId)
                .map(mapper::customerEntityToCustomer);
    }
}
