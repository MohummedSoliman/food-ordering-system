package com.mohamed.payment.service.dataaccess.payment.adapter;

import com.mohamed.payment.service.dataaccess.payment.entity.PaymentEntity;
import com.mohamed.payment.service.dataaccess.payment.mapper.PaymentDataAccessMapper;
import com.mohamed.payment.service.dataaccess.payment.repository.PaymentJpaRepository;
import com.mohamed.payment.service.domain.entity.Payment;
import com.mohamed.payment.service.domain.ports.output.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentDataAccessMapper mapper;
    private final PaymentJpaRepository paymentJpaRepository;

    public PaymentRepositoryImpl(PaymentDataAccessMapper mapper,
                                 PaymentJpaRepository paymentJpaRepository) {
        this.mapper = mapper;
        this.paymentJpaRepository = paymentJpaRepository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity paymentEntity = mapper.paymentToPaymentEntity(payment);
        return mapper.paymentEntityToPayment(paymentJpaRepository.save(paymentEntity));
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return paymentJpaRepository.findByOrderId(orderId)
                .map(mapper::paymentEntityToPayment);
    }
}
