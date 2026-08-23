package com.mohamed.order.service.domain;

import com.mohamed.event.EmptyEvent;
import com.mohamed.order.service.domain.dto.message.PaymentResponse;
import com.mohamed.order.service.domain.event.OrderPaidEvent;
import com.mohamed.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


@Slf4j
@Validated
@AllArgsConstructor
@Service
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {

    private final OrderPaymentSage orderPaymentSage;

    @Override
    public void paymentCompleted(PaymentResponse paymentResponse) {
        OrderPaidEvent domainEvent = orderPaymentSage.proces(paymentResponse);
        log.info("Publishing OrderPaidEvent for order id: {}", paymentResponse.getOrderId());
        domainEvent.fire();
    }

    @Override
    public void paymentCancelled(PaymentResponse paymentResponse) {
        orderPaymentSage.rollback(paymentResponse);
        log.info("Order is roll back for order id: {}, with failure messages {}",
                paymentResponse.getOrderId(),
                String.join(",", paymentResponse.getFailureMessages()));
    }
}
