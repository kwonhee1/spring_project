package com.example.demo.model;

public enum Role {
    USER("user"), ADMIN("admin");

    public final String role;

    Role(String role){
        this.role = role;
    }
}
