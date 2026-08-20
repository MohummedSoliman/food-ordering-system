package com.mohamed.restaurant.service.domain.valueobject;

import com.mohamed.valueobject.BaseId;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID> {

    public OrderApprovalId(UUID value) {
        super(value);
    }
}
