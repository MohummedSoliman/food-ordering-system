package com.mohamed.restaurant.service.domain;

import com.mohamed.restaurant.service.domain.entity.Restaurant;
import com.mohamed.restaurant.service.domain.event.OrderApprovalEvent;

import java.util.List;

public interface RestaurantDomainService {

    OrderApprovalEvent validateOrder(Restaurant restaurant,
                                     List<String> failureMessages);
}
