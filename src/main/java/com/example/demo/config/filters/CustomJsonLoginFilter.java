package com.example.demo.config.filters;

import com.example.demo.config.CustomDaoAuthenticationProvder.CustomJsonLoginDaoAuthenticationProvider;
import com.example.demo.exception.http.CustomException;
import com.example.demo.exception.http.view.CustomMessage;
import com.example.demo.exception.http.view.CustomTitle;
import com.example.demo.model.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.EOFException;
import java.io.IOException;

public class CustomJsonLoginFilter extends AbstractAuthenticationProcessingFilter {
    private static final String FILTER_URI = "/login";
    private static final String FILTER_METHOD = "POST";

    private CustomJsonLoginDaoAuthenticationProvider customJsonLoginProvider;
    private ObjectMapper objectMapper;

    public CustomJsonLoginFilter(CustomJsonLoginDaoAuthenticationProvider customJsonLoginProvider, ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher(FILTER_URI, FILTER_METHOD));
        this.customJsonLoginProvider = customJsonLoginProvider;
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

    public CustomJsonLoginFilter getCustomTokenFilter() {
        this.setAuthenticationManager(new ProviderManager(customJsonLoginProvider));
        this.setAuthenticationFailureHandler((request, response, exception) -> {
            throw new CustomException(CustomTitle.BAD_REQUEST, CustomMessage.PASSWD_NOT_CORRECT);
        });
        this.setAuthenticationSuccessHandler((request, response, authentication) -> {
            System.out.println("success");
        });

        return this;
    }
}
