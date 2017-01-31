/**
 * 
 */
package com.seeyon.v3x.space;

import com.seeyon.v3x.common.exceptions.BusinessException;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-7-6
 */
public class SpaceException extends BusinessException {

	private static final long serialVersionUID = -5476614270940543695L;

	public SpaceException() {
		super();
	}

	public SpaceException(String message) {
		super(message);
	}

	public SpaceException(Throwable cause) {
		super(cause);
	}

	public SpaceException(String message, Throwable cause) {
		super(message, cause);
	}

	public SpaceException(String errorCode, Object... errorArg) {
		super(errorCode, errorArg);
	}

	public SpaceException(Throwable cause, String errorCode,
			Object... errorArgs) {
		super(cause, errorCode, errorArgs);
	}

}
