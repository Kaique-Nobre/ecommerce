package com.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    private String firstName;

    private String lastName;

    private String phone;

    private LocalDate birthDate;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "update_at", nullable = false)
    private OffsetDateTime updatedAt;

    public static Customer create(UUID id, String email) {
        Customer customer = new Customer();

        customer.id = id;
        customer.email = email;
        customer.createdAt = OffsetDateTime.now();
        customer.updatedAt = OffsetDateTime.now();

        return customer;
    }

    public void updateProfile(
            String firstName,
            String lastName,
            String phone,
            LocalDate birthDate
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.birthDate = birthDate;
        this.updatedAt = OffsetDateTime.now();
    }
}
