package com.example.demo.config.security.util.blacklist;

import com.example.demo.config.security.util.blacklist.lock.AccessTokenBlackList;
import com.example.demo.config.security.util.blacklist.model.AccessDetails;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class BlackListService {
    private static List<String> blackListIP = new ArrayList<>();
    private static final ReentrantLock lock = new ReentrantLock();
    private static final AccessTokenBlackList accessTokenBlackList = new AccessTokenBlackList();

    public static void addBlackList(String ip) {
        blackListIP.add(ip);
        System.out.println("BlackListService :: addBlackList() :: "+ip + " is blacklisted.");
    }
    public static void checkIP(String ip){
        if(blackListIP.contains(ip)){
            throw new BlackListException(ip);
        }
    }

    // access black list
    public static void addBlackList(String accessToken, Date expireDate) {
        accessTokenBlackList.addBlacklist(new AccessDetails(accessToken, expireDate));
    }

    public static void checkToken(String accessToken){
        accessTokenBlackList.checkToken(accessToken);
    }
}
