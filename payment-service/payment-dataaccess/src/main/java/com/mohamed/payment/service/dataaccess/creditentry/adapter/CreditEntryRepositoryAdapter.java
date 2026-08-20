package com.mohamed.payment.service.dataaccess.creditentry.adapter;

import com.mohamed.payment.service.dataaccess.creditentry.entity.CreditEntryEntity;
import com.mohamed.payment.service.dataaccess.creditentry.mapper.CreditEntryDataAccessMapper;
import com.mohamed.payment.service.dataaccess.creditentry.repository.CreditEntryJpaRepository;
import com.mohamed.payment.service.domain.entity.CreditEntry;
import com.mohamed.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.mohamed.valueobject.CustomerId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreditEntryRepositoryAdapter implements CreditEntryRepository {

    private final CreditEntryJpaRepository creditEntryJpaRepository;
    private final CreditEntryDataAccessMapper mapper;

    public CreditEntryRepositoryAdapter(CreditEntryJpaRepository creditEntryRepository,
                                        CreditEntryDataAccessMapper mapper) {
        this.creditEntryJpaRepository = creditEntryRepository;
        this.mapper = mapper;
    }

    @Override
    public CreditEntry save(CreditEntry creditEntry) {
        CreditEntryEntity creditEntryEntity = mapper.creditEntryToCreditEntryEntity(creditEntry);
        return mapper.creditEntryEntityToCreditEntry(creditEntryJpaRepository.save(creditEntryEntity));
    }

    @Override
    public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
        return creditEntryJpaRepository.findByCustomerId(customerId.getValue())
                .map(mapper::creditEntryEntityToCreditEntry);
    }
}
