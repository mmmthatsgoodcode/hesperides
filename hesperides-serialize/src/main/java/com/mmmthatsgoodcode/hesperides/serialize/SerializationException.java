package com.mmmthatsgoodcode.hesperides.serialize;

public class SerializationException extends Exception {

	public SerializationException(Exception e) {
		super(e);
	}

	public SerializationException(String message) {
		super(message);
	}

	public SerializationException(String message, Exception e) {
		super(message, e);
	}

	
	
}
