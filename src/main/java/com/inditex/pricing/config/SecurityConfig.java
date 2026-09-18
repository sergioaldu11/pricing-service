package com.inditex.pricing.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    @ConditionalOnProperty(name = "pricing.security.enabled", havingValue = "true")
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers("/actuator/health", "/actuator/health/**")
                                        .permitAll()
                                        .requestMatchers("/api/v1/prices", "/api/prices")
                                        .hasAuthority("SCOPE_pricing.read")
                                        .anyRequest()
                                        .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    @ConditionalOnProperty(
            name = "pricing.security.enabled",
            havingValue = "false",
            matchIfMissing = true)
    SecurityFilterChain localSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}
