package com.example.demo.config.security.provider;

import com.example.demo.exception.http.CustomException;
import com.example.demo.exception.http.view.CustomMessage;
import com.example.demo.exception.http.view.CustomTitle;
import com.example.demo.model.Member;
import com.example.demo.service.MemberService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

/*
    provider는 filter로부터 request에서 얻은 Member정보를 security Authentication class로 받게됨
    1. authentication 의 password를 set된 passwordEncoder를 통해 암호화함
    2. authentication의 userName과 set된 UserDetailsService를 통해 db정보를 가져옴 (Spring boot.User class)
    3. additionalAuthenticalchecks를 통해 authentication(input)과 userDetails(db)를 비교함 (실제 passwd확인 과정) (암화화된 상태로 비교함)
    4. authentication 함수로 Role, 또는 다른 작업을 수행함 (authentication의 사용자 코드를 실행전에 해당 함수에서 위 함수들을 부른다 정도로 이해)
    5. 성공 실패를 filter에게 전달하고 filter의 handler를 실행시킴
 */
public class CustomJsonLoginDaoAuthenticationProvider extends DaoAuthenticationProvider {
    private MemberService memberService;

    public CustomJsonLoginDaoAuthenticationProvider(MemberService memberService) {
        this.memberService = memberService;
    }

    // authentication에 이상이 있는지 확인함
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = (String) authentication.getPrincipal();
        Member member = (Member) authentication.getCredentials();

        Member db = (Member)getUserDetailsService().loadUserByUsername(email);

        additionalAuthenticationChecks(db, (UsernamePasswordAuthenticationToken)authentication);

        return new UsernamePasswordAuthenticationToken(email, db, db.getAuthorities());
    }

    // getUserDetails함수를 통해 db에서 authetication의 Member정보를 가져옴 -> 두개를 비교함 : 진짜 로그인 과정
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String inputPasswd = ((Member) authentication.getCredentials()).getPasswd();
        if (!userDetails.getPassword().equals(inputPasswd)) {
            throw new CustomException(CustomTitle.BAD_REQUEST, CustomMessage.PASSWD_NOT_CORRECT);
        }
    }

    public CustomJsonLoginDaoAuthenticationProvider getLoginDaoAuthenticationProvider() {
        // spring 에서 제공하는 passwd 암호화 factory
        this.setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
        this.setUserDetailsService((email)->{
            return memberService.getMemberByEmail(email);
        });
        return this;
    }
}
