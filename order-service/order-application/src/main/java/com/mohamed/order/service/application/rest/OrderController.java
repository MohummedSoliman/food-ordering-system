package com.mohamed.order.service.application.rest;

import com.mohamed.order.service.domain.dto.create.CreateOrderCommand;
import com.mohamed.order.service.domain.dto.create.CreateOrderResponse;
import com.mohamed.order.service.domain.dto.track.TrackOrderQuery;
import com.mohamed.order.service.domain.dto.track.TrackOrderResponse;
import com.mohamed.order.service.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/orders", produces = "application/vnd.api.v1+json")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderCommand createOrderCommand) {
        log.info("Creating order for customer: {} at restaurant: {}",
                createOrderCommand.getCustomerId(), createOrderCommand.getRestaurantId());

        CreateOrderResponse orderResponse = orderApplicationService.createOrder(createOrderCommand);
        log.info("Order created with tracking id: {}", orderResponse.getOrderTrackingId());
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<TrackOrderResponse> getOrderByTrackingId(@PathVariable UUID trackingId) {
        TrackOrderResponse trackOrderResponse = orderApplicationService.trackOrder(
                TrackOrderQuery.builder()
                        .orderTrackingId(trackingId)
                        .build()
        );
        log.info("Returning order status with tracking id: {} ", trackingId);
        return ResponseEntity.ok(trackOrderResponse);
    }
}
