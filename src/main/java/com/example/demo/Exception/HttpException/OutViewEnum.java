package com.example.demo.Exception.HttpException;

public enum OutViewEnum {
    NO_NECESSARY_INPUT("%s is necessary for this request");

    public String format;

    OutViewEnum(String format) {this.format = format;}

}
