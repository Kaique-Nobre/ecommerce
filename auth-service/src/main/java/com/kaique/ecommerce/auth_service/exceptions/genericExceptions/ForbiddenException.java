package com.kaique.ecommerce.auth_service.exceptions.genericExceptions;

import com.kaique.ecommerce.auth_service.exceptions.BusinessException;
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
