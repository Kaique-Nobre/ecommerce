package com.kaique.ecommerce.auth_service.exceptions.genericExceptions;

import com.kaique.ecommerce.auth_service.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BusinessException {
    public UnauthorizedException(String message) {

        super(
                "Unauthorized Resource",
                message,
                HttpStatus.UNAUTHORIZED
        );
    }
}
