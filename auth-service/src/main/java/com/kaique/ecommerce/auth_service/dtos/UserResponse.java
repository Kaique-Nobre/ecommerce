package com.kaique.ecommerce.auth_service.dtos;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String email
) {
}
