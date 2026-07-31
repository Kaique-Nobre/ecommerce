package com.kaique.ecommerce.auth_service.exceptions.genericExceptions;

import com.ecommerce.contracts.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class BadCredentialsException extends BusinessException {
    public BadCredentialsException(String message) {

        super(
                "Bad Credentials",
                message,
                HttpStatus.UNAUTHORIZED
        );
    }
}
