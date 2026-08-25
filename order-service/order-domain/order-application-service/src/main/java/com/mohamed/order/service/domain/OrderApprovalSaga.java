package com.mohamed.order.service.domain;

import com.mohamed.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.mohamed.order.service.domain.entity.Order;
import com.mohamed.order.service.domain.event.OrderCancelledEvent;
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
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper helper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    @Override
    @Transactional
    public void proces(RestaurantApprovalResponse restaurantApprovalResponse) {

        Optional<OrderApprovalOutboxMessage> approvalOutboxResponse =
                approvalOutboxHelper.getApprovalOutboxBySagaIdAndSagaStatus(
                        UUID.fromString(restaurantApprovalResponse.getSagaId()),
                        SagaStatus.PROCESSING
                );

        if (approvalOutboxResponse.isEmpty()) {
            log.info("An outbox message with saga id {} is already processed",
                    restaurantApprovalResponse.getSagaId());
            return;
        }

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = approvalOutboxResponse.get();
        Order order = approveOrder(restaurantApprovalResponse);
        SagaStatus sagaStatus = helper.orderStatusToSageStatus(order.getOrderStatus());
        approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(
                orderApprovalOutboxMessage,
                order.getOrderStatus(),
                sagaStatus
        ));

        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(
                restaurantApprovalResponse.getSagaId(),
                order.getOrderStatus(),
                sagaStatus
        ));
        log.info("Order with id: {} is approved", restaurantApprovalResponse.getOrderId());
    }

    @Override
    @Transactional
    public void rollback(RestaurantApprovalResponse restaurantApprovalResponse) {

        Optional<OrderApprovalOutboxMessage> approvalOutboxResponse =
                approvalOutboxHelper.getApprovalOutboxBySagaIdAndSagaStatus(
                        UUID.fromString(restaurantApprovalResponse.getSagaId()),
                        SagaStatus.PROCESSING
                );

        if (approvalOutboxResponse.isEmpty()) {
            log.info("An outbox message with saga id {} is already roll backed",
                    restaurantApprovalResponse.getSagaId());
            return;
        }

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = approvalOutboxResponse.get();
        OrderCancelledEvent domainEvent = rollbackOrder(restaurantApprovalResponse);
        SagaStatus sagaStatus = helper.orderStatusToSageStatus(domainEvent.getOrder().getOrderStatus());
        approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(
                orderApprovalOutboxMessage,
                domainEvent.getOrder().getOrderStatus(),
                sagaStatus
        ));

        paymentOutboxHelper.savePaymentOutboxMessage(
                orderDataMapper.orderCancelledEventToorderPaymentEventPayload(domainEvent),
                domainEvent.getOrder().getOrderStatus(),
                sagaStatus,
                OutboxStatus.STARTED,
                UUID.fromString(restaurantApprovalResponse.getSagaId())
        );

        log.info("Order with id : {} is cancelled", domainEvent.getOrder().getId().getValue());
    }

    private Order approveOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Approving Order with id: {}", restaurantApprovalResponse.getOrderId());
        Order order = helper.findOrder(restaurantApprovalResponse.getOrderId());
        orderDomainService.approveOrder(order);
        helper.saveOrder(order);
        return order;
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(
            OrderApprovalOutboxMessage orderApprovalOutboxMessage,
            OrderStatus orderStatus, SagaStatus sagaStatus) {
        orderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        orderApprovalOutboxMessage.setOrderStatus(orderStatus);
        orderApprovalOutboxMessage.setSagaStatus(sagaStatus);
        return orderApprovalOutboxMessage;
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(
            String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
        Optional<OrderPaymentOutboxMessage> paymentOutboxMessageResponse =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(sagaId), SagaStatus.PROCESSING);
        if (paymentOutboxMessageResponse.isEmpty()) {
            throw new OrderDomainException("Payment outbox message can't be found");
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = paymentOutboxMessageResponse.get();
        orderPaymentOutboxMessage.setSagaStatus(sagaStatus);
        orderPaymentOutboxMessage.setOrderStatus(orderStatus);
        orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTC)));
        return orderPaymentOutboxMessage;
    }


    private OrderCancelledEvent rollbackOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Cancelling order with id: {}", restaurantApprovalResponse.getOrderId());
        Order order = helper.findOrder(restaurantApprovalResponse.getOrderId());
        OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(
                order,
                restaurantApprovalResponse.getFailureMessages()
        );
        helper.saveOrder(order);
        return orderCancelledEvent;
    }
}
