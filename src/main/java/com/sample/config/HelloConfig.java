package com.sample.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

@Configuration
public class HelloConfig {
	@Bean
	public CircuitBreaker circuitBreaker() {
		CircuitBreakerConfig config = CircuitBreakerConfig.custom()
				// Percentage of failures to start short-circuit - 40% in this case
				.failureRateThreshold(50)
				// Min. calls in half-open state
				.ringBufferSizeInHalfOpenState(5).enableAutomaticTransitionFromOpenToHalfOpen()
				// Min number of call attempts
				.ringBufferSizeInClosedState(8).waitDurationInOpenState(Duration.ofSeconds(15)).build();

		// We create a CircuitBreaker object and call the remote service through it:
		CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
		return registry.circuitBreaker("circuit-breaker");
	}

	@Bean
	public RateLimiter rateLimiter() {
		RateLimiterConfig config = RateLimiterConfig.custom().limitForPeriod(2).build();
		RateLimiterRegistry registry = RateLimiterRegistry.of(config);
		return registry.rateLimiter("rate-limiter");
	}
	@Bean
	public Bulkhead bulkHead() {
		BulkheadConfig config = BulkheadConfig.custom().maxConcurrentCalls(1).build();
		BulkheadRegistry registry = BulkheadRegistry.of(config);
		return registry.bulkhead("bulk-head");
	}
	@Bean
	public Retry retry() {
		RetryConfig config = RetryConfig.custom().maxAttempts(3).build();
		RetryRegistry registry = RetryRegistry.of(config);
		return registry.retry("retry");
	}
}
