package com.mohamed.order.service.dataaccess.outbox.payment.adapter;

import com.mohamed.order.service.dataaccess.outbox.payment.entity.PaymentOutboxEntity;
import com.mohamed.order.service.dataaccess.outbox.payment.exception.PaymentOutboxNotFoundException;
import com.mohamed.order.service.dataaccess.outbox.payment.mapper.PaymentOutboxDataAccessMapper;
import com.mohamed.order.service.dataaccess.outbox.payment.reposiotry.PaymentOutboxJpaRepository;
import com.mohamed.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.mohamed.order.service.domain.ports.output.repository.PaymentOutboxRepository;
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
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository {

    private final PaymentOutboxJpaRepository paymentOutboxJpaRepository;
    private final PaymentOutboxDataAccessMapper outboxDataAccessMapper;

    @Override
    public OrderPaymentOutboxMessage save(OrderPaymentOutboxMessage outboxMessage) {

        PaymentOutboxEntity paymentOutboxEntity = outboxDataAccessMapper.orderPaymentOutboxMessageToPaymentOutboxEntity(outboxMessage);
        return outboxDataAccessMapper.paymentOutboxEntityToOrderPaymentOutboxMessage(
                paymentOutboxJpaRepository.save(paymentOutboxEntity)
        );

    }

    @Override
    public Optional<List<OrderPaymentOutboxMessage>> findByTypeAndOutboxStatusAndSagaStatus(String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        return Optional.of(paymentOutboxJpaRepository.findByTypeAndOutboxStatusAndSagaStatusIn(
                        type,
                        outboxStatus,
                        Arrays.asList(sagaStatus)
                )
                .orElseThrow(() ->
                        new PaymentOutboxNotFoundException("Payment outbox message could not be found"))
                .stream()
                .map(outboxDataAccessMapper::paymentOutboxEntityToOrderPaymentOutboxMessage)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<OrderPaymentOutboxMessage> findByTypeAndSagaIdAndSageStatus(String type, UUID sageId, SagaStatus... sagaStatus) {
        return paymentOutboxJpaRepository
                .findByTypeAndSagaIdAndSagaStatusIn(type, sageId, Arrays.asList(sagaStatus))
                .map(outboxDataAccessMapper::paymentOutboxEntityToOrderPaymentOutboxMessage);
    }

    @Override
    public void deleteByTypeAndOutboxStatusAndSagaStatus(String type, OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
        paymentOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(type, outboxStatus,
                Arrays.asList(sagaStatus));
    }
}
