package com.mohamed.customer.service.domain.ports.input.service;

import com.mohamed.customer.service.domain.create.CreateCustomerCommand;
import com.mohamed.customer.service.domain.create.CreateCustomerResponse;
import jakarta.validation.Valid;

public interface CustomerApplicationService {

    CreateCustomerResponse createCustomer(@Valid CreateCustomerCommand customerCommand);
}
