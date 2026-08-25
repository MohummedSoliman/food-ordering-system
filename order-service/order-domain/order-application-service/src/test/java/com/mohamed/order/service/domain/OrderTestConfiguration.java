package com.mohamed.order.service.domain;

import com.mohamed.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher;
import com.mohamed.order.service.domain.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher;
import com.mohamed.order.service.domain.ports.output.repository.ApprovalOutboxRepository;
import com.mohamed.order.service.domain.ports.output.repository.OrderRepository;
import com.mohamed.order.service.domain.ports.output.repository.PaymentOutboxRepository;
import com.mohamed.order.service.domain.ports.output.repository.RestaurantRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.mohamed")
public class OrderTestConfiguration {

    @Bean
    public PaymentRequestMessagePublisher paymentRequestMessagePublisher() {
        return Mockito.mock(PaymentRequestMessagePublisher.class);
    }

    @Bean
    public RestaurantApprovalRequestMessagePublisher restaurantApprovalRequestMessagePublisher() {
        return Mockito.mock(RestaurantApprovalRequestMessagePublisher.class);
    }

    @Bean
    public OrderRepository orderRepository() {
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    public RestaurantRepository restaurantRepository() {
        return Mockito.mock(RestaurantRepository.class);
    }

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

    @Bean
    public ApprovalOutboxRepository approvalOutboxRepository() {
        return Mockito.mock(ApprovalOutboxRepository.class);
    }

    @Bean
    public PaymentOutboxRepository paymentOutboxRepository() {
        return Mockito.mock(PaymentOutboxRepository.class);
    }
}
