package com.mohamed.order.service.domain.ports.output.repository;

import com.mohamed.order.service.domain.entity.Order;
import com.mohamed.order.service.domain.valueobject.TracingId;
import com.mohamed.valueobject.OrderId;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(OrderId orderId);

    Optional<Order> findByTrackingId(TracingId trackingId);
}
