package com.mohamed.restaurant.service.dataaccess.restaurant.adapter;

import com.mohamed.dataaccess.restaurant.entity.RestaurantEntity;
import com.mohamed.dataaccess.restaurant.repository.RestaurantJpaRepository;
import com.mohamed.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.mohamed.restaurant.service.domain.entity.Restaurant;
import com.mohamed.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantDataAccessMapper mapper;

    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {

        List<UUID> restaurantProducts =
                mapper.restaurantToRestaurantProducts(restaurant);
        Optional<List<RestaurantEntity>> restaurantEntities = restaurantJpaRepository.findByRestaurantIdAndProductIdIn(
                restaurant.getId().getValue(), restaurantProducts
        );

        return restaurantEntities.map(mapper::restaurantEntityToRestaurant);
    }
}
