package com.ecommerce.mapper;

import com.ecommerce.contracts.event.order.OrderCreatedEvent;
import com.ecommerce.contracts.event.order.OrderCreatedItem;
import com.ecommerce.dtos.response.OrderItemResponse;
import com.ecommerce.dtos.response.OrderResponse;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-31T14:11:48-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Ubuntu)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderResponse toOrderResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        UUID orderId = null;
        List<OrderItemResponse> items = null;
        UUID customerId = null;
        OrderStatus status = null;
        BigDecimal totalAmount = null;
        OffsetDateTime createdAt = null;

        orderId = order.getId();
        items = orderItemListToOrderItemResponseList( order.getItems() );
        customerId = order.getCustomerId();
        status = order.getStatus();
        totalAmount = order.getTotalAmount();
        createdAt = order.getCreatedAt();

        OrderResponse orderResponse = new OrderResponse( orderId, customerId, status, totalAmount, createdAt, items );

        return orderResponse;
    }

    @Override
    public OrderCreatedEvent toOrderCreatedEvent(Order order) {
        if ( order == null ) {
            return null;
        }

        UUID id = null;
        UUID customerId = null;
        BigDecimal totalAmount = null;
        List<OrderCreatedItem> items = null;

        id = order.getId();
        customerId = order.getCustomerId();
        totalAmount = order.getTotalAmount();
        items = orderItemListToOrderCreatedItemList( order.getItems() );

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent( id, customerId, totalAmount, items );

        return orderCreatedEvent;
    }

    protected OrderItemResponse orderItemToOrderItemResponse(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        UUID productId = null;
        String productName = null;
        Integer quantity = null;
        BigDecimal unitPrice = null;

        productId = orderItem.getProductId();
        productName = orderItem.getProductName();
        quantity = orderItem.getQuantity();
        unitPrice = orderItem.getUnitPrice();

        BigDecimal subtotal = null;

        OrderItemResponse orderItemResponse = new OrderItemResponse( productId, productName, quantity, unitPrice, subtotal );

        return orderItemResponse;
    }

    protected List<OrderItemResponse> orderItemListToOrderItemResponseList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemResponse> list1 = new ArrayList<OrderItemResponse>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( orderItemToOrderItemResponse( orderItem ) );
        }

        return list1;
    }

    protected OrderCreatedItem orderItemToOrderCreatedItem(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        UUID productId = null;
        Integer quantity = null;

        productId = orderItem.getProductId();
        quantity = orderItem.getQuantity();

        OrderCreatedItem orderCreatedItem = new OrderCreatedItem( productId, quantity );

        return orderCreatedItem;
    }

    protected List<OrderCreatedItem> orderItemListToOrderCreatedItemList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderCreatedItem> list1 = new ArrayList<OrderCreatedItem>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( orderItemToOrderCreatedItem( orderItem ) );
        }

        return list1;
    }
}
