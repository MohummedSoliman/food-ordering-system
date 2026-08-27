package com.mohamed.restaurant.service.domain;

import com.mohamed.restaurant.service.domain.dto.RestaurantApprovalReqeust;
import com.mohamed.restaurant.service.domain.event.OrderApprovalEvent;
import com.mohamed.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@AllArgsConstructor
@Service
public class RestaurantApprovalRequestMessageListenerImpl implements RestaurantApprovalRequestMessageListener {

    private final RestaurantApprovalRequestHelper helper;

    @Override
    public void approveOrder(RestaurantApprovalReqeust restaurantApprovalReqeust) {
        OrderApprovalEvent orderApprovalEvent = helper.persistOrderApproval(restaurantApprovalReqeust);
    }
}
