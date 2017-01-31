/**
 * 
 */
package com.seeyon.v3x.main.section.sso;

import com.seeyon.v3x.common.exceptions.BusinessException;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 *
 */
public class LoginFailingException extends BusinessException {

	private static final long serialVersionUID = 8681275174093252239L;

	public LoginFailingException() {
	}

	/**
	 * @param message
	 */
	public LoginFailingException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public LoginFailingException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public LoginFailingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param errorCode
	 * @param errorArg
	 */
	public LoginFailingException(String errorCode, Object... errorArg) {
		super(errorCode, errorArg);
	}

	/**
	 * @param cause
	 * @param errorCode
	 * @param errorArgs
	 */
	public LoginFailingException(Throwable cause, String errorCode,
			Object... errorArgs) {
		super(cause, errorCode, errorArgs);
	}

}
