package com.example.demo.role;

import com.example.demo.exception.http.CustomException;
import com.example.demo.exception.http.view.CustomMessage;
import com.example.demo.exception.http.view.CustomTitle;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;

public enum MemberRole {
    USER(Permission.USER), ADMIN(Permission.USER, Permission.ADMIN);

    public final ArrayList<Permission> role = new ArrayList<>();

    MemberRole(Permission... role){
        for(Permission permission : role){
            this.role.add(permission);
        }
    }

    public static MemberRole getRole(String roleName){
        for(MemberRole role : MemberRole.values()){
            if(role.name().equals(roleName)){
                return role;
            }
        }
        throw new IllegalArgumentException("MemberRole can not find role name " + roleName);
    }

    public String toString() {
        if(role.isEmpty())
            return "";
        else return role.toString();
    }
}
