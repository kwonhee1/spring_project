package com.example.demo.config.security.filters;

import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.exception.http.CustomException;
import com.example.demo.exception.http.view.CustomMessage;
import com.example.demo.exception.http.view.CustomTitle;
import com.example.demo.utils.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public abstract class CustomTokenFilter extends CustomFilter{

    private JWTService jwtService;

    protected CustomTokenFilter(JWTService jwtService) {
        super(new CustomRequestMatchers(CustomRequestMatchers.AccessTokenPattern, CustomRequestMatchers.ALL_METHOD));
        this.jwtService = jwtService;

        // createCustomTokenFilter();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(!requiresAuthentication((HttpServletRequest) request, (HttpServletResponse) response)){
            chain.doFilter(request, response);
            return;
        }

        Authentication authentication = null; // 이전에 authentication객체를 생성하는 어떠한 filter도 존재하지 않음
        try{
            authentication = attemptAuthentication((HttpServletRequest) request, (HttpServletResponse) response);
        }catch (Exception e){
            e.printStackTrace();
            failureHandler((HttpServletRequest) request, (HttpServletResponse) response, e);
            return;
        }
        if(authentication == null){
            failureHandler((HttpServletRequest) request, (HttpServletResponse) response,  new CustomException(CustomTitle.NOT_FOUND, CustomMessage.NO_ACCESS_TOKEN));
        }else {
            successHandler((HttpServletRequest) request, (HttpServletResponse) response, authentication);
            //successfulAuthentication() 에서 해당 authentication security context에 저장
            chain.doFilter(request, response);
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        request.setCharacterEncoding("UTF-8");

        String access_token = Arrays.stream(request.getCookies()).filter(c -> {
            return c.getName().equals(JWTService.ACCESS);
        }).findAny().get().getValue();

        validateToken(getIpFromRequest(request), getAgentFromRequest(request));

        return getAuthentication(access_token);
    }

    protected abstract void successHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication);
    protected abstract void failureHandler(HttpServletRequest request, HttpServletResponse response, Exception e);
    protected abstract Authentication getAuthentication(String token);
    protected abstract void validateToken(String ip, String agent);
//    protected void createCustomTokenFilter(){
//        // setAuthenticationManager() : 설정하지 않음 (authenticaion객체를 검증할 일이 없음 (무조건 탈취되지 않았다 가정)
//        setAuthenticationSuccessHandler(this::successHandler);
//        setAuthenticationFailureHandler(this::failureHandler);
//    }
}