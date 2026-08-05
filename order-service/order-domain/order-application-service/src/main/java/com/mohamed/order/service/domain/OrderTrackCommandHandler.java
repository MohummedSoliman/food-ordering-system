package com.mohamed.order.service.domain;

import com.mohamed.order.service.domain.exception.OrderNotFoundException;
import com.mohamed.order.service.domain.dto.track.TrackOrderQuery;
import com.mohamed.order.service.domain.dto.track.TrackOrderResponse;
import com.mohamed.order.service.domain.entity.Order;
import com.mohamed.order.service.domain.mapper.OrderDataMapper;
import com.mohamed.order.service.domain.ports.output.repository.OrderRepository;
import com.mohamed.order.service.domain.valueobject.TracingId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class OrderTrackCommandHandler {

    private final OrderDataMapper orderDataMapper;
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        log.info("Tracking order with query: {}", trackOrderQuery);
        Optional<Order> orderResult = orderRepository.findByTrackingId(new TracingId(trackOrderQuery.getOrderTrackingId()));
        if (orderResult.isEmpty()) {
            log.warn("Order with tracking id {} not found", trackOrderQuery.getOrderTrackingId());
            throw new OrderNotFoundException("Order with tracking id " + trackOrderQuery.getOrderTrackingId() + " not found");
        }
        // Implement the logic to track an order here
        return orderDataMapper.orderToTrackOrderResponse(orderResult.get()); // Return a response after tracking the order
    }
}


