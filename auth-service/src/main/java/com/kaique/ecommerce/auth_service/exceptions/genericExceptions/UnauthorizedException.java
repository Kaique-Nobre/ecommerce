package com.kaique.ecommerce.auth_service.exceptions.genericExceptions;

import com.ecommerce.contracts.exception.BusinessException;
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
