package com.example.api_gateway.routing;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfiguration {

    @Bean
    RouteLocator gatewayRouting(RouteLocatorBuilder builder) {
        return builder.routes()

            // Currency Exchange (fiat → fiat)
            .route(p -> p.path("/api/currency-exchange/**")
            		.filters(f -> f.rewritePath("/api/currency-exchange(?<segment>/?.*)",
                            "/api/currency-exchange${segment}"))
                .uri("lb://currency-exchange"))

            // Currency Conversion (fiat → fiat)
            .route(p -> p.path("/api/currency-conversion/**")
                .filters(f -> f.rewritePath("/api/currency-conversion/(?<segment>.*)", "/${segment}"))
                .uri("lb://currency-conversion"))

            // Crypto Exchange
            .route(p -> p.path("/api/crypto-exchange/**")
                .filters(f -> f.rewritePath("/api/crypto-exchange/(?<segment>.*)", "/${segment}"))
                .uri("lb://crypto-exchange"))

            // Crypto Conversion
            .route(p -> p.path("/api/conversion/**")
                .filters(f -> f.rewritePath("/api/conversion/(?<segment>.*)", "/${segment}"))
                .uri("lb://crypto-conversion"))

            // Crypto Wallet
            .route(p -> p.path("/api/crypto-wallets/**")
                .filters(f -> f.rewritePath("/api/crypto-wallets/(?<segment>.*)", "/${segment}"))
                .uri("lb://crypto-wallet"))

            // Bank Accounts
            .route(p -> p.path("/api/bank-accounts/**")
                .filters(f -> f.rewritePath("/api/bank-accounts/(?<segment>.*)", "/${segment}"))
                .uri("lb://bankaccount"))

            // Trade Service
            .route(p -> p.path("/api/trade/**")
                .filters(f -> f.rewritePath("/api/trade/(?<segment>.*)", "/${segment}"))
                .uri("lb://tradeservice"))

            // Users Service
            .route(p -> p.path("/api/users/**")
                .filters(f -> f.rewritePath("/api/users/(?<segment>.*)", "/${segment}"))
                .uri("lb://users-service"))

            .build();
    }
}
