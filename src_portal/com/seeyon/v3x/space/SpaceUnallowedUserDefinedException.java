/**
 * 
 */
package com.seeyon.v3x.space;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-8-30
 */
public class SpaceUnallowedUserDefinedException extends SpaceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6453290726622692750L;

	/**
	 * 
	 */
	public SpaceUnallowedUserDefinedException() {
	}

	/**
	 * @param message
	 */
	public SpaceUnallowedUserDefinedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SpaceUnallowedUserDefinedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SpaceUnallowedUserDefinedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param errorCode
	 * @param errorArg
	 */
	public SpaceUnallowedUserDefinedException(String errorCode,
			Object... errorArg) {
		super(errorCode, errorArg);
	}

	/**
	 * @param cause
	 * @param errorCode
	 * @param errorArgs
	 */
	public SpaceUnallowedUserDefinedException(Throwable cause,
			String errorCode, Object... errorArgs) {
		super(cause, errorCode, errorArgs);
	}

}
