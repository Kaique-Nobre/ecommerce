package com.ecommerce.contracts.event.order;

import java.util.UUID;

public record OrderCreatedItem(

        UUID productId,

        Integer quantity

) {
}
