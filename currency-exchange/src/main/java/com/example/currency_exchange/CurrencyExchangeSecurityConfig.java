
package com.example.currency_exchange;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class CurrencyExchangeSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
            .anyRequest().authenticated()  // svaka ruta mora biti autentifikovana
            .and()
            .httpBasic(); // basic auth
        return http.build();
    }
}
