package com.seeyon.v3x.meeting.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.meeting.domain.MtContentTemplate;
import com.seeyon.v3x.meeting.manager.MtContentTemplateManager;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;

/**
 * 会议正文格式的 Controller
 * @author wolf
 */
@CheckRoleAccess(roleTypes={RoleType.Administrator, RoleType.GroupAdmin})
public class MtContentTemplateController extends BaseController {
	private MtContentTemplateManager MtContentTemplateManager;
	private OrgManager orgManager;

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setMtContentTemplateManager(MtContentTemplateManager MtContentTemplateManager) {
		this.MtContentTemplateManager = MtContentTemplateManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	/**
	 * 创建会议正文格式
	 */
	public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
		MtContentTemplate bean = new MtContentTemplate();
		bean.setUsedFlag(true);
		bean.setCreateDate(new Date());
		bean.setCreateUser(CurrentUser.get().getId());
		bean.setTemplateFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		
		return new ModelAndView("meeting/admin/content_template_create", "bean", bean);
	}
	
	/**
	 * 编辑会议正文格式
	 */
	public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr = request.getParameter("id");
		MtContentTemplate bean;
		if(StringUtils.isBlank(idStr)) {
			bean = new MtContentTemplate();	
			bean.setUsedFlag(true);
			bean.setCreateDate(new Date());
			bean.setCreateUser(CurrentUser.get().getId());
			bean.setTemplateFormat(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML);
		}
		else {
			bean = MtContentTemplateManager.getById(Long.valueOf(idStr));
		}
		
		return new ModelAndView("meeting/admin/content_template_create", "bean", bean);
	}
	
	/**
	 * 保存会议正文格式
	 */
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		MtContentTemplate bean = null;
		String idStr = request.getParameter("id");
		String contentType = request.getParameter("bodyType");
		String type = request.getParameter("type");//四种类型； 1代表会议 ，2代表计划 ，3代表公告 ，4代表新闻
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		if(StringUtils.isBlank(idStr)) {
			bean = new MtContentTemplate();
		} else {
			bean=MtContentTemplateManager.getById(Long.valueOf(idStr));
			String orgName = bean.getTemplateName();
			String name = request.getParameter("templateName");
			if(!orgName.equals(name)) {
				try {
					MtContentTemplateManager.checkDupleName(name);
				} 
				catch (BusinessException e) {			
					ModelAndView mav = new ModelAndView("meeting/admin/content_template_create");
					mav.addObject("bean", bean);
					request.getSession().setAttribute("_my_exception", e);
					return mav;
				}
			}
		}
		super.bind(request,bean);
		
		if(bean.isNew()) {
			bean.setCreateDate(new Date());
			bean.setCreateUser(CurrentUser.get().getId());
			bean.setAccountId(accountId);			
		}
		V3xOrgAccount account = orgManager.getAccountById(accountId);
		if(account.getIsRoot()) {//集团
			bean.setExt2(type);//用扩展字段ext2,来存类型--集团。
		} else {//单位
			bean.setExt1(type);//用扩展字段ext1,来存类型--单位。
		}
		
		bean.setTemplateFormat(contentType);		
		bean.setUpdateDate(new Date());
		bean.setUpdateUser(CurrentUser.get().getId());
		
		try {
			MtContentTemplateManager.saveTemplate(bean);
		} catch (Exception e) {			
			ModelAndView mav = new ModelAndView("meeting/admin/content_template_create");
			mav.addObject("bean", bean);
			request.getSession().setAttribute("_my_exception", e);
			return mav;
		}

		return this.redirectModelAndView("/mtContentTemplate.do?method=listMain");
	}
	
	
	/**
	 * 删除会议正文格式，支持批量删除
	 */
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Long> ids = CommonTools.parseStr2Ids(request.getParameter("id"));
		if(CollectionUtils.isNotEmpty(ids))
			MtContentTemplateManager.deletes(ids);
		return this.redirectModelAndView("/mtContentTemplate.do?method=listMain");
	}
	
	public ModelAndView homeEntry(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("meeting/admin/homeEntry");
	}
	
	/**
	 * 常用正文格式列表主页面：包括公告、新闻、计划(只在单位管理员情况下出现)、会议(只在单位管理员情况下出现)
	 */
	public ModelAndView listMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("meeting/admin/content_template_list_main");
	}

	/**
	 * 常用正文格式列表：包括公告、新闻、计划(只在单位管理员情况下出现)、会议(只在单位管理员情况下出现)
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<MtContentTemplate> list = null;
		String type = request.getParameter("type");
		if(Strings.isBlank(type) || "all".equals(type)){
			list = MtContentTemplateManager.findAll();
		} else {
			//不能简单通过当前用户是否集团管理员来判断，其他普通用户也会在其他场景进入常用正文格式列表
			Long accountId = CurrentUser.get().getAccountId();
			V3xOrgAccount account = this.orgManager.getAccountById(accountId);
			list = account.getIsRoot() ? MtContentTemplateManager.findGroupTypeAll(type) : MtContentTemplateManager.findTypeAll(type);
		}
		return new ModelAndView("meeting/admin/content_template_list_iframe").addObject("list", list).addObject("type", type);
	}
	
	/**
	 * 显示会议正文格式详细页面，或预览会议正文格式
	 */
	public ModelAndView detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String idStr=request.getParameter("id");
		MtContentTemplate bean=null;
		if(StringUtils.isBlank(idStr)){
			bean = new MtContentTemplate();
		} else {
			bean = MtContentTemplateManager.getById(Long.valueOf(idStr));
		}
		
		String view="meeting/admin/content_template_list_detail_iframe";
		if(request.getParameter("preview") != null){
			view="meeting/admin/template_preview";
		} else if(request.getParameter("oper") != null){
			view="meeting/admin/showContent";
		}
		
		return new ModelAndView(view, "bean", bean);
	}
	
}
