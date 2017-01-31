package com.seeyon.v3x.organization.inexportutil.manager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.inexportutil.DataUtil;
import com.seeyon.v3x.organization.services.OrganizationServices;

public interface IOManager {
	public OrganizationServices getOrganizationServices();
	public User getOpUser();
	public void setOpUser(User opUser) ;
	
	public V3xOrgAccount getVaccount();
	public void setVaccount(V3xOrgAccount vaccount);
	public void setVaccountByUser(User u);
	public void setVaccountByUser();
	
	public ModelAndView expOrgToExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception;
	
	public ModelAndView doImport(HttpServletRequest request,
			HttpServletResponse response) throws Exception;
	public ModelAndView popMatchPage(HttpServletRequest request,
			HttpServletResponse response) throws Exception ;
	public ModelAndView matchField(HttpServletRequest request,
			HttpServletResponse response) throws Exception ;
	public ModelAndView exportReport(HttpServletRequest request,
			HttpServletResponse response) throws Exception ;
	public ModelAndView importExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception ;
	public ModelAndView importReport(HttpServletRequest request,
			HttpServletResponse response) throws Exception ;
	public String doImport4Redirect(HttpServletRequest request,
			HttpServletResponse response) throws Exception ;
	
	public void writeExpEndProcScript(HttpServletRequest request,
			HttpServletResponse response) throws Exception;
	
	public ModelAndView toImpBase(HttpServletRequest request,
			HttpServletResponse response) throws Exception ;	
	public ModelAndView toExpBase(HttpServletRequest request,
			HttpServletResponse response) throws Exception;
	public ModelAndView toExpRepeater(HttpServletRequest request,
			HttpServletResponse response,String url) throws Exception;
	
//	tanglh  判断是否可以执行IO
	public String canIO() throws Exception ;
	public String canIO(User u) throws Exception ;
	public String canIO(long userid) throws Exception ;
	public String canIO(String userid) throws Exception ;
}
