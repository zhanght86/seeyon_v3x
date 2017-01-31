package com.seeyon.v3x.log.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.logonlog.domain.LogonLog;
import com.seeyon.v3x.common.logonlog.manager.LogonLogManager;
import com.seeyon.v3x.common.online.OnlineRecorder;
import com.seeyon.v3x.common.online.OnlineUser;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.log.manager.LogonLogHelper;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.manager.OnLineManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.principal.PrincipalManager;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

@CheckRoleAccess(roleTypes={RoleType.SystemAdmin,RoleType.AuditAdmin})
public class LogonLogController extends BaseController {
	
	private static Log log = LogFactory.getLog(LogonLogController.class);
	private LogonLogManager logonLogManager;
	private FileToExcelManager fileToExcelManager;
	private OrgManager orgManager;
	private AppLogManager appLogManager;
	private SystemConfig systemConfig;
	private PrincipalManager principalManager;
	private OnLineManager onLineManager;

	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}

	public void setOnLineManager(OnLineManager onLineManager) {
		this.onLineManager = onLineManager;
	}

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	public void setLogonLogManager(LogonLogManager logonLogManager) {
		this.logonLogManager = logonLogManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public ModelAndView summaryStat(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mv = new ModelAndView("log/logonLog/summaryStat");
		String startDay = request.getParameter("startDay");
		String endDay = request.getParameter("endDay");
		Date[] dates = this.getDateTime(startDay, endDay);
		Date startTime = dates[0];
		Date endTime = dates[1];
		long totalAccess = this.logonLogManager.getTotalAccess(-1, startTime, endTime);
		mv.addObject("totalAccess", totalAccess);
		
		startDay =  Datetimes.formatDatetime(startTime);
		mv.addObject("beginStatTime", startDay);
		
		startDay = Datetimes.formatDate(startTime);
		mv.addObject("fromDate", startDay);
		endDay = Datetimes.formatDate(endTime);
		mv.addObject("toDate", endDay);
		
		long days = Datetimes.minusDay(endTime, startTime);
		mv.addObject("days", days+1);
		
		String maxAccess = this.logonLogManager.getMaxAccessMonth(-1, startTime, endTime);
		mv.addObject("maxAccess", maxAccess);
		
		double avgAccess = totalAccess/(days + 1);
		mv.addObject("avgAccess", avgAccess);
		
		mv.addObject("startDay", startDay==null||"".equals(startDay)?Datetimes.format(startTime, Datetimes.dateStyle):startDay);
		mv.addObject("endDay", endDay==null||"".equals(endDay)?Datetimes.format(endTime, Datetimes.dateStyle):endDay);
		return mv;
	}
	
	/**
	 * 时长统计
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView onlineTimeStat(HttpServletRequest request,HttpServletResponse response) throws Exception{
	    ModelAndView mv = new ModelAndView("log/logonLog/onlineTimeStatList");
        String show = request.getParameter("show"); 
        if(!"list".equals(show)){//在线时长顶页面,无需后面的查询
            mv = new ModelAndView("log/logonLog/onlineTimeStat");
            return mv;
        }  
	    String systemFlag = "GROUP" ;
	    if(SysFlag.sys_isEnterpriseVer.getFlag().equals(true) || SysFlag.sys_isGovVer.getFlag().equals(true)){
	        systemFlag = "ORG" ;
	    }
	    mv.addObject("systemFlag", systemFlag) ;
		String strUsers = request.getParameter("users");
		Long[] users = this.getUserCondition(strUsers);
		String startDay = request.getParameter("startDay");
		String endDay = request.getParameter("endDay");
		Date[] dates = this.getDateTime(startDay, endDay);
		Date startTime = dates[0];
		Date endTime = dates[1];
		int desc = this.getDesc(request.getParameter("desc"));
		List<String[]> totalOnlineTime = this.logonLogManager.getTotalOnlineTime(-1, users, startTime, endTime, desc);
		mv.addObject("totalOnlineTime", totalOnlineTime);
		mv.addObject("startDay", startDay==null||"".equals(startDay)?Datetimes.format(startTime, Datetimes.dateStyle):startDay);
		mv.addObject("endDay", endDay==null||"".equals(endDay)?Datetimes.format(endTime, Datetimes.dateStyle):endDay);
		return mv;
	}
	
	/**
	 * 时长统计下载
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exportOnlineTimeToExcel(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String strUsers = request.getParameter("oldUsers");
		Long[] users = this.getUserCondition(strUsers);
		String startDay = request.getParameter("oldStartDay");
		String endDay = request.getParameter("oldEndDay");
		Date[] dates = this.getDateTime(startDay, endDay);
		Date startTime = dates[0];
		Date endTime = dates[1];
		int desc = this.getDesc(request.getParameter("oldDesc"));
		List<String[]> totalOnlineTime = this.logonLogManager.getAllTotalOnlineTime(-1, users, startTime, endTime, desc);
		String resource = "com.seeyon.v3x.log.resources.i18n.LogResource";
		Locale locale = LocaleContext.getLocale(request);
		if (totalOnlineTime != null && totalOnlineTime.size() > 10000) {
			String alert = ResourceBundleUtil.getString(resource, locale, "logon.excel.maxLenth");
			PrintWriter writer = response.getWriter();
			writer.println("<script>");
			writer.println("alert('"+alert+"')");
			writer.println("</script>");
			writer.flush();
			return null;
		}
		String name = ResourceBundleUtil.getString(resource, locale, "logon.onlineTimeStat.label");
		//人员
		String stat_Person = ResourceBundleUtil.getString(resource, locale, "logon.stat.person");
		String account = ResourceBundleUtil.getString(resource, locale, "log.toolbar.title.account");
		String leave = ResourceBundleUtil.getString(resource, locale, "logon.org.post");
		boolean isGroupVer = (Boolean)SysFlag.sys_isGroupVer.getFlag();
		//在线时长总计
		String stat_Time = ResourceBundleUtil.getString(resource, locale, "logon.stat.onlineTime");
		String[] columnName = null ;
		if(isGroupVer){
			columnName = new String[]{stat_Person,account, leave,stat_Time};
		}else{
			columnName = new String[]{stat_Person,leave,stat_Time} ;	
		}
		List<String[]> datas = new ArrayList<String[]>();
		if(totalOnlineTime != null && totalOnlineTime.size() > 0) {
			for(String[] t : totalOnlineTime) {
				String[] d = new String[columnName.length] ;
				if(t[0] == null){
					d[0] = Functions.showMemberName(1);
				}else{
					d[0] = Functions.showMemberNameOnly(Long.parseLong(t[0]));
				}
				int index = 0 ;
				if(isGroupVer){
					d[1] = Functions.showOrgAccountNameByMemberid(Long.parseLong(t[0])) ;
					index = 1 ;
 				} 
				d[index + 1] = Functions.showOrgPostNameByMemberid(Long.parseLong(t[0])) ;
				d[index + 2] = t[1];
				datas.add(d);
			}
		}
		fileToExcelManager.save(request, response, name, LogonLogHelper.exportToExcel(name, datas, columnName));
		return null;
	}
	
	public ModelAndView detailList(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mv = new ModelAndView("log/logonLog/detailList");
		String userId = request.getParameter("userId");
		String startDay = request.getParameter("startDay");
		String endDay = request.getParameter("endDay");
		Date[] dates = this.getDateTime(startDay, endDay);
		Date startTime = dates[0];
		Date endTime = dates[1];
		List<LogonLog> result = this.logonLogManager.getDetailList(Long.parseLong(userId),startTime,endTime);
		String loginName = Functions.getUserName(Long.parseLong(userId));
		//取得人员的在线信息
		OnlineUser onlineUser = OnlineRecorder.getOnlineUser(loginName);
		Long onlineLog = null;
		if(onlineUser != null){
			for (LogonLog log : result) {
				if((log.getLogonTime().getTime()/1000== onlineUser.getLoginTime().getTime()/1000)){
					onlineLog = log.getId();
					break;
				}
			}
		}
		mv.addObject("onlineLog", onlineLog);
		mv.addObject("now", new Date());
		mv.addObject("results", result);
		mv.addObject("startDay", startDay==null||"".equals(startDay)?Datetimes.format(startTime, Datetimes.dateStyle):startDay);
		mv.addObject("endDay", endDay==null||"".equals(endDay)?Datetimes.format(endTime, Datetimes.dateStyle):endDay);
		return mv;
	}
	
	/**
	 * 系统管理员明细日志导出,修改此方法时 请注意修改 detailList（）这个方法，此方法由这个方法衍生来
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exportExcelSys(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mv = new ModelAndView("log/logonLog/detailList");
		String userId = request.getParameter("userId");
		String startDay = request.getParameter("startDay");
		String endDay = request.getParameter("endDay");
		Date[] dates = this.getDateTime(startDay, endDay);
		Date startTime = dates[0];
		Date endTime = dates[1];
		
		Long id[] = new Long[1];
		id[0] = Long.parseLong(userId);
		List<LogonLog> result = logonLogManager.getAllLogonLogs(id, startTime, endTime, null);
//		List<LogonLog> result = this.logonLogManager.getDetailList(,startTime,endTime);
		String loginName = Functions.getUserName(Long.parseLong(userId));
		//取得人员的在线信息
		OnlineUser onlineUser = OnlineRecorder.getOnlineUser(loginName);
		Long onlineLog = null;
		if(onlineUser != null){
			for (LogonLog log : result) {
				if((log.getLogonTime().getTime()/1000== onlineUser.getLoginTime().getTime()/1000)){
					onlineLog = log.getId();
					break;
				}
			}
		}
		mv.addObject("onlineLog", onlineLog);
		mv.addObject("now", new Date());
		mv.addObject("results", result);
		mv.addObject("startDay", startDay==null||"".equals(startDay)?Datetimes.format(startTime, Datetimes.dateStyle):startDay);
		mv.addObject("endDay", endDay==null||"".equals(endDay)?Datetimes.format(endTime, Datetimes.dateStyle):endDay);
		if (result != null && result.size() > 10000) {
			String resource = "com.seeyon.v3x.log.resources.i18n.LogResource";
			Locale locale = LocaleContext.getLocale(request);
			String alert = ResourceBundleUtil.getString(resource, locale, "logon.excel.maxLenth");
			PrintWriter writer = response.getWriter();
			writer.println("<script>");
			writer.println("alert('"+alert+"')");
			writer.println("</script>");
			writer.flush();
			return null;
		}
		
		String processLog = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", "processLog.list.title.label") ;
    	
    	String person = ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource", "logon.stat.person") ;
    	String account = ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource", "log.toolbar.title.account") ;
    	String post = ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource", "logon.org.post") ;
    	String logonTime = ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource", "logon.search.logonTime") ;
    	String logoutTime = ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource", "logon.search.logoutTime") ;
    	String onlineTime = ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource", "logon.search.onlineTime") ;
    	String ip = ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource", "logon.search.ip") ;
    	String logonType = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "message.online.loginType") ;
    	String remark = ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource", "logon.search.remark") ;
    	String[] columnName = {person,account,post,logonTime,logoutTime,onlineTime,ip,logonType,remark} ;
    	
    	DataRecord dataRecord = new DataRecord() ;
    	dataRecord.setSheetName(processLog) ;
    	dataRecord.setColumnName(columnName) ;
    	
    	for (int i = 0 ; i < result.size() ; i ++) {
    		LogonLog logonLog = result.get(i) ;
    		DataRow dataRow = new DataRow();
    		dataRow.addDataCell(Functions.showMemberNameOnly(logonLog.getMemberId()), DataCell.DATA_TYPE_TEXT) ; 
    		dataRow.addDataCell(Functions.showOrgAccountNameByMemberid(logonLog.getMemberId()), DataCell.DATA_TYPE_TEXT) ;
    		dataRow.addDataCell(Functions.showOrgPostNameByMemberid(logonLog.getMemberId()), DataCell.DATA_TYPE_DATE) ;
    		dataRow.addDataCell(Datetimes.format(logonLog.getLogonTime(), Datetimes.datetimeStyle), DataCell.DATA_TYPE_TEXT) ;
    		dataRow.addDataCell(Datetimes.format(logonLog.getLogoutTime(), Datetimes.datetimeStyle), DataCell.DATA_TYPE_TEXT) ;
    		
    		// 在线时长
    		long onTime = (logonLog.getLogoutTime().getTime() - logonLog.getLogonTime().getTime())/(1000*60) ;
    		int hour = (int) onTime/60;
    		int min = (int) onTime%60;
    		dataRow.addDataCell(hour+"小时"+min+"分", DataCell.DATA_TYPE_TEXT) ;
    		
    		dataRow.addDataCell(logonLog.getIpAddress(), DataCell.DATA_TYPE_TEXT) ;

    		// 登录方式
    		dataRow.addDataCell(ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", "online.loginType."+logonLog.getLoginType()), DataCell.DATA_TYPE_TEXT) ;
    		
    		// 备注
    		if (logonLog.getLogoutType() !=null && logonLog.getLogoutType().intValue() == 0) {
    			dataRow.addDataCell(ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource","logon.search.logoutNoError"), DataCell.DATA_TYPE_TEXT) ;
    		} else if (onlineLog != null && onlineLog.equals(logonLog.getId())) {
    			dataRow.addDataCell(ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource","logon.search.online"), DataCell.DATA_TYPE_TEXT) ;
    		} else {
    			dataRow.addDataCell(ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource","logon.search.logoutWithError"), DataCell.DATA_TYPE_TEXT) ;
    		}
    		
    		dataRecord.addDataRow(dataRow);
    		dataRow = null;
    	}
    	fileToExcelManager.saveAsCSV(request,response,processLog,dataRecord);
		
		return null ;
	}
	
	public ModelAndView detailListMain(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mv = new ModelAndView("log/logonLog/detailListMain");
		return mv;
	}
	
	public ModelAndView detailSearch(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("log/logonLog/detailSearch");
		if(CurrentUser.get().isAuditAdmin()){ //审计管理员界面
			mv = new ModelAndView("log/logonLog/logDetail");
		}
		String strUsers = request.getParameter("users");
		Long[] users = this.getUserCondition(strUsers);
	    String systemFlag = "GROUP" ;
	    if(SysFlag.sys_isEnterpriseVer.getFlag().equals(true) || SysFlag.sys_isGovVer.getFlag().equals(true)){
	       // sysFlag = true ;
	        systemFlag = "ORG";
	    }
	    mv.addObject("systemFlag", systemFlag);		
		String startDay = request.getParameter("startDay");
		String endDay = request.getParameter("endDay");
		Date[] dates = this.getDateTime(startDay, endDay);
		Date startTime = dates[0];
		Date endTime = dates[1];
		
		String ipAddress = request.getParameter("ipAddress");
		
		List<LogonLog> detail = new ArrayList<LogonLog>();
		String isExprotExcel = request.getParameter("isExprotExcel") ;
		
		if(Strings.isNotBlank(isExprotExcel) && "true".equals(isExprotExcel)) {
			detail = this.logonLogManager.getDetail(-1, users, startTime, endTime, ipAddress,false) ;
			String resource = "com.seeyon.v3x.log.resources.i18n.LogResource";
			Locale locale = LocaleContext.getLocale(request);
			String name = ResourceBundleUtil.getString(resource, locale, "logon.detailSearch.label");
			String[] getColName = getColName(resource,locale,request) ;
			if (detail != null && detail.size() > 10000) {
				String alert = ResourceBundleUtil.getString(resource, locale, "logon.excel.maxLenth");
				PrintWriter writer = response.getWriter();
				writer.println("<script>");
				writer.println("alert('"+alert+"')");
				writer.println("</script>");
				writer.flush();
				return null;
			}
			try{
				List<String[]> detailStrings = getDetail(locale,detail,getColName.length) ;
				exprotExcel (name,getColName,detailStrings,request, response) ;
			}catch(Exception e){
				log.error("导出Excel", e) ;
			}
			
			return null ;
		}
		List<LogonLog> detailList = this.logonLogManager.getDetail(-1, users, startTime, endTime, ipAddress);
		if(detailList!=null && detailList.size()>0){
		for (LogonLog logonLog : detailList) {
			LogonLog logon = new LogonLog();
			PropertyUtils.copyProperties(logon, logonLog);
			boolean isSameLogin = false;
			try {
				Long memberId = logon.getMemberId();
				String loginName = "";
				if (memberId == 1) {
					loginName = orgManager.getSystemAdmin().getLoginName();
				} else if (memberId == 0) {
					loginName = orgManager.getAuditAdmin().getLoginName();
				} else {
					loginName = principalManager.getLoginNameByMemberId(memberId);
				}
				isSameLogin = onLineManager.isSameLogin(loginName, logon.getLogonTime());
			} catch (Exception e) {
				log.error("", e);
			}
			if (isSameLogin) {
				logon.setLogoutType(1);
				logon.setOnlineTime((int) (((new Date()).getTime() - logon.getLogonTime().getTime()) / 60000));
			}
			detail.add(logon);
		}
		}
		mv.addObject("detail", detail);
		mv.addObject("now", new Date());
		mv.addObject("startDay", startDay==null||"".equals(startDay)?Datetimes.format(startTime, Datetimes.dateStyle):startDay);
		mv.addObject("endDay", endDay==null||"".equals(endDay)?Datetimes.format(endTime, Datetimes.dateStyle):endDay);
		return mv;
	}
	
	private List<String[]> getDetail(Locale locale,List<LogonLog> list ,int length) throws Exception{
		boolean isGroupVer = (Boolean)SysFlag.sys_isGroupVer.getFlag();
		int index = 0 ;
		List<String[]> returnObjects = new ArrayList<String[]>() ;
		if (list != null) {
			for (LogonLog logonLog : list) {
				String[] data = new String[length];
				data[0] = Functions.showMemberName(logonLog.getMemberId());
				if (isGroupVer) {
					index = 1;
					V3xOrgAccount account = orgManager.getAccountById(logonLog.getAccountId());
					if (account != null) {
						if (account.getId().equals(1L) || account.getId().equals(Functions.getGroup().getId())) {
							data[1] = "-";
						} else {
							data[1] = account.getName();
						}
					} else {
						data[1] = "-";
					}
				}
				OnlineUser user = Functions.getOnlineUser(logonLog.getMemberId());
				int onlineTime = logonLog.getOnlineTime();
				String logErrorMessage = "";
				boolean isThis = false;
				if(user != null){
					if((logonLog.getLogonTime().getTime()/1000== user.getLoginTime().getTime()/1000)){
						isThis = true;
					}
				}
				if(isThis){
					onlineTime = new Long((new Date().getTime() - logonLog.getLogoutTime().getTime())/(60000)).intValue();
					data[3 + index] = "--";
					logErrorMessage = ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource",locale, "logon.search.online");
				}else{
					data[3 + index] = Datetimes.formatDatetimeWithoutSecond(logonLog.getLogoutTime());
					String lable = "logon.search.logoutWithError";
					if (logonLog.getLogoutType() != null && logonLog.getLogoutType().intValue() == 0) {
						lable = "logon.search.logoutNoError";
					}
					logErrorMessage =  ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource",locale, lable);
				}
				data[1 + index] = Functions.showOrgPostNameByMemberid(logonLog
						.getMemberId());
				data[2 + index] = Datetimes.formatDatetimeWithoutSecond(logonLog.getLogonTime());
				data[4 + index] = (onlineTime / 60) + "小时"+ (onlineTime % 60) + "分";
				
				data[5 + index] = logonLog.getIpAddress();
				data[6 + index] = ResourceBundleUtil.getString(
						"com.seeyon.v3x.main.resources.i18n.MainResources",
						locale, "online.loginType." + logonLog.getLoginType());
				data[7 + index] = logErrorMessage;
				returnObjects.add(data);
				data = null;
			}
		}
		return returnObjects ;
	}
	
	private String[] getColName(String resource,Locale locale,HttpServletRequest request) {
		String[] columnName = null ;		
		boolean isGroupVer = (Boolean)SysFlag.sys_isGroupVer.getFlag();
		String stat_Person = ResourceBundleUtil.getString(resource, locale, "logon.stat.person");
		String leave = ResourceBundleUtil.getString(resource, locale, "logon.org.post");
		String stat_Time = ResourceBundleUtil.getString(resource, locale, "logon.search.logonTime");
		String logoutTime = ResourceBundleUtil.getString(resource, locale, "logon.search.logoutTime");
		String onlineTime = ResourceBundleUtil.getString(resource, locale, "logon.search.onlineTime");
		String ip = ResourceBundleUtil.getString(resource, locale, "logon.search.ip");
		String loginType = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources", locale, "message.online.loginType");
		String remark = ResourceBundleUtil.getString(resource, locale, "logon.search.remark");
		if(isGroupVer){
			String account = ResourceBundleUtil.getString(resource, locale ,"log.toolbar.title.account");
			columnName = new String[]{stat_Person,account,leave,stat_Time,logoutTime,onlineTime,ip,loginType,remark} ;	
		}else{
			columnName = new String[]{stat_Person,leave,stat_Time,logoutTime,onlineTime,ip,loginType,remark} ;	
		}
		
		return columnName ;
	}
	
	private void exprotExcel(String fileName,String[] columnName,List<String[]> detail ,
			HttpServletRequest request,HttpServletResponse response)throws Exception{			
		fileToExcelManager.save(request, response, fileName, LogonLogHelper.exportToExcel(fileName, detail, columnName));	
	}
	
	private  String[] getUnlogColName(String resource,Locale locale,HttpServletRequest request) {
		String[] columnName = null ;		
		
		String stat_Person = ResourceBundleUtil.getString(resource, locale, "logon.stat.person");
		String lasLogonTime = ResourceBundleUtil.getString(resource, locale, "logon.search.lasLogonTime");
		String account = ResourceBundleUtil.getString(resource, locale, "log.toolbar.title.account");
		String leave = ResourceBundleUtil.getString(resource, locale, "logon.org.post");
		String state = ResourceBundleUtil.getString(resource, locale, "logon.search.user.state");
		if((Boolean)SysFlag.sys_isGroupVer.getFlag()){
			columnName = new String[]{stat_Person,account,leave,lasLogonTime,state} ;
		}else{
			columnName = new String[]{stat_Person,leave,lasLogonTime,state} ;
		}
			
		return columnName ;		
	}
	
	private List<String[]> getUnlogRecord(Locale locale,List<Object[]> list ,int length){
		List<String[]> returnObjects = new ArrayList<String[]>() ;		
		for(Object[] obj : list){
			String[] data = new String[length] ;
			data[0] = Functions.showMemberNameOnly((Long)obj[0]) ;
			int  index = 0 ;
			//data[0] = Functions.showMemberName((Long)obj[0]) ;
			if((Boolean)SysFlag.sys_isGroupVer.getFlag()){
				index = 1 ; 
				data[1] = Functions.showOrgAccountNameByMemberid((Long)obj[0]) ;
			}
			data[1 + index] =  Functions.showOrgPostNameByMemberid((Long)obj[0]) ;
			//data[1] = Functions.showMemberName((Long)obj[1]) ;
			if(obj[1] == null){
				data[2+index] = ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource", locale, "logon.noLogRecord");
			}else{
				data[2+index] = Datetimes.formatDatetime((Date)obj[1]);
			}
			if(Functions.getMember((Long)obj[0]).getState() == 1){
				data[3+index] = ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource", locale, "logon.user.state.1");
			} else {
				data[3+index] = ResourceBundleUtil.getString("com.seeyon.v3x.log.resources.i18n.LogResource", locale, "logon.user.state.2");
			}
			returnObjects.add(data) ;
			data = null;
		}
		return returnObjects ;
	}
	
	public ModelAndView unlogSearch(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mv = new ModelAndView("log/logonLog/unlogSearch");
		String startDay = request.getParameter("startDay");
		String endDay = request.getParameter("endDay");
		Date[] dates = this.getDateTime(startDay, endDay);
		Date startTime = dates[0];
		Date endTime = dates[1];
		
		List<Object[]> results = null ;
		String isExprotExcel = request.getParameter("isExprotExcel") ;
		
		if(Strings.isNotBlank(isExprotExcel) && "true".equals(isExprotExcel)) {
			results = this.logonLogManager.getUnlog(-1, startTime,endTime, false);
			String resource = "com.seeyon.v3x.log.resources.i18n.LogResource";
			Locale locale = LocaleContext.getLocale(request);
			String name = ResourceBundleUtil.getString(resource, locale, "logon.unlogonSearch.label");
			String[] getColName = getUnlogColName(resource,locale,request) ;
			List<String[]> detailStrings = getUnlogRecord(locale,results,getColName.length) ;
			try{
				exprotExcel (name,getColName,detailStrings,request, response) ;
			}catch(Exception e){
				log.error("导出Excel", e) ;
			}
			
			return null ;
		}
		
		results = this.logonLogManager.getUnlog(-1, startTime,endTime);
		mv.addObject("results", results);
		mv.addObject("startDay", startDay==null||"".equals(startDay)?Datetimes.format(startTime, Datetimes.dateStyle):startDay);
		mv.addObject("endDay", endDay==null||"".equals(endDay)?Datetimes.format(endTime, Datetimes.dateStyle):endDay);
		return mv;
	}
	
	
	/**
	 * 登录日志导出Excel
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exportExcel(HttpServletRequest request,HttpServletResponse response)throws Exception{
		User user = CurrentUser.get();
		if(user.isAuditAdmin()){
			String strUsers = request.getParameter("users");
			Long[] users = this.getUserCondition(strUsers);	
			String startDay = request.getParameter("startDay");
			String endDay = request.getParameter("endDay");
			Date[] dates = this.getDateTime(startDay, endDay);
			Date startTime = dates[0];
			Date endTime = dates[1];
			String ipAddress = request.getParameter("ipAddress");
			List<LogonLog> logonLogs = this.logonLogManager.getAllLogonLogs(users, startTime, endTime, ipAddress);
			
			String commonResource = "com.seeyon.v3x.log.resources.i18n.LogResource";
			String title = ResourceBundleUtil.getString(commonResource ,"logonLog.excel.count.title");
			Locale locale = LocaleContext.getLocale(request);
			String[] getColName = getColName(commonResource,locale,request) ;
			try{
				List<String[]> detailStrings = getDetail(locale,logonLogs,getColName.length) ;
				exprotExcel (title,getColName,detailStrings,request, response) ;
			}catch(Exception e){
				log.error("导出Excel", e) ;
			}
			/**
			boolean isGroupVer = (Boolean)SysFlag.sys_isGroupVer.getFlag();
			int colNums = isGroupVer? 8 : 7;
			String[] columnName = new String[colNums];
			int index = 0;
			columnName[index] = ResourceBundleUtil.getString(commonResource ,"logon.stat.person");
			if(isGroupVer){
				columnName[++index] = ResourceBundleUtil.getString(commonResource ,"log.toolbar.title.account");
			}
			columnName[++index] = ResourceBundleUtil.getString(commonResource ,"logon.org.leave");
			columnName[++index] = ResourceBundleUtil.getString(commonResource ,"logon.search.logonTime");
			columnName[++index] = ResourceBundleUtil.getString(commonResource ,"logon.search.logoutTime");
			columnName[++index] = ResourceBundleUtil.getString(commonResource ,"logon.search.onlineTime");
			columnName[++index] = ResourceBundleUtil.getString(commonResource ,"logon.search.ip");
			columnName[++index] = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources" ,"message.online.loginType");
			columnName[++index] = ResourceBundleUtil.getString(commonResource ,"logon.search.remark");
			V3xOrgAccount account = null;
			String logoutSucess = ResourceBundleUtil.getString(commonResource ,"logon.search.logoutNoError");
			String logoutFailure = ResourceBundleUtil.getString(commonResource ,"logon.search.logoutWithError");
			List<Object[]> rows = new ArrayList<Object[]>();
			if(logonLogs != null && !logonLogs.isEmpty()){
				Long rootAccountId = Functions.getGroup().getId();
				for(LogonLog log : logonLogs) {
					index = 0;
					Object[] obj = new Object[colNums];
					obj[index] = Functions.showMemberNameOnly(log.getMemberId());
					if(isGroupVer){
						account = orgManager.getAccountById(log.getAccountId());
						if(account != null){
							if(account.getId().equals(rootAccountId)){
								obj[++index] = "-";	
							}else{
								obj[++index] = account.getName();
							}
						}
						else{
							obj[++index] = "-";						
						}
					}
					
					obj[++index] = Functions.showMemberLeave(log.getMemberId());
					
					if(log.getLogonTime() != null){
						obj[++index] = Datetimes.format(log.getLogonTime(), "yyyy-MM-dd HH:mm");
					}else{
						obj[++index] = log.getLogonTime();
					}
					if(log.getLogoutTime() != null ){
						obj[++index] = Datetimes.format(log.getLogoutTime(), "yyyy-MM-dd HH:mm");
					}else{					
						obj[++index] = log.getLogoutTime();
					}
					
					obj[++index] = Datetimes.formatMins2TimeStr(log.getOnlineTime());
					obj[++index] = log.getIpAddress();
					obj[++index] = ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources" ,"online.loginType."+ log.getLoginType());
					if(log.getLogoutType()!=null && log.getLogoutType().equals(0)){
						obj[++index] = logoutSucess;
					}
					else{
						obj[++index] = logoutFailure;					
					}
					rows.add(obj);
				}
			}
			ColHelper.exportToExcel(request, response, fileToExcelManager, "登录日志", rows, columnName, title, "sheet1");
		**/
		}
		//super.rendJavaScript(response, "parent.exportOK()");
		return null;			
	}
	
	/**
	 * 审计管理员 - 删除日志
	 * 规则：删除‘保留期限’前XX月的日志
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView clearLogs(HttpServletRequest request,HttpServletResponse response) throws Exception{
		User user = CurrentUser.get();
		boolean isAuditAdmin = user.isAuditAdmin();
		if(isAuditAdmin){
			String logDeadline = systemConfig.get(IConfigPublicKey.LOG_Deadline_Login);
			if(Strings.isNotBlank(logDeadline)){
				int line = Integer.parseInt(logDeadline); //保存期限
				String clearMonth = request.getParameter("dateCondition"); //XX月前
				if(Strings.isNotBlank(clearMonth)){
					line += Integer.parseInt(clearMonth);
				}
				Date clearDate = Datetimes.addMonth(new Date(), -line);
				this.logonLogManager.clearLogsBeforeDate(clearDate);
				String dateStr = Datetimes.format(clearDate, Datetimes.dateStyle);
				appLogManager.insertLog(user, AppLogAction.Clear_Log_Logon, user.getName(), dateStr);
				try {
					PrintWriter pw = response.getWriter();
					pw.println("<script>");
					pw.println("alert('"+Constants.getString4CurrentUser("system.manager.ok")+"')");
					pw.println("</script>");
				}
				catch(Exception e) {
					log.error(e);
				}
			}
		}
		return super.redirectModelAndView("/logonLog.do?method=detailSearch&from=audit");
	}
	
	public ModelAndView clearLog(HttpServletRequest request,HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("log/logonLog/clearLog");
		String clearMonth = request.getParameter("clearMonth");
		if(clearMonth != null) {
			this.logonLogManager.clearLog(-1, Integer.parseInt(clearMonth));
			try {
				PrintWriter pw = response.getWriter();
				pw.println("<script>");
				pw.println("parent.showAlert();");
				pw.println("</script>");
				return null;
			}catch(Exception e) {
				log.error(e);
			}
		}
		return mv;
	}
	
	/**
	 * 显示删除日志对话框
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView showClearLogsDlg(HttpServletRequest request,HttpServletResponse response){
		ModelAndView mv = new ModelAndView("log/clearLogsDialog");
		if("logon".equals(request.getParameter("type"))){
			//登录日志保存期限
			String logDeadlineLoginCfi = systemConfig.get(IConfigPublicKey.LOG_Deadline_Login);
			if(logDeadlineLoginCfi != null){
				mv.addObject("logDeadline", logDeadlineLoginCfi);
			}			
		}
		else if("app".equals(request.getParameter("type"))){
			//登录日志保存期限
			String logDeadlineLoginCfi = systemConfig.get(IConfigPublicKey.LOG_Deadline_App);
			if(logDeadlineLoginCfi != null){
				mv.addObject("logDeadline", logDeadlineLoginCfi);
			}
		}
        return mv;
	}
	
	private Long[] getUserCondition(String strUsers) {
		Long[] users = null;
		if(strUsers != null && !"".equals(strUsers)) {
			String[] arr = strUsers.split(",");
			users = new Long[arr.length];
			int i=0;
			for(String user:arr) {
				users[i] = Long.parseLong(user);
				i++;
			}
		}
		return users;
	}
	
	private Date[] getDateTime(String startTime,String endTime) {
		Date[] dates = new Date[2];
		if(Strings.isNotBlank(startTime) || Strings.isNotBlank(endTime)) {
			if(startTime.indexOf(" ")  > 0){
				dates[0] = Datetimes.parseDatetime(startTime);
			}else{
				dates[0] = Datetimes.parseDatetime(startTime + " 00:00:00");
			}
			if(endTime.indexOf(" ")> 0){
				dates[1] = Datetimes.parseDatetime(endTime);
			}else{
				dates[1] = Datetimes.parseDatetime(endTime + " 23:59:59");
			}
		}
		else{
			dates[1] = new Date();
			dates[0] = Datetimes.getFirstDayInMonth(dates[1]);
		}
		return dates;
	}
	
	private int getDesc(String desc) {
		if(desc == null || "".equals(desc))
			return 0;
		return Integer.parseInt(desc);
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}
}