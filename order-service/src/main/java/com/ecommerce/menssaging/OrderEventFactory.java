package com.ecommerce.menssaging;

import com.ecommerce.contracts.event.order.OrderCreatedEvent;
import com.ecommerce.entity.Order;
import com.ecommerce.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventFactory {

    private final OrderMapper mapper;

    public OrderCreatedEvent createOrderCreatedEvent(Order order) {
        return mapper.toOrderCreatedEvent(order);
    }
}
