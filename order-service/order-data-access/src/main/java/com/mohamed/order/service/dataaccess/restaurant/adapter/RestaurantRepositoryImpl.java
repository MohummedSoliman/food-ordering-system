package com.mohamed.order.service.dataaccess.restaurant.adapter;

import com.mohamed.dataaccess.restaurant.repository.RestaurantJpaRepository;
import com.mohamed.order.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.mohamed.order.service.domain.entity.Restaurant;
import com.mohamed.order.service.domain.ports.output.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantDataAccessMapper mapper;

    public RestaurantRepositoryImpl(RestaurantJpaRepository restaurantJpaRepository,
                                    RestaurantDataAccessMapper mapper) {
        this.restaurantJpaRepository = restaurantJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
        List<UUID> restaurantProducts = mapper.restaurantToRestaurantProducts(restaurant);
        return this.restaurantJpaRepository
                .findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(), restaurantProducts)
                .map(mapper::restaurantEntityToRestaurant);
    }
}
