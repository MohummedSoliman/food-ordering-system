package com.mohamed.restaurant.service.domain.mapper;

import com.mohamed.restaurant.service.domain.dto.RestaurantApprovalReqeust;
import com.mohamed.restaurant.service.domain.entity.OrderDetail;
import com.mohamed.restaurant.service.domain.entity.Product;
import com.mohamed.restaurant.service.domain.entity.Restaurant;
import com.mohamed.restaurant.service.domain.event.OrderApprovalEvent;
import com.mohamed.restaurant.service.domain.outbox.model.OrderEventPayload;
import com.mohamed.valueobject.Money;
import com.mohamed.valueobject.OrderId;
import com.mohamed.valueobject.OrderStatus;
import com.mohamed.valueobject.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataMapper {
    public Restaurant restaurantApprovalReqeustToRestaurant(
            RestaurantApprovalReqeust restaurantApprovalReqeust) {
        return Restaurant.Builder.builder()
                .restaurantId(new RestaurantId(UUID.fromString(restaurantApprovalReqeust.getRestaurantId())))
                .orderDetail(OrderDetail.Builder.builder()
                        .orderId(new OrderId(UUID.fromString(restaurantApprovalReqeust.getOrderId())))
                        .products(restaurantApprovalReqeust.getProducts().stream()
                                .map(product ->
                                        Product.Builder.builder()
                                                .productId(product.getId())
                                                .quantity(product.getQuantity())
                                                .build())
                                .collect(Collectors.toList()))
                        .totalAmount(new Money(restaurantApprovalReqeust.getPrice()))
                        .orderStatus(OrderStatus.valueOf(restaurantApprovalReqeust.getRestaurantOrderStatus().name()))
                        .build())
                .build();
    }

    public OrderEventPayload
    orderApprovalEventToOrderEventPayload(OrderApprovalEvent orderApprovalEvent) {
        return OrderEventPayload.builder()
                .orderId(orderApprovalEvent.getOrderApproval().getOrderId().getValue().toString())
                .restaurantId(orderApprovalEvent.getRestaurantId().getValue().toString())
                .orderApprovalStatus(orderApprovalEvent.getOrderApproval().getOrderApprovalStatus().name())
                .createdAt(orderApprovalEvent.getCreatedAt())
                .failureMessage(orderApprovalEvent.getFailureMessages())
                .build();
    }
}
