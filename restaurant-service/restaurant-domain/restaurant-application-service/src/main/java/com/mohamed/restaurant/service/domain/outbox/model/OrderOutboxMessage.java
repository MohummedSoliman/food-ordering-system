package com.mohamed.restaurant.service.domain.outbox.model;

import com.mohamed.outbox.OutboxStatus;
import com.mohamed.valueobject.OrderApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class OrderOutboxMessage {

    private UUID id;
    private UUID sagaId;
    private ZonedDateTime createdAt;
    private ZonedDateTime processedAt;
    private String type;
    private String payload;
    private OrderApprovalStatus approvalStatus;
    @Setter
    private OutboxStatus outboxStatus;
    private int version;

}
