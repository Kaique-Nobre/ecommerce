package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "orders")
public class Order {

    @Id
    UUID id;

    @Column(name = "customer_id", nullable = false)
    UUID customerId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public static Order create(UUID customerId) {
        Order order = new Order();

        order.id = UUID.randomUUID();

        order.customerId = customerId;

        order.status = OrderStatus.CREATED;

        order.createdAt = OffsetDateTime.now();

        order.updatedAt = OffsetDateTime.now();

        order.totalAmount = BigDecimal.ZERO;

        return order;
    }

    private static BigDecimal calculateTotalAmount(List<OrderItem> orderItems) {
        return orderItems.stream().map(OrderItem::calculateSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void recalculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(OrderItem::calculateSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addItem(OrderItem item) {
        OrderItem existingItem =
                items.stream()
                        .filter(i -> i.getProductId().equals(item.getProductId()))
                        .findFirst()
                        .orElse(null);

        if(existingItem != null) {
            existingItem.increaseQuantity(item.getQuantity());
        } else {
            item.assignOrder(this);
            items.add(item);
        }
        recalculateTotalAmount();
        this.updatedAt = OffsetDateTime.now();
    }

    public void markAsPaid() {
        this.status = OrderStatus.PAID;
        this.updatedAt = OffsetDateTime.now();
    }

    public void markAsShipped() {
        this.status = OrderStatus.SHIPPED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void markAsCancelled() {
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void markAsReturned() {
        this.status = OrderStatus.RETURNED;
        this.updatedAt = OffsetDateTime.now();
    }
}
