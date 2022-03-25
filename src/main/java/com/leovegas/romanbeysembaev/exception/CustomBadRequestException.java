package com.leovegas.romanbeysembaev.exception;

public class CustomBadRequestException extends RuntimeException {
    public CustomBadRequestException(String reason) {
        super(reason);
    }
}
