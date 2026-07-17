package com.kaique.ecommerce.auth_service.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
        String title,
        int status,
        LocalDateTime timestamp,
        Map<String, String> errors
) {
}
