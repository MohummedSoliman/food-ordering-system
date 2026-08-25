package com.mohamed.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohamed.order.service.domain.exception.OrderDomainException;
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
public class KafkaMessageHelper {

    private final ObjectMapper objectMapper;

    public <T, U> BiConsumer<SendResult<String, T>, Throwable> getKafkaCallback(
            String responseTopic, T requestAvroModel, U outboxMessage,
            BiConsumer<U, OutboxStatus> outboxCallback, String orderId) {

        return (result, ex) -> {
            if (ex != null) {
                log.error("Error while sending {} with message: {} and outbox type: {} to topic {}",
                        requestAvroModel, requestAvroModel,
                        outboxMessage.getClass().getName(), responseTopic, ex);
                outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
            }

            RecordMetadata metadata = result.getRecordMetadata();
            log.info("Received successful response from Kafka for order id: {}" +
                            " Topic: {} Partition: {} Offset: {} Timestamp: {}",
                    orderId,
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset(),
                    metadata.timestamp());
            outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
        };
    }

    public <T> T getOrderEventPayload(String payload, Class<T> outputType) {
        try {
            return objectMapper.readValue(payload, outputType);
        } catch (JsonProcessingException e) {
            log.error("Could not read {} object", outputType.getName());
            throw new OrderDomainException("Could not read " + outputType.getName() + "object");
        }
    }
}
