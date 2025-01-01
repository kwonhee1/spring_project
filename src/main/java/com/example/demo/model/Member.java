package com.example.demo.model;

public class Member {
    private String id;      // pk auto_increment
    private String email;   // varchar(35)
    private String passwd;  // not null
    private String name;

    private Role role;      // check role in("user", "admin") default "user"

    public Member(){}

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
    public Role getRole() {return role;}
    public void setRole(Role role) {this.role = role;}
}
