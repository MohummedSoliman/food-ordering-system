package com.mohamed.order.service.domain.outbox.scheduler.approval;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamed.order.service.domain.exception.OrderDomainException;
import com.mohamed.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.mohamed.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.mohamed.order.service.domain.ports.output.repository.ApprovalOutboxRepository;
import com.mohamed.outbox.OutboxStatus;
import com.mohamed.saga.SagaStatus;
import com.mohamed.valueobject.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mohamed.saga.order.SagaConstants.ORDER_SAGA_NAME;

@Slf4j
@Component
@AllArgsConstructor
public class ApprovalOutboxHelper {

    private final ApprovalOutboxRepository approvalOutboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Optional<List<OrderApprovalOutboxMessage>> getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
            OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        return approvalOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(
                ORDER_SAGA_NAME,
                outboxStatus,
                sagaStatus);

    }

    @Transactional(readOnly = true)
    public Optional<OrderApprovalOutboxMessage> getApprovalOutboxBySagaIdAndSagaStatus(
            UUID sagaId, SagaStatus... sagaStatus) {
        return approvalOutboxRepository.findByTypeAndSagaIdAndSageStatus(
                ORDER_SAGA_NAME,
                sagaId,
                sagaStatus);
    }

    @Transactional
    public void save(OrderApprovalOutboxMessage orderApprovalOutboxMessage) {
        OrderApprovalOutboxMessage outboxMessage = this.approvalOutboxRepository.save(orderApprovalOutboxMessage);
        if (outboxMessage == null) {
            log.error("Could not save OrderApprovalOutboxMessage with outbox id: {}", orderApprovalOutboxMessage.getId());
            throw new OrderDomainException("Could not save OrderApprovalOutboxMessage with outbox id:" + orderApprovalOutboxMessage.getId());
        }
        log.info("OrderApprovalOutboxMessage saved with outbox id: {}", orderApprovalOutboxMessage.getId());
    }

    @Transactional
    public void deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
            OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        this.approvalOutboxRepository.deleteByTypeAndOutboxStatusAndSagaStatus(
                ORDER_SAGA_NAME,
                outboxStatus,
                sagaStatus
        );
    }

    @Transactional
    public void saveApprovalOutboxMessage(OrderApprovalEventPayload orderApprovalEventPayload,
                                          OrderStatus orderStatus,
                                          SagaStatus sagaStatus,
                                          OutboxStatus outboxStatus,
                                          UUID sageId) {
        save(OrderApprovalOutboxMessage.builder()
                .id(UUID.randomUUID())
                .sageId(sageId)
                .orderStatus(orderStatus)
                .outboxStatus(outboxStatus)
                .sagaStatus(sagaStatus)
                .createdAt(orderApprovalEventPayload.getCreatedAt())
                .type(ORDER_SAGA_NAME)
                .payload(createPayload(orderApprovalEventPayload))
                .build());
    }

    private String createPayload(OrderApprovalEventPayload orderApprovalEventPayload) {
        try {
            return objectMapper.writeValueAsString(orderApprovalEventPayload);
        } catch (JsonProcessingException e) {
            log.error("Could not create OrderApprovalEventPayload for order id: {}",
                    orderApprovalEventPayload.getOrderId());
            throw new OrderDomainException("Could not create OrderApprovalEventPayload for order id: " +
                    orderApprovalEventPayload.getOrderId());
        }
    }
}
