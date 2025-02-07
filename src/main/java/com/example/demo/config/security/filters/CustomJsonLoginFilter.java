package com.example.demo.config.security.filters;

import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.config.security.authentication.CustomAuthentication;
import com.example.demo.config.security.util.jwt.model.RefreshToken;
import com.example.demo.model.Member;
import com.example.demo.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class CustomJsonLoginFilter extends CustomFilter {

    private ObjectMapper objectMapper;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private MemberService memberService;

    public CustomJsonLoginFilter(ObjectMapper objectMapper, AuthenticationEntryPoint authenticationEntryPoint,  MemberService memberService) {
        super(new CustomRequestMatchers(CustomRequestMatchers.LoginPattern, "POST"));
        this.objectMapper = objectMapper;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.memberService = memberService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        System.out.println("custom json login filter");

        Member member = objectMapper.readValue((BufferedReader)request.getAttribute(FirstFilter.READER), Member.class);
        member.checkNecessary(new String[]{"email","passwd"}, Member.getters);

        return getAuthenticationManager().authenticate(new CustomAuthentication(member.getEmail(), member.getPasswd()));
    }

    @Override
    protected void successHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token =
                new RefreshToken((CustomAuthentication) authentication).toString();

        response.addCookie( createCookie(RefreshToken.REFRESH, token, RefreshToken.REFRESH_TIME) );

        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void failureHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException, ServletException {
        e.printStackTrace();
        authenticationEntryPoint.commence(request, response, (AuthenticationException) e);
    }

    @Override
    protected Authentication provider(Authentication authentication){
        String email = ((CustomAuthentication) authentication).getEmail();
        String inputPasswd = (String) authentication.getCredentials();

        Member dbMember = memberService.login(email, inputPasswd);

        ((CustomAuthentication) authentication).setMemberId(dbMember.getId());
        ((CustomAuthentication) authentication).setRefreshLevel(dbMember.getRefreshLevel());
        ((CustomAuthentication) authentication).setRoles(dbMember.getRoles());

        ((CustomAuthentication) authentication).setAuthenticated(true);

        return authentication;
    }
}
