package com.ecommerce.product;

import java.util.UUID;

public interface ProductClient {

    ProductSnapshot getProduct(UUID productId);
}
