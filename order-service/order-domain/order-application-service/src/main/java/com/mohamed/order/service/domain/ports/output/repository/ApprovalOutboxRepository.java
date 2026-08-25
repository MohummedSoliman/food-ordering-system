package com.mohamed.order.service.domain.ports.output.repository;

import com.mohamed.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.mohamed.outbox.OutboxStatus;
import com.mohamed.saga.SagaStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApprovalOutboxRepository {


    OrderApprovalOutboxMessage save(OrderApprovalOutboxMessage outboxMessage);

    Optional<List<OrderApprovalOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(
            String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus);

    Optional<OrderApprovalOutboxMessage> findByTypeAndSagaIdAndSageStatus(
            String type, UUID sageId, SagaStatus... sagaStatus);

    void deleteByTypeAndOutboxStatusAndSagaStatus(
            String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus);
}
