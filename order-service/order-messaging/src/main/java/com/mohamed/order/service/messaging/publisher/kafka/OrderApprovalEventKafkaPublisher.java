package com.mohamed.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.mohamed.kafka.producer.KafkaMessageHelper;
import com.mohamed.kafka.producer.service.KafkaProducer;
import com.mohamed.order.service.domain.config.OrderServiceConfigData;
import com.mohamed.order.service.domain.outbox.model.approval.OrderApprovalEventPayload;
import com.mohamed.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.mohamed.order.service.domain.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import com.mohamed.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.mohamed.outbox.OutboxStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@AllArgsConstructor
@Component
public class OrderApprovalEventKafkaPublisher implements RestaurantApprovalRequestMessagePublisher {

    private final OrderMessagingDataMapper mapper;
    private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;
    private final KafkaMessageHelper kafkaMessageHelper;
    private final OrderServiceConfigData orderServiceConfigData;

    @Override
    public void publish(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                        BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback) {

        OrderApprovalEventPayload orderEventPayload = kafkaMessageHelper.getOrderEventPayload(
                orderApprovalOutboxMessage.getPayload(),
                OrderApprovalEventPayload.class
        );

        String sagaId = orderApprovalOutboxMessage.getSageId().toString();
        log.info("Received OrderApprovalEventPayload for order id: {}, saga id: {}",
                orderEventPayload.getOrderId(),
                sagaId);

        try {
            RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel =
                    mapper.orderApprovalEventToRestaurantApprovalRequestAvroModel(sagaId, orderEventPayload);

            kafkaProducer.send(
                    orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                    sagaId,
                    restaurantApprovalRequestAvroModel,
                    kafkaMessageHelper.getKafkaCallback(
                            orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                            restaurantApprovalRequestAvroModel,
                            orderApprovalOutboxMessage,
                            outboxCallback,
                            orderEventPayload.getOrderId()
                    )
            );

            log.info("OrderApprovalEventPayload sent to kafka for order id: {}",
                    orderEventPayload.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending OrderApprovalEventPayload to kafka for order id: {}",
                    orderEventPayload.getOrderId());

        }

    }
}
