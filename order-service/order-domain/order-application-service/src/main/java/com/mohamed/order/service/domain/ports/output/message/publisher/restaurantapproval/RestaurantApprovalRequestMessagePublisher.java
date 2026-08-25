package com.mohamed.order.service.domain.ports.output.message.publisher.restaurantapproval;

import com.mohamed.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.mohamed.outbox.OutboxStatus;

import java.util.function.BiConsumer;

public interface RestaurantApprovalRequestMessagePublisher {

    void publish(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                 BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback);
}
