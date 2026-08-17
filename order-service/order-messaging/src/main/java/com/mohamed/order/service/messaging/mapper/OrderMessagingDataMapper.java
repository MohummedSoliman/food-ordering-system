package com.mohamed.order.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.*;
import com.mohamed.order.service.domain.dto.message.PaymentResponse;
import com.mohamed.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.mohamed.order.service.domain.entity.Order;
import com.mohamed.order.service.domain.event.OrderCancelledEvent;
import com.mohamed.order.service.domain.event.OrderCreatedEvent;
import com.mohamed.order.service.domain.event.OrderPaidEvent;
import com.mohamed.valueobject.OrderApprovalStatus;
import com.mohamed.valueobject.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {

    public PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {
        Order order = orderCreatedEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setCustomerId(order.getCustomerId().getValue().toString())
                .setOrderId(order.getId().getValue().toString())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();
    }

    public PaymentRequestAvroModel orderCanceledEventToPaymentRequestAvroModel(OrderCancelledEvent orderCancelledEvent) {
        Order order = orderCancelledEvent.getOrder();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setCustomerId(order.getCustomerId().getValue().toString())
                .setOrderId(order.getId().getValue().toString())
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
                .build();
    }

    public RestaurantApprovalRequestAvroModel orderPaidEventToRestaurantApprovalRequestAvroModel(OrderPaidEvent orderPaidEvent) {
        Order order = orderPaidEvent.getOrder();
        return RestaurantApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId("")
                .setOrderId(order.getId().getValue().toString())
                .setRestaurantId(order.getRestaurantId().getValue().toString())
                .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
                .setProducts(order.getItems().stream().map(orderItem ->
                                Product.newBuilder()
                                        .setId(orderItem.getId().getValue().toString())
                                        .setQuantity(orderItem.getQuantity())
                                        .build()
                        )
                        .collect(Collectors.toList()))
                .setPrice(order.getPrice().getAmount())
                .setCreatedAt(orderPaidEvent.getCreatedAt().toInstant())
                .build();
    }

    public PaymentResponse paymentResponseArvoModelToPaymentResponse(PaymentResponseAvroModel paymentResponseAvroModel) {
        return PaymentResponse.builder()
                .id(paymentResponseAvroModel.getId())
                .orderId(paymentResponseAvroModel.getOrderId())
                .paymentId(paymentResponseAvroModel.getPaymentId())
                .customerId(paymentResponseAvroModel.getCustomerId())
                .sagaId(paymentResponseAvroModel.getSagaId())
                .price(paymentResponseAvroModel.getPrice())
                .paymentStatus(PaymentStatus.valueOf(paymentResponseAvroModel.getPaymentStatus().toString()))
                .createdAt(paymentResponseAvroModel.getCreatedAt())
                .failureMessages(paymentResponseAvroModel.getFailureMessages())
                .build();
    }

    public RestaurantApprovalResponse restaurantApprovalResponseArvoToRestaurantApprovalResponse(
            RestaurantApprovalResponseAvroModel responseAvroModel) {
        return RestaurantApprovalResponse.builder()
                .id(responseAvroModel.getId())
                .orderId(responseAvroModel.getOrderId())
                .restaurantId(responseAvroModel.getRestaurantId())
                .sagaId(responseAvroModel.getSagaId())
                .createdAt(responseAvroModel.getCreatedAt())
                .failureMessages(responseAvroModel.getFailureMessages())
                .orderApprovalStatus(
                        OrderApprovalStatus.valueOf(responseAvroModel.getOrderApprovalStatus().toString())
                )
                .build();
    }
}
