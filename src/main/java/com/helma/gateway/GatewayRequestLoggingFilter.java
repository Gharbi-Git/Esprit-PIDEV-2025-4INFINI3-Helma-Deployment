package com.helma.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayRequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GatewayRequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long start = System.currentTimeMillis();

        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getRawPath();
        String query = exchange.getRequest().getURI().getRawQuery();
        String requestId = exchange.getRequest().getId();

        String fullPath = query == null ? path : path + "?" + query;

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
                    int status = statusCode == null ? 0 : statusCode.value();
                    long duration = System.currentTimeMillis() - start;

                    log.info("[Gateway] requestId={} {} {} -> {} in {}ms",
                            requestId,
                            method,
                            fullPath,
                            status,
                            duration
                    );
                });
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}