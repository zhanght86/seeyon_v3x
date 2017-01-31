package com.seeyon.v3x.hr.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.domain.Page;
import com.seeyon.v3x.hr.domain.PageProperty;
import com.seeyon.v3x.hr.domain.Salary;
import com.seeyon.v3x.hr.manager.SalaryManager;
import com.seeyon.v3x.hr.manager.UserDefinedManager;
import com.seeyon.v3x.hr.util.SalaryUserDefinedHelper;
import com.seeyon.v3x.system.util.PwdStrengthValidationUtil;
import com.seeyon.v3x.util.Strings;

/**
 * 员工工资查看
 */
public class ViewSalaryController extends BaseController {
	private transient static final Log log = LogFactory.getLog(ViewSalaryController.class);

	private SalaryManager salaryManager;
	private UserDefinedManager userDefinedManager;

	public UserDefinedManager getUserDefinedManager() {
		return userDefinedManager;
	}

	public void setUserDefinedManager(UserDefinedManager userDefinedManager) {
		this.userDefinedManager = userDefinedManager;
	}

	public SalaryManager getSalaryManager() {
		return salaryManager;
	}

	public void setSalaryManager(SalaryManager salaryManager) {
		this.salaryManager = salaryManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	public ModelAndView viewSalary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/viewSalary/home");
	}

	public ModelAndView homeEntry(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/viewSalary/homeEntry");
	}

	public ModelAndView toolBar(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/viewSalary/toolbar");
	}

	public ModelAndView viewListSalary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/viewSalary/viewListSalary");
		
		try {
			String viewPage = "setSalaryPassword";
			if (salaryManager.hasSalaryPasswordRecord(CurrentUser.get().getId())) {
				viewPage = "inputSalaryPassword";
			}
			
			List<Page> hrPages = userDefinedManager.getPageByModelName("salary");
			Map<Long, List<PageProperty>> pageProperties = SalaryUserDefinedHelper.getPageProperties(this.userDefinedManager, hrPages);
			mav.addObject("hrPages", hrPages);
			mav.addObject("pageProperties", pageProperties);
			mav.addObject("propertyTypes", SalaryUserDefinedHelper.getPropertyTypes(request, this.userDefinedManager, pageProperties));
			mav.addObject("viewPage", viewPage);
			mav.addObject("salarys", null);
		} catch (Exception e) {
			log.error("工资查看：", e);
		}
		
		return mav;
	}

	@SuppressWarnings("unchecked")
	public ModelAndView viewData(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long userId = CurrentUser.get().getId();
		ModelAndView mav = new ModelAndView("hr/viewSalary/viewListSalary");
		
		List<Salary> salarys = new ArrayList<Salary>();
		String fromTime = RequestUtils.getStringParameter(request, "fromTime");
		String toTime = RequestUtils.getStringParameter(request, "toTime");
		if (Strings.isNotBlank(fromTime) && Strings.isNotBlank(toTime)) {
			String[] from = fromTime.split("-");
			String[] end = toTime.split("-");
			int fromYear = Integer.parseInt(from[0]);
			int fromMonth = Integer.parseInt(from[1]);
			int toYear = Integer.parseInt(end[0]);
			int toMonth = Integer.parseInt(end[1]);
			salarys = salaryManager.getSalaryByTime(userId, fromYear, fromMonth, toYear, toMonth);
		} else {
			salarys = salaryManager.findSalaryByStaffId(userId);
		}
		mav.addObject("salarys", salarys);
		mav.addObject("resultCount", salarys.size());
		
		List<Page> hrPages = userDefinedManager.getPageByModelName("salary");
		Map<Long, List<PageProperty>> pageProperties = SalaryUserDefinedHelper.getPageProperties(this.userDefinedManager, hrPages);
		mav.addObject("hrPages", hrPages);
		mav.addObject("pageProperties", pageProperties);
		mav.addObject("propertyTypes", SalaryUserDefinedHelper.getPropertyTypes(request, this.userDefinedManager, pageProperties));
		mav.addObject("propertyValues", SalaryUserDefinedHelper.getPropertyValues(userId, this.userDefinedManager, salarys, pageProperties));

		int size = Pagination.getRowCount();
		if (salarys != null) {
			if (size == 0) {
				size = salarys.size();
			}
		}

		int pageSize = NumberUtils.toInt(request.getParameter("pageSize"), Pagination.getMaxResults());
		if (pageSize < 1) {
			pageSize = Pagination.getMaxResults();
		}

		int pages = (size + pageSize - 1) / pageSize;
		if (pages < 1) {
			pages = 1;
		}

		int page = NumberUtils.toInt(request.getParameter("page"), 1);
		if (page < 1) {
			page = 1;
		} else if (page > pages) {
			page = pages;
		}

		mav.addObject("size", size);
		mav.addObject("pageSize", pageSize);
		mav.addObject("pages", pages);
		mav.addObject("page", page);

		return mav;
	}
	
	public ModelAndView setSalaryPassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/viewSalary/setSalaryPassword");
		// 读取是否启用密码强度检查
		mav.addObject("pwdStrengthValidation", PwdStrengthValidationUtil.getPwdStrengthValidationValue());
		return mav;
	}
	
	public ModelAndView newSalaryPassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/viewSalary/newSalaryPassword");
		// 读取是否启用密码强度检查
		mav.addObject("pwdStrengthValidation", PwdStrengthValidationUtil.getPwdStrengthValidationValue());
		return mav;
	}
	
	public ModelAndView inputSalaryPassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/viewSalary/inputSalaryPassword");
		// 读取是否启用密码强度检查
		mav.addObject("pwdStrengthValidation", PwdStrengthValidationUtil.getPwdStrengthValidationValue());
		return mav;
	}
	
	public ModelAndView setPassWord(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String password = request.getParameter("newPassword");
		try {
			salaryManager.setSalaryPasswordRecord(CurrentUser.get().getId(), password);
		} catch (Exception e) {
			PrintWriter out = response.getWriter();
			out.println("<Script>");
			out.println("parent.window.returnValue = null ;");
			out.println("parent.window.close();");
			out.println("</Script>");
			return null;
		}

		Boolean f = (Boolean) (BrowserFlag.PageBreak.getFlag(request));
		PrintWriter out = response.getWriter();
		out.println("<Script>");
		if (f) {
			out.println("parent.window.returnValue = 'true' ;");
			out.println("parent.window.close();");
		} else {
			out.println("parent.parent.functionBack('true');");
		}
		out.println("</Script>");
		return null;
	}
	
	public ModelAndView checkPassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String password = request.getParameter("newPassword");
		boolean checkWord = salaryManager.checkPassWord(CurrentUser.get().getId(), password);
		if (checkWord) {
			Boolean f = (Boolean) (BrowserFlag.PageBreak.getFlag(request));
			PrintWriter out = response.getWriter();
			out.println("<Script>");
			if (f) {
				out.println("parent.window.returnValue = 'true' ;");
				out.println("parent.window.close();");
			} else {
				out.println("parent.parent.functionBack('true');");
			}
			out.println("</Script>");
			return null;
		}
		String jsContent = getOutPutString("manager.oldword.rep");
		super.rendJavaScript(response, "alert('" + jsContent + "'); window.parent.document.getElementById('newPassword').value = \"\";");
		return null;
	}
	
	public ModelAndView updatePassWord(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String oldPassWord = request.getParameter("oldPassWord");
		boolean checkWord = salaryManager.checkPassWord(CurrentUser.get().getId(), oldPassWord);
		if (!checkWord) {
			String jsContent = getOutPutString("manager.oldword");
			super.rendJavaScript(response, "alert('" + jsContent + "')");
			return null;
		}
		String newPassword = request.getParameter("newPassword");
		if (salaryManager.updatePassWord(CurrentUser.get().getId(), newPassword)) {
			Boolean f = (Boolean) (BrowserFlag.OpenDivWindow.getFlag(request));
			if (f.booleanValue() == true) {
				super.rendJavaScript(response, "parent.window.close();");
			} else {
				super.rendJavaScript(response, "parent.parent.functionBack();");
			}
			return null;
		}
		return null;
	}

	public ModelAndView updatePersonsPassWord(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String newPassword = request.getParameter("newPassword");
		String updateMembers = request.getParameter("updateMembers");
		if (Strings.isBlank(newPassword) || Strings.isBlank(updateMembers)) {
			super.rendJavaScript(response, "parent.window.close();");
			return null;
		}
		if (salaryManager.updatePassWord(newPassword, updateMembers)) {
			super.rendJavaScript(response, "parent.window.close();");
			return null;
		}
		return null;
	}

	private String getOutPutString(String key) {
		String propResources = "com.seeyon.v3x.system.resources.i18n.SysMgrResources";
		return ResourceBundleUtil.getString(propResources, key);
	}
	
}