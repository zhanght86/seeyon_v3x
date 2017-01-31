package com.seeyon.v3x.system.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.collaboration.manager.impl.ColHelper;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.domain.AppLog;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ExportHelper;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.system.signet.domain.WebAppLog;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * @author xgghen
 * 应用日志
 *
 */
public class AppLogController extends BaseController {
	private static final Log log = LogFactory.getLog(AppLogController.class);
	
	 private AppLogManager appLogManager ;
	 private OrgManagerDirect orgManagerDirect;
	 private FileToExcelManager fileToExcelManager;
	 private OrgManager orgManager ;
	private SystemConfig systemConfig; 

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	 public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	 }
		public AppLogManager getAppLogManager() {
			return appLogManager;
		}

		public OrgManagerDirect getOrgManagerDirect() {
			return orgManagerDirect;
		}

		public FileToExcelManager getFileToExcelManager() {
			return fileToExcelManager;
		}

		public OrgManager getOrgManager() {
			return orgManager;
		}

		@Override
		public ModelAndView index(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			return null;
		}
	    /**
	     * 管理员（审计管理员、系统管理员、集团管理员、单位管理员）进入应用日志的页面的主框架
	     * @param request
	     * @param response
	     * @return
	     * @throws Exception
	     */
		@CheckRoleAccess(roleTypes={RoleType.SystemAdmin, RoleType.GroupAdmin, RoleType.Administrator, RoleType.AuditAdmin})
		public ModelAndView mainFrame(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("sysMgr/appLog/appLogMainFram") ;
	    	Date firstDay = Datetimes.getFirstDayInMonth(new Date(System.currentTimeMillis()));
	    	Date today = Datetimes.getTodayFirstTime();
		    //boolean sysFlag = false ;
		    String systemFlag = "GROUP" ;
		    if(SysFlag.sys_isEnterpriseVer.getFlag().equals(true) || SysFlag.sys_isGovVer.getFlag().equals(true)){
		       // sysFlag = true ;
		        systemFlag = "ORG" ;
		    }
            mav.addObject("systemFlag", systemFlag);
	    	mav.addObject("firstDay" , firstDay);
	    	mav.addObject("today",today);
	    	mav.addObject("isCanDelete", CurrentUser.get().isAuditAdmin());
	    	
			return mav;
		}
		/**
		 * 系统管理员获得所有符合条件的应用日志
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		@CheckRoleAccess(roleTypes={RoleType.SystemAdmin, RoleType.GroupAdmin, RoleType.Administrator, RoleType.AuditAdmin})
		public ModelAndView listAllAppLogData(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("sysMgr/appLog/searchAppLog") ;
			String fromDate  = Datetimes.formatDate(Datetimes.getFirstDayInMonth(new Date(System.currentTimeMillis()))) ;
			String endDate  = Datetimes.formatDate(Datetimes.getTodayFirstTime()) ;
			//String fromDate = Datetimes.getFirstDayInMonth(new Date(System.currentTimeMillis())).toLocaleString() ;
			//String endDate = Datetimes.getTodayFirstTime().toLocaleString() ;
			String moduleId = request.getParameter("moduleId") ;
			String selectPersonIds =request.getParameter("selectPersonIds") ;
			String actionType = request.getParameter("actionType") ;
			List<AppLog> list = this.appLogManager.queryAppLogsUnits(moduleId, actionType, selectPersonIds, null, null, fromDate, endDate,true);
//			for (int i = 0; i < list.size(); i++) {
//				String tmp = list.get(i).getParam0();
//				log.info("+++++++++++++++++++++"+tmp+"+++++++++"+tmp+"++++++++++++++++++++++");
//				if("System administrator".equals(tmp)|| "系统管理员".equals(tmp)||"单位管理员".equals(tmp)||"审计管理员".equals(tmp)||"安全管理员".equals(tmp)||"集团管理员".equals(tmp)){
//					log.info("----------------------"+tmp+"---------------------");
//					list.remove(i);
//				}
//			}
			mav.addObject("appLogsList", getAllWebAppLog(list));
			return mav ;
		}
		/**
		 * 系统管理员查询
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		@CheckRoleAccess(roleTypes={RoleType.SystemAdmin, RoleType.GroupAdmin, RoleType.Administrator, RoleType.AuditAdmin})
		public ModelAndView queryAppLog(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("sysMgr/appLog/searchAppLog") ;
			String fromDate = request.getParameter("beginDate") ;
			String endDate = request.getParameter("endDate") ;
			String moduleId = request.getParameter("moduleId") ;
			String selectPersonIds =request.getParameter("selectPersonIds") ;
			String actionType = request.getParameter("actionType") ;
			List<AppLog> list = this.appLogManager.queryAppLogsUnits(moduleId, actionType, selectPersonIds, null, null, fromDate, endDate,true);
			mav.addObject("appLogsList", getAllWebAppLog(list)) ;			
			return mav ;
		}
		/**
		 * 删除应用日志
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		@CheckRoleAccess(roleTypes={RoleType.AuditAdmin})
		public ModelAndView delAppLog (HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			User user = CurrentUser.get();
			boolean isAuditAdmin = user.isAuditAdmin();
			if(isAuditAdmin){
				String logDeadline = systemConfig.get(IConfigPublicKey.LOG_Deadline_App);
				if(Strings.isNotBlank(logDeadline)){
					int line = Integer.parseInt(logDeadline); //保存期限
					String clearMonth = request.getParameter("dateCondition"); //XX月前
					if(Strings.isNotBlank(clearMonth)){
						line = Integer.parseInt(clearMonth);
					}
					Date clearDate = Datetimes.addMonth(new Date(), -line);
					this.appLogManager.clearAppLogs(clearDate) ;
					String dateStr = Datetimes.format(clearDate, Datetimes.dateStyle);
					appLogManager.insertLog(user, AppLogAction.Clear_Log_App, user.getName(), dateStr);
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
			
			return super.redirectModelAndView("/appLog.do?method=mainFrame&from=audit");		
		}
		/**
		 * 导出日志到Excel
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		@CheckRoleAccess(roleTypes={RoleType.SystemAdmin, RoleType.GroupAdmin, RoleType.Administrator, RoleType.AuditAdmin, RoleType.DepartmentAdmin})
		public ModelAndView appLogDataExportExcel(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			// 不分页
			Pagination.withoutPagination(null);
			Pagination.setFirstResult(0);
			Pagination.setMaxResults(Integer.MAX_VALUE);
			ModelAndView mav = ExportHelper.excutePageMethod(this, request, response, "pageMethod");
			List<WebAppLog> webApplogList = null;
			if (mav != null) {
				webApplogList = (List<WebAppLog>) mav.getModel().get("appLogsList");
			}
/*
			User user = CurrentUser.get();
			String fromDate = request.getParameter("beginDate") ;
			String endDate = request.getParameter("endDate") ;
			String moduleId = request.getParameter("moduleId") ;
			String selectPersonIds =request.getParameter("selectPersonIds") ;
			String actionType = request.getParameter("actionType") ;
			
			if(Strings.isBlank(fromDate)){
				fromDate = Datetimes.format(Datetimes.getFirstDayInMonth(new Date(System.currentTimeMillis())), "yyyy-MM-dd") ;
			}
			if(Strings.isBlank(endDate)){
				endDate  = Datetimes.format(Datetimes.getTodayFirstTime(), "yyyy-MM-dd") ;
			}
			List<AppLog> list = null ;
			if(user.isGroupAdmin()|| user.isAuditAdmin()) {
				list = this.appLogManager.queryAppLogsUnits(moduleId, actionType, selectPersonIds, null, null, fromDate, endDate,false);
			}else if(user.isAdministrator()) {
				list = this.appLogManager.queryAppLogsUnits(moduleId, actionType, selectPersonIds, user.getAccountId(), null, fromDate, endDate,false);
			}else {
				Date beginTime = appLogManager.formatDate(fromDate) ;
				Date endTime = appLogManager.formatDateEndTime(endDate) ;
				Integer modId = null ;
				if(Strings.isNotBlank(moduleId)){
					modId = Integer.valueOf(moduleId) ;
				}
				Integer actionTypeId = null ;
				if(Strings.isNotBlank(actionType)){
					actionTypeId = Integer.valueOf(actionType) ;
				}
				list =this.appLogManager.queryAppLogs(modId, actionTypeId,beginTime, endTime ,user,false);
				//list = this.appLogManager.queryAppLogsUnits(moduleId, actionType, selectPersonIds, null, user.getDepartmentId(), fromDate, endDate, false,true) ;
			}
			
			List<WebAppLog> webApplogList = this.getAllWebAppLog(list) ;
*/
			String[] columnName = new String[7];
			String commonResource = "com.seeyon.v3x.log.resources.i18n.LogResource";
			String title = ResourceBundleUtil.getString(commonResource ,"appLog.excel.count.title");
			
			columnName[0] = ResourceBundleUtil.getString(commonResource ,"log.toolbar.title.stuff");
			columnName[1] = ResourceBundleUtil.getString(commonResource ,"log.toolbar.title.optionActon");
			columnName[2] = ResourceBundleUtil.getString(commonResource ,"log.toolbar.title.optionActionDesc");
			columnName[3] = ResourceBundleUtil.getString(commonResource ,"log.toolbar.title.operationTime");
			columnName[4] = ResourceBundleUtil.getString(commonResource ,"logon.search.ip");
			columnName[5] = ResourceBundleUtil.getString(commonResource ,"log.toolbar.title.account");
			columnName[6] = ResourceBundleUtil.getString(commonResource ,"log.toolbar.title.category");
			
			List<Object[]> rows = new ArrayList<Object[]>();
			
			for(WebAppLog webAppLog : webApplogList) {
				Object[] obj = new Object[7] ;
				obj[0] = webAppLog.getUser() ;
				obj[1] = webAppLog.getActionType() ;
				obj[2] = webAppLog.getActionDesc() ;
				if(webAppLog.getActionTime() == null){
					obj[3] = webAppLog.getActionTime() ;
				}else{
					obj[3] = Datetimes.formatDatetimeWithoutSecond(webAppLog.getActionTime()) ;
				}
				
				obj[4] = webAppLog.getIpAddress() ;
				obj[5] = webAppLog.getAccount() ;
				obj[6] = webAppLog.getModelName() ;
				rows.add(obj) ;
			}
			String fileName = ResourceBundleUtil.getString(commonResource, "appLog.excel.download.fileName") ; 
			if(rows.size() == 0) {
				ColHelper.exportToExcel(request, response, fileToExcelManager, fileName, null, columnName, title, "sheet1");
			}else {
				ColHelper.exportToExcel(request, response, fileToExcelManager, fileName, rows, columnName, title, "sheet1");
			}								
			return null ;
		}
		/**
		 * 单位管理
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		@CheckRoleAccess(roleTypes={RoleType.Administrator})
		public ModelAndView accountMain(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("sysMgr/appLog/accountMainFrame") ;			
	    	Date firstDay = Datetimes.getFirstDayInMonth(new Date(System.currentTimeMillis()));
	    	Date today = Datetimes.getTodayFirstTime();		
	    	mav.addObject("firstDay" , firstDay) ;
	    	mav.addObject("today",today) ;
			return mav;
		}
		
		/**
		 * 部门管理
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		@CheckRoleAccess(roleTypes={RoleType.DepartmentAdmin})
		public ModelAndView depMentMain(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("sysMgr/appLog/depMentMainFrame") ; 
			User user = CurrentUser.get();
            mav.addObject("showDepartmentId", departmentIdsToString());           
	    	Date firstDay = Datetimes.getFirstDayInMonth(new Date(System.currentTimeMillis()));
	    	Date today = Datetimes.getTodayFirstTime();	
	    	Set<Long> set=user.getAccessSystemMenu();
	    	boolean flag=false;
	    	for(Long l:set)
	    	{
	    		if(l.intValue()==2001)
	    		{
	    			flag=true;
	    			break;
	    		}
	    	}
	    	if(flag)
	    		mav.addObject("ishr" , "ok") ;
	    	else
	    		mav.addObject("ishr" , "no") ;
	    	mav.addObject("firstDay" , firstDay) ;
	    	mav.addObject("today",today) ;
			return mav;
		}
		

		
		public String departmentIdsToString() {
		  StringBuffer str = new StringBuffer() ;
		 try{
			  List<Long> deptIds = appLogManager.getDepartmentIds(CurrentUser.get()) ;			         
	          if(deptIds == null)
	        	  return str.toString() ;
	          for(Long id : deptIds){
	        	  str.append(id+",") ;
	          }			
		  }catch(BusinessException e){
				log.error("查询该人员的部门id出现问题", e) ;
		  }
          return str.toString() ;
		}
		/**
		 * 得到当月该单位的数据
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */	
		@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.DepartmentAdmin})
		public ModelAndView listAllAccountAppLogData(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			ModelAndView mav = new ModelAndView("sysMgr/appLog/searchAppLog") ;
			User user = CurrentUser.get();
			String fromDate  = Datetimes.formatDate(Datetimes.getFirstDayInMonth(new Date(System.currentTimeMillis()))) ;
			String endDate  = Datetimes.formatDate(Datetimes.getTodayFirstTime()) ;
			String moduleId = request.getParameter("moduleId") ;
			String selectPersonIds =request.getParameter("selectPersonIds") ;
			String actionType = request.getParameter("actionType") ;
			List<AppLog> list = null ;
			
			if(user.isAdministrator()) {
				list = this.appLogManager.queryAppLogsUnits(moduleId, actionType, selectPersonIds, user.getAccountId(), null, fromDate, endDate,true);
			}else {
				Date beginTime = appLogManager.formatDate(fromDate) ;
				Date endTime = appLogManager.formatDateEndTime(endDate) ;
				Integer modId = null ;
				if(Strings.isNotBlank(moduleId)){
					modId = Integer.valueOf(moduleId) ;
				}
				Integer actionTypeId = null ;
				if(Strings.isNotBlank(actionType)){
					actionTypeId = Integer.valueOf(actionType) ;
				}
				list = this.appLogManager.queryAppLogs(modId, actionTypeId,beginTime, endTime ,user,true);
				//list = this.appLogManager.queryAppLogsUnits(moduleId, actionType, selectPersonIds,null, user.getDepartmentId(), fromDate, endDate,true);
			}
			mav.addObject("appLogsList", getAllWebAppLog(list)) ;
			return mav ;
		}
		
		/**
		 * 单位/部门管理员查询
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
		@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.DepartmentAdmin})
		public ModelAndView accountQueryAppLog(HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			User user = CurrentUser.get();
			ModelAndView mav = new ModelAndView("sysMgr/appLog/searchAppLog") ;
			String fromDate = request.getParameter("beginDate") ;
			String endDate = request.getParameter("endDate") ;
			String moduleId = request.getParameter("moduleId") ;
			String selectPersonIds =request.getParameter("selectPersonIds") ;
			String actionType = request.getParameter("actionType") ;
			List<AppLog> list = null ;
			if(user.isAdministrator()) {
				list = this.appLogManager.queryAppLogsUnits(moduleId, actionType, selectPersonIds, user.getAccountId(), null, fromDate, endDate,true);
			}else {
				if(Strings.isNotBlank(selectPersonIds))
				      list = this.appLogManager.queryAppLogsUnits(moduleId, actionType, selectPersonIds,null, null, fromDate, endDate,true);
				else{
					Date beginTime = appLogManager.formatDate(fromDate) ;
					Date endTime = appLogManager.formatDateEndTime(endDate) ;
					Integer modId = null ;
					if(Strings.isNotBlank(moduleId)){
						modId = Integer.valueOf(moduleId) ;
					}
					Integer actionTypeId = null ;
					if(Strings.isNotBlank(actionType)){
						modId = Integer.valueOf(actionType) ;
					}
					list = this.appLogManager.queryAppLogs(modId, actionTypeId,beginTime, endTime ,user,true);
				}					 				
			}			
			mav.addObject("appLogsList", getAllWebAppLog(list)) ;			
			return mav ;
		}
		
	/**
	 * 对查询得到的数据进行封装,用于前台列表显示
	 */
	private List<WebAppLog> getAllWebAppLog(List<AppLog> list) throws Exception {
		List<WebAppLog> webAppLogList = new ArrayList<WebAppLog>();
		if (null == list || list.size() == 0) {
			return webAppLogList;
		}
		String OrganizationResources = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		String systemName = ResourceBundleUtil.getString(OrganizationResources, "org.account_form.systemAdminName.value");
		String auditName = ResourceBundleUtil.getString(OrganizationResources, "org.auditAdminName.value");
		String secretName = ResourceBundleUtil.getString(OrganizationResources, "org.secretAdminName.value");
		String groupName = ResourceBundleUtil.getString(OrganizationResources, "org.account_form.groupAdminName.value" + (String) SysFlag.EditionSuffix.getFlag());

		V3xOrgAccount rootAccount = orgManager.getRootAccount();
		Map<Long, String> accountNames = orgManager.getAllAccountShortNames();
		accountNames.put(1L, "-");
		Map<Long, String> memberNames = orgManager.getAllMemberNames(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		memberNames.put(1L, systemName);
		memberNames.put(0L, auditName);
		memberNames.put(2L, secretName);

		for (AppLog appLog : list) {
			Long accountId = appLog.getActionAccountId();
			Long memberId = appLog.getActionUserId();
			boolean isRootAccount = accountId.equals(rootAccount.getId());

			String accountName = accountNames.get(accountId);
			String memberName = memberNames.get(memberId);
			if (isRootAccount) {
				accountName = "-";
				memberName = groupName;
			}

			WebAppLog webAppLog = new WebAppLog();
			webAppLog.setId(appLog.getId());
			webAppLog.setAccount(accountName);
			webAppLog.setUser(memberName);
			webAppLog.setActionType(appLog.getActionType());
			webAppLog.setIpAddress(appLog.getIP());
			webAppLog.setActionDesc(appLog.getActionDesc());
			webAppLog.setModelName(appLog.getModuleName());
			webAppLog.setDepment("");
			webAppLog.setActionTime(appLog.getActionDate());
			webAppLog.setAppLog(appLog);
			webAppLogList.add(webAppLog);
		}
		return webAppLogList;
	}

}