package com.kaique.ecommerce.auth_service.messaging;

import com.kaique.ecommerce.auth_service.messaging.event.DomainEvent;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
