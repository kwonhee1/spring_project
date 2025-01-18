package com.example.demo.config.security.filters;

import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.config.security.authentication.CustomAuthentication;
import com.example.demo.config.security.provider.CustomJsonLoginDaoAuthenticationProvider;
import com.example.demo.exception.http.CustomException;
import com.example.demo.exception.http.view.CustomMessage;
import com.example.demo.exception.http.view.CustomTitle;
import com.example.demo.model.Member;
import com.example.demo.config.security.util.jwt.JWTService;
import com.example.demo.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomJsonLoginFilter extends CustomFilter {

    private ObjectMapper objectMapper;
    private AuthenticationEntryPoint authenticationEntryPoint;
    private JWTService jwtService;
    private MemberService memberService;
    private PasswordEncoder passwordEncoder;

    public CustomJsonLoginFilter(ObjectMapper objectMapper, AuthenticationEntryPoint authenticationEntryPoint, JWTService jwtService) {
        super(new CustomRequestMatchers(CustomRequestMatchers.LoginPattern, "POST"));
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

    @Override
    protected Authentication provider(Authentication authentication){
        String email = ((CustomAuthentication) authentication).getEmail();
        String inputPasswd = (String) authentication.getCredentials();

        Member dbMember = memberService.login(email, inputPasswd);

        ((CustomAuthentication) authentication).setMemberId(dbMember.getId());
        ((CustomAuthentication) authentication).setRefreshLevel(dbMember.getRefreshLevel());

        ((CustomAuthentication) authentication).setAuthenticated(true);

        return authentication;
    }

    public void createCustomTokenFilter() {
        setAuthenticationFailureHandler((request, response, exception) -> {
            //throw exception;\
            exception.printStackTrace();
            authenticationEntryPoint.commence(request, response, exception);
        });
        setAuthenticationSuccessHandler((request, response, authentication) -> {
            String token =
                    jwtService.createToken(JWTService.REFRESH, JWTService.UNLIMITED_TIME)
                            .claim("email", (String)authentication.getPrincipal())
                            .claim("level" , ((CustomAuthentication)authentication).getRefreshLevel())
                            .build();

            response.addCookie( createCookie(JWTService.REFRESH, token, JWTService.UNLIMITED_TIME) );

            response.setStatus(HttpServletResponse.SC_OK);
        });
    }
}
