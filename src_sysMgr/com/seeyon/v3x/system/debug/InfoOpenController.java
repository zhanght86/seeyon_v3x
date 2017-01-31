/**
 * 
 */
package com.seeyon.v3x.system.debug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.util.TextEncoder;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2011-2-16
 */
@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
public class InfoOpenController extends BaseController {
	
	private InfoOpenManager infoOpenManager;
	
	public void setInfoOpenManager(InfoOpenManager infoOpenManager) {
		this.infoOpenManager = infoOpenManager;
	}

	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("sysMgr/debug/infoOpen");
		
		InfoOpen infoOpen = this.infoOpenManager.get();
		
		mv.addObject("infoOpen", infoOpen);
		
		return mv;
	}
	
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean enabled = request.getParameterValues("enabled") != null;
		String endTime = request.getParameter("endTime");
		String password = request.getParameter("password");
		
		InfoOpen infoOpen = new InfoOpen();
		infoOpen.setEnabled(enabled);
		infoOpen.setEndTime(endTime);
		infoOpen.setPassword(TextEncoder.encode(password));
		
		this.infoOpenManager.save(infoOpen);
		
		return super.redirectModelAndView("/infoOpen.do?method=index");
	}
}
