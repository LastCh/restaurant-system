package com.restaurant.system.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException (String message) {
        super(message);
    }
}
