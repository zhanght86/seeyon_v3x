package com.seeyon.v3x.hr.controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.task.TaskManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.hr.PagePropertyConstant;
import com.seeyon.v3x.hr.domain.Page;
import com.seeyon.v3x.hr.domain.PageLabel;
import com.seeyon.v3x.hr.domain.PageProperty;
import com.seeyon.v3x.hr.domain.PropertyLabel;
import com.seeyon.v3x.hr.domain.Repository;
import com.seeyon.v3x.hr.domain.Salary;
import com.seeyon.v3x.hr.manager.SalaryManager;
import com.seeyon.v3x.hr.manager.UserDefinedManager;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.hr.util.SalaryUserDefinedHelper;
import com.seeyon.v3x.hr.webmodel.ImportReport;
import com.seeyon.v3x.hr.webmodel.WebProperty;
import com.seeyon.v3x.hr.webmodel.WebSalary;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgRelationship;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.system.util.PwdStrengthValidationUtil;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 员工工资管理
 */
@CheckRoleAccess(roleTypes = RoleType.HrAdmin)
public class SalaryController extends BaseController {
	private transient static final Log log = LogFactory.getLog(SalaryController.class);

	private SalaryManager salaryManager;
	private OrgManager orgManager;
	private AttachmentManager attachmentManager;
	private FileManager fileManager;
	private FileToExcelManager fileToExcelManager;
	private UserDefinedManager userDefinedManager;
	private TaskManager taskManager;
	private String jsonView;
	
	public String getJsonView() {
		return jsonView;
	}

	public void setJsonView(String jsonView) {
		this.jsonView = jsonView;
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	public void setTaskManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}

	public UserDefinedManager getUserDefinedManager() {
		return userDefinedManager;
	}

	public void setUserDefinedManager(UserDefinedManager userDefinedManager) {
		this.userDefinedManager = userDefinedManager;
	}

	public SalaryManager getSalaryManager() {
		return salaryManager;
	}

	public void setSalaryManager(SalaryManager salaryManager) {
		this.salaryManager = salaryManager;
	}

	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
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

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	@CheckRoleAccess(roleTypes = RoleType.SalaryAdmin)
	public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/salary/home");
	}

	@CheckRoleAccess(roleTypes = RoleType.SalaryAdmin)
	public ModelAndView homeEntry(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/salary/homeEntry");
	}

	@CheckRoleAccess(roleTypes = RoleType.SalaryAdmin)
	public ModelAndView toolBar(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/salary/toolbar").addObject("attachments", new ArrayList<Attachment>());
	}
	
	/**
	 * 所有员工工资列表
	 */
	@CheckRoleAccess(roleTypes = RoleType.SalaryAdmin)
	public ModelAndView salaryInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/salary/salaryInfo");
		Long accountId = CurrentUser.get().getLoginAccount();
		String condition = RequestUtils.getStringParameter(request, "condition");
		String staffName = request.getParameter("staffName");
		String fromTime = request.getParameter("fromTime");
		String toTime = request.getParameter("toTime");
		String salaryDeptId = request.getParameter("salaryDeptId");
		
		try {
			List<Salary> salarys = new ArrayList<Salary>();
			if (StringUtils.isBlank(condition)) {
				salarys = salaryManager.findAllAccountStaffSalary(accountId, null, null, null, null, null);
			} else {
				if ("staffName".equals(condition)) {
					salarys = this.salaryManager.findAllAccountStaffSalary(accountId, "staffName", staffName, null, null, null);
				} else if ("salaryDate".equals(condition)) {
					if (StringUtils.isNotBlank(fromTime) && StringUtils.isNotBlank(toTime)) {
						String[] from = fromTime.split("-");
						String[] end = toTime.split("-");
						salarys = this.salaryManager.findAllAccountStaffSalary(accountId, "salaryDate", from[0], from[1], end[0], end[1]);
					}
				} else if ("salaryDept".equals(condition)) {
					salarys = this.salaryManager.findAllAccountStaffSalary(accountId, "salaryDept", salaryDeptId, null, null, null);
				}
			}
			
			List<Long> salaryIds = CommonTools.getIds(salarys);
			List<WebSalary> results = this.getWebSalary(salarys);
			List<Page> hrPages = userDefinedManager.getPageByModelName("salary");
			Map<Long, List<PageProperty>> pageProperties = SalaryUserDefinedHelper.getPageProperties(this.userDefinedManager, hrPages);
			mav.addObject("salarys", results);
			mav.addObject("hrPages", hrPages);
			mav.addObject("pageProperties", pageProperties);
			mav.addObject("propertyTypes", SalaryUserDefinedHelper.getPropertyTypes(request, this.userDefinedManager, pageProperties));
			mav.addObject("propertyValues", SalaryUserDefinedHelper.getSalaryAdminRepositoryPropertyId(salaryIds, this.userDefinedManager, salarys, pageProperties));

			int size = Pagination.getRowCount();
			if (results != null) {
				if (size == 0) {
					size = results.size();
				}
			}

			int pageSize = NumberUtils.toInt(request.getParameter("pageSize"), Pagination.getMaxResults());
			if (pageSize < 1) {
				pageSize = Pagination.getMaxResults();
			}

			int pages = (size + pageSize - 1) / pageSize;
			if (pages < 1) {
				pages = 1;
			}

			int page = NumberUtils.toInt(request.getParameter("page"), 1);
			if (page < 1) {
				page = 1;
			} else if (page > pages) {
				page = pages;
			}

			mav.addObject("size", size);
			mav.addObject("pageSize", pageSize);
			mav.addObject("pages", pages);
			mav.addObject("page", page);
			
			mav.addObject("condition", condition);
			mav.addObject("staffName", staffName);
			mav.addObject("fromTime", fromTime);
			mav.addObject("toTime", toTime);
			mav.addObject("salaryDeptId", salaryDeptId);
		} catch (Exception e) {
			log.error("查看所有员工工资列表：", e);
		}

		return mav;
	}

	/**
	 * 新建工资
	 */
	@SuppressWarnings("unchecked")
	@CheckRoleAccess(roleTypes = RoleType.SalaryAdmin)
	public ModelAndView newSalaryInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/salary/salaryDetail");
		List<Page> pages = userDefinedManager.getPageByModelName("salary");
		Map<Long, List<WebProperty>> pageProperties = new LinkedHashMap<Long, List<WebProperty>>();
		if (CollectionUtils.isNotEmpty(pages)) {
			for (Page page : pages) {
				List<WebProperty> webProperties = new ArrayList<WebProperty>();
				List<PageProperty> properties = this.userDefinedManager.getPropertyByPageId(page.getId());
				for (PageProperty property : properties) {
					WebProperty webProperty = new WebProperty();
					webProperty.setProperty_id(property.getId());
					webProperty.setPage_id(page.getId());
					if (property.getNot_null() == PagePropertyConstant.Page_Property_NotNull) {
						webProperty.setNot_null("no");
					} else {
						webProperty.setNot_null("yes");
					}
					webProperty.setPropertyType(Integer.parseInt(String.valueOf(property.getType())));
					
					List<PageLabel> pageLabels = this.userDefinedManager.getPageLabelByPageId(page.getId());
					for (PageLabel label : pageLabels) {
						if (label.getLanguage().equals("zh_CN")) {
							webProperty.setPageName_zh(label.getPageLabelValue());
						} else {
							webProperty.setPageName_en(label.getPageLabelValue());
						}
					}
					List<PropertyLabel> labels = this.userDefinedManager.getPropertyLabelByPropertyId(property.getId());
					
					for (PropertyLabel label : labels) {
						if (label.getLanguage().equals("zh_CN")) {
							webProperty.setLabelName_zh(label.getPropertyLabelValue());
						} else {
							webProperty.setLabelName_en(label.getPropertyLabelValue());
						}
					}
					webProperties.add(webProperty);
				}
				pageProperties.put(page.getId(), webProperties);
			}
		}
		mav.addObject("pages", pages);
		mav.addObject("pageProperties", pageProperties);
		mav.addObject("isCreated", true);
		return mav;
	}

	/**
	 * 保存工资
	 */
	@CheckRoleAccess(roleTypes = RoleType.SalaryAdmin)
	public ModelAndView saveSalary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		Date operatingTime = new Date();
		
		List<Long> staffIds = CommonTools.parseStr2Ids(request, "staffIds");
		List<String> staffNames = this.toNameList(request.getParameter("staffNames"));
		String yearMonth = request.getParameter("yearMonth");
		String[] year_month = yearMonth.split("-");
		int year = NumberUtils.toInt(year_month[0]);
		int month = NumberUtils.toInt(year_month[1]);
		
		List<PageProperty> properties = SalaryUserDefinedHelper.getPageProperties(userDefinedManager);

		List<Salary> salaryList = new ArrayList<Salary>();
		List<Repository> repositoryList = new ArrayList<Repository>();
		for (int i = 0; i < staffIds.size(); i ++) {
			Salary salary = new Salary();
			Long staffId = staffIds.get(i);
			bind(request, salary);
			salary.setIdIfNew();
			salary.setStaffId(staffId);
			salary.setName(staffNames.get(i));
			salary.setYear(year);
			salary.setMonth(month);
			salary.setCreatorId(user.getId());
			salary.setCreatedTimestamp(operatingTime);
			salary.setModifiedTimestamp(operatingTime);
			salary.setAccountId(accountId);

			int j = 0;
			for (PageProperty property : properties) {
				Repository repository = new Repository();
				
				Long pageId = RequestUtils.getLongParameter(request, property.getId() + "_pageId");

				repository.setIdIfNew();
				repository.setMemberId(staffId);
				repository.setPage_id(pageId);
				repository.setProperty_id(property.getId());
				repository.setOperation_id(salary.getId());
				repository.setCreateTime(operatingTime);
				repository.setOrdering(j ++);
				
				int propertyType = property.getType().intValue();
				String propertyValue = request.getParameter(String.valueOf(property.getId()));
				SalaryUserDefinedHelper.addRepository(propertyType, propertyValue, repository);
				repositoryList.add(repository);
			}
			salaryList.add(salary);
		}
		this.userDefinedManager.addAllRepository(repositoryList);
		this.salaryManager.addAllSalary(salaryList);
		
		return super.refreshWorkspace();
	}

	/**
	 * 浏览工资
	 */
	@SuppressWarnings("unchecked")
	@CheckRoleAccess(roleTypes = RoleType.NeedNoCheck)
	public ModelAndView viewSalary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/salary/salaryDetail");
		Long accountId = CurrentUser.get().getLoginAccount();
		boolean dis = RequestUtils.getBooleanParameter(request, "dis", true);
		Long salaryId = RequestUtils.getLongParameter(request, "sId");
		Salary salary = this.salaryManager.findSalaryById(salaryId);
		
		List<Page> pages = userDefinedManager.getPageByModelName("salary", true, true);
		Map<Long, Page> pageMap = new HashMap<Long, Page>();
		if (CollectionUtils.isNotEmpty(pages)) {
			for (Page page : pages) {
				pageMap.put(page.getId(), page);
			}
		}
		
		List<PageProperty> propertys = userDefinedManager.getPropertyByAccount(accountId);
		Map<Long, PageProperty> propertyMap = new HashMap<Long, PageProperty>();
		if (CollectionUtils.isNotEmpty(propertys)) {
			for (PageProperty pageProperty : propertys) {
				propertyMap.put(pageProperty.getId(), pageProperty);
			}
		}
		
		List<Repository> repositories = this.userDefinedManager.getRepostoryByOperationId(salary.getId());
		Map<Long, List<WebProperty>> pageProperties = new LinkedHashMap<Long, List<WebProperty>>();
		
		if (CollectionUtils.isNotEmpty(repositories)) {
			for (Repository repository : repositories) {
				Long pageId = repository.getPage_id();
				
				WebProperty webProperty = new WebProperty();
				webProperty.setRepository_id(repository.getId());
				
				List<PageLabel> pageLabels = this.userDefinedManager.getPageLabelByPageId(pageId);
				for (PageLabel label : pageLabels) {
					if (label.getLanguage().equals("zh_CN")) {
						webProperty.setPageName_zh(label.getPageLabelValue());
					} else {
						webProperty.setPageName_en(label.getPageLabelValue());
					}
				}
				
				List<PropertyLabel> propertyLabels = this.userDefinedManager.getPropertyLabelByPropertyId(repository.getProperty_id());
				for (PropertyLabel label : propertyLabels) {
					if (label.getLanguage().equals("zh_CN")) {
						webProperty.setLabelName_zh(label.getPropertyLabelValue());
					} else {
						webProperty.setLabelName_en(label.getPropertyLabelValue());
					}
				}
				
				PageProperty pageProperty = propertyMap.get(repository.getProperty_id());
				
				if (pageProperty.getNot_null() == PagePropertyConstant.Page_Property_NotNull)
					webProperty.setNot_null("no");
				else
					webProperty.setNot_null("yes");
				
				if (pageProperty.getType() == PagePropertyConstant.Page_Property_Integer) {
					webProperty.setPropertyType(PagePropertyConstant.Page_Property_Integer);
					if (repository.getF1() != null) {
						webProperty.setF1(repository.getF1());
					}
				} else if (pageProperty.getType() == PagePropertyConstant.Page_Property_Float) {
					webProperty.setPropertyType(PagePropertyConstant.Page_Property_Float);
					if (repository.getF2() != null) {
						webProperty.setF2(repository.getF2());
					}
				} else if (pageProperty.getType() == PagePropertyConstant.Page_Property_Date) {
					webProperty.setPropertyType(PagePropertyConstant.Page_Property_Date);
					if (repository.getF3() != null) {
						webProperty.setF3(repository.getF3());
					}
				} else if (pageProperty.getType() == PagePropertyConstant.Page_Property_Varchar) {
					webProperty.setPropertyType(PagePropertyConstant.Page_Property_Varchar);
					webProperty.setF4(repository.getF4());
				} else {
					webProperty.setPropertyType(PagePropertyConstant.Page_Property_Text);
					webProperty.setF5(repository.getF5());
				}
				
				if (pageProperties.get(pageId) == null) {
					List<WebProperty> properties = new ArrayList<WebProperty>();
					properties.add(webProperty);
					pageProperties.put(pageId, properties);
				} else {
					pageProperties.get(pageId).add(webProperty);
				}
			}
		}
		
		mav.addObject("pageMap", pageMap);
		mav.addObject("pageProperties", pageProperties);
		mav.addObject("salary", salary);
		mav.addObject("dis", dis);
		return mav;
	}
	
	/**
	 * 更新工资信息
	 */
	@SuppressWarnings("unchecked")
	@CheckRoleAccess(roleTypes = RoleType.SalaryAdmin)
	public ModelAndView updateSalary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Salary salary = this.salaryManager.findSalaryById(RequestUtils.getLongParameter(request, "id"));
		
		if (salary != null) {
			List<Long> staffIds = CommonTools.parseStr2Ids(request, "staffIds");
			List<String> staffNames = this.toNameList(request.getParameter("staffNames"));
			String yearMonth = request.getParameter("yearMonth");
			
			Long staffId = staffIds.get(0);
			String[] year_month = yearMonth.split("-");
			int year = NumberUtils.toInt(year_month[0]);
			int month = NumberUtils.toInt(year_month[1]);
			
			super.bind(request, salary);
			salary.setStaffId(staffId);
			salary.setName(staffNames.get(0));
			salary.setYear(year);
			salary.setMonth(month);
			Date currentTime = new Date();
			salary.setModifiedTimestamp(currentTime);
			this.salaryManager.updateSalary(salary);
			
			List<Repository> repositories = this.userDefinedManager.getRepostoryByOperationId(salary.getId());
			
			if (CollectionUtils.isNotEmpty(repositories)) {
				for (Repository repository : repositories) {
					repository.setMemberId(staffId);
					
					int propertyType = RequestUtils.getIntParameter(request, repository.getId() + "_Type");
					String propertyValue = request.getParameter(String.valueOf(repository.getId()));
					SalaryUserDefinedHelper.updateRepository(propertyType, propertyValue, repository);
				}
				this.userDefinedManager.updateAllRepository(repositories);
			}
		}
		
		return super.refreshWorkspace();
	}
	
	/**
	 * 删除工资
	 */
	@CheckRoleAccess(roleTypes = RoleType.SalaryAdmin)
	public ModelAndView destroySalary(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Long> ids = CommonTools.parseStr2Ids(request, "sIds");
		this.userDefinedManager.deleteRepositoryByOperationId(ids);
		this.salaryManager.removeSalaryByIds(ids);
		return super.refreshWorkspace();
	}

	/**
	 * 根据姓名, 所属部门, 所属单位获取人员
	 */
	private V3xOrgMember getOrgMember(String memberName, String deptName, String accountName, Long accountId) throws Exception {
		// 在本单位中取
		List<V3xOrgMember> members = orgManager.getMembersByProperty("name", memberName, accountId);
		if (CollectionUtils.isNotEmpty(members)) {
			if (members.size() == 1) {
				return members.get(0);
			} else {
				List<V3xOrgDepartment> depts = orgManager.getEntitiesByName(V3xOrgDepartment.class, deptName, accountId);
				if (CollectionUtils.isNotEmpty(depts)) {
					for (V3xOrgMember member : members) {
						for (V3xOrgDepartment dept : depts) {
							if (member.getOrgDepartmentId().equals(dept.getId())) {
								return member;
							}
						}
					}
				}
			}
		}

		// 在兼职人员中取
		List<V3xOrgRelationship> rels = orgManager.getOrgInstance().getRelsByType(accountId, V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST);
		if (rels != null) {
			for (V3xOrgRelationship rel : rels) {
				V3xOrgMember member = orgManager.getMemberById(rel.getSourceId());
				if (member != null) {
					V3xOrgAccount cntAccount = orgManager.getAccountById(member.getOrgAccountId());
					V3xOrgDepartment cntDept = orgManager.getDepartmentById(member.getOrgDepartmentId());
					
					if (member.getName().equals(memberName) && cntAccount.getShortname().equals(accountName) && cntDept.getName().equals(deptName)) {
						return member;
					}
				}
			}
		}

		return null;
	}
	
	/**
	 * 导入工资
	 */
	@CheckRoleAccess(roleTypes = RoleType.SalaryAdmin)
	public ModelAndView importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Long accountId = user.getLoginAccount();
		
		Salary salary = new Salary();
		salary.setIdIfNew();
		// 保存附件
		String attaFlag = this.attachmentManager.create(ApplicationCategoryEnum.hr, salary.getId(), salary.getId(), request);
		if (com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attaFlag)) {
			salary.setHasAttachment(true);
		}
		List<Attachment> attachments = this.attachmentManager.getByReference(salary.getId());
		
		boolean hasError = false;
		List<ImportReport> reportList = new ArrayList<ImportReport>();

		HttpSession session = request.getSession();
		String repeat = (String) session.getAttribute("repeat");
		
		// 所有已经存在的工资
		HashMap<String, Salary> salaryMap = this.getAllSalaryMap();
		
		//信息项
		List<Page> pages = userDefinedManager.getPageByModelName("salary");
		List<WebProperty> webPropertys = new ArrayList<WebProperty>();
		if (CollectionUtils.isNotEmpty(pages)) {
			for (Page page : pages) {
				List<PageProperty> properties = this.userDefinedManager.getPropertyByPageId(page.getId());
				for (PageProperty property : properties) {
					WebProperty webProperty = new WebProperty();
					webProperty.setProperty_id(property.getId());
					webProperty.setPage_id(page.getId());
					webProperty.setPropertyType(property.getType().intValue());
					webPropertys.add(webProperty);
				}
			}
		}

		String account_label = ResourceBundleUtil.getString(Constants.RESOURCE_HR, LocaleContext.getLocale(request), "hr.salary.account.label");

		for (Attachment attachment : attachments) {
			try {
				File file = this.fileManager.getFile(attachment.getFileUrl(), attachment.getCreatedate());
				// 读取文件类型(只能是xls格式的)
				String fileType = attachment.getFilename().substring(attachment.getFilename().indexOf('.') + 1);
				
				if (null != file && "xls".equalsIgnoreCase(fileType)) {
					List<List<String>> excelList = null;
					try {
						excelList = fileToExcelManager.readExcel(file);
						if (excelList==null) {
							PrintWriter out = response.getWriter();
							out.println("<script>");
							out.println("alert(parent.v3x.getMessage(\"HRLang.hr_file_read_failed\"));");
							out.println("parent.getA8Top().endProc();");
							out.println("</script>");
							out.flush();
							return null;
						}
					} catch (Exception e) {
						log.error("fileToExcelManager.readExcel  异常",e);
						hasError = true;
					}
					List<Salary> sList = new ArrayList<Salary>();
					List<Salary> uList = new ArrayList<Salary>();
					List<Repository> repositories = new ArrayList<Repository>();
					// 存放要删除的工资信息项（上传时选择覆盖）
					List<Long> dProperties = new ArrayList<Long>();
					
					if (CollectionUtils.isNotEmpty(excelList)) {
						//模板添加"单位"一列, 处理兼职人员工资, 如果是旧模板提示更新
						String accountLabel = excelList.get(1).get(2);
						if (!account_label.equals(accountLabel)) {
							PrintWriter out = response.getWriter();
							out.println("<script>");
							out.println("alert(parent.v3x.getMessage(\"HRLang.hr_file_insert_error_template\"));");
							out.println("parent.getA8Top().endProc();");
							out.println("</script>");
							out.flush();
							return null;
						}

						for (int i = 2; i < excelList.size(); i++) {
							List<String> salaryList = excelList.get(i);
							
							// 过滤无效项(姓名、部门、日期不能为空)
							if (StringUtils.isBlank(salaryList.get(0)) || Strings.isBlank(salaryList.get(1)) || StringUtils.isBlank(salaryList.get(3))) {
								hasError = true;
								ImportReport report = new ImportReport(salaryList.get(1), salaryList.get(0), false, Constants.IMPORT_REPORT_ERROR_1);
								reportList.add(report);
								log.info("人员：" + salaryList.get(2) + "|" + salaryList.get(1) + "|" + salaryList.get(0) + "无效，人员姓名、所属部门、工资月份不能为空！");
								continue;
							}
							
							String memberName = salaryList.get(0).trim();
							String depName = salaryList.get(1).trim();
							String accountName = salaryList.get(2).trim();

							V3xOrgMember member = this.getOrgMember(memberName, depName, accountName, accountId);
							if (member == null) {
								hasError = true;
								ImportReport report = new ImportReport(depName, memberName, false, Constants.IMPORT_REPORT_ERROR_2);
								reportList.add(report);
								log.info("人员：" + accountName + "|" + depName + "|" + memberName + "未找到！");
								continue;
							}
							
							String salaryTime = salaryList.get(3).trim();
							int year = 0;
							int month = 0;
							
							try {
								year = NumberUtils.toInt(salaryTime.substring(0, salaryTime.indexOf('-')));
                                int p = salaryTime.indexOf('-', salaryTime.indexOf('-') + 1);
                                p = p == -1 ? salaryTime.length() : p;
								month = NumberUtils.toInt(salaryTime.substring(salaryTime.indexOf('-') + 1,p));
							} catch (Exception e) {
								hasError = true;
								ImportReport report = new ImportReport(depName, memberName, false, Constants.IMPORT_REPORT_ERROR_3);
								reportList.add(report);
								log.info("人员：" + accountName + "|" + depName + "|" + memberName + "无效，《工资月份-" + salaryTime + "》格式不正确！");
								continue;
							}

							Salary s = (Salary) salaryMap.get(member.getId() + "-" + year + "-" + month);
							boolean isRepeat = s == null ? false : true;
							boolean isNew = false;

							if (!isRepeat) {
								s = new Salary();
								isNew = true;
							} else {
								if ("0".equals(repeat)) {// 存在时跳过,继续添加
									s = new Salary();
									isNew = true;
								}
							}

							s.setIdIfNew();
							s.setStaffId(member.getId());
							s.setName(member.getName());
							s.setYear(year);
							s.setMonth(month);
							s.setAccountId(accountId);
							s.setCreatorId(user.getId());
							Date now = new Date();
							s.setCreatedTimestamp(now);
							s.setModifiedTimestamp(now);
							
							if (!isNew) {
								// 将要覆盖的工资id放入集合，以便进行批量删除工资信息项
								dProperties.add(s.getId());
							}
							
							// 信息项
							int j = 0;
							for (WebProperty webProperty : webPropertys) {
								Repository r = new Repository();
								r.setIdIfNew();
								r.setCreateTime(new Date());
								r.setMemberId(s.getStaffId());
								r.setOperation_id(s.getId());
								r.setPage_id(webProperty.getPage_id());
								r.setProperty_id(webProperty.getProperty_id());
								r.setOrdering(j);

								int propertyType = webProperty.getPropertyType();
								String propertyValue = (salaryList.size() <= j + 4) ? "" : (String)salaryList.get(j + 4);
								SalaryUserDefinedHelper.addRepository(propertyType, propertyValue, r);

								repositories.add(r);
								j++;
							}

							if (isNew) {
								sList.add(s);
							} else {
								uList.add(s);
							}
						}
					}
					// 批量删除工资信息项
					this.userDefinedManager.deleteRepositoryByOperationId(dProperties);
					this.salaryManager.exportSalary(sList, uList, repositories);
				} else {
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage(\"HRLang.hr_insert_error_message\"));");
					out.println("parent.getA8Top().endProc();");
					out.println("</script>");
					out.flush();
					return null;
				}
				
				// 删除附件
				attachmentManager.deleteByReference(salary.getId());
				if (null != file) {
					// 删除物理文件
					file.delete();
				}
			} catch (RuntimeException e) {
				hasError = true;
				log.error("导入工资失败：", e);
			}
		}
		
		salary.setHasAttachment(false);
		
		PrintWriter out = response.getWriter();
		out.println("<script>");
		if (hasError) { // 有失败，生成日志
			session.setAttribute("reportList", reportList);
			out.println("parent.getA8Top().endProc();");
			out.println("parent.parent.document.getElementById('listFrame').src='/seeyon/hrSalary.do?method=salaryInfo&importReport=importReport'");
			out.println("parent.parent.document.getElementById('detailFrame').src='/seeyon/hrSalary.do?method=importReport'");
		
		} else { // 完全成功
			out.println("alert(parent.v3x.getMessage(\"HRLang.hr_file_import_success_message\"));");
			out.println("parent.getA8Top().endProc();");
			out.println("parent.parent.location.reload(true);");
		}
		out.println("</script>");
		out.flush();
		
		return null;
	}
	
	/**
	 * 生成导入报告
	 */
	@SuppressWarnings("unchecked")
	@CheckRoleAccess(roleTypes = RoleType.SalaryAdmin)
	public ModelAndView importReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("hr/salary/importReport");
		HttpSession session = request.getSession();
		List<ImportReport> reportList = (List<ImportReport>) session.getAttribute("reportList");
		modelAndView.addObject("reportList", CommonTools.pagenate(reportList));
		return modelAndView;
	}
	
	/**
	 * 导出导入报告
	 */
	@SuppressWarnings("unchecked")
	@CheckRoleAccess(roleTypes = RoleType.SalaryAdmin)
	public ModelAndView exportReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		List<ImportReport> reportList = (List<ImportReport>) session.getAttribute("reportList");
		List<DataRecord> records = new ArrayList<DataRecord>();
		Locale locale = LocaleContext.getLocale(request);

		if (CollectionUtils.isNotEmpty(reportList)) {
			DataRecord record = new DataRecord();
			this.initDataRecordForReport(record, request, locale);

			for (ImportReport report : reportList) {
				String data_label_ = ResourceBundleUtil.getString(Constants.RESOURCE_HR, locale, "import.report.data", report.getDeptName(), report.getMemberName());
				String result_label_ = ResourceBundleUtil.getString(Constants.RESOURCE_HR, locale, "import.report." + report.isSuccess());
				String description_label_ = ResourceBundleUtil.getString(Constants.RESOURCE_HR, locale, "import.report.error." + report.getError());

				DataRow row = new DataRow();
				row.addDataCell(data_label_, DataCell.DATA_TYPE_TEXT);
				row.addDataCell(result_label_, DataCell.DATA_TYPE_TEXT);
				row.addDataCell(description_label_, DataCell.DATA_TYPE_TEXT);
				record.addDataRow(row);
			}
			records.add(record);
		} else {
			DataRecord record = new DataRecord();
			this.initDataRecordForReport(record, request, locale);
			records.add(record);
		}

		DataRecord[] dataRecords = new DataRecord[records.size()];
		for (int i = 0; i < records.size(); i++) {
			dataRecords[i] = records.get(i);
		}

		this.fileToExcelManager.save(request, response, "salary_report", dataRecords);
		return null;
	}
	
	/**
	 * 按人、年、月将工资放入Map, 以便检查
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, Salary> getAllSalaryMap() {
		HashMap<String, Salary> salaryMap = new HashMap<String, Salary>();
		List<Salary> salarys = salaryManager.findAllStaffSalary();
		for (Salary s : salarys) {
			salaryMap.put(s.getStaffId() + "-" + s.getYear() + "-" + s.getMonth(), s);
		}
		return salaryMap;
	}
	
	/**
	 * 工资管理员-模板下载
	 */
	@CheckRoleAccess(roleTypes = RoleType.SalaryAdmin)
	public ModelAndView exportTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		
		DataRecord record = new DataRecord();

		// 自定义信息项
		List<PageProperty> pageProperties = SalaryUserDefinedHelper.getPageProperties(this.userDefinedManager);
		this.initDataRecord(record, pageProperties, request);
		
		DataRow row = new DataRow();
		
		// 姓名
		row.addDataCell(user.getName(), DataCell.DATA_TYPE_TEXT);
		
		// 所属部门
		V3xOrgDepartment dept = orgManager.getDepartmentById(user.getDepartmentId());
		row.addDataCell(dept != null ? dept.getName() : "", DataCell.DATA_TYPE_TEXT);
		
		// 所属单位
		V3xOrgAccount account = orgManager.getAccountById(user.getAccountId());
		row.addDataCell(account != null ? account.getShortname() : "", DataCell.DATA_TYPE_TEXT);
		
		// 工资年月份
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		row.addDataCell(year + "-" + month, DataCell.DATA_TYPE_TEXT);
		
		// 自定义信息项
		for (PageProperty property : pageProperties) {
			if (property.getType() == PagePropertyConstant.Page_Property_Integer) {
				row.addDataCell("0", DataCell.DATA_TYPE_INTEGER);
			} else if (property.getType() == PagePropertyConstant.Page_Property_Float) {
				row.addDataCell("0", DataCell.DATA_TYPE_NUMERIC);
			} else if (property.getType() == PagePropertyConstant.Page_Property_Date) {
				row.addDataCell(Datetimes.formatDate(new Date()), DataCell.DATA_TYPE_DATE);
			} else if (property.getType() == PagePropertyConstant.Page_Property_Varchar) {
				row.addDataCell("", DataCell.DATA_TYPE_TEXT);
			} else if (property.getType() == PagePropertyConstant.Page_Property_Text) {
				row.addDataCell("", DataCell.DATA_TYPE_TEXT);
			} else {
				row.addDataCell("", DataCell.DATA_TYPE_TEXT);
			}
		}

		record.addDataRow(row);

		DataRecord[] dataRecords = new DataRecord[1];
		dataRecords[0] = record;
		
		this.fileToExcelManager.save(request, response, "salary_template", dataRecords);
		return null;
	}
		
	/**
	 * 个人导出工资
	 */
	@SuppressWarnings("unchecked")
	@CheckRoleAccess(roleTypes = RoleType.NeedNoCheck)
	public ModelAndView exportExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Long staffId = user.getId();

		String fromTime = request.getParameter("fromTime");
		String toTime = request.getParameter("toTime");
		int isSearch = RequestUtils.getIntParameter(request, "isSearch", 0);

		List<Salary> salarys = new ArrayList<Salary>();

		if (StringUtils.isNotBlank(fromTime) && StringUtils.isNotBlank(toTime) && isSearch == 1) {
			String[] from = fromTime.split("-");
			String[] end = toTime.split("-");
			int fromYear = NumberUtils.toInt(from[0]);
			int fromMonth = NumberUtils.toInt(from[1]);
			int toYear = NumberUtils.toInt(end[0]);
			int toMonth = NumberUtils.toInt(end[1]);
			salarys = this.salaryManager.getSalaryByTime(staffId, fromYear, fromMonth, toYear, toMonth);
		} else {
			salarys = salaryManager.findSalaryByStaffId(staffId);
		}
		
		// 先循环一遍取出所有的信息项
		List<PageProperty> pageProperties = SalaryUserDefinedHelper.getPageProperties(this.userDefinedManager);
		List<Long> listPagePropertiesIds = CommonTools.getIds(pageProperties);

		List<Long> salaryIds = CommonTools.getIds(salarys);
		List<Repository> allRepository = userDefinedManager.getSalaryAdminRepositoryPropertyId(salaryIds, listPagePropertiesIds);
		
		List<DataRecord> records = new ArrayList<DataRecord>();
		
		if (salarys.size() != 0) {
			DataRecord record = new DataRecord();
			initDataRecord(record, pageProperties, request);
			for (Salary salary : salarys) {
				DataRow row = new DataRow();

				String memberName = "";
				String deptName = "";
				String accountName = "";
				V3xOrgMember member = orgManager.getMemberById(salary.getStaffId());
				if (member != null) {
					memberName = member.getName();

					V3xOrgDepartment dept = orgManager.getDepartmentById(member.getOrgDepartmentId());
					if (dept != null) {
						deptName = dept.getName();
					}

					V3xOrgAccount account = orgManager.getAccountById(member.getOrgAccountId());
					if (account != null) {
						accountName = account.getShortname();
					}
				}
				
				row.addDataCell(memberName, DataCell.DATA_TYPE_TEXT);
				row.addDataCell(deptName, DataCell.DATA_TYPE_TEXT);
				row.addDataCell(accountName, DataCell.DATA_TYPE_TEXT);

				row.addDataCell(salary.getYear() + "-" + salary.getMonth(), DataCell.DATA_TYPE_TEXT);

				for (PageProperty property : pageProperties) {
					Repository repository = SalaryUserDefinedHelper.getRepository(salary.getStaffId(), property.getId(), salary.getId(), allRepository);
					if (repository != null) {
						if (property.getType() == PagePropertyConstant.Page_Property_Integer) {
							if (repository.getF1() != null) {
								row.addDataCell(repository.getF1() + "", DataCell.DATA_TYPE_INTEGER);
							} else {
								row.addDataCell("0", DataCell.DATA_TYPE_INTEGER);
							}
						} else if (property.getType() == PagePropertyConstant.Page_Property_Float) {
							if (repository.getF2() != null) {
								row.addDataCell(repository.getF2() + "", DataCell.DATA_TYPE_NUMERIC);
							} else {
								row.addDataCell("0", DataCell.DATA_TYPE_NUMERIC);
							}
						} else if (property.getType() == PagePropertyConstant.Page_Property_Date) {
							if (repository.getF3() != null) {
								row.addDataCell(Datetimes.formatDate(repository.getF3()), DataCell.DATA_TYPE_DATE);
							} else {
								row.addDataCell("", DataCell.DATA_TYPE_DATE);
							}
						} else if (property.getType() == PagePropertyConstant.Page_Property_Varchar) {
							if (repository.getF4() != null) {
								row.addDataCell(repository.getF4() + "", DataCell.DATA_TYPE_TEXT);
							} else {
								row.addDataCell("", DataCell.DATA_TYPE_TEXT);
							}
						} else if (property.getType() == PagePropertyConstant.Page_Property_Text) {
							if (repository.getF5() != null) {
								row.addDataCell(repository.getF5() + "", DataCell.DATA_TYPE_TEXT);
							} else {
								row.addDataCell("", DataCell.DATA_TYPE_TEXT);
							}
						} else {
							row.addDataCell("", DataCell.DATA_TYPE_TEXT);
						}
					} else {
						row.addDataCell("", DataCell.DATA_TYPE_TEXT);
					}
				}
				
				record.addDataRow(row);
			}
			records.add(record);
		} else {
			DataRecord record = new DataRecord();
			initDataRecord(record, pageProperties, request);
			records.add(record);
		}

		DataRecord[] dataRecords = new DataRecord[records.size()];
		for (int i = 0; i < records.size(); i++) {
			dataRecords[i] = records.get(i);
		}
		
		if (records.isEmpty()) {
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage(\"HRLang.hr_export_noData\"));");
			out.println("</script>");
			out.flush();
		} else {
			this.fileToExcelManager.save(request, response, "salary", dataRecords);
		}
		
		return null;
	}
	
	@CheckRoleAccess(roleTypes = RoleType.SalaryAdmin)
	public ModelAndView membersNewSalaryPassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("hr/viewSalary/membersNewSalaryPassword");
		
		// 读取是否启用密码强度检查
		mv.addObject("pwdStrengthValidation", PwdStrengthValidationUtil.getPwdStrengthValidationValue());
		
		return mv;
	}
	
	private void initDataRecord(DataRecord record, List<PageProperty> properties, HttpServletRequest request) {
		Locale locale = LocaleContext.getLocale(request);
		String resource = Constants.RESOURCE_HR;

		String title = ResourceBundleUtil.getString(resource, locale, "hr.salary.list.label");
		String staff_Name = ResourceBundleUtil.getString(resource, locale, "hr.salary.name.label");
		String dept_Name = ResourceBundleUtil.getString(resource, locale, "hr.salary.dept.label");
		String account_Name = ResourceBundleUtil.getString(resource, locale, "hr.salary.account.label");
		String mounth = ResourceBundleUtil.getString(resource, locale, "hr.salary.mounth.label");

		int size = properties.size();
		String[] columnNames = new String[size + 4];
		
		columnNames[0] = staff_Name;
		columnNames[1] = dept_Name;
		columnNames[2] = account_Name;
		columnNames[3] = mounth;
		
		Map<Long, String> propertyTypes = SalaryUserDefinedHelper.getPropertyTypes(request, userDefinedManager, properties);
		for (int i = 0; i < size; i++) {
			columnNames[i + 4] = propertyTypes.get(properties.get(i).getId());
		}
		
		record.setSheetName(title);
		record.setTitle(title);
		record.setColumnName(columnNames);
	}
	
	private void initDataRecordForReport(DataRecord record, HttpServletRequest request, Locale locale) {
		String report_label = ResourceBundleUtil.getString(Constants.RESOURCE_ORGANIZATION, locale, "import.report");
		String data_label = ResourceBundleUtil.getString(Constants.RESOURCE_ORGANIZATION, locale, "import.data");
		String result_label = ResourceBundleUtil.getString(Constants.RESOURCE_ORGANIZATION, locale, "import.result");
		String description_label = ResourceBundleUtil.getString(Constants.RESOURCE_ORGANIZATION, locale, "import.description");

		String[] columnNames = new String[3];
		columnNames[0] = data_label;
		columnNames[1] = result_label;
		columnNames[2] = description_label;
		
		short[] columnWith = { 50, 20, 100 };

		record.setSheetName(report_label);
		record.setTitle(report_label);
		record.setColumnName(columnNames);
		record.setColumnWith(columnWith);
	}
	
	/**
	 * 工资信息转换页面信息
	 */
	private List<WebSalary> getWebSalary(List<Salary> salarys) throws Exception {
		List<WebSalary> results = new ArrayList<WebSalary>();
		for (Salary s : salarys) {
			WebSalary w = new WebSalary();
			w.setSalary(s);
			V3xOrgMember member = orgManager.getMemberById(s.getStaffId());
			if (member != null) {
				w.setOrgDepartmentId(member.getOrgDepartmentId());
			}
			w.setYearMonth(s.getYear() + "-" + s.getMonth());
			results.add(w);
		}
		return results;
	}
	
	/**
	 * 转换成姓名
	 */
	private List<String> toNameList(String staffNames) throws Exception {
		List<String> namesList = new ArrayList<String>();
		if (Strings.isNotBlank(staffNames)) {
			String[] names = staffNames.split("、");
			for (String name : names) {
				namesList.add(name);
			}
		}
		return namesList;
	}
	
}