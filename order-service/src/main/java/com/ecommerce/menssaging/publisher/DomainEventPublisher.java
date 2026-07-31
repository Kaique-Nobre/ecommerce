package com.ecommerce.menssaging.publisher;

import com.ecommerce.contracts.event.DomainEvent;

public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
