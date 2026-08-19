package com.mohamed.order.service.domain;

import com.mohamed.event.publisher.DomainEventPublisher;
import com.mohamed.order.service.domain.entity.Order;
import com.mohamed.order.service.domain.entity.Restaurant;
import com.mohamed.order.service.domain.event.OrderCancelledEvent;
import com.mohamed.order.service.domain.event.OrderCreatedEvent;
import com.mohamed.order.service.domain.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {

    OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant, DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher);

    OrderPaidEvent payOrder(Order order, DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages, DomainEventPublisher<OrderCancelledEvent> orderCancelledEventDomainEventPublisher);

    void cancelOrder(Order order, List<String> failureMessages);

    void approveOrder(Order order);
}
