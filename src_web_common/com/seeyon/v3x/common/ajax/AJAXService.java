package com.seeyon.v3x.common.ajax;

/**
 * Performs invocation of the actual AJAX request and returns a result object to
 * converted into XML.
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-9-16
 */
public interface AJAXService {
	
	/**
	 * 处理AJAX请求，并把数据放置在response中
	 * 
	 * @param request
	 * @return
	 * @throws AJAXException
	 */
	AJAXResponse processRequest(AJAXRequest request) throws AJAXException;
}
