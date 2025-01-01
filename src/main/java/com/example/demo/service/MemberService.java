package com.example.demo.service;

import com.example.demo.model.Member;
import com.example.demo.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    public void addMmber(){
        Member user = new Member();
        user.setEmail("test@gmail.com");
        user.setName("test");
        user.setPasswd("passwd");
        memberRepository.addMember(user);
    }
}
