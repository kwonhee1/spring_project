package com.example.demo.config;

import com.example.demo.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Cross site Request forgery : disable
                .csrf((httpSecurityCsrfConfigurer ->
                        httpSecurityCsrfConfigurer.disable())
                )
                //
                .headers(httpSecurityHeadersConfigurer ->
                        httpSecurityHeadersConfigurer.frameOptions(frameOptionsConfig ->
                                frameOptionsConfig.disable())
                )
                // 접근 설정
                .authorizeHttpRequests(authorizeHttpRequestsConfigurer ->
                        authorizeHttpRequestsConfigurer
                                .requestMatchers("/", "/login", "/Login").permitAll()
                                .requestMatchers("/AdminPage").hasRole(Role.ADMIN.role)
                                .anyRequest().authenticated()
                )
                .formLogin(formLoginConfigurer ->
                        formLoginConfigurer
                                .loginPage("/Login")
                                .usernameParameter("username")
                                .passwordParameter("passwd")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/", true)
                )
                .logout(logoutConfigurer ->
                        logoutConfigurer.logoutSuccessUrl("/")
                )

                // 허가 받지 않은 uri접근시 권한 부족 error 전송하기
                ;
        return http.build();
    }
}
