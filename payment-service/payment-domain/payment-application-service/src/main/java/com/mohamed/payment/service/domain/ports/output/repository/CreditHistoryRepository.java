package com.mohamed.payment.service.domain.ports.output.repository;

import com.mohamed.payment.service.domain.entity.CreditHistory;
import com.mohamed.valueobject.CustomerId;

import java.util.List;
import java.util.Optional;

public interface CreditHistoryRepository {

    CreditHistory save(CreditHistory creditHistory);

    Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId);
}
