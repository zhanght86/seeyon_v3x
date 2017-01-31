package com.seeyon.v3x.system.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.collaboration.templete.manager.TempleteConfigManager;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgRelationship;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgMember;
import com.seeyon.v3x.util.Strings;

@CheckRoleAccess(roleTypes={RoleType.GroupAdmin})
public class DistributeManagerController extends BaseController {

	private OrgManager orgManager;
	
	private OrgManagerDirect orgManagerDirect;

	private MetadataManager metadataManager;

    private TempleteConfigManager templeteConfigManager;
    
    private AppLogManager appLogManager;

    private SearchManager searchManager;

	public AppLogManager getAppLogManager() {
		return appLogManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public void setTempleteConfigManager(TempleteConfigManager templeteConfigManager) {
        this.templeteConfigManager = templeteConfigManager;
    }

    public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

	/**
	 * 进入未分配人员上下结构部分
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView distributeFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"sysMgr/distribute/distributeFrame");
		return result;
	}

	/**
	 * 进入为分配人员管理数据方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			User user = CurrentUser.get();
			List<V3xOrgMember> memberlist = orgManager.getAllUnAssignedMembers();

			ModelAndView result = new ModelAndView(
					"sysMgr/distribute/listDistributeMember");

			List<WebV3xOrgMember> resultlist = new ArrayList<WebV3xOrgMember>();
			long deptId = -1;
			long levelId = -1;
			long postId = -1;
			long accountId = -1;

			if (null != memberlist) {
				for (V3xOrgMember member : memberlist) {
					deptId = member.getOrgDepartmentId();
					levelId = member.getOrgLevelId();
					postId = member.getOrgPostId();
					accountId = member.getOrgAccountId();

					WebV3xOrgMember webMember = new WebV3xOrgMember();
					webMember.setV3xOrgMember(member);
					V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
					if (dept != null) {
						webMember.setDepartmentName(dept.getName());
					}

					V3xOrgLevel level = orgManager.getLevelById(levelId);
					if (null != level) {
						webMember.setLevelName(level.getName());
					}

					V3xOrgPost post = orgManager.getPostById(postId);
					if (null != post) {
						webMember.setPostName(post.getName());
					}

					V3xOrgAccount account = orgManager.getAccountById(accountId);
					if (null != account) {
						webMember.setAccountName(account.getName());
					}

					resultlist.add(webMember);
				}
			}


//			 获得单位类别下拉列表中的数据
			Map<String, Metadata> orgMeta = metadataManager.getMetadataMap(ApplicationCategoryEnum.organization);
			result.addObject("orgMeta", orgMeta);
			
			// 取得所有的岗位,以便初始化查询条件的岗位下拉列表
			List<V3xOrgPost> postlist = orgManager.getAllPosts(user.getLoginAccount(),false,false);
			result.addObject("postlist", postlist);

			// 取得所有职务级别
			List<V3xOrgLevel> levellist = orgManager.getAllLevels(user.getLoginAccount(),false,false);
			result.addObject("levellist", levellist);
			// 取得所有的部门
			List<V3xOrgDepartment> departmentlist = orgManager.getAllDepartments(user.getLoginAccount(),false,false);
			result.addObject("departmentlist", departmentlist);
			
			result.addObject("memberlist", resultlist);

			result.addObject("size", resultlist.size()) ;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * method: 未分配人员根据条件模糊查询(人员名称，原单位名称)
	 * @param request
	 * @param response
	 * @return 
	 * @throws Exception
	 */
	public ModelAndView queryUnAssignedMembersByCondition(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView view = new ModelAndView("sysMgr/distribute/listDistributeMember");
		String condition = request.getParameter("condition");
		String name = request.getParameter("textfieldName") ;
		String corp = request.getParameter("textfieldCorp") ;
		
		List<WebV3xOrgMember> resultlist = new ArrayList<WebV3xOrgMember>();
		List<WebV3xOrgMember> result_list = new ArrayList<WebV3xOrgMember>();
		List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>() ;
		
		User user = CurrentUser.get();
		// 根据人名查询
		if (Strings.isNotBlank(condition) && "name".equals(condition)) {
			Map<String,Object> fieldMap = new HashMap<String,Object>();
			String textfieldName = request.getParameter("textfieldName") ;
			
			String mHql = "select m from "+V3xOrgMember.class.getName()+" m where m.name like (:name) and m.orgLevelId!=-1 and m.isDeleted=0 and m.isAssigned=0";
			if (textfieldName != null && !"".equals(textfieldName)) {
				fieldMap.put("name", "%"+textfieldName+"%");  
				memberlist = searchManager.searchByHql(mHql, fieldMap, false);
			}
			view.addObject("textfieldName", name);
		} 
		// 根据原单位进行查询
		else if (Strings.isNotBlank(condition) && "corp".equals(condition)) {
			Map<String,Object> fieldMap = new HashMap<String,Object>();
			Map<String,Object> aFieldMap = new HashMap<String,Object>();
			
			String textfieldCorp = request.getParameter("textfieldCorp") ;
			
			if (textfieldCorp != null && !"".equals(textfieldCorp.trim())) {
				String aHql = "select a.id from "+V3xOrgAccount.class.getName()+" a where a.name like (:corp)";
				aFieldMap.put("corp", "%"+textfieldCorp+"%"); 
				List<Long> account = searchManager.searchByHql(aHql, aFieldMap, false);
				if (account != null && account.size() > 0) {
					String mHql = "select m from "+V3xOrgMember.class.getName()+" m where m.orgAccountId in (:accountId) and m.orgLevelId!=-1 and m.isDeleted=0 and m.isAssigned=0";
					fieldMap.put("accountId", account);  
					memberlist = searchManager.searchByHql(mHql, fieldMap, false);
				}
			}
			view.addObject("textfieldCorp", textfieldCorp);
		} else if (Strings.isNotBlank(condition) && "choice".equals(condition)) {
			Map<String,Object> fieldMap = new HashMap<String,Object>();
			String mHql = "select m from "+V3xOrgMember.class.getName()+" m where m.orgLevelId!=-1 and m.isDeleted=0 and m.isAssigned=0";
			memberlist = searchManager.searchByHql(mHql, fieldMap, false);
		}
		
		long deptId = -1;
		long levelId = -1;
		long postId = -1;
		long accountId = -1;
		if (null != memberlist && memberlist.size() > 0) {
			for (V3xOrgMember member : memberlist) {
				deptId = member.getOrgDepartmentId();
				levelId = member.getOrgLevelId();
				postId = member.getOrgPostId();
				accountId = member.getOrgAccountId();

				WebV3xOrgMember webMember = new WebV3xOrgMember();
				webMember.setV3xOrgMember(member);
				V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
				if (dept != null) {
					webMember.setDepartmentName(dept.getName());
				}

				V3xOrgLevel level = orgManager.getLevelById(levelId);
				if (null != level) {
					webMember.setLevelName(level.getName());
				}

				V3xOrgPost post = orgManager.getPostById(postId);
				if (null != post) {
					webMember.setPostName(post.getName());
				}

				V3xOrgAccount account = orgManager.getAccountById(accountId);
				if (null != account) {
					webMember.setAccountName(account.getName());
				}

				resultlist.add(webMember);
			}
		}
		view.addObject("condition", condition);
		
		// 此处为对分页做的处理
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(resultlist.size());
		for(int i = first ; i < first + pageSize ; i ++) {
			if(i > resultlist.size()-1)
				break;
			result_list.add(resultlist.get(i));
		}
		
		view.addObject("memberlist", resultlist);
		
		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager.getMetadataMap(ApplicationCategoryEnum.organization);
		view.addObject("orgMeta", orgMeta);
		
		// 取得所有的岗位,以便初始化查询条件的岗位下拉列表
		List<V3xOrgPost> postlist = orgManager.getAllPosts(user.getLoginAccount(),false,false);
		view.addObject("postlist", postlist);

		// 取得所有职务级别
		List<V3xOrgLevel> levellist = orgManager.getAllLevels(user.getLoginAccount(),false,false);
		view.addObject("levellist", levellist);
		// 取得所有的部门
		List<V3xOrgDepartment> departmentlist = orgManager.getAllDepartments(user.getLoginAccount(),false,false);
		view.addObject("departmentlist", departmentlist);
		
		view.addObject("size", memberlist.size()) ;
		
		return view ;
	}
	
	/**
	 * 进入修改人员管理界面方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView editMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("sysMgr/distribute/distributeEditMember");
		String id = request.getParameter("id");
		V3xOrgMember member = orgManager.getUnAssignedMemberById(Long.parseLong(id));
		long deptId = member.getOrgDepartmentId();
		long levelId = member.getOrgLevelId();
		long postId = member.getOrgPostId();
		long accountId = member.getOrgAccountId();
		WebV3xOrgMember webMember = new WebV3xOrgMember();
		webMember.setV3xOrgMember(member);
		V3xOrgDepartment dept = orgManager.getDepartmentById(deptId);
		if (dept != null) {
			webMember.setDepartmentName(dept.getName());
		}

		V3xOrgLevel level = orgManager.getLevelById(levelId);
		if (null != level) {
			webMember.setLevelName(level.getName());
		}

		V3xOrgPost post = orgManager.getPostById(postId);
		if (null != post) {
			webMember.setPostName(post.getName());
		}
		
		V3xOrgAccount account = orgManager.getAccountById(accountId);
		if (null != account) {
			webMember.setAccountName(account.getName());
		}


		// 取得人员的副岗
		List<MemberPost> memberPosts = member.getSecond_post();
		if (null != memberPosts && !memberPosts.isEmpty()) {
			StringBuffer deptpostbuffer = new StringBuffer();
			for (MemberPost memberPost : memberPosts) {
				StringBuffer sbuffer = new StringBuffer();
				Long deptid = memberPost.getDepId();
				V3xOrgDepartment v3xdept = orgManager.getDepartmentById(deptid);
				sbuffer.append(v3xdept.getName());
				sbuffer.append("|");
				Long postid = memberPost.getPostId();
				V3xOrgPost v3xpost = orgManager.getPostById(postid);
				sbuffer.append(v3xpost.getName());
				deptpostbuffer.append(sbuffer.toString());
				deptpostbuffer.append(",");
			}
			if (deptpostbuffer.length() > 0) {
				deptpostbuffer.substring(0, deptpostbuffer.length() - 1);
				webMember.setSecondPosts(deptpostbuffer.toString());
			}
		}
		result.addObject("member", webMember);
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
		} 

		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		result.addObject("orgMeta", orgMeta);

		// 获取个人角色信息---------xut
		V3xOrgRole role = null;
		List<String> roleNameList = new ArrayList<String>();
		// V3xOrgAccount account = null;
//		List<V3xOrgEntity> roleList = orgManager.getUserDomain(new Long(id),V3xOrgEntity.ORGENT_TYPE_ROLE);
//		for (int i = 0; i < roleList.size(); i++) {
//			role = (V3xOrgRole) roleList.get(i);
//			roleNameList.add(role.getName());
//			// if(role.getType()==V3xOrgEntity.ROLE_BOND_ACCOUNT){
//			// account = orgManager.getAccountById(role.getOrgAccountId());
//			// }
//		}
		result.addObject("roleNameList", roleNameList);
		// 判断是否回显密码
		result.addObject("showPassword", 1);
		return result;
	}

	/**
	 * 修改人员管理界面方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		V3xOrgMember model = new V3xOrgMember();
		bind(request, model);
		V3xOrgMember member = orgManager.getMemberById(model.getId());
		BeanUtils.copyProperties(member, model);

		String strSecondPostIds = request.getParameter("secondPostIds");
		String[] arrSecondPosts = strSecondPostIds.split(",");

		if (null != arrSecondPosts && arrSecondPosts.length > 0) {
			HashMap<Long, Long> second_post = new HashMap<Long, Long>();
			for (String secondpostid : arrSecondPosts) {
				String[] arrDeptPosts = secondpostid.split("_");
				if (null != arrDeptPosts[0] && !arrDeptPosts[0].equals("")
						&& null != arrDeptPosts[1]
						&& !arrDeptPosts[1].equals("")) {
					member.addSecondPost(Long.parseLong(arrDeptPosts[0]), Long
							.parseLong(arrDeptPosts[1]));
				}
			}
		}

		orgManager.updateEntity(member);

		// 提示用户操作成功
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert(parent.v3x.getMessage('sysMgrLang.system_post_ok'));");
		out.println("</script>");

		return super.refreshWorkspace();
		// return redirectModelAndView("/organization.do?method=listMember");
	}

	/**
	 * 删除取消人员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView destroyMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] membetIds = request.getParameterValues("id");
		List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
		for (String string : membetIds) {
			Long ids = Long.parseLong(string);
			orgManager.deleteEntity(V3xOrgMember.class, ids, V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			memberList.add(orgManager.getMemberById(ids));
		}
        //日志信息                
		List<String[]> appLogs = new ArrayList<String[]>();
        User user = CurrentUser.get();	
		for (V3xOrgMember member1 : memberList) {
			String[] appLog = new String[2];
			appLog[0] = user.getName();
			appLog[1] = member1.getName();
			appLogs.add(appLog);
		}
		appLogManager.insertLogs(user, AppLogAction.Organization_DeleteCancelMember, appLogs);

		// 提示用户操作成功
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert(parent.v3x.getMessage('sysMgrLang.system_post_ok'));");
		out.println("</script>");
		return super.refreshWorkspace();
		// return redirectModelAndView("/organization.do?method=listMember");
	}
	/**
	 * 	进入弹出页面方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView ballAccount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("sysMgr/distribute/returnDistributeMember");
		List<V3xOrgAccount> list = orgManager.getAllAccounts();
		//去掉停用单位及集团根单位
		List<V3xOrgAccount> accountList = new ArrayList<V3xOrgAccount>();
		for(V3xOrgAccount account:list){
			if(account.getEnabled()&&!account.getIsRoot())
				accountList.add(account);
		}
		modelAndView.addObject("listAllAccount", accountList);
		return modelAndView;
	}
	
	/**
	 * 	将选中的人员分配到相应的单位去
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView updateDistributeMessage(HttpServletRequest request,HttpServletResponse response) {
		// 获得人员 ID
		String[] membetIds = request.getParameterValues("id");
		// 获得单位 ID
		Long accountId = Long.parseLong(request.getParameter("accountId"));
		List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
		V3xOrgMember member ;
		for (String string : membetIds) {
			Long ids = Long.parseLong(string);
			try {
				member = orgManagerDirect.getMemberById(ids);
				member.setIsAssigned(true);
				//重新分配人员后其排序号变为最大
				member.setSortId(orgManagerDirect.getMaxMemberSortNum(accountId)+1);
				if(!member.getOrgAccountId().equals(accountId)){
					member.setOrgAccountId(accountId);
					V3xOrgLevel lev = orgManagerDirect.getLowestLevel(accountId);
					if(lev!=null){
						member.setOrgLevelId(lev.getId());
					}else{
						member.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
					}					
					member.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
					member.setOrgDepartmentId(V3xOrgEntity.DEFAULT_NULL_ID);
					member.setSecond_post(new ArrayList<MemberPost>());
					// 解除人员的角色关系
					orgManager.deleteRelationships("type", V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE, "sourceId", member.getId());
					orgManager.deleteRelationships("type", V3xOrgEntity.ORGREL_TYPE_MEMBER_ACCROLE, "sourceId", member.getId());
					// 删除副岗
					orgManager.deleteRelationships("type", V3xOrgEntity.ORGREL_TYPE_MEMBER_POST, "sourceId", member.getId());
					// 删除人员兼职
					orgManager.deleteRelationships("type", V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST, "sourceId", member.getId());
					
					// 移动个人组
					List<V3xOrgTeam> allTeams = orgManager.getAllTeams(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
					for (V3xOrgTeam team : allTeams) {
						if(!team.getOwnerId().equals(ids))continue;
						if(team.getOrgAccountId().equals(accountId))continue;
						team.setOrgAccountId(accountId);
						orgManager.updateEntity(team); 
						List<V3xOrgRelationship> rels = orgManager.getRelationships("sourceId",team.getId());
						for (V3xOrgRelationship rel : rels) {
							// 从原单位移除
							orgManager.deleteEntity(rel);
							// 添加到新单位
							rel.setOrgAccountId(accountId); 
							orgManager.addEntity(rel);
						}					
					}					
					
				}
				orgManager.updateMember(member);
				memberList.add(member);
                //将授权范围内的模板推送到该用户的首页
                templeteConfigManager.pushAvailabileTemplete4Member(member.getId());
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
        //日志信息                
		List<String[]> appLogs = new ArrayList<String[]>();
        User user = CurrentUser.get();	
        try {
			V3xOrgAccount account = orgManager.getAccountById(accountId);
			for (V3xOrgMember member1 : memberList) {
				String[] appLog = new String[3];
				appLog[0] = user.getName();
				appLog[1] = member1.getName();
				appLog[2] = account.getName();
				appLogs.add(appLog);
			}
		} catch (BusinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		appLogManager.insertLogs(user, AppLogAction.Organization_OrgMember, appLogs);

		// 提示用户操作成功
		PrintWriter out;
		try {
			out = response.getWriter();
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('sysMgrLang.system_post_ok'));");
			out.println("</script>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return super.refreshWorkspace();//redirectModelAndView("/distribute.do?method=listMember");
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
}
