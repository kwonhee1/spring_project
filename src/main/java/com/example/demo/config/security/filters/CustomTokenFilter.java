package com.example.demo.config.security.filters;

import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.exception.http.CustomException;
import com.example.demo.exception.http.view.CustomMessage;
import com.example.demo.exception.http.view.CustomTitle;
import com.example.demo.config.security.util.jwt.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public abstract class CustomTokenFilter extends CustomFilter {

    protected String tokenName;
    protected JWTService jwtService;

    protected CustomTokenFilter(String tokenName, CustomRequestMatchers requestMatchers, JWTService jwtService) {
        super(requestMatchers);
        this.tokenName = tokenName;
        this.jwtService = jwtService;

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
            //chain.doFilter(request, response);
            return;
        }

        if(authentication != null) {
            successHandler((HttpServletRequest) request, (HttpServletResponse) response, authentication);
        }
        chain.doFilter(request, response);
    }

    protected String getToken(HttpServletRequest request, String tokenName) {
        Optional<Cookie> token = Arrays.stream(request.getCookies()).filter(c -> {
            return c.getName().equals(tokenName);
        }).findAny();

        if(token.isEmpty()) {
            return null;
        }
        return token.get().getValue();
    }

    protected abstract void successHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication);
    protected abstract void failureHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException;
    protected abstract Authentication provider(Authentication authentication);
}