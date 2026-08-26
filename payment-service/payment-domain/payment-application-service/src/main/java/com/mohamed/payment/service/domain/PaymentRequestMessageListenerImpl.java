package com.mohamed.payment.service.domain;

import com.mohamed.payment.service.domain.dto.PaymentRequest;
import com.mohamed.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

    private final PaymentRequestHelper helper;

    public PaymentRequestMessageListenerImpl(PaymentRequestHelper helper) {
        this.helper = helper;
    }

    @Override
    public void completePayment(PaymentRequest paymentRequest) {
        helper.persistPayment(paymentRequest);
    }

    @Override
    public void cancelPayment(PaymentRequest paymentRequest) {
        helper.persistCancelPayment(paymentRequest);
    }
}
