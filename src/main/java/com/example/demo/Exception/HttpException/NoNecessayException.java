package com.example.demo.Exception.HttpException;

public class NoNecessayException extends RuntimeException {
    private static final String format = "%s is necessary for this request";
    public NoNecessayException(String neccessay) {
        super(String.format(format, neccessay));
    }
}
