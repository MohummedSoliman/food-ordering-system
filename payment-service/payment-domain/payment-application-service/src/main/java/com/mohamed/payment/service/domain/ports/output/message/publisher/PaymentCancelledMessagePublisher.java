package com.mohamed.payment.service.domain.ports.output.message.publisher;

import com.mohamed.event.publisher.DomainEventPublisher;
import com.mohamed.payment.service.domain.event.PaymentCancelledEvent;

public interface PaymentCancelledMessagePublisher extends DomainEventPublisher<PaymentCancelledEvent> {
}
