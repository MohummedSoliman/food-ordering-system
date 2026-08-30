package com.mohamed.customer.service.domain.mapper;

import com.mohamed.customer.service.domain.create.CreateCustomerCommand;
import com.mohamed.customer.service.domain.create.CreateCustomerResponse;
import com.mohamed.customer.service.domain.entity.Customer;
import com.mohamed.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataMapper {

    public Customer createCustomerCommandToCustomer(CreateCustomerCommand customerCommand) {
        return new Customer(
                new CustomerId(customerCommand.getCustomerId()),
                customerCommand.getUsername(),
                customerCommand.getFirstName(),
                customerCommand.getLastName()
        );
    }

    public CreateCustomerResponse customerToCreateCustomerResponse(Customer customer, String message) {
        return new CreateCustomerResponse(customer.getId().getValue(), message);
    }
}
