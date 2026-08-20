package com.mohamed.payment.service.dataaccess.payment.mapper;

import com.mohamed.payment.service.dataaccess.payment.entity.PaymentEntity;
import com.mohamed.payment.service.domain.entity.Payment;
import com.mohamed.payment.service.domain.valueobject.PaymentId;
import com.mohamed.valueobject.CustomerId;
import com.mohamed.valueobject.Money;
import com.mohamed.valueobject.OrderId;
import org.springframework.stereotype.Component;

@Component
public class PaymentDataAccessMapper {

    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId().getValue())
                .orderId(payment.getOrderId().getValue())
                .customerId(payment.getCustomerId().getValue())
                .paymentStatus(payment.getPaymentStatus())
                .price(payment.getPrice().getAmount())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
        return Payment.Builder.builder()
                .paymentId(new PaymentId(paymentEntity.getId()))
                .orderId(new OrderId(paymentEntity.getOrderId()))
                .customerId(new CustomerId(paymentEntity.getCustomerId()))
                .price(new Money(paymentEntity.getPrice()))
                .createdAt(paymentEntity.getCreatedAt())
                .build();
    }
}
