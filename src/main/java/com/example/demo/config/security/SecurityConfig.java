package com.example.demo.config.security;

import com.example.demo.config.security.filters.*;
import com.example.demo.controller.URIMappers;
import com.example.demo.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
     public SecurityConfig(ObjectMapper objectMapper, MemberService memberService,@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver, PasswordEncoder passwordEncoder) {
         this.memberService = memberService;
         this.objectMapper = objectMapper;
         this.handlerExceptionResolver = resolver;
         this.passwordEncoder = passwordEncoder;
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
                                .requestMatchers(URIMappers.MainPageURI, URIMappers.RegisterPageURI, URIMappers.LoginPageURI).permitAll()
                                .requestMatchers(URIMappers.UserPageURI).hasAuthority(URIMappers.UserRole)
                                .requestMatchers(URIMappers.AdminPageURI).hasRole(URIMappers.AdminRole)
                                .anyRequest().authenticated()
                )
//                .authorizeHttpRequests(
//                        new Customizer<AuthorizeHttpRequestsConfigurer<org.springframework.security.config.annotation.web.builders.HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>() {
//                            @Override
//                            public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizationManagerRequestMatcherRegistry) {
//                                authorizationManagerRequestMatcherRegistry
//                                        .requestMatchers("","","").permitAll();
//                            }
//                        }
//                )

                // oauth2Login social login 부분

                .logout(logoutConfigurer ->
                        logoutConfigurer.logoutSuccessUrl("/")
                )

                // 허가 받지 않은 uri접근시 권한 부족 error 전송하기
                ;
        // login logout filter 추가
            // json 객체
        http.addFilterBefore(customJsonLoginFilter(), LogoutFilter.class); // 4번쨰
        http.addFilterBefore(customRefreshFilter(), CustomJsonLoginFilter.class); // 3번째
        http.addFilterBefore(customAccessFilter(), CustomRefreshTokenFilter.class); // 2번쨰
        http.addFilterBefore(customBlackListFilter(), CustomAccessTokenFilter.class); // 1번쨰
        http.addFilterBefore(firstFilter(), CustomBlackListFilter.class); // 0번째
        return http.build();
    }

    // filter
    @Bean
    public CustomJsonLoginFilter customJsonLoginFilter() {
        return new CustomJsonLoginFilter(objectMapper, authenticationEntryPoint(), memberService);
    }
    @Bean
    public CustomFilter customAccessFilter() {
        return new CustomAccessTokenFilter();
    }

    @Bean
    public CustomFilter customRefreshFilter() {
        return new CustomRefreshTokenFilter(memberService);
    }

    @Bean
    public CustomFilter customBlackListFilter(){return new CustomBlackListFilter();}

    @Bean
    public AbstractAuthenticationProcessingFilter firstFilter(){
        return new FirstFilter();
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
