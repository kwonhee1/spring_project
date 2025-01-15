package com.example.demo.config.security.util.blacklist;

public class BlackListException extends RuntimeException {
    public BlackListException(String message) {
        super(message+" is in blacklist");
    }
}
