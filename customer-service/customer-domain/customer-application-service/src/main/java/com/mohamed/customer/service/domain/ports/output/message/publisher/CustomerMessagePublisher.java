package com.mohamed.customer.service.domain.ports.output.message.publisher;

import com.mohamed.customer.service.domain.event.CustomerCreatedEvent;

public interface CustomerMessagePublisher {

    void publish(CustomerCreatedEvent customerCreatedEvent);
}
