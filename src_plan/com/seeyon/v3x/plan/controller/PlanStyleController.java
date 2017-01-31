package com.seeyon.v3x.plan.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Hibernate;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.plan.domain.PlanStyle;
import com.seeyon.v3x.plan.domain.PlanStyleBody;
import com.seeyon.v3x.plan.manager.PlanStyleManager;
import com.seeyon.v3x.util.Datetimes;

/**
 * 
 * <p/> Title:计划格式
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Feb 10, 2007 9:12:01 PM
 * </p>
 * 
 * @author T3
 * @version 1.0
 */
public class PlanStyleController extends BaseController {

	private PlanStyleManager planStyleManager;

	// @Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPlanStyleManager(PlanStyleManager planStyleManager) {
		this.planStyleManager = planStyleManager;
	}

	/**
	 * 列出查询列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listStyle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List styleList = planStyleManager.listPlanStyle();
		ModelAndView mav = new ModelAndView("plan/listStyle");
		mav.addObject("styleList", styleList);
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
	public ModelAndView initNewStyle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("plan/addStyle");
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
	public ModelAndView addStyle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PlanStyle style = new PlanStyle();
		PlanStyleBody planStyleBody = new PlanStyleBody();

		setStyleParameter(request, style, planStyleBody);

		planStyleManager.addPlanStyle(style);
		planStyleManager.addPlanStyleBody(planStyleBody,style);

		ModelAndView mav = new ModelAndView("plan/sSysMgr");
		mav.addObject("style", style);
		mav.addObject("body", planStyleBody);
		return mav;
	}

	/**
	 * 初始化更新
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView initUpdateStyle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 得到URL传送的ID
		String strId = request.getParameter("id");
		// 判断是否为数字，是数字取Style，不是返回null
		if (NumberUtils.isNumber(strId)) {
			PlanStyle style = planStyleManager
					.getPlanStyleByPk(new Long(strId));
			Hibernate.initialize(style.getPlanStyleBody());
			PlanStyleBody psb = style.getPlanStyleBody();
			ModelAndView mav = new ModelAndView("plan/editStyle");
			mav.addObject("style", style);
			mav.addObject("body", psb);
			return mav;
		} else
			return null;
	}
	
	//点击预览
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		 得到URL传送的ID
		String strId = request.getParameter("id");
		// 判断是否为数字，是数字取Style，不是返回null
		if (NumberUtils.isNumber(strId)) {
			PlanStyle style = planStyleManager
					.getPlanStyleByPk(new Long(strId));
			Hibernate.initialize(style.getPlanStyleBody());
			PlanStyleBody psb = style.getPlanStyleBody();
			String view="plan/content_template_list_detail_iframe";
			
			if(request.getParameter("oper")!=null){
				view="plan/showContent";
			}
			ModelAndView mav = new ModelAndView(view);
			mav.addObject("style", style);
			mav.addObject("body", psb);
			return mav;
		} else
			return null;
	}
	
	
	
	

	/**
	 * 更新
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateStyle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PlanStyle style = planStyleManager.getPlanStyleByPk(RequestUtils
				.getLongParameter(request, "id"));
		Hibernate.initialize(style.getPlanStyleBody());
		PlanStyleBody psb = style.getPlanStyleBody();
		if (psb == null) {
			psb = new PlanStyleBody();
		}
		setStyleParameter(request, style, psb);
		planStyleManager.updatePlanStyle(style);
		planStyleManager.updatePlanStyleBody(psb);
		ModelAndView mav = new ModelAndView("plan/sSysMgr");
		mav.addObject("style", style);
		mav.addObject("body", psb);
		return mav;
	}

	/**
	 * 删除
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView deleteStyle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] strIds = request.getParameterValues("id");
		if (strIds != null && strIds.length != 0) {
			for (int i = 0; i < strIds.length; i++) {
				if (NumberUtils.isNumber(strIds[i])) {
					Long planStyleId = new Long(strIds[i]);
					planStyleManager
							.deletePlanStyleBodyByPlanStyleId(planStyleId);
					planStyleManager.deletePlanStyle(planStyleId);
				}
			}
		}
		List styleList = planStyleManager.listPlanStyle();
		ModelAndView mav = new ModelAndView("plan/listStyle");
		mav.addObject("styleList", styleList);
		return mav;
	}

	/**
	 * 从Request中得到值，set入Style实体中。
	 * 
	 * @param request
	 * @param style
	 * @throws ServletRequestBindingException
	 */
	private void setStyleParameter(HttpServletRequest request,
			PlanStyle planStyle, PlanStyleBody planStyleBody) {
		Long accountId = CurrentUser.get().getLoginAccount();
		try {
			planStyle.setId(RequestUtils.getLongParameter(request, "id"));
		} catch (ServletRequestBindingException e) {
		}
		try {
			planStyle.setTitle(RequestUtils
					.getStringParameter(request, "title"));
		} catch (ServletRequestBindingException e) {
		}
		try {
			planStyle.setType(RequestUtils.getStringParameter(request, "type"));
		} catch (ServletRequestBindingException e) {
		}
		try {
			planStyle.setTextType(RequestUtils.getStringParameter(request,
					"bodyType"));
		} catch (ServletRequestBindingException e) {
		}
		try {
			planStyle.setAccountId(accountId);
		} catch (Exception e) {
		}
		try {
			planStyleBody.setContent(RequestUtils.getStringParameter(request,
					"content"));// 正文

			planStyleBody.setBodyType(RequestUtils.getStringParameter(request,
					"bodyType"));
			// bind(request, planStyleBody);
			Date bodyCreateDate = Datetimes.parseDatetime(request
					.getParameter("bodyCreateDate"));
			if (bodyCreateDate != null) {
				planStyleBody.setCreateDate(new Timestamp(bodyCreateDate
						.getTime()));
			}
			planStyleBody.setPlanStyle(planStyle);
		} catch (ServletRequestBindingException e) {
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}
}
