package com.example.demo.exception.http.view;

public enum CustomMessage {
    // success
    RETURN_SUCCESS_FORMAT("{\"title\" : \"success\", \"message\" : \"%s\"}"),
    // Handler
    RETURN_JSON_FORMAT("{\"title\" : \"%s\", \"message\" : \"%s\"}"),

    // Custom Exception Messages

    // login
    NO_NECESSARY_INPUT("%s is necessary for this request"),

    // register
    EAMIL_ALREADY_EXIST("%s is already exist"),
    INVALID_EMAIL_KEY("email key is invalid");

    public String format;

    CustomMessage(String format) {this.format = format;}

}
