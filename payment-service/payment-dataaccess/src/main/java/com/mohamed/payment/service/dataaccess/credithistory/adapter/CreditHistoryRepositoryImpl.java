package com.mohamed.payment.service.dataaccess.credithistory.adapter;

import com.mohamed.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import com.mohamed.payment.service.dataaccess.credithistory.mapper.CreditHistoryDataAccessMapper;
import com.mohamed.payment.service.dataaccess.credithistory.repository.CreditHistoryJpaRepository;
import com.mohamed.payment.service.domain.entity.CreditHistory;
import com.mohamed.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.mohamed.valueobject.CustomerId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {

    private final CreditHistoryJpaRepository creditHistoryRepository;
    private final CreditHistoryDataAccessMapper mapper;

    public CreditHistoryRepositoryImpl(CreditHistoryJpaRepository creditHistoryRepository1,
                                       CreditHistoryDataAccessMapper mapper) {
        this.creditHistoryRepository = creditHistoryRepository1;
        this.mapper = mapper;
    }

    @Override
    public CreditHistory save(CreditHistory creditHistory) {
        CreditHistoryEntity creditHistoryEntity = mapper.creditHistoryToCreditHistoryEntity(creditHistory);
        return mapper.creditHistoryEntityToCreditHistory(
                creditHistoryRepository.save(creditHistoryEntity));
    }

    @Override
    public Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId) {
        Optional<List<CreditHistoryEntity>> creditHistory =
                creditHistoryRepository.findByCustomerId(customerId.getValue());
        return creditHistory
                .map(creditHistoryList ->
                        creditHistoryList.stream()
                                .map(mapper::creditHistoryEntityToCreditHistory)
                                .collect(Collectors.toList()));
    }
}
