package com.mohamed.payment.service.domain.outbox.scheduler;

import com.mohamed.outbox.OutboxScheduler;
import com.mohamed.outbox.OutboxStatus;
import com.mohamed.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.mohamed.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class OrderOutboxScheduler implements OutboxScheduler {

    private final OrderOutboxHelper helper;
    private final PaymentResponseMessagePublisher paymentMessagePublisher;

    @Override
    @Transactional
    @Scheduled(fixedRateString = "${payment-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${payment-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessage() {

        Optional<List<OrderOutboxMessage>> outboxMessageResponse =
                helper.getOrderOutboxMessageByOutboxStatus(OutboxStatus.STARTED);

        if (outboxMessageResponse.isPresent() && !outboxMessageResponse.get().isEmpty()) {
            List<OrderOutboxMessage> outboxMessages = outboxMessageResponse.get();
            log.info("Received {} OrderOutboxMessages sending to kafka", outboxMessages.size());
            outboxMessages.forEach(outboxMessage ->
                    paymentMessagePublisher.publish(
                            outboxMessage,
                            helper::updateOutboxMessage
                    )
            );
            log.info("{} OrderOutboxMessage sent to message bus", outboxMessages.size());
        }
    }
}
