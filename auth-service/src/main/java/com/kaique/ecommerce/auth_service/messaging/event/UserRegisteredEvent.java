package com.kaique.ecommerce.auth_service.messaging.event;

import com.kaique.ecommerce.auth_service.messaging.RabbitConstants;
import com.kaique.ecommerce.auth_service.messaging.annotation.EventMetadata;

import java.util.UUID;

@EventMetadata(routingKey = RabbitConstants.USER_CREATED)
public record UserRegisteredEvent(

        UUID userId,
        String email

) implements DomainEvent {
}
