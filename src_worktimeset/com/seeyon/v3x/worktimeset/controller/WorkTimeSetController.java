package com.seeyon.v3x.worktimeset.controller;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseManageController;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.worktimeset.domain.WorkTimeCurrency;
import com.seeyon.v3x.worktimeset.manager.WorkTimeSetManager;
import com.seeyon.v3x.worktimeset.manager.WorkTimeSetManagerImpl;

@CheckRoleAccess(roleTypes={RoleType.Administrator,RoleType.GroupAdmin,RoleType.HrAdmin})
public class WorkTimeSetController extends BaseManageController {
	/** 资源文件定义 */
	private static String mRes = "com.seeyon.v3x.meetingroom.resources.i18n.WorkTimeSetResources";
	
	private static final Log log = LogFactory.getLog(WorkTimeSetController.class);

	private WorkTimeSetManager workTimeSetManager;

	public void setWorkTimeSetManager(WorkTimeSetManager workTimeSetManager) {
		this.workTimeSetManager = workTimeSetManager;
	}

	private AppLogManager appLogManager;

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	
	/**
	 * 日历查看页面
	 * 
	 * @param request
	 * @param response
	 * @return 转到viewbycalendar.jsp页面
	 * @throws Exception
	 */
	public ModelAndView viewByCalendar(HttpServletRequest request, HttpServletResponse response) throws Exception{
		User user = CurrentUser.get();
		Long orgAcconutID = user.getLoginAccount();
		boolean isGroupAdmin = user.isGroupAdmin();
		ModelAndView mav = new ModelAndView("worktimeset/viewbycalendar");

		String showGroupSpeSet = request.getParameter("showGroupSpeSet");
		if ("true".equals(showGroupSpeSet)) {
			orgAcconutID = WorkTimeSetManagerImpl.GROUP_ADMIN_ACCOUNT;
			isGroupAdmin = true;
			mav = new ModelAndView("worktimeset/groupSpecialWorkDaySetShow");
		}

		String year = request.getParameter("year");
		String month = request.getParameter("month");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		if(year != null && year.length() > 0){
			c.set(Calendar.YEAR, Integer.parseInt(year));
		}else{
			year = String.valueOf(c.get(Calendar.YEAR));
		}
		if(month != null && month.length() > 0){
			c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		}else{
			month = String.valueOf(c.get(Calendar.MONTH) + 1);
		}
		
		String comnRestDayStr = this.workTimeSetManager.findComnRestDaySet(
				year, month, orgAcconutID, isGroupAdmin);
		String specialWorkDayStr = this.workTimeSetManager.findSpecialWorkDaySet(year, month, orgAcconutID, isGroupAdmin);
		WorkTimeCurrency workTimeCurrency = this.workTimeSetManager
				.findComnWorkTimeSet(year, month, orgAcconutID, isGroupAdmin);
		StringBuffer workTime = new StringBuffer();
		workTime.append(workTimeCurrency.getAmWorkTimeBeginTime());
		workTime.append(",");
		workTime.append(workTimeCurrency.getAmWorkTimeEndTime());
		workTime.append(",");
		workTime.append(workTimeCurrency.getPmWorkTimeBeginTime());
		workTime.append(",");
		workTime.append(workTimeCurrency.getPmWorkTimeEndTime());

		mav.addObject("year", year);
		mav.addObject("month", month);
		mav.addObject("comnRestDayStr", comnRestDayStr);
		mav.addObject("specialWorkDayStr", specialWorkDayStr);
		mav.addObject("workTimeStr", workTime.toString());

		mav.addObject("isGroupAdmin", CurrentUser.get().isGroupAdmin());
		Calendar c1 = Calendar.getInstance();
		String year4checkString = String.valueOf(c1.get(Calendar.YEAR));
		String month4checkString = String.valueOf(c1.get(Calendar.MONTH) + 1);
		String day4checkString = String.valueOf(c1.get(Calendar.DAY_OF_MONTH));
		String systemDateNum = year4checkString + "/" + month4checkString + "/"
				+ day4checkString;
		mav.addObject("systemDateNum", systemDateNum);

		return mav;
	}
	
	
	/**
	 * 设置一般工作时间
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView setCurrencyWorkTime(HttpServletRequest request, HttpServletResponse response) throws Exception{
		User user = CurrentUser.get();
		Long orgAcconutID = user.getAccountId();
		boolean isGroupAdmin = user.isGroupAdmin();
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		// workDays : 格式如：1,0,0,1,0,1,1
		String workDays = request.getParameter("workDays");
		
		String workAmBeginTimeStr = request.getParameter("workAmBeginTime");
		String workAmEndTimeStr = request.getParameter("workAmEndTime"); 
		String workPmBeginTimeStr = request.getParameter("workPmBeginTime"); 
		String workPmEndTimeStr = request.getParameter("workPmEndTime"); 
		
		String copyCurrencyTimeFlagString = request
				.getParameter("copyCurrencyTimeFlag");
		// 是否复制工作时间设置
		boolean copyCurrencyTimeFlag = false;
		if ((!Strings.isBlank(copyCurrencyTimeFlagString))
				&& ("true".equals(copyCurrencyTimeFlagString))) {
			copyCurrencyTimeFlag = true;
		}

		// 记录应用日志
		String WorkTimeSetResources = "com.seeyon.v3x.worktimeset.resources.i18n.WorkTimeSetResources";
		// 工作时间设置
		String workTime = "";
		// 集团和各子单位工作时间
		String groupAndUnitWorkTime = "";

		if (copyCurrencyTimeFlag && (!isGroupAdmin)) {
			// 单位管理员选择了继承集团的时间设置
			this.workTimeSetManager.copyCurrenctTimeFormGroupToUnit(
					orgAcconutID, year, Integer.parseInt(month));
			this.workTimeSetManager.saveWorkTime(workAmBeginTimeStr.split(":"), workPmEndTimeStr.split(":"), orgAcconutID);
			workTime = ResourceBundleUtil.getString(WorkTimeSetResources,
					"mr.label.GroupWorkTime");
			appLogManager.insertLog(user,
					AppLogAction.WorkTimeSet_Inherit_Group_WorkTime,
					user.getName(), workTime);
			return null;
		}

		// 设置工作日期时间
		this.workTimeSetManager.updateComnWorkDayTimeSet(year, workDays,
				workAmBeginTimeStr, workAmEndTimeStr, workPmBeginTimeStr,
				workPmEndTimeStr, orgAcconutID, isGroupAdmin,
				copyCurrencyTimeFlag, Integer.parseInt(month));

		if (copyCurrencyTimeFlag && isGroupAdmin) {
			// 集团管理员 修改集团和各子单位工作时间
			groupAndUnitWorkTime = ResourceBundleUtil.getString(
					WorkTimeSetResources, "mr.label.GroupAndUnitWorkTime");
			appLogManager.insertLog(user,
					AppLogAction.WorkTimeSet_Update_Group_WorkTime, user
					.getName(), groupAndUnitWorkTime);
		}

		if (isGroupAdmin && !copyCurrencyTimeFlag) {
			// 集团管理员修改集团工作时间
			workTime = ResourceBundleUtil.getString(WorkTimeSetResources,
					"mr.label.GroupWorkTime");
			appLogManager.insertLog(user,
					AppLogAction.WorkTimeSet_Update_Group_WorkTime, user
					.getName(), workTime);
		} else if (!isGroupAdmin) {
			// 单位管理员修改单位工作时间
			workTime = ResourceBundleUtil.getString(WorkTimeSetResources,
					"mr.label.UnitWorkTime");
			appLogManager.insertLog(user,
					AppLogAction.WorkTimeSet_Update_Unit_WorkTime,
					user
					.getName(), workTime);
		}
		return null;
	}
	
	public ModelAndView openWorkTimeConfig(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		// workDays : 格式如：1001011
		String sComnRestDays = request.getParameter("sComnRestDays");
		String workTimeStr = request.getParameter("workTimeStr");

		String groupComnRestDayStr = this.workTimeSetManager
				.findComnRestDaySet(
				year, month, WorkTimeSetManagerImpl.GROUP_ADMIN_ACCOUNT, true);
		WorkTimeCurrency groupWorkTimeCurrency = this.workTimeSetManager
				.findComnWorkTimeSet(year, month,
						WorkTimeSetManagerImpl.GROUP_ADMIN_ACCOUNT, true);
		StringBuffer workTime = new StringBuffer();
		workTime.append(groupWorkTimeCurrency.getAmWorkTimeBeginTime());
		workTime.append(",");
		workTime.append(groupWorkTimeCurrency.getAmWorkTimeEndTime());
		workTime.append(",");
		workTime.append(groupWorkTimeCurrency.getPmWorkTimeBeginTime());
		workTime.append(",");
		workTime.append(groupWorkTimeCurrency.getPmWorkTimeEndTime());

		ModelAndView mav = new ModelAndView("worktimeset/worktimeconfig");
		mav.addObject("year", year);
		mav.addObject("month", month);
		mav.addObject("workDays", sComnRestDays);
		mav.addObject("workTimeStr", workTimeStr);
		mav.addObject("sComnRestDays", sComnRestDays);
		mav.addObject("isGroupAdmin", CurrentUser.get().isGroupAdmin());
		mav.addObject("groupComnRestDayStr", groupComnRestDayStr);
		mav.addObject("groupWorkTimeStr", workTime.toString());

		return mav;  
	}
	public ModelAndView toFrameHTML(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("worktimeset/frame");
		return mav;
		
	}
	public ModelAndView toViewbycalendarTop(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("worktimeset/viewbycalendarTop");
		return mav;
		
	}
}
