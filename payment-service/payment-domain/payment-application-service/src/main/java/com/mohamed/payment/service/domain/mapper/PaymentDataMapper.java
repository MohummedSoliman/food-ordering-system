package com.mohamed.payment.service.domain.mapper;

import com.mohamed.payment.service.domain.dto.PaymentRequest;
import com.mohamed.payment.service.domain.entity.Payment;
import com.mohamed.payment.service.domain.valueobject.PaymentId;
import com.mohamed.valueobject.CustomerId;
import com.mohamed.valueobject.Money;
import com.mohamed.valueobject.OrderId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentDataMapper {
    public Payment paymentRequestModelToPayment(PaymentRequest paymentRequest) {
        return Payment.Builder.builder()
                .orderId(new OrderId(UUID.fromString(paymentRequest.getOrderId())))
                .customerId(new CustomerId(UUID.fromString(paymentRequest.getCustomerId())))
                .price(new Money(paymentRequest.getPrice()))
                .build();
    }
}
