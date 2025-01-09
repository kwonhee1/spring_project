package com.example.demo.config.security.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomAuthentication implements Authentication {
    private UserDetails member;
    private Collection<String> roles;
    private String email;
    private String passwd;
    private boolean authenticated = false;

    public CustomAuthentication(String principal,String credential) {
        email = principal;
        passwd = credential;
    }

    public CustomAuthentication(String principal,String credential, Collection<String> authorities) {
        email = principal;
        passwd = credential;
        roles = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        return authorities;
    }
    public void setRoles(Collection<String>roles) {
        this.roles = roles;
    }
    public Collection<String> getRoles() {
        return roles;
    }
    @Override
    public Object getCredentials() {
        return passwd;
    }
    public void removeCredentials() {
        passwd = null;
    }
    @Override
    public Object getDetails() {
        return member;
    }
    public void setDetails(UserDetails member) {
        this.member = member;
    }
    @Override
    public Object getPrincipal() {
        return email;
    }
    public void setPrincipal(String email) {
        this.email = email;
    }
    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        authenticated = isAuthenticated;
        removeCredentials();
    }
    @Override
    public boolean equals(Object another) {
        return another instanceof CustomAuthentication;
    }
    @Override
    public int hashCode() {
        return 0;
    }
    @Override
    public String getName() {
        return "CustomAuthentication.getName() >> 뭔지 몰라 구현 안함";
    }

    @Override
    public String toString() {
        return "CustomAuthentication{" +
                "member=" + member +
                ", roles=" + roles +
                ", email='" + email + '\'' +
                ", passwd='" + passwd + '\'' +
                ", authenticated=" + authenticated +
                '}';
    }
}
