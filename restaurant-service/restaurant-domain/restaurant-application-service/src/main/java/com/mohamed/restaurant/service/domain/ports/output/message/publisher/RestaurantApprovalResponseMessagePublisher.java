package com.mohamed.restaurant.service.domain.ports.output.message.publisher;

import com.mohamed.outbox.OutboxStatus;
import com.mohamed.restaurant.service.domain.outbox.model.OrderOutboxMessage;

import java.util.function.BiConsumer;

public interface RestaurantApprovalResponseMessagePublisher {

    void publish(OrderOutboxMessage outboxMessage,
                 BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback);
}
