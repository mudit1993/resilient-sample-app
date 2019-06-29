package com.sample.exception;

public class HelloException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HelloException() {
		super();
	}

	public HelloException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HelloException(String message, Throwable cause) {
		super(message, cause);
	}

	public HelloException(String message) {
		super(message);
	}

	public HelloException(Throwable cause) {
		super(cause);
	}

}
