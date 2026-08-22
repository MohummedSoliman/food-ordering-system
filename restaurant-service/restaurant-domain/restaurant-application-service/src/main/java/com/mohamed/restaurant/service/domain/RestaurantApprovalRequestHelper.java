package com.mohamed.restaurant.service.domain;

import com.mohamed.restaurant.service.domain.dto.RestaurantApprovalReqeust;
import com.mohamed.restaurant.service.domain.entity.Restaurant;
import com.mohamed.restaurant.service.domain.event.OrderApprovalEvent;
import com.mohamed.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.mohamed.restaurant.service.domain.mapper.RestaurantDataMapper;
import com.mohamed.restaurant.service.domain.ports.output.message.publisher.OrderApprovedMessagePublisher;
import com.mohamed.restaurant.service.domain.ports.output.message.publisher.OrderRejectedMessagePublisher;
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
    private final OrderApprovedMessagePublisher orderApprovedMessagePublisher;
    private final OrderRejectedMessagePublisher orderRejectedMessagePublisher;

    @Transactional
    public OrderApprovalEvent persistOrderApproval(RestaurantApprovalReqeust restaurantApprovalReqeust) {
        log.info("Processing restaurant approval for order id: {} ", restaurantApprovalReqeust.getOrderId());
        List<String> failureMessages = new ArrayList<>();
        Restaurant restaurant = findRestaurant(restaurantApprovalReqeust);
        OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(
                restaurant, failureMessages,
                orderApprovedMessagePublisher, orderRejectedMessagePublisher
        );
        orderApprovalRepository.save(restaurant.getOrderApproval());
        return orderApprovalEvent;
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
}
