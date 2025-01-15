package com.example.demo.config.security.util.jwt.exception;

public class TokenCreateFailException extends RuntimeException {
    public TokenCreateFailException(String message) {
        super(message);
    }
}
