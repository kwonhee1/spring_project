package com.example.demo.model;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Member extends MyModel {
    private String id;      // pk auto_increment
    private String email;   // varchar(35)
    private String passwd;  // not null
    private String name;

    private Role role;      // check role in("user", "admin") default "user"

    public static Map<String, Method> getters = new HashMap<>();

    public Member(){}

    public String getName() {
        return name;
    }
    public Member setName(String name) {
        this.name = name;
        return this;
    }
    public String getId() {
        return id;
    }
    public Member setId(String id) {
        this.id = id;return this;
    }
    public String getPasswd() {
        return passwd;
    }
    public Member setPasswd(String passwd) {
        this.passwd = passwd;return this;
    }
    public String getEmail() {
        return email;
    }
    public Member setEmail(String email) {
        this.email = email;
        return this;
    }
    public Role getRole() {return role;}
    public Member setRole(Role role) {this.role = role;
        return this;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", passwd='" + passwd + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                '}';
    }
}
