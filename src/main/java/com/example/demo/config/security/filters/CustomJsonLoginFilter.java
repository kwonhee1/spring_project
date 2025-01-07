package com.example.demo.config.security.filters;

import com.example.demo.config.security.authentication.CustomAuthentication;
import com.example.demo.config.security.provider.CustomJsonLoginDaoAuthenticationProvider;
import com.example.demo.model.Member;
import com.example.demo.role.Permission;
import com.example.demo.utils.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
import java.util.List;

public class CustomJsonLoginFilter extends AbstractAuthenticationProcessingFilter {
    private static final String FILTER_URI = "/Login";
    private static final String FILTER_METHOD = "POST";

    private CustomJsonLoginDaoAuthenticationProvider customJsonLoginProvider;
    private ObjectMapper objectMapper;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private JWTService jwtService;

    public CustomJsonLoginFilter(CustomJsonLoginDaoAuthenticationProvider customJsonLoginProvider, ObjectMapper objectMapper, AuthenticationEntryPoint authenticationEntryPoint, JWTService jwtService) {
        super(new AntPathRequestMatcher(FILTER_URI, FILTER_METHOD));
        this.customJsonLoginProvider = customJsonLoginProvider;
        this.objectMapper = objectMapper;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtService = jwtService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        request.setCharacterEncoding("UTF-8");
        Member member = objectMapper.readValue(request.getReader().readLine(), Member.class);
        member.checkNecessary(new String[]{"email","passwd"}, Member.getters);

        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPasswd()));
    }

    public CustomJsonLoginFilter getCustomTokenFilter() {
        this.setAuthenticationManager(new ProviderManager(customJsonLoginProvider));
        this.setAuthenticationFailureHandler((request, response, exception) -> {
            //throw exception;
            authenticationEntryPoint.commence(request, response, exception);
        });
        this.setAuthenticationSuccessHandler((request, response, authentication) -> {
            String token = jwtService.createAccessToken((String)authentication.getPrincipal(), authentication.getAuthorities(), getIpFromRequest(request), request.getHeader("User-Agent"));
            Cookie cookie = new Cookie("access_token", token);
            cookie.setHttpOnly(false);
            cookie.setPath("/");
            response.addCookie(cookie);

            response.setStatus(HttpServletResponse.SC_OK);
        });

        return this;
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
}
