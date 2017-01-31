package com.seeyon.v3x.hr.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.organization.OrganizationHelper;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.CompareSortEntity;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgMember;

public class HrDeptAdminManagerController extends BaseController {
	
	private transient static final Log LOG = LogFactory.getLog(HrDeptAdminManagerController.class);
	
	private OrgManagerDirect orgManagerDirect;
	
	private MetadataManager metadataManager;
	
	@SuppressWarnings("deprecation")
	private SearchManager searchManager;


	public SearchManager getSearchManager() {
		return searchManager;
	}

	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 部门及子部门人员列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */	
	public ModelAndView listMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView result = new ModelAndView("organization/member/listMember");
		User user = CurrentUser.get();
		// 取得所有的岗位,以便初始化查询条件的岗位下拉列表
		List<V3xOrgPost> postlist = orgManagerDirect.getAllPosts(user.getLoginAccount(),false);
		result.addObject("postlist", postlist);
		// 取得所有职务级别
		List<V3xOrgLevel> levellist = orgManagerDirect.getAllLevels(user.getLoginAccount(),false);
		result.addObject("levellist", levellist);
		try {			
			String condition = request.getParameter("condition");
			String textfield = request.getParameter("textfield");
			List<V3xOrgMember> memberlist = OrganizationHelper.searchMember(condition, textfield, searchManager, orgManagerDirect, true, true, true);
			Collections.sort(memberlist,CompareSortEntity.getInstance());
			List<WebV3xOrgMember> resultlist = new ArrayList<WebV3xOrgMember>();
			long deptId = -1;
			long levelId = -1;
			long postId = -1;

			if (null != memberlist) {
				for (V3xOrgMember member : memberlist) {
					deptId = member.getOrgDepartmentId();
					levelId = member.getOrgLevelId();
					postId = member.getOrgPostId();

					WebV3xOrgMember webMember = new WebV3xOrgMember();
					webMember.setV3xOrgMember(member);
					V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
					if (dept != null) {
						webMember.setDepartmentName(dept.getName());
					}

					V3xOrgLevel level = orgManagerDirect.getLevelById(levelId);
					if (null != level) {
						webMember.setLevelName(level.getName());
					}

					V3xOrgPost post = orgManagerDirect.getPostById(postId);
					if (null != post) {
						webMember.setPostName(post.getName());
					}

					resultlist.add(webMember);
				}
			}
			result.addObject("memberlist", resultlist);
            //判断是什么版本
			boolean showAssign = (Boolean)(SysFlag.org_showGroupAccountAssign.getFlag());
			result.addObject("showAssign", showAssign);
			//判断是否含有NC插件
			boolean hasNC =  SystemEnvironment.hasPlugin("nc");
			result.addObject("hasNC", hasNC);
			// 获得单位类别下拉列表中的数据
			Map<String, Metadata> orgMeta = metadataManager.getMetadataMap(ApplicationCategoryEnum.organization);
			result.addObject("orgMeta", orgMeta);
			result.addObject("condition", condition);
			result.addObject("textfield", textfield);			

			return result;
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

}
