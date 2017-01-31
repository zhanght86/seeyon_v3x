package com.seeyon.v3x.collaboration.exception;

import com.seeyon.v3x.common.exceptions.BusinessException;

public class ColException extends BusinessException {

	private static final long serialVersionUID = 1L;

	public ColException() {
		super();
	}

	public ColException(String message, Throwable cause) {
		super(message, cause);
	}

	public ColException(String message) {
		super(message);
	}

	public ColException(Throwable cause) {
		super(cause);
	}
	
	public ColException(Throwable cause, String errorCode,Object... object){
		super(cause, errorCode,object);
	}
	
}
