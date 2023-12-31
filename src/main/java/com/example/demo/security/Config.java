package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class Config {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(CsrfConfigurer::disable)
            .authorizeHttpRequests(x -> x.requestMatchers("/api/**").authenticated())
            .httpBasic(x -> x.authenticationEntryPoint((req, res, exc) -> {}))
            .sessionManagement(x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(new CustomFilterBean(), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

}