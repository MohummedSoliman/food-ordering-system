package com.mohamed.customer.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.CustomerAvroModel;
import com.mohamed.customer.service.domain.config.CustomerServiceConfigData;
import com.mohamed.customer.service.domain.event.CustomerCreatedEvent;
import com.mohamed.customer.service.domain.ports.output.message.publisher.CustomerMessagePublisher;
import com.mohamed.customer.service.messaging.mapper.CustomerMessagingDataMapper;
import com.mohamed.kafka.producer.service.KafkaProducer;
import com.mohamed.outbox.OutboxStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@AllArgsConstructor
@Component
public class CustomerCreateEventKafkaPublisher implements CustomerMessagePublisher {

    private final KafkaProducer<String, CustomerAvroModel> kafkaProducer;
    private final CustomerMessagingDataMapper mapper;
    private final CustomerServiceConfigData customerServiceConfigData;

    @Override
    public void publish(CustomerCreatedEvent customerCreatedEvent) {
        log.info("Received CustomerCreatedEvent for customer id: {}",
                customerCreatedEvent.getCustomer().getId().getValue());

        try {
            CustomerAvroModel customerAvroModel =
                    mapper.paymentResponseAvroModelToPaymentResponse(customerCreatedEvent);

            kafkaProducer.send(
                    customerServiceConfigData.getCustomerTopicName(),
                    customerAvroModel.getId(),
                    customerAvroModel,
                    getKafkaCallback(
                            customerServiceConfigData.getCustomerTopicName(),
                            customerAvroModel
                    )
            );
            log.info("CustomerCreatedEvent sent to kafka for customer id: {}"
                    , customerAvroModel.getId());
        } catch (Exception e) {
            log.error("Error while sending CustomerCreatedEvent to kafka for customer id: {}"
                    , customerCreatedEvent.getCustomer().getId());
        }
    }

    public BiConsumer<SendResult<String, CustomerAvroModel>, Throwable> getKafkaCallback(
            String topicName, CustomerAvroModel message) {
        return (result, ex) -> {
            if (ex != null) {
                log.error("Error while sending message {} to topic {}", message.toString(), topicName, ex);
                return;
            }
            RecordMetadata metadata = result.getRecordMetadata();
            log.info("Received successful response from Kafka " +
                            " Topic: {} Partition: {} Offset: {} Timestamp: {}",
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset(),
                    metadata.timestamp());
        };
    }
}
