package com.mohamed.order.service.dataaccess.outbox.restaurantapproval.exception;

public class ApprovalOutboxNotFoundException extends RuntimeException {

    public ApprovalOutboxNotFoundException(String message) {
        super(message);
    }
}
