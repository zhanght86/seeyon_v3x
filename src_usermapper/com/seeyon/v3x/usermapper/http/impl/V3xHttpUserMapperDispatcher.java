package com.seeyon.v3x.usermapper.http.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.usermapper.domain.V3xOrgUserMapper;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.organization.inexportutil.DataUtil;
import com.seeyon.v3x.usermapper.NoMethodException;
import com.seeyon.v3x.usermapper.common.action.UserMapperAction;
import com.seeyon.v3x.usermapper.common.constants.RefreshUserMapperPolice;
import com.seeyon.v3x.usermapper.http.HttpUserMapperDispatcher;
import com.seeyon.v3x.usermapper.report.domain.ReportDetail;
import com.seeyon.v3x.usermapper.util.UserMapperUtil;


public class V3xHttpUserMapperDispatcher extends
		SimpleHttpUserMapperDispatcher {
	protected static Log log = LogFactory.getLog(V3xHttpUserMapperDispatcher.class);
	//protected String inType=null;
	
	private FileToExcelManager fileToExcelManager;
	
	protected RefreshUserMapperPolice police(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		String p=request.getParameter(HttpUserMapperDispatcher.HTTP_PARA_POLICE);
		if(p==null){
			Object o=request.getAttribute(HttpUserMapperDispatcher.HTTP_PARA_POLICE);
			if(o!=null){
				if(o instanceof RefreshUserMapperPolice)
					return (RefreshUserMapperPolice)o;
				
				try{
					p=(String)o;
				}catch(Exception e){
					
				}
			}
				
		}
		
		return UserMapperUtil.providerPolice(p);
	}
	
	protected V3xOrgUserMapper  catchUserMapperFromHttp(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String login=request.getParameter(HttpUserMapperDispatcher.HTTP_PARA_LOGIN);
		String exlogin=request.getParameter(HttpUserMapperDispatcher.HTTP_PARA_EXLOGIN);
		
		if(!StringUtils.hasText(login))
			throw new Exception("no login para");
		
		if(!StringUtils.hasText(exlogin))
			throw new Exception("no exlogin para");
		
		V3xOrgUserMapper um=new V3xOrgUserMapper();
		um.setId(-1L);
		um.setLoginName(login);
		um.setExLoginName(exlogin);
		
		return um;
	}
	protected LoginExLoginListEntity  catchLoginExLoginListEntityFromHttp(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String slogin=request.getParameter(HttpUserMapperDispatcher.HTTP_PARA_LOGIN);
		if(!StringUtils.hasText(slogin)){
			Object ol=request.getAttribute(HttpUserMapperDispatcher.HTTP_PARA_LOGIN);
			if(ol!=null)
				slogin=(String)ol;
		}
		String[] sexLogins=request.getParameterValues(HttpUserMapperDispatcher.HTTP_PARA_EXLOGIN);
		if(sexLogins==null){
			Object oel=request.getAttribute(HttpUserMapperDispatcher.HTTP_PARA_EXLOGIN);
			if(oel!=null)
				sexLogins=(String[])oel;
		}
		
		if(!StringUtils.hasText(slogin))
			throw new Exception("no login para");
		/*if(sexLogins==null )
			throw new Exception("no exlogin para");*/
		
		LoginExLoginListEntity lelEntity=new LoginExLoginListEntity();
		
		lelEntity.setLogin(slogin);
		List<String> l=new ArrayList<String>();
		if(sexLogins!=null){
			for(String ex:sexLogins){
				if(!StringUtils.hasText(ex))
					continue;
				
				l.add(ex);
			}
		}
		
		/*if(sexLogins.length<1)
			throw new Exception("no exlogin para");*/
		lelEntity.setExLogins(l);
		
		return lelEntity;
	}
	
	public UserMapperAction getAction() {
		// TODO Auto-generated method stub
		return this.action;
	}
	
	public ModelAndView singleUserMapper(HttpServletRequest request,
			HttpServletResponse response) throws NoMethodException, Exception {
		// TODO Auto-generated method stub
		//this.type(request, response);
		checkAction();
		V3xOrgUserMapper um=this.catchUserMapperFromHttp(request, response);
		
		this.getAction().begin(
				this.police(request, response));
		this.getAction().execute(um);
		this.getAction().ok();
		
		afterAction(request,response);
		
		return null;
	}
	public ModelAndView doUserMapperSingleLoginListExlogin(
			HttpServletRequest request, HttpServletResponse response)
			throws NoMethodException, Exception {
		// TODO Auto-generated method stub
		//this.type(request, response);
		checkAction();
		LoginExLoginListEntity entity=
			this.catchLoginExLoginListEntityFromHttp(request, response);
		
		this.getAction().begin(
				this.police(request, response));
		this.getAction().execute4LoginExLogin(
				          entity.getLogin(), entity.getExLogins());
		this.getAction().ok();
		
		afterAction(request,response);
		
		return null;
	}
	
	public ModelAndView saveImportReport(HttpServletRequest request,
			HttpServletResponse response) throws NoMethodException, Exception {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		DataRow[] datarow = (DataRow[])session.getAttribute("datarowlist");
		if(datarow==null){
			List resultlst=(List)session.getAttribute(HttpUserMapperDispatcher.HTTP_PARA_ACTION_REPORT);
			session.removeAttribute(HttpUserMapperDispatcher.HTTP_PARA_ACTION_REPORT);
			datarow=DataUtil.createDataRowsFromResultObjects(resultlst);
		}
		Locale locale=request.getLocale();
		if( Locale.CHINA!=locale 
				|| Locale.TAIWAN!=locale 
				|| Locale.ENGLISH!=locale)
			locale=Locale.ENGLISH;
		
		String resource = "com.seeyon.v3x.usermapper.resources.i18n.UserMapperResources";//todo
		
		String import_report =
			 // "OrgImportReport";
			ResourceBundleUtil.getString(resource, locale, "report");
		
		String import_data = ResourceBundleUtil.getString(resource, locale, "report.data");
		String import_action = ResourceBundleUtil.getString(resource, locale, "report.action");
		String import_memo = ResourceBundleUtil.getString(resource, locale, "report.memo");
		
		String title = "report";
		String sheetName = "UsermapperReport";
		
//		将导入结果添加到excel中
		DataRecord dataRecord = new DataRecord();
		try {
			dataRecord.addDataRow(datarow);
		} catch (Exception e) {
			log.error("", e);
		}
		String[] columnName = { import_data , import_action , import_memo };
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(title);
		dataRecord.setSheetName(sheetName);

		try {
			getFileToExcelManager().save(request, response, import_report, dataRecord);
		} catch (Exception e) {
			log.error("error", e);
		}
		return null;
	}
	
	public FileToExcelManager getFileToExcelManager() {
		return fileToExcelManager;
	}
	public void setFileToExcelManager(FileToExcelManager val) {
		this.fileToExcelManager = val;
	}
	
	protected void checkAction()throws Exception{
		if(null==this.action)
			throw new Exception("no action to work");
	}
	
	protected void afterAction(
			HttpServletRequest request, HttpServletResponse response)throws Exception{
		List<ReportDetail> rdl=this.action.getReportDetails();
		request.getSession().setAttribute(HttpUserMapperDispatcher.HTTP_PARA_ACTION_REPORT, rdl);
	}
	
	protected class LoginExLoginListEntity{
		String login;
		
		List<String> exLogins;

		public List<String> getExLogins() {
			return exLogins;
		}
		public void setExLogins(List<String> val) {
			this.exLogins = val;
		}
		
		public String getLogin() {
			return login;
		}
		public void setLogin(String val) {
			this.login = val;
		}
	}//end class
	
	
}//end class
