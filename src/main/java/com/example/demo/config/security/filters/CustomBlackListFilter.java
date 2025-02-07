package com.example.demo.config.security.filters;

import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.config.security.util.blacklist.BlackListException;
import com.example.demo.config.security.util.blacklist.BlackListService;
import com.example.demo.config.security.util.jwt.model.AccessToken;
import com.example.demo.config.security.util.jwt.model.RefreshToken;
import com.example.demo.controller.URIMappers;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

public class CustomBlackListFilter extends CustomTokenFilter{
    private final BlackListService blackListService;

    public CustomBlackListFilter() {
        super(AccessToken.ACCESS,
                new CustomRequestMatchers(CustomRequestMatchers.TokenPattern, CustomRequestMatchers.ALL_METHOD));
        setAuthenticationManager(authentication -> authentication);
        blackListService = new BlackListService();
    }

    @Override
    protected void successHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 아무것도 하지 않음
    }

    @Override
    protected void failureHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        if (e instanceof BlackListException) {
            // access, refresh token 날림
            response.addCookie(createCookie(RefreshToken.REFRESH, "", 0));
            response.addCookie(createCookie(AccessToken.ACCESS, "", 0));

            response.sendRedirect(URIMappers.MainPageURI);
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        BlackListService.checkIP((String)request.getAttribute("ip"));
        BlackListService.checkToken((String)request.getAttribute(AccessToken.ACCESS));

        return null;
    }
    @Override
    protected Authentication provider(Authentication authentication) {
        return null;
    }
}