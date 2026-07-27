package com.ecommerce.service;

import com.ecommerce.contracts.event.user.UserRegisteredEvent;
import com.ecommerce.entity.Customer;
import com.ecommerce.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public void createFromEvent(UserRegisteredEvent event) {
        if(customerRepository.existsByEmail(event.email())) {
            return;
        }

        Customer customer = Customer.create(
                event.userId(),
                event.email()
        );

        customerRepository.save(customer);
    }
}
