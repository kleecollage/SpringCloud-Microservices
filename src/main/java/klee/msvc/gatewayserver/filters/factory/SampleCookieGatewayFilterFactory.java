package klee.msvc.gatewayserver.filters.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class SampleCookieGatewayFilterFactory extends AbstractGatewayFilterFactory<ConfigurationCookie> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SampleCookieGatewayFilterFactory() {
        super(ConfigurationCookie.class);
    }

    @Override
    public GatewayFilter apply(ConfigurationCookie config) {
        return (exchange, chain) -> {
            logger.info("PRE: Executing pre gateway filter factory: {}", config.getMessage());
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                Optional.ofNullable(config.getValue()).ifPresent(cookie -> {
                    exchange.getResponse().addCookie(ResponseCookie.from(config.getName(), cookie).build());
                });
                logger.info("POST: Executing post gateway filter factory: {}", config.getMessage());
            }));
        };
    }

//    ORDER THE FILTER VARS
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("message", "name", "value");
    }

//    RENAME FILTER
    @Override
    public String name() {
        return "ExampleCookie";
    }
}
