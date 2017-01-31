package com.seeyon.v3x.hr.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;

public class HrOrgMgrController extends BaseController {
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	/**
	 * 组织管理Home
	 */
	@CheckRoleAccess(roleTypes={RoleType.HrAdmin})
	public ModelAndView orgMgrHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/organization/orgMgrHome");
	}
	
	
}
