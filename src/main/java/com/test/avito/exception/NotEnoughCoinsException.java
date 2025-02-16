package com.test.avito.exception;

public class NotEnoughCoinsException extends RuntimeException {
    public NotEnoughCoinsException(String message) {
        super(message);
    }
}
