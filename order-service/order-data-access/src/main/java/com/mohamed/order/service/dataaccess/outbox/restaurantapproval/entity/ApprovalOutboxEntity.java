package com.mohamed.order.service.dataaccess.outbox.restaurantapproval.entity;

import com.mohamed.outbox.OutboxStatus;
import com.mohamed.saga.SagaStatus;
import com.mohamed.valueobject.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "restaurant_approval_outbox")
public class ApprovalOutboxEntity {

    @Id
    private UUID id;
    private UUID safaId;
    private ZonedDateTime createdAt;
    private ZonedDateTime processedAt;
    private String type;
    private String payload;
    @Enumerated(EnumType.STRING)
    private SagaStatus sagaStatus;
    @Enumerated(EnumType.STRING)
    private OutboxStatus outboxStatus;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Version
    private int version;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ApprovalOutboxEntity that = (ApprovalOutboxEntity) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
