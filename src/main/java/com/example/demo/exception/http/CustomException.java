package com.example.demo.exception.http;

import com.example.demo.exception.http.view.CustomTitle;
import com.example.demo.exception.http.view.CustomMessage;
import org.springframework.security.core.AuthenticationException;

public class CustomException extends AuthenticationException {
    public CustomException(CustomTitle title, CustomMessage message, String ... args) {
        super(String.format(CustomMessage.RETURN_JSON_FORMAT.format, title.title, String.format(message.format, args)));
    }
}
