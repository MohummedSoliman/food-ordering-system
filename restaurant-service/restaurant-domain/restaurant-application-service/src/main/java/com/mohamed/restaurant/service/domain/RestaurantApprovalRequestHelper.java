package com.mohamed.restaurant.service.domain;

import com.mohamed.outbox.OutboxStatus;
import com.mohamed.restaurant.service.domain.dto.RestaurantApprovalReqeust;
import com.mohamed.restaurant.service.domain.entity.Restaurant;
import com.mohamed.restaurant.service.domain.event.OrderApprovalEvent;
import com.mohamed.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.mohamed.restaurant.service.domain.mapper.RestaurantDataMapper;
import com.mohamed.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.mohamed.restaurant.service.domain.outbox.scheduler.OrderOutboxHelper;
import com.mohamed.restaurant.service.domain.ports.output.message.publisher.RestaurantApprovalResponseMessagePublisher;
import com.mohamed.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import com.mohamed.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import com.mohamed.valueobject.RestaurantId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class RestaurantApprovalRequestHelper {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantDataMapper mapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final OrderOutboxHelper helper;
    private final RestaurantApprovalResponseMessagePublisher publisher;

    @Transactional
    public void persistOrderApproval(RestaurantApprovalReqeust restaurantApprovalReqeust) {

        if (publishIfOutboxMessageProcessed(restaurantApprovalReqeust)) {
            log.info("An outbox message with saga id: {} already saved to database!",
                    restaurantApprovalReqeust.getSagaId());
            return;
        }

        log.info("Processing restaurant approval for order id: {} ", restaurantApprovalReqeust.getOrderId());
        List<String> failureMessages = new ArrayList<>();
        Restaurant restaurant = findRestaurant(restaurantApprovalReqeust);
        OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(
                restaurant, failureMessages
        );
        orderApprovalRepository.save(restaurant.getOrderApproval());

        helper
                .saveOrderOutboxMessage(mapper.orderApprovalEventToOrderEventPayload(orderApprovalEvent),
                        orderApprovalEvent.getOrderApproval().getOrderApprovalStatus(),
                        OutboxStatus.STARTED,
                        UUID.fromString(restaurantApprovalReqeust.getSagaId()));

    }

    private Restaurant findRestaurant(RestaurantApprovalReqeust restaurantApprovalReqeust) {
        Restaurant restaurant =
                mapper.restaurantApprovalReqeustToRestaurant(restaurantApprovalReqeust);
        Optional<Restaurant> restaurantResult = restaurantRepository.findRestaurantInformation(restaurant);
        if (restaurantResult.isEmpty()) {
            log.error("Restaurant with id :{} not found", restaurant.getId().getValue());
            throw new RestaurantNotFoundException("Restaurant with id : " + restaurant.getId().getValue()
                    + "not found");
        }
        Restaurant restaurantEntity = restaurantResult.get();
        restaurant.setActive(restaurantEntity.isActive());
        restaurant.getOrderDetail().getProducts().forEach(product -> {
            restaurantEntity.getOrderDetail().getProducts().forEach(p -> {
                if (p.getId().equals(product.getId())) {
                    product.updateWithConfirmedNamePriceAndAvailability(p.getName(), p.getPrice(), p.isAvailable());
                }
            });
        });
        restaurant.setId(new RestaurantId(UUID.fromString(restaurantApprovalReqeust.getOrderId())));
        return restaurant;
    }

    private boolean publishIfOutboxMessageProcessed(RestaurantApprovalReqeust restaurantApprovalRequest) {
        Optional<OrderOutboxMessage> orderOutboxMessage =
                helper.getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(
                        UUID.fromString(restaurantApprovalRequest.getSagaId()),
                        OutboxStatus.COMPLETED
                );

        if (orderOutboxMessage.isPresent()) {
            publisher.publish(orderOutboxMessage.get(), helper::updateOutboxStatus);
            return true;
        }
        return false;
    }
}
