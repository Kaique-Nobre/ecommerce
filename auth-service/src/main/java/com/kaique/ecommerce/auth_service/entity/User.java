package com.kaique.ecommerce.auth_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public static User createCustomer(
            String email,
            String passwordHash,
            Role role
    ) {

        User user = new User();

        user.id = UUID.randomUUID();

        user.email = email;

        user.passwordHash = passwordHash;

        user.enabled = true;

        user.createdAt = OffsetDateTime.now();

        user.updatedAt = OffsetDateTime.now();

        user.roles.add(role);

        return user;
    }

    public void disableUser(User user) {
        user.enabled = false;
    }
}
