package com.ecommerce.menssaging.consumer;

import com.ecommerce.contracts.event.user.UserRegisteredEvent;
import com.ecommerce.menssaging.constants.RabbitConstants;
import com.ecommerce.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegisteredConsumer {
    private final CustomerService customerService;

    @RabbitListener(queues = RabbitConstants.USER_CREATED_QUEUE)
    public void consume(UserRegisteredEvent event) {
        customerService.createFromEvent(event);
    }
}
