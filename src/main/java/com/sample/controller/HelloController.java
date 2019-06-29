package com.sample.controller;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sample.model.HelloError;
import com.sample.model.HelloModel;
import com.sample.service.HelloService;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.vavr.control.Try;

@RestController
public class HelloController {
	private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
	@Autowired
	HelloService helloService;
	@Autowired
	CircuitBreaker circuitBreaker;
	@Autowired
	Retry retry;

	@GetMapping(path = "message")
	public ResponseEntity<Object> getMessage(@RequestParam("var") Integer var) {

		Supplier<HelloModel> fetchHelloModel = () -> helloService.generateMessage(var);
		fetchHelloModel = CircuitBreaker.decorateSupplier(circuitBreaker, fetchHelloModel);
		fetchHelloModel = Retry.decorateSupplier(retry, fetchHelloModel);
		try {
			HelloModel helloModel = Try.ofSupplier(fetchHelloModel).recover(exception -> fallback(var, exception))
					.get();
			return new ResponseEntity<>(helloModel, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(new HelloError("1 leads to handled exception, 0 leads to unhandled"),
					HttpStatus.BAD_REQUEST);
		}
	}

	private HelloModel fallback(Integer var, Throwable throwable) {
		logger.error("Fallback method - " + throwable.getMessage());
		return new HelloModel(var, "Fallback Method invoked. var should not be " + var);
	}
}
