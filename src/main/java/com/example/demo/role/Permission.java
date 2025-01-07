package com.example.demo.role;

import org.springframework.security.core.GrantedAuthority;

public enum Permission implements GrantedAuthority {
    // page
    PAGE_USER("page_user"), PAGE_ADMIN("page_admin");

    // others

    public String permission;

    Permission(String p){
        permission = p;
    }

    @Override
    public String getAuthority() {
        return permission;
    }
}
