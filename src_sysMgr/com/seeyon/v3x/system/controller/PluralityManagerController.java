package com.seeyon.v3x.system.controller;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.collaboration.templete.manager.TempleteConfigManager;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ListSearchHelper;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.menu.domain.Security;
import com.seeyon.v3x.menu.manager.MenuManager;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgRelationship;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgAccount;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgRelationship;
import com.seeyon.v3x.util.Strings;

@CheckRoleAccess(roleTypes={RoleType.GroupAdmin, RoleType.Administrator, RoleType.HrAdmin})
public class PluralityManagerController extends BaseController {
	
	private OrgManagerDirect orgManagerDirect;
	private OrgManager orgManager;
	private FileToExcelManager fileToExcelManager;
	private MenuManager menuManager;
	private AppLogManager appLogManager;
	private TempleteConfigManager templeteConfigManager;
	
	
	
	public void setTempleteConfigManager(TempleteConfigManager templeteConfigManager) {
		this.templeteConfigManager = templeteConfigManager;
	}

	public MenuManager getMenuManager() {
		return menuManager;
	}

	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public FileToExcelManager getFileToExcelManager() {
		return fileToExcelManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
	
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	/**
	 * 兼职主框架
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView pluralityFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("sysMgr/plurality/pluralityFrame");
		return result;
	}
	
	/**
	 * 兼职列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listPlurality(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ListSearchHelper.pickupExpression(request, null);
		ModelAndView result = new ModelAndView("sysMgr/plurality/listPlurality");
		// 判断是系统管理员还是单位管理员进入的页面
		User user = CurrentUser.get();
		List cntListOrg =  new ArrayList();
		List<V3xOrgRelationship> outCntListOrg = new ArrayList<V3xOrgRelationship>();
		
		
		if(orgManagerDirect.isGroupAdmin(user.getLoginName())){
			result.addObject("curUser", "systemAdmin");
			//获得当前排序号的最大值
	        Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgAccount.class.getSimpleName(), CurrentUser.get().getLoginAccount());
	        V3xOrgAccount account = new V3xOrgAccount();
	        account.setSortId(maxSortNum+1);
	        account.setEnabled(true);
	        WebV3xOrgAccount webaccount = new WebV3xOrgAccount();
	        webaccount.setV3xOrgAccount(account);        
	        result.addObject("account", webaccount);
			// 获取所有的单位,以便初始化下来列表
	        List<V3xOrgAccount> accountlist = orgManagerDirect.getAllAccounts();
	        result.addObject("accountlist", accountlist);
	        String mname=request.getParameter("sname");
	        String accountId=request.getParameter("saccount");
	        String cntaccountid=request.getParameter("scntaccount");
	        String condition=request.getParameter("condition");
	        if(Strings.isNotBlank(condition))
	        	result.addObject("condition", condition);
	        if(Strings.isNotBlank(mname))
			{
				//按人员姓名模糊查询
	        	cntListOrg = orgManagerDirect.getAllConcurrentPostByMname(mname);
	        	result.addObject("showV", mname);
			}else if(Strings.isNotBlank(accountId))
			{//按原单位查询
				cntListOrg = orgManagerDirect.getAllConcurrentPostByOriginalAccount(Long.valueOf(accountId));
				result.addObject("showV", accountId);
			}
			else if(Strings.isNotBlank(cntaccountid))
			{//按兼职单位查询
				cntListOrg = orgManagerDirect.getAllConcurrentPostByAccount(Long.valueOf(cntaccountid));
				result.addObject("showV", cntaccountid);
			}else{
			//得到所有跨单位兼职列表
			cntListOrg = orgManagerDirect.getAllConcurrentPostByAccount(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			}
			List<WebV3xOrgRelationship> cntList = new ArrayList<WebV3xOrgRelationship>();
			Iterator cntIt = cntListOrg.iterator();
			while(cntIt.hasNext()){
				V3xOrgRelationship rel = (V3xOrgRelationship)cntIt.next();
				V3xOrgMember vom = orgManagerDirect.getMemberById(rel.getSourceId());
				if(null == vom) continue;//客户BUG增加由于数据问题引起的null指针
				WebV3xOrgRelationship webRel = new WebV3xOrgRelationship();
				webRel.setV3xOrgRelationship(rel);
				webRel.setOrgAccountId(vom.getOrgAccountId());
				webRel.setOrgPostId(vom.getOrgPostId());
				cntList.add(webRel);
			}
			result.addObject("cntList", cntList);
			//System.out.println("cntList=="+cntList.size());
		}else{
			result.addObject("curUser", "accountAdmin");
			//判断是本单位兼职出去的还是兼职到本单位的
			String isIn = request.getParameter("isIn");
			if(isIn!=null&&isIn.equals("0")){
				//得到本单位兼职出去的兼职列表
				outCntListOrg = orgManagerDirect.getAllOutConcurrentPostByAccount(user.getLoginAccount(), true);		
				List<WebV3xOrgRelationship> outCntList = new ArrayList<WebV3xOrgRelationship>();
				for(V3xOrgRelationship rel:outCntListOrg){
					V3xOrgMember vom = orgManagerDirect.getMemberById(rel.getSourceId());
					if(null == vom) continue;//客户BUG增加由于数据问题引起的null指针
					WebV3xOrgRelationship webRel = new WebV3xOrgRelationship();
					webRel.setV3xOrgRelationship(rel);
					webRel.setOrgAccountId(vom.getOrgAccountId());
					webRel.setOrgPostId(vom.getOrgPostId());
					outCntList.add(webRel);
				}
				result.addObject("outCntList", outCntList);
				result.addObject("isIn","0");
			}else{
				//得到所有跨单位兼职列表
				cntListOrg = orgManagerDirect.getAllConcurrentPostByAccount(user.getLoginAccount());		
				//将兼职信息重新封装
				List<WebV3xOrgRelationship> cntList = new ArrayList<WebV3xOrgRelationship>();
				Iterator cntIt = cntListOrg.iterator();
				while(cntIt.hasNext()){
					V3xOrgRelationship rel = (V3xOrgRelationship)cntIt.next();
					V3xOrgMember vom = orgManagerDirect.getMemberById(rel.getSourceId());
					if(null == vom) continue;//客户BUG增加由于数据问题引起的null指针
					WebV3xOrgRelationship webRel = new WebV3xOrgRelationship();
					webRel.setV3xOrgRelationship(rel);
					webRel.setOrgAccountId(vom.getOrgAccountId());
					webRel.setOrgPostId(vom.getOrgPostId());
					cntList.add(webRel);
				}
				result.addObject("cntList", cntList);
				result.addObject("isIn","1");
			}
		}		
		return result;
	}
	
	/**
	 * 初始化新增页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.GroupAdmin})
	public ModelAndView toAddCntPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("sysMgr/plurality/addPlurality");
		WebV3xOrgRelationship cntPost = new WebV3xOrgRelationship();
		V3xOrgRelationship vors = new V3xOrgRelationship();	
		cntPost.setV3xOrgRelationship(vors);
		vors.setExtend1(new Long("-1"));
        //获取默认菜单权限
		String securityIds = null;
		String securityNames = null;
        List<Security> defaultSecurities = menuManager.getDefaultSecurities();
        for(Security security : defaultSecurities){
            if(securityIds == null){
                securityIds = security.getId().toString();
                securityNames = security.getName();
            }
            else{
                securityIds += "," + security.getId();
                securityNames += "," + security.getName();
            }
        }
        result.addObject("securityIds", securityIds);
        result.addObject("securityNames", securityNames);
		result.addObject("cntPost", cntPost);
		result.addObject("oper", "add");
		return result;
	}
	
	/**
	 * 新增跨单位兼职
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.GroupAdmin})
	public ModelAndView addCntPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = null;
		V3xOrgRelationship vors = new V3xOrgRelationship();
		vors.setType(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST);
		vors.setSourceId(RequestUtils.getLongParameter(request, "memberId"));
		vors.setExtend3(request.getParameter("number"));	
		Long orgAccountId = RequestUtils.getLongParameter(request, "cntAccountId");
		vors.setOrgAccountId(orgAccountId);
		String strCntDeptId = request.getParameter("orgDepartmentId");
		String strCntPostId = request.getParameter("orgPostId");
		String strCntLevelId = request.getParameter("orgLevelId");
		String securityIdsStr = request.getParameter("securityIds");
		if(strCntDeptId!=null&&!StringUtils.isEmpty(strCntDeptId)){
			vors.setObjectiveId(Long.parseLong(strCntDeptId));
		}
		if(strCntPostId!=null&&!StringUtils.isEmpty(strCntPostId)){
			vors.setBackupId(Long.parseLong(strCntPostId));
		}		
		//设置排序号
		vors.setExtend1(Long.valueOf(orgManagerDirect.getMaxMemberSortNum(orgAccountId))+1);
		//判断人员兼职信息是否重复
        if(!isMemberCntPostDistinct(vors)){
			Locale local = LocaleContext.getLocale(request);
			String resource = "com.seeyon.v3x.system.resources.i18n.SysMgrResources";
			String message = ResourceBundleUtil.getString(resource, local, "cntPost.cntPost.repeated");
			out = response.getWriter();
			out.println("<script>");				
			out.println("alert('"+message+"');");
			out.println("</script>");	
			super.rendJavaScript(response, "parent.doEnd();");
			return null;
		}else{
			//设置职务级别(如果人员在本单位的职务已经设置，修改此人在本单位的职务级别)
			if(strCntLevelId!=null&&!StringUtils.isEmpty(strCntLevelId)){
				Long cntLevelId = Long.parseLong(strCntLevelId);
				if(!cntLevelId.equals(vors.getExtend2())){
					List<V3xOrgRelationship> rels = orgManagerDirect.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"sourceId",vors.getSourceId(),"orgAccountId",vors.getOrgAccountId());
					if(rels!=null&&rels.size()>0){
						for(V3xOrgRelationship rel:rels){
							if(!cntLevelId.equals(rel.getExtend2())){
								rel.setExtend2(cntLevelId);
								orgManagerDirect.updateEntity(rel);
								orgManager.updateEntity(rel);							
							}
						}					
					}
				}
				vors.setExtend2(Long.parseLong(strCntLevelId));
			}
			//设置人员在兼职单位的菜单权限
            if (null != securityIdsStr && securityIdsStr.length() > 0) {
                String[] securityIds = securityIdsStr.split(",");
                List<Long> securityIdsList = new ArrayList<Long>();
                for(String idStr : securityIds){
                    securityIdsList.add(Long.parseLong(idStr));
                }
                menuManager.saveMemberSecurity( vors.getSourceId(), vors.getOrgAccountId(), securityIdsList);
            }
			orgManagerDirect.updateEntity(vors);
			//此处更新部门是为了更新部门岗位关系
			if(vors.getObjectiveId()!=null&&vors.getBackupId()!=null){
				V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(vors.getObjectiveId());
				if(!dept.getPosts().contains(vors.getBackupId())){
					dept.addDepPost(vors.getBackupId());
					orgManagerDirect.updateEntity(dept);
				}				
			}
			orgManager.updateEntity(vors);
			//推送兼职模板
			try {
				templeteConfigManager.pushNewOrgEntityTemplete4Member(V3xOrgEntity.ORGENT_TYPE_MEMBER,vors.getSourceId(),vors.getSourceId());
			} catch (Exception e) {
				logger.warn("推送兼职模板失败",e);
			}
			
			//增加日志
			User user = CurrentUser.get();
			V3xOrgMember member = orgManager.getMemberById(vors.getSourceId());
			V3xOrgAccount account = orgManager.getAccountById(vors.getOrgAccountId());
			appLogManager.insertLog(user, AppLogAction.Organization_GroupAdminAddCntPost, member.getName(), account.getName(), user.getName());
		}
        super.rendJavaScript(response, "parent.doEndAdd();");
		return null;
	}	

	/**
	 * 初始化更新跨单位兼职
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView toUpdateCntPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("sysMgr/plurality/editPlurality");;
		Long id = RequestUtils.getLongParameter(request,"id");

		V3xOrgEntity ent = orgManagerDirect.getEntityById(V3xOrgRelationship.class.getSimpleName(),id);
		//兼职为空的判断
		if(ent==null){
			PrintWriter out = response.getWriter();
			out.println("<script>");				
			out.println("alert(parent.v3x.getMessage('sysMgrLang.cnt_null'));");
			out.println("</script>");	
			return super.refreshWorkspace();
		}
		WebV3xOrgRelationship webRel = new WebV3xOrgRelationship();
		V3xOrgRelationship rel = (V3xOrgRelationship)ent;
		V3xOrgMember vom = orgManagerDirect.getMemberById(rel.getSourceId());	
		webRel.setV3xOrgRelationship(rel);
		webRel.setOrgAccountId(vom.getOrgAccountId());
		webRel.setOrgPostId(vom.getOrgPostId());
		result.addObject("cntPost", webRel);
		result.addObject("oper", "update");
		result.addObject("id", id);
		//获取该用户菜单权限
        String securityIds = null;
        String securityNames = null;
        List<Security> defaultSecurities = this.menuManager.getSecurityOfMember(rel.getSourceId(), rel.getOrgAccountId(), true);
        for(Security security : defaultSecurities){
            if(securityIds == null){
                securityIds = security.getId().toString();
                securityNames = security.getName();
            }
            else{
                securityIds += "," + security.getId();
                securityNames += "," + security.getName();
            }
        }
        result.addObject("securityIds", securityIds);
        result.addObject("securityNames", securityNames);
		return result;
	}
	
	/**
	 * 更新跨单位兼职
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateCntPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = null;
		Long id = RequestUtils.getLongParameter(request,"id");	
		V3xOrgRelationship vors = (V3xOrgRelationship)orgManagerDirect.getEntityById(V3xOrgRelationship.class.getSimpleName(), id);
		//兼职为空的判断
		if(vors==null){
			out = response.getWriter();
			out.println("<script>");				
			out.println("alert(parent.v3x.getMessage('sysMgrLang.cnt_null'));");
			out.println("</script>");	
			out.flush();
			return super.refreshWorkspace();
		}
		//先删除内存中已有的兼职
		orgManager.deleteEntity(vors);
		vors.setType(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST);
		vors.setSourceId(RequestUtils.getLongParameter(request, "memberId"));
		Long sortId = RequestUtils.getLongParameter(request, "sortId");
		if(sortId!=null){
			vors.setExtend1(sortId);
		}		
		vors.setExtend3(request.getParameter("number"));	
		Long cntAccountId = RequestUtils.getLongParameter(request, "cntAccountId");
		if(cntAccountId!=null){
			vors.setOrgAccountId(RequestUtils.getLongParameter(request, "cntAccountId"));
		}		
		String strCntDeptId = request.getParameter("orgDepartmentId");
		String strCntPostId = request.getParameter("orgPostId");
		String strCntLevelId = request.getParameter("orgLevelId");
		if(strCntDeptId!=null){
			if(!StringUtils.isEmpty(strCntDeptId)){
				vors.setObjectiveId(Long.parseLong(strCntDeptId));
			}else{
				vors.setObjectiveId(null);
			}			
		}
		if(strCntPostId!=null){
			if(!StringUtils.isEmpty(strCntPostId)){
				vors.setBackupId(Long.parseLong(strCntPostId));
			}else{
				vors.setBackupId(null);
			}			
		}	
		
		if(!isMemberCntPostDistinct(vors)){
			Locale local = LocaleContext.getLocale(request);
			String resource = "com.seeyon.v3x.system.resources.i18n.SysMgrResources";
			String message = ResourceBundleUtil.getString(resource, local, "cntPost.cntPost.repeated");
			out = response.getWriter();
			out.println("<script>");				
			out.println("alert('"+message+"');");
			out.println("</script>");
			super.rendJavaScript(response, "parent.doEnd();");
			return null;
		}else{
			//设置职务级别(如果人员在本单位的职务已经设置，修改此人在本单位的职务级别)
			if(strCntLevelId!=null&&!StringUtils.isEmpty(strCntLevelId)){
				Long cntLevelId = Long.parseLong(strCntLevelId);
				if(!cntLevelId.equals(vors.getExtend2())){
					List<V3xOrgRelationship> rels = orgManagerDirect.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"sourceId",vors.getSourceId(),"orgAccountId",vors.getOrgAccountId());
					if(rels!=null&&rels.size()>0){
						for(V3xOrgRelationship rel:rels){
							if(!cntLevelId.equals(rel.getExtend2())){
								rel.setExtend2(cntLevelId);
								orgManagerDirect.updateEntity(rel);
								orgManager.updateEntity(rel);							
							}
						}					
					}
				}
				vors.setExtend2(Long.parseLong(strCntLevelId));
			}else{
				vors.setExtend2(null);
			}
			//修改个人菜单权限
	        String securityIdsStr = request.getParameter("securityIds");
	        if (null != securityIdsStr && securityIdsStr.length() > 0) {
	            String[] securityIds = securityIdsStr.split(",");
	            List<Long> securityIdsList = new ArrayList<Long>();
	            for(String idStr : securityIds){
	                securityIdsList.add(Long.parseLong(idStr));
	            }
	            menuManager.saveMemberSecurity(vors.getSourceId(), vors.getOrgAccountId(), securityIdsList);
	        }			
			orgManagerDirect.updateEntity(vors);
			//此处更新部门是为了更新部门岗位关系
			if(vors.getObjectiveId()!=null&&vors.getBackupId()!=null){
				V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(vors.getObjectiveId());
				if(!dept.getPosts().contains(vors.getBackupId())){
					dept.addDepPost(vors.getBackupId());
					orgManagerDirect.updateEntity(dept);
				}				
			}
			orgManager.updateEntity(vors);
			//推送兼职模板
			try {
				templeteConfigManager.pushNewOrgEntityTemplete4Member(V3xOrgEntity.ORGENT_TYPE_MEMBER,vors.getSourceId(),vors.getSourceId());
			} catch (Exception e) {
				logger.warn("推送兼职模板失败",e);
			}
			//增加日志
			User user = CurrentUser.get();
			V3xOrgMember member = orgManager.getMemberById(vors.getSourceId());
			V3xOrgAccount account = orgManager.getAccountById(vors.getOrgAccountId());
			appLogManager.insertLog(user, AppLogAction.Organization_GroupAdminUpdateCntPost, member.getName(), account.getName(), user.getName());				
		}
		super.rendJavaScript(response, "parent.doEndAdd();");
		return null;
	}
	
	/**
	 * 删除跨单位兼职
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView deleteCntPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] strIds = request.getParameterValues("id");
		User user = CurrentUser.get();
		if (strIds != null && strIds.length != 0) {
			for (int i = 0; i < strIds.length; i++) {
				if (NumberUtils.isNumber(strIds[i])) {						
					V3xOrgRelationship vors = (V3xOrgRelationship)orgManagerDirect.getEntityById(V3xOrgRelationship.class.getSimpleName(), Long.parseLong(strIds[i]));
					if(vors!=null){
						V3xOrgMember member = orgManager.getMemberById(vors.getSourceId());
						V3xOrgAccount account = orgManager.getAccountById(vors.getOrgAccountId());
						appLogManager.insertLog(user, AppLogAction.Organization_GroupAdminDeleteCntPost, member.getName(), account.getName(), user.getName());						
					}
					orgManagerDirect.deleteConcurrentPost(Long.parseLong(strIds[i]));
				}
			}
		}
		PrintWriter out = response.getWriter();
		out.print("<script>");
		out.print("parent.location.href='plurality.do?method=listPlurality';");
		out.print("</script>");
		out.flush();
		return null;
	}	
	
	/**
	 * 显示兼职详细信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView cntPostDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("sysMgr/plurality/editPlurality");
		List<V3xOrgRelationship> vors = orgManagerDirect.getRelationships("id",RequestUtils.getLongParameter(request, "id"));
		//兼职为空的判断
		if(vors == null||vors.size() == 0){
			PrintWriter out = response.getWriter();
			out.println("<script>");				
			out.println("alert(parent.v3x.getMessage('sysMgrLang.cnt_null'));");
			out.println("</script>");	
			return super.refreshWorkspace();
		}
		V3xOrgRelationship rel = vors.get(0);
		WebV3xOrgRelationship webRel = new WebV3xOrgRelationship();
		V3xOrgMember vom = orgManagerDirect.getMemberById(rel.getSourceId());
		webRel.setV3xOrgRelationship(rel);
		webRel.setOrgAccountId(vom.getOrgAccountId());
		webRel.setOrgPostId(vom.getOrgPostId());
		result.addObject("cntPost", webRel);
		result.addObject("oper", "show");
		//获取该用户菜单权限
        String securityIds = null;
        String securityNames = null;
        List<Security> defaultSecurities = menuManager.getSecurityOfMember(rel.getSourceId(), rel.getOrgAccountId(), true);
        for(Security security : defaultSecurities){
            if(securityIds == null){
                securityIds = security.getId().toString();
                securityNames = security.getName();
            }
            else{
                securityIds += "," + security.getId();
                securityNames += "," + security.getName();
            }
        }
        result.addObject("securityIds", securityIds);
        result.addObject("securityNames", securityNames);

		return result;
	}

	/**
	 * 批量增加兼职信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.GroupAdmin})
	public ModelAndView banchAdd(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("sysMgr/plurality/banchAdd");
		return result;
	}

	/**
	 * 批量增加兼职信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes={RoleType.GroupAdmin})
	public ModelAndView addbanchCnt(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<V3xOrgEntity> rels = new ArrayList<V3xOrgEntity>();
		String memberIdStr = request.getParameter("memberId");
		String cntAccountIdStr = request.getParameter("cntAccountId");
		Long cntNumLong = null;
		if(memberIdStr!=null&&cntAccountIdStr!=null&&!StringUtils.isEmpty(memberIdStr)&&!StringUtils.isEmpty(cntAccountIdStr)){
			String[] memberIds = memberIdStr.split(",");
			String[] cntAccountIds = cntAccountIdStr.split(",");
			if(memberIds.length == 1){
				Long memberId = Long.parseLong(memberIds[0]);
				for(String cntAccountStr:cntAccountIds){
					Long cntAccountId = Long.parseLong(cntAccountStr);
					V3xOrgRelationship rel = new V3xOrgRelationship();
					rel.setSourceId(memberId);
					rel.setOrgAccountId(cntAccountId);
					rel.setType(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST);
					//取得排序号
					cntNumLong = Long.valueOf(orgManagerDirect.getMaxMemberSortNum(cntAccountId)+1);
					rel.setExtend1(cntNumLong);
					rels.add(rel);
				}
			}else if(cntAccountIds.length == 1){
				Long accountId = Long.parseLong(cntAccountIds[0]);
				//取得排序号
				cntNumLong = Long.valueOf(orgManagerDirect.getMaxMemberSortNum(accountId)+1);
				for(String memberStr:memberIds){
					Long memberId = Long.parseLong(memberStr);
					V3xOrgRelationship rel = new V3xOrgRelationship();
					rel.setSourceId(memberId);
					rel.setOrgAccountId(accountId);
					rel.setType(V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST);
					rel.setExtend1(cntNumLong);
					if(cntNumLong<99999){
						cntNumLong++;
					}					
					rels.add(rel);
				}
			}
			//保存到数据库,载入内存
			orgManagerDirect.updateEntitys(rels);
			for(V3xOrgEntity rel:rels){
				orgManager.updateEntity(rel);
				V3xOrgRelationship vors = (V3xOrgRelationship)rel;
				//增加日志
				User user = CurrentUser.get();
				V3xOrgMember member = orgManager.getMemberById(vors.getSourceId());
				V3xOrgAccount account = orgManager.getAccountById(vors.getOrgAccountId());
				appLogManager.insertLog(user, AppLogAction.Organization_GroupAdminAddCntPost, member.getName(), account.getName(), user.getName());				
			}			
		}
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("window.returnValue=true;");
		out.println("window.close();");
		out.println("</script>");
		out.flush();
		return super.refreshWorkspace();
	}
	
	/**
	 * 批量导出人员兼职信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exportCnt(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 判断是系统管理员还是单位管理员进入的页面
		User user = CurrentUser.get();
		String isIn = request.getParameter("isIn");
		List<V3xOrgRelationship> cntListOrg =  new ArrayList<V3xOrgRelationship>();
		
		if(orgManagerDirect.isGroupAdmin(user.getLoginName())){
			//得到所有跨单位兼职列表
			cntListOrg = orgManagerDirect.getAllSidelineByAccount(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		}else{
			//得到单位下的兼职列表
			if(isIn!=null&&isIn.equals("1")){
				cntListOrg = orgManagerDirect.getAllSidelineByAccount(user.getLoginAccount());
			}else{
				cntListOrg = orgManagerDirect.getAllOutConcurrentPostByAccount(user.getLoginAccount(), false);
			}			
		}
		List<DataRecord> records = new ArrayList<DataRecord>();
		List<V3xOrgRelationship> rels = cntListOrg;
			
			if(rels.size() != 0){
				DataRecord record = new DataRecord();
				Locale locale = LocaleContext.getLocale(request);
				String resource = "com.seeyon.v3x.system.resources.i18n.SysMgrResources";
				String[] columnNames = new String[7];
				columnNames[0] = ResourceBundleUtil.getString(resource, locale, "cntPost.body.membername.label");
				columnNames[1] = ResourceBundleUtil.getString(resource, locale, "cntPost.body.number.label");
				columnNames[2] = ResourceBundleUtil.getString(resource, locale, "cntPost.body.account.label");
				columnNames[3] = ResourceBundleUtil.getString(resource, locale, "cntPost.body.post.label");
				columnNames[4] = ResourceBundleUtil.getString(resource, locale, "cntPost.body.cntaccount.label");
				columnNames[5] = ResourceBundleUtil.getString(resource, locale, "cntPost.body.cntdept.label");
				columnNames[6] = ResourceBundleUtil.getString(resource, locale, "cntPost.body.cntpost.label");
				record.setColumnName(columnNames);
				record.setSheetName(ResourceBundleUtil.getString(resource, locale, "cntPost.detail.label"));
				record.setTitle(ResourceBundleUtil.getString(resource, locale, "cntPost.detail.label"));
				
				for(V3xOrgRelationship rel : rels){
					DataRow row = new DataRow();
					//先从内存中查询人员，再从数据库查询，目的是提高性能
					V3xOrgMember member = orgManager.getMemberById(rel.getSourceId(),V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
					if(member == null){
						member = orgManagerDirect.getMemberById(rel.getSourceId());
					}
					if(member!=null){
						row.addDataCell(Functions.showMemberName(member), DataCell.DATA_TYPE_TEXT);
						row.addDataCell(rel.getExtend3(), DataCell.DATA_TYPE_TEXT);
						String accountName = (member == null ? "":orgManager.getAccountById(member.getOrgAccountId()).getName());
						row.addDataCell(accountName,DataCell.DATA_TYPE_TEXT);
						String postName = (member == null ? "":orgManager.getPostById(member.getOrgPostId(),member.getOrgAccountId()).getName());
						row.addDataCell(postName, DataCell.DATA_TYPE_TEXT);					
						row.addDataCell(orgManager.getAccountById(rel.getOrgAccountId()).getName(), DataCell.DATA_TYPE_TEXT);
						String cntDeptName = "";
						if(rel.getObjectiveId()!=null){
							V3xOrgDepartment dept = orgManager.getDepartmentById(rel.getObjectiveId(),rel.getOrgAccountId());
							if(dept == null){
								List deptList = orgManagerDirect.getEntityListNoRelation(V3xOrgDepartment.class.getSimpleName(), "id", rel.getObjectiveId(), rel.getOrgAccountId());
								if(deptList!=null&&deptList.size()>0){
									dept = (V3xOrgDepartment)deptList.get(0);
									cntDeptName = dept.getName();
								}
							}else{
								cntDeptName = dept.getName();
							}
						}
						row.addDataCell(cntDeptName, DataCell.DATA_TYPE_TEXT);
						String cntPostName = "";
						if(rel.getBackupId()!=null){
							V3xOrgPost post = orgManager.getPostById(rel.getBackupId(),rel.getOrgAccountId());
							if(post == null){
								List postList = orgManagerDirect.getEntityListNoRelation(V3xOrgPost.class.getSimpleName(), "id", rel.getBackupId(), rel.getOrgAccountId());
								if(postList!=null&&postList.size()>0){
									post = (V3xOrgPost)postList.get(0);
									cntPostName = post.getName();
								}
							}else{
								cntPostName = post.getName();
							}
						}					
						row.addDataCell(cntPostName, DataCell.DATA_TYPE_TEXT);
						record.addDataRow(row);
					}					
				}
				records.add(record);
				DataRecord[] dataRecords = new DataRecord[records.size()];
				int j = 0;
				for(DataRecord rec : records){
					dataRecords[j] = rec;
					j++;
				}
			    fileToExcelManager.save(request,response, "cntPost", "location.href",dataRecords);
			    return null;
			}else{
				//没有数据不能导出
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('sysMgrLang.system_partition_no_data'));");
				out.println("</script>");
				out.flush();
				return null;
			}
	}

	
	/**
	 * 判断编码是否重复
	 * @param request
	 * 
	 * @throws BusinessException  
	 */
	public boolean isNumDistinct(ConcurrentPost cntPost) throws BusinessException{
		Iterator cntIt = orgManagerDirect.getAllCrossAccountCntPost().iterator();
		while(cntIt.hasNext()){
			ConcurrentPost cntPost1 = (ConcurrentPost)cntIt.next();
			if(cntPost.getNumber()==cntPost1.getNumber()&&(!cntPost.getId().equals(cntPost1.getId()))){
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断人员兼职信息是否重复
	 * @param cntPost
	 * 
	 * @throws BusinessException  
	 */
	public boolean isMemberCntPostDistinct(V3xOrgRelationship vors) throws BusinessException{
		if(vors.getObjectiveId()!=null&&vors.getBackupId()!=null){
			List<V3xOrgRelationship> cntRels = orgManagerDirect.getRelationships("type",V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,"orgAccountId",vors.getOrgAccountId(),
					"objectiveId",vors.getObjectiveId(),"backupId",vors.getBackupId(),"sourceId",vors.getSourceId());
			if(cntRels!=null&&cntRels.size()>0){
				for(V3xOrgRelationship cntRel:cntRels){
					if(!cntRel.getId().equals(vors.getId())){
						return false;
					}
				}
			}
		}
		return true;
	}

	public AppLogManager getAppLogManager() {
		return appLogManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
}