package com.example.demo.exception.http.view;

public enum CustomMessage {
    // success
    RETURN_SUCCESS_FORMAT("{\"title\" : \"success\", \"message\" : \"%s\"}"),
    // Handler
    RETURN_JSON_FORMAT("{\"title\" : \"%s\", \"message\" : \"%s\"}"),

    // Custom Exception Messages

    // model
    NO_NECESSARY_INPUT("%s is necessary for this request"),

    // login
    PASSWD_NOT_CORRECT("password is not correct"),
    NO_EXIST_EMAIL("email address is not exist"),

    // register
    EAMIL_ALREADY_EXIST("%s is already exist"),
    INVALID_EMAIL_KEY("email key is invalid");

    public String format;

    CustomMessage(String format) {this.format = format;}

}
