package com.example.apigateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    @Data
    public static class Config {
        // put the configuration properties
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

    public GlobalFilter() {
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            //Custom pre filter
            log.info("Global Prefilter base message : {}", config.getBaseMessage());
            log.info("IP : {}",request.getRemoteAddress().getAddress().getHostAddress());

            if (config.isPreLogger()) {
                log.info("Global Filter Start : {}", request.getId());
            }

            //Custom Post filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    log.info("Global Post Start : {}", response.getStatusCode());
                }
//                log.info("Custom Post filter : response id -> {}", response.getStatusCode());
            }));
        };
    }
}