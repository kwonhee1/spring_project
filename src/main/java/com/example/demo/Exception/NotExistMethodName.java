package com.example.demo.Exception;

public class NotExistMethodName extends RuntimeException{
    public NotExistMethodName(String methodName) {
        super("Not Exist Method: " + methodName);
    }
}
