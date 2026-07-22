package com.kaique.ecommerce.auth_service.dtos;

import com.kaique.ecommerce.auth_service.entity.RoleName;

import java.util.Set;
import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        UUID userId,
        Set<RoleName> userRoles
) {
}
