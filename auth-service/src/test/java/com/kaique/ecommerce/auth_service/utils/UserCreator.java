package com.kaique.ecommerce.auth_service.utils;

import com.kaique.ecommerce.auth_service.entity.Role;
import com.kaique.ecommerce.auth_service.entity.RoleName;
import com.kaique.ecommerce.auth_service.entity.User;
import com.kaique.ecommerce.auth_service.security.AuthenticatedUser;
import com.kaique.ecommerce.auth_service.security.jwt.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class UserCreator {

    public static AuthenticatedUser createAuthenticatedUser() {
        Set<RoleName> roles = new HashSet<>(RoleName.CUSTOMER.ordinal());

        return new AuthenticatedUser(UUID.randomUUID(), "user@email.com", "hashed-password", true, roles);
    }

    public static User createUser() {

        Role role = new Role(1L, RoleName.CUSTOMER);

        return User.createCustomer("user@email.com", "12345678", role);
    }

    public static JwtUser createJwtUser() {
        RoleName role = RoleName.CUSTOMER;

        Set<RoleName> roles = new HashSet<>(Collections.singleton(role));

        return new JwtUser(UUID.randomUUID(), roles);
    }

    public static User createIntegrationTestUser(String password) {

        Role role = new Role(1L, RoleName.CUSTOMER);

        return User.createCustomer("user@email.com", password, role);
    }
}
