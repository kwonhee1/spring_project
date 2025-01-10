package com.example.demo.config.security;

import com.example.demo.controller.URIMappers;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;
import java.util.regex.Pattern;

public class CustomRequestMatchers implements RequestMatcher {
    public static final Pattern LoginPattern = Pattern.compile("^"+ URIMappers.LoginPageURI +"$");
    public static final Pattern AccessTokenPattern = Pattern.compile(String.format("^(?!(%s|%s)$).*", URIMappers.LoginPageURI, URIMappers.RegisterPageURI)); // Login과 완별일치기 return false (LoginPage는 허용임)
    public static final String[] ALL_METHOD = new String[]{"GET", "POST", "PUT", "DELETE", "OPTIONS"};

    private final Pattern pattern;
    private final String []method;

    public CustomRequestMatchers(Pattern pattern, String ...method) {
        this.pattern = pattern;
        this.method = method;

    }

    @Override
    public boolean matches(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        System.out.println("uri:" + uri + " method:" + method);
        return pattern.matcher(uri).matches() && Arrays.stream(this.method).anyMatch(method::equals);
    }
}
