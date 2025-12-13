package com.example.currency_exchange;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class CurrencyExchangeSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/currency-exchange/**").permitAll()  // dozvoli javno
                .anyRequest().authenticated()
            )
            .httpBasic().disable();  // isključi basic auth ako ne želiš login

        return http.build();
    }
}
