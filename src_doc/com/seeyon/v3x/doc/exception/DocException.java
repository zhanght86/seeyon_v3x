package com.seeyon.v3x.doc.exception;

import com.seeyon.v3x.common.exceptions.BusinessException;

public class DocException extends BusinessException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6268821506145574538L;

	public DocException() {
		super();
	}
	
	public DocException(String message){
		super(message, new Exception());
	}

	public DocException(String errorCode, Object... errorArgs) {
		super(errorCode, errorArgs);
	}

	public DocException(Throwable cause, String errorCode,
			Object... errorArgs) {
		super(cause, errorCode, errorArgs);
	}
}
