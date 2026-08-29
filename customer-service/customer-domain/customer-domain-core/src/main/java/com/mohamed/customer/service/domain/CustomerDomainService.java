package com.mohamed.customer.service.domain;

import com.mohamed.customer.service.domain.entity.Customer;
import com.mohamed.customer.service.domain.event.CustomerCreatedEvent;

public interface CustomerDomainService {

    CustomerCreatedEvent validateAndInitiateCustomer(Customer customer);
}
