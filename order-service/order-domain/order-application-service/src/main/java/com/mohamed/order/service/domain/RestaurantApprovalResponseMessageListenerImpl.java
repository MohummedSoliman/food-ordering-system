package com.mohamed.order.service.domain;

import com.mohamed.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.mohamed.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalMessageResponseListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


@Slf4j
@Validated
@AllArgsConstructor
@Service
public class RestaurantApprovalResponseMessageListenerImpl implements RestaurantApprovalMessageResponseListener {

    private final OrderApprovalSaga orderApprovalSaga;

    @Override
    public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {
        orderApprovalSaga.proces(restaurantApprovalResponse);
        log.info("Order is approved for order id: {}", restaurantApprovalResponse.getOrderId());
    }

    @Override
    public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {
        orderApprovalSaga.rollback(restaurantApprovalResponse);
        log.info("Order Approval Saga rollback operation is completed for order id: {}", restaurantApprovalResponse.getOrderId());
    }
}
