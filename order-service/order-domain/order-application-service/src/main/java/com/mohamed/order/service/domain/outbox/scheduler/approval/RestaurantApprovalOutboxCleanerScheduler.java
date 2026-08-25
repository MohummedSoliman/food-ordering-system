package com.mohamed.order.service.domain.outbox.scheduler.approval;

import com.mohamed.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
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
public class RestaurantApprovalOutboxCleanerScheduler implements OutboxScheduler {

    private final ApprovalOutboxHelper helper;

    @Override
    @Scheduled(cron = "@midnight")
    public void processOutboxMessage() {
        Optional<List<OrderApprovalOutboxMessage>> outboxMessages = helper.getApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                OutboxStatus.COMPLETED,
                SagaStatus.COMPENSATED,
                SagaStatus.SUCCEEDED,
                SagaStatus.FAILED
        );

        if (outboxMessages.isPresent() && !outboxMessages.get().isEmpty()) {
            List<OrderApprovalOutboxMessage> messages = outboxMessages.get();
            log.info("Received {} OrderApprovalOutboxMessage for clean-up.", messages.size());
            helper.deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
                    OutboxStatus.COMPLETED,
                    SagaStatus.COMPENSATED,
                    SagaStatus.SUCCEEDED,
                    SagaStatus.FAILED
            );
            log.info("{} OrderApprovalOutboxMessage deleted", messages.size());
        }
    }
}
