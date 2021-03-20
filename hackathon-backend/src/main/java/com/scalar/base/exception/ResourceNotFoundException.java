package com.scalar.base.exception;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6346592329172928814L;

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
