package com.mohamed.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.mohamed.kafka.producer.service.KafkaProducer;
import com.mohamed.order.service.domain.config.OrderServiceConfigData;
import com.mohamed.order.service.domain.event.OrderCreatedEvent;
import com.mohamed.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessage;
import com.mohamed.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.mohamed.kafka.producer.KafkaMessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderKafkaMessagePublisher implements OrderCreatedPaymentRequestMessage {

    private final OrderMessagingDataMapper mapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final KafkaMessageHelper messageHelper;

    public CreateOrderKafkaMessagePublisher(OrderMessagingDataMapper mapper,
                                            OrderServiceConfigData orderServiceConfigData,
                                            KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer,
                                            KafkaMessageHelper messageHelper) {
        this.mapper = mapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.messageHelper = messageHelper;
    }

    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received Order Created Event for Order Id: {}", orderId);
        try {
            PaymentRequestAvroModel paymentRequestAvroModel = mapper.orderCreatedEventToPaymentRequestAvroModel(domainEvent);

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
