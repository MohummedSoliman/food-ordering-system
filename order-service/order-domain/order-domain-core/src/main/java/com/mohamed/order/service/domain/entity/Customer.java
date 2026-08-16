package com.mohamed.order.service.domain.entity;

import com.mohamed.entity.AggregateRoot;
import com.mohamed.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {

    public Customer() {
    }

    public Customer(CustomerId customerId) {
        super.setId(customerId);
    }
}
