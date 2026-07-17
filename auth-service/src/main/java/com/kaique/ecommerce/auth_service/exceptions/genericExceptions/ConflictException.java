package com.kaique.ecommerce.auth_service.exceptions.genericExceptions;

import com.kaique.ecommerce.auth_service.exceptions.BusinessException;
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
