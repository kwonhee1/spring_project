package com.example.demo.config.security;

import com.example.demo.controller.URIMappers;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.regex.Pattern;

public class CustomRequestMatchers implements RequestMatcher {
    public static final Pattern LoginPattern = Pattern.compile("^"+ URIMappers.LoginPageURI +"$");
    public static final Pattern AccessTokenPattern = Pattern.compile("^(?!"+URIMappers.LoginPageURI+"$).*"); // Login과 완별일치기 return false (LoginPage는 허용임)

    private Pattern pattern;

    public CustomRequestMatchers(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return pattern.matcher(uri).matches();
    }
}
