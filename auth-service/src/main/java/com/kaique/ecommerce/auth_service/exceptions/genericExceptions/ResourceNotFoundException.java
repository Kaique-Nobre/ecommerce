package com.kaique.ecommerce.auth_service.exceptions.genericExceptions;

import com.kaique.ecommerce.auth_service.exceptions.BusinessException;
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
