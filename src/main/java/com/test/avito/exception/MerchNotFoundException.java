package com.test.avito.exception;

public class MerchNotFoundException extends RuntimeException {
    public MerchNotFoundException(String message) {
        super(message);
    }
}
