package com.antkorwin.ioutils.error;



public class InternalException extends RuntimeException {

	public InternalException(Throwable throwable) {
		super(throwable);
	}

	public InternalException(String message) {
		super(message);
	}

	public InternalException(String message, Throwable throwable) {
		super(message, throwable);
	}
}