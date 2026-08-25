package com.mohamed.order.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.mohamed.kafka.consumer.KafkaConsumer;
import com.mohamed.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalMessageResponseListener;
import com.mohamed.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RestaurantApprovalResponseKafkaTopic implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {

    private final RestaurantApprovalMessageResponseListener responseListener;
    private final OrderMessagingDataMapper mapper;

    public RestaurantApprovalResponseKafkaTopic(RestaurantApprovalMessageResponseListener responseListener,
                                                OrderMessagingDataMapper mapper) {
        this.responseListener = responseListener;
        this.mapper = mapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${order-service.restaurant-approval-response-topic-name}")
    public void receive(@Payload List<RestaurantApprovalResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment responses received with key: {}, partitions: {} and offset: {}",
                messages.size(), keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(restaurantApprovalResponseAvroModel -> {
            try {
                if (OrderApprovalStatus.APPROVED == restaurantApprovalResponseAvroModel.getOrderApprovalStatus()) {
                    log.info("Processing approved order for order id: {}", restaurantApprovalResponseAvroModel.getOrderId());
                    responseListener.orderApproved(
                            mapper.restaurantApprovalResponseArvoToRestaurantApprovalResponse(restaurantApprovalResponseAvroModel)
                    );
                } else if (OrderApprovalStatus.REJECTED == restaurantApprovalResponseAvroModel.getOrderApprovalStatus()) {
                    log.info("Processing rejected order  for order id: {}", restaurantApprovalResponseAvroModel.getOrderId());
                    responseListener.orderRejected(
                            mapper.restaurantApprovalResponseArvoToRestaurantApprovalResponse(restaurantApprovalResponseAvroModel)
                    );
                }
            } catch (Exception e) {
                log.error("Caught Optimistic locking exception in RestaurantApprovalResponseKafkaTopic " +
                        "for order id: {}", restaurantApprovalResponseAvroModel.getOrderId());
            }
        });
    }
}
