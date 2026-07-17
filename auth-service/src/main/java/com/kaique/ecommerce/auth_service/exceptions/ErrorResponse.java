package com.kaique.ecommerce.auth_service.exceptions;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse(
        String title,
        String message,
        int status,
        LocalDateTime timestamp
) {
}
