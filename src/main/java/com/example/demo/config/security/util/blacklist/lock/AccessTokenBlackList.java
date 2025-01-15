package com.example.demo.config.security.util.blacklist.lock;

import com.example.demo.config.security.util.blacklist.BlackListException;
import com.example.demo.config.security.util.blacklist.model.AccessDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;

public class AccessTokenBlackList extends ReentrantLock {
    private static Map<String, AccessDetails> blackListAccessToken = new HashMap<>();
    private ReentrantLock lock = new ReentrantLock();
    private Stack<AccessDetails> accessStack = new Stack<>();

    public void addBlacklist(AccessDetails accessDetails) {
        if(lock.isLocked()){
            // 다른 곳에서 사용중이면 그냥 stack위에 올려놓고 pass
            accessStack.push(accessDetails);
        }
        else{
            lock.lock();

            // 남은 stack값들도 모두 넣어버림
            blackListAccessToken.put(accessDetails.accessToken, accessDetails);
            for(AccessDetails details : accessStack){
                blackListAccessToken.put(details.accessToken, details);
            }

            lock.unlock();
        }
    }

    public void checkToken(String accessToken) {
        if(!lock.isLocked()){
            checkExpireDate();
        }

        // 입력 중라면 그냥 현재 값들만 가지고 검사함
        if (blackListAccessToken.containsKey(accessToken))
            throw new BlackListException(accessToken);
    }

    private void checkExpireDate(){
        lock.lock();

        // 모든 blacklist를 돌면서 만료된 값들을 모두 날림
        Date now = new Date();
        blackListAccessToken.keySet().stream()
                // validateExpire값이 false인 것들만 모음
                .filter(key -> !blackListAccessToken.get(key).validateExpire(now))
                .forEach(key -> blackListAccessToken.remove(key));

        lock.unlock();
    }
}
