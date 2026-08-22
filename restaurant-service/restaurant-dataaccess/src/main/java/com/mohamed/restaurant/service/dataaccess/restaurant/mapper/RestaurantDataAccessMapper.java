package com.mohamed.restaurant.service.dataaccess.restaurant.mapper;

import com.mohamed.dataaccess.restaurant.entity.RestaurantEntity;
import com.mohamed.restaurant.service.dataaccess.restaurant.entity.OrderApprovalEntity;
import com.mohamed.restaurant.service.domain.entity.OrderApproval;
import com.mohamed.restaurant.service.domain.entity.OrderDetail;
import com.mohamed.restaurant.service.domain.entity.Product;
import com.mohamed.restaurant.service.domain.entity.Restaurant;
import com.mohamed.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.mohamed.restaurant.service.domain.valueobject.OrderApprovalId;
import com.mohamed.valueobject.Money;
import com.mohamed.valueobject.OrderId;
import com.mohamed.valueobject.ProductId;
import com.mohamed.valueobject.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {

    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getOrderDetail().getProducts()
                .stream()
                .map(product -> product.getId().getValue())
                .collect(Collectors.toList());
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity = restaurantEntities.stream().findFirst().orElseThrow(
                () -> new RestaurantNotFoundException("No Restaurant Found")
        );

        List<Product> products = restaurantEntities.stream().map(entity ->
                        Product.Builder.builder()
                                .productId(new ProductId(entity.getProductId()))
                                .name(entity.getProductName())
                                .price(new Money(entity.getProductPrice()))
                                .available(entity.getProductAvailable())
                                .build())
                .toList();

        return Restaurant.Builder.builder()
                .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
                .orderDetail(OrderDetail.Builder.builder()
                        .products(products)
                        .build())
                .active(restaurantEntity.isRestaurantActive())
                .build();
    }

    public OrderApprovalEntity orderApprovalToOrderApprovalEntity(OrderApproval orderApproval) {
        return OrderApprovalEntity.builder()
                .id(orderApproval.getId().getValue())
                .restaurantId(orderApproval.getRestaurantId().getValue())
                .orderId(orderApproval.getOrderId().getValue())
                .orderApprovalStatus(orderApproval.getOrderApprovalStatus())
                .build();
    }

    public OrderApproval orderApprovalEntityToOrderApproval(OrderApprovalEntity orderApprovalEntity) {
        return OrderApproval.Builder.builder()
                .orderApprovalId(new OrderApprovalId(orderApprovalEntity.getId()))
                .restaurantId(new RestaurantId(orderApprovalEntity.getRestaurantId()))
                .orderId(new OrderId(orderApprovalEntity.getOrderId()))
                .orderApprovalStatus(orderApprovalEntity.getOrderApprovalStatus())
                .build();
    }
}
