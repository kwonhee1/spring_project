package com.example.demo.service;

import com.example.demo.exception.http.CustomException;
import com.example.demo.exception.http.view.CustomTitle;
import com.example.demo.exception.http.view.CustomMessage;
import com.example.demo.model.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private MemberRepository memberRepository;
    private EmailService emailService;

    @Autowired
    public MemberService(EmailService emailService, MemberRepository memberRepository) {
        this.emailService = emailService;
        this.memberRepository = memberRepository;
    }

    public void addMember(Member member, String keyCode){
        // check conflict
        validateNotExistEmail(member.getEmail());

        // check email and key code
        if(!emailService.checkEmailKey(member.getEmail(), keyCode))
            throw new CustomException(CustomTitle.ALREADY_EXISTS, CustomMessage.INVALID_EMAIL_KEY);

        // add member to db
        memberRepository.addMember(member);
    }

    public void sendEmail(String email){
        // check conflict
        validateNotExistEmail(email);

        //send email
        emailService.sendEmial(email);
    }

    private void validateNotExistEmail(String email){
        if(memberRepository.isExistEmail(email))
            throw new CustomException(CustomTitle.ALREADY_EXISTS, CustomMessage.EAMIL_ALREADY_EXIST,email); // email is exist
    }
}
