package com.example.demo.config.security.filters;

import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.exception.http.CustomException;
import com.example.demo.exception.http.view.CustomMessage;
import com.example.demo.exception.http.view.CustomTitle;
import com.example.demo.utils.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class CustomAccessTokenFilter extends CustomFilter {

    private final ObjectMapper objectMapper;
    private JWTService jwtService;

    public CustomAccessTokenFilter(ObjectMapper objectMapper, JWTService jwtService) {
        super(new CustomRequestMatchers(CustomRequestMatchers.AccessTokenPattern, CustomRequestMatchers.ALL_METHOD));
        this.objectMapper = objectMapper;
        this.jwtService = jwtService;

        createCustomTokenFilter();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // AbstractAuthenticationProcessingFilter(URLMatcher) : URLMatcher와 request비교하는 함수(기본생성자 없어서 이방법 밖에 없음)
        if(!requiresAuthentication((HttpServletRequest) request, (HttpServletResponse) response)){
            chain.doFilter(request, response);
            return;
        }

        Authentication authentication = null; // 이전에 authentication객체를 생성하는 어떠한 filter도 존재하지 않음
        try{
            authentication = attemptAuthentication((HttpServletRequest) request, (HttpServletResponse) response);
        }catch (Exception e){
            e.printStackTrace();
            unsuccessfulAuthentication((HttpServletRequest) request, (HttpServletResponse) response, new CustomException(CustomTitle.NOT_FOUND, CustomMessage.NO_ACCESS_TOKEN));
            return;
        }
        if(authentication == null){
            unsuccessfulAuthentication((HttpServletRequest) request, (HttpServletResponse) response, new CustomException(CustomTitle.NOT_FOUND, CustomMessage.NO_ACCESS_TOKEN));
        }else {
            successfulAuthentication((HttpServletRequest) request, (HttpServletResponse) response, chain, authentication);
            //successfulAuthentication() 에서 해당 authentication security context에 저장
            chain.doFilter(request, response);
        }
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        // attemptAuthentication함수에서 어떠한 exception처리를하지않음 위에서 try {함수} 실행 예정임 (기본은 AuthenticationException 만 try하나 override처리하여 모두 잡는걸로 수정)
        //System.out.println("access token filter");
        request.setCharacterEncoding("UTF-8");

        Optional<Cookie> access_token = Arrays.stream(request.getCookies()).filter(c -> {
            return c.getName().equals(JWTService.ACCESS);
        }).findAny();
        // 있는지 검사 안함 -> 오류발생시 어짜피 failureHander로 전송 -> access token없음으로 간주

        return jwtService
                .getAuthentication(access_token.get().getValue(), getIpFromRequest(request), getAgentFromRequest(request));

    }

    public void createCustomTokenFilter() {
        setAuthenticationManager(authentication -> {
            // 어떤 검증절차도 필요하지 않음 // 위에서 부르지 않을 예정
            return authentication;
        });
        //성공 : authentication을 가지고 다음 filter 진행( 사실상 security종료)
        //실패 : access token 지우고 현재 request 로 redirect
        setAuthenticationSuccessHandler((request, response, authentication) -> {
            // 성공시 해당 authentication을 security context에 저장함
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // context 만들어야함? gpt는 gpt는 앞에 filter에서 만들어준다하고, git hub code는 직접 만들었음
        });
        setAuthenticationFailureHandler((request, response, exception) -> {
            Cookie cookie = new Cookie(JWTService.ACCESS, "");
            cookie.setHttpOnly(false);
            cookie.setPath("/");
            cookie.setMaxAge(0);  // cookie 만료
            response.addCookie(cookie);
            //response.sendRedirect(request.getContextPath());
        });
    }
}
