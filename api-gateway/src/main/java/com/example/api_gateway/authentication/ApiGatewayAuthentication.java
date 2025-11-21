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

    // security rules - prilagodi po potrebi
    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchange -> exchange
                    .pathMatchers(HttpMethod.POST).hasRole("ADMIN")
                    .pathMatchers("/currency-exchange/**").permitAll()
                    .pathMatchers("/currency-conversion/**").hasRole("USER")
                    .pathMatchers("/users/**").hasRole("ADMIN")
                    .anyExchange().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // load-balanced WebClient.Builder (koristi lb://service-name ili http://service-name)
    @Bean
    WebClient.Builder webClientBuilder(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        return WebClient.builder().filter(lbFunction);
    }

    @Bean
    ReactiveUserDetailsService reactiveUserDetailsService(WebClient.Builder webClientBuilder, BCryptPasswordEncoder encoder) {
        // koristimo service name "users-service" (registracija u Eureki obezbedi ovo)
        WebClient client = webClientBuilder.baseUrl("http://users-service").build();

        return username -> client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users/email")
                        .queryParam("email", username)
                        .build()
                )
                .retrieve()
                .bodyToMono(UserDto.class)
                .flatMap(dto -> Mono.just(User.withUsername(dto.getEmail())
                        .password(encoder.encode(dto.getPassword()))
                        .roles(dto.getRole())
                        .build()
                ));
    }

    @Bean
    BCryptPasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder();
    }
}
