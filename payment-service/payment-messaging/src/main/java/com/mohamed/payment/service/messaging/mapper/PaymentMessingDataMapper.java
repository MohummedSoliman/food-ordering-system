package com.mohamed.payment.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.mohamed.payment.service.domain.dto.PaymentRequest;
import com.mohamed.payment.service.domain.outbox.model.OrderEventPayload;
import com.mohamed.valueobject.PaymentOrderStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessingDataMapper {

    public PaymentRequest
    paymentRequestAvroModelToPaymentRequest(PaymentRequestAvroModel paymentRequestAvroModel) {
        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId())
                .sageId(paymentRequestAvroModel.getSagaId())
                .customerId(paymentRequestAvroModel.getCustomerId())
                .orderId(paymentRequestAvroModel.getOrderId())
                .price(paymentRequestAvroModel.getPrice())
                .paymentOrderStatus(PaymentOrderStatus
                        .valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()))
                .createdAt(paymentRequestAvroModel.getCreatedAt())
                .build();
    }

    public PaymentResponseAvroModel orderEventPayloadToPaymentResponseAvroModel(
            String sagaId, OrderEventPayload orderEventPayload) {
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setOrderId(orderEventPayload.getOrderId())
                .setPaymentId(orderEventPayload.getPaymentId())
                .setCustomerId(orderEventPayload.getCustomerId())
                .setPrice(orderEventPayload.getPrice())
                .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(orderEventPayload.getPaymentStatus()))
                .setFailureMessages(orderEventPayload.getFailureMessages())
                .build();
    }
}
