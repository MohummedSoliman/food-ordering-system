package com.mohamed.payment.service.domain.ports.output.repository;

import com.mohamed.payment.service.domain.entity.CreditEntry;
import com.mohamed.valueobject.CustomerId;

import java.util.Optional;

public interface CreditEntryRepository {

    CreditEntry save(CreditEntry creditEntry);

    Optional<CreditEntry> findByCustomerId(CustomerId customerId);
}
