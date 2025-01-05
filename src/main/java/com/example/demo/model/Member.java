package com.example.demo.model;

import com.example.demo.role.Permission;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Method;
import java.util.*;

public class Member extends MyModel implements UserDetails{
    private String id;      // pk auto_increment
    private String email;   // varchar(35)
    private String passwd;  // not null
    private String name;

    private ArrayList<Permission> roles;      // check role in("user", "admin") default "user"

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
    public Member addRole(Collection<Permission> role) {
        roles.addAll(role);
        return this;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", passwd='" + passwd + '\'' +
                ", name='" + name + '\'' +
                ", role=" + roles +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }
}
