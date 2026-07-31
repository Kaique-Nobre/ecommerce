package com.ecommerce.contracts.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {

        super(
                "Forbidden Resource",
                message,
                HttpStatus.FORBIDDEN
        );
    }
}
