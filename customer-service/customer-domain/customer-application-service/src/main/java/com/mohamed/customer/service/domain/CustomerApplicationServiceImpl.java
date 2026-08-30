package com.mohamed.customer.service.domain;

import com.mohamed.customer.service.domain.create.CreateCustomerCommand;
import com.mohamed.customer.service.domain.create.CreateCustomerResponse;
import com.mohamed.customer.service.domain.event.CustomerCreatedEvent;
import com.mohamed.customer.service.domain.mapper.CustomerDataMapper;
import com.mohamed.customer.service.domain.ports.input.service.CustomerApplicationService;
import com.mohamed.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@AllArgsConstructor
@Service
public class CustomerApplicationServiceImpl implements CustomerApplicationService {

    private final CustomerDataMapper mapper;
    private final CustomerMessagePublisher publisher;
    private final CustomerCreateCommandHandler handler;

    @Override
    public CreateCustomerResponse createCustomer(CreateCustomerCommand customerCommand) {
        CustomerCreatedEvent customerCreatedEvent = handler.createCustomer(customerCommand);
        publisher.publish(customerCreatedEvent);
        return mapper.customerToCreateCustomerResponse(
                customerCreatedEvent.getCustomer(),
                "Customer Saved Successfully");
    }
}
