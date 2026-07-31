package com.ecommerce.product;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.UUID;

@Configuration
public class ProductConfig {

    @Bean
    public ProductClient productClient() {
        return new ProductClient() {
            @Override
            public ProductSnapshot getProduct(UUID productId) {
                return new ProductSnapshot(productId, "product", new BigDecimal(5000));
            }
        };
    }
}
