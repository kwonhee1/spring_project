package com.example.demo.model;

public class User {
    private String id;
    private String passwd;
    private String name;
    private String email;

    private Boolean isAdmin;
    private Boolean isSocial;

    public Boolean validate(){
        return true;
    }

    public User(){}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getSocial() {
        return isSocial;
    }

    public void setSocial(Boolean social) {
        isSocial = social;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", passwd=" + passwd +
                ", name=" + name +
                ", email=" + email +
                ", isAdmin=" + isAdmin +
                ", isSocial=" + isSocial +
                '}';
    }
}
