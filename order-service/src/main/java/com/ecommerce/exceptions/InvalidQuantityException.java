package com.ecommerce.exceptions;

import com.ecommerce.contracts.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidQuantityException extends BusinessException {
    public InvalidQuantityException(String message) {

        super(
                "Invalid Quantity",
                message,
                HttpStatus.BAD_REQUEST
        );
    }
}
