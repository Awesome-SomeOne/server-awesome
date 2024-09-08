package com.example.SomeOne.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/travel-records/**").permitAll() // 인증 없이 접근 허용
                                .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .httpBasic(Customizer.withDefaults()) // 기본 HTTP 인증 활성화
                .csrf(csrf -> csrf.disable()); // CSRF 보호 비활성화 (필요에 따라 조정)
        return http.build();
    }
}
