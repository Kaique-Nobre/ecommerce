package com.kaique.ecommerce.auth_service.messaging;

import com.ecommerce.contracts.event.DomainEvent;


public interface DomainEventPublisher {
    void publish(DomainEvent event);

}
