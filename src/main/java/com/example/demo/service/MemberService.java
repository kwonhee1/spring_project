package com.example.demo.service;

import com.example.demo.config.security.authentication.AuthenticationFailException;
import com.example.demo.exception.http.CustomException;
import com.example.demo.exception.http.view.CustomTitle;
import com.example.demo.exception.http.view.CustomMessage;
import com.example.demo.model.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.util.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private MemberRepository memberRepository;
    private EmailService emailService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(EmailService emailService, MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member login(String email, String inputPassword){
        Member dbMember = memberRepository.getMemberByEmail(email);

        if(dbMember == null)
            throw new CustomException(CustomTitle.NOT_FOUND, CustomMessage.NO_EXIST_EMAIL);

        if(!passwordEncoder.matches(inputPassword, dbMember.getPasswd()))
            throw new CustomException(CustomTitle.BAD_REQUEST, CustomMessage.PASSWD_NOT_CORRECT);

        return dbMember;
    }

    public Member getMemberByMemberId(int memberId){
        Member member = memberRepository.getMemberByMemberId(memberId);
        if(member == null)
            throw new CustomException(CustomTitle.NOT_FOUND, CustomMessage.NO_EXIST_EMAIL);
        System.out.println("service:"+ member.getAuthorities());
        return member;
    }

    public void addMember(Member member, String keyCode){
        // check conflict
        validateNotExistEmail(member.getEmail());

        // check email and key code
        if(!emailService.checkEmailKey(member.getEmail(), keyCode))
            throw new CustomException(CustomTitle.ALREADY_EXISTS, CustomMessage.INVALID_EMAIL_KEY);

        // encode passwd
        member.setPasswd(passwordEncoder.encode(member.getPasswd()));
        // add member to db
        int userId = memberRepository.addMember(member);
        memberRepository.addRefresh(userId);
    }

    public void sendEmail(String email){
        // check conflict
        validateNotExistEmail(email);

        //send email
        emailService.sendEmial(email);
    }

    public boolean checkRefreshLevel(int memberId, int inputLevel){
       return memberRepository.getRefreshLevel(memberId);
    }

    private void validateNotExistEmail(String email){
        if(memberRepository.isExistEmail(email))
            throw new CustomException(CustomTitle.ALREADY_EXISTS, CustomMessage.EAMIL_ALREADY_EXIST,email); // email is exist
    }
}
