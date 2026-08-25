package com.mohamed.order.service.domain;

import com.mohamed.order.service.domain.dto.create.CreateOrderCommand;
import com.mohamed.order.service.domain.dto.create.CreateOrderResponse;
import com.mohamed.order.service.domain.event.OrderCreatedEvent;
import com.mohamed.order.service.domain.mapper.OrderDataMapper;
import com.mohamed.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.mohamed.outbox.OutboxStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class OrderCreateCommandHandler {

    private final OrderCreateHelper orderCreateHelper;
    private final OrderDataMapper orderDataMapper;
    private final PaymentOutboxHelper helper;
    private final OrderSagaHelper sagaHelper;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);// Save the order to the repository
        log.info("Order with id {} created successfully", orderCreatedEvent.getOrder().getId().getValue());
        CreateOrderResponse createOrderResponse =
                orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder(), "Order Created Successfully");// Return a response after creating the order

        helper.savePaymentOutboxMessage(
                orderDataMapper.orderCreateEventToOrderPaymentEventPayload(orderCreatedEvent),
                orderCreatedEvent.getOrder().getOrderStatus(),
                sagaHelper.orderStatusToSageStatus(orderCreatedEvent.getOrder().getOrderStatus()),
                OutboxStatus.STARTED,
                UUID.randomUUID()
        );

        log.info("Retuning CreateOrderResponse with order id: {}",
                orderCreatedEvent.getOrder().getId().getValue().toString());

        return createOrderResponse;
    }
}
