package com.seeyon.v3x.system.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.system.Constants;

@CheckRoleAccess(roleTypes={RoleType.Administrator})
public class WorkscopeController extends BaseController {

	private OrgManagerDirect orgManagerDirect;
	
	private AppLogManager appLogManager;
	
	public AppLogManager getAppLogManager() {
		return appLogManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}


	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

	/**
	 * 进入工作范围设置方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView workFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("sysMgr/worksarea/worksarea");
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		V3xOrgAccount account = orgManagerDirect.getAccountById(accountId);
		List<V3xOrgLevel> levels = orgManagerDirect.getAllLevels(CurrentUser.get().getLoginAccount(),false);
		int levelMax = 0;
		for (int i = 0; i < levels.size(); i++) {
			if (levels.get(i).getLevelId() > levelMax)
				levelMax = levels.get(i).getLevelId();
		}
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < levelMax-1; i++) {
			list.add(i);
		}
		
		result.addObject("list", list);
		result.addObject("levelMax", levelMax);
		result.addObject("listScope", account.getLevelScope());
		result.addObject("levels", levels);
		
		return result;
	}

	/**
	 * 对工作范围进行编辑
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView editWorkarea(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		int theId = Integer.valueOf(request.getParameter("id"));
		V3xOrgAccount account = null ;
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		account = orgManagerDirect.getAccountById(accountId);
		account.setLevelScope(theId);
		orgManagerDirect.updateEntity(account);
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('"+Constants.getString4CurrentUser("system.manager.ok")+"')");
		out.println("</script>");			
		appLogManager.insertLog(user, AppLogAction.Organization_UpdateWorkScope, user.getName());		
		return redirectModelAndView("/worksarea.do?method=workFrame");

	}


}
