/**
 * 
 */
package com.seeyon.v3x.main.section.definition.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.link.domain.LinkCategory;
import com.seeyon.v3x.link.domain.LinkSystem;
import com.seeyon.v3x.link.manager.OuterlinkManager;
import com.seeyon.v3x.main.section.definition.SectionDefinitionManager;
import com.seeyon.v3x.main.section.definition.domain.SectionDefinition;
import com.seeyon.v3x.main.section.definition.domain.SectionSecurity;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.util.Strings;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 * 
 */
@CheckRoleAccess(roleTypes=RoleType.SystemAdmin)
public class SectionDefinitionController extends BaseController {

	private SectionDefinitionManager sectionDefinitionManager;

	private OuterlinkManager outerlinkManager;

	private AppLogManager appLogManager;
	
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setSectionDefinitionManager(
			SectionDefinitionManager sectionDefinitionManager) {
		this.sectionDefinitionManager = sectionDefinitionManager;
	}

	public void setOuterlinkManager(OuterlinkManager outerlinkManager) {
		this.outerlinkManager = outerlinkManager;
	}

	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

	public ModelAndView main(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("sysMgr/section/main");
	}

	public ModelAndView create(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("sysMgr/section/create");

		String idStr = request.getParameter("id");

		if (Strings.isNotBlank(idStr)) {
			long sectionDefinitionid = Long.parseLong(idStr);
			SectionDefinition sectionDefinition = this.sectionDefinitionManager
					.getSectionDefinition(sectionDefinitionid);

			Map<String, String> props = this.sectionDefinitionManager
					.getSectionProps(sectionDefinitionid);
			List<SectionSecurity> sectionSecurities = this.sectionDefinitionManager
					.getSectionSecurity(sectionDefinitionid);
			if(props != null && !props.isEmpty()){
				String systemIdStr = props.get("ssoWebcontentLinkSystemId");
				if(Strings.isBlank(systemIdStr)){
					systemIdStr = props.get("ssoIframeLinkSystemId");
				}
				if(Strings.isNotBlank(systemIdStr)){
					LinkSystem linkSystem = outerlinkManager.getLinkSystemById(Long.parseLong(systemIdStr));
					if(linkSystem != null){
						mv.addObject("linkSystemName", linkSystem.getName());						
					}
				}
			}
			mv.addObject("sectionDefinition", sectionDefinition);
			mv.addObject("sectionProps", props);
			mv.addObject("SectionSecurities", sectionSecurities);
		}

		return mv;
	}

	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("sysMgr/section/list");

		List<SectionDefinition> all = this.sectionDefinitionManager
				.getAllSectionDefinition();
		Map<Long, List<SectionSecurity>> sectionDefinitionSecurityMap = new HashMap<Long, List<SectionSecurity>>();
		if(all != null){
			for(SectionDefinition sd : all){
				List<SectionSecurity> sectionSecurities = this.sectionDefinitionManager.getSectionSecurity(sd.getId());
				sectionDefinitionSecurityMap.put(sd.getId(), sectionSecurities);
			}
		}
		mv.addObject("all", all);
		mv.addObject("securityMap", sectionDefinitionSecurityMap);

		return mv;
	}

	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String idStr = request.getParameter("id");
		String name = request.getParameter("name");
		String selectPeopleStr = request.getParameter("selectPeopleStr");
		int type = Integer.parseInt(request.getParameter("type"));
		int state = Integer.parseInt(request.getParameter("state"));

		Map<String, String> props = new HashMap<String, String>();

		String[] propNames = request.getParameterValues("propName" + type);
		if (propNames != null) {
			for (String n : propNames) {
				props.put(n, Strings.escapeNULL(request.getParameter(n), ""));
			}
		}
		User user = CurrentUser.get();
		if (Strings.isBlank(idStr)) {
			this.sectionDefinitionManager.save(name, type, state,
					selectPeopleStr, props);
			appLogManager.insertLog(user, AppLogAction.ExtenColumnChange_Create,getSectionName(type),name);
		} else {
			this.sectionDefinitionManager.update(Long.parseLong(idStr), name, type,
					state, selectPeopleStr, props);
			appLogManager.insertLog(user, AppLogAction.ExtenColumnChange_Update,getSectionName(type),name);
		}
		
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('"
				+ Constants.getString4CurrentUser("system.manager.ok") + "')");
		out.println("</script>");
		out.flush();

		return super.redirectModelAndView("/sectionDefinition.do?method=main",
				"parent");
	}
	private String getSectionName(int type){
		switch(type){
		case 0:
			return ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources","space.section.ssoWebcontent");
		case 1:
			return ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources","space.section.ssoIframe");
		default:
			return ResourceBundleUtil.getString("com.seeyon.v3x.main.resources.i18n.MainResources","space.section.iframe");
		}
	}
	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String[] ids = request.getParameterValues("id");
		List<Long> idList = new ArrayList<Long>();
		if (ids != null) {
			for(String idStr : ids){
				idList.add(Long.parseLong(idStr));
			}
		}
		List<String[]> labelsList = new ArrayList<String[]>();
		if(!idList.isEmpty()){
			List<SectionDefinition> listAll = sectionDefinitionManager.getSectionsByIds(idList);
			for(SectionDefinition section : listAll){
				labelsList.add(new String[]{getSectionName(section.getType()),section.getName()});
			}
			for(Long id :idList){
				this.sectionDefinitionManager.delete(id);
			}
		}
		User user = CurrentUser.get();
		appLogManager.insertLogs(user, AppLogAction.ExtenColumnChange_Delete, labelsList);
		
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('"
				+ Constants.getString4CurrentUser("system.manager.ok") + "')");
		out.println("</script>");
		out.flush();

		return super.redirectModelAndView("/sectionDefinition.do?method=main",
				"parent");
	}

	/**
	 * 选择关联系统
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView linkSystemSelector(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"sysMgr/section/linkSystemSelector");
		Map<Long, List<LinkSystem>> linkSystemMap = new HashMap<Long, List<LinkSystem>>();
		List<LinkCategory> catagoryList = outerlinkManager.getAllLinkCategory(); // 获取所有的类别
		if (catagoryList != null && !catagoryList.isEmpty()) {
			for (LinkCategory c : catagoryList) {
				List<LinkSystem> list = outerlinkManager
						.getLinkSystemByCategoryId(c.getId()); // 获取所有的常用连接
				linkSystemMap.put(c.getId(), list);
			}
		}
		
		String id = request.getParameter("linkSystemId");
		if(Strings.isNotBlank(id)){
			LinkSystem system = outerlinkManager.getLinkSystemById(Long.parseLong(id));
			if(system != null){
				modelAndView.addObject("currentCatagroyId", system.getLinkCategoryId());
			}
		}
		
		modelAndView.addObject("linkSystemMap", linkSystemMap);
		modelAndView.addObject("catagoryList", catagoryList);
		return modelAndView;
	}

}