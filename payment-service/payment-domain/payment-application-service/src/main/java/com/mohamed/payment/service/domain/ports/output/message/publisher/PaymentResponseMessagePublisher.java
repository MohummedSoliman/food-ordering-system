package com.mohamed.payment.service.domain.ports.output.message.publisher;

import com.mohamed.outbox.OutboxStatus;
import com.mohamed.payment.service.domain.outbox.model.OrderOutboxMessage;

import java.util.function.BiConsumer;

public interface PaymentResponseMessagePublisher {

    void publish(OrderOutboxMessage orderOutboxMessage,
                 BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback);
}
