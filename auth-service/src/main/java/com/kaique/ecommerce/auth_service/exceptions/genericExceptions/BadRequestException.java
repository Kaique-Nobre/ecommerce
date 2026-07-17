package com.kaique.ecommerce.auth_service.exceptions.genericExceptions;

import com.kaique.ecommerce.auth_service.exceptions.BusinessException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends BusinessException {
    public BadRequestException(String message) {

        super(
                "Bad Request",
                message,
                HttpStatus.BAD_REQUEST
        );
    }
}
