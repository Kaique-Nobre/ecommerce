package com.ecommerce.mapper;

import com.ecommerce.contracts.event.order.OrderCreatedEvent;
import com.ecommerce.dtos.response.OrderResponse;
import com.ecommerce.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper( OrderMapper.class );

    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "items", target = "items")
    OrderResponse toOrderResponse(Order order);

    OrderCreatedEvent toOrderCreatedEvent(Order order);
}
