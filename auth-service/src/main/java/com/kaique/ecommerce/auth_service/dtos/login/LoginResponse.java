package com.kaique.ecommerce.auth_service.dtos.login;

import com.kaique.ecommerce.auth_service.entity.RoleName;

import java.util.Set;
import java.util.UUID;

public record LoginResponse(
        String accessToken,
        UUID userId,
        Set<RoleName> userRoles
) {
}
