package com.mohamed.order.service.domain.outbox.scheduler.payment;

import com.mohamed.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.mohamed.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.mohamed.outbox.OutboxScheduler;
import com.mohamed.outbox.OutboxStatus;
import com.mohamed.saga.SagaStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Component
public class PaymentOutboxScheduler implements OutboxScheduler {

    private final PaymentOutboxHelper helper;
    private final PaymentRequestMessagePublisher paymentRequestMessagePublisher;

    @Override
    @Transactional
    @Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {
        Optional<List<OrderPaymentOutboxMessage>> outboxMessageResponse =
                helper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
                        OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATING
                );
        if (outboxMessageResponse.isPresent() && !outboxMessageResponse.get().isEmpty()) {
            List<OrderPaymentOutboxMessage> outboxMessages = outboxMessageResponse.get();
            log.info("Received {} OrderPaymentOutboxMessage with ids: {} sending to message bus",
                    outboxMessages.size(),
                    outboxMessages.stream().map(
                                    message -> message.getId().toString())
                            .collect(Collectors.joining(","))
            );

            outboxMessages.forEach(message ->
                    paymentRequestMessagePublisher.publish(message, this::updateOutboxStatus)
            );
            log.info("{} OrderPaymentOutboxMessage sent to message bus", outboxMessages.size());
        }
    }

    private void updateOutboxStatus(OrderPaymentOutboxMessage orderPaymentOutboxMessage, OutboxStatus outboxStatus) {
        orderPaymentOutboxMessage.setOutboxStatus(outboxStatus);
        helper.save(orderPaymentOutboxMessage);
        log.info("OrderPaymentOutboxMessage is updated with outbox status: {}", outboxStatus.name());
    }
}
