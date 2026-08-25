package com.mohamed.order.service.dataaccess.outbox.restaurantapproval.adapter;

import com.mohamed.order.service.dataaccess.outbox.restaurantapproval.exception.ApprovalOutboxNotFoundException;
import com.mohamed.order.service.dataaccess.outbox.restaurantapproval.mapper.ApprovalOutboxDataAccessMapper;
import com.mohamed.order.service.dataaccess.outbox.restaurantapproval.repotiory.ApprovalOutboxJpaRepository;
import com.mohamed.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.mohamed.order.service.domain.ports.output.repository.ApprovalOutboxRepository;
import com.mohamed.outbox.OutboxStatus;
import com.mohamed.saga.SagaStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class ApprovalOutboxRepositoryImpl implements ApprovalOutboxRepository {

    private final ApprovalOutboxJpaRepository outboxJpaRepository;
    private final ApprovalOutboxDataAccessMapper mapper;

    @Override
    public OrderApprovalOutboxMessage save(OrderApprovalOutboxMessage outboxMessage) {
        return mapper
                .approvalOutboxEntityToOrderApprovalOutboxMessage(outboxJpaRepository
                        .save(mapper
                                .orderCreatedOutboxMessageToOutboxEntity(outboxMessage)));
    }

    @Override
    public Optional<List<OrderApprovalOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        return Optional.of(outboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus,
                        Arrays.asList(sagaStatus))
                .orElseThrow(() -> new ApprovalOutboxNotFoundException("Approval outbox object " +
                        "could be found for saga type " + type))
                .stream()
                .map(mapper::approvalOutboxEntityToOrderApprovalOutboxMessage)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<OrderApprovalOutboxMessage> findByTypeAndSagaIdAndSageStatus(String type, UUID sageId, SagaStatus... sagaStatus) {
        return outboxJpaRepository
                .findByTypeAndSagaIdAndSagaStatusIn(type, sageId,
                        Arrays.asList(sagaStatus))
                .map(mapper::approvalOutboxEntityToOrderApprovalOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatusAndSagaStatus(String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        outboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus,
                Arrays.asList(sagaStatus));
    }
}
