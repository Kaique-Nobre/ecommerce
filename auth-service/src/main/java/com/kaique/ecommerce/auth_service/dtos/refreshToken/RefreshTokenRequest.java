package com.kaique.ecommerce.auth_service.dtos.refreshToken;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequest(
        @NotNull
        String refreshToken
) {
}
