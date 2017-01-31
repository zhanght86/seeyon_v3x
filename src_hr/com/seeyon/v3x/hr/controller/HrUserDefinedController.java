package com.seeyon.v3x.hr.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.hr.PagePropertyConstant;
import com.seeyon.v3x.hr.domain.Page;
import com.seeyon.v3x.hr.domain.PageLabel;
import com.seeyon.v3x.hr.domain.PageProperties;
import com.seeyon.v3x.hr.domain.PageProperty;
import com.seeyon.v3x.hr.domain.PropertyCategory;
import com.seeyon.v3x.hr.domain.PropertyLabel;
import com.seeyon.v3x.hr.domain.Repository;
import com.seeyon.v3x.hr.manager.UserDefinedManager;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.hr.webmodel.WebProperty;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.util.CommonTools;

@CheckRoleAccess(roleTypes = {RoleType.HrAdmin, RoleType.SalaryAdmin})
public class HrUserDefinedController extends BaseController {
	
	private UserDefinedManager userDefinedManager;
	private String jsonView;

	public String getJsonView() {
		return jsonView;
	}

	public void setJsonView(String jsonView) {
		this.jsonView = jsonView;
	}

	public UserDefinedManager getUserDefinedManager() {
		return userDefinedManager;
	}

	public void setUserDefinedManager(UserDefinedManager userDefinedManager) {
		this.userDefinedManager = userDefinedManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/userDefined/home");
	}

	public ModelAndView homeEntry(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/userDefined/homeEntry");
	}
	
	@SuppressWarnings("unchecked")
	public ModelAndView initSpace(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = null;
		User user = CurrentUser.get();
		int settingType = RequestUtils.getIntParameter(request, "settingType", 1);
		if (settingType == 1) {
			mav = new ModelAndView("hr/userDefined/listCategory");
			List<PropertyCategory> categories = this.userDefinedManager.getCategorysByRemove(PagePropertyConstant.Page_Remove_No, true);
			List<PropertyCategory> temp = this.userDefinedManager.getCategorysByRemove(PagePropertyConstant.Page_Remove_No, false);
			CommonTools.addAllIgnoreEmpty(categories, temp);
			categories = CommonTools.pagenate(categories);
			mav.addObject("categories", categories);
		} else if (settingType == 2) {
			mav = new ModelAndView("hr/userDefined/listProperty");
			List<PageProperty> properties = this.userDefinedManager.getPropertyByRemove(PagePropertyConstant.Page_Remove_No, true);
			List<PageProperty> temp = this.userDefinedManager.getPropertyByRemove(PagePropertyConstant.Page_Remove_No, false);
			CommonTools.addAllIgnoreEmpty(properties, temp);
			List<WebProperty> webProperties = new ArrayList<WebProperty>();
			for (PageProperty property : properties) {
				WebProperty webProperty = new WebProperty();
				webProperty.setProperty_id(property.getId());
				webProperty.setPropertyName(property.getName());
				int type = Integer.parseInt(String.valueOf(property.getType()));
				if (type == PagePropertyConstant.Page_Property_Integer) {
					webProperty.setType("hr.userDefined.type.integer.label");
				} else if (type == PagePropertyConstant.Page_Property_Float) {
					webProperty.setType("hr.userDefined.type.float.label");
				} else if (type == PagePropertyConstant.Page_Property_Date) {
					webProperty.setType("hr.userDefined.type.date.label");
				} else if (type == PagePropertyConstant.Page_Property_Varchar) {
					webProperty.setType("hr.userDefined.type.varchar.label");
				} else {
					webProperty.setType("hr.userDefined.type.text.label");
				}

				if (property.getNot_null() == PagePropertyConstant.Page_Property_Null) {
					webProperty.setNot_null("hr.userDefined.yes.label");
				} else {
					webProperty.setNot_null("hr.userDefined.no.label");
				}
				if (property.getCategory_id() != null) {
					webProperty.setCategory(this.userDefinedManager.getCategoryById(property.getCategory_id()).getName());
				}
				Long property_id = property.getId();
				List<PropertyLabel> propertyLabels = this.userDefinedManager.getPropertyLabelByPropertyId(property_id);
				for (PropertyLabel label : propertyLabels) {
					if (label.getLanguage().equals("zh_CN")) {
						webProperty.setLabelName_zh(label.getPropertyLabelValue());
					} else if (label.getLanguage().equals("en")) {
						webProperty.setLabelName_en(label.getPropertyLabelValue());
					}
				}
				webProperty.setSysFlag(property.isSysFlag());
				webProperties.add(webProperty);
			}
			webProperties = CommonTools.pagenate(webProperties);
			mav.addObject("properties", webProperties);
		} else {
			mav = new ModelAndView("hr/userDefined/listPage");
			List<Page> pages = new ArrayList<Page>();
			boolean isSalaryAdmin = Functions.isRole(V3xOrgEntity.ORGENT_META_KEY_SALARYADMIN, user);
			boolean isHrAdmin = Functions.isRole(V3xOrgEntity.ORGENT_META_KEY_HRADMIN, user);
			if (isSalaryAdmin && isHrAdmin) {
				List<Page> salaryPages = this.userDefinedManager.getPageByModelName(PagePropertyConstant.Page_ModelName_Salary, false, true);
				if (CollectionUtils.isNotEmpty(salaryPages)) {
					pages.addAll(salaryPages);
				}
				List<Page> staffPages = this.userDefinedManager.getPageByModelName(PagePropertyConstant.Page_ModelName_Staff, false, true);
				if (CollectionUtils.isNotEmpty(staffPages)) {
					pages.addAll(staffPages);
				}
			} else if (isSalaryAdmin) {
				List<Page> salaryPages = this.userDefinedManager.getPageByModelName(PagePropertyConstant.Page_ModelName_Salary, false, true);
				if (CollectionUtils.isNotEmpty(salaryPages)) {
					pages.addAll(salaryPages);
				}
			} else {
				List<Page> staffPages = this.userDefinedManager.getPageByModelName(PagePropertyConstant.Page_ModelName_Staff, false, true);
				if (CollectionUtils.isNotEmpty(staffPages)) {
					pages.addAll(staffPages);
				}
			}
			
			List<WebProperty> webPages = new ArrayList<WebProperty>();
			for (Page page : pages) {
				WebProperty webPage = new WebProperty();
				webPage.setPage_id(page.getId());
				webPage.setPageName(page.getPageName());
				webPage.setMemo(page.getMemo());
				if (page.getModelName().equals("salary")) {
					webPage.setModelName("menu.hr.laborageMgr");
				} else {
					webPage.setModelName("menu.hr.staffinfoMgr");
				}
				int display = page.getPageDisplay();
				int repair = page.getRepair();
				if (display == 0) {
					webPage.setDisplay("hr.userDefined.yes.label");
				} else {
					webPage.setDisplay("hr.userDefined.no.label");
				}
				if (repair == 0) {
					webPage.setRepair("hr.userDefined.yes.label");
				} else {
					webPage.setRepair("hr.userDefined.no.label");
				}
				List<PageLabel> pageLabels = new ArrayList<PageLabel>();
				pageLabels = this.userDefinedManager.getPageLabelByPageId(page.getId());
				for (PageLabel label : pageLabels) {
					if (label.getLanguage().equals("zh_CN")) {
						webPage.setLabelName_zh(label.getPageLabelValue());
					} else if (label.getLanguage().equals("en")) {
						webPage.setLabelName_en(label.getPageLabelValue());
					}
				}
				List<PageProperty> properties = this.userDefinedManager.getPropertyByPageId(page.getId());
				if (properties.size() > 0) {
					webPage.setProperty_id(Long.parseLong("1"));
				} else {
					webPage.setProperty_id(Long.parseLong("0"));
				}
				webPage.setSysFlag(page.isSysFlag());
				webPages.add(webPage);
			}
			webPages = CommonTools.pagenate(webPages);
			mav.addObject("pages", webPages);
		}
		
		return mav;
	}
	
	/**
	 * 新建信息项类别
	 */
	public ModelAndView newCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/userDefined/newCategory");
	}
	
	/**
	 * 添加信息项类别
	 */
	public ModelAndView addCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (this.isSameName(request, "PropertyCategory", null)) {
			super.rendJavaScript(response, "parent.isSameName();");
			return null;
		}
		
		Long accountId = CurrentUser.get().getLoginAccount();
		int settingType = RequestUtils.getIntParameter(request, "settingType", 1);
		PropertyCategory category = new PropertyCategory();
		bind(request, category);
		category.setAccountId(accountId);
		this.userDefinedManager.addCategory(category);
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType, "parent.parent");
	}
	
	/**
	 * 查看信息项类别
	 */
	public ModelAndView viewCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/userDefined/updateCategory");
		boolean readonly = RequestUtils.getBooleanParameter(request, "readonly", true);
		boolean disabled = RequestUtils.getBooleanParameter(request, "disabled", true);
		Long category_id = CommonTools.parseStr2Ids(request, "category_id").get(0);
		PropertyCategory category = this.userDefinedManager.getCategoryById(category_id);
		mav.addObject("category", category);
		mav.addObject("readonly", readonly);
		mav.addObject("disabled", disabled);
		return mav;
	}
	
	/**
	 * 更新信息项类别
	 */
	public ModelAndView updateCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = RequestUtils.getLongParameter(request, "id");
		if (this.isSameName(request, "PropertyCategory", id)) {
			super.rendJavaScript(response, "parent.isSameName();");
			return null;
		}
		
		int settingType = RequestUtils.getIntParameter(request, "settingType", 1);
		PropertyCategory category = this.userDefinedManager.getCategoryById(id);
		bind(request, category);
		this.userDefinedManager.updateCategory(category);
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType, "parent.parent");
	}
	
	/**
	 * 删除信息项类别
	 */
	public ModelAndView destroyCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int settingType = RequestUtils.getIntParameter(request, "settingType", 1);
		List<Long> categoryIds = CommonTools.parseStr2Ids(request, "cIds");
		for (Long category_id : categoryIds) {
			if (this.userDefinedManager.getPropertyByCategoryId(category_id, PagePropertyConstant.Page_Remove_No).size() == 0) {
				PropertyCategory pc = userDefinedManager.getCategoryById(category_id);
				if (pc != null) {
					pc.setRemove(PagePropertyConstant.Page_Remove_Yes);
					userDefinedManager.updateCategory(pc);
				}
			}
		}
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType, "parent");
	}

	/**
	 * 新建信息项
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView newOption(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/userDefined/newOption");
		List categories = this.userDefinedManager.getCategorysByRemove(PagePropertyConstant.Page_Remove_No);
		mav.addObject("categories", categories);
		return mav;
	}
	
	/**
	 * 添加信息项
	 */
	public ModelAndView addOption(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (this.isSameName(request, "PageProperty", null)) {
			super.rendJavaScript(response, "parent.isSameName();");
			return null;
		}
		
		int settingType = RequestUtils.getIntParameter(request, "settingType", 1);
		Long accountId = CurrentUser.get().getLoginAccount();
		PageProperty pageProperty = new PageProperty();
		pageProperty.setName(RequestUtils.getStringParameter(request, "propertyName"));
		pageProperty.setType(Long.valueOf(RequestUtils.getStringParameter(request, "type")));
		pageProperty.setOrdering(0);
		pageProperty.setNot_null(Integer.parseInt(RequestUtils.getStringParameter(request, "notNull")));
		pageProperty.setRemove(PagePropertyConstant.Page_Remove_No);
		pageProperty.setLength(0);
		pageProperty.setCategory_id(RequestUtils.getLongParameter(request, "categoryId"));
		pageProperty.setSysFlag(false);
		pageProperty.setAccountId(accountId);
		this.userDefinedManager.addPageProperty(pageProperty);

		String propertyLabel_en = RequestUtils.getStringParameter(request, "propertyLabel_en");
		List<Locale> locales = LocaleContext.getAllLocales();
		/** 重要G6BUG_G6_v1.0_成都市教育局_工资条管理员账号打开工资奖金管理不显示自定义的信息项_20120712011617 xiangfan 20120717  */
		boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();
		if(isGovVersion && !locales.contains(Locale.ENGLISH.getLanguage())){
			locales.add(new Locale("en"));
		}
		/** 重要G6BUG_G6_v1.0_成都市教育局_工资条管理员账号打开工资奖金管理不显示自定义的信息项_20120712011617 xiangfan 20120717  */
		int i = 0;
		int y = 0;
		for (Locale locale : locales) {
			PropertyLabel propertyLabel = new PropertyLabel();
			propertyLabel.setProperty_id(pageProperty.getId());
			if (locale.getLanguage().equals(Locale.CHINESE.getLanguage()) && i < 1) {
				propertyLabel.setLanguage("zh_CN");
				propertyLabel.setPropertyLabelValue(pageProperty.getName());
				i++;
				this.userDefinedManager.addPropertyLabel(propertyLabel);
			}
			if (locale.getLanguage().equals(Locale.ENGLISH.getLanguage()) && y < 1) {
				propertyLabel.setLanguage("en");
				propertyLabel.setPropertyLabelValue(propertyLabel_en);
				y++;
				this.userDefinedManager.addPropertyLabel(propertyLabel);
			}
			//lijl添加,GOV-3843.【人事管理】-【信息项设置】-【信息项】,用户自定义的信息项'英文'保存不起-------------Start
			/*重要G6BUG_G6_v1.0_成都市教育局_工资条管理员账号打开工资奖金管理不显示自定义的信息项_20120712011617 xiangfan 20120717 注释掉
			 * boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();
			if(isGovVersion){
				if (y < 1) {
					propertyLabel.setLanguage("en");
					propertyLabel.setPropertyLabelValue(propertyLabel_en);
					y++;
					this.userDefinedManager.addPropertyLabel(propertyLabel);
				}
			}*/
			//lijl添加,GOV-3843.【人事管理】-【信息项设置】-【信息项】,用户自定义的信息项'英文'保存不起-------------End
		}
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType, "parent.parent");
	}
	
	/**
	 * 查看信息项
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView viewOption(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/userDefined/updateOption");
		Long property_id = CommonTools.parseStr2Ids(request, "property_id").get(0);
		PageProperty property = new PageProperty();
		property = this.userDefinedManager.getPropertyById(property_id);
		List categories = new ArrayList();
		categories = this.userDefinedManager.getCategorysByRemove(PagePropertyConstant.Page_Remove_No);
		List<PropertyLabel> propertyLabels = this.userDefinedManager.getPropertyLabelByPropertyId(property.getId());
		String labelName_zh = "";
		String labelName_en = "";
		for (PropertyLabel label : propertyLabels) {
			if (label.getLanguage().equals("zh_CN"))
				labelName_zh = label.getPropertyLabelValue();
			else if (label.getLanguage().equals("en"))
				labelName_en = label.getPropertyLabelValue();
		}

		mav.addObject("labelName_zh", labelName_zh);
		mav.addObject("labelName_en", labelName_en);
		mav.addObject("property", property);
		mav.addObject("categories", categories);
		mav.addObject("readonly", RequestUtils.getBooleanParameter(request, "readonly", true));
		mav.addObject("disabled", RequestUtils.getBooleanParameter(request, "disabled", true));
		return mav;
	}
	
	/**
	 * 更新信息项
	 */
	public ModelAndView updateOption(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long property_id = RequestUtils.getLongParameter(request, "id");
		if (this.isSameName(request, "PageProperty", property_id)) {
			super.rendJavaScript(response, "parent.isSameName();");
			return null;
		}
		
		int settingType = RequestUtils.getIntParameter(request, "settingType", 1);
		PageProperty property = this.userDefinedManager.getPropertyById(property_id);
		property.setName(RequestUtils.getStringParameter(request, "propertyName"));
		property.setType(Long.valueOf(RequestUtils.getStringParameter(request, "type")));
		property.setNot_null(Integer.parseInt(RequestUtils.getStringParameter(request, "notNull")));
		property.setCategory_id(RequestUtils.getLongParameter(request, "categoryId"));
		this.userDefinedManager.updatePageProperty(property);

		String propertyLabel_en = RequestUtils.getStringParameter(request, "propertyLabel_en");
		List<PropertyLabel> propertyLabels = this.userDefinedManager.getPropertyLabelByPropertyId(property.getId());
		for (PropertyLabel label : propertyLabels) {
			if (label.getLanguage().equals("zh_CN")) {
				label.setPropertyLabelValue(property.getName());
				this.userDefinedManager.updatePropertyLabel(label);
			} else if (label.getLanguage().equals("en")) {
				label.setPropertyLabelValue(propertyLabel_en);
				this.userDefinedManager.updatePropertyLabel(label);
			}
		}
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType, "parent.parent");
	}
	
	/**
	 * 删除信息项
	 */
	public ModelAndView destroyOption(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int settingType = RequestUtils.getIntParameter(request, "settingType", 1);
		List<Long> propertyIds = CommonTools.parseStr2Ids(request, "oIds");
		List<Repository> repositorys = userDefinedManager.getRepositoryPropertyId(propertyIds);
		if (CollectionUtils.isNotEmpty(repositorys)) {
			PrintWriter out = response.getWriter();
			out = response.getWriter();
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('HRLang.hr_userDefined_property_isempty_message'));");
			out.println("</script>");
			out.flush();
			return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType, "parent");
		}
		
		for (Long property_id : propertyIds) {
			PageProperty property = userDefinedManager.getPropertyById(property_id);
			property.setRemove(PagePropertyConstant.Page_Remove_Yes);
			this.userDefinedManager.updatePageProperty(property);
			this.userDefinedManager.deletePagePropertiesByPropertyId(property_id);
		}
		
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType, "parent");
	}
	
	/**
	 * 添加、查看、更新页签
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView viewPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/userDefined/updatePage");
		String page_id = request.getParameter("page_id");
		boolean isNew = page_id == null ? true : false;
		Page page = null;
		List<PageProperty> uses = new ArrayList<PageProperty>();
		if (isNew) {
			page = new Page(0, false, true);
			mav.addObject("isNew", true);
		} else {
			Long pageId = CommonTools.parseStr2Ids(page_id).get(0);
			page = this.userDefinedManager.getPageById(pageId);
			
			mav.addObject("readonly", RequestUtils.getBooleanParameter(request, "readonly", false));
			
			List<PageLabel> pageLabels = new ArrayList<PageLabel>();
			pageLabels = this.userDefinedManager.getPageLabelByPageId(pageId);
			String labelName_en = "";
			for (PageLabel pageLabel : pageLabels) {
				if (pageLabel.getLanguage().equals("en")) {
					labelName_en = pageLabel.getPageLabelValue();
				}
			}
			mav.addObject("labelName_en", labelName_en);
			
			uses = this.userDefinedManager.getPropertyByPageId(pageId);
		}
		
		List<Page> pages = this.userDefinedManager.getPageByRemove(PagePropertyConstant.Page_Remove_No);
		List<WebProperty> webPages = new ArrayList<WebProperty>();
		for (Page pg : pages) {
			List<PageLabel> pgLabels = new ArrayList<PageLabel>();
			WebProperty webPage = new WebProperty();
			webPage.setModelName(pg.getModelName());
			pgLabels = this.userDefinedManager.getPageLabelByPageId(pg.getId());
			for (PageLabel label : pgLabels) {
				if (label.getLanguage().equals("en")) {
					webPage.setLabelName_en(label.getPageLabelValue());
				} else {
					webPage.setLabelName_zh(label.getPageLabelValue());
				}
				webPage.setPage_id(label.getPage_id());
			}
			webPages.add(webPage);
		}
		mav.addObject("webPages", webPages);
		
		List<PropertyCategory> categories = this.userDefinedManager.getCategorysByRemove(PagePropertyConstant.Page_Remove_No);
		mav.addObject("categories", categories);
		mav.addObject("uses", uses);
		mav.addObject("page", page);
		
		return mav;
	}
	
	/**
	 * 保存页签
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView updatePage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int settingType = RequestUtils.getIntParameter(request, "settingType", 1);
		boolean isNew = RequestUtils.getBooleanParameter(request, "isNew", false);
		Page page = null;
		if (isNew) {
			if (this.isSameName(request, "Page", null)) {
				super.rendJavaScript(response, "parent.isSameName();");
				return null;
			}
			
			page = new Page();
			bind(request, page);
			page.setPageNo(0);
			page.setRepair(0);
			page.setRemove(PagePropertyConstant.Page_Remove_No);
			page.setAccountId(CurrentUser.get().getLoginAccount());
			page.setSysFlag(false);
			
			//排序
			List<Page> pageList = null;
			if (PagePropertyConstant.Page_ModelName_Salary.equals(page.getModelName())) {
				pageList = this.userDefinedManager.getPageByModelName(PagePropertyConstant.Page_ModelName_Salary, false, true);
			} else {
				pageList = this.userDefinedManager.getPageByModelName(PagePropertyConstant.Page_ModelName_Staff, false, true);
			}
			if (CollectionUtils.isNotEmpty(pageList)) {
				Page topPage = pageList.get(pageList.size() - 1);
				page.setSort(topPage.getSort() + 1);
			}
			
			this.userDefinedManager.addPage(page);

			List<Locale> languages = LocaleContext.getAllLocales();
			/** 重要G6BUG_G6_v1.0_成都市教育局_工资条管理员账号打开工资奖金管理不显示自定义的信息项_20120712011617 xiangfan 20120717  */
			boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();
			if(isGovVersion && !languages.contains(Locale.ENGLISH.getLanguage())){
				languages.add(new Locale("en"));
			}
			/** 重要G6BUG_G6_v1.0_成都市教育局_工资条管理员账号打开工资奖金管理不显示自定义的信息项_20120712011617 xiangfan 20120717  */
			int x = 0, y = 0;
			for (Locale language : languages) {
				PageLabel pageLabel = new PageLabel();
				pageLabel.setPage_id(page.getId());
				if (language.getLanguage().equals(Locale.CHINESE.getLanguage())) {
					if (x < 1) {
						pageLabel.setLanguage("zh_CN");
						pageLabel.setPageLabelValue(page.getPageName());
						this.userDefinedManager.addPageLabel(pageLabel);
						x++;
					}
				}
				if (language.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
					if (y < 1) {
						String pageLabel_en = request.getParameter("pageLabel_en");
						pageLabel.setLanguage("en");
						pageLabel.setPageLabelValue(pageLabel_en);
						this.userDefinedManager.addPageLabel(pageLabel);
						y++;
					}
				}
			}
		} else {
			Long page_id = RequestUtils.getLongParameter(request, "page_id");
			if (this.isSameName(request, "Page", page_id)) {
				super.rendJavaScript(response, "parent.isSameName();");
				return null;
			}
			
			page = this.userDefinedManager.getPageById(page_id);
			bind(request, page);
			this.userDefinedManager.updatePage(page);
			
			List<PageLabel> pageLabels = this.userDefinedManager.getPageLabelByPageId(page_id);
			for (PageLabel pageLabel : pageLabels) {
				if (pageLabel.getLanguage().equals("zh_CN")) {
					pageLabel.setPageLabelValue(page.getPageName());
					this.userDefinedManager.updatePageLabel(pageLabel);
				} else if (pageLabel.getLanguage().equals("en")) {
					String pageLabel_en = request.getParameter("pageLabel_en");
					if (pageLabel_en != null) {
						pageLabel.setPageLabelValue(pageLabel_en);
						this.userDefinedManager.updatePageLabel(pageLabel);
					}
				}
			}
			
			//删除原来的信息项
			this.userDefinedManager.deletePageProperties(page_id);
		}
		
		List<Long> pIds = CommonTools.parseStr2Ids(request, "pIds");
		if (pIds != null) {
			List<PageProperties> pagePropertiesList = new ArrayList<PageProperties>();
			int i = 0;
			for (Long property_id : pIds) {
				PageProperty property = this.userDefinedManager.getPropertyById(property_id);
				PageProperties pageProperties = new PageProperties();
				pageProperties.setIdIfNew();
				pageProperties.setPage(page);
				pageProperties.setPageProperty(property);
				pageProperties.setProperty_ordering(i ++);
				pagePropertiesList.add(pageProperties);
			}
			this.userDefinedManager.addAllProperties(pagePropertiesList);
		}
		
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType, "parent.parent");
	}
	
	/**
	 * 删除页签
	 */
	public ModelAndView destroyPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int settingType = RequestUtils.getIntParameter(request, "settingType", 1);
		int save = RequestUtils.getIntParameter(request, "save");
		List<Long> pageIds = CommonTools.parseStr2Ids(request, "pIds");
		if (save == 1) {
			this.userDefinedManager.deletePage(pageIds);
			for (Long page_id : pageIds) {
				this.userDefinedManager.deleteRespository(page_id);
			}
		} else {
			for (Long page_id : pageIds) {
				Page page = this.userDefinedManager.getPageById(page_id);
				page.setRemove(PagePropertyConstant.Page_Remove_Yes);
				this.userDefinedManager.updatePage(page);
				this.userDefinedManager.deletePageProperties(page_id);
			}
		}
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=" + settingType, "parent");
	}
	
	/**
	 * 切换信息项类别
	 */
	public ModelAndView changeCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String category = request.getParameter("categoryId");
		List<PageProperty> properties = new ArrayList<PageProperty>();
		if (category != null && !category.equals("")) {
			Long categoryId = Long.valueOf(category);
			properties = this.userDefinedManager.findUnUsePropertyByCategoryId(categoryId, PagePropertyConstant.Page_Remove_No);
		}
		boolean isAjax = RequestUtils.getRequiredBooleanParameter(request, "ajax");

		JSONArray jsonArray = new JSONArray();
		for (PageProperty property : properties) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.putOpt("optionValue", property.getId().toString());
			jsonObject.putOpt("optionDisplay", Functions.toHTML(property.getName()));
			jsonArray.put(jsonObject);
		}
		String view = null;
		if (isAjax) {
			view = this.getJsonView();
		}
		return new ModelAndView(view, Constants.AJAX_JSON, jsonArray);
	}
	
	/**
	 * 验证是否是相同的信息项类别
	 */
	public ModelAndView isEmptyOfCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean isAjax = RequestUtils.getRequiredBooleanParameter(request, "ajax");
		List<Long> categoryIds = CommonTools.parseStr2Ids(request, "cIds");
		JSONObject jsonObject = new JSONObject();
		boolean isEmpty = true;
		for (Long category_id : categoryIds) {
			if (this.userDefinedManager.getPropertyByCategoryId(category_id, PagePropertyConstant.Page_Remove_No).size() > 0) {
				PropertyCategory category = this.userDefinedManager.getCategoryById(category_id);
				jsonObject.putOpt("categoryId", category_id);
				jsonObject.putOpt("categoryName", category.getName());
				isEmpty = false;
				jsonObject.putOpt("isEmpty", isEmpty);
				break;
			}
		}
		if (isEmpty)
			jsonObject.putOpt("isEmpty", isEmpty);
		String view = null;
		if (isAjax)
			view = this.getJsonView();
		return new ModelAndView(view, Constants.AJAX_JSON, jsonObject);
	}

	/**
	 * 页签排序
	 */
	public ModelAndView pageOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Page> pages = this.userDefinedManager.getPageByModelName(PagePropertyConstant.Page_ModelName_Salary, false, true);
		return new ModelAndView("hr/userDefined/pageOrder", "pages", pages);
	}

	/**
	 * 保存页签排序
	 */
	public ModelAndView saveOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.userDefinedManager.updatePageOrder(request.getParameterValues("pageIds"));
		return super.redirectModelAndView("/hrUserDefined.do?method=homeEntry&settingType=3", "parent");
	}
	
	/**
	 * 判断信息项类别、信息项、页签是否重名
	 */
	private boolean isSameName(HttpServletRequest request, String type, Long id) {
		if ("PropertyCategory".equals(type)) {
			String categoryName = request.getParameter("name").trim();
			List<PropertyCategory> categorys = this.userDefinedManager.getCategorysByRemove(PagePropertyConstant.Page_Remove_No);
			for (PropertyCategory category : categorys) {
				if (!category.getId().equals(id) && category.getName().equals(categoryName)) {
					return true;
				}
			}
		} else if ("PageProperty".equals(type)) {
			String propertyName = request.getParameter("propertyName").trim();
			List<PageProperty> properties = userDefinedManager.getPropertyByRemove(PagePropertyConstant.Page_Remove_No);
			for (PageProperty property : properties) {
				if (!property.getId().equals(id) && property.getName().equals(propertyName)) {
					return true;
				}
			}
		} else if ("Page".equals(type)) {
			String pageName = request.getParameter("pageName").trim();
			String modelName = request.getParameter("modelName");
			List<Page> pages = this.userDefinedManager.getPageByModelName(modelName, false, true);
			for (Page page : pages) {
				if (!page.getId().equals(id) && page.getPageName().equals(pageName)) {
					return true;
				}
			}
		}
		return false;
	}

}