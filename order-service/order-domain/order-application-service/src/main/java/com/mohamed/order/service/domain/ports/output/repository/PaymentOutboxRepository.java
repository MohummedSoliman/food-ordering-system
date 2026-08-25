package com.mohamed.order.service.domain.ports.output.repository;

import com.mohamed.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.mohamed.outbox.OutboxStatus;
import com.mohamed.saga.SagaStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentOutboxRepository {

    OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage outboxMessage);

    Optional<List<OrderPaymentOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(
            String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus);

    Optional<OrderPaymentOutboxMessage> findByTypeAndSagaIdAndSageStatus(
            String type, UUID sageId, SagaStatus... sagaStatus);

    void deleteByTypeAndOutboxStatusAndSagaStatus(
            String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus);

}
