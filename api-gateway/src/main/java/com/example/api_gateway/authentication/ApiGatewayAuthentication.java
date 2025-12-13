package com.example.api_gateway.authentication;

import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

import api.dtos.UserDto;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class ApiGatewayAuthentication {

	@Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) {

        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchange -> exchange

                /** --------------------------
                 *  PUBLIC ENDPOINTS
                 *  -------------------------- */
            	.pathMatchers("/api/currency-exchange").permitAll()
            	.pathMatchers("/api/currency-exchange/**").permitAll()
                .pathMatchers("/crypto-exchange/**").permitAll()

                /** --------------------------
                 *  USER-ONLY ENDPOINTS
                 *  -------------------------- */

                // Currency conversion (fiat → fiat)
                .pathMatchers("/api/currency-conversion").hasRole("USER")
                .pathMatchers("/api/currency-conversion/**").hasRole("USER")

                // Crypto conversion (BTC → ETH itd.)
                .pathMatchers(HttpMethod.POST, "/api/conversion").hasRole("USER")

                // Trade service (kupovina/prodaja kripta)
                .pathMatchers("/api/trade/**").hasRole("USER")

                /** --------------------------
                 *  SHARED RESOURCES
                 *  USER, OWNER, ADMIN
                 *  -------------------------- */

                // Wallets
                .pathMatchers("/wallet/**").hasAnyRole("USER", "OWNER", "ADMIN")

                // Bank accounts
                .pathMatchers("/bank-accounts/**").hasAnyRole("USER", "OWNER", "ADMIN")

                /** --------------------------
                 *  ADMIN-ONLY ENDPOINTS
                 *  -------------------------- */
                .pathMatchers("/users/**").hasRole("ADMIN")

                /** --------------------------
                 * EVERYTHING ELSE → AUTH REQUIRED
                 * -------------------------- */
                .anyExchange().authenticated()
            )

            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // Load balanced WebClient (Eureka support)
    @Bean
    WebClient.Builder webClientBuilder(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        return WebClient.builder().filter(lbFunction);
    }

    // Remote user lookup from UsersService
    @Bean
    ReactiveUserDetailsService reactiveUserDetailsService(
            WebClient.Builder webClientBuilder,
            BCryptPasswordEncoder encoder
    ) {
        WebClient client = webClientBuilder.baseUrl("http://users-service").build();

        return username ->
                client.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/users/email")
                                .queryParam("email", username)
                                .build())
                        .retrieve()
                        .bodyToMono(UserDto.class)
                        .map(dto ->
                                User.withUsername(dto.getEmail())
                                        .password(dto.getPassword())
                                        .roles(dto.getRole())
                                        .build()
                        );
    }

    @Bean
    BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}

