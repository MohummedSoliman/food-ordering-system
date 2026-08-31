package com.mohamed.order.service.domain.ports.input.customer;

import com.mohamed.order.service.domain.dto.message.CustomerModel;

public interface CustomerMessageListener {

    void customerCreated(CustomerModel customerModel);
}
