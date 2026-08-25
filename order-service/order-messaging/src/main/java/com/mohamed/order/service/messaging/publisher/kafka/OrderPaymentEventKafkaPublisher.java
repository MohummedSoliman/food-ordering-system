package com.mohamed.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.mohamed.kafka.producer.KafkaMessageHelper;
import com.mohamed.kafka.producer.service.KafkaProducer;
import com.mohamed.order.service.domain.config.OrderServiceConfigData;
import com.mohamed.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.mohamed.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.mohamed.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.mohamed.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.mohamed.outbox.OutboxStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@AllArgsConstructor
@Component
public class OrderPaymentEventKafkaPublisher implements PaymentRequestMessagePublisher {

    private final OrderMessagingDataMapper mapper;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    public void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage,
                        BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback) {

        OrderPaymentEventPayload orderPaymentEventPayload =
                kafkaMessageHelper.getOrderEventPayload(
                        orderPaymentOutboxMessage.getPayload(), OrderPaymentEventPayload.class);
        String sagaId = orderPaymentOutboxMessage.getSageId().toString();
        log.info("Received OrderPaymentOutboxMessage for order id: {} and saga id: {}",
                orderPaymentEventPayload.getOrderId(), sagaId);

        try {
            PaymentRequestAvroModel paymentRequestAvroModel =
                    mapper.orderPaymentEventToPaymentRequestAvroModel(
                            sagaId, orderPaymentEventPayload);

            kafkaProducer.send(
                    orderServiceConfigData.getPaymentRequestTopicName(),
                    sagaId,
                    paymentRequestAvroModel,
                    kafkaMessageHelper.getKafkaCallback(
                            orderServiceConfigData.getPaymentRequestTopicName(),
                            paymentRequestAvroModel,
                            orderPaymentOutboxMessage,
                            outboxCallback,
                            paymentRequestAvroModel.getOrderId()
                    )
            );
        } catch (Exception e) {
            log.error("Error while sending OrderPaymentEventPayload to kafka");

        }
    }
}
