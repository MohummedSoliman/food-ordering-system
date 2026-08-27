package com.mohamed.payment.service.domain;

import com.mohamed.payment.service.dataaccess.outbox.repository.OrderOutboxJpaRepository;
import com.mohamed.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = PaymentServiceApplication.class)
public class PaymentRequestMessageListenerTest {

    @Autowired
    private PaymentRequestMessageListener paymentRequestMessageListener;
    @Autowired
    private OrderOutboxJpaRepository orderOutboxJpaRepository;
}
