package com.example.cloudstorageservice.model.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidAccessDataException extends AuthenticationException {

    public InvalidAccessDataException(String message) {
        super(message);
    }
}
