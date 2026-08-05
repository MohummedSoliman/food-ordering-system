package com.mohamed.order.service.domain.ports.output.message.publisher.payment;

import com.mohamed.event.publisher.DomainEventPublisher;
import com.mohamed.order.service.domain.event.OrderCreatedEvent;

public interface OrderCreatedPaymentRequestMessage extends DomainEventPublisher<OrderCreatedEvent> {
}
