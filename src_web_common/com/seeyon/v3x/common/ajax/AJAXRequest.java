
package com.seeyon.v3x.common.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-16
 */
public interface AJAXRequest {
	public String getServiceName();

	public String getMethodName();

	public Class[] getTypes();

	public Object[] getValues();

	public HttpServletRequest getServletRequest();

	public HttpServletResponse getServletResponse();
}
