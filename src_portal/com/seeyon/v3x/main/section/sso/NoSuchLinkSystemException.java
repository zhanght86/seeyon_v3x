/**
 * 
 */
package com.seeyon.v3x.main.section.sso;

import com.seeyon.v3x.common.exceptions.BusinessException;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 *
 */
public class NoSuchLinkSystemException extends BusinessException {

	private static final long serialVersionUID = -6875741647643367374L;


	public NoSuchLinkSystemException() {
	}

	/**
	 * @param message
	 */
	public NoSuchLinkSystemException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NoSuchLinkSystemException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NoSuchLinkSystemException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param errorCode
	 * @param errorArg
	 */
	public NoSuchLinkSystemException(String errorCode, Object... errorArg) {
		super(errorCode, errorArg);
	}

	/**
	 * @param cause
	 * @param errorCode
	 * @param errorArgs
	 */
	public NoSuchLinkSystemException(Throwable cause, String errorCode,
			Object... errorArgs) {
		super(cause, errorCode, errorArgs);
	}

}
