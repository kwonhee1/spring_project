package com.example.demo.config.security.authentication;

import com.example.demo.model.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomAuthentication implements Authentication {
    private int memberId;
    private Collection<String> roles;
    private String email;
    private String passwd;
    private boolean authenticated = false;
    private int refreshLevel;

    // json login filter :: input id and passwd
    public CustomAuthentication(String principal,String credential) {
        email = principal;
        passwd = credential;
    }

    // use AccessToken(String) to Authentic
    public CustomAuthentication(int id, String principal, Collection<String> authorities) {
        memberId = id;
        email = principal;
        roles = authorities;
    }
    // use RefreshToken(String) to Authentic
    public CustomAuthentication(int id, int level) {
        memberId = id;
        refreshLevel = level;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        return authorities;
    }
    public void setRefreshLevel(int refreshLevel) {this.refreshLevel = refreshLevel;}
    public int getRefreshLevel() {return refreshLevel;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public void setMemberId(int id) {memberId = id;}
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
        return null;
    }
    @Override
    public Object getPrincipal() {
        return memberId;
    }
    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }
    @Override
    public void setAuthenticated(boolean isAuthenticated) {
        if(email == null || memberId == 0 || roles == null)
            throw new AuthenticationFailException("CustomAuthentication >> setAuthenticated :: left null value");

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
                ", roles=" + roles +
                ", email='" + email + '\'' +
                ", passwd='" + passwd + '\'' +
                ", authenticated=" + authenticated +
                '}';
    }
}
