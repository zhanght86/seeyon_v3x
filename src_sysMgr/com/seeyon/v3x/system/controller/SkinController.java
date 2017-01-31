package com.seeyon.v3x.system.controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.skin.SkinType;
import com.seeyon.v3x.skin.manager.SkinManager;
import com.seeyon.v3x.system.Constants;

@CheckRoleAccess(roleTypes = RoleType.SystemAdmin)
public class SkinController extends BaseController {

	private SkinManager skinManager;

	public void setSkinManager(SkinManager skinManager) {
		this.skinManager = skinManager;
	}

	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("sysMgr/skin/setting");

		SkinType skinType = skinManager.get();
		mav.addObject("skinType", skinType);

		return mav;
	}

	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String skinSuffix = request.getParameter("skinSuffix");

		SkinType skinType = new SkinType();
		skinType.setSkinSuffix(skinSuffix);
		skinManager.save(skinType);
		
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("	alert('" + Constants.getString4CurrentUser("system.manager.ok") + "');");
		out.println("</script>");
		out.flush();

		return super.redirectModelAndView("/skinManager.do?method=index");
	}

}