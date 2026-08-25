package com.mohamed.order.service.domain.outbox.scheduler.payment;

import com.mohamed.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.mohamed.outbox.OutboxScheduler;
import com.mohamed.outbox.OutboxStatus;
import com.mohamed.saga.SagaStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class PaymentOutboxCleanerScheduler implements OutboxScheduler {

    private final PaymentOutboxHelper helper;

    @Override
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {

        Optional<List<OrderPaymentOutboxMessage>> outboxMessageResponse =
                helper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
                        OutboxStatus.COMPLETED,
                        SagaStatus.SUCCEEDED,
                        SagaStatus.FAILED,
                        SagaStatus.COMPENSATED
                );

        if (outboxMessageResponse.isPresent()) {
            List<OrderPaymentOutboxMessage> outboxMessages = outboxMessageResponse.get();
            log.info("Received {} OrderPaymentOutboxMessage for clean-up.", outboxMessages.size());
            helper.deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(
                    OutboxStatus.COMPLETED,
                    SagaStatus.SUCCEEDED,
                    SagaStatus.FAILED,
                    SagaStatus.COMPENSATED
            );
            log.info("{} OrderPaymentOutboxMessage deleted.", outboxMessages.size());
        }
    }
}
