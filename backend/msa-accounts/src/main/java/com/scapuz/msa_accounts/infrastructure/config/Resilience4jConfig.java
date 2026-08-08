package com.scapuz.msa_accounts.infrastructure.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jConfig {

        @Bean
        public CircuitBreakerConfig circuitBreakerConfig() {
                return CircuitBreakerConfig.custom()
                                .slidingWindowSize(10)
                                .failureRateThreshold(50)
                                .waitDurationInOpenState(Duration.ofSeconds(30))
                                .permittedNumberOfCallsInHalfOpenState(3)
                                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                                .minimumNumberOfCalls(5)
                                .recordExceptions(
                                                Exception.class)
                                .build();
        }

        @Bean
        public RetryConfig retryConfig() {
                return RetryConfig.custom()
                                .maxAttempts(3)
                                .intervalFunction(
                                                io.github.resilience4j.core.IntervalFunction
                                                                .ofExponentialBackoff(500, 2))
                                .retryExceptions(
                                                java.net.ConnectException.class,
                                                java.net.SocketTimeoutException.class,
                                                org.springframework.web.client.ResourceAccessException.class)
                                .build();
        }

        @Bean
        public TimeLimiterConfig timeLimiterConfig() {
                return TimeLimiterConfig.custom()
                                .timeoutDuration(Duration.ofSeconds(10)) // Max 10s per call
                                .cancelRunningFuture(true)
                                .build();
        }
}