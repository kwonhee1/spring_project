package com.example.demo.config.security.filters;

import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.config.security.SecurityConfig;
import com.example.demo.config.security.authentication.CustomAuthentication;
import com.example.demo.utils.jwt.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//refresh token 검증
//				성공 : access token 발급 + security context에 authentication저장
//				실패 : refresh token 지우고 로그인 페이지로 redirect
public class CustomRefreshTokenFilter extends CustomTokenFilter{
    protected Authentication authentication;

    public CustomRefreshTokenFilter() {
        super(
                JWTService.REFRESH,
                new CustomRequestMatchers(CustomRequestMatchers.TokenPattern, CustomRequestMatchers.ALL_METHOD),
                new JWTService());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()){
            // authenticaion 객체가 이매 생성되었다면 그냥 다음으로 넘어감
            chain.doFilter(request,response);
            return;
        }

        super.doFilter(request,response, chain);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        // authentication 없음 (access 없음) => refresh 얻기

        request.setCharacterEncoding("UTF-8");
        String refresh = Arrays.stream(request.getCookies()).filter(c -> {
            return c.getName().equals(tokenName);
        }).findAny().get().getValue();

        authentication = jwtService.getAuthentication(refresh, null);
        return getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected void successHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // refresh 존재 => access 발급 + security context에 authenticaion저장
        String token = jwtService.createAccessToken((String)authentication.getPrincipal(), (List<String>)((CustomAuthentication)authentication).getRoles(), getIpFromRequest(request), getAgentFromRequest(request));
        Cookie cookie = new Cookie(JWTService.ACCESS, token);
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);  // 쿠키 유효시간 설정 (1시간)
        response.addCookie(cookie);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected void failureHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        // refresh 이상함 or 존재 안함 => refresh 만료 (redirect 걸지 않음 차후 security에 의해 차당 과정 진입
        Cookie cookie = new Cookie(JWTService.REFRESH, "");
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);  // cookie 만료
        response.addCookie(cookie);
    }
}
