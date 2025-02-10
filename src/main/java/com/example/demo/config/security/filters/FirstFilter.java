package com.example.demo.config.security.filters;

import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.config.security.util.jwt.model.AccessToken;
import com.example.demo.config.security.util.jwt.model.RefreshToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class FirstFilter extends AbstractAuthenticationProcessingFilter {
    public static final String READER = "reader";

    // 모든 주소에
    public FirstFilter() {
        super(new CustomRequestMatchers(CustomRequestMatchers.TokenPattern, CustomRequestMatchers.ALL_METHOD));
        setAuthenticationManager(a->a);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");

        // reader : login or other json data
        request.setAttribute(READER, request.getReader());

        request.setAttribute("ip", getIpFromRequest((HttpServletRequest) request));
        request.setAttribute("agent", getAgentFromRequest((HttpServletRequest) request));
        // token
        request.setAttribute(AccessToken.ACCESS, getToken((HttpServletRequest) request, AccessToken.ACCESS));
        request.setAttribute(RefreshToken.REFRESH, getToken((HttpServletRequest) request, RefreshToken.REFRESH));

        System.out.println("first filter");
        chain.doFilter(request, response);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        return null;
    }

    private String getIpFromRequest(HttpServletRequest request) {
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

    private String getAgentFromRequest(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    private String getToken(HttpServletRequest request, String tokenName) {
        if(request.getCookies() == null)
            return null;

        Optional<Cookie> token = Arrays.stream(request.getCookies()).filter(c -> {
            return c.getName().equals(tokenName);
        }).findAny();

        if(token.isEmpty())
            return null;

        return token.get().getValue();
    }
}
