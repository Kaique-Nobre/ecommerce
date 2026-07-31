package com.ecommerce.contracts.event.order;

import com.ecommerce.contracts.event.DomainEvent;
import com.ecommerce.contracts.event.annotation.EventMetadata;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@EventMetadata(eventName = "order.created")
public record OrderCreatedEvent(

        UUID id,
        UUID customerId,
        BigDecimal totalAmount,
        List<OrderCreatedItem> items

) implements DomainEvent {
}
