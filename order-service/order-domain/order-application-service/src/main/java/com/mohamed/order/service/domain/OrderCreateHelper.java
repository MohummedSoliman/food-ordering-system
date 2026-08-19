package com.mohamed.order.service.domain;

import com.mohamed.event.publisher.DomainEventPublisher;
import com.mohamed.order.service.domain.dto.create.CreateOrderCommand;
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
@AllArgsConstructor
@Component
public class OrderCreateHelper {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper orderDataMapper;
    private final OrderCreatedPaymentRequestMessage orderCreatedEventDomainEventPublisher;

    @Transactional
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);// Map the command to an Order entity
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order,
                restaurant,
                orderCreatedEventDomainEventPublisher);// Validate and initiate the order
        saveOrder(order);
        log.info("Order with id {} created successfully", order.getId().getValue());
        return orderCreatedEvent;
    }

    private void checkCustomer(UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomerById(customerId);
        if (customer.isEmpty()) {
            log.warn("Customer with id {} not found", customerId);
            throw new OrderDomainException("Customer with id " + customerId + " not found");
        }
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findRestaurantInformation(restaurant);
        if (optionalRestaurant.isEmpty()) {
            log.warn("Restaurant with id {} not found", createOrderCommand.getRestaurantId());
            throw new OrderDomainException("Restaurant with id " + createOrderCommand.getRestaurantId() + " not found");
        }
        return optionalRestaurant.get();
    }

    private Order saveOrder(Order order) {
        Order orderResult = orderRepository.save(order);
        if (orderResult == null) {
            log.error("Failed to save order with id {}", order.getId().getValue());
            throw new OrderDomainException("Failed to save order with id " + order.getId());
        }
        log.info("Order with id {} saved successfully", order.getId().getValue());
        return orderResult;
    }
}
