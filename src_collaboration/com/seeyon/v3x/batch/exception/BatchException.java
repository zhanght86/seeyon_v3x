/**
 * 
 */
package com.seeyon.v3x.batch.exception;

/**
 * @author dongyj
 *
 */
public class BatchException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3227709373175788466L;
	
	private int errorCode;
	
	private String message;
	
	public BatchException(int code){
		this.errorCode = code;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public BatchException(int code,String message){
		this.errorCode = code;
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
