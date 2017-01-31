package com.seeyon.v3x.log.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.operationlog.domain.OperationLog;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.log.tools.WebLogModel;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.XMLCoder;

public class LogController extends BaseController{

	private OperationlogManager operationlogManager;
	private OrgManagerDirect orgManagerDirect;
	private SystemConfig systemConfig; 

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}
	
	public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	public ModelAndView mainEntry(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("log/logMainEntry");
		
		return mav;
	}
	
	public ModelAndView main(HttpServletRequest request,HttpServletResponse response)throws Exception{
		User user = CurrentUser.get();
		ModelAndView mav = new ModelAndView("log/logMain");
		if(!user.isSystemAdmin()){
			mav.addObject("userType", "accountAdmin") ;
		}
		
		
		
		return mav;
	}
	
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("log/logListIframe");
        //公文开关
		String edocEnabled = "true";
		String edocCfi = systemConfig.get(IConfigPublicKey.EDOC_ENABLE);
		if(null!=edocCfi && edocCfi.equals(IConfigPublicKey.ENABLE)){
			edocEnabled = "true";
		}else if(null!=edocCfi && edocCfi.equals(IConfigPublicKey.DISABLE)){
			edocEnabled = "false";
		}
		mav.addObject("edocEnabled", edocEnabled);
		List<OperationLog> list = null;
		
		String category = request.getParameter("category");
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		
		if( null!=category && !"".equals(category) && "-1".equals(category))
			category = null;//如果类别为空或为 -1，即为空，全部查询
		list = operationlogManager.getOperationLogByCondition(category,beginDate,endDate,true);
		
		mav.addObject("totalList", convertLabel(request,list));
		mav.addObject("category", null!=category && !"".equals(category) ? category : "-1");		
		mav.addObject("beginDate", beginDate);
		mav.addObject("endDate", endDate);
		
		return mav;
	}
	/**
	 * 单位管理员查看本单位的应用日志
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView accountLogView(HttpServletRequest request,HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("log/logListIframe");
		long accountId = CurrentUser.get().getAccountId();
        //公文开关
		String edocEnabled = "true";
		String edocCfi = systemConfig.get(IConfigPublicKey.EDOC_ENABLE);
		if(null!=edocCfi && edocCfi.equals(IConfigPublicKey.ENABLE)){
			edocEnabled = "true";
		}else if(null!=edocCfi && edocCfi.equals(IConfigPublicKey.DISABLE)){
			edocEnabled = "false";
		}
		mav.addObject("edocEnabled", edocEnabled);
		List<OperationLog> list = null;
		
		String category = request.getParameter("category");
		String beginDate = request.getParameter("beginDate");
		String endDate = request.getParameter("endDate");
		
		if( null!=category && !"".equals(category) && "-1".equals(category))
			category = null;//如果类别为空或为 -1，即为空，全部查询
		list = operationlogManager.getOperationLogByCondition(Long.valueOf(accountId) ,
				category,beginDate,endDate,true ,orgManagerDirect);
		
		//List<OperationLog> returnList = this.doOrgSelect(list) ;
		
		mav.addObject("totalList", convertLabel(request,list));
		mav.addObject("category", null!=category && !"".equals(category) ? category : "-1");		
		mav.addObject("beginDate", beginDate);
		mav.addObject("endDate", endDate);
		
		return mav;		
		
	}
	/**
	private List<OperationLog> doOrgSelect(List<OperationLog> list) throws Exception{
		List<OperationLog> orgLogList = new ArrayList<OperationLog>() ;
		long accountId = CurrentUser.get().getAccountId();
		if(null != list){
			for(OperationLog log : list){
				V3xOrgMember orgMember = orgManagerDirect.getMemberById(log.getMemberId());
				if(orgMember.getOrgAccountId().longValue() ==  accountId){
					orgLogList.add(log) ;
				}
			}
		}
		//Pagination.setRowCount(orgLogList.size());
		return orgLogList ;
	}
	**/
	public ModelAndView detail(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		ModelAndView mav = new ModelAndView("log/logDetail");

		List<OperationLog> list = operationlogManager.getAllOperationLog(true);
		mav.addObject("totalList", list);			
		
		return mav;
	}
	
	
	
	/**
	 * @param request
	 * @param list
	 * @return
	 * @throws BusinessException
	 */
	public List<WebLogModel> convertLabel(HttpServletRequest request, List<OperationLog> list)throws BusinessException{
		
		if(null==list || list.size() == 0) return null;
		
		List<WebLogModel> modelList = new ArrayList<WebLogModel>();
		
		Locale locale = LocaleContext.getLocale(request);
		String resource = null;
		String category = null;
		
		for(OperationLog log:list){
			int logCat = log.getModuleId().intValue();
			if(logCat == ApplicationCategoryEnum.edoc.ordinal()){
				resource = "com.seeyon.v3x.edoc.resources.i18n.EdocResource";
			}else if(logCat == ApplicationCategoryEnum.doc.ordinal()){
				resource = "com.seeyon.v3x.doc.resources.i18n.DocResource";				
			}else if(logCat == ApplicationCategoryEnum.collaboration.ordinal()){
				resource = "com.seeyon.v3x.collaboration.resources.i18n.CollaborationResource";				
			}else if(logCat == ApplicationCategoryEnum.bbs.ordinal()){
				resource = "com.seeyon.v3x.bbs.resources.i18n.BBSResources";				
			}else if(logCat == ApplicationCategoryEnum.blog.ordinal()){
				resource = "com.seeyon.v3x.blog.resources.i18n.BLOGResources";				
			}else if(logCat == ApplicationCategoryEnum.bulletin.ordinal()){
				resource = "com.seeyon.v3x.bulletin.resources.i18n.BulletinResources";				
			}else if(logCat == ApplicationCategoryEnum.calendar.ordinal()){
				resource = "com.seeyon.v3x.calendar.resources.i18n.CalendarResource";				
			}else if(logCat == ApplicationCategoryEnum.exchange.ordinal()){
				resource = "com.seeyon.v3x.exchange.resources.i18n.ExchangeResource";			
			}else if(logCat == ApplicationCategoryEnum.form.ordinal()){
				resource = "www.seeyon.com.v3x.form.resources.i18n.FormResources";				
			}else if(logCat == ApplicationCategoryEnum.global.ordinal()){
				resource = "com.seeyon.v3x.main.resources.i18n.MainResources";				
			}else if(logCat == ApplicationCategoryEnum.hr.ordinal()){
				resource = "com.seeyon.v3x.hr.resource.i18n.HRResources";				
			}else if(logCat == ApplicationCategoryEnum.inquiry.ordinal()){
				resource = "com.seeyon.v3x.inquiry.resources.i18n.InquiryResources";				
			}else if(logCat == ApplicationCategoryEnum.mail.ordinal()){
				resource = "com.seeyon.v3x.webmail.resources.i18n.WebMailResources";				
			}else if(logCat == ApplicationCategoryEnum.meeting.ordinal()){
				resource = "com.seeyon.v3x.meeting.resources.i18n.MeetingResources";				
			}else if(logCat == ApplicationCategoryEnum.news.ordinal()){
				resource = "com.seeyon.v3x.news.resources.i18n.NewsResources";				
			}else if(logCat == ApplicationCategoryEnum.organization.ordinal()){
				resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";				
			}else if(logCat == ApplicationCategoryEnum.plan.ordinal()){
				resource = "com.seeyon.v3x.plan.resource.i18n.PlanResources";				
			}else if(logCat == ApplicationCategoryEnum.project.ordinal()){
				resource = "com.seeyon.v3x.project.resources.i18n.ProjectResources";				
			}else if(logCat == ApplicationCategoryEnum.relateMember.ordinal()){
				resource = "com.seeyon.v3x.peoplerelate.resources.i18n.RelateResources";				
			}else if(logCat == ApplicationCategoryEnum.modifyPassword.ordinal()){
				resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";	
			}
			
			category = ResourceBundleUtil.getString("com.seeyon.v3x.common.resources.i18n.SeeyonCommonResources", locale, "application."+logCat+".label");
			
			Object[] params = null;
			if(Strings.isNotBlank(log.getContentParameters())){
				params = (Object[])XMLCoder.decoder(log.getContentParameters());
			}

			
			String contentValue = ResourceBundleUtil.getString(resource, locale, log.getContentLabel(), params);
			
			WebLogModel model = new WebLogModel();
			model.setOperationLog(log);
			model.setContentValue(contentValue);
			model.setCategory(category);
			model.setIpAddress(log.getRemoteIp());
			
			String staffName = "";
			V3xOrgMember orgMember = orgManagerDirect.getMemberById(log.getMemberId());
			String AccountName="";
			if(orgMember != null){
				staffName = orgMember.getName();
				AccountName=orgManagerDirect.getAccountById(orgMember.getOrgAccountId()).getName();
			}
			model.setPersonnel(staffName);
			model.setAccountName(AccountName);
			modelList.add(model);
		}
		
		return modelList;
	}
	
	public ModelAndView deleteById(HttpServletRequest request, HttpServletResponse response)throws Exception{
		
		String id = request.getParameter("id");
		
		if(null!=id && !"".equals(id)){
			operationlogManager.deleteLogById(id);
		}

		
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("parent.searchByself();");
		out.println("</script>");
		 
		return null;
		//return super.refreshWindow("parent");
	}
	
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
	
}
