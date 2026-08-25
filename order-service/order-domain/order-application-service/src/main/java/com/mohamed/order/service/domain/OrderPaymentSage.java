package com.mohamed.order.service.domain;

import com.mohamed.order.service.domain.dto.message.PaymentResponse;
import com.mohamed.order.service.domain.entity.Order;
import com.mohamed.order.service.domain.event.OrderPaidEvent;
import com.mohamed.order.service.domain.exception.OrderDomainException;
import com.mohamed.order.service.domain.mapper.OrderDataMapper;
import com.mohamed.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.mohamed.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.mohamed.order.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.mohamed.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.mohamed.outbox.OutboxStatus;
import com.mohamed.saga.SagaStatus;
import com.mohamed.saga.SagaStep;
import com.mohamed.valueobject.OrderStatus;
import com.mohamed.valueobject.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.mohamed.DomainConstants.UTC;

@Slf4j
@AllArgsConstructor
@Component
public class OrderPaymentSage implements SagaStep<PaymentResponse> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper helper;
    private final PaymentOutboxHelper outboxHelper;
    private final OrderSagaHelper orderSagaHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    @Override
    @Transactional
    public void proces(PaymentResponse paymentResponse) {
        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
                outboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(paymentResponse.getSagaId()),
                        SagaStatus.STARTED);

        if (orderPaymentOutboxMessageResponse.isEmpty()) {
            log.info("An Outbox message with saga id: {} is already processed",
                    paymentResponse.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

        OrderPaidEvent orderPaidEvent = completePaymentForOrder(paymentResponse);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSageStatus(
                orderPaidEvent.getOrder().getOrderStatus());
        outboxHelper.save(getUpdatedPaymentOutboxMessage(
                orderPaymentOutboxMessage, orderPaidEvent.getOrder().getOrderStatus(), sagaStatus
        ));

        approvalOutboxHelper.saveApprovalOutboxMessage(
                orderDataMapper.orderPaidEventToorderApprovalEventPayload(orderPaidEvent),
                orderPaidEvent.getOrder().getOrderStatus(),
                sagaStatus,
                OutboxStatus.STARTED,
                UUID.fromString(paymentResponse.getSagaId())
        );

        log.info("Order with id : {} is paid", orderPaidEvent.getOrder().getId().getValue());
    }

    @Override
    @Transactional
    public void rollback(PaymentResponse paymentResponse) {

        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
                outboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(paymentResponse.getSagaId()),
                        getCurrentSagaStatus(paymentResponse.getPaymentStatus())
                );

        if (orderPaymentOutboxMessageResponse.isEmpty()) {
            log.info("An outbox message with sage id : {} is already roll backed",
                    paymentResponse.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();

        Order order = rollbackPaymentForOrder(paymentResponse);
        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSageStatus(order.getOrderStatus());
        outboxHelper.save(getUpdatedPaymentOutboxMessage(
                orderPaymentOutboxMessage,
                order.getOrderStatus(),
                sagaStatus
        ));

        if (paymentResponse.getPaymentStatus() == PaymentStatus.CANCELLED) {
            approvalOutboxHelper.save(
                    getUpdatedApprovalOutboxMessage(
                            paymentResponse.getSagaId(), order.getOrderStatus(), sagaStatus)
            );
        }

        log.info("Order with id : {} is cancelled", paymentResponse.getOrderId());
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(
            String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
        Optional<OrderApprovalOutboxMessage> approvalOutboxMessage =
                approvalOutboxHelper.getApprovalOutboxBySagaIdAndSagaStatus(
                        UUID.fromString(sagaId), SagaStatus.COMPENSATING
                );

        if (approvalOutboxMessage.isEmpty()) {
            throw new OrderDomainException("Approval outbox message could not be found");
        }

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = approvalOutboxMessage.get();
        orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        orderApprovalOutboxMessage.setOrderStatus(orderStatus);
        orderApprovalOutboxMessage.setSagaStatus(sagaStatus);

        return orderApprovalOutboxMessage;
    }

    private Order rollbackPaymentForOrder(PaymentResponse paymentResponse) {
        log.info("Cancelling order with id: {}", paymentResponse.getOrderId());
        Order order = helper.findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        helper.saveOrder(order);
        return order;
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(
            OrderPaymentOutboxMessage orderPaymentOutboxMessage,
            OrderStatus orderStatus,
            SagaStatus sagaStatus) {
        orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        orderPaymentOutboxMessage.setOrderStatus(orderStatus);
        orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
        return orderPaymentOutboxMessage;
    }

    private OrderPaidEvent completePaymentForOrder(PaymentResponse paymentResponse) {
        log.info("Completing payment for order id: {}", paymentResponse.getOrderId());
        Order order = helper.findOrder(paymentResponse.getOrderId());
        OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order);
        helper.saveOrder(order);
        return orderPaidEvent;
    }

    private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case COMPLETED -> new SagaStatus[]{SagaStatus.STARTED};
            case CANCELLED -> new SagaStatus[]{SagaStatus.PROCESSING};
            case FAILED -> new SagaStatus[]{SagaStatus.STARTED, SagaStatus.PROCESSING};
        };
    }
}
