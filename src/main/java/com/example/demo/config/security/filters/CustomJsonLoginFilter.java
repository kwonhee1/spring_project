package com.example.demo.config.security.filters;

import com.example.demo.config.security.provider.CustomJsonLoginDaoAuthenticationProvider;
import com.example.demo.model.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public class CustomJsonLoginFilter extends AbstractAuthenticationProcessingFilter {
    private static final String FILTER_URI = "/Login";
    private static final String FILTER_METHOD = "POST";

    private CustomJsonLoginDaoAuthenticationProvider customJsonLoginProvider;
    private ObjectMapper objectMapper;
    private AuthenticationEntryPoint authenticationEntryPoint;

    public CustomJsonLoginFilter(CustomJsonLoginDaoAuthenticationProvider customJsonLoginProvider, ObjectMapper objectMapper, AuthenticationEntryPoint authenticationEntryPoint) {
        super(new AntPathRequestMatcher(FILTER_URI, FILTER_METHOD));
        this.customJsonLoginProvider = customJsonLoginProvider;
        this.objectMapper = objectMapper;
        this.authenticationEntryPoint = authenticationEntryPoint;
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
            //throw exception;
            authenticationEntryPoint.commence(request, response, exception);
        });
        this.setAuthenticationSuccessHandler((request, response, authentication) -> {
            System.out.println("success");
        });

        return this;
    }
}
