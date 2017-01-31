package com.seeyon.v3x.system.controller;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;
import com.seeyon.v3x.common.flag.SysFlag;
import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;

import com.seeyon.v3x.common.CustomOrgRole;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.organization.Constant;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.system.Constants;

@CheckRoleAccess(roleTypes={RoleType.Administrator})
public class RoleController extends BaseController {
	
	private OrgManagerDirect orgManagerDirect;
	
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");

	private AppLogManager appLogManager;
	
	public AppLogManager getAppLogManager() {
		return appLogManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setIOperBase(IOperBase operBase) {
	  iOperBase = operBase;
	}
	
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
	
	
	
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("sysMgr/role/roleList");
		//todo:暂时不提交
		List<V3xOrgRole> roleList = orgManagerDirect.getAllRoles(CurrentUser.get().getLoginAccount(),false);
		int loop = roleList.size();
		V3xOrgRole role = null;
		List<String> roleMembers = new ArrayList<String>();
		List<V3xOrgMember> ml = new ArrayList<V3xOrgMember>();
		List<V3xOrgRole> roleListForPage = new ArrayList<V3xOrgRole>();
		
		
		boolean flag = SystemEnvironment.hasPlugin("hr");
		//将相对角色及部门角色、单位管理员、单位主管、hr管理员、公文发起权角色过滤掉
		for(int i = 0 ; i < loop ; i++ ){
			role = roleList.get(i);
			if(role.getType()!=V3xOrgEntity.ROLETYPE_RELATIVEROLE
					&&role.getBond() != V3xOrgEntity.ROLE_BOND_DEPARTMENT
					&&!role.getName().equals(V3xOrgEntity.ORGENT_META_KEY_ACCOUNTMANAGER)
					&&!role.getName().equals(EdocRoleHelper.accountEdocCreateRoleName)
					&&!(role.getName().equals(EdocRoleHelper.acountExchangeRoleName)&&!Functions.isEnableEdoc())){
				if(role.getName().equals(V3xOrgEntity.ORGENT_META_KEY_HRADMIN)){
					if(flag)
						roleListForPage.add(role);				
				}else if(role.getType() == V3xOrgEntity.ROLETYPE_PLUGIN){
					if(CustomOrgRole.getInstance().isValidRole(role.getName()))
						roleListForPage.add(role);
				}else {
					roleListForPage.add(role);
				}				
			}
		}
		
		// 分页
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(roleListForPage.size());
		List<V3xOrgRole> sublist = null;
		if ((first + pageSize) > roleListForPage.size()) {
			sublist = roleListForPage.subList(first, roleListForPage.size());
		} else {
			sublist = roleListForPage.subList(first, first + pageSize);
		}
		
		for(V3xOrgRole rol : sublist){
			String members = "";
			
			ml = orgManagerDirect.getMemberByRole(rol.getBond(), rol.getOrgAccountId(), rol.getId());
			if(ml!=null&&ml.size()!=0){
				for(V3xOrgMember vom : ml){
 					if(vom!=null){
 						members+= vom.getName()+",";
					}
				}
				if(members!=null&&!members.equals("")){
					roleMembers.add(members.substring(0, members.length()-1));
				}
			}else{
				roleMembers.add(members);
			}
			
		}

		mav.addObject("rm", roleMembers);
		mav.addObject("roleList", sublist);
		return mav;
	}
	
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		String oper = request.getParameter("oper");	
		InternalResourceView ss = new InternalResourceView("/roleManage.do?method=summary&oper="+oper+"&id="+id);
		ss.render(null, request, response);
		return null;
	}
	
	public ModelAndView summary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("sysMgr/role/roleSummary");
		String id = request.getParameter("id");
		String oper = request.getParameter("oper");
		V3xOrgRole role = null;
		String members = "";
		String membersId = "";
		List<V3xOrgMember> ml = new ArrayList<V3xOrgMember>();
		
		if(id!=null&&!id.equals("")){
			role = orgManagerDirect.getRoleById(new Long(id));
			ml = orgManagerDirect.getMemberByRole(role.getBond(), role.getOrgAccountId(), role.getId());
			if(ml!=null&&ml.size()!=0){
				for(V3xOrgMember vom : ml){
					if(vom!=null){
						members+= vom.getName()+",";
						membersId+=vom.getId()+",";
					}
				}
				if(members!=null&&!members.equals("")){
					mav.addObject("rm", members.substring(0, members.length()-1));
					mav.addObject("rmId", membersId.substring(0, membersId.length()-1));
				}
			}
			mav.addObject("bean", role);
		}else{
			//取得最大排序号
			Integer maxSortId = orgManagerDirect.getMaxSortNum(V3xOrgRole.class.getSimpleName(), CurrentUser.get().getLoginAccount());
			V3xOrgRole roleNew = new V3xOrgRole();
			roleNew.setId(null);
			roleNew.setSortId(maxSortId+1);
			roleNew.setType(3);
		    mav.addObject("bean", roleNew);
		}
	    
	    mav.addObject("oper", oper);
		return mav;
	}
	
	public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
		InternalResourceView ss = new InternalResourceView("/roleManage.do?method=summary&oper=create");
		ss.render(null, request, response);
		return null;
	}
	
	
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		PrintWriter out = response.getWriter();
		Locale local = LocaleContext.getLocale(request);
		String id = request.getParameter("id");
		String bond = request.getParameter("bond");
		String memberIds = request.getParameter("ids");
		String[] memberIdsArr = memberIds.split(",");
		Long[] memberIdsArray = null;
		int i = 0;	
		if(memberIds!=null&&!memberIds.equals("")){
			memberIdsArray =  new Long[memberIdsArr.length];
			for(String memberId : memberIdsArr){
				memberIdsArray[i] = Long.parseLong(memberId);
				i++;
			}
		}
		V3xOrgRole role = null;
		if(id!=null&&!id.equals("")){
			role = orgManagerDirect.getRoleById(new Long(id));
			int orgbond = role.getBond();
			if(StringUtils.isBlank(bond)){
				bond = String.valueOf(orgbond); 
			}
	        //排序号的重复处理		
			Integer orgSortId = role.getSortId();
			Integer sortId = Integer.valueOf(request.getParameter("sortId"));		
			String isInsert = request.getParameter("isInsert");
	        if(!orgSortId.equals(sortId)&&isInsert.equals("1")&&orgManagerDirect.isPropertyDuplicated(V3xOrgRole.class.getSimpleName(), "sortId", sortId,role.getId())){
	        	orgManagerDirect.insertRepeatSortNum(V3xOrgRole.class.getSimpleName(), CurrentUser.get().getLoginAccount(), sortId);
			}
			role.setUpdateTime(new Date());
			super.bind(request,role);			
			//如果原始关联为单位则删除单位角色，如果为部门则删除部门角色
			V3xOrgAccount roleAccount = orgManagerDirect.getAccountById(role.getOrgAccountId());
			if(orgbond!=role.getBond()){
				List<V3xOrgEntity> entList = orgManagerDirect.getEntityList("V3xOrgRelationship", "objectiveId", role.getId().toString(), role.getOrgAccountId());
				for(V3xOrgEntity ent:entList){
					orgManagerDirect.deleteEntity(ent);
				}
				orgManagerDirect.addRole2Member(orgbond, CurrentUser.get().getLoginAccount(), role.getId(), new Long[0]);			
			}	
			orgManagerDirect.updateEntity(role);
			if(memberIdsArray!=null){
				//表单管理员做特殊校验
				if(role!=null&&role.getName().equals(V3xOrgEntity.ORGENT_META_KEY_FORMADMIN)){
					List<V3xOrgMember> roleMemberList = orgManagerDirect.getMemberByRole(V3xOrgEntity.ROLE_BOND_ACCOUNT, CurrentUser.get().getLoginAccount(), role.getId());
					for(V3xOrgMember mem:roleMemberList){
						boolean isIn = false;
						for(int j=0;j<memberIdsArray.length;j++){
							if(memberIdsArray[j].equals(mem.getId())){
								isIn = true;
							}
						}
						//如果人员的角色被取消
						if(!isIn){						
							if(iOperBase.queryOwnerListByownerid(mem.getId())){
								out = response.getWriter();
								out.println("<script>");
								out.println("alert('"+ Constant.getString("account.update.change.role",local)+"');");
								out.println("</script>");	
								out.flush();
								return this.refreshWorkspace();								
							}
						}
					}
				}
				orgManagerDirect.addRole2Member(Integer.parseInt(bond), CurrentUser.get().getLoginAccount(), role.getId(), memberIdsArray);
			}else{
				//表单管理员做特殊处理
				if(role!=null&&role.getName().equals(V3xOrgEntity.ORGENT_META_KEY_FORMADMIN)){
					List<V3xOrgMember> roleMemberList = orgManagerDirect.getMemberByRole(V3xOrgEntity.ROLE_BOND_ACCOUNT, CurrentUser.get().getLoginAccount(), role.getId());
					for(V3xOrgMember mem:roleMemberList){
						if(iOperBase.queryOwnerListByownerid(mem.getId())){
								out = response.getWriter();
								out.println("<script>");
								out.println("alert('"+ Constant.getString("account.update.change.role",local)+"');");
								out.println("</script>");	
								out.flush();
								return this.refreshWorkspace();
						}
					}						
				}
				orgManagerDirect.addRole2Member(Integer.parseInt(bond), CurrentUser.get().getLoginAccount(), role.getId(), new Long[0]);
			}
            //重新载入单位
			if(V3xOrgEntity.ROLE_BOND_ACCOUNT == Integer.parseInt(bond)){
				orgManagerDirect.updateEntity(roleAccount);
			}
			//日志
			User user = CurrentUser.get();
			
			String resource = "com.seeyon.v3x.system.resources.i18n.SysMgrResources";
			Locale locale = LocaleContext.getLocale(request);
			String roleName = ResourceBundleUtil.getString(resource, locale, "sys.role.rolename."+role.getName());

			appLogManager.insertLog(user, AppLogAction.Organization_UpdateRole, user.getName(),roleName);
		}else{
			role = new V3xOrgRole();
			role.setCreateTime(new Date());
			role.setOrgAccountId(CurrentUser.get().getLoginAccount());
			role.setType(V3xOrgEntity.ROLETYPE_USERROLE);
			V3xOrgAccount roleAccount = orgManagerDirect.getAccountById(role.getOrgAccountId());
			super.bind(request,role);
			List<V3xOrgRole> rolesList = new ArrayList<V3xOrgRole>();
			rolesList.add(role);
			//排序号的重复处理
			String isInsert = request.getParameter("isInsert");
	        if(isInsert.equals("1")&&orgManagerDirect.isPropertyDuplicated(V3xOrgRole.class.getSimpleName(), "sortId", String.valueOf(role.getSortId()))){
	        	orgManagerDirect.insertRepeatSortNum(V3xOrgRole.class.getSimpleName(), CurrentUser.get().getLoginAccount(), role.getSortId());
			}
			orgManagerDirect.addRole(role);
			if(memberIdsArray!=null){
				orgManagerDirect.addRole2Member(Integer.parseInt(bond), CurrentUser.get().getLoginAccount(), role.getId(), memberIdsArray);
			}else{
				orgManagerDirect.addRole2Member(Integer.parseInt(bond), CurrentUser.get().getLoginAccount(), role.getId(), new Long[0]);
			}
            //重新载入单位
			//Fix BUG AEIGHT-9568 lilong 20130606 角色授权后不必重载这个单位的缓存，否则如果加载过慢会造成当前单位组织信息不可用
//			if(V3xOrgEntity.ROLE_BOND_ACCOUNT == Integer.parseInt(bond)){
//				orgManagerDirect.updateEntity(roleAccount);
//			}
		}
		//提示成功
		//out.println("<script>");
		//out.println("alert('"+ Constant.getString("organization.yes",local)+"');");
		//out.println("</script>");
		super.rendJavaScript(response, "parent.parent.detailFrame.location.href=\"/seeyon/common/detail.jsp\";parent.parent.listFrame.location.reload(true);");
		return null;
	}

	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String ids = request.getParameter("id");
		String[] idArray = ids.split(","); 
		V3xOrgRole role = null;
		for(String id:idArray){
			role = orgManagerDirect.getRoleById(new Long(id));
			orgManagerDirect.deleteEntity(role);
		}
		return this.refreshWorkspace();
	}
	
	public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelAndView mav = new ModelAndView("sysMgr/role/roleSetting");
		//todo:暂时不提交
		List<V3xOrgRole> roleList = orgManagerDirect.getAllRoles(CurrentUser.get().getLoginAccount(),false);
		int loop = roleList.size();
		V3xOrgRole role = null;
		List<String> roleMembers = new ArrayList<String>();
		List<String> roleMembersId = new ArrayList<String>();
		List<V3xOrgMember> ml = new ArrayList<V3xOrgMember>();
		List<V3xOrgRole> roleListForPage = new ArrayList<V3xOrgRole>();

		
		boolean flag = SystemEnvironment.hasPlugin("hr");
		//将相对角色及部门角色、单位管理员、单位主管、hr管理员、公文发起权角色过滤掉
		for(int i = 0 ; i < loop ; i++ ){
			role = roleList.get(i);
			if(role.getType()!=V3xOrgEntity.ROLETYPE_RELATIVEROLE
					&&role.getBond() != V3xOrgEntity.ROLE_BOND_DEPARTMENT
					&&!role.getName().equals(V3xOrgEntity.ORGENT_META_KEY_ACCOUNTMANAGER)
					&&!role.getName().equals(EdocRoleHelper.accountEdocCreateRoleName)
					&&!(role.getName().equals(EdocRoleHelper.acountExchangeRoleName)&&!Functions.isEnableEdoc())){
				if(role.getName().equals(V3xOrgEntity.ORGENT_META_KEY_HRADMIN) || role.getName().equals(V3xOrgEntity.ORGENT_META_KEY_SALARYADMIN)){
					if(flag)
						roleListForPage.add(role);				
				}else if(role.getType() == V3xOrgEntity.ROLETYPE_PLUGIN){
					if(CustomOrgRole.getInstance().isValidRole(role.getName()))
						roleListForPage.add(role);
				}else if((Boolean)SysFlag.sys_isGovVer.getFlag() && V3xOrgEntity.ORGENT_META_KEY_FORMADMIN.equals(role.getName())) {
					if(SystemEnvironment.hasPlugin("form"))
						roleListForPage.add(role);
				}
				else {
					roleListForPage.add(role);
				}				
			}
		}
		
		// 分页
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(roleListForPage.size());
		List<V3xOrgRole> sublist = null;
		if ((first + pageSize) > roleListForPage.size()) {
			sublist = roleListForPage.subList(first, roleListForPage.size());
		} else {
			sublist = roleListForPage.subList(first, first + pageSize);
		}
		
		for(V3xOrgRole rol : sublist){
			String members = "";
			String membersId = "";
			ml = orgManagerDirect.getMemberByRole(rol.getBond(), rol.getOrgAccountId(), rol.getId());
			if(ml!=null&&ml.size()!=0){
				for(V3xOrgMember vom : ml){
 					if(vom!=null){
 						final String sep = "、";
						if(CurrentUser.get().getLoginAccount() != vom.getOrgAccountId()){
 							V3xOrgAccount account = orgManagerDirect.getAccountById(vom.getOrgAccountId());
 							members+= vom.getName()+"("+account.getShortname()+")"+sep;
 						}else{
 							members+= vom.getName()+sep;
 						}
 						membersId+=vom.getId()+",";
					}
				}
				if(members!=null&&!members.equals("")&& membersId!=null&&!membersId.equals("")){
					roleMembers.add(members.substring(0, members.length()-1));
					roleMembersId.add(membersId.substring(0, membersId.length()-1));
				}
			}else{
				roleMembers.add(members);
				roleMembersId.add(membersId);
			}
			
		}

		mav.addObject("rm", roleMembers);
		mav.addObject("rmId", roleMembersId);
		mav.addObject("roleList", sublist);
		//System.out.println(request.getParameter("update"));
		//mav.addObject("update", request.getParameter("update"));
		//ModelAndView mav = new ModelAndView("sysMgr/role/roleFrame");
		return mav;
	}
	public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		Locale local = LocaleContext.getLocale(request);
		String [] ids = request.getParameterValues("id");
		String bond = request.getParameter("bond");
		if(ids!=null && ids.length>0){
			for (int g = 0; g < ids.length; g++) {
				String memberIds = request.getParameter("ids"+g);
				String id = ids[g];
				String[] memberIdsArr = memberIds.split(",");
				Long[] memberIdsArray = null;
				
				int index = 0;	
				if(memberIds!=null&&!memberIds.equals("")){
					memberIdsArray =  new Long[memberIdsArr.length];
					for(String memberId : memberIdsArr){
						memberIdsArray[index] = Long.parseLong(memberId);
						index++;
					}
				}
				V3xOrgRole role = null;				
				if(id!=null&&!id.equals("")){
					role = orgManagerDirect.getRoleById(new Long(id));
					logger.debug("日志角色开始授权当前角色"+role.getName());
					long startTime=System.currentTimeMillis();   //获取开始时间
					if(memberIdsArray!=null){
						orgManagerDirect.addRole2Member(Integer.parseInt(bond), CurrentUser.get().getLoginAccount(), role.getId(), memberIdsArray);
					}else{
						orgManagerDirect.addRole2Member(Integer.parseInt(bond), CurrentUser.get().getLoginAccount(), role.getId(), new Long[0]);
					}
					long endTime=System.currentTimeMillis(); //获取结束时间
					logger.debug(role.getName()+"角色授权时间： "+(endTime-startTime)+"ms"); 
				}else{
					out.println("<script>");
					out.println("alert('"+ Constant.getString("account.role.new.alert",local)+"');");
					out.println("window.parent.location.reload();");
					out.println("</script>");	
					out.flush();
				}
			}
			//日志
			User user = CurrentUser.get();
			appLogManager.insertLog(user, AppLogAction.Organization_UpdateRole, user.getName());
			//重新载入单位
			//orgManagerDirect.updateEntity(roleAccount);//客户问题深圳一致药业更新角色后加载缓慢
		}
		out.println("<script>");
		out.println("alert('"+Constants.getString4CurrentUser("system.manager.ok")+"');");
		out.println("window.parent.location.reload();");
		out.println("</script>");	
		out.flush();
		//request.setAttribute("update", "update");
		return null;
	}
	public ModelAndView openHelp(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("sysMgr/role/roleHelp");
		return mav;
	}




}
