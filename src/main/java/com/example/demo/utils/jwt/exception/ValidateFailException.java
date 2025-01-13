package com.example.demo.utils.jwt.exception;

public class ValidateFailException extends RuntimeException {
    public ValidateFailException(String message) {
        super("JWTService token validate Fail :" +message);
    }
}
