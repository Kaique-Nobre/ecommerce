package com.kaique.ecommerce.auth_service.exceptions.genericExceptions;

import com.kaique.ecommerce.auth_service.exceptions.BusinessException;
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
