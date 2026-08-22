package com.mohamed.order.service.dataaccess.restaurant.mapper;

import com.mohamed.dataaccess.restaurant.entity.RestaurantEntity;
import com.mohamed.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.mohamed.order.service.domain.entity.Product;
import com.mohamed.order.service.domain.entity.Restaurant;
import com.mohamed.valueobject.Money;
import com.mohamed.valueobject.ProductId;
import com.mohamed.valueobject.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {


    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getProducts().stream()
                .map(product -> product.getId().getValue())
                .collect(Collectors.toList());
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {
        RestaurantEntity restaurantEntity = restaurantEntities.stream().findFirst()
                .orElseThrow(() -> new RestaurantDataAccessException("Restaurant Could not be found!"));

        List<Product> products = restaurantEntities.stream()
                .map(entity ->
                        new Product(new ProductId(entity.getProductId()),
                                entity.getProductName(), new Money(entity.getProductPrice())
                        )).toList();

        return Restaurant.Builder.builder()
                .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
                .products(products)
                .active(restaurantEntity.isRestaurantActive())
                .build();
    }
}
