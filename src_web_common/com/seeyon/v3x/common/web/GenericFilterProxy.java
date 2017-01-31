/**
 * 
 */
package com.seeyon.v3x.common.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2011-4-7
 */
public abstract class GenericFilterProxy {
	
	private String[] urlPattern;
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return true:正常向下进行，false:停止
	 * @throws Exception
	 */
	public abstract boolean doFilter(HttpServletRequest request, HttpServletResponse response) throws Exception;
	

	public String[] getUrlPattern() {
		return this.urlPattern;
	}

	public void setUrlPattern(String[] urlPattern) {
		this.urlPattern = urlPattern;
	}
}
