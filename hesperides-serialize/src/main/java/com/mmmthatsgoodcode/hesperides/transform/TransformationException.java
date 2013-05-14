package com.mmmthatsgoodcode.hesperides.transform;

public class TransformationException extends Exception {

	public TransformationException(Exception e) {
		super(e);
	}

	public TransformationException(String message) {
		super(message);
	}

	public TransformationException(String message, Exception e) {
		super(message, e);
	}

	
	
}
