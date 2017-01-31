/**
 * 
 */
package com.seeyon.v3x.mobile;

import com.seeyon.v3x.common.exceptions.BusinessException;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-10-21
 */
public class MobileException extends BusinessException {

	private static final long serialVersionUID = 6865251492042497505L;

	/**
	 * 
	 */
	public MobileException() {
		super();
	}

	/**
	 * @param message
	 */
	public MobileException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MobileException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MobileException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param errorCode
	 * @param errorArg
	 */
	public MobileException(String errorCode, Object... errorArg) {
		super(errorCode, errorArg);
	}

	/**
	 * @param cause
	 * @param errorCode
	 * @param errorArgs
	 */
	public MobileException(Throwable cause, String errorCode,
			Object... errorArgs) {
		super(cause, errorCode, errorArgs);
	}

}
