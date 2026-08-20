package com.mohamed.restaurant.service.domain.exception;

import com.mohamed.exception.DomainException;

public class RestaurantNotFoundException extends DomainException {

    public RestaurantNotFoundException(String message) {
        super(message);
    }

    public RestaurantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
