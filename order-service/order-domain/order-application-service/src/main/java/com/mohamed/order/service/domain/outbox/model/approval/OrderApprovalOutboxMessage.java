package com.mohamed.order.service.domain.outbox.model.approval;

import com.mohamed.outbox.OutboxStatus;
import com.mohamed.saga.SagaStatus;
import com.mohamed.valueobject.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderApprovalOutboxMessage {

    private UUID id;
    private UUID sageId;
    private ZonedDateTime createdAt;
    @Setter
    private ZonedDateTime processedAt;
    private String type;
    private String payload;
    @Setter
    private SagaStatus sagaStatus;
    @Setter
    private OrderStatus orderStatus;
    @Setter
    private OutboxStatus outboxStatus;
    private int version;
}
