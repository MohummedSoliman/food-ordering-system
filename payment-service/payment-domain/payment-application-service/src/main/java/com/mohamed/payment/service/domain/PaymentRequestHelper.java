package com.mohamed.payment.service.domain;

import com.mohamed.event.publisher.DomainEventPublisher;
import com.mohamed.payment.service.domain.dto.PaymentRequest;
import com.mohamed.payment.service.domain.entity.CreditEntry;
import com.mohamed.payment.service.domain.entity.CreditHistory;
import com.mohamed.payment.service.domain.entity.Payment;
import com.mohamed.payment.service.domain.event.PaymentEvent;
import com.mohamed.payment.service.domain.event.PaymentFailedEvent;
import com.mohamed.payment.service.domain.exception.PaymentApplicationServiceException;
import com.mohamed.payment.service.domain.mapper.PaymentDataMapper;
import com.mohamed.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import com.mohamed.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.mohamed.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import com.mohamed.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.mohamed.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.mohamed.payment.service.domain.ports.output.repository.PaymentRepository;
import com.mohamed.valueobject.CustomerId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PaymentRequestHelper {

    private final PaymentDataMapper mapper;
    private final PaymentDomainService paymentDomainService;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final PaymentCompletedMessagePublisher paymentCompletedEventDomainEventPublisher;
    private final PaymentCancelledMessagePublisher paymentCancelledEventDomainEventPublisher;
    private final PaymentFailedMessagePublisher paymentFailedEventDomainEventPublisher;


    public PaymentRequestHelper(PaymentDataMapper mapper,
                                PaymentDomainService paymentDomainService,
                                PaymentRepository paymentRepository,
                                CreditEntryRepository creditEntryRepository,
                                CreditHistoryRepository creditHistoryRepository,
                                PaymentCompletedMessagePublisher paymentCompletedEventDomainEventPublisher,
                                PaymentCancelledMessagePublisher paymentCancelledEventDomainEventPublisher,
                                PaymentFailedMessagePublisher paymentFailedEventDomainEventPublisher) {
        this.mapper = mapper;
        this.paymentDomainService = paymentDomainService;
        this.paymentRepository = paymentRepository;
        this.creditEntryRepository = creditEntryRepository;
        this.creditHistoryRepository = creditHistoryRepository;
        this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
        this.paymentCancelledEventDomainEventPublisher = paymentCancelledEventDomainEventPublisher;
        this.paymentFailedEventDomainEventPublisher = paymentFailedEventDomainEventPublisher;
    }

    @Transactional
    public PaymentEvent persistPayment(PaymentRequest paymentRequest) {
        log.info("Receive Payment complete event for order id: {}", paymentRequest.getOrderId());
        Payment payment = mapper.paymentRequestModelToPayment(paymentRequest);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
        List<String> failureMessage = new ArrayList<>();
        PaymentEvent paymentEvent =
                paymentDomainService.validateAndInitiatePayment(
                        payment, creditEntry, creditHistories,
                        failureMessage, paymentCompletedEventDomainEventPublisher,
                        paymentFailedEventDomainEventPublisher);
        persistDBObject(payment, failureMessage, creditEntry, creditHistories);
        return paymentEvent;
    }

    @Transactional
    public PaymentEvent persistCancelPayment(PaymentRequest paymentRequest) {
        log.info("Receive Payment rollback event for order id: {}", paymentRequest.getOrderId());
        Optional<Payment> paymentResponse = paymentRepository.findByOrderId(UUID.fromString(paymentRequest.getOrderId()));
        if (paymentResponse.isEmpty()) {
            log.error("Payment with order id: {} could not be found", paymentRequest.getOrderId());
            throw new PaymentApplicationServiceException("Payment with order id " +
                    paymentRequest.getOrderId() + "Could not be found");
        }
        Payment payment = paymentResponse.get();
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndCancelledEvent(
                payment, creditEntry, creditHistories,
                failureMessages,
                paymentCancelledEventDomainEventPublisher,
                paymentFailedEventDomainEventPublisher);
        persistDBObject(payment, failureMessages, creditEntry, creditHistories);
        return paymentEvent;
    }

    private void persistDBObject(Payment payment, List<String> failureMessage, CreditEntry creditEntry, List<CreditHistory> creditHistories) {
        paymentRepository.save(payment);
        if (failureMessage.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
        }
    }

    private CreditEntry getCreditEntry(CustomerId customerId) {
        Optional<CreditEntry> creditEntry = creditEntryRepository.findByCustomerId(customerId);
        if (creditEntry.isEmpty()) {
            log.error("Could not find credit entry for customer id: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit entry for customer id:"
                    + customerId.getValue());
        }

        return creditEntry.get();
    }

    private List<CreditHistory> getCreditHistory(CustomerId customerId) {
        Optional<List<CreditHistory>> creditHistories = creditHistoryRepository.findByCustomerId(customerId);
        if (creditHistories.isEmpty()) {
            log.error("Could not find credit history for customer id: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit entry for customer id:" +
                    customerId.getValue());
        }
        return creditHistories.get();
    }
}
