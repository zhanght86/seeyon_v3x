package com.seeyon.v3x.plan.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.web.BaseController;

/**
 * 
 * <p/> Title:
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Feb 10, 2007 9:12:01 PM
 * </p>
 * 
 * @author T3
 * @version 1.0
 */
public class SystemManagerController extends BaseController {

	// @Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// return new ModelAndView("plan/slayout");
		return null;
	}

	public ModelAndView style(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return new ModelAndView("plan/sStyle");
	}

	public ModelAndView myPlan(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		ModelAndView mav = new  ModelAndView("plan/sMyPlan") ;
		mav.addObject("from", request.getParameter("from")) ;
        mav.addObject("planId", request.getParameter("planId")) ;
        mav.addObject("type", request.getParameter("type")) ;
        String calSelectedYear = request.getParameter("calSelectedYear");
        String calSelectedMonth = request.getParameter("calSelectedMonth");
        String calSelectedDate = request.getParameter("calSelectedDate");
        if (calSelectedYear == null || calSelectedYear.equals("") || calSelectedYear.equals("-1"))
        {
            Calendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            calSelectedYear = String.valueOf(cal.get(Calendar.YEAR));
            calSelectedMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
            calSelectedDate = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        }
       
        mav.addObject("calSelectedYear", calSelectedYear);
        mav.addObject("calSelectedMonth", calSelectedMonth);
        mav.addObject("calSelectedDate", calSelectedDate);
 		
		return mav;
	}
	
	public ModelAndView planMgrHome(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("plan/planMgrHomeEntry");
		String calSelectedYear = request.getParameter("calSelectedYear");
		String calSelectedMonth = request.getParameter("calSelectedMonth");
		String calSelectedDate = request.getParameter("calSelectedDate");
		if (calSelectedYear == null || calSelectedYear.equals("")
				|| calSelectedYear.equals("-1")) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(new Date());
			calSelectedYear = String.valueOf(cal.get(Calendar.YEAR));
			calSelectedMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
			calSelectedDate = String
					.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		}
		mav.addObject("calSelectedYear", calSelectedYear);
		mav.addObject("calSelectedMonth", calSelectedMonth);
		mav.addObject("calSelectedDate", calSelectedDate);
		mav.addObject("type", request.getParameter("type"));
		return mav;
		
	}
	//部门更多页面显示 1
	public ModelAndView planMoreHome(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String planDeptId = request.getParameter("planDeptId");
//		部门计划空间更多进入创建SESSION，取的前台传过来的部门ID
		request.getSession().setAttribute("plan.planDeptId",planDeptId );
	
		return new ModelAndView("plan/deptspace/planMoreHomeEntry");
	}
	
	public ModelAndView myPlanHome(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("plan/myPlanHomeEntry");
		String calSelectedYear = request.getParameter("calSelectedYear");
		String calSelectedMonth = request.getParameter("calSelectedMonth");
		String calSelectedDate = request.getParameter("calSelectedDate");
		if (calSelectedYear == null || calSelectedYear.equals("")
				|| calSelectedYear.equals("-1")) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(new Date());
			calSelectedYear = String.valueOf(cal.get(Calendar.YEAR));
			calSelectedMonth = String.valueOf(cal.get(Calendar.MONTH) + 1);
			calSelectedDate = String
					.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		}
		mav.addObject("calSelectedYear", calSelectedYear);
		mav.addObject("calSelectedMonth", calSelectedMonth);
		mav.addObject("calSelectedDate", calSelectedDate);
		mav.addObject("type", request.getParameter("type") == null ? 2 : request.getParameter("type"));
		return mav;
	}

	public ModelAndView planMgr(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("plan/sPlanMgr");
	}
	//部门更多页面显示 2
	public ModelAndView planMore(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("plan/sPlanMore");
	}
	
	public ModelAndView planHeader(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("plan/header");
	}
	
	public ModelAndView homeEntry(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("plan/homeEntry");
		return mav;
	}
	
	public ModelAndView planSysMgr(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("plan/sSysMgr");
	}
	
	public ModelAndView planSysMgrToolBar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("plan/sysMgrToolBar");
	}

	public ModelAndView authed(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("plan/sAuthed");
	}
}
