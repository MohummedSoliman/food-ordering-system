package com.mohamed.restaurant.service.dataaccess.restaurant.outbox.adapter;

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.mohamed.kafka.producer.service.KafkaProducer;
import com.mohamed.outbox.OutboxStatus;
import com.mohamed.restaurant.service.dataaccess.restaurant.outbox.entity.OrderOutboxEntity;
import com.mohamed.restaurant.service.dataaccess.restaurant.outbox.exception.OrderOutboxNotFoundException;
import com.mohamed.restaurant.service.dataaccess.restaurant.outbox.mapper.OrderOutboxDataAccessMapper;
import com.mohamed.restaurant.service.dataaccess.restaurant.outbox.repository.OrderOutboxJpaRepository;
import com.mohamed.restaurant.service.domain.outbox.model.OrderOutboxMessage;
import com.mohamed.restaurant.service.domain.ports.output.repository.OrderOutboxRepository;
import com.mohamed.valueobject.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

    private final OrderOutboxDataAccessMapper mapper;
    private final KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer;
    private final OrderOutboxJpaRepository orderOutboxJpaRepository;

    @Override
    public OrderOutboxMessage save(OrderOutboxMessage outboxMessage) {
        OrderOutboxEntity orderOutboxEntity = mapper.orderOutboxMessageToOutboxEntity(outboxMessage);
        return mapper.orderOutboxEntityToOrderOutboxMessage(
                orderOutboxJpaRepository.save(orderOutboxEntity)
        );
    }

    @Override
    public Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {

        return Optional.of(orderOutboxJpaRepository.findByTypeAndOutboxStatus(type, outboxStatus)
                .orElseThrow(() ->
                        new OrderOutboxNotFoundException("Approval outbox object can't be found"))
                .stream()
                .map(mapper::orderOutboxEntityToOrderOutboxMessage)
                .toList());
    }

    @Override
    public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndOutboxStatus(String type, UUID sagaId, OutboxStatus outboxStatus) {
        return orderOutboxJpaRepository.findByTypeAndSagaIdAndOutboxStatus(type, sagaId, outboxStatus)
                .map(mapper::orderOutboxEntityToOrderOutboxMessage);
    }


    @Override
    public void deleteByTypeAndOutboxStatus(String type, OutboxStatus outboxStatus) {
        orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(type, outboxStatus);
    }
}
