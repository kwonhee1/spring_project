package com.example.demo.config.security.filters;

import com.example.demo.config.security.util.jwt.JWTService;

public class NoTokenException extends RuntimeException {
    public NoTokenException(String tokenName) {
        super("no input "+tokenName);
    }
}
