package com.mohamed.restaurant.service.domain;

import com.mohamed.event.publisher.DomainEventPublisher;
import com.mohamed.restaurant.service.domain.entity.Restaurant;
import com.mohamed.restaurant.service.domain.event.OrderApprovalEvent;
import com.mohamed.restaurant.service.domain.event.OrderApprovedEvent;
import com.mohamed.restaurant.service.domain.event.OrderRejectedEvent;
import com.mohamed.valueobject.OrderApprovalStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.mohamed.DomainConstants.UTC;

@Slf4j
public class RestaurantDomainServiceImpl implements RestaurantDomainService {
    @Override
    public OrderApprovalEvent validateOrder(Restaurant restaurant,
                                            List<String> failureMessages,
                                            DomainEventPublisher<OrderApprovedEvent> orderApprovedEventDomainEventPublisher,
                                            DomainEventPublisher<OrderRejectedEvent> orderRejectedEventDomainEventPublisher) {

        restaurant.validateOrder(failureMessages);
        log.info("Validating order with id: {}", restaurant.getOrderDetail().getId().getValue());

        if (failureMessages.isEmpty()) {
            log.info("Order with id: {} is approved", restaurant.getOrderDetail().getId().getValue());
            restaurant.constructOrderApproval(OrderApprovalStatus.APPROVED);
            return new OrderApprovedEvent(
                    restaurant.getOrderApproval(),
                    restaurant.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC)),
                    orderApprovedEventDomainEventPublisher);
        } else {
            log.info("Order with id : {} is rejected ", restaurant.getOrderDetail().getId().getValue());
            restaurant.constructOrderApproval(OrderApprovalStatus.REJECTED);
            return new OrderRejectedEvent(
                    restaurant.getOrderApproval(),
                    restaurant.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC)),
                    orderRejectedEventDomainEventPublisher);
        }
    }
}
