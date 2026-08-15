package com.mohamed.order.service.dataaccess.order.adapter;

import com.mohamed.order.service.dataaccess.order.entity.OrderEntity;
import com.mohamed.order.service.dataaccess.order.mapper.OrderDataAccessMapper;
import com.mohamed.order.service.dataaccess.order.repository.OrderJpaRepository;
import com.mohamed.order.service.domain.entity.Order;
import com.mohamed.order.service.domain.ports.output.repository.OrderRepository;
import com.mohamed.order.service.domain.valueobject.TracingId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataAccessMapper mapper;

    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository,
                               OrderDataAccessMapper mapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = this.mapper.orderToOrderEntity(order);
        OrderEntity savedOrderEntity = this.orderJpaRepository.save(orderEntity);
        return mapper.orderEntityToOrder(savedOrderEntity);
    }

    @Override
    public Optional<Order> findByTrackingId(TracingId trackingId) {
        return orderJpaRepository.findByTrackingID(trackingId.getValue())
                .map(mapper::orderEntityToOrder);

    }
}
