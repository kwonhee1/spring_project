package com.example.demo.config.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthentication extends UsernamePasswordAuthenticationToken {
    private String requestIP;

    // filter to Provider
    public CustomAuthentication(String email, String passwd, String requestIP) {
        super(email, passwd);
        this.requestIP = requestIP;
    }

    public String getRequestIP() {
        return requestIP;
    }

    // return at provider to security
    public CustomAuthentication(Object principal, Collection<? extends GrantedAuthority> authorities, String requestIP) {
        super(principal, null, authorities);
        // provider에서 반환할때는 password값이 없는 상태로 반환되어야함
        this.requestIP = requestIP;
    }
}
