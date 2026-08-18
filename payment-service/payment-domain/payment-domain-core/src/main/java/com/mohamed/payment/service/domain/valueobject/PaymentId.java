package com.mohamed.payment.service.domain.valueobject;

import com.mohamed.valueobject.BaseId;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {
    public PaymentId(UUID value) {
        super(value);
    }
}
