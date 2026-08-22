package com.mohamed.restaurant.service.dataaccess.restaurant.adapter;

import com.mohamed.restaurant.service.dataaccess.restaurant.entity.OrderApprovalEntity;
import com.mohamed.restaurant.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.mohamed.restaurant.service.dataaccess.restaurant.repository.OrderApprovalJpaRepository;
import com.mohamed.restaurant.service.domain.entity.OrderApproval;
import com.mohamed.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderApprovalRepositoryImpl implements OrderApprovalRepository {

    private final OrderApprovalJpaRepository orderApprovalJpaRepository;
    private final RestaurantDataAccessMapper mapper;

    @Override
    public OrderApproval save(OrderApproval orderApproval) {
        OrderApprovalEntity orderApprovalEntity = mapper.orderApprovalToOrderApprovalEntity(orderApproval);
        return mapper.orderApprovalEntityToOrderApproval(orderApprovalJpaRepository.save(orderApprovalEntity));
    }
}
