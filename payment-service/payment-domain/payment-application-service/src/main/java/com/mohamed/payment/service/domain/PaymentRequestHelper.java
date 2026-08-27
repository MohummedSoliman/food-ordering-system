package com.mohamed.payment.service.domain;

import com.mohamed.outbox.OutboxStatus;
import com.mohamed.payment.service.domain.dto.PaymentRequest;
import com.mohamed.payment.service.domain.entity.CreditEntry;
import com.mohamed.payment.service.domain.entity.CreditHistory;
import com.mohamed.payment.service.domain.entity.Payment;
import com.mohamed.payment.service.domain.event.PaymentEvent;
import com.mohamed.payment.service.domain.exception.PaymentApplicationServiceException;
import com.mohamed.payment.service.domain.mapper.PaymentDataMapper;
import com.mohamed.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.mohamed.payment.service.domain.outbox.scheduler.OrderOutboxHelper;
import com.mohamed.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import com.mohamed.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.mohamed.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.mohamed.payment.service.domain.ports.output.repository.PaymentRepository;
import com.mohamed.valueobject.CustomerId;
import com.mohamed.valueobject.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class PaymentRequestHelper {

    private final PaymentDataMapper mapper;
    private final PaymentDomainService paymentDomainService;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final OrderOutboxHelper orderOutboxHelper;
    private final PaymentResponseMessagePublisher paymentResponseMessagePublisher;

    @Transactional
    public void persistPayment(PaymentRequest paymentRequest) {

        if (publishIfOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.COMPLETED)) {
            log.info("An outbox message with sage id: {} is already saved", paymentRequest.getSageId());
            return;
        }

        log.info("Receive Payment complete event for order id: {}", paymentRequest.getOrderId());
        Payment payment = mapper.paymentRequestModelToPayment(paymentRequest);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
        List<String> failureMessage = new ArrayList<>();
        PaymentEvent paymentEvent =
                paymentDomainService.validateAndInitiatePayment(
                        payment, creditEntry, creditHistories,
                        failureMessage);
        persistDBObject(payment, failureMessage, creditEntry, creditHistories);

        orderOutboxHelper.saveOrderOutboxMessage(
                mapper.paymentEventToOrderEventPayload(paymentEvent),
                paymentEvent.getPayment().getPaymentStatus(),
                OutboxStatus.STARTED,
                UUID.fromString(paymentRequest.getSageId())
        );
    }

    @Transactional
    public void persistCancelPayment(PaymentRequest paymentRequest) {

        if (publishIfOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.CANCELLED)) {
            log.info("An outbox message with sage id: {} is already Cancelled", paymentRequest.getSageId());
            return;
        }

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
                failureMessages);
        persistDBObject(payment, failureMessages, creditEntry, creditHistories);

        orderOutboxHelper.saveOrderOutboxMessage(
                mapper.paymentEventToOrderEventPayload(paymentEvent),
                paymentEvent.getPayment().getPaymentStatus(),
                OutboxStatus.STARTED,
                UUID.fromString(paymentRequest.getSageId())
        );
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

    private boolean publishIfOutboxMessageProcessedForPayment(PaymentRequest paymentRequest,
                                                              PaymentStatus paymentStatus) {
        Optional<OrderOutboxMessage> orderOutboxMessage =
                orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(
                        UUID.fromString(paymentRequest.getSageId()),
                        paymentStatus
                );

        if (orderOutboxMessage.isPresent()) {
            paymentResponseMessagePublisher.publish(
                    orderOutboxMessage.get(),
                    orderOutboxHelper::updateOutboxMessage
            );
            return true;
        }
        return false;
    }
}
