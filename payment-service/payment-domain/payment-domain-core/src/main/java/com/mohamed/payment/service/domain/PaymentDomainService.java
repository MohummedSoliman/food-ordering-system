package com.mohamed.payment.service.domain;

import com.mohamed.event.publisher.DomainEventPublisher;
import com.mohamed.payment.service.domain.entity.CreditEntry;
import com.mohamed.payment.service.domain.entity.CreditHistory;
import com.mohamed.payment.service.domain.entity.Payment;
import com.mohamed.payment.service.domain.event.PaymentCancelledEvent;
import com.mohamed.payment.service.domain.event.PaymentCompletedEvent;
import com.mohamed.payment.service.domain.event.PaymentEvent;
import com.mohamed.payment.service.domain.event.PaymentFailedEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment,
                                            CreditEntry creditEntry,
                                            List<CreditHistory> creditHistories,
                                            List<String> failureMessages,
                                            DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher, DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher);

    PaymentEvent validateAndCancelledEvent(Payment payment,
                                           CreditEntry creditEntry,
                                           List<CreditHistory> creditHistories,
                                           List<String> failureMessages,
                                           DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher, DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher);
}
