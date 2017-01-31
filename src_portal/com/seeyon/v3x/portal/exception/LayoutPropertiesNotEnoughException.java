package com.seeyon.v3x.portal.exception;

/**
 * 缺少配置异常
 * @author dongyj
 *
 */
public class LayoutPropertiesNotEnoughException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5076397309625978298L;
	
	private String prop;
	
	public LayoutPropertiesNotEnoughException(String prop){
		this.prop = prop;
	}

	public String getProp() {
		return prop;
	}
	
}
