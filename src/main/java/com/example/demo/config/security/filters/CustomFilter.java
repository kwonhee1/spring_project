package com.example.demo.config.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

public abstract class CustomFilter extends AbstractAuthenticationProcessingFilter {
    protected CustomFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
        setAuthenticationManager(this::provider);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!requiresAuthentication((HttpServletRequest) request, (HttpServletResponse) response)) {
            chain.doFilter(request, response);
            return;
        }

        Authentication authentication = null; // 이전에 authentication객체를 생성하는 어떠한 filter도 존재하지 않음
        try {
            authentication = attemptAuthentication((HttpServletRequest) request, (HttpServletResponse) response);
        } catch (Exception e) {
            e.printStackTrace();
            failureHandler((HttpServletRequest) request, (HttpServletResponse) response, e);
            return;
        }

        if(authentication != null) {
            successHandler((HttpServletRequest) request, (HttpServletResponse) response, authentication);
        }
        chain.doFilter(request, response);
    }

    protected Cookie createCookie(String object, String value, int age){
        Cookie cookie = new Cookie(object, value);
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        cookie.setMaxAge(age);
        return cookie;
    }

    protected abstract void successHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication);
    protected abstract void failureHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException, ServletException;
    protected abstract Authentication provider(Authentication authentication);
}
