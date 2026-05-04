package com.helma.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator helmaRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("helma-auth-service", route -> route
                        .path("/api/auth/**", "/api/users/**")
                        .filters(filters -> filters
                                .addResponseHeader("X-Gateway", "helma-api-gateway")
                                .addResponseHeader("X-Service", "helma-auth-service")
                        )
                        .uri("http://helma-auth-service:8080"))

                .route("helma-crowdfunding-service", route -> route
                        .path("/api/crowdfunding/**", "/api/portfolio/**", "/health")
                        .filters(filters -> filters
                                .addResponseHeader("X-Gateway", "helma-api-gateway")
                                .addResponseHeader("X-Service", "helma-crowdfunding-service")
                        )
                        .uri("http://helma-crowdfunding-service:8080"))

                .build();
    }
}