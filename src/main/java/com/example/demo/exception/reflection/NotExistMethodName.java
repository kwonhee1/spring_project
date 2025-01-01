package com.example.demo.exception.reflection;

public class NotExistMethodName extends RuntimeException{
    public NotExistMethodName(String methodName) {
        super("Not Exist Method: " + methodName);
    }
}
