package com.example.demo.config.security;

import com.example.demo.config.security.CustomDaoAuthenticationProvder.CustomJsonLoginDaoAuthenticationProvider;
import com.example.demo.config.security.filters.CustomJsonLoginFilter;
import com.example.demo.exception.http.CustomException;
import com.example.demo.model.Role;
import com.example.demo.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {
    // for filter
    private MemberService memberService;
    private ObjectMapper objectMapper;
    private HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
     public SecurityConfig(ObjectMapper objectMapper, MemberService memberService,@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
         this.memberService = memberService;
         this.objectMapper = objectMapper;

         this.handlerExceptionResolver = resolver;
     }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Cross site Request forgery : disable
                .csrf((httpSecurityCsrfConfigurer ->
                        httpSecurityCsrfConfigurer.disable())
                )
                .formLogin(formLoginConfigurer ->
                        formLoginConfigurer
                                .disable()
                )
                .httpBasic(httpBasicConfigurer ->httpBasicConfigurer.disable())
                //
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer.frameOptions(frameOptionsConfig ->
                                frameOptionsConfig.disable())
                )
                // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .sessionManagement(SessionManagementConfigurer::disable)

                // exception handler 설정 authenticationEntryPotin
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer.authenticationEntryPoint(authenticationEntryPoint()))

                // 접근 설정
                .authorizeHttpRequests(authorizeHttpRequestsConfigurer ->
                        authorizeHttpRequestsConfigurer
                                .requestMatchers("/", "/Register", "/Login", "/login").permitAll()
                                .requestMatchers("/AdminPage").hasRole(Role.ADMIN.role)
                                .anyRequest().authenticated()
                )

                // oauth2Login social login 부분

                .logout(logoutConfigurer ->
                        logoutConfigurer.logoutSuccessUrl("/")
                )

                // 허가 받지 않은 uri접근시 권한 부족 error 전송하기
                ;
        // login logout filter 추가
            // json 객체
        http.addFilterAfter(customJsonLoginFilter(), LogoutFilter.class);
//        http.addFilterBefore()
        return http.build();
    }

    @Bean
    public CustomJsonLoginDaoAuthenticationProvider daoAuthenticationProvider() {
        return new CustomJsonLoginDaoAuthenticationProvider(memberService).getLoginDaoAuthenticationProvider();
    }

    @Bean
    public CustomJsonLoginFilter customJsonLoginFilter() {
        return new CustomJsonLoginFilter(daoAuthenticationProvider(), objectMapper, authenticationEntryPoint()).getCustomTokenFilter();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPoint(){
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                handlerExceptionResolver.resolveException(request, response, null, authException);
            }
        };
    }
}
