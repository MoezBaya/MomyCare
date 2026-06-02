package com.example.MomyCare.exception;

public class InvalidTimeException extends RuntimeException {
    public InvalidTimeException(String message) {
        super(message);
    }
}