package com.kaique.ecommerce.auth_service.exceptions.genericExceptions;

import com.ecommerce.contracts.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BusinessException {
    public InvalidTokenException(String message) {

        super(
                "Invalid Token",
                message,
                HttpStatus.UNAUTHORIZED
        );
    }
}
