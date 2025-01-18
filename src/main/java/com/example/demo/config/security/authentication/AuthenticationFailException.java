package com.example.demo.config.security.authentication;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationFailException extends AuthenticationException {
    public AuthenticationFailException(String message) {
        super("authenticaion validate fail : "+message);
    }
}
