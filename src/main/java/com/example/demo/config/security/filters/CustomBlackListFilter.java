package com.example.demo.config.security.filters;

import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.config.security.authentication.CustomAuthentication;
import com.example.demo.config.security.util.blacklist.BlackListException;
import com.example.demo.config.security.util.blacklist.BlackListService;
import com.example.demo.config.security.util.jwt.JWTService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

public class CustomBlackListFilter extends CustomTokenFilter{
    private final BlackListService blackListService;

    public CustomBlackListFilter() {
        super(JWTService.ACCESS,
                new CustomRequestMatchers(CustomRequestMatchers.TokenPattern, CustomRequestMatchers.ALL_METHOD),
                null);
        blackListService = new BlackListService();
    }

    @Override
    protected void successHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 아무것도 하지 않음
    }

    @Override
    protected void failureHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        if (e instanceof BlackListException) {
            // access, refresh token 날림
            response.addCookie(createCookie(JWTService.REFRESH, "", 0));
            response.addCookie(createCookie(JWTService.ACCESS, "", 0));
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        BlackListService.checkIP(getIpFromRequest(request));
        BlackListService.checkToken(getToken(request, JWTService.ACCESS));

        return null;
    }
}