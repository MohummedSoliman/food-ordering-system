package com.mohamed.order.service.dataaccess.order.mapper;

import com.mohamed.order.service.dataaccess.order.entity.OrderAddressEntity;
import com.mohamed.order.service.dataaccess.order.entity.OrderEntity;
import com.mohamed.order.service.dataaccess.order.entity.OrderItemEntity;
import com.mohamed.order.service.domain.entity.Order;
import com.mohamed.order.service.domain.entity.OrderItem;
import com.mohamed.order.service.domain.entity.Product;
import com.mohamed.order.service.domain.valueobject.OrderItemId;
import com.mohamed.order.service.domain.valueobject.StreetAddress;
import com.mohamed.order.service.domain.valueobject.TracingId;
import com.mohamed.valueobject.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDataAccessMapper {

    public OrderEntity orderToOrderEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .restaurantId(order.getRestaurantId().getValue())
                .trackingId(order.getTracingId().getValue())
                .address(deliveryAddressToAddressEntity(order.getDeliveryAddress()))
                .orderStatus(order.getOrderStatus())
                .price(order.getPrice().getAmount())
                .items(orderItemsToOrderItemsEntity(order.getItems()))
                .failureMessages(failureMessagesToString(order.getFailureMessages()))
                .build();

        orderEntity.getAddress().setOrder(orderEntity);
        orderEntity.getItems()
                .forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        return Order.Builder.builder()
                .orderId(new OrderId(orderEntity.getId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
                .tracingId(new TracingId(orderEntity.getTrackingId()))
                .orderStatus(orderEntity.getOrderStatus())
                .price(new Money(orderEntity.getPrice()))
                .deliveryAddress(addressEntityToDeliveryAddress(orderEntity.getAddress()))
                .items(orderItemEntityToOrderItems(orderEntity.getItems()))
                .failureMessages(failureMessageStringToList(orderEntity)
                )
                .build();
    }

    private ArrayList<String> failureMessageStringToList(OrderEntity orderEntity) {
        return orderEntity.getFailureMessages().isEmpty() ?
                new ArrayList<>() :
                new ArrayList<>
                        (Arrays.asList(
                                orderEntity.getFailureMessages()
                                        .split(Order.FAILURE_MESSAGES_DELIMITER)));
    }

    private List<OrderItem> orderItemEntityToOrderItems(List<OrderItemEntity> items) {
        return items.stream()
                .map(item ->
                        OrderItem.Builder
                                .builder()
                                .orderItemId(new OrderItemId(item.getId()))
                                .price(new Money(item.getPrice()))
                                .quantity(item.getQuantity())
                                .subtotal(new Money(item.getSubTotal()))
                                .product(new Product(new ProductId(item.getProductId())))
                                .build()
                )
                .collect(Collectors.toList());
    }

    private StreetAddress addressEntityToDeliveryAddress(OrderAddressEntity address) {
        return new StreetAddress(address.getId(), address.getStreet(),
                address.getPostalCode(), address.getCity());
    }

    private String failureMessagesToString(List<String> failureMessages) {
        if (failureMessages == null || failureMessages.isEmpty()) {
            return "";
        }
        return failureMessages.stream()
                .filter(message -> !message.isEmpty() || !message.isBlank())
                .collect(Collectors.joining(Order.FAILURE_MESSAGES_DELIMITER));
    }

    private List<OrderItemEntity> orderItemsToOrderItemsEntity(List<OrderItem> items) {
        return items.stream()
                .map(item ->
                        OrderItemEntity.builder()
                                .id(item.getId().getValue())
                                .productId(item.getProduct().getId().getValue())
                                .price(item.getPrice().getAmount())
                                .quantity(item.getQuantity())
                                .subTotal(item.getSubtotal().getAmount())
                                .build()
                )
                .collect(Collectors.toList());
    }

    private OrderAddressEntity deliveryAddressToAddressEntity(StreetAddress deliveryAddress) {
        return OrderAddressEntity.builder()
                .id(deliveryAddress.getId())
                .city(deliveryAddress.getCity())
                .street(deliveryAddress.getStreet())
                .postalCode(deliveryAddress.getPostalCode())
                .build();
    }
}
