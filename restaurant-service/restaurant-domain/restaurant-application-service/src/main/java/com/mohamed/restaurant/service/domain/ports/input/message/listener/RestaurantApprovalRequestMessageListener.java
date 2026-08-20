package com.mohamed.restaurant.service.domain.ports.input.message.listener;

import com.mohamed.restaurant.service.domain.dto.RestaurantApprovalReqeust;

public interface RestaurantApprovalRequestMessageListener {

    void approveOrder(RestaurantApprovalReqeust restaurantApprovalReqeust);
}
