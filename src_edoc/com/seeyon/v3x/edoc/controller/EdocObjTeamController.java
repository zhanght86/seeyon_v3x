package com.seeyon.v3x.edoc.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ListSearchHelper;
import com.seeyon.v3x.edoc.domain.EdocObjTeam;
import com.seeyon.v3x.edoc.manager.EdocObjTeamManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.Strings;

@CheckRoleAccess(roleTypes={RoleType.Administrator})
public class EdocObjTeamController extends BaseController {
	
	private EdocObjTeamManager edocObjTeamManager;
	private OrgManager orgManager;

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {		
		ModelAndView mav = new ModelAndView("edoc/orgTeam/orgTeam_list_main");		
		return mav;
	}
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ListSearchHelper.pickupExpression(request, null);
		ModelAndView mav = new ModelAndView("edoc/orgTeam/orgTeam_list");
		List<EdocObjTeam> list=null;
		User user=CurrentUser.get();
		
		//处理条件查询
		String expressionType = request.getParameter("expressionType");
		String expressionValue = request.getParameter("expressionValue");
		if(Strings.isNotBlank(expressionType) && Strings.isNotBlank(expressionValue)){
			list = edocObjTeamManager.findByName(expressionValue,user.getLoginAccount());
		}else{
			list=edocObjTeamManager.findAll(user.getLoginAccount());
		}
		
		
		String productEdition = (String)(SysFlag.valueOf("frontPage_showMenu").getFlag());		
		mav.addObject("productEdition",productEdition);
		mav.addObject("teamList",list);
		return mav;
	}
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id=request.getParameter("id");
		ModelAndView mav = new ModelAndView("edoc/orgTeam/edit_team_detail");
		EdocObjTeam team=edocObjTeamManager.getById(Long.parseLong(id));
		String productEdition = (String)(SysFlag.valueOf("frontPage_showMenu").getFlag());		
		mav.addObject("productEdition",productEdition);
		mav.addObject("team",team);
		mav.addObject("accountId",CurrentUser.get().getLoginAccount());
		mav.addObject("flag",request.getParameter("flag"));
		
		return mav;
	}
	
	public ModelAndView addNew(HttpServletRequest request,HttpServletResponse response)throws Exception{
		ModelAndView mav = new ModelAndView("edoc/orgTeam/team_detail");
		String productEdition = (String)(SysFlag.valueOf("frontPage_showMenu").getFlag());		
		mav.addObject("productEdition",productEdition);
		EdocObjTeam team=new EdocObjTeam();
		mav.addObject("team",team);
		mav.addObject("accountId",CurrentUser.get().getLoginAccount());
		return mav;
	}
	public ModelAndView save(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		User user=CurrentUser.get();
		
		EdocObjTeam edocObjTeam=null;
		String idStr=request.getParameter("id");
		String comm="save";
		if(idStr==null || "".equals(idStr))
		{
			edocObjTeam=new EdocObjTeam();
			edocObjTeam.setIdIfNew();
		}
		else
		{
			edocObjTeam=edocObjTeamManager.getById(Long.parseLong(idStr));
			edocObjTeam.getEdocObjTeamMembers().clear();
			comm="upd";
		}
		bind(request, edocObjTeam);
		edocObjTeam.setSelObjsStr(request.getParameter("grantedDepartId"));
		edocObjTeam.changeTeamMember();
		
		edocObjTeam.setOrgAccountId(user.getLoginAccount());		
		
		if(edocObjTeam.getDescription()==null){edocObjTeam.setDescription("");}
		
		if("save".equals(comm))
		{
			edocObjTeamManager.save(edocObjTeam);
		}
		else
		{
			edocObjTeamManager.update(edocObjTeam);
		}
		return super.refreshWindow("parent");
	}
	public ModelAndView delete(HttpServletRequest request,HttpServletResponse response)throws Exception{
		String [] ids=request.getParameterValues("id");
		String idsStr="";
		idsStr=StringUtils.join(ids,",");
		edocObjTeamManager.delete(idsStr);
		return super.refreshWindow("parent");
	}
	
	
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<T> subList = null;
		if(first>=list.size()) return Collections.emptyList();
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
	
	public EdocObjTeamManager getEdocObjTeamManager() {
		return edocObjTeamManager;
	}
	public void setEdocObjTeamManager(EdocObjTeamManager edocObjTeamManager) {
		this.edocObjTeamManager = edocObjTeamManager;
	}
	public OrgManager getOrgManager() {
		return orgManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

}
