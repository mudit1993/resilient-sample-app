package com.sample;

import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sample.model.HelloModel;
import com.sample.service.HelloService;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.control.Try;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class AppTest {

	@Autowired
	HelloService helloService;

	@Test
	public void whenRemoteServiceFails_thenCircuitBreakerIsUsed() {

		CircuitBreakerConfig config = CircuitBreakerConfig.custom().failureRateThreshold(20)
				.ringBufferSizeInClosedState(10).build();
		CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
		CircuitBreaker circuitBreaker = registry.circuitBreaker("circuit-breaker");
		Function<Integer, HelloModel> decorated = CircuitBreaker.decorateFunction(circuitBreaker,
				helloService::generateMessage);

		for (int i = 0; i < 10; i++) {
			try {
				decorated.apply(0);
			} catch (Exception ignore) {
			}
		}
		// after exceeding the threshold of failed invocations- circuit breaker state
		// changes from closed to open
		assert (circuitBreaker.getState().name()).equalsIgnoreCase("OPEN");
	}

	@Test
	public void whenRemoteServiceFails_thenRetry() {
		CircuitBreakerConfig cbconfig = CircuitBreakerConfig.custom().failureRateThreshold(50)
				.ringBufferSizeInClosedState(3).build();
		CircuitBreakerRegistry cbregistry = CircuitBreakerRegistry.of(cbconfig);
		CircuitBreaker circuitBreaker = cbregistry.circuitBreaker("circuit-breaker");

		RetryConfig config = RetryConfig.custom().maxAttempts(3).build();
		RetryRegistry registry = RetryRegistry.of(config);
		Retry retry = registry.retry("retry");

		Supplier<HelloModel> fetchHelloModel = () -> helloService.generateMessage(0);
		fetchHelloModel = CircuitBreaker.decorateSupplier(circuitBreaker, fetchHelloModel);
		fetchHelloModel = Retry.decorateSupplier(retry, fetchHelloModel);

		try {
			Try.ofSupplier(fetchHelloModel).get();
		} catch (Exception ignore) {
		}
		// Calling once. Retry will recall and the circuit will break open
		assert (circuitBreaker.getState().name()).equalsIgnoreCase("OPEN");
	}
}
