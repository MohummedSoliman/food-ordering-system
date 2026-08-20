package com.mohamed.payment.service.dataaccess.credithistory.mapper;

import com.mohamed.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import com.mohamed.payment.service.domain.entity.CreditHistory;
import com.mohamed.payment.service.domain.valueobject.CreditHistoryId;
import com.mohamed.valueobject.CustomerId;
import com.mohamed.valueobject.Money;
import org.springframework.stereotype.Component;

@Component
public class CreditHistoryDataAccessMapper {

    public CreditHistoryEntity creditHistoryToCreditHistoryEntity(CreditHistory creditHistory) {
        return CreditHistoryEntity.builder()
                .id(creditHistory.getId().getValue())
                .customerId(creditHistory.getCustomerId().getValue())
                .amount(creditHistory.getAmount().getAmount())
                .type(creditHistory.getTransactionType())
                .build();
    }

    public CreditHistory creditHistoryEntityToCreditHistory(CreditHistoryEntity creditHistoryEntity) {
        return CreditHistory.Builder.builder()
                .creditHistoryId(new CreditHistoryId(creditHistoryEntity.getId()))
                .customerId(new CustomerId(creditHistoryEntity.getCustomerId()))
                .amount(new Money(creditHistoryEntity.getAmount()))
                .transactionType(creditHistoryEntity.getType())
                .build();
    }
}
