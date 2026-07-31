package com.ecommerce.dtos.request;

import java.util.UUID;

public record CreateOrderItemRequest(
        UUID productId,

        Integer quantity
) {
}
