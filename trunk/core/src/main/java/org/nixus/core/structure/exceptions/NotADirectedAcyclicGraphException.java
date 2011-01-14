package org.nixus.core.structure.exceptions;

public class NotADirectedAcyclicGraphException extends RuntimeException {

	private static final long serialVersionUID = -4873721173189738808L;

	public NotADirectedAcyclicGraphException(String message) {
		super(message);
	}

}
