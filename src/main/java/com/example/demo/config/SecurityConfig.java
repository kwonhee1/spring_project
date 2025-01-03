package com.example.demo.config;

import com.example.demo.config.security_filters.CustomJsonLoginFilter;
import com.example.demo.config.security_filters.CustomTokenFilter;
import com.example.demo.model.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {
    ObjectMapper objectMapper;

    @Autowired
     public SecurityConfig(ObjectMapper objectMapper) {
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
//        http.addFilterAfter(customFilter(), LogoutFilter.class);
//        http.addFilterBefore()
        return http.build();
    }

    @Bean
    public CustomJsonLoginFilter customJsonLoginFilter() {
        CustomJsonLoginFilter filter = new CustomJsonLoginFilter(objectMapper);
        filter.setAuthenticationManager();
        filter.setAuthenticationSuccessHandler((request, response, authentication) -> {});
        filter.setAuthenticationFailureHandler((request, response, exception) -> {});
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
