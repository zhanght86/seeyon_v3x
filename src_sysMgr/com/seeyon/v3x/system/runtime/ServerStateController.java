/**
 * 
 */
package com.seeyon.v3x.system.runtime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.ServerState;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-10-20
 */
@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
public class ServerStateController extends BaseController {

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.common.web.BaseController#index(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("sysMgr/runTime/server");
		
		ServerState instance = ServerState.getInstance();
		if(instance.isShutdown()){
			mv.addObject("isShutdown", true);
			mv.addObject("comment", instance.getComment());
			mv.addObject("minute", instance.getMinute());
			mv.addObject("autoExit", instance.isAutoExit());
		}
		
		return mv;
	}
	
	public ModelAndView doChanageState(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Integer minute = Integer.parseInt(request.getParameter("minute"));
		String comment = request.getParameter("comment");
		boolean autoExit = request.getParameterValues("autoExit") != null;
		
		if(comment == null){
			comment = "";
		}
		
		ServerState.getInstance().setStateShutdown(minute, comment, autoExit);
		
		return super.redirectModelAndView("/serverState.do?method=index");
	}
}
