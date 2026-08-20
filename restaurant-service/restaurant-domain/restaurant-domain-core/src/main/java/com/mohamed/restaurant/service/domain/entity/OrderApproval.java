package com.mohamed.restaurant.service.domain.entity;

import com.mohamed.entity.BaseEntity;
import com.mohamed.restaurant.service.domain.valueobject.OrderApprovalId;
import com.mohamed.valueobject.OrderApprovalStatus;
import com.mohamed.valueobject.OrderId;
import com.mohamed.valueobject.RestaurantId;

public class OrderApproval extends BaseEntity<OrderApprovalId> {

    private final OrderId orderId;
    private final RestaurantId restaurantId;
    private final OrderApprovalStatus orderApprovalStatus;

    private OrderApproval(Builder builder) {
        setId(builder.orderApprovalId);
        orderId = builder.orderId;
        restaurantId = builder.restaurantId;
        orderApprovalStatus = builder.orderApprovalStatus;
    }


    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public OrderApprovalStatus getOrderApprovalStatus() {
        return orderApprovalStatus;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public static final class Builder {
        private OrderApprovalId orderApprovalId;
        private OrderId orderId;
        private RestaurantId restaurantId;
        private OrderApprovalStatus orderApprovalStatus;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder orderApprovalId(OrderApprovalId val) {
            orderApprovalId = val;
            return this;
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder orderApprovalStatus(OrderApprovalStatus val) {
            orderApprovalStatus = val;
            return this;
        }

        public OrderApproval build() {
            return new OrderApproval(this);
        }
    }
}
