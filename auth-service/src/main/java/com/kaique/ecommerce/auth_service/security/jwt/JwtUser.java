package com.kaique.ecommerce.auth_service.security.jwt;

import com.kaique.ecommerce.auth_service.entity.Role;
import com.kaique.ecommerce.auth_service.entity.RoleName;
import com.kaique.ecommerce.auth_service.entity.User;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record JwtUser(
        UUID id,
        Set<RoleName> roles
) {
    public static JwtUser from(User user) {

        return new JwtUser(

                user.getId(),

                user.getRoles()
                        .stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())

        );

    }
}
