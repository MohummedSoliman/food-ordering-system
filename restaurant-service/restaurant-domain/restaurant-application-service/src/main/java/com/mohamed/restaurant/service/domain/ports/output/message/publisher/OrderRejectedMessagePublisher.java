package com.mohamed.restaurant.service.domain.ports.output.message.publisher;

import com.mohamed.event.publisher.DomainEventPublisher;
import com.mohamed.restaurant.service.domain.event.OrderRejectedEvent;

public interface OrderRejectedMessagePublisher extends DomainEventPublisher<OrderRejectedEvent> {
}
