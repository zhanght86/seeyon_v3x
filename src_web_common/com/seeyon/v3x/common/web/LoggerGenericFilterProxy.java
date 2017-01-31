/**
 * 
 */
package com.seeyon.v3x.common.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.seeyon.v3x.system.debug.InfoOpenManager;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2011-4-7
 */
public class LoggerGenericFilterProxy extends GenericFilterProxy {
	
	private InfoOpenManager infoOpenManager;
	
	public void setInfoOpenManager(InfoOpenManager infoOpenManager) {
		this.infoOpenManager = infoOpenManager;
	}

	public boolean doFilter(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(false);
		if(session == null || session.getAttribute("GoodLuckA8") == null){
			response.sendError(404);
			return false;
		}
		
		
		return true;
	}

}
