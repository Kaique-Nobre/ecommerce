package com.ecommerce.entity;

import com.ecommerce.exceptions.InvalidQuantityException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Integer quantity;

    public static OrderItem create(
            UUID productId,
            String productName,
            BigDecimal unitPrice,
            Integer quantity
    ) {

        if (quantity <= 0) {
            throw new InvalidQuantityException("quantity must be greater than 0");
        }

        OrderItem item = new OrderItem();

        item.id = UUID.randomUUID();

        item.productId = productId;

        item.productName = productName;

        item.unitPrice = unitPrice;

        item.quantity = quantity;

        return item;
    }

    public BigDecimal calculateSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void increaseQuantity(Integer quantity) {
        this.quantity += quantity;
    }

    public void assignOrder(Order order) {
        this.order = order;
    }
}
