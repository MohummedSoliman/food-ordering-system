package com.mohamed.order.service.domain;

import com.mohamed.event.EmptyEvent;
import com.mohamed.order.service.domain.dto.message.PaymentResponse;
import com.mohamed.order.service.domain.entity.Order;
import com.mohamed.order.service.domain.event.OrderPaidEvent;
import com.mohamed.order.service.domain.exception.OrderNotFoundException;
import com.mohamed.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.mohamed.order.service.domain.ports.output.repository.OrderRepository;
import com.mohamed.saga.SagaStep;
import com.mohamed.valueobject.OrderId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class OrderPaymentSage implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper helper;
    private final OrderPaidRestaurantRequestMessagePublisher requestMessagePublisher;

    @Override
    @Transactional
    public OrderPaidEvent proces(PaymentResponse paymentResponse) {
        log.info("Completing payment for order id: {}", paymentResponse.getOrderId());
        Order order = helper.findOrder(paymentResponse.getOrderId());
        OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order, requestMessagePublisher);
        helper.saveOrder(order);
        log.info("Order with id : {} is paid", order.getId().getValue());
        return orderPaidEvent;
    }

    @Override
    @Transactional
    public EmptyEvent rollback(PaymentResponse paymentResponse) {
        log.info("Cancelling order with id: {}", paymentResponse.getOrderId());
        Order order = helper.findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        helper.saveOrder(order);
        log.info("Order with id : {} is cancelled", paymentResponse.getOrderId());
        return EmptyEvent.INSTANCE;
    }
}
