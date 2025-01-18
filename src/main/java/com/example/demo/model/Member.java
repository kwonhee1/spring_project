package com.example.demo.model;

import com.example.demo.role.MemberRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Method;
import java.util.*;

public class Member extends MyModel implements UserDetails{
    private int id;      // pk auto_increment
    private String email;   // varchar(35)
    private String passwd;  // not null
    private String name;

    private ArrayList<String> roles = new ArrayList<>();      // check role in("user", "admin") default "user"

    private int refreshLevel;

    public static Map<String, Method> getters = new HashMap<>();

    public Member(){}

    public String getName() {
        return name;
    }
    public Member setName(String name) {
        this.name = name;
        return this;
    }
    public int getId() {
        return id;
    }
    public Member setId(int id) {
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
    public Member setRoles(String input) {
        roles.addAll(MemberRole.getMemberRole(input).role);
        System.out.println(roles);
        return this;
    }
    public Collection<String> getRoles() {
        return roles;
    }
    public int getRefreshLevel() {
        return refreshLevel;
    }
    public void setRefreshLevel(int refresh_level) {
        this.refreshLevel = refresh_level;
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
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for(String role : roles){
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }
        return grantedAuthorities;
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
