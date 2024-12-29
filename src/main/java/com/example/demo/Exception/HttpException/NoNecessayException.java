package com.example.demo.Exception.HttpException;

public class NoNecessayException extends RuntimeException {
    public NoNecessayException(String neccessay) {
        super(String.format(OutViewEnum.NO_NECESSARY_INPUT.format, neccessay));
    }
}
