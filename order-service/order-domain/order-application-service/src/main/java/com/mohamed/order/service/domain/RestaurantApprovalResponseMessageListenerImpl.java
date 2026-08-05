package com.mohamed.order.service.domain;

import com.mohamed.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.mohamed.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalMessageResponseListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


@Slf4j
@Validated
@Service
public class RestaurantApprovalResponseMessageListenerImpl implements RestaurantApprovalMessageResponseListener {

    @Override
    public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {

    }

    @Override
    public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {

    }
}
