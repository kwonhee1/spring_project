package com.example.demo.role;

import java.util.ArrayList;

public enum MemberRole {
    USER(Permission.PAGE_USER), ADMIN(Permission.PAGE_ADMIN, Permission.PAGE_ADMIN);

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
}
