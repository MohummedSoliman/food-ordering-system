package com.mohamed.payment.service.domain;

import com.mohamed.payment.service.domain.entity.CreditEntry;
import com.mohamed.payment.service.domain.entity.CreditHistory;
import com.mohamed.payment.service.domain.entity.Payment;
import com.mohamed.payment.service.domain.event.PaymentEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentEvent validateAndInitiatePayment(Payment payment,
                                            CreditEntry creditEntry,
                                            List<CreditHistory> creditHistories,
                                            List<String> failureMessages);

    PaymentEvent validateAndCancelledEvent(Payment payment,
                                           CreditEntry creditEntry,
                                           List<CreditHistory> creditHistories,
                                           List<String> failureMessages);
}
