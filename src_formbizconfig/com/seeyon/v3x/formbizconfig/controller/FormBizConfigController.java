package com.seeyon.v3x.formbizconfig.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.base.SelectPersonOperation;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;

import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bulletin.domain.BulType;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.collaboration.manager.ColManager;
import com.seeyon.v3x.collaboration.manager.ColSuperviseManager;
import com.seeyon.v3x.collaboration.templete.domain.Templete;
import com.seeyon.v3x.collaboration.templete.domain.TempleteCategory;
import com.seeyon.v3x.collaboration.templete.manager.TempleteCategoryManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteManager;
import com.seeyon.v3x.collaboration.webmodel.ColSummaryModel;
import com.seeyon.v3x.collaboration.webmodel.ColSuperviseModel;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfig;
import com.seeyon.v3x.formbizconfig.domain.FormBizConfigColumn;
import com.seeyon.v3x.formbizconfig.domain.V3xBizAuthority;
import com.seeyon.v3x.formbizconfig.domain.V3xBizConfig;
import com.seeyon.v3x.formbizconfig.domain.V3xBizConfigItem;
import com.seeyon.v3x.formbizconfig.manager.FormBizConfigColumnManager;
import com.seeyon.v3x.formbizconfig.manager.FormBizConfigManager;
import com.seeyon.v3x.formbizconfig.manager.V3xBizConfigManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigConstants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;
import com.seeyon.v3x.formbizconfig.webmodel.ColumnNodeModel;
import com.seeyon.v3x.formbizconfig.webmodel.MenuConfig;
import com.seeyon.v3x.formbizconfig.webmodel.TempleteCategorysWebModel;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.main.MainHelper;
import com.seeyon.v3x.menu.domain.Menu;
import com.seeyon.v3x.menu.manager.MenuManager;
import com.seeyon.v3x.news.domain.NewsType;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.domain.SpaceFix;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.Strings;
/**
 * FormBizConfigController - 表单业务配置Controller，其全部方法大致按照用户的操作顺序进行编排
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2009-08-12
 */
public class FormBizConfigController extends BaseController {
	private OrgManager orgManager;	
	private MenuManager menuManager;
	private SpaceManager spaceManager;
	private ColManager colManager;
	private AffairManager affairManager;
	private MetadataManager metadataManager;
	private ColSuperviseManager colSuperviseManager;	
	private FormBizConfigManager formBizConfigManager;	
	private V3xBizConfigManager v3xBizConfigManager;	
	private FormBizConfigColumnManager formBizConfigColumnManager;
	private TempleteManager templeteManager;
	private TempleteCategoryManager templeteCategoryManager;	
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	private UserMessageManager userMessageManager;	
	private static final Log logger = LogFactory.getLog(FormBizConfigController.class);
	private BulTypeManager bulTypeManager;
	private InquiryManager inquiryManager;
	private BbsBoardManager bbsBoardManager;
	private NewsTypeManager newsTypeManager;
	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}
	
	public ModelAndView listAllIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("formbizconfig/write/bizconfig_list_index");
	}
	
	public ModelAndView listAllFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("formbizconfig/write/bizconfig_list_frame");
	}	
	
	/**
	 * 显示当前用户自己创建和他人共享的全部或按指定条件查询(支持用户以业务显示名称、创建者、创建日期和挂接方式进行查询)的业务配置列表
	 */
	public ModelAndView listAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SearchModel searchModel = SearchModel.getSearchModel(request);
		List<FormBizConfig> bizConfigList = formBizConfigManager.findAll(CurrentUser.get().getId(), searchModel);
		return new ModelAndView("formbizconfig/write/bizconfig_list", "bizConfigList", bizConfigList);
	}
	
	/**
	 * 用户点击列表上方"新建"菜单按钮，进入新建业务配置页面，默认选中栏目挂接，包含栏目、菜单挂接项的可选、已选项展现
	 * @see #showAllColumns
	 * @see #showAllMenus
	 */
	public ModelAndView  newBizconfig (HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("formbizconfig/write/bizconfig_new");
	}	
	
	/**
	 * 在新建、查看、修改页面的"首页栏目功能配置"-"可选项"中显示用户可选的全部栏目挂接项<br>
	 * 在查看业务配置或进入业务配置修改页面时，此处默认按照普通用户权限展现，即只显示当前用户有权使用的表单查询和表单统计模板；<br>
	 * 如果用户为表单管理员且在选择表单模板时选中"所有模板"单选框进行操作，完成表单模板选择后，<br>
	 * 此处显示选中表单模板对应表单的全部表单查询和表单统计模板，不进行权限过滤<br>
	 */
	public ModelAndView showAllColumns (HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/bizconfig_column_all");
		String formTempIds = request.getParameter("tempIds");
		if(Strings.isNotBlank(formTempIds)) {
			String from = StringUtils.defaultIfEmpty(request.getParameter("from"), FormBizConfigConstants.FLAG_FROM_USER);
			List<ColumnNodeModel> queryAndStatisticInfo = formBizConfigColumnManager.getQueryAndStatisticInfo(formTempIds, from);
			mav.addObject("queryAndStatisticInfo", queryAndStatisticInfo);
		}
		return mav;
	}
	
	/**
	 * 在新建、查看、修改页面的"菜单功能设置"-"可选项"中显示用户可选的全部菜单挂接项
	 */
	public ModelAndView  showAllMenus (HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("formbizconfig/write/bizconfig_menu_all");
	}
	
	/**
	 * 响应用户点击表单模板"设置"按钮或对应文本框的操作，页面中设定一个Iframe指向展示表单模板树形结构页面
	 */
	public ModelAndView  showFormTempletsFrame (HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/bizconfig_show_templets_frame");
		boolean isFormAdmin = MainHelper.isFORMAdmin(orgManager);
		String categoryHTML = iOperBase.categoryHTML(templeteCategoryManager).toString();
		return mav.addObject("isFormAdmin", isFormAdmin).addObject("categoryHTML", categoryHTML);	
	}
	
	/**
	 * 显示当前用户可以使用的表单模板(包括共享的<b>外单位</b>表单模板)，初始进入时默认按此显示<br>
	 * 如果当前用户是表单管理员且选中了"所有模板"单选按钮，则显示<b>当前单位</b>所有表单模板<br>
	 */
	public ModelAndView  showFormTemplets (HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/bizconfig_show_templets");
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		
		String condition = request.getParameter("condition");
		String textfield = null;
		if(TempleteCategorysWebModel.SEARCH_BY_CATEGORY.equals(condition)) {
			textfield = request.getParameter("categoryId");
		} else if(TempleteCategorysWebModel.SEARCH_BY_SUBJECT.equals(condition)) {
			textfield = request.getParameter("textfield");
		}
		boolean fromUser = FormBizConfigConstants.FLAG_FROM_USER.equals(request.getParameter("from"));
		// 选中"所有模板"单选按钮，显示当前单位下的所有表单模板，选中"我的模板"单选按钮，只显示其有权使用的所有表单模板
		List<Templete> tempList = null;
		if(fromUser) {
			tempList = templeteManager.getSysFormTempsByMemberId(user.getId(), condition, textfield);
		}
		else {
			tempList = new ArrayList<Templete>();
			List<Templete> tList = templeteManager.getAllSystemTempletesByAcl(accountId, TempleteCategory.TYPE.form.ordinal());
			if(Strings.isNotBlank(condition) && Strings.isNotBlank(textfield) && CollectionUtils.isNotEmpty(tList)){
				for (Templete t : tList) {
					if(condition.equalsIgnoreCase("subject")) {
			        	if(Strings.isNotBlank(t.getSubject()) && t.getSubject().indexOf(textfield)!=-1) {
			        		tempList.add(t);
			        	}
			       } else if(condition.equalsIgnoreCase("category")) {
			        	if(t.getCategoryId().equals(Long.valueOf(textfield))){
			        		tempList.add(t);
			        	}
			       }
				}
			} else {
				tempList.addAll(tList);
			}
		}
		
		TempleteCategorysWebModel categorysModel = templeteCategoryManager.getCategorys(accountId, condition, textfield, tempList);	
		return mav.addObject("tempList", tempList).addObject("categorysModel", categorysModel);
	}
	
	/**
	 * 用户创建一条业务配置记录，包含对表单模板与业务配置关系记录、共享范围、栏目挂接项、菜单挂接项的处理
	 */
	public ModelAndView  create(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Long creatorId = user.getId();
		String creatorName = user.getName();
		String bizConfigName = request.getParameter("bizConfigName");
		boolean columnConfig = Strings.isNotBlank(request.getParameter("column"));    
		boolean menuConfig = Strings.isNotBlank(request.getParameter("menu"));		  
		
		int bizConfigType = FormBizConfigUtils.getBizConfigType(columnConfig, menuConfig);
		FormBizConfig bizConfig = new FormBizConfig(bizConfigName, new Timestamp(System.currentTimeMillis()),
				creatorId, null, bizConfigType);
		this.formBizConfigManager.saveBizConfig(bizConfig);
		Long bizConfigId = bizConfig.getId();
		
		String templeteIds = request.getParameter("formTempletIds");
		this.formBizConfigManager.saveTempleteProfiles(templeteIds, bizConfigId);
		
		String shareScope = request.getParameter("shareScopeIds");
		this.formBizConfigManager.saveShareInfo(shareScope, bizConfigId);
		List<Long> sharerIds = FormBizConfigUtils.getSharerIds(shareScope, creatorId, orgManager);
		
		// 处理栏目挂接项
		if(columnConfig) {
			String[] columnIdAndCategorys = request.getParameterValues("columnIdAndCategory");
			String[] columnNames = request.getParameterValues("columnName");
			this.formBizConfigColumnManager.saveColumns(columnIdAndCategorys, columnNames, bizConfigId);
		}
		
		// 处理菜单挂接项
		if(menuConfig) {
			String[] menuIdAndCategorys = request.getParameterValues("menuIdAndCategory");
			String[] menuNames = request.getParameterValues("menuName");
			formBizConfigManager.saveMainAndSubMenus(menuIdAndCategorys, menuNames, bizConfig, templeteIds, sharerIds, null);	
		}
		
		if(CollectionUtils.isNotEmpty(sharerIds)) {
			userMessageManager.sendSystemMessage(MessageContent.get("formbizconfig.share.label", creatorName, bizConfigName),
					ApplicationCategoryEnum.form, creatorId, 
					MessageReceiver.get(bizConfigId, sharerIds, "message.link.formbizconfig.view", bizConfigId), bizConfigId);
		}
		
		super.rendJavaScript(response, "parent.refresh4Save('" + menuConfig + "', '" + columnConfig + "', '" + bizConfigId + "');");
		return null;
	}
	
	/**
	 * 用户修改自己创建的业务配置或查看自己创建、他人共享的业务配置<br>
	 * 修改操作时在前端使用Ajax进行校验，查看时的校验则统一在后台进行(通过系统消息点击查看时不便使用Ajax)，避免重复校验<br>
	 */
	public ModelAndView  viewOrEdit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/bizconfig_new");
		Long bizConfigId = NumberUtils.toLong(request.getParameter("id"));
		FormBizConfig bizConfig = this.formBizConfigManager.findById(bizConfigId);
		
		User user = CurrentUser.get();
		if(BooleanUtils.toBoolean(request.getParameter("view"))) {
			// 如果通过系统消息点击打开模态对话框查看窗口，则防护js动作为关闭窗口，否则为刷新列表页面
			boolean fromMsgWindow = "Message".equals(request.getParameter("from"));
			String jsAction = fromMsgWindow ? "window.close();" : "parent.getA8Top().reFlesh();";
			if(!this.formBizConfigManager.isCreatorOrInShareScope(bizConfig, user.getId())) {
				super.rendJavaScript(response, "alert('" + FormBizConfigUtils.getI18NValue("bizconfig.notinscopeordeleted.label") + "');" + jsAction);
				return null; 
			}
		}
		mav.addObject("bizConfig", bizConfig);
		
		// 显示选择的表单模板
		List<Templete> temps = this.templeteManager.getTempletes4BizConfigWithoutAuthCheck(bizConfigId);
		if(CollectionUtils.isNotEmpty(temps)) {
			StringBuilder tempIds = new StringBuilder("");
			StringBuilder tempNames = new StringBuilder("");
			for(Templete temp : temps) {
				tempIds.append(temp.getId() + ",");
				tempNames.append(temp.getSubject() + FormBizConfigConstants.SEPARATOR);
			}
			mav.addObject("temps", temps);
			mav.addObject("tempIds", tempIds.substring(0, tempIds.length() - 1));
			mav.addObject("tempNames", tempNames.substring(0, tempNames.length() - 1));
		}
		
		// 显示共享范围
		String scopeIds = this.formBizConfigManager.getShareScopeIds(bizConfigId);
		mav.addObject("scopeIds", scopeIds);
		
		// 显示已选栏目挂接项
		if(bizConfig.hasColumnConfig()) {
			List<Long> domainIds = FormBizConfigUtils.getUserDomainIds(user, this.orgManager);
			List<FormBizConfigColumn> columns = formBizConfigColumnManager.getSelectedColumns(domainIds, bizConfigId);
			mav.addObject("columns", columns);
			//传入地址
			SpaceFix fix = spaceManager.getPersonSpace(user.getId(), user.getLoginAccount());
			if(fix != null) {
				String _pagePath = fix.getPagePath();
				mav.addObject("pagePath", _pagePath);
				boolean isPublished = spaceManager.IsPublishedFormBizSection(String.valueOf(bizConfigId),_pagePath);
				mav.addObject("isPublished", isPublished);
			}
		}
		
		// 显示已选菜单挂接项
		if(bizConfig.hasMenuConfig()) {
			List<MenuConfig> menuConfigs = this.formBizConfigManager.getMenuConfigs(bizConfigId);
			mav.addObject("menuConfigs", menuConfigs);
		}
		
		return mav;
	}
	
	/**
	 * 保存用户(业务配置创建者)对表单业务配置的修改
	 */
	public ModelAndView  saveEdit (HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long bizConfigId = NumberUtils.toLong(request.getParameter("id"));
		FormBizConfig bizConfig = this.formBizConfigManager.findById(bizConfigId);
		Long creatorId = CurrentUser.get().getId();
		
		String newBizConfigName = request.getParameter("bizConfigName");
		String oldBizConfigName = bizConfig.getName();
		boolean isNameChanged = !newBizConfigName.equals(oldBizConfigName);		
		bizConfig.setName(newBizConfigName);
		// 完成修改后是否需要刷新主页顶部菜单显示
		boolean needRefreshMenu = false;
		
		// 处理表单模板变化
		String newTempleteIds = request.getParameter("formTempletIds");		
		String oldTempleteIds = request.getParameter("oldTempleteIds");	
		if(!newTempleteIds.equals(oldTempleteIds)) {
			this.formBizConfigManager.deleteTempletProfiles(bizConfigId);
			this.formBizConfigManager.saveTempleteProfiles(newTempleteIds, bizConfigId);
		}
		
		// 处理共享范围变化
		String newScope = request.getParameter("shareScopeIds");
		String oldScope = request.getParameter("oldShareScopeIds");
		List<Long> newMembers = null, sharersAdded = null, sharersReduced = null;
		List<Long> oldMembers = FormBizConfigUtils.getSharerAndCreatorIds(oldScope, creatorId, orgManager);
		
		// 如果共享范围发生变化，则需清空旧共享范围，保存新共享范围，并获取新增和取消的共享对象，以便对其发送不同消息
		boolean scopeChanged = !FormBizConfigUtils.equals(newScope, oldScope);
		if(scopeChanged) {
			if(Strings.isNotBlank(oldScope)) {
				this.formBizConfigManager.deleteShareScopes(bizConfigId);
			}
			
			if(Strings.isNotBlank(newScope)) {
				this.formBizConfigManager.saveShareInfo(newScope, bizConfigId);
				newMembers = FormBizConfigUtils.getSharerAndCreatorIds(newScope, creatorId, orgManager);
			}
			
			sharersAdded = FormBizConfigUtils.getAddedCollection(oldMembers, newMembers);
			if(sharersAdded != null) {	
				sharersAdded.remove(creatorId);
			}
			sharersReduced = FormBizConfigUtils.getReducedCollection(oldMembers, newMembers);
			if(sharersReduced != null) {
				sharersReduced.remove(creatorId);
			}
		}
		
		// 处理栏目挂接项变化：从有到无、从无到有或在有的基础上进行修改
		boolean openColumnConfig = false;
		boolean columnConfig = Strings.isNotBlank(request.getParameter("column"));
		if(!columnConfig) {	
			// 如果修改前具备栏目挂接，修改后不具备，清空栏目挂接项
			if(bizConfig.hasColumnConfig()) { 
				this.formBizConfigColumnManager.deleteColumns(bizConfigId);
			}
		} 
		// 修改后具备栏目挂接
		else {
			String[] idAndCategorysColumns = request.getParameterValues("columnIdAndCategory");
			String[] columnNames = request.getParameterValues("columnName");
			String[] oldColumnNames = request.getParameterValues("oldColumnName");
			// 如果修改前后栏目挂接项名称数组未变，不作处理，反之则清除旧记录，生成新的栏目挂接项
			if(!Arrays.equals(columnNames, oldColumnNames)) {
				if(bizConfig.hasColumnConfig()) {
					this.formBizConfigColumnManager.deleteColumns(bizConfigId);
				}
				else {
					// 修改前不具备栏目挂接，修改后具备，则保存时需打开栏目发布页面方便用户操作
					openColumnConfig = true;
				}
				this.formBizConfigColumnManager.saveColumns(idAndCategorysColumns, columnNames, bizConfigId);
			}
		}
		
		// 处理菜单挂接项变化：从有到无、从无到有或在有的基础上进行修改
		boolean menuConfig = Strings.isNotBlank(request.getParameter("menu"));
		if(!menuConfig) {
			// 如果修改前具备菜单挂接，修改后不具备，清空菜单挂接项、菜单并最终刷新菜单显示
			if(bizConfig.hasMenuConfig()) {
				Menu mainMenu = this.menuManager.getMainMenu4BizConfig(bizConfigId);
				if(mainMenu != null) {
					Long mainMenuId = mainMenu.getId();
					this.menuManager.deleteMenuProfiles(oldMembers, mainMenuId);
					this.menuManager.deleteWithBizConfig(bizConfigId);
					this.formBizConfigManager.deleteMenuProfiles(bizConfigId);
					needRefreshMenu = true;
				} else {
					// 异常情况记录
					logger.warn("业务配置[" + bizConfig.getName() + ", ID=" + bizConfigId + "]具备菜单挂接，但对应的一级菜单却已不存在!");
				}
			}
		} 
		else {
			String[] menuIdAndCategorys = request.getParameterValues("menuIdAndCategory");
			String[] menuNames = request.getParameterValues("menuName");
			// 修改前后均具备菜单挂接
			List<Long> sharers = scopeChanged ? newMembers : oldMembers;
			if(CollectionUtils.isNotEmpty(sharers)) {
				sharers.remove(bizConfig.getCreateUser());
			}
			
			if(bizConfig.hasMenuConfig()) { 
				Menu mainMenu = this.menuManager.getMainMenu4BizConfig(bizConfigId);
				
				if(mainMenu != null) {
					Long mainMenuId = mainMenu.getId();
					// 如果业务配置名称改变了，其对应的一级菜单名称也需要随之改变，并且需要刷新菜单显示
					if(isNameChanged) {	
						this.menuManager.updateMenuField(mainMenuId, new String[]{"name"}, new Object[]{newBizConfigName});
						needRefreshMenu = true;
					}
					
					// 对新增的共享者和删除的共享者更新其对应的菜单个性化信息记录
					this.formBizConfigManager.updateMenuSetting(mainMenuId, sharersAdded, true);
					this.formBizConfigManager.updateMenuSetting(mainMenuId, sharersReduced, false);
					
					// 如果二级菜单项修改前后名称数组等同，表明用户未对二级菜单项作出修改，不作处理，反之则清除旧有记录，生成新的菜单挂接项
					String[] oldMenuIdAndCategorys = request.getParameterValues("oldMenuIdAndCategory");
					String[] oldMenuNames = request.getParameterValues("oldMenuName");
					
					if(!Arrays.equals(menuIdAndCategorys, oldMenuIdAndCategorys) || !Arrays.equals(menuNames, oldMenuNames)) {
						needRefreshMenu = true;
						this.menuManager.deleteSubMenusWithBizConfig(bizConfigId);
						this.formBizConfigManager.deleteSubMenuProfiles(bizConfigId, mainMenuId);
						this.formBizConfigManager.saveSubMenusAndProfiles(bizConfigId, newTempleteIds, mainMenuId, menuIdAndCategorys, menuNames);
					}
				} 
				else {
					// 异常情况记录
					logger.warn("业务配置[" + bizConfig.getName() + ", ID=" + bizConfigId + "]具备菜单挂接，但对应的一级菜单却已不存在!");
					
					formBizConfigManager.saveMainAndSubMenus(menuIdAndCategorys, menuNames, bizConfig, newTempleteIds, sharers, null);
					needRefreshMenu = true;
				}
			} 
			// 修改前不具备菜单挂接，修改后具备
			else { 
				formBizConfigManager.saveMainAndSubMenus(menuIdAndCategorys, menuNames, bizConfig, newTempleteIds, sharers, null);
				needRefreshMenu = true;
			}			
		}
		
		// 更新表单业务配置的修改时间和挂接状态
		bizConfig.setUpdateDate(new Timestamp(System.currentTimeMillis()));
		bizConfig.setBizConfigType(FormBizConfigUtils.getBizConfigType(columnConfig, menuConfig));
		this.formBizConfigManager.updateBizConfig(bizConfig);
		
		String msgSenderName = CurrentUser.get().getName();
		// 给新增的共享对象和被取消共享的对象发送相应系统消息
		if(CollectionUtils.isNotEmpty(sharersAdded)) { 
			userMessageManager.sendSystemMessage(MessageContent.get("formbizconfig.share.label", msgSenderName, newBizConfigName), 
					ApplicationCategoryEnum.form, creatorId, 
					MessageReceiver.get(bizConfigId, sharersAdded, "message.link.formbizconfig.view", bizConfigId), bizConfigId);
		}
		if(CollectionUtils.isNotEmpty(sharersReduced)) { 
			userMessageManager.sendSystemMessage(MessageContent.get("formbizconfig.sharecancel.label", msgSenderName, newBizConfigName), 
					ApplicationCategoryEnum.form, creatorId, 
					MessageReceiver.get(bizConfigId, sharersReduced), bizConfigId);
		}
		
		super.rendJavaScript(response, "parent.refresh4Save('" + needRefreshMenu + "', '" + openColumnConfig + "', '" + bizConfigId + "');");
		return null;
	}
	
	/**
	 * 用户删除若干条选中的业务配置记录，根据所选中的业务配置情况，在操作完成后给出不同提示信息
	 */
	public ModelAndView  delete (HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Long> ids = FormBizConfigUtils.parseStr2Ids(request, "ids");
		boolean needRefreshMenu = formBizConfigManager.deleteBizConfigs(ids, CurrentUser.get().getId());		
		super.rendJavaScript(response, "parent.refresh4Delete('" + needRefreshMenu + "', '" + request.getParameter("areAllCreated") + "');");
		return null;
	}
	
	/**
	 * 用户选中一条有效业务配置(未删除、具备共享权，在前端使用AJAX进行校验)并点击"另存为"按钮时，弹出另存为页面
	 */
	public ModelAndView  toSaveAs (HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("formbizconfig/write/bizconfig_saveAs");
	}
	
	/**
	 * 用户将自己共享的表单业务配置另存为一条新记录
	 */
	public ModelAndView  saveAs (HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long bizConfigId = NumberUtils.toLong(request.getParameter("id"));
		FormBizConfig bizConfig = this.formBizConfigManager.findById(bizConfigId);
		Long userId = CurrentUser.get().getId();
		String newName = request.getParameter("newBizConfigName");
		
		// 在点击确定完成另存为操作之前，如果创建者删除了业务配置记录或取消了当前用户共享使用权，需加以防护
		if(!this.formBizConfigManager.isCreatorOrInShareScope(bizConfig, userId)) { 
			super.rendJavaScript(response, "alert('" + FormBizConfigUtils.getI18NValue("bizconfig.cannotsaveas.label") + "');" +
										   "parent.reFresh4SaveAs('false')");
			return null;
		}
		Long orginalBizConfigId = bizConfig.getId();
		FormBizConfig newBizConfig = new FormBizConfig(newName, new Timestamp(System.currentTimeMillis()), 
										 			   userId, null, bizConfig.getBizConfigType());
		this.formBizConfigManager.saveBizConfig(newBizConfig);
		Long newBizConfigId = newBizConfig.getId();
		
		// 复制表单模板与业务配置关系记录
		this.formBizConfigManager.cloneTempleteProfiles(orginalBizConfigId, newBizConfigId);
		
		// 复制共享范围
		String shareScope = this.formBizConfigManager.getShareScopeIds(orginalBizConfigId);
		List<Long> sharerIds = null;
		if(Strings.isNotBlank(shareScope)) {
			this.formBizConfigManager.saveShareInfo(shareScope, newBizConfigId);
			sharerIds = FormBizConfigUtils.getSharerIds(shareScope, userId, orgManager);
		}
		
		// 复制栏目挂接项：原业务配置选中了栏目挂接时才进行处理
		boolean columnConfig = newBizConfig.hasColumnConfig();
		if(columnConfig) {
			this.formBizConfigColumnManager.cloneColumns(orginalBizConfigId, newBizConfigId);
		}
		
		// 复制菜单挂接项：原业务配置选中了菜单挂接时才进行处理
		boolean menuConfig = newBizConfig.hasMenuConfig();
		if(menuConfig) {
			Long newMainMenuId = this.formBizConfigManager.saveMainMenuAndUpdateMenuSetting(newName, newBizConfigId, userId, sharerIds, null);
			this.formBizConfigManager.cloneSubMenus(orginalBizConfigId, newBizConfigId, newMainMenuId);
		}		
		
		if(CollectionUtils.isNotEmpty(sharerIds)) { 
			userMessageManager.sendSystemMessage(
					MessageContent.get("formbizconfig.share.label", CurrentUser.get().getName(), newName), 
					ApplicationCategoryEnum.form,
					userId,
					MessageReceiver.get(newBizConfigId, sharerIds, "message.link.formbizconfig.view", newBizConfigId),
					newBizConfigId);
		}

		super.rendJavaScript(response, "parent.reFresh4SaveAs('" + menuConfig + "', '" + columnConfig + "', '" + newBizConfigId + "')");
		return null;
	}
	
	/**
	 * 辅助方法：从首页栏目或菜单进入信息中心页面时进行防护校验<br>
	 * 需要进行防护的情况：<br>
	 * 1.点击菜单挂接项而来的情况：<br>
	 * 	1.1业务配置已被删除；<br>
	 * 	1.2业务配置未删除，但已取消菜单挂接；<br>
	 * 	1.3业务配置未删除，未取消菜单挂接，但信息中心子菜单已被创建者删除；<br>
	 * 	1.4业务配置未删除，未取消菜单挂接，信息中心子菜单存在，但用户已被取消了共享使用权。<br>
	 * 2.点击栏目挂接项而来的情况：<br>
	 * 	2.1业务配置已被删除；<br>
	 * 	2.2业务配置未删除，但已取消栏目挂接；<br>
	 * 	2.3业务配置未删除，未取消栏目挂接，但用户已被取消了共享使用权。
	 */
	private void validate4InfoCenter(ModelAndView mav, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long bizConfigId = NumberUtils.toLong(request.getParameter("bizConfigId"), -1l);
		FormBizConfig bizConfig = bizConfigId == -1l ? null : this.formBizConfigManager.findById(bizConfigId);
		String type = request.getParameter("type");
		Long memberId = CurrentUser.get().getId();
		
		boolean needProtection = false;
		Long infoCenterMenuId = null;
		boolean isInShareScope = this.formBizConfigManager.isCreatorOrInShareScope(bizConfig, memberId);
		
		// 用户是否从点击菜单操作入口而来
		boolean fromMenu = "menu".equals(type); 
		if(fromMenu) {
			Object[] result = this.formBizConfigManager.isInfoCenterMenuExist(bizConfig);
			boolean isInfoCenterMenuExist = (Boolean)result[0];
			infoCenterMenuId = (Long)result[1];
			needProtection = !isInfoCenterMenuExist || !isInShareScope;
		} else {
			needProtection = bizConfig == null || !bizConfig.hasColumnConfig() || !isInShareScope;
		}
		
		if(needProtection) {
			super.rendJavaScript(response, "alert('"+ FormBizConfigUtils.getI18NValue("bizconfig.invalid.label") + "\\n" +
											FormBizConfigUtils.getI18NValue("bizconfig.invalid.reason1.label") + "\\n" +
											FormBizConfigUtils.getI18NValue("bizconfig.invalid.reason2" + type + ".label") + "\\n" + 
											FormBizConfigUtils.getI18NValue("bizconfig.invalid.reason3" + type + ".label") + 
										   (fromMenu ? "\\n" + FormBizConfigUtils.getI18NValue("bizconfig.invalid.reason4" + type + ".label") : "") + "');" +
										   "parent.getA8Top().refreshAndBack('" + fromMenu + "');");
			mav = null;
		}
		
		if(mav != null) {
			mav.addObject("bizConfig", bizConfig);
			if(fromMenu) {
				mav.addObject("menuId", infoCenterMenuId);
			}
		}			
	}

	/**
	 * 从首页栏目或菜单进入信息中心页面
	 */
	public ModelAndView  enterManagerCenter (HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/bizconfig_infocenter");
		this.validate4InfoCenter(mav, request, response);
		if(mav == null)
			return null;
		
		List<Long> formTempIds = FormBizConfigUtils.parseStr2Ids(request, "tempIds");
		List<Templete> formTemps = this.templeteManager.getTempletesByIds(formTempIds);
		return mav.addObject("formIds", FormBizConfigUtils.getFormIds(formTemps));
	}
	
	/**
	 * 显示信息中心页面中表单流程部分的内容：待办事项、已办事项、已发事项、跟踪事项、督办事项
	 */
	public ModelAndView showFormFlows(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/bizconfig_formflows");
		List<Long> templeteIds = FormBizConfigUtils.parseStr2Ids(request, "tempIds");
		// 获取所选表单模板对应的待办事项、已办事项、已发事项、跟踪事项、督办事项总数目
		User user = CurrentUser.get();
		Long userId = user.getId();
        V3xOrgMember theMember = orgManager.getEntityById(V3xOrgMember.class,user.getId());
        if(theMember.getAgentId() == V3xOrgEntity.DEFAULT_NULL_ID) { 
            int pendings = colManager.getColCount(StateEnum.col_pending.key(), templeteIds);
			mav.addObject("pendings", pendings);
        } else {
	    	mav.addObject("pendings", 0);
        }
        
        mav.addObject("done", colManager.getColCount(StateEnum.col_done.key(), templeteIds));
       	mav.addObject("sent", colManager.getColCount(StateEnum.col_sent.key(), templeteIds));
        mav.addObject("track", affairManager.queryTrackCount4BizConfig(userId, templeteIds));
        int status = com.seeyon.v3x.collaboration.Constant.superviseState.supervising.ordinal();
        mav.addObject("supervise", this.formBizConfigManager.getSuperviseTotalCount4BizConfig(userId, status, templeteIds));
        
		return mav;
	}
	
	/**
	 * 业务配置信息中心各种事项列表中，需要获取元数据信息和事项总条数（避免在查询事项列表时发出重复sql计算总条数）
	 */
	private void setMetadataAndCount(ModelAndView mav, HttpServletRequest request) throws IOException {
		Map<String, Metadata> colMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.collaboration);
		Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);	
		mav.addObject("comImportanceMetadata", comImportanceMetadata).addObject("colMetadata", colMetadata);
		
		// 各类事项总数已在导航页面显示，无需在列表页面重复发出sql获取
		Pagination.setNeedCount(false);
		Pagination.setRowCount(NumberUtils.toInt(request.getParameter("count")));
	}
	
	/**
	 * 显示信息中心页面中表单流程部分的内容：待办事项
	 * @see com.seeyon.v3x.collaboration.controller.CollaborationController#listPending
	 */
	public ModelAndView  listPending (HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/bizconfig_pending_list");	
		this.setMetadataAndCount(mav, request);
	
        V3xOrgMember member = orgManager.getMemberById(CurrentUser.get().getId());        
        List<ColSummaryModel> pendingList = null;
        if(member != null && member.getAgentId() == V3xOrgEntity.DEFAULT_NULL_ID) {
        	pendingList = colManager.queryTodoList(FormBizConfigUtils.parseStr2Ids(request, "tempIds"));
        }
	    return mav.addObject("pendingList", pendingList);
	}
	
	/**
	 * 显示信息中心页面中表单流程部分的内容：已办事项
	 * @see com.seeyon.v3x.collaboration.controller.CollaborationController#listDone
	 */
	public ModelAndView  listDone (HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/bizconfig_done_list");
		this.setMetadataAndCount(mav, request);
    	List<ColSummaryModel> finishedList = colManager.queryFinishedList(FormBizConfigUtils.parseStr2Ids(request, "tempIds"));
        return mav.addObject("finishedList", finishedList);
	}
	
	/**
	 * 显示信息中心页面中表单流程部分的内容：已发事项
	 * @see com.seeyon.v3x.collaboration.controller.CollaborationController#listSent
	 */
	public ModelAndView  listSent (HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/bizconfig_sent_list");	
		this.setMetadataAndCount(mav, request);
		List<ColSummaryModel> csList = colManager.querySentList(FormBizConfigUtils.parseStr2Ids(request, "tempIds"));
        return mav.addObject("csList", csList);
	}	

	/**
	 * 显示信息中心页面中表单流程部分的内容：跟踪事项
	 * @see com.seeyon.v3x.main.controller.MainController#moreTrack
	 */
	public ModelAndView  listTrack (HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/bizconfig_track_list");		
		this.setMetadataAndCount(mav, request);
		List<Affair> affairs = affairManager.queryTrackList4BizConfig(CurrentUser.get().getId(), 
				FormBizConfigUtils.parseStr2Ids(request, "tempIds"));
		return mav.addObject("affairs", affairs);
	}
	
	/**
	 * 显示信息中心页面中表单流程部分的内容：督办事项
	 * @see com.seeyon.v3x.collaboration.controller.ColSuperviseController#superviseList
	 */
	public ModelAndView  listSupervise (HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/bizconfig_supervise_list");	
		this.setMetadataAndCount(mav, request);
		int status = com.seeyon.v3x.collaboration.Constant.superviseState.supervising.ordinal();
		List<ColSuperviseModel> list = this.colSuperviseManager.getSuperviseCollListByCondition(null, null, null, 
				CurrentUser.get().getId(), status, FormBizConfigUtils.parseStr2Ids(request, "tempIds"));
		return mav.addObject("superviseDetails", list).addObject("status", status); 
	}
	
	/**
	 * 用户取消表单业务配置中的栏目挂接
	 */
	public ModelAndView  cancelColumnConfig (HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long bizConfigId = Long.valueOf(request.getParameter("bizConfigId"));
		this.formBizConfigColumnManager.deleteColumns(bizConfigId);
		
		// 更新表单业务配置的挂接状态为未挂接或菜单挂接
		FormBizConfig bizConfig = this.formBizConfigManager.findById(bizConfigId);
		int newConfigType = bizConfig.getBizConfigType() == FormBizConfigConstants.CONFIG_TYPE_COLUMN ?
							FormBizConfigConstants.CONFIG_TYPE_NO : FormBizConfigConstants.CONFIG_TYPE_MENU;
		this.formBizConfigManager.updateBizConfigField(bizConfigId, new String[]{"bizConfigType", "updateDate"}, 
				new Object[]{newConfigType, new Timestamp(System.currentTimeMillis())});
		
		// 返回首页
		super.rendJavaScript(response, "alert('" + FormBizConfigUtils.getI18NValue("bizconfig.cancelcolumnconfig.label") + "');"  + 
		   							   "parent.getA8Top().refreshAndBack('false');");
		return null;
	}
	
	/**
	 * 用户取消表单业务配置中的菜单挂接
	 */
	public ModelAndView  cancelMenuConfig (HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long bizConfigId = Long.valueOf(request.getParameter("bizConfigId"));
		Long mainMenuId = this.menuManager.getMainMenu4BizConfig(bizConfigId).getId();
		String shareScope = this.formBizConfigManager.getShareScopeIds(bizConfigId);
		List<Long> memberIds = FormBizConfigUtils.getSharerAndCreatorIds(shareScope, CurrentUser.get().getId(), orgManager);
		
		menuManager.deleteMenuProfiles(memberIds, mainMenuId);
		menuManager.deleteWithBizConfig(bizConfigId);
		formBizConfigManager.deleteMenuProfiles(bizConfigId);
		
		FormBizConfig bizConfig = this.formBizConfigManager.findById(bizConfigId);
		int newBizConfigType =  bizConfig.getBizConfigType() == FormBizConfigConstants.CONFIG_TYPE_MENU ? 
								FormBizConfigConstants.CONFIG_TYPE_NO : FormBizConfigConstants.CONFIG_TYPE_COLUMN;	
		formBizConfigManager.updateBizConfigField(bizConfigId, new String[]{"bizConfigType", "updateDate"}, 
				new Object[]{newBizConfigType, new Timestamp(System.currentTimeMillis())});
		
		// 刷新顶部页面菜单显示，返回首页
		super.rendJavaScript(response, "alert('" + FormBizConfigUtils.getI18NValue("bizconfig.cancelmenuconfig.label") + "');" +
									   "parent.getA8Top().refreshAndBack('true');");
		return null;
	}
	
	/**
	 * 用户从栏目进入资产管理中心后,点击"修改配置"准备修改栏目挂接配置
	 */
	public ModelAndView  toEditColumnConfig (HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/bizconfig_column_config");
		User user = CurrentUser.get();
		Long bizConfigId = NumberUtils.toLong(request.getParameter("id"));
		List<Long> domainIds = FormBizConfigUtils.getUserDomainIds(CurrentUser.get(), this.orgManager);
		List<FormBizConfigColumn> columns = this.formBizConfigColumnManager.getSelectedColumns(domainIds, bizConfigId);
		
		mav.addObject("columns", columns);
		boolean isPublished = spaceManager.isBizConfigPublished(bizConfigId, user.getId(), user.getLoginAccount());
		mav.addObject("isPublished", isPublished);
		return mav;
	}
	
	/**
	 * 保存用户对栏目挂接配置的修改
	 */
	public ModelAndView  saveEditColumnConfig (HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long bizConfigId = NumberUtils.toLong(request.getParameter("id"));
		String[] idAndCategorysColumns = request.getParameterValues("columnIdAndCategory");
		String[] columnNames = request.getParameterValues("columnName");
		String[] oldColumnNames = request.getParameterValues("oldColumnName");
		if(!Arrays.equals(columnNames, oldColumnNames)) {
			this.formBizConfigColumnManager.deleteColumns(bizConfigId);
			this.formBizConfigColumnManager.saveColumns(idAndCategorysColumns, columnNames, bizConfigId);
		}
		super.rendJavaScript(response, "parent.close();");
		return null;
	}
	
	/**
	 * 用户从菜单进入信息中心后,点击"修改配置"准备修改菜单挂接配置
	 */
	public ModelAndView  toEditMenuConfig (HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/bizconfig_menu_config");
		Long bizConfigId = NumberUtils.toLong(request.getParameter("id"));
		List<Templete> temps = this.templeteManager.getTempletes4BizConfigWithoutAuthCheck(bizConfigId);
		List<MenuConfig> menuConfigs = this.formBizConfigManager.getMenuConfigs(bizConfigId);
		return mav.addObject("temps", temps).addObject("menuConfigs", menuConfigs);
	}
	
	/**
	 * 保存用户对菜单挂接配置的修改
	 */
	public ModelAndView  saveEditMenuConfig (HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = Long.valueOf(request.getParameter("id"));
		String[] menuIdAndCategorys = request.getParameterValues("menuIdAndCategory");
		String[] menuNames = request.getParameterValues("menuName");
		String[] oldMenuIdAndCategorys = request.getParameterValues("oldMenuIdAndCategory");
		String[] oldMenuNames = request.getParameterValues("oldMenuName");
		
		// 如果用户取消了"信息中心"菜单，那么保存完毕后要从信息中心页面返回到首页
		boolean stayAtInfoCenter = false;
		// 如果用户确实对菜单功能配置进行了改动，才有必要刷新菜单显示
		boolean reallyChanged = !Arrays.equals(menuIdAndCategorys, oldMenuIdAndCategorys) || !Arrays.equals(menuNames, oldMenuNames);
		if(reallyChanged) {
			String templeteIds = this.formBizConfigManager.getTempleteIds(id);
			Long mainMenuId = this.menuManager.getMainMenu4BizConfig(id).getId();
			// 判断"信息中心"菜单是否仍然存在
			for(int i=0; i<menuIdAndCategorys.length; i++) {
				String[] idAndCategory = menuIdAndCategorys[i].split(",");
				if(Integer.parseInt(idAndCategory[1]) == FormBizConfigConstants.MENU_INFO_CENTER) {
					stayAtInfoCenter = true;
					break;
				}
			}
			
			// 处理二级菜单:先全部清空二级菜单及关系记录，再重新生成新的二级菜单
			this.menuManager.deleteSubMenusWithBizConfig(id);
			this.formBizConfigManager.deleteSubMenuProfiles(id, mainMenuId);
			this.formBizConfigManager.saveSubMenusAndProfiles(id, templeteIds, mainMenuId, menuIdAndCategorys, menuNames);
		}
		super.rendJavaScript(response, "parent.refreshAfterSaveMenuConfig('" + reallyChanged + "', '" + stayAtInfoCenter + "')");
		return null;
	}
	
	/**
	 * 弹出表单业务配置所选的表单模板页面(包含表单模板及对应的表单信息)，用户可以点击链接新建协同
	 */
	public ModelAndView  showTempletes4NewCol (HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = NumberUtils.toLong(request.getParameter("id"));
		List<Long> domainIds = FormBizConfigUtils.getUserDomainIds(CurrentUser.get(), this.orgManager);
		List<Templete> tempModels = this.templeteManager.getTempletesWithFormInfo4BizConfig(id, domainIds);
		if(CollectionUtils.isEmpty(tempModels)) {
			super.rendJavaScript(response, "alert('" + FormBizConfigUtils.getI18NValue("bizconfig.novalidtemplete.label") + "');window.close();");
			return null;
		}
		return new ModelAndView("formbizconfig/write/bizconfig_templetes_col").addObject("tempModels", tempModels);
	}
	
	
	/**
	 * 
	 * 以下为业务配置管理模块内容
	 * 
	 * 
	 */
	public ModelAndView listBizManage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("formbizconfig/write/v3x_bizconfig_list_index");
	}
	
	public ModelAndView listAllBizConfigFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("formbizconfig/write/v3x_bizconfig_list_frame");
	}
	
	public ModelAndView listAllBizConfigManage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SearchModel searchModel = SearchModel.getSearchModel(request);
		List<V3xBizConfig> bizConfigList = v3xBizConfigManager.findAllByCondition(searchModel);
		return new ModelAndView("formbizconfig/write/v3x_bizconfig_list", "bizConfigList", bizConfigList);
	}
	
	public ModelAndView newBiz(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("formbizconfig/write/v3x_bizconfig_new");
	}
	
	public ModelAndView newSaveBiz(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Long creatorId = user.getId();
		String bizConfigName = request.getParameter("bizConfigName");
		String[] sourceTypeAry = request.getParameterValues("sourceType");
		String[] sourceIdAry = request.getParameterValues("sourceId");
		String[] bizConfigItemNameAry = request.getParameterValues("menuName");
		String[] formAppmainIdAry = request.getParameterValues("formAppmainId");
		String[] flowMenuTypeNameAry = request.getParameterValues("flowMenuTypeName");
		
		List<Menu> allMenuList = new ArrayList<Menu>();
		
		//保存业务配置管理
		V3xBizConfig bizConfig = new V3xBizConfig();
		bizConfig.setNewId();
		bizConfig.setName(bizConfigName);
		bizConfig.setMenuId(UUIDLong.longUUID());
		bizConfig.setCreateUser(creatorId);
		bizConfig.setCreateDate(new Timestamp(System.currentTimeMillis()));
		
		Long bizConfigId = bizConfig.getId();
		Long firMenuId = bizConfig.getMenuId();
		
		//一级菜单
		int maxSortId = menuManager.getMaxSortId4NewMenu();
		Menu parentMenu = new Menu();
		parentMenu.setId(firMenuId);
		parentMenu.setName(bizConfig.getName());
		parentMenu.setType(Menu.TYPE.formAppBindBizConfig.ordinal());
		parentMenu.setSortId(++maxSortId);
		allMenuList.add(parentMenu);
		
		
		//保存业务配置管理明细
		List<V3xBizConfigItem> bizConfigItemList = new ArrayList<V3xBizConfigItem>();
		for (int k = 0; k < sourceIdAry.length; k++) {
			Integer sourceType = new Integer(sourceTypeAry[k]);
			Long sourceId = new Long(sourceIdAry[k]);
			String bizConfigItemName = bizConfigItemNameAry[k];
			V3xBizConfigItem bizConfigItem = new V3xBizConfigItem();
			bizConfigItem.setNewId();
			bizConfigItem.setBizConfigId(bizConfigId);
			bizConfigItem.setName(bizConfigItemName);
			bizConfigItem.setSourceType(sourceType);
			bizConfigItem.setSourceId(sourceId);
			bizConfigItem.setMenuId(UUIDLong.longUUID());
			if(sourceType == FormBizConfigConstants.SOURCE_TYPE_FLOWTEMPLATE || sourceType == FormBizConfigConstants.SOURCE_TYPE_INFOMANAGE
					|| sourceType == FormBizConfigConstants.SOURCE_TYPE_BASEDATA || sourceType == FormBizConfigConstants.SOURCE_TYPE_QUERY
					|| sourceType == FormBizConfigConstants.SOURCE_TYPE_REPORT){
				bizConfigItem.setFormAppmainId(new Long(formAppmainIdAry[k]));
				if(sourceType == FormBizConfigConstants.SOURCE_TYPE_FLOWTEMPLATE){
					String flowMenuType = request.getParameter(flowMenuTypeNameAry[k]);
					bizConfigItem.setFlowMenuType(new Integer(flowMenuType));
				}
			}
			bizConfigItem.setSortId(k+1);
			bizConfigItemList.add(bizConfigItem);
			
			//菜单明细
			Menu childMenu = new Menu();
			childMenu.setId(bizConfigItem.getMenuId());
			childMenu.setParentId(parentMenu.getId());
			childMenu.setSortId(k+1);
			childMenu.setType(Menu.TYPE.formAppBindBizConfig.ordinal());
			childMenu.setName(bizConfigItemName);
			childMenu.setTarget("main");
			childMenu.setIcon("/common/images/left/icon/newMenu.gif");
			childMenu.setAction(FormBizConfigUtils.getAction(bizConfigItem));
			allMenuList.add(childMenu);
		}
		
		//保存授权信息
		String shareScope = request.getParameter("shareScopeIds");
		List<V3xBizAuthority> v3xBizAuthorityList = new ArrayList<V3xBizAuthority>();	
		if(Strings.isNotBlank(shareScope)) {
			String[][] scopeInfo = Strings.getSelectPeopleElements(shareScope);
	    	if(scopeInfo != null){
	    		SelectPersonOperation spc = new SelectPersonOperation();
	    		for (String[] strings : scopeInfo) {
	    			V3xBizAuthority auth=new V3xBizAuthority();
	    			auth.setNewId();
	    			auth.setBizConfigId(bizConfigId);
	    			auth.setScopeType(spc.changeType(strings[0]));
	    			auth.setScopeId(Long.parseLong(strings[1]));
	    			v3xBizAuthorityList.add(auth);
	    		}
	    	}
		}
		
		v3xBizConfigManager.saveBizConfig(bizConfig, bizConfigItemList, v3xBizAuthorityList);
		
		//保存菜单
		menuManager.saveAllMenus(allMenuList);
		//保存个性化配置
		FormBizConfigUtils.saveMenuProfile(v3xBizAuthorityList, null, firMenuId);//TODO性能优化点
		if(Strings.isBlank(request.getParameter("from"))){
			return listBizManage(request,response);
		}else{
			super.rendJavaScript(response, "window.returnValue='true';window.close();");
			return null;
		}
	}
	
	public ModelAndView editViewBiz(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/v3x_bizconfig_new");
		Long bizConfigId = new Long(request.getParameter("bizConfigId"));
		V3xBizConfig bizConfig = v3xBizConfigManager.findBizConfigById(bizConfigId);
		mav.addObject("bizConfig", bizConfig);
		List<V3xBizAuthority> authList = bizConfig.getV3xBizAuthorityList();
		StringBuffer scopeIds = new StringBuffer("");
		if(CollectionUtils.isNotEmpty(authList)) {
			for(V3xBizAuthority auth : authList) {
				scopeIds.append(auth.getScopeTypeStr() + "|" + auth.getScopeId() + ",");
			}
		}
		String scopeIdStr = Strings.isBlank(scopeIds.toString()) ? "" : scopeIds.substring(0, scopeIds.length() - 1);
		mav.addObject("scopeIds", scopeIdStr);
		return mav;
	}
	public ModelAndView formdetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("formbizconfig/write/v3x_bizconfig_detail");
		Long bizConfigId = new Long(request.getParameter("bizConfigId"));
		V3xBizConfig bizConfig = v3xBizConfigManager.findBizConfigById(bizConfigId);
		mav.addObject("bizConfig", bizConfig);
		List<V3xBizAuthority> authList = bizConfig.getV3xBizAuthorityList();
		StringBuffer scopeIds = new StringBuffer("");
		if(CollectionUtils.isNotEmpty(authList)) {
			for(V3xBizAuthority auth : authList) {
				scopeIds.append(auth.getScopeTypeStr() + "|" + auth.getScopeId() + ",");
			}
		}
		String scopeIdStr = Strings.isBlank(scopeIds.toString()) ? "" : scopeIds.substring(0, scopeIds.length() - 1);
		mav.addObject("scopeIds", scopeIdStr);
		return mav;
	}
	public ModelAndView editSaveBiz(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long bizConfigId = new Long(request.getParameter("bizConfigId"));
		String bizConfigName = request.getParameter("bizConfigName");
		String[] sourceTypeAry = request.getParameterValues("sourceType");
		String[] sourceIdAry = request.getParameterValues("sourceId");
		String[] bizConfigItemNameAry = request.getParameterValues("menuName");
		String[] formAppmainIdAry = request.getParameterValues("formAppmainId");
		String[] flowMenuTypeNameAry = request.getParameterValues("flowMenuTypeName");
		
		//保存业务配置管理
		V3xBizConfig bizConfig = v3xBizConfigManager.findBizConfigById(bizConfigId);
		List<V3xBizAuthority> oldV3xBizAuthorityList = bizConfig.getV3xBizAuthorityList();
		bizConfig.setName(bizConfigName);
		bizConfig.setUpdateDate(new Timestamp(System.currentTimeMillis()));
		
		//修改一级菜单
		List<Menu> allMenuList = new ArrayList<Menu>();
		Long firMenuId = bizConfig.getMenuId();
		Menu parentMenu = menuManager.getMenuById(firMenuId);
		parentMenu.setName(bizConfig.getName());
		allMenuList.add(parentMenu);
		menuManager.updateAllMenus(allMenuList);
		allMenuList.clear();
		
		//保存业务配置管理明细
		List<V3xBizConfigItem> bizConfigItemList = new ArrayList<V3xBizConfigItem>();
		for (int k = 0; k < sourceIdAry.length; k++) {
			Integer sourceType = new Integer(sourceTypeAry[k]);
			Long sourceId = new Long(sourceIdAry[k]);
			String bizConfigItemName = bizConfigItemNameAry[k];
			V3xBizConfigItem bizConfigItem = new V3xBizConfigItem();
			bizConfigItem.setNewId();
			bizConfigItem.setBizConfigId(bizConfigId);
			bizConfigItem.setName(bizConfigItemName);
			bizConfigItem.setSourceType(sourceType);
			bizConfigItem.setSourceId(sourceId);
			bizConfigItem.setMenuId(UUIDLong.longUUID());
			if(sourceType == FormBizConfigConstants.SOURCE_TYPE_FLOWTEMPLATE || sourceType == FormBizConfigConstants.SOURCE_TYPE_INFOMANAGE
					|| sourceType == FormBizConfigConstants.SOURCE_TYPE_BASEDATA || sourceType == FormBizConfigConstants.SOURCE_TYPE_QUERY
					|| sourceType == FormBizConfigConstants.SOURCE_TYPE_REPORT){
				bizConfigItem.setFormAppmainId(new Long(formAppmainIdAry[k]));
				if(sourceType == FormBizConfigConstants.SOURCE_TYPE_FLOWTEMPLATE){
					String flowMenuType = request.getParameter(flowMenuTypeNameAry[k]);
					bizConfigItem.setFlowMenuType(new Integer(flowMenuType));
				}
			}
			bizConfigItem.setSortId(k+1);
			bizConfigItemList.add(bizConfigItem);
			
			//菜单明细
			Menu childMenu = new Menu();
			childMenu.setId(bizConfigItem.getMenuId());
			childMenu.setParentId(parentMenu.getId());
			childMenu.setSortId(k+1);
			childMenu.setType(Menu.TYPE.formAppBindBizConfig.ordinal());
			childMenu.setName(bizConfigItemName);
			childMenu.setTarget("main");
			childMenu.setIcon("/common/images/left/icon/newMenu.gif");
			childMenu.setAction(FormBizConfigUtils.getAction(bizConfigItem));
			allMenuList.add(childMenu);
		}
		
		//保存二级菜单
		menuManager.deleteMenuByParentId(parentMenu.getId());
		menuManager.saveAllMenus(allMenuList);
		
		//保存授权信息
		String shareScope = request.getParameter("shareScopeIds");
		List<V3xBizAuthority> v3xBizAuthorityList = new ArrayList<V3xBizAuthority>();	
		if(Strings.isNotBlank(shareScope)) {
			String[][] scopeInfo = Strings.getSelectPeopleElements(shareScope);
	    	if(scopeInfo != null){
	    		SelectPersonOperation spc = new SelectPersonOperation();
	    		for (String[] strings : scopeInfo) {
	    			V3xBizAuthority auth = new V3xBizAuthority();
	    			auth.setNewId();
	    			auth.setBizConfigId(bizConfigId);
	    			auth.setScopeType(spc.changeType(strings[0]));
	    			auth.setScopeId(Long.parseLong(strings[1]));
	    			v3xBizAuthorityList.add(auth);
	    		}
	    	}
		}

		v3xBizConfigManager.updateBizConfig(bizConfig, bizConfigItemList, v3xBizAuthorityList);
		
		//保存个性化配置
		FormBizConfigUtils.saveMenuProfile(v3xBizAuthorityList, oldV3xBizAuthorityList, firMenuId);
		super.rendJavaScript(response, "window.returnValue='true';window.close();");
		return null;
	}
	
	public ModelAndView deleteBiz(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] bizIdAry = request.getParameterValues("id");
		if(bizIdAry!=null && bizIdAry.length!=0){
			List<Long> bizConfigIds = new ArrayList<Long>();
			for (String bizId : bizIdAry) {
				bizConfigIds.add(new Long(bizId));
			}
			v3xBizConfigManager.deleteBizConfig(bizConfigIds);
		}
		super.rendJavaScript(response, "parent.location.reload();");
	  	return null;
	}
	
	public ModelAndView bizShowPublcInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("formbizconfig/write/show_bizconfig_pubinfo");
	}
	//公共信息展现的树查询的方法
	public ModelAndView showPublicInfoTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav=new ModelAndView("formbizconfig/write/show_bizconfig_pubinfo_tree");
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		//bul
		List<BulType> groupBulList = bulTypeManager.groupFindAll();
		List<BulType> adminBulList = bulTypeManager.boardFindAllByAccountId(accountId);
		mav.addObject("groupBulList", groupBulList);
		mav.addObject("adminBulList", adminBulList);
		// inquiry
		List<InquirySurveytype> groupInquiryList=inquiryManager.getGroupSurveyTypeList();
		List<InquirySurveytype> adminInquiryList=inquiryManager.getAccountSurveyTypeList(user.getAccountId());
		mav.addObject("groupInquiryList", groupInquiryList);
		mav.addObject("adminInquiryList", adminInquiryList);
		//bbs
		List<V3xBbsBoard> groupBbsBoardList=bbsBoardManager.getAllGroupBbsBoard();
		List<V3xBbsBoard> adminBbsBoardList=bbsBoardManager.getAllCorporationBbsBoard(accountId);
		mav.addObject("groupBbsBoardList", groupBbsBoardList);
		mav.addObject("adminBbsBoardList", adminBbsBoardList);
		//news
		List<NewsType> groupNewsList=newsTypeManager.groupFindAll();
		List<NewsType> adminNewsList=newsTypeManager.findAll(user.getLoginAccount());
		mav.addObject("groupNewsList", groupNewsList);
		mav.addObject("adminNewsList", adminNewsList);
		return mav;
	} 
	
	public ModelAndView listBizColList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("formbizconfig/write/v3x_bizconfig_collaboration_list").addObject("templeteId", request.getParameter("templeteId"));
	}
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setFormBizConfigManager(FormBizConfigManager formBizConfigManager) {
		this.formBizConfigManager = formBizConfigManager;
	}
	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
	public void setTempleteManager(TempleteManager templeteManager) {
		this.templeteManager = templeteManager;
	}
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}
	public void setTempleteCategoryManager(TempleteCategoryManager templeteCategoryManager) {
		this.templeteCategoryManager = templeteCategoryManager;
	}
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}
	public void setIOperBase(IOperBase operBase) {
		iOperBase = operBase;
	}
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	public void setColManager(ColManager colManager) {
		this.colManager = colManager;
	}
	public void setColSuperviseManager(ColSuperviseManager colSuperviseManager) {
		this.colSuperviseManager = colSuperviseManager;
	}
	public void setFormBizConfigColumnManager(FormBizConfigColumnManager formBizConfigColumnManager) {
		this.formBizConfigColumnManager = formBizConfigColumnManager;
	}
	public void setV3xBizConfigManager(V3xBizConfigManager v3xBizConfigManager) {
		this.v3xBizConfigManager = v3xBizConfigManager;
	}
	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}
	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}
}