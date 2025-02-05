package com.example.demo.config.security.filters;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

public abstract class CustomFilter extends AbstractAuthenticationProcessingFilter {
    protected CustomFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
        setAuthenticationManager((authentic)->{return null;});
    }

    protected String getIpFromRequest(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    protected String getAgentFromRequest(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    protected Cookie createCookie(String object, String value, int age){
        Cookie cookie = new Cookie(object, value);
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        cookie.setMaxAge(age);
        return cookie;
    }

    protected abstract Authentication provider(Authentication authentication);
}
