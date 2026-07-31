package com.ecommerce.contracts.exception;


import org.springframework.http.HttpStatus;

public class ConflictException extends BusinessException {
    public ConflictException(String message) {

        super(
                "Conflict Error",
                message,
                HttpStatus.CONFLICT
        );
    }
}
