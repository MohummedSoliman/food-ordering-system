package com.mohamed.order.service.domain;

import com.mohamed.order.service.domain.dto.track.TrackOrderQuery;
import com.mohamed.order.service.domain.dto.track.TrackOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderTrackCommandHandler {

    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        log.info("Tracking order with query: {}", trackOrderQuery);
        // Implement the logic to track an order here
        return new TrackOrderResponse(); // Return a response after tracking the order
    }
}


