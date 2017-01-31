package com.seeyon.v3x.plan.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.plan.domain.PlanUserScope;
import com.seeyon.v3x.plan.manager.PlanUserScopeManager;

/**
 * 
 * <p/> Title:计划用户查看范围
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Feb 10, 2007 9:12:01 PM
 * </p>
 * 
 * @author T3
 * @version 1.0
 */
public class PlanUserScopeController extends BaseController {

	private PlanUserScopeManager planUserScopeManager;

	// @Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPlanUserScopeManager(
			PlanUserScopeManager planUserScopeManager) {
		this.planUserScopeManager = planUserScopeManager;
	}

	/**
	 * 列出查询列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listUserScope(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List userScopeList = planUserScopeManager.listPlanUserScope();
		ModelAndView mav = new ModelAndView("plan/listUserScope");
		mav.addObject("userScopeList", userScopeList);
		return mav;
	}

	/**
	 * 初始化新建
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView initNewUserScope(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("plan/addUserScope");
		return mav;
	}

	/**
	 * 添加
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView addUserScope(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PlanUserScope userScope = new PlanUserScope();

		setPlanUserScopeParameter(request, userScope);

		planUserScopeManager.addPlanUserScope(userScope);

//		ModelAndView mav = new ModelAndView("plan/editUserScope");
//		mav.addObject("detail", "yes");
		return super.refreshWorkspace();
	}

	/**
	 * 初始化更新
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView initUpdateUserScope(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PlanUserScope userScope = planUserScopeManager
					.getPlanUserScopeByPk(RequestUtils.getLongParameter(request, "id"));
		ModelAndView mav = new ModelAndView("plan/editUserScope");
		mav.addObject("userScope", userScope);
		return mav;
	}

	/**
	 * 更新
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateUserScope(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PlanUserScope userScope = planUserScopeManager
				.getPlanUserScopeByPk(RequestUtils.getLongParameter(request,
						"id"));
		setPlanUserScopeParameter(request, userScope);
		planUserScopeManager.updatePlanUserScope(userScope);
		/*ModelAndView mav = new ModelAndView("plan/editUserScope");
		mav.addObject("detail", "yes");*/
	return	this.redirectModelAndView("/planSystemMgr.do?method=planSysMgr&toolbarType=UserScope","parent");
//		return mav;
	}

	/**
	 * 删除
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView deleteUserScope(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] strIds = request.getParameterValues("id");
		if (strIds != null && strIds.length != 0) {
			for (int i = 0; i < strIds.length; i++) {
				if (NumberUtils.isNumber(strIds[i])) {
					Long PlanUserScopeId = new Long(strIds[i]);
					planUserScopeManager.deletePlanUserScope(PlanUserScopeId);
				}
			}
		}
		List styleList = planUserScopeManager.listPlanUserScope();
		ModelAndView mav = new ModelAndView("plan/listUserScope");
		mav.addObject("userScopeList", styleList);
		return mav;
	}

	/**
	 * 从Request中得到值，set入PlanUserScope实体中。
	 * 
	 * @param request
	 * @param style
	 * @throws ServletRequestBindingException
	 */
	private void setPlanUserScopeParameter(HttpServletRequest request,
			PlanUserScope PlanUserScope) {
		try {
			PlanUserScope.setId(RequestUtils.getLongParameter(request, "id"));
		} catch (ServletRequestBindingException e) {
		}
			PlanUserScope.setRefAccountId(CurrentUser.get().getLoginAccount());
		try {
			PlanUserScope.setRefUserId(RequestUtils.getLongParameter(request,
					"refUserId"));
		} catch (ServletRequestBindingException e) {
		}
		try {
			PlanUserScope.setRefUserName(RequestUtils.getStringParameter(
					request, "refUserName"));
		} catch (ServletRequestBindingException e) {
		}
		try {
			PlanUserScope.setScopeUserIds(RequestUtils.getStringParameter(
					request, "scopeUserIds"));
		} catch (ServletRequestBindingException e) {
		}
		try {
			PlanUserScope.setScopeUserNames(RequestUtils.getStringParameter(
					request, "scopeUserNames"));
		} catch (ServletRequestBindingException e) {
		}
		try {
			PlanUserScope.setIsSeeDetail(RequestUtils.getBooleanParameter(
					request, "isSeeDetail"));
		} catch (ServletRequestBindingException e) {
		}
	}
}
