package com.seeyon.v3x.resource.controller;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.resource.ResourceType;
import com.seeyon.v3x.resource.domain.Resource;
import com.seeyon.v3x.resource.manager.ResourceManager;


@CheckRoleAccess(roleTypes={RoleType.Administrator})
public class ResourceController extends BaseController {

	private ResourceManager resourceManager;
	private static Log log = LogFactory.getLog(ResourceController.class);
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	public ModelAndView resourceHome(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("v3xmain/resource/sResource");
		return mav;		
	}

	public ModelAndView listToolBar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("v3xmain/resource/listToolBar");
		return mav;		
	}
	
	public ModelAndView initList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List rsList = resourceManager.listResourceForPage();
		ModelAndView mav = new ModelAndView("v3xmain/resource/listResource");
		mav.addObject("rsList", FormBizConfigUtils.pagenate(rsList));
		return mav;
	}
	
	public ModelAndView initAdd(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("v3xmain/resource/addResource");
		return mav;
	}
	
	public ModelAndView addResource(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		boolean bool = resourceManager.checkDuplicatedName(RequestUtils.getStringParameter(request, "name").trim(), null);
		if(bool){
			out.println("<script>");
			out.println("alert('已有同名公共资源,请更换名称!');");
			out.println("self.history.back();");
			out.println("</script>");
			return null;
		}
		Resource rs = new Resource();
		rs.setName(RequestUtils.getStringParameter(request, "name"));
//		rs.setType(RequestUtils.getStringParameter(request, "type"));
//		公共资源去掉类型后默认先将类型设置为与会资源   xut  2008-3-19
		rs.setType(ResourceType.MEETINTRESOURCE.getValue());
		rs.setDescription( request.getParameter("description"));	
		//		设置单位
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		rs.setAccountId(accountId);
		
		resourceManager.addResource(rs);
		return super.refreshWorkspace();
	}
	
	public ModelAndView initUpdate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Resource rs = resourceManager.getResourceByPk(RequestUtils.getLongParameter(request, "id"));
		String oper = request.getParameter("oper");
		ModelAndView mav = new ModelAndView("v3xmain/resource/updateResource");
		mav.addObject("resource", rs);
		mav.addObject("oper", oper);
		return mav;
	}
	
	public ModelAndView updateResource(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Resource rs = resourceManager.getResourceByPk(RequestUtils.getLongParameter(request, "id"));
		String orgName = rs.getName();
		rs.setName(RequestUtils.getStringParameter(request, "name"));
//		rs.setType(RequestUtils.getStringParameter(request, "type"));
		rs.setDescription( request.getParameter("description"));
		
		PrintWriter out = response.getWriter();
		boolean bool = resourceManager.checkDuplicatedName(RequestUtils.getStringParameter(request, "name").trim(), null);
		if(bool && !orgName.equals(RequestUtils.getStringParameter(request, "name").trim())){
			out.println("<script>");
			out.println("alert('已有同名公共资源,请更换名称!');");
			out.println("self.history.back();");
			out.println("</script>");
			return null;
		}
		
		
		resourceManager.updateResource(rs);	
		//boolean isTrue = resourceManager.isResourcesImpropriated(rs.getId(), Datetimes.parse("2007-6-12 09:00:01", Datetimes.datetimeStyle), Datetimes.parse("2007-6-12 12:00:00", Datetimes.datetimeStyle));
		//resourceManager.impropriateResources(rs.getId(), Long.valueOf("686102333838779044"),Datetimes.parse("2007-6-12 09:00:01", Datetimes.datetimeStyle), Datetimes.parse("2007-6-12 12:00:00", Datetimes.datetimeStyle));
		//resourceManager.updateImpropriateResources(rs.getId(), Long.valueOf("686102333838779044"),Datetimes.parse("2007-6-12 08:00:01", Datetimes.datetimeStyle), Datetimes.parse("2007-6-12 12:00:00", Datetimes.datetimeStyle));
		//resourceManager.delResourceIppByAppId(new Long(451236));
		//int size = resourceManager.findResourcesByType("office").size();		
		return super.refreshWorkspace();
	}
	
	public ModelAndView delResource(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		resourceManager.deleteResource(RequestUtils.getLongParameter(request, "id"));		
		return super.refreshWorkspace();	
	}
	
	//批量删除公共资源
	public ModelAndView delResources(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		String idStr=request.getParameter("id");	
		String[] idStrs=idStr.split(",");
		for(String str :idStrs){
			try {
				resourceManager.deleteResource(Long.valueOf(str));		
			} catch (Exception e) {
				log.error("删除公共资源出错", e);
			}	
			
		}		
		return super.refreshWorkspace();	
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

}