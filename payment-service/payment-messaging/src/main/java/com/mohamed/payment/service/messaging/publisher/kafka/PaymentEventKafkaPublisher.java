package com.mohamed.payment.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.mohamed.kafka.producer.KafkaMessageHelper;
import com.mohamed.kafka.producer.service.KafkaProducer;
import com.mohamed.outbox.OutboxStatus;
import com.mohamed.payment.service.domain.config.PaymentServiceConfigData;
import com.mohamed.payment.service.domain.outbox.model.OrderEventPayload;
import com.mohamed.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.mohamed.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import com.mohamed.payment.service.messaging.mapper.PaymentMessingDataMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@AllArgsConstructor
@Component
public class PaymentEventKafkaPublisher implements PaymentResponseMessagePublisher {

    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentMessingDataMapper mapper;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMessageHelper helper;

    @Override
    public void publish(OrderOutboxMessage orderOutboxMessage,
                        BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback) {

        OrderEventPayload orderEventPayload =
                helper.getOrderEventPayload(orderOutboxMessage.getPayload(), OrderEventPayload.class);

        String sagaId = orderOutboxMessage.getSagaId().toString();
        log.info("Received OrderOutboxMessage for order id: {}", orderEventPayload.getOrderId());

        try {
            PaymentResponseAvroModel paymentResponseAvroModel =
                    mapper.orderEventPayloadToPaymentResponseAvroModel(sagaId, orderEventPayload);

            kafkaProducer.send(
                    paymentServiceConfigData.getPaymentResponseTopicName(),
                    sagaId,
                    paymentResponseAvroModel,
                    helper.getKafkaCallback(
                            paymentServiceConfigData.getPaymentResponseTopicName(),
                            paymentResponseAvroModel,
                            orderOutboxMessage,
                            outboxCallback,
                            orderEventPayload.getOrderId()
                    )
            );

            log.info("PaymentResponseAvroModel sent to kafka for order id: {}", orderEventPayload.getOrderId());
        } catch (Exception e) {
            log.error("Error while sending PaymentResponseAvroModel message to kafka with order" +
                    "id: {}", orderEventPayload.getOrderId());
        }

    }
}
