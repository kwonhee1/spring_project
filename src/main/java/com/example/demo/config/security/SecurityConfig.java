package com.example.demo.config.security;

import com.example.demo.config.security.filters.CustomAccessTokenFilter;
import com.example.demo.config.security.provider.CustomJsonLoginDaoAuthenticationProvider;
import com.example.demo.config.security.filters.CustomJsonLoginFilter;
import com.example.demo.controller.URIMappers;
import com.example.demo.service.MemberService;
import com.example.demo.utils.JWTService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private JWTService jwtService;
    private PasswordEncoder passwordEncoder;

    @Autowired
     public SecurityConfig(ObjectMapper objectMapper, MemberService memberService,@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver, JWTService jwtService, PasswordEncoder passwordEncoder) {
         this.memberService = memberService;
         this.objectMapper = objectMapper;
         this.handlerExceptionResolver = resolver;
         this.jwtService = jwtService;
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
        http.addFilterAfter(customJsonLoginFilter(), LogoutFilter.class);
        http.addFilterBefore(customTokenFilter(), CustomJsonLoginFilter.class);
        return http.build();
    }

    // filter / provider
    @Bean
    public CustomJsonLoginDaoAuthenticationProvider daoAuthenticationProvider() {
        return new CustomJsonLoginDaoAuthenticationProvider(memberService, passwordEncoder).getLoginDaoAuthenticationProvider();
    }
    @Bean
    public CustomJsonLoginFilter customJsonLoginFilter() {
        return new CustomJsonLoginFilter(daoAuthenticationProvider(), objectMapper, authenticationEntryPoint(), jwtService);
    }
    @Bean
    public CustomAccessTokenFilter customTokenFilter(){
        return new CustomAccessTokenFilter( jwtService);
    }

    // entry point :: filter Handler에서 exception반환시 security밖으로 빼주는 역할 -> controller로 전송

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
