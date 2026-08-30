package com.mohamed.customer.service.domain;

import com.mohamed.customer.service.domain.create.CreateCustomerCommand;
import com.mohamed.customer.service.domain.entity.Customer;
import com.mohamed.customer.service.domain.event.CustomerCreatedEvent;
import com.mohamed.customer.service.domain.exception.CustomerDomainException;
import com.mohamed.customer.service.domain.mapper.CustomerDataMapper;
import com.mohamed.customer.service.domain.ports.output.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
@Component
public class CustomerCreateCommandHandler {

    private final CustomerRepository customerRepository;
    private final CustomerDomainService customerDomainService;
    private final CustomerDataMapper mapper;

    @Transactional
    public CustomerCreatedEvent createCustomer(CreateCustomerCommand createCustomerCommand) {
        Customer customer = mapper.createCustomerCommandToCustomer(createCustomerCommand);
        CustomerCreatedEvent customerCreatedEvent = customerDomainService.validateAndInitiateCustomer(customer);
        Customer savedCustomer = customerRepository.createCustomer(customer);
        if (savedCustomer == null) {
            log.error("Could not save customer with id: {}", createCustomerCommand.getCustomerId());
            throw new CustomerDomainException("Could not save customer with id: " +
                    createCustomerCommand.getCustomerId());
        }
        log.info("Returning CustomerCreatedEvent for customer id: {}", savedCustomer.getId());
        return customerCreatedEvent;
    }
}
