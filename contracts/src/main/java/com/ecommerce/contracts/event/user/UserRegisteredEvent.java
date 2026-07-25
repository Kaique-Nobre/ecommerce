package com.ecommerce.contracts.event.user;

import com.ecommerce.contracts.event.DomainEvent;
import com.ecommerce.contracts.event.annotation.EventMetadata;

import java.util.UUID;

@EventMetadata(eventName = "user.created")
public record UserRegisteredEvent(

        UUID userId,
        String email

) implements DomainEvent {
}
