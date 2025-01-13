package com.example.demo.config.security.filters;


import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.utils.jwt.JWTService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class CustomAccessTokenFilter extends CustomTokenFilter {

    public CustomAccessTokenFilter() {
        super(JWTService.ACCESS,
                new CustomRequestMatchers(CustomRequestMatchers.TokenPattern, CustomRequestMatchers.ALL_METHOD),
                new JWTService()
        );
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        request.setCharacterEncoding("UTF-8");

        String access_token = Arrays.stream(request.getCookies()).filter(c -> {
            return c.getName().equals(tokenName);
        }).findAny().get().getValue();

        HashMap<String, String> jwtValidateMap = new HashMap<>();
        jwtValidateMap.put("ip", getIpFromRequest(request));
        jwtValidateMap.put("agent", getAgentFromRequest(request));

        Authentication authentication = jwtService.getAuthentication(access_token, jwtValidateMap);
        return getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected void successHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 저장하는게 맞나?
        // context를 만드는게 맞나? 그냥 get하기만 하면끝인가?
    }

    @Override
    protected void failureHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        Cookie cookie = new Cookie(JWTService.ACCESS, "");
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // cookie 만료
        response.addCookie(cookie);
    }
}
