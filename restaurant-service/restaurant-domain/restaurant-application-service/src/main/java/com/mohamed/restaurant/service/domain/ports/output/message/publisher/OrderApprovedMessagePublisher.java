package com.mohamed.restaurant.service.domain.ports.output.message.publisher;

import com.mohamed.event.publisher.DomainEventPublisher;
import com.mohamed.restaurant.service.domain.event.OrderApprovedEvent;

public interface OrderApprovedMessagePublisher extends DomainEventPublisher<OrderApprovedEvent> {

}
