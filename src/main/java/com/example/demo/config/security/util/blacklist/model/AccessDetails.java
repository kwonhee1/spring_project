package com.example.demo.config.security.util.blacklist.model;

import java.util.Date;

public class AccessDetails {
    public String accessToken;
    public Date expireDate;

    public AccessDetails(String accessToken, Date expireDate) {
        this.accessToken = accessToken;
        this.expireDate = expireDate;
    }

    public boolean validateExpire(Date now){
        return now.after(expireDate);
    }
}
