package com.mohamed.order.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.*;
import com.mohamed.order.service.domain.dto.message.PaymentResponse;
import com.mohamed.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.mohamed.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.mohamed.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.mohamed.valueobject.OrderApprovalStatus;
import com.mohamed.valueobject.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {

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

    public PaymentRequestAvroModel orderPaymentEventToPaymentRequestAvroModel(String sagaId, OrderPaymentEventPayload
            orderPaymentEventPayload) {
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setCustomerId(orderPaymentEventPayload.getCustomerId())
                .setOrderId(orderPaymentEventPayload.getOrderId())
                .setPrice(orderPaymentEventPayload.getPrice())
                .setCreatedAt(orderPaymentEventPayload.getCreatedAt().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.valueOf(orderPaymentEventPayload.getPaymentOrderStatus()))
                .build();
    }

    public RestaurantApprovalRequestAvroModel
    orderApprovalEventToRestaurantApprovalRequestAvroModel(String sagaId, OrderApprovalEventPayload
            orderApprovalEventPayload) {
        return RestaurantApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setOrderId(orderApprovalEventPayload.getOrderId())
                .setRestaurantId(orderApprovalEventPayload.getRestaurantId())
                .setRestaurantOrderStatus(RestaurantOrderStatus
                        .valueOf(orderApprovalEventPayload.getRestaurantOrderStatus()))
                .setProducts(orderApprovalEventPayload.getProducts().stream().map(orderApprovalEventProduct ->
                        Product.newBuilder()
                                .setId(orderApprovalEventProduct.getId())
                                .setQuantity(orderApprovalEventProduct.getQuantity())
                                .build()).collect(Collectors.toList()))
                .setPrice(orderApprovalEventPayload.getPrice())
                .setCreatedAt(orderApprovalEventPayload.getCreatedAt().toInstant())
                .build();
    }
}
