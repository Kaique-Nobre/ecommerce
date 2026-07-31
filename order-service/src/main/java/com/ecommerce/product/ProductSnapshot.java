package com.ecommerce.product;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSnapshot(

        UUID productId,

        String name,

        BigDecimal price
) {
}
