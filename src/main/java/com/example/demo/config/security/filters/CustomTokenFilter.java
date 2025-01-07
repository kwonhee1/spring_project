package com.example.demo.config.security.filters;

import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.utils.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class CustomTokenFilter extends CustomFilter {

    private final ObjectMapper objectMapper;
    private JWTService jwtService;

    public CustomTokenFilter(ObjectMapper objectMapper, JWTService jwtService) {
        super(new CustomRequestMatchers(CustomRequestMatchers.AccessTokenPattern));
        this.objectMapper = objectMapper;
        this.jwtService = jwtService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        System.out.println("access token filter");

        request.setCharacterEncoding("UTF-8");
        Optional<Cookie> access_token = Arrays.stream(request.getCookies()).filter(c->{return c.getName().equals(JWTService.ACCESS);}).findAny();
        // 있는지 검사 안함 -> 오류발생시 어짜피 failureHander로 전송 -> access token없음으로 간주

        return jwtService
                .validateAccessToken(access_token.get().getValue(), getIpFromRequest(request), getAgentFromRequest(request))
                .getAuthentication();
    }

    public CustomTokenFilter customTokenFilter() {
        CustomTokenFilter customTokenFilter = new CustomTokenFilter(objectMapper, jwtService);
        customTokenFilter.setAuthenticationManager(authentication -> {
            // 어떤 검증절차도 필요하지 않음 // 위에서 부르지 않을 예정
            return authentication;
        });
        customTokenFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            // 성공시 다른 작업 없음 위에 Authentication반환으로 access filter 작업 끝남
            System.out.println(authentication.getPrincipal().toString() +", "+ authentication.getCredentials().toString() +", "+ authentication.getAuthorities().toString() +", "+ authentication.getName() +", "+ authentication.getDetails().toString());
        });
        customTokenFilter.setAuthenticationFailureHandler((request, response, exception) -> {
            // access token없음 그냥 return -> 다음 refresh filter로 자동 이전
            // 디버깅용 에러 확인
            System.out.println("failure handler");
            //exception.printStackTrace();
        });
        return customTokenFilter;
    }
}
