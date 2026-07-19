package com.kaique.ecommerce.auth_service.security.jwt;

import com.kaique.ecommerce.auth_service.entity.RoleName;

import java.util.Set;
import java.util.UUID;

public record JwtUser(
        UUID id,
        Set<RoleName> roles
) {
}
