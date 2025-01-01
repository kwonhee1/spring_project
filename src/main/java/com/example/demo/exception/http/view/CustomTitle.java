package com.example.demo.exception.http.view;

public enum CustomTitle {
    ALREADY_EXISTS("Already Exists"),
    NOT_FOUND("Not Found"),
    BAD_REQUEST("Bad Request"),
    NO_NECESSARY_PARAMETER("No Necessary Parameter");

    public String title;

    CustomTitle(String title){
        this.title = title;
    }
}
