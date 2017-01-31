package com.seeyon.v3x.flowperm.exception;

import com.seeyon.v3x.common.exceptions.BusinessException;

public class FlowPermException extends BusinessException {
	
	private static final long serialVersionUID = 1L;

	public FlowPermException() {
		// TODO Auto-generated constructor stub
	}

	public FlowPermException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public FlowPermException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public FlowPermException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public FlowPermException(String errorCode, Object... errorArg) {
		super(errorCode, errorArg);
		// TODO Auto-generated constructor stub
	}

	public FlowPermException(Throwable cause, String errorCode,
			Object... errorArgs) {
		super(cause, errorCode, errorArgs);
		// TODO Auto-generated constructor stub
	}

}
