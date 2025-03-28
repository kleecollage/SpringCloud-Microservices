package klee.msvc.items;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AppConfig {

    @Bean
    Customizer<Resilience4JCircuitBreakerFactory> customizerCircuitBreaker() {
        return (factory) -> factory.configureDefault(id -> {
            return new Resilience4JConfigBuilder(id).circuitBreakerConfig(
                    CircuitBreakerConfig.custom()
                            .slidingWindowSize(10) /* SIZE OF REQUEST */
                            .failureRateThreshold(50) /* PERCENTAGE OF FAILS ALLOW */
                            .waitDurationInOpenState(Duration.ofSeconds(10L)) /* TIME TO RECONNECTION */
                            .permittedNumberOfCallsInHalfOpenState(5) /* NUM OF SUCCESS CALLS TO PASS FROM HALF OPEN TO CLOSE */
                            .slowCallDurationThreshold(Duration.ofSeconds(2L)) /* MAX TIMEOUT ALLOWED TO REQUESTS */
                            .slowCallRateThreshold(50) /* PERCENTAGE OF TIMEOUT FAILS ALLOW */
                            .build())
                    .timeLimiterConfig(TimeLimiterConfig.custom()
                            .timeoutDuration(Duration.ofSeconds(4L)) /* MAX TIMEOUT TO REQUESTS (since is priority, this must be higher than slowCallDurationThreshold) */
                            .build())
                    .build();
        });
    }
}
