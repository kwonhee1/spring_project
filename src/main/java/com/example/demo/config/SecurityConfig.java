package com.example.demo.config;

import com.example.demo.config.CustomDaoAuthenticationProvder.CustomJsonLoginDaoAuthenticationProvider;
import com.example.demo.config.filters.CustomJsonLoginFilter;
import com.example.demo.model.Member;
import com.example.demo.model.Role;
import com.example.demo.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {
    private MemberService memberService;
    private ObjectMapper objectMapper;

    @Autowired
     public SecurityConfig(ObjectMapper objectMapper, MemberService memberService) {
         this.memberService = memberService;
         this.objectMapper = objectMapper;
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
        return new CustomJsonLoginFilter(daoAuthenticationProvider(), objectMapper).getCustomTokenFilter();
    }
}
