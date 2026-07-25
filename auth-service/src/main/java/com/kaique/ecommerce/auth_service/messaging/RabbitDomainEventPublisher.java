package com.kaique.ecommerce.auth_service.messaging;

import com.ecommerce.contracts.event.DomainEvent;
import com.ecommerce.contracts.event.annotation.EventMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitDomainEventPublisher implements DomainEventPublisher{

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(DomainEvent event) {
        EventMetadata metadata = event.getClass().getAnnotation(EventMetadata.class);

        if (metadata == null) {
            throw new IllegalArgumentException(
                    "Event %s must be annotated with @EventMetadata"
                            .formatted(event.getClass().getSimpleName())
            );
        }

        rabbitTemplate.convertAndSend(
                RabbitConstants.ECOMMERCE_EXCHANGE,
                metadata.eventName(),
                event
        );
    }
}
