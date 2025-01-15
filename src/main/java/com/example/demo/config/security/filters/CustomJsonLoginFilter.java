package com.example.demo.config.security.filters;

import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.config.security.authentication.CustomAuthentication;
import com.example.demo.config.security.provider.CustomJsonLoginDaoAuthenticationProvider;
import com.example.demo.model.Member;
import com.example.demo.config.security.util.jwt.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CustomJsonLoginFilter extends CustomFilter {

    private CustomJsonLoginDaoAuthenticationProvider customJsonLoginProvider;
    private ObjectMapper objectMapper;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private JWTService jwtService;

    public CustomJsonLoginFilter(CustomJsonLoginDaoAuthenticationProvider customJsonLoginProvider, ObjectMapper objectMapper, AuthenticationEntryPoint authenticationEntryPoint, JWTService jwtService) {
        super(new CustomRequestMatchers(CustomRequestMatchers.LoginPattern, "POST"));
        this.customJsonLoginProvider = customJsonLoginProvider;
        this.objectMapper = objectMapper;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtService = jwtService;

        createCustomTokenFilter();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        System.out.println("custom json login filter");
        request.setCharacterEncoding("UTF-8");
        Member member = objectMapper.readValue(request.getReader().readLine(), Member.class);
        member.checkNecessary(new String[]{"email","passwd"}, Member.getters);

        return getAuthenticationManager().authenticate(new CustomAuthentication(member.getEmail(), member.getPasswd()));
    }

    public void createCustomTokenFilter() {
        setAuthenticationManager(new ProviderManager(customJsonLoginProvider));
        setAuthenticationFailureHandler((request, response, exception) -> {
            //throw exception;\
            exception.printStackTrace();
            authenticationEntryPoint.commence(request, response, exception);
        });
        setAuthenticationSuccessHandler((request, response, authentication) -> {
            HashMap<String , String> claims = new HashMap<>();
            claims.put("email", (String)authentication.getPrincipal());

            String token = jwtService.createToken(JWTService.REFRESH, claims, (List<String>)((CustomAuthentication)authentication).getRoles());

            response.addCookie( createCookie(JWTService.REFRESH, token, JWTService.UNLIMITED_TIME) );

            response.setStatus(HttpServletResponse.SC_OK);
        });

    }
}
