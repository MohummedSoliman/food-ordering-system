package com.mohamed.order.service.domain;

import com.mohamed.order.service.domain.dto.create.CreateOrderCommand;
import com.mohamed.order.service.domain.dto.create.CreateOrderResponse;
import com.mohamed.order.service.domain.entity.Customer;
import com.mohamed.order.service.domain.entity.Order;
import com.mohamed.order.service.domain.entity.Restaurant;
import com.mohamed.order.service.domain.event.OrderCreatedEvent;
import com.mohamed.order.service.domain.exception.OrderDomainException;
import com.mohamed.order.service.domain.mapper.OrderDataMapper;
import com.mohamed.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessage;
import com.mohamed.order.service.domain.ports.output.repository.CustomerRepository;
import com.mohamed.order.service.domain.ports.output.repository.OrderRepository;
import com.mohamed.order.service.domain.ports.output.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class OrderCreateCommandHandler {

    private final OrderCreateHelper orderCreateHelper;
    private final OrderDataMapper orderDataMapper;
    private final OrderCreatedPaymentRequestMessage orderCreatedPaymentRequestMessagePublisher;

    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);// Save the order to the repository
        log.info("Order with id {} created successfully", orderCreatedEvent.getOrder().getId().getValue());
        orderCreatedPaymentRequestMessagePublisher.publish(orderCreatedEvent);
        return orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder(), "Order Created Successfully"); // Return a response after creating the order
    }
}
