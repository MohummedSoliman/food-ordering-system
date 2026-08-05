package com.mohamed.order.service.domain;

import com.mohamed.order.service.domain.event.OrderCreatedEvent;
import com.mohamed.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@AllArgsConstructor
@Component
public class OrderCreatedEventApplicationListener {

    private final OrderCreatedPaymentRequestMessage orderCreatedPaymentRequestMessagePublisher;

    @TransactionalEventListener
    void process(OrderCreatedEvent orderCreatedEvent) {
        orderCreatedPaymentRequestMessagePublisher.publish(orderCreatedEvent);
        log.info("OrderCreatedEventApplicationListener processed for order id: {}", orderCreatedEvent.getOrder().getId().getValue());
    }
}
