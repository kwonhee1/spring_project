package com.example.demo.role;

import com.example.demo.controller.URIMappers;
import com.example.demo.exception.http.CustomException;
import com.example.demo.exception.http.view.CustomMessage;
import com.example.demo.exception.http.view.CustomTitle;

import java.util.ArrayList;

public enum MemberRole {
    MEMBER_USER("USER",URIMappers.UserRole), MEMBER_ADMIN("ADMIN",URIMappers.UserRole, URIMappers.AdminRole);

    private String dbName;
    public final ArrayList<String> role = new ArrayList<>();

    MemberRole(String dbName ,String... role){
        this.dbName = dbName;
        for(String permission : role){
            this.role.add(permission);
        }
    }
    public static MemberRole getMemberRole(String dbName){
        for(MemberRole role : MemberRole.values()){
            if(role.dbName.equals(dbName)){
                return role;
            }
        }
        throw new CustomException(CustomTitle.NOT_FOUND, CustomMessage.ROLE_NOT_FOUND, dbName);
    }
}
