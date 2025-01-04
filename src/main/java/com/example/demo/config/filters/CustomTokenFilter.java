package com.example.demo.config.filters;

import com.example.demo.model.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public class CustomTokenFilter extends AbstractAuthenticationProcessingFilter {
    private static final String FILTER_URI = "/Login";
    private static final String FILTER_METHOD = "post";

    private final ObjectMapper objectMapper;

    public CustomTokenFilter(ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher(FILTER_URI, FILTER_METHOD));
        this.objectMapper = objectMapper;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        Member member = objectMapper.readValue(request.getReader().readLine(), Member.class);
        member.checkNecessary(new String[]{"email","passwd"}, Member.getters);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPasswd());
        return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
    }
}
