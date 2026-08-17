package com.mohamed.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.mohamed.kafka.producer.service.KafkaProducer;
import com.mohamed.order.service.domain.config.OrderServiceConfigData;
import com.mohamed.order.service.domain.event.OrderCancelledEvent;
import com.mohamed.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.mohamed.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class CancelOrderKafkaMessagePublisher implements OrderCancelledPaymentRequestMessagePublisher {

    private final OrderMessagingDataMapper mapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderKafkaMessageHelper messageHelper;

    public CancelOrderKafkaMessagePublisher(OrderMessagingDataMapper mapper,
                                            OrderServiceConfigData orderServiceConfigData,
                                            KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer,
                                            OrderKafkaMessageHelper messageHelper) {
        this.mapper = mapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.messageHelper = messageHelper;
    }

    @Override
    public void publish(OrderCancelledEvent domainEvent) {

        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received Order Cancelled Event for Order Id: {}", orderId);
        try {
            PaymentRequestAvroModel paymentRequestAvroModel =
                    mapper.orderCanceledEventToPaymentRequestAvroModel(domainEvent);

            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    paymentRequestAvroModel,
                    messageHelper.getKafkaCallback(
                            orderServiceConfigData.getPaymentResponseTopicName(),
                            paymentRequestAvroModel,
                            orderId));

            log.info("PaymentRequestAvroModel sent to kafka  for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error While Sending PaymentRequestAvroModel message");
        }
    }
}
