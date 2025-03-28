package klee.msvc.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class SampleGlobalFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("PRE: Executing filter before request");
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header("token", "Abc123") // Aquí se agrega el header
                .build();
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
            logger.info("POST: Executing filter after response");
            // TRADITIONAL WAY //
            String token = mutatedExchange.getRequest().getHeaders().getFirst("token");
            if (token != null) {
                logger.info("token: {}", token);
                exchange.getResponse().getHeaders().add("token", token);
            }

            // THE BEST WAY, THE REACTIVE WAY //
            Optional.ofNullable(mutatedExchange.getRequest().getHeaders().getFirst("token"))
                    .ifPresent(value -> {
                        logger.info("token: {}", value);
                        exchange.getResponse().getHeaders().add("token", value);
                    });

            exchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "red").build());
            exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        }));
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
