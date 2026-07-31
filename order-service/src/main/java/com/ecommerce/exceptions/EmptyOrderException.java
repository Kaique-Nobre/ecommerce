package com.ecommerce.exceptions;

import com.ecommerce.contracts.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class EmptyOrderException extends BusinessException {
    public EmptyOrderException(String message) {

        super(
                "Empty Order Exception",
                message,
                HttpStatus.BAD_REQUEST
                );
    }
}
