package com.mohamed.order.service.domain;

import com.mohamed.order.service.domain.dto.message.CustomerModel;
import com.mohamed.order.service.domain.entity.Customer;
import com.mohamed.order.service.domain.exception.OrderDomainException;
import com.mohamed.order.service.domain.mapper.OrderDataMapper;
import com.mohamed.order.service.domain.ports.input.customer.CustomerMessageListener;
import com.mohamed.order.service.domain.ports.output.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class CustomerMessageListenerImpl implements CustomerMessageListener {

    private final CustomerRepository customerRepository;
    private final OrderDataMapper mapper;

    @Override
    public void customerCreated(CustomerModel customerModel) {
        Customer customer = mapper.customerModelToCustomer(customerModel);
        Customer saveeCustomer = customerRepository.save(customer);
        if (saveeCustomer == null) {
            log.error("customer could not be created in order DB with id: {}", customerModel.getId());
            throw new OrderDomainException("customer could not be created in order DB with id " +
                    customerModel.getId());
        }
        log.info("Customer created successfully in order DB");
    }
}
