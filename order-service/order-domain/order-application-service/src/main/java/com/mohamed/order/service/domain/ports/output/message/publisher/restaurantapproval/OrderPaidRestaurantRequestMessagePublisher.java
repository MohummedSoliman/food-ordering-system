package com.mohamed.order.service.domain.ports.output.message.publisher.restaurantapproval;

import com.mohamed.event.publisher.DomainEventPublisher;
import com.mohamed.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidRestaurantRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
