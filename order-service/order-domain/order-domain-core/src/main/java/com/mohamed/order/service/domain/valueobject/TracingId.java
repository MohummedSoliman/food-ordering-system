package com.mohamed.order.service.domain.valueobject;

import com.mohamed.valueobject.BaseId;

import java.util.UUID;

public class TracingId  extends BaseId<UUID> {
    public TracingId(UUID value) {
        super(value);
    }
}
