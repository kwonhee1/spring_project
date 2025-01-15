package com.example.demo.config.security.util.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;

@FunctionalInterface
public interface ValidateFunctionInterface {
    void validateFunction(DecodedJWT decodedJWT, String key, String expected);
}
