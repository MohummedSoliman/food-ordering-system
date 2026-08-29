package com.mohamed.customer.service.domain.exception;

import com.mohamed.exception.DomainException;

public class CustomerDomainException extends DomainException {
    public CustomerDomainException(String message) {
        super(message);
    }
}
