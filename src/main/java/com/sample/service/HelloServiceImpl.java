package com.sample.service;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sample.exception.HelloException;
import com.sample.model.HelloModel;

@Service
public class HelloServiceImpl implements HelloService {

	private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

	public HelloModel generateMessage(Integer var) {
		logger.info("generateMessage() called");
		Integer id = new Random().nextInt();
		HelloModel helloModel = new HelloModel();
		try {
			if (var == 0) {
				logger.error("General exception");
				throw new RuntimeException("Unexpected runtime exception occured");
			} else if (var == 1) {
				helloModel.setId(id);
				helloModel.setDescription("exception handled: Random Id generated is " + id);
				throw new HelloException("Application handled Exception");
			} else {
				helloModel.setId(id);
				helloModel.setDescription("Random Id generated is " + id);
			}

		} catch (HelloException he) {
			logger.error(he.getMessage());
		}

		return helloModel;
	}
}
