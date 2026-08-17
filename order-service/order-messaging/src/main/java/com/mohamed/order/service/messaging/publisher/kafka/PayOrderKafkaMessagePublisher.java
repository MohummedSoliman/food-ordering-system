package com.mohamed.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.mohamed.kafka.producer.service.KafkaProducer;
import com.mohamed.order.service.domain.config.OrderServiceConfigData;
import com.mohamed.order.service.domain.event.OrderPaidEvent;
import com.mohamed.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.mohamed.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {

    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
    private final OrderMessagingDataMapper mapper;
    private final OrderKafkaMessageHelper helper;

    public PayOrderKafkaMessagePublisher(OrderServiceConfigData orderServiceConfigData,
                                         KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer,
                                         OrderMessagingDataMapper mapper,
                                         OrderKafkaMessageHelper helper) {
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.mapper = mapper;
        this.helper = helper;
    }

    @Override
    public void publish(OrderPaidEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();

        try {
            RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel =
                    mapper.orderPaidEventToRestaurantApprovalRequestAvroModel(domainEvent);

            kafkaProducer.send(orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                    orderId,
                    restaurantApprovalRequestAvroModel,
                    helper.getKafkaCallback(
                            orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                            restaurantApprovalRequestAvroModel,
                            orderId));

            log.info("RestaurantApprovalRequestAvroModel sent to kafka with order ID: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending RestaurantApprovalRequestAvroModel message to kafka" +
                    "with order id :{}, error: {}", orderId, e.getMessage());
        }
    }
}
