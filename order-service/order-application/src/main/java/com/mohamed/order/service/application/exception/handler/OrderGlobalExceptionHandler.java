package com.mohamed.order.service.application.exception.handler;

import com.mohamed.application.handler.ErrorDTO;
import com.mohamed.application.handler.GlobalExceptionHandler;
import com.mohamed.order.service.domain.exception.OrderDomainException;
import com.mohamed.order.service.domain.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class OrderGlobalExceptionHandler extends GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = OrderDomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleError(OrderDomainException orderDomainException) {
        log.error(orderDomainException.getMessage(), orderDomainException);
        return new ErrorDTO(HttpStatus.BAD_REQUEST.getReasonPhrase(), orderDomainException.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleError(OrderNotFoundException orderNotFoundException) {
        log.error(orderNotFoundException.getMessage(), orderNotFoundException);
        return new ErrorDTO(HttpStatus.NOT_FOUND.getReasonPhrase(), orderNotFoundException.getMessage());
    }
}
