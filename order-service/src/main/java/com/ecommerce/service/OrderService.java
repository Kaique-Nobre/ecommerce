package com.ecommerce.service;

import com.ecommerce.contracts.exception.ResourceNotFoundException;
import com.ecommerce.dtos.request.CreateOrderItemRequest;
import com.ecommerce.dtos.request.CreateOrderRequest;
import com.ecommerce.dtos.response.OrderResponse;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.exceptions.EmptyOrderException;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.menssaging.OrderEventFactory;
import com.ecommerce.menssaging.publisher.RabbitDomainEventPublisher;
import com.ecommerce.product.ProductClient;
import com.ecommerce.product.ProductSnapshot;
import com.ecommerce.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final ProductClient productClient;

    private final RabbitDomainEventPublisher eventPublisher;

    private final OrderEventFactory factory;

    private final OrderMapper mapper;

    @Transactional
    public OrderResponse create(UUID customerId, CreateOrderRequest request) {

        if (request.items().isEmpty()) {
            throw new EmptyOrderException("Order can not be empty");
        }

        Order order = Order.create(customerId);

        for (CreateOrderItemRequest requestItem : request.items()) {
            ProductSnapshot product = productClient.getProduct(requestItem.productId());

            if (product == null) {
                throw new ResourceNotFoundException("Product was not found");
            }

            OrderItem item = OrderItem.create(
                    product.productId(),
                    product.name(),
                    product.price(),
                    requestItem.quantity()
            );

            order.addItem(item);
        }

        Order savedOrder = orderRepository.save(order);

        eventPublisher.publish(factory.createOrderCreatedEvent(savedOrder));

        return mapper.toOrderResponse(savedOrder);
    }
}
