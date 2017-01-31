package com.seeyon.v3x.common.ajax;

import com.seeyon.v3x.common.exceptions.BusinessException;


/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-16
 */
public class AJAXException extends BusinessException {
	private static final long serialVersionUID = -4376678428829148831L;

	public AJAXException() {
		super();
	}

	public AJAXException(String errorCode, Object... errorArg) {
		super(errorCode, errorArg);
	}

	public AJAXException(String message, Throwable cause) {
		super(message, cause);
	}

	public AJAXException(String message) {
		super(message);
	}

	public AJAXException(Throwable cause, String errorCode, Object... errorArgs) {
		super(cause, errorCode, errorArgs);
	}

	public AJAXException(Throwable cause) {
		super(cause);
	}

}
