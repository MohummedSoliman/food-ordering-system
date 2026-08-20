package com.mohamed.restaurant.service.domain.ports.output.repository;

import com.mohamed.restaurant.service.domain.entity.OrderApproval;

public interface OrderApprovalRepository {

    OrderApproval save(OrderApproval orderApproval);
}
