package com.ecommerce.dtos.response;

import com.ecommerce.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID orderId,

        UUID customerId,

        OrderStatus status,

        BigDecimal totalAmount,

        OffsetDateTime createdAt,

        List<OrderItemResponse> items

) {
}
