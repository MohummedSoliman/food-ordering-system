package com.mohamed.order.service.domain.mapper;

import com.mohamed.order.service.domain.dto.create.CreateOrderCommand;
import com.mohamed.order.service.domain.dto.create.CreateOrderResponse;
import com.mohamed.order.service.domain.dto.create.OrderAddress;
import com.mohamed.order.service.domain.dto.create.OrderItem;
import com.mohamed.order.service.domain.dto.track.TrackOrderResponse;
import com.mohamed.order.service.domain.entity.Order;
import com.mohamed.order.service.domain.entity.Product;
import com.mohamed.order.service.domain.entity.Restaurant;
import com.mohamed.order.service.domain.event.OrderCancelledEvent;
import com.mohamed.order.service.domain.event.OrderCreatedEvent;
import com.mohamed.order.service.domain.event.OrderPaidEvent;
import com.mohamed.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.mohamed.order.service.domain.outbox.model.approval.OrderApprovalEventProduct;
import com.mohamed.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.mohamed.order.service.domain.valueobject.StreetAddress;
import com.mohamed.valueobject.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {
    public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
        return Restaurant.Builder
                .builder()
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .products(createOrderCommand.getItems().stream()
                        .map(orderItem -> new Product(new ProductId(orderItem.getProductId())))
                        .collect(Collectors.toList())
                )
                .build(); // Return the mapped Restaurant object
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTracingId().getValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build(); // Return the mapped CreateOrderResponse object
    }

    public TrackOrderResponse orderToTrackOrderResponse(Order order) {
        return TrackOrderResponse.builder()
                .orderTrackingId(order.getTracingId().getValue())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build(); // Return the mapped TrackOrderResponse object
    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.Builder
                .builder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
                .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(orderItemsToOrderItemsEntity(createOrderCommand.getItems()))
                .build(); // Return the mapped Order object
    }

    public OrderPaymentEventPayload orderCreateEventToOrderPaymentEventPayload(
            OrderCreatedEvent orderCreatedEvent) {
        return OrderPaymentEventPayload.builder()
                .orderId(orderCreatedEvent.getOrder().getId().getValue().toString())
                .customerId(orderCreatedEvent.getOrder().getCustomerId().getValue().toString())
                .price(orderCreatedEvent.getOrder().getPrice().getAmount())
                .createdAt(orderCreatedEvent.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
                .build();
    }

    public OrderApprovalEventPayload orderPaidEventToorderApprovalEventPayload(OrderPaidEvent orderPaidEvent) {
        return OrderApprovalEventPayload.builder()
                .orderId(orderPaidEvent.getOrder().getId().getValue().toString())
                .restaurantId(orderPaidEvent.getOrder().getRestaurantId().getValue().toString())
                .price(orderPaidEvent.getOrder().getPrice().getAmount())
                .createdAt(orderPaidEvent.getCreatedAt())
                .restaurantOrderStatus(RestaurantOrderStatus.PAID.name())
                .products(orderPaidEvent.getOrder().getItems().stream()
                        .map(orderItem ->
                                OrderApprovalEventProduct.builder()
                                        .id(orderItem.getId().getValue().toString())
                                        .quantity(orderItem.getQuantity())
                                        .build())
                        .toList())
                .build();
    }

    public OrderPaymentEventPayload orderCancelledEventToorderPaymentEventPayload(OrderCancelledEvent orderCancelledEvent) {
        return OrderPaymentEventPayload.builder()
                .orderId(orderCancelledEvent.getOrder().getId().getValue().toString())
                .customerId(orderCancelledEvent.getOrder().getCustomerId().getValue().toString())
                .price(orderCancelledEvent.getOrder().getPrice().getAmount())
                .paymentOrderStatus(PaymentOrderStatus.CANCELLED.name())
                .createdAt(orderCancelledEvent.getCreatedAt())
                .build();
    }

    private StreetAddress orderAddressToStreetAddress(OrderAddress orderAddress) {
        return new StreetAddress(
                UUID.randomUUID(),
                orderAddress.getStreet(),
                orderAddress.getPostalCode(),
                orderAddress.getCity()
        );
    }


    private List<com.mohamed.order.service.domain.entity.OrderItem> orderItemsToOrderItemsEntity(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> com.mohamed.order.service.domain.entity.OrderItem.Builder
                        .builder()
                        .product(new Product(new ProductId(orderItem.getProductId())))
                        .quantity(orderItem.getQuantity())
                        .price(new Money(orderItem.getPrice()))
                        .subtotal(new Money(orderItem.getSubTotal()))
                        .build())
                .collect(Collectors.toList());

    }


}
