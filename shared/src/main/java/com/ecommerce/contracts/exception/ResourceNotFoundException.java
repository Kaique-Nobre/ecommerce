package com.ecommerce.contracts.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {

        super(
                "Resource Not Found",
                message,
                HttpStatus.NOT_FOUND
        );
    }
}
