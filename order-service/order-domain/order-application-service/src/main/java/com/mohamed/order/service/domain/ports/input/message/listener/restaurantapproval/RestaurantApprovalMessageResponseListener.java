package com.mohamed.order.service.domain.ports.input.message.listener.restaurantapproval;

import com.mohamed.order.service.domain.dto.message.RestaurantApprovalResponse;

public interface RestaurantApprovalMessageResponseListener {

    void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse);

    void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse);
}
