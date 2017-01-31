/**
 * 
 */
package com.seeyon.v3x.plugin.identification;

import com.seeyon.v3x.common.exceptions.BusinessException;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2008-1-16
 */
public class NoSuchIndentificationDogException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3536529831431926786L;

	/**
	 * 
	 */
	public NoSuchIndentificationDogException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public NoSuchIndentificationDogException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public NoSuchIndentificationDogException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NoSuchIndentificationDogException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param errorCode
	 * @param errorArg
	 */
	public NoSuchIndentificationDogException(String errorCode,
			Object... errorArg) {
		super(errorCode, errorArg);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 * @param errorCode
	 * @param errorArgs
	 */
	public NoSuchIndentificationDogException(Throwable cause, String errorCode,
			Object... errorArgs) {
		super(cause, errorCode, errorArgs);
		// TODO Auto-generated constructor stub
	}

}
