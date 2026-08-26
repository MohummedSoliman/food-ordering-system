package com.mohamed.payment.service.dataaccess.outbox.adapter;

import com.mohamed.outbox.OutboxStatus;
import com.mohamed.payment.service.dataaccess.outbox.entity.OrderOutboxEntity;
import com.mohamed.payment.service.dataaccess.outbox.exception.OrderOutboxNotFoundException;
import com.mohamed.payment.service.dataaccess.outbox.mapper.OrderOutboxDataAccessMapper;
import com.mohamed.payment.service.dataaccess.outbox.repository.OrderOutboxJpaRepository;
import com.mohamed.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.mohamed.payment.service.domain.ports.output.repository.OrderOutboxRepository;
import com.mohamed.valueobject.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

    private final OrderOutboxJpaRepository orderOutboxJpaRepository;
    private final OrderOutboxDataAccessMapper mapper;

    @Override
    public OrderOutboxMessage save(OrderOutboxMessage outboxMessage) {
        OrderOutboxEntity orderOutboxEntity = mapper.orderOutboxMessageToOutboxEntity(outboxMessage);
        return mapper.orderOutboxEntityToOrderOutboxMessage(
                orderOutboxJpaRepository.save(orderOutboxEntity)
        );
    }

    @Override
    public Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {

        return Optional.of(orderOutboxJpaRepository.findByTypeAndOutboxStatus(type, outboxStatus)
                .orElseThrow(() ->
                        new OrderOutboxNotFoundException("Approval Outbox object can't be found"))
                .stream()
                .map(mapper::orderOutboxEntityToOrderOutboxMessage)
                .toList());
    }

    @Override
    public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
            String type, UUID sageId, PaymentStatus paymentStatus, OutboxStatus outboxStatus) {
        return orderOutboxJpaRepository.findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(
                        type, sageId, paymentStatus, outboxStatus)
                .map(mapper::orderOutboxEntityToOrderOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
        orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(type, outboxStatus);
    }
}
