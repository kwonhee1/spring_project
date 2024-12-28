package com.example.demo.Exception;

import com.example.demo.Exception.HttpException.NoNecessayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HttpExceptionHandler {
    @ExceptionHandler(NoNecessayException.class)
    public ResponseEntity<?> handleNoNecessaryException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
