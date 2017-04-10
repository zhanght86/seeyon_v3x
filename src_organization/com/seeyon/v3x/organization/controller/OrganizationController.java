/**
 * OrganizationController1.java
 * Created on 2007-3-6
 */
package com.seeyon.v3x.organization.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.OperHelper;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;

import com.seeyon.v3x.agent.manager.AgentIntercalateManager;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.blog.manager.BlogManager;
import com.seeyon.v3x.bulletin.manager.BaseBulletinManager;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.bulletin.util.Constants;
import com.seeyon.v3x.collaboration.templete.manager.TempleteConfigManager;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.Constants.LoginOfflineOperation;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.ldap.config.LDAPConfig;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.online.OnlineRecorder;
import com.seeyon.v3x.common.online.OnlineUser;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.util.ListSearchHelper;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.doc.manager.DocLibManager;
import com.seeyon.v3x.edoc.manager.EdocDocTemplateManager;
import com.seeyon.v3x.edoc.manager.EdocHelper;
import com.seeyon.v3x.edoc.manager.EdocRoleHelper;
import com.seeyon.v3x.event.EventDispatcher;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.flowperm.util.FlowPermHelper;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.hbcb.domain.FileDownload;
import com.seeyon.v3x.hbcb.manager.FileDownloadManager;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexInitConfig;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.main.AccountSymbol;
import com.seeyon.v3x.main.MainDataLoader;
import com.seeyon.v3x.main.phrase.CommonPhraseManager;
import com.seeyon.v3x.menu.domain.Security;
import com.seeyon.v3x.menu.manager.MenuManager;
import com.seeyon.v3x.news.manager.NewsTypeManager;
import com.seeyon.v3x.organization.Constant;
import com.seeyon.v3x.organization.OrganizationHelper;
import com.seeyon.v3x.organization.dao.OrgHelper;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.CallbackAddInitialData;
import com.seeyon.v3x.organization.domain.CompareSortEntity;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgDutyLevel;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgRelationship;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.event.MoveDepartmentEvent;
import com.seeyon.v3x.organization.event.OrganizationEventComposite;
import com.seeyon.v3x.organization.event.OrganizationEventListener;
import com.seeyon.v3x.organization.event.UpdateBanchMemberEvent;
import com.seeyon.v3x.organization.event.UpdateTeamEvent;
import com.seeyon.v3x.organization.inexportutil.DataManager;
import com.seeyon.v3x.organization.inexportutil.DataUtil;
import com.seeyon.v3x.organization.inexportutil.manager.IOManager;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.manager.OrganizationMessage;
import com.seeyon.v3x.organization.principal.PrincipalManager;
import com.seeyon.v3x.organization.services.OrganizationServices;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgAccount;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgDepartment;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgLevel;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgMember;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgModel;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgPost;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgResult;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgTeam;
import com.seeyon.v3x.plugin.ldap.domain.V3xLdapRdn;
import com.seeyon.v3x.plugin.ldap.manager.OrganizationLdapEvent;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.resource.manager.ResourceManager;
import com.seeyon.v3x.secret.domain.SecretAudit;
import com.seeyon.v3x.secret.manager.SecretAuditManager;
import com.seeyon.v3x.space.Constants.SpaceState;
import com.seeyon.v3x.space.Constants.SpaceTypeClass;
import com.seeyon.v3x.space.domain.SpaceModel;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.system.signet.manager.SignetManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.annotation.SetContentType;
import com.seeyon.v3x.webmail.util.DateUtil;
import com.seeyon.v3x.worktimeset.domain.WorkTimeCurrency;
import com.seeyon.v3x.worktimeset.manager.WorkTimeSetManager;

public class OrganizationController extends BaseController {
	private static final Log log = LogFactory
			.getLog(OrganizationController.class);

	public static final String Parameter_Name_ViewPage = "ViewPage";

	private OrgManagerDirect orgManagerDirect;

	private OrgManager orgManager;

	private MetadataManager metadataManager;

	@SuppressWarnings("deprecation")
	private SearchManager searchManager;

	private FileToExcelManager fileToExcelManager;

	private InquiryManager inquiryManager;

	private BbsBoardManager bbsBoardManager;

	private SpaceManager spaceManager;

	private BulDataManager bulDataManager;

	private BulTypeManager bulTypeManager;

	private DataManager dataManagerImpl;

	private SignetManager signetManager;

	private WorkTimeSetManager workTimeSetManager;

	// 2017-4-5 诚佰公司 添加
	private FileDownloadManager fileDownloadManager;

	// 添加公文套红模板
	private EdocDocTemplateManager edocDocTemplateManager;

	// 添加文档库信息

	private DocLibManager docLibManager;

	private BlogManager blogManager;
	private FileManager fileManager;

	private OperationlogManager operationlogManager;

	private MenuManager menuManager;

	private ResourceManager resourceManager;

	private CommonPhraseManager phraseManager;

	private IOperBase iOperBase = (IOperBase) SeeyonForm_Runtime.getInstance()
			.getBean("iOperBase");

	private ProjectManager projectManager;

	private IOManager ioManager;

	private NewsTypeManager newsTypeManager;

	private ConfigManager configManager;

	private TempleteConfigManager templeteConfigManager;

	private OrganizationEventListener eventListener = OrganizationEventComposite
			.getInstance();

	private AppLogManager appLogManager;

	private StaffInfoManager staffInfoManager;

	private OrganizationServices organizationServices;

	private OrganizationLdapEvent event = (OrganizationLdapEvent) ApplicationContextHolder
			.getBean("organizationLdapEvent");

	private AgentIntercalateManager agentIntercalateManager;
	private PrincipalManager principalManager;
	private IndexManager indexManager;
	private UpdateIndexManager updateIndexManager;

	private SystemConfig systemConfig;

	private SecretAuditManager secretAuditManager;

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	public UpdateIndexManager getUpdateIndexManager() {
		return updateIndexManager;
	}

	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}

	public PrincipalManager getPrincipalManager() {
		return principalManager;
	}

	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}

	public void setAgentIntercalateManager(
			AgentIntercalateManager agentIntercalateManager) {
		this.agentIntercalateManager = agentIntercalateManager;
	}

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	public void setIOperBase(IOperBase operBase) {
		iOperBase = operBase;
	}

	public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public DataManager getDataManagerImpl() {
		return dataManagerImpl;
	}

	public void setDataManagerImpl(DataManager dataManagerImpl) {
		this.dataManagerImpl = dataManagerImpl;
	}

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public MetadataManager getMetadataManager() {
		return metadataManager;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	@SuppressWarnings("deprecation")
	public SearchManager getSearchManager() {
		return searchManager;
	}

	@SuppressWarnings("deprecation")
	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}

	public FileToExcelManager getFileToExcelManager() {
		return fileToExcelManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}

	public SpaceManager getSpaceManager() {
		return spaceManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	public void setDocLibManager(DocLibManager docLibManager) {
		this.docLibManager = docLibManager;
	}

	public void setBlogManager(BlogManager blogManager) {
		this.blogManager = blogManager;
	}

	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public void setTempleteConfigManager(
			TempleteConfigManager templeteConfigManager) {
		this.templeteConfigManager = templeteConfigManager;
	}

	public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
		this.staffInfoManager = staffInfoManager;
	}

	public IndexManager getIndexManager() {
		return indexManager;
	}

	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public WorkTimeSetManager getWorkTimeSetManager() {
		return workTimeSetManager;
	}

	public void setWorkTimeSetManager(WorkTimeSetManager workTimeSetManager) {
		this.workTimeSetManager = workTimeSetManager;
	}

	public FileDownloadManager getFileDownloadManager() {
		return fileDownloadManager;
	}

	public void setFileDownloadManager(FileDownloadManager fileDownloadManager) {
		this.fileDownloadManager = fileDownloadManager;
	}

	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

	/**
	 * 单位树形结构 - 初始页
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView accountTreeIndex(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return new ModelAndView("organization/account/tree/index");
	}

	/**
	 * 单位树形结构 - menubar
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView showTopMenu(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/account/tree/topMenu");
		modelAndView.addObject("groupAccountId", orgManagerDirect
				.getRootAccount().getId());
		return modelAndView;
	}

	/**
	 * 单位树形结构 - main
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView showRightMain(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/account/tree/rightMain");
		String parentId = request.getParameter("parentId");
		if (Strings.isNotBlank(parentId)) {
			modelAndView.addObject("parentId", parentId);
		}
		String accountId = request.getParameter("accountId");
		if (Strings.isNotBlank(accountId)) {
			modelAndView.addObject("accountId", accountId);
		}
		if ("true".equals(request.getParameter("isRefreshTop"))) {
			V3xOrgAccount groupAccount = orgManagerDirect.getRootAccount();
			AccountSymbol accountSymbol = MainDataLoader.getInstance()
					.getAccountSymbol(groupAccount.getId());
			if (accountSymbol != null && !accountSymbol.isHiddenGroupName()) {
				modelAndView.addObject("isRefreshGroupName", true);
				modelAndView.addObject("groupAccount", groupAccount);
			}
		}
		return modelAndView;
	}

	/**
	 * 单位树形结构 - account Tree
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView showLeftTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/account/tree/leftTree");
		String parentId = request.getParameter("parentId");
		if (Strings.isNotBlank(parentId)) {
			modelAndView.addObject("parentId", parentId);
		}
		String accountId = request.getParameter("accountId");
		if (Strings.isNotBlank(accountId)) {
			modelAndView.addObject("accountId", accountId);
		}
		// 从数据库取出所有单位，包括停用单位，此处不能用getAllAccounts()，因为有可能丢掉停用的单位
		List<V3xOrgEntity> accountlist = orgManagerDirect.getEntityList(
				V3xOrgAccount.class.getSimpleName(), "isDeleted", false,
				V3xOrgEntity.VIRTUAL_ACCOUNT_ID, false);
		// accountlist.add(orgManagerDirect.getRootAccount());
		List<WebV3xOrgAccount> resultlist = new ArrayList<WebV3xOrgAccount>();
		Long groupAccountId = null;
		for (V3xOrgEntity accountEnt : accountlist) {
			V3xOrgAccount account = (V3xOrgAccount) accountEnt;
			if (!account.isValid()) {
				continue;
			}
			if (account.getIsRoot()) {
				groupAccountId = account.getId();
			}
			WebV3xOrgAccount webaccount = new WebV3xOrgAccount();
			webaccount.setV3xOrgAccount(account);
			Long superId = account.getSuperior();
			if (null != superId && superId != 0) {
				V3xOrgAccount superaccount = orgManagerDirect
						.getAccountById(superId);
				if (null != superaccount) {
					webaccount.setSuperiorName(superaccount.getShortname());
				}
			}
			resultlist.add(webaccount);
		}
		modelAndView.addObject("accountlist", resultlist);
		modelAndView.addObject("groupAccountId", groupAccountId);
		return modelAndView;
	}

	/**
	 * 单位树形结构 - 新建单位
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView createAccountOfTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"organization/account/tree/editAccount");
		// 获得当前排序号的最大值
		Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgAccount.class
				.getSimpleName(), CurrentUser.get().getLoginAccount());
		V3xOrgAccount account = new V3xOrgAccount();
		account.setSortId(maxSortNum + 1);
		account.setEnabled(true);
		String parentId = request.getParameter("parentId");
		if (Strings.isNotBlank(parentId)) {
			account.setSuperior(Long.parseLong(parentId));
			result.addObject("parentId", parentId);
		}
		String accountId = request.getParameter("accountId");
		if (Strings.isNotBlank(accountId)) {
			result.addObject("accountId", accountId);
		}
		WebV3xOrgAccount webaccount = new WebV3xOrgAccount();
		webaccount.setV3xOrgAccount(account);
		result.addObject("account", webaccount);
		V3xOrgAccount rootAccount = orgManagerDirect.getRootAccount();
		result.addObject("rootAccountId", rootAccount.getId());
		// 获取所有的单位,以便初始化下来列表
		List<V3xOrgAccount> accountlist = orgManagerDirect.getAllAccounts();
		result.addObject("accountlist", accountlist);
		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		result.addObject("orgMeta", orgMeta);
		result.addObject("operation", "create");
		// 判断是否启用多组织模型
		boolean systemMsgOrgEnable = false;
		String enableMsgOrgConfig = this.systemConfig
				.get(IConfigPublicKey.MUCHORG_ENABLE);
		if (enableMsgOrgConfig != null) {
			systemMsgOrgEnable = "enable".equals(enableMsgOrgConfig);
		}
		if (LDAPConfig.getInstance().getIsEnableLdap()
				&& SystemEnvironment
						.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
								.name())) {
			result.addObject("hasLDAPAD", true);
		}
		result.addObject("systemMsgOrgEnable", systemMsgOrgEnable);

		return result;
	}

	/**
	 * 单位树形结构 - 查看/编辑单位信息
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView editAccountOfTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/account/tree/editAccount");
		String accountIdStr = request.getParameter("id");
		if (Strings.isNotBlank(accountIdStr)) {
			V3xOrgAccount account = orgManagerDirect.getAccountById(Long
					.parseLong(accountIdStr));
			orgManagerDirect.loadEntityProperty(account);
			V3xOrgMember member = orgManagerDirect.getMemberByLoginName(account
					.getAdminName());
			WebV3xOrgAccount webaccount = new WebV3xOrgAccount();
			webaccount.setV3xOrgAccount(account);

			V3xOrgAccount rootAccount = orgManagerDirect.getRootAccount();
			modelAndView.addObject("rootAccountId", rootAccount.getId());
			// 获取所有的单位,以便初始化下来列表
			List<V3xOrgAccount> accountlist = orgManagerDirect.getAllAccounts();
			// 过滤掉自己和自己的子节点
			accountlist.removeAll(orgManagerDirect.getChildAccount(
					account.getId(), accountlist));
			accountlist.remove(account);
			modelAndView.addObject("accountlist", accountlist);

			// 获得单位类别下拉列表中的数据
			Map<String, Metadata> orgMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.organization);
			modelAndView.addObject("orgMeta", orgMeta);
			if (null != member) {
				webaccount.setAdminPass(member.getPassword());
			}
			// 获取扩展属性
			webaccount.setAccountCategory(account
					.getProperty("accountCategory"));
			webaccount.setAccountLevel(account.getProperty("accountLevel"));
			webaccount.setAccountNature(account.getProperty("accountNature"));
			webaccount.setChiefLeader(account.getProperty("chiefLeader"));
			webaccount.setAddress(account.getProperty("address"));
			webaccount.setZipCode(account.getProperty("zipCode"));
			webaccount.setTelephone(account.getProperty("telephone"));
			webaccount.setFax(account.getProperty("fax"));
			webaccount.setIpAddress(account.getProperty("ipAddress"));
			webaccount.setAccountMail(account.getProperty("accountMail"));
			boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag(); // 政务版标识
			if (isGovVersion) {
				webaccount.setAdminiLevel(account.getProperty("adminiLevel")); // 政务版新增扩展属性
																				// 行政级别
			}

			modelAndView.addObject("account", webaccount);

			if ("true".equals(request.getParameter("readOnly"))) {
				modelAndView.addObject("operation", "view");
			} else {
				modelAndView.addObject("operation", "modify");
			}
			// 判断是否启用多组织模型
			boolean systemMsgOrgEnable = false;
			String enableMsgOrgConfig = this.systemConfig
					.get(IConfigPublicKey.MUCHORG_ENABLE);
			if (enableMsgOrgConfig != null) {
				systemMsgOrgEnable = "enable".equals(enableMsgOrgConfig);
			}
			modelAndView.addObject("systemMsgOrgEnable", systemMsgOrgEnable);

			if (LDAPConfig.getInstance().getIsEnableLdap()
					&& SystemEnvironment
							.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
									.name())) {
				try {
					V3xLdapRdn value = event.findLdapSet(Long
							.parseLong(accountIdStr));
					modelAndView.addObject("ldapValue", value);
					modelAndView.addObject("hasLDAPAD", true);
				} catch (Exception e) {
					log.error("ldap/ad 取得ldap配置出错！", e);
					throw new Exception("ldap/ad 取得ldap配置出错！", e);
				}
			}
		}
		return modelAndView;
	}

	/**
	 * 单位树形结构 - 更新单位
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView updateAccountOfTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String operation = request.getParameter("operation");
		String ldapOu = request.getParameter("ldapOu");
		User user = CurrentUser.get();
		// 新建单位的提交
		if ("create".equals(operation)) {
			V3xOrgAccount account = new V3xOrgAccount();
			bind(request, account);
			if (Strings.isBlank(request.getParameter("sortId"))) {
				account.setSortId(1);
			}
			// 单位排序号的重复处理
			String isInsert = request.getParameter("isInsert");
			if (isInsert.equals("1")
					&& orgManagerDirect.isPropertyDuplicated(
							V3xOrgAccount.class.getSimpleName(), "sortId",
							account.getSortId())) {
				orgManagerDirect.insertRepeatSortNum(
						V3xOrgAccount.class.getSimpleName(),
						V3xOrgEntity.VIRTUAL_ACCOUNT_ID, account.getSortId());
			}
			// 添加单位文档
			// docLibManager.addSysDocLibs(account.getId());
			// 增加扩展属性
			account.setProperty("accountCategory",
					request.getParameter("accountCategory"));
			account.setProperty("accountLevel",
					request.getParameter("accountLevel"));
			account.setProperty("accountNature",
					request.getParameter("accountNature"));
			account.setProperty("chiefLeader",
					request.getParameter("chiefLeader"));
			account.setProperty("address", request.getParameter("address"));
			account.setProperty("zipCode", request.getParameter("zipCode"));
			account.setProperty("telephone", request.getParameter("telephone"));
			account.setProperty("fax", request.getParameter("fax"));
			account.setProperty("ipAddress", request.getParameter("ipAddress"));
			account.setProperty("accountMail",
					request.getParameter("accountMail"));
			// 政务版：新增单位扩展字段：行政级别 adminiLevel
			boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag();
			if (isGovVersion) {
				account.setProperty("adminiLevel",
						request.getParameter("adminiLevel"));
			}
			orgManager.addAccount(account);

			// 新建单位时设置单位的考勤工作时间
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			String year = String.valueOf(c.get(Calendar.YEAR));
			String month = String.valueOf(c.get(Calendar.MONTH) + 1);
			WorkTimeCurrency workTimeCurrency = workTimeSetManager
					.findComnWorkTimeSet(year, month, account.getId(), false);
			String[] beginTime = workTimeCurrency.getAmWorkTimeBeginTime()
					.split(":");
			String[] endTime = workTimeCurrency.getPmWorkTimeEndTime().split(
					":");
			workTimeSetManager
					.saveWorkTime(beginTime, endTime, account.getId());

			String aCategory = request.getParameter("accountCategory");
			if (!Strings.isBlank(aCategory)) {
				long iad = Long.valueOf(aCategory).longValue();
				Metadata metadata = metadataManager
						.getMetadata(MetadataNameEnum.org_property_account_category);
				if (null != metadata) {
					MetadataItem item = metadataManager.getMetadataItem(
							metadata.getName(), Long.valueOf(iad).toString());
					if (null != item) {
						metadataManager.refMetadataItem(metadata.getId(),
								item.getId(), Long.valueOf(iad).intValue());
					}
				}
			}

			// 加管理员
			/*
			 * V3xOrgMember member = new V3xOrgMember(); String adminNameValue =
			 * ResourceBundleUtil.getString(
			 * "com.seeyon.v3x.organization.resources.i18n.OrganizationResources"
			 * ,"org.account_form.adminName.value", ""); String adminName =
			 * request.getParameter("adminName");
			 * member.setLoginName(adminName); String adminPass =
			 * request.getParameter("adminPass"); member.setPassword(adminPass);
			 * member.setName(adminNameValue); member.setIsAdmin(true);
			 * member.setOrgAccountId(account.getId());
			 * member.setOrgDepartmentId(account.getId());
			 * orgManagerDirect.addMember(member);
			 */

			eventListener.addAccount(account);
			// 记录应用日志
			appLogManager.insertLog(user, AppLogAction.Organization_NewAccount,
					user.getName(), account.getName());
			// 是否复制集团职务级别
			String isCopy = request.getParameter("isCopy");
			if (isCopy != null && isCopy.equals("on")) {
				// 复制一套集团职务级别
				orgManagerDirect.copyGroupLevelToAccount(account.getId());
			}
			// 政务版--预置一套职级
			if (isGovVersion) {
				orgManagerDirect.generateDutyLevelToAccount(account.getId());
			}
			// 初始化单位数据
			// this.generateInitData(account.getId());
			if (request.getParameter("cont") != null) {
				return redirectModelAndView("/organization.do?method=createAccountOfTree&cont=true&reloadTree=true&parentId="
						+ account.getSuperior()
						+ "&accountCategory="
						+ account.getProperty("accountCategory")
						+ "&chiefLeader="
						+ account.getProperty("chiefLeader")
						+ "&address="
						+ account.getProperty("address")
						+ "&zipCode="
						+ account.getProperty("zipCode")
						+ "&telephone="
						+ account.getProperty("telephone")
						+ "&fax="
						+ account.getProperty("fax")
						+ "&ipAddress="
						+ account.getProperty("ipAddress")
						+ "&accountMail="
						+ account.getProperty("accountMail")
						+ "&adminiLevel="
						+ account.getProperty("adminiLevel") // 政务版新增行政级别属性
						+ "&accountId=" + account.getId());
			} else {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('"
						+ Constant.getString4CurrentUser("organization.ok")
						+ "');");
				out.println("</script>");
				return redirectModelAndView("/organization.do?method=showRightMain&reloadTree=true&parentId="
						+ account.getSuperior()
						+ "&accountId="
						+ account.getId());
			}
		}
		// 修改单位的提交
		else {
			PrintWriter out = response.getWriter();
			boolean isGroupAccount = "true".equals(request
					.getParameter("isGroupAccount"));
			try {
				V3xOrgAccount model = new V3xOrgAccount();
				bind(request, model);
				V3xOrgAccount account = null;
				long id = model.getId();
				if (isGroupAccount) {
					account = orgManagerDirect.getRootAccount();
				} else {
					account = orgManagerDirect.getAccountById(id);
				}
				// 2011-12-07lilong由于V3xOrgAccount不支持克隆，为增加监听添加此变量，后updateAccount(old,
				// new)
				V3xOrgAccount oldAccount = new V3xOrgAccount();
				BeanUtils.copyProperties(oldAccount, account);
				// 已删除单位的校验
				if (account == null
						|| (account.getIsDeleted() != null && account
								.getIsDeleted().booleanValue())) {
					out.println("<script>");
					out.println("alert('"
							+ Constant
									.getString4CurrentUser("organization.update.no.account")
							+ "');");
					out.println("</script>");
					out.flush();
					return redirectModelAndView("/organization.do?method=showRightMain&reloadTree=true&isRefreshTop=true");
				}

				boolean orgEnabled = account.getEnabled();
				// 停用单位的校验
				if (orgEnabled && !model.getEnabled()) {
					// 单位下存在未删除的组织模型时不允许停用，但允许存在停用的组织模型
					if (!isAccountCanBeDisenable(id)) {
						account.setEnabled(orgEnabled);
						out.println("<script>");
						out.println("alert('"
								+ Constant
										.getString4CurrentUser("orgainzation.unable.account")
								+ "');");
						out.println("</script>");
						out.flush();
						return redirectModelAndView("/organization.do?method=showRightMain&reloadTree=false");
					}
				}

				if (isGroupAccount) {
					Integer orgSortId = account.getSortId();
					// 单位排序号的重复处理
					Integer sortId = Integer.valueOf(request
							.getParameter("sortId"));
					String isInsert = request.getParameter("isInsert");
					if (!orgSortId.equals(sortId)
							&& isInsert.equals("1")
							&& orgManagerDirect.isPropertyDuplicated(
									V3xOrgAccount.class.getSimpleName(),
									"sortId", sortId, account.getId())) {
						orgManagerDirect.insertRepeatSortNum(
								V3xOrgAccount.class.getSimpleName(),
								CurrentUser.get().getLoginAccount(), sortId);
					}
				}
				String oldLoginName = account.getAdminName();
				// 拷贝属性之前先将原单位的工作范围保存
				model.setLevelScope(account.getLevelScope());
				account.copyProperties(model);

				if (isGroupAccount) {
					account.setIsRoot(true);
					account.setSortId(1);
					account.setAdminName(oldLoginName);
				} else {
					account.setIsRoot(account.getIsRoot());
				}
				// 扩展属性
				account.setProperty("accountCategory",
						request.getParameter("accountCategory"));
				account.setProperty("accountLevel",
						request.getParameter("accountLevel"));
				account.setProperty("accountNature",
						request.getParameter("accountNature"));
				account.setProperty("chiefLeader",
						request.getParameter("chiefLeader"));
				account.setProperty("address", request.getParameter("address"));
				account.setProperty("zipCode", request.getParameter("zipCode"));
				account.setProperty("telephone",
						request.getParameter("telephone"));
				account.setProperty("fax", request.getParameter("fax"));
				account.setProperty("ipAddress",
						request.getParameter("ipAddress"));
				account.setProperty("accountMail",
						request.getParameter("accountMail"));
				// 政务版：新增单位扩展字段：行政级别 adminiLevel
				boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag();
				if (isGovVersion) {
					account.setProperty("adminiLevel",
							request.getParameter("adminiLevel"));
				}
				// 更新管理员
				V3xOrgMember member = orgManagerDirect
						.getMemberByLoginName(oldLoginName);
				String adminName = request.getParameter("adminName");
				String adminPass = request.getParameter("adminPass");
				/*
				 * if(adminName!=null&&!StringUtils.isEmpty(adminName)){
				 * account.setAdminName(adminName);
				 * member.setLoginName(adminName); }else{
				 * account.setAdminName(oldLoginName); }
				 */
				orgManager.updateAccount(account);

				String aCategory = request.getParameter("accountCategory");
				if (!Strings.isBlank(aCategory)) {
					long iad = Long.valueOf(aCategory).longValue();
					Metadata metadata = metadataManager
							.getMetadata(MetadataNameEnum.org_property_account_category);
					if (null != metadata) {
						MetadataItem item = metadataManager.getMetadataItem(
								metadata.getName(), Long.valueOf(iad)
										.toString());
						if (null != item) {
							metadataManager.refMetadataItem(metadata.getId(),
									item.getId(), Long.valueOf(iad).intValue());
						}
					}
				}

				// 判断当没有更改密码的时候就不去更改密码
				/*
				 * if (!Strings.isEmpty(adminPass)){
				 * member.setPassword(adminPass); } if (account.getEnabled()){
				 * member.setEnabled(true); }else if(!account.getEnabled()){
				 * member.setEnabled(false); }
				 * orgManagerDirect.updateEntity(member);
				 */
				if (!oldLoginName.equalsIgnoreCase(adminName)
						&& !isGroupAccount) {
					// 管理员登录名改变，强制下线
					OnlineRecorder.moveToOffline(oldLoginName,
							LoginOfflineOperation.adminKickoff);
				}
				// 记录密码修改日志
				if (!V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD.equals(adminPass)
						&& !Strings.isEmpty(adminPass)) {
					appLogManager.insertLog(user,
							AppLogAction.Systemmanager_UpdateAdminPassWord,
							user.getName(), member.getName());
				}

				boolean isReloadTree = false;
				String oldName = request.getParameter("oldName");
				String oldSuperiorId = request.getParameter("oldSuperiorId");
				if (!account.getName().equals(oldName)
						|| !account.getSuperior().toString()
								.equals(oldSuperiorId)) {
					isReloadTree = true;
				}

				// 更改集团名称时需要更新TOP
				boolean isRefreshTop = false;
				if (isGroupAccount) {
					if (!account.getName().equals(oldName)
							|| !account.getSecondName().equals(
									request.getParameter("oldSecondName"))) {
						isRefreshTop = true;
					}
				}
				eventListener.updateAccount(oldAccount, account);
				// GKE更新单位
				eventListener.updateAccount(account);

				// 记录应用日志
				appLogManager.insertLog(user,
						AppLogAction.Organization_UpdateAccount,
						user.getName(), account.getName());

				ldap4AccountOU(account, ldapOu, id);
				out.println("<script>");
				out.println("alert('"
						+ Constant.getString4CurrentUser("organization.ok")
						+ "');");
				out.println("</script>");
				out.flush();
				return redirectModelAndView("/organization.do?method=showRightMain&reloadTree="
						+ isReloadTree + "&isRefreshTop=" + isRefreshTop);

			} catch (BusinessException e) {
				out.println("<script>");
				out.println("alert('" + e.getMessage() + "!');");
				out.println("</script>");
				out.flush();
				return redirectModelAndView(
						"/organization.do?method=accountTreeIndex", "parent");
			}
		}
	}

	private void ldap4AccountOU(V3xOrgAccount account, String ldapOu, long id)
			throws BusinessException {
		if (LDAPConfig.getInstance().getIsEnableLdap()
				&& SystemEnvironment
						.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
								.name())) {
			try {
				User user = CurrentUser.get();
				V3xLdapRdn value = null;
				value = event.findLdapSet(id);
				if (value == null) {
					value = new V3xLdapRdn();

					value.setRootAccountRdn(ldapOu);
					value.setOrgAccountId(id);
					appLogManager.insertLog(user, AppLogAction.LDAP_OU_Create,
							account.getName(), ldapOu);
				} else {
					value.setRootAccountRdn(ldapOu);
					appLogManager.insertLog(user, AppLogAction.LDAP_OU_Update,
							account.getName(), ldapOu);
				}
				event.saveOrUpdateLdapSet(value);
			} catch (Exception e) {
				log.error("ldap_ad 更新和添加单位ldap配置！", e);
				throw new BusinessException("ldap_ad 更新和添加单位ldap配置！", e);
			}
		}
	}

	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView destroyAccountOfList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String strIds = request.getParameter("sIds");
		String[] ids = new String[] {};
		PrintWriter out = response.getWriter();
		User user = CurrentUser.get();
		// 记录日志
		List<String[]> applogs = new ArrayList<String[]>();

		if (strIds != null && !strIds.equals("")) {
			ids = strIds.split(",");
			// 记录不能被删除的单位名称
			String cantBeDeletedAccounts = "";
			for (int i = 0; i < ids.length; i++) {
				Long accountId = Long.parseLong(ids[i]);
				V3xOrgAccount account = orgManagerDirect
						.getAccountById(accountId);
				if (account != null) {
					if (!isAccountCanBeDeleted(accountId)) {
						if (StringUtils.isBlank(cantBeDeletedAccounts)) {
							cantBeDeletedAccounts += account.getName();
						} else {
							cantBeDeletedAccounts += ("," + account.getName());
						}
					} else {
						orgManager.deleteAccount(account);
						eventListener.deleteAccount(account);
						String[] applog = new String[2];
						applog[0] = user.getName();
						applog[1] = account.getName();
						applogs.add(applog);
					}
				}
			}

			if (!StringUtils.isBlank(cantBeDeletedAccounts)) {
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.organization_accout_delete_no', '"
						+ cantBeDeletedAccounts + "'));");
				out.println("</script>");
				out.flush();
			} else {
				// 记录日志
				appLogManager.insertLogs(user,
						AppLogAction.Organization_DeleteAccount, applogs);
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.option_organization_ok'));");
				out.println("</script>");
				out.flush();
			}
		}

		return super.refreshWorkspace();
	}

	/**
	 * 单位树形结构 - 删除单位
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView destroyAccountOfTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String accountIdStr = request.getParameter("accountId");
		PrintWriter out = response.getWriter();
		if (Strings.isNotBlank(accountIdStr)) {
			Long accountId = Long.parseLong(accountIdStr);
			V3xOrgAccount account = orgManagerDirect.getAccountById(accountId);
			// 单位下存在未删除的组织模型时不允许删除
			if (!isAccountCanBeDeleted(accountId)) {
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_alert_delAccount_no'));");
				out.println("</script>");
				out.flush();
				return super.refreshWorkspace();
			}
			orgManager.deleteAccount(account);
			eventListener.deleteAccount(account);
			// 记录应用日志
			User user = CurrentUser.get();
			appLogManager.insertLog(user,
					AppLogAction.Organization_DeleteAccount, user.getName(),
					account.getName());

			if (LDAPConfig.getInstance().getIsEnableLdap()
					&& SystemEnvironment
							.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
									.name())) {
				try {
					event.deleteLdapSet(accountId);
				} catch (Exception e) {
					log.error("ldap_ad 删除单位时相应删除单位根目录设置！", e);
				}
			}
		}
		out.println("<script>");
		out.println("alert(parent.v3x.getMessage('organizationLang.option_organization_ok'));");
		out.println("</script>");
		out.flush();
		return redirectModelAndView("/organization.do?method=showRightMain&reloadTree=true");
	}

	/**
	 * 进入组织模型的单位管理主界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView listAccount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"organization/account/listAccount");
		String accountName = request.getParameter("accountName");
		String shortName = request.getParameter("shortName");
		String code = request.getParameter("codeName");

		List sublist = new ArrayList();
		if ("search".equals(request.getParameter("search"))) {
			StringBuffer strbuf = new StringBuffer();
			Map<String, Object> param = new HashMap<String, Object>();
			strbuf.append("select a from " + V3xOrgAccount.class.getName()
					+ " a where a.isDeleted='0' ");
			if (accountName != null && !accountName.equals("")) {
				strbuf.append(" and a.name like:name");
				param.put("name", "%" + accountName + "%");
			} else if (shortName != null && !shortName.equals("")) {
				strbuf.append(" and a.shortname like:shortname");
				param.put("shortname", "%" + shortName + "%");
			} else if (code != null && !code.equals("")) {
				strbuf.append(" and a.code like:code");
				param.put("code", "%" + code + "%");
			}
			strbuf.append(" order by a.sortId asc");
			sublist = searchManager.searchByHql(strbuf.toString(), param);
		} else {
			sublist = orgManagerDirect.getEntityList(
					V3xOrgAccount.class.getSimpleName(), "isDeleted", false,
					V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		}

		List<WebV3xOrgAccount> resultlist = new ArrayList<WebV3xOrgAccount>();
		for (Object account : sublist) {
			V3xOrgAccount accountEnt = (V3xOrgAccount) account;
			WebV3xOrgAccount webaccount = new WebV3xOrgAccount();
			webaccount.setV3xOrgAccount(accountEnt);
			Long superId = accountEnt.getSuperior();
			if (null != superId && superId != 0) {
				V3xOrgAccount superaccount = orgManagerDirect
						.getAccountById(superId);
				if (null != superaccount)
					webaccount.setSuperiorName(superaccount.getShortname());
			}
			resultlist.add(webaccount);
		}
		result.addObject("accountlist", resultlist);
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		result.addObject("orgMeta", orgMeta);
		return result;
	}

	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView exportAccount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String userName = user.getLoginName();
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";

		String company_list = ResourceBundleUtil.getString(resource, local,
				"org.account_form.list");
		/** 此处暂时不做分页处理　10.16 **/
		List<V3xOrgAccount> accountlist = orgManagerDirect.getAllAccounts();
		List<WebV3xOrgAccount> resultlist = new ArrayList<WebV3xOrgAccount>();
		for (V3xOrgAccount account : accountlist) {
			WebV3xOrgAccount webaccount = new WebV3xOrgAccount();
			// 得到单位扩展属性并设置
			orgManagerDirect.loadEntityProperty(account);
			// orgManagerDirect.setDefaultAccount(account.getId());
			webaccount.setV3xOrgAccount(account);
			Long superId = account.getSuperior();
			if (null != superId && superId != 0) {
				V3xOrgAccount superaccount = orgManagerDirect
						.getAccountById(superId);
				if (null != superaccount)
					webaccount.setSuperiorName(superaccount.getName());
			}
			orgManagerDirect.loadEntityProperty(account);
			// 获取扩展属性
			webaccount.setAccountCategory(account
					.getProperty("accountCategory"));
			webaccount.setAccountLevel(account.getProperty("accountLevel"));
			webaccount.setAccountNature(account.getProperty("accountNature"));
			webaccount.setChiefLeader(account.getProperty("chiefLeader"));
			webaccount.setAddress(account.getProperty("address"));
			webaccount.setZipCode(account.getProperty("zipCode"));
			webaccount.setTelephone(account.getProperty("telephone"));
			webaccount.setFax(account.getProperty("fax"));
			webaccount.setIpAddress(account.getProperty("ipAddress"));
			webaccount.setAccountMail(account.getProperty("accountMail"));

			resultlist.add(webaccount);
		}
		DataRecord dataRecord = OrganizationHelper.exportAccount(resultlist,
				request, metadataManager, response, fileToExcelManager);
		OrganizationHelper.exportToExcel(request, response, fileToExcelManager,
				company_list + "-" + userName, dataRecord);
		return null;

	}

	/**
	 * 进入组织模型的单位管理的添加状态
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView addAccount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("organization/account/account");
		// 获得当前排序号的最大值
		Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgAccount.class
				.getSimpleName(), CurrentUser.get().getLoginAccount());
		V3xOrgAccount account = new V3xOrgAccount();
		account.setSortId(maxSortNum + 1);
		account.setEnabled(true);
		WebV3xOrgAccount webaccount = new WebV3xOrgAccount();
		webaccount.setV3xOrgAccount(account);
		result.addObject("account", webaccount);
		// 获取所有的单位,以便初始化下来列表
		List<V3xOrgAccount> accountlist = orgManagerDirect.getAllAccounts();
		result.addObject("accountlist", accountlist);
		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		result.addObject("orgMeta", orgMeta);
		result.addObject("modifyRole", 0);
		// 本参数是为了在前太验证是否走那个验证重名称的验证 ---->> 0 表示进行添加方法的验证
		result.addObject("vaildateName", 0);
		// 进入下次是添加还是修改状态
		result.addObject("accountEntity", "createAccount");
		result.addObject("accountAdminis", 1);
		// 添加判断是否为添加 0 添加 1 修改（关系到源码的问题）
		result.addObject("isValidatePassword", 0);
		// 判断继续添加的复选框是否显示 0 不显示 1 显示
		result.addObject("isShowChecked", 0);
		// 本参数设置当前登陆人员
		result.addObject("isGroupAdmin", "true");
		result.addObject("oper", "add");
		// 判断是否启用多组织模型
		boolean systemMsgOrgEnable = false;
		String enableMsgOrgConfig = this.systemConfig
				.get(IConfigPublicKey.MUCHORG_ENABLE);
		if (enableMsgOrgConfig != null) {
			systemMsgOrgEnable = "enable".equals(enableMsgOrgConfig);
		}
		result.addObject("systemMsgOrgEnable", systemMsgOrgEnable);
		return result;
	}

	/**
	 * 进入组织模型的单位管理的编辑方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView createAccount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		V3xOrgAccount account = new V3xOrgAccount();
		bind(request, account);
		String sortId = request.getParameter("sortId");
		if (null == sortId || sortId.equals("")) {
			account.setSortId(1);
		}
		// 单位排序号的重复处理
		String isInsert = request.getParameter("isInsert");
		if (isInsert.equals("1")
				&& orgManagerDirect.isPropertyDuplicated(
						V3xOrgAccount.class.getSimpleName(), "sortId",
						account.getSortId())) {
			orgManagerDirect.insertRepeatSortNum(V3xOrgAccount.class
					.getSimpleName(), CurrentUser.get().getLoginAccount(),
					account.getSortId());
		}
		// 添加单位文档
		// docLibManager.addSysDocLibs(account.getId());
		// 增加扩展属性
		account.setProperty("accountCategory",
				request.getParameter("accountCategory"));
		account.setProperty("accountLevel",
				request.getParameter("accountLevel"));
		account.setProperty("accountNature",
				request.getParameter("accountNature"));
		account.setProperty("chiefLeader", request.getParameter("chiefLeader"));
		account.setProperty("address", request.getParameter("address"));
		account.setProperty("zipCode", request.getParameter("zipCode"));
		account.setProperty("telephone", request.getParameter("telephone"));
		account.setProperty("fax", request.getParameter("fax"));
		account.setProperty("ipAddress", request.getParameter("ipAddress"));
		account.setProperty("accountMail", request.getParameter("accountMail"));
		boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag();
		if (isGovVersion) {
			account.setProperty("adminiLevel",
					request.getParameter("adminiLevel")); // 政务新增扩展属性 行政级别
		}
		orgManager.addAccount(account);
		eventListener.addAccount(account);
		String aCategory = request.getParameter("accountCategory");
		if (!Strings.isBlank(aCategory)) {
			long iad = Long.valueOf(aCategory).longValue();
			Metadata metadata = metadataManager
					.getMetadata(MetadataNameEnum.org_property_account_category);
			if (null != metadata) {
				MetadataItem item = metadataManager.getMetadataItem(
						metadata.getName(), Long.valueOf(iad).toString());
				if (null != item) {
					metadataManager.refMetadataItem(metadata.getId(),
							item.getId(), Long.valueOf(iad).intValue());
				}
			}
		}

		// 加管理员
		/*
		 * V3xOrgMember member = new V3xOrgMember(); String adminNameValue =
		 * ResourceBundleUtil .getString(
		 * "com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
		 * "org.account_form.adminName.value", "");
		 * 
		 * String adminName = request.getParameter("adminName");
		 * member.setLoginName(adminName); String adminPass =
		 * request.getParameter("adminPass"); member.setPassword(adminPass);
		 * member.setName(adminNameValue); member.setIsAdmin(true);
		 * member.setOrgAccountId(account.getId());
		 * member.setOrgDepartmentId(account.getId());
		 * orgManagerDirect.addMember(member);
		 */
		// 是否复制集团职务级别
		String isCopy = request.getParameter("isCopy");
		if (isCopy != null && isCopy.equals("on")) {
			// 复制一套集团职务级别
			orgManagerDirect.copyGroupLevelToAccount(account.getId());
		}
		// 初始化单位数据
		// this.generateInitData(account.getId());

		boolean continues = request.getParameterValues("cont") != null;
		return redirectModelAndView("/organization.do?method=organizationFrame&from=Account&addOrganiza=addAccount&cont="
				+ continues);

	}

	/**
	 * 进入组织模型中的单位管理的修改状态
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView editAccount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("organization/account/account");
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		result.addObject("isDetail", isDetail);

		// hr 管理-组织信息设置-单位信息-屏蔽单位角色管理标志
		String unitManager = request.getParameter("unitManager");

		// 判断是集团管理登陆还是单位管理员登陆
		boolean isGroupAdmin = false;
		String isGroupAdminStr = request.getParameter("isGroupAdmin");
		if (isGroupAdminStr != null && isGroupAdminStr.equals("true")) {
			isGroupAdmin = true;
		}
		V3xOrgAccount account = null;
		if (isGroupAdmin) {
			account = orgManagerDirect.getAccountById(Long.parseLong(request
					.getParameter("id")));
			// 如果是根单位
			if (account.getIsRoot()) {
				return redirectModelAndView("/organization.do?method=editGroupAccount&isGroupAdmin=true&isDetail="
						+ isDetail);
			}
		} else {
			long accountId = CurrentUser.get().getLoginAccount();
			account = orgManagerDirect.getAccountById(accountId);
			// 如果是修改了单位名称，则需要判断是否更新TOP
			if ("true".equals(request.getParameter("isRefreshTop"))) {
				AccountSymbol accountSymbol = MainDataLoader.getInstance()
						.getAccountSymbol(accountId);
				if (accountSymbol != null) {
					result.addObject("isRefreshAccountName",
							!accountSymbol.isHiddenAccountName());
					if (!accountSymbol.isHiddenGroupName()) {
						result.addObject("groupShortName", orgManagerDirect
								.getRootAccount().getShortname());
					}
				}
			}

		}
		orgManagerDirect.loadEntityProperty(account);
		V3xOrgMember member = orgManagerDirect.getMemberByLoginName(account
				.getAdminName());
		WebV3xOrgAccount webaccount = new WebV3xOrgAccount();
		webaccount.setV3xOrgAccount(account);
		// 获取所有的单位,以便初始化下来列表
		List<V3xOrgAccount> accountlist = orgManagerDirect.getAllAccounts();
		// 过滤掉自己和自己的子节点
		accountlist.removeAll(orgManagerDirect.getChildAccount(account.getId(),
				accountlist));
		accountlist.remove(account);
		result.addObject("accountlist", accountlist);
		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		result.addObject("orgMeta", orgMeta);
		if (null != member)
			webaccount.setAdminPass(member.getPassword());

		// 获取扩展属性
		webaccount.setAccountCategory(account.getProperty("accountCategory"));
		webaccount.setAccountLevel(account.getProperty("accountLevel"));
		webaccount.setAccountNature(account.getProperty("accountNature"));
		webaccount.setChiefLeader(account.getProperty("chiefLeader"));
		webaccount.setAddress(account.getProperty("address"));
		webaccount.setZipCode(account.getProperty("zipCode"));
		webaccount.setTelephone(account.getProperty("telephone"));
		webaccount.setFax(account.getProperty("fax"));
		webaccount.setIpAddress(account.getProperty("ipAddress"));
		webaccount.setAccountMail(account.getProperty("accountMail"));
		boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag();
		if (isGovVersion) {
			webaccount.setAdminiLevel(account.getProperty("adminiLevel")); // 政务新增扩展字段
																			// 行政级别
		}

		result.addObject("account", webaccount);

		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
		}
		// 本参数是为了在前台验证是否走那个验证重名称的验证 ---->> 0 表示进行添加方法的验证 1 表示修改验证
		result.addObject("vaildateName", 1);
		// 本参数是在前台判断提交后走那个操作方法
		result.addObject("accountEntity", "updateAccount");
		// 添加判断是否为添加 0 添加 1 修改（关系到源码的问题）
		result.addObject("isValidatePassword", 1);
		// 本参数说明是否是集团管理员登陆
		result.addObject("isGroupAdmin", isGroupAdmin);

		// hr管理去掉单位角色管理标志
		result.addObject("unitManager", unitManager);

		// 判断是否启用多组织模型
		boolean systemMsgOrgEnable = false;
		String enableMsgOrgConfig = this.systemConfig
				.get(IConfigPublicKey.MUCHORG_ENABLE);
		if (enableMsgOrgConfig != null) {
			systemMsgOrgEnable = "enable".equals(enableMsgOrgConfig);
		}
		result.addObject("systemMsgOrgEnable", systemMsgOrgEnable);
		if (LDAPConfig.getInstance().getIsEnableLdap()
				&& SystemEnvironment
						.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
								.name())) {
			try {
				V3xLdapRdn value = event.findLdapSet(CurrentUser.get()
						.getLoginAccount());
				result.addObject("ldapValue", value);
				result.addObject("hasLDAPAD", true);
			} catch (Exception e) {
				log.error("取得ldap_ad 配置出错！", e);
				throw new Exception("取得ldap_ad 配置出错！", e);
			}
		}
		return result;
	}

	/**
	 * 进入组织模型中的集团单位管理的修改状态
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView editGroupAccount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = null;
		// 判断是系统管理员还是集团管理员登陆,根据版本判断是否显示集团单位页面
		boolean isGroupAdmin = false;
		String isGroupAdminStr = request.getParameter("isGroupAdmin");
		V3xOrgAccount account = null;
		if (isGroupAdminStr != null && isGroupAdminStr.equals("true")) {
			isGroupAdmin = true;
			result = new ModelAndView("organization/account/groupAccount");
			account = orgManagerDirect.getRootAccount();
		} else if (!(Boolean) SysFlag.org_showGroupAccountPage.getFlag()) {
			result = new ModelAndView("organization/account/systemAccount");
			List<V3xOrgAccount> accountList = orgManagerDirect.getAllAccounts();
			for (V3xOrgAccount acc : accountList) {
				if (!acc.getIsRoot()) {
					account = acc;
				}
			}
		} else {
			result = new ModelAndView("organization/account/groupAccount");
			account = orgManagerDirect.getRootAccount();
		}

		orgManagerDirect.loadEntityProperty(account);

		V3xOrgMember member = orgManagerDirect.getMemberByLoginName(account
				.getAdminName());
		WebV3xOrgAccount webaccount = new WebV3xOrgAccount();
		webaccount.setV3xOrgAccount(account);

		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		result.addObject("orgMeta", orgMeta);

		String adminPass = "";
		if (null != member) {
			adminPass = member.getPassword();
			webaccount.setAdminPass(adminPass);
		}

		// 获取扩展属性
		webaccount.setAccountCategory(account.getProperty("accountCategory"));
		webaccount.setAccountLevel(account.getProperty("accountLevel"));
		webaccount.setAccountNature(account.getProperty("accountNature"));
		webaccount.setChiefLeader(account.getProperty("chiefLeader"));
		webaccount.setAddress(account.getProperty("address"));
		webaccount.setZipCode(account.getProperty("zipCode"));
		webaccount.setTelephone(account.getProperty("telephone"));
		webaccount.setFax(account.getProperty("fax"));
		webaccount.setIpAddress(account.getProperty("ipAddress"));
		webaccount.setAccountMail(account.getProperty("accountMail"));
		boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag();
		if (isGovVersion) {
			webaccount.setAdminiLevel(account.getProperty("adminiLevel")); // 政务新增扩展字段
																			// 行政级别
		}

		result.addObject("account", webaccount);
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		result.addObject("isDetail", isDetail);
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
		}
		result.addObject("modifyRole", 1);
		// 本参数是为了在前台验证是否走那个验证重名称的验证 ---->> 0 表示进行添加方法的验证 1 表示修改验证
		result.addObject("vaildateName", 1);
		// 本参数是在前台判断提交后走那个操作方法
		result.addObject("accountEntity", "updateGroupAccount");
		// 添加判断是否为添加 0 添加 1 修改（关系到源码的问题）
		result.addObject("isValidatePassword", 1);
		// 本参数说明是否是集团管理员登陆
		result.addObject("isGroupAdmin", isGroupAdmin);

		// 如果是修改了集团名称，则需要判断是否更新TOP
		if ("true".equals(request.getParameter("isRefreshTop"))) {
			AccountSymbol accountSymbol = MainDataLoader.getInstance()
					.getAccountSymbol(account.getId());
			if (accountSymbol != null) {
				result.addObject("isRefreshAccountName",
						!accountSymbol.isHiddenGroupName());
			}
		}

		return result;
	}

	/**
	 * 组织模型的单位管理的修改 单位管理信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView modfiyManagerWord(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"organization/account/addAccount");
		return result;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView updateAccount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String ldapOu = request.getParameter("ldapOu");
		String unitManager = request.getParameter("unitManager");
		Locale local = LocaleContext.getLocale(request);
		User user = CurrentUser.get();
		// 判断是系统管理员还是集团管理员登陆
		boolean isGroupAdmin = false;
		String isGroupAdminStr = request.getParameter("isGroupAdmin");
		if (isGroupAdminStr != null && isGroupAdminStr.equals("true")) {
			isGroupAdmin = true;
		}
		PrintWriter out = response.getWriter();

		try {
			V3xOrgAccount model = new V3xOrgAccount();
			bind(request, model);
			long id = model.getId();
			V3xOrgAccount account = orgManagerDirect.getAccountById(id);
			V3xOrgAccount oldAccount = new V3xOrgAccount();// 20111207增加新时间监听添加lilong
			BeanUtils.copyProperties(oldAccount, account);
			Integer orgSortId = account.getSortId();
			boolean orgEnabled = account.getEnabled();
			if (isGroupAdmin) {
				// 单位排序号的重复处理
				Integer sortId = Integer
						.valueOf(request.getParameter("sortId"));
				String isInsert = request.getParameter("isInsert");
				if (!orgSortId.equals(sortId)
						&& isInsert.equals("1")
						&& orgManagerDirect.isPropertyDuplicated(
								V3xOrgAccount.class.getSimpleName(), "sortId",
								sortId, account.getId())) {
					orgManagerDirect.insertRepeatSortNum(V3xOrgAccount.class
							.getSimpleName(), CurrentUser.get()
							.getLoginAccount(), sortId);
				}
			}
			Long orgSuper = account.getSuperior();
			Integer orgAccess = account.getAccessPermission();
			boolean orgIsRoot = account.getIsRoot();
			String oldLoginName = account.getAdminName();
			// 拷贝属性之前先将原单位的工作范围保存
			model.setLevelScope(account.getLevelScope());
			account.copyProperties(model);
			account.setIsRoot(orgIsRoot);
			boolean isRefreshTop = false; // 更改单位名称时需要更新TOP
			if (!isGroupAdmin) {
				account.setSortId(orgSortId);
				account.setAccessPermission(orgAccess);
				account.setSuperior(orgSuper);
				if (!account.getName().equals(request.getParameter("oldName"))
						|| !account.getSecondName().equals(
								request.getParameter("oldSecondName"))) {
					isRefreshTop = true;
				}
			}
			// 扩展属性
			account.setProperty("accountCategory",
					request.getParameter("accountCategory"));
			account.setProperty("accountLevel",
					request.getParameter("accountLevel"));
			account.setProperty("accountNature",
					request.getParameter("accountNature"));
			account.setProperty("chiefLeader",
					request.getParameter("chiefLeader"));
			account.setProperty("address", request.getParameter("address"));
			account.setProperty("zipCode", request.getParameter("zipCode"));
			account.setProperty("telephone", request.getParameter("telephone"));
			account.setProperty("fax", request.getParameter("fax"));
			account.setProperty("ipAddress", request.getParameter("ipAddress"));
			account.setProperty("accountMail",
					request.getParameter("accountMail"));
			boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag();
			if (isGovVersion) {
				account.setProperty("adminiLevel",
						request.getParameter("adminiLevel")); // 政务新增扩展属性 行政级别
			}
			// 更新管理员
			V3xOrgMember member = orgManagerDirect
					.getMemberByLoginName(oldLoginName);
			String adminName = request.getParameter("adminName");
			String adminPass = request.getParameter("adminPass");
			account.setAdminName(adminName);
			account.setAdminPass(adminPass);
			// 停用单位的校验
			if (orgEnabled && !account.getEnabled()) {
				// 单位下存在未删除的组织模型时不允许停用，但允许存在停用的组织模型
				if (!isAccountCanBeDisenable(id)) {
					account.setEnabled(orgEnabled);
					out.println("<script>");
					out.println("alert('"
							+ Constant.getString("orgainzation.unable.account",
									local) + "');");
					out.println("</script>");
					out.flush();
					return redirectModelAndView("/organization.do?method=organizationFrame&from=Account&addOrganiza=addAccount");
				}
			}
			orgManager.updateAccount(account);
			eventListener.updateAccount(oldAccount, account);
			// GKE同步单位名称
			eventListener.updateAccount(account);

			// 记录应用日志
			appLogManager.insertLog(user,
					AppLogAction.Organization_UpdateAccount, user.getName(),
					account.getName());
			String aCategory = request.getParameter("accountCategory");
			if (!Strings.isBlank(aCategory)) {
				long iad = Long.valueOf(aCategory).longValue();
				Metadata metadata = metadataManager
						.getMetadata(MetadataNameEnum.org_property_account_category);
				if (null != metadata) {
					MetadataItem item = metadataManager.getMetadataItem(
							metadata.getName(), Long.valueOf(iad).toString());
					if (null != item) {
						metadataManager.refMetadataItem(metadata.getId(),
								item.getId(), Long.valueOf(iad).intValue());
					}
				}
			}

			// member.setLoginName(adminName);
			// 判断当没有更改密码的时候就不去更改密码
			/*
			 * if (!Strings.isEmpty(adminPass)){ member.setPassword(adminPass);
			 * } if (account.getEnabled()){ member.setEnabled(true); }else
			 * if(!account.getEnabled()){ member.setEnabled(false); }
			 */
			// orgManagerDirect.updateEntity(member);

			// 切换新的帐号
			if (!oldLoginName.equalsIgnoreCase(adminName)) {
				OnlineUser ou = OnlineRecorder.getOnlineAdmin(oldLoginName);
				if (ou != null) {
					// 当前用户是单位管理员，不退出，修改CurrentUser重新登录
					if (oldLoginName.equals(user.getLoginName())) {
						user.setLoginName(adminName);
						CurrentUser.set(user);
						ou.setLoginName(adminName);
						OnlineRecorder.loginAdmin(user, ou);
					}
					OnlineRecorder.removeAdmin(oldLoginName);
				}
			}

			// 记录密码修改日志
			if (!V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD.equals(adminPass)
					&& !Strings.isEmpty(adminPass)) {
				appLogManager.insertLog(user,
						AppLogAction.Systemmanager_UpdateAdminPassWord,
						user.getName(), member.getName());
			}

			this.ldap4AccountOU(account, ldapOu, id);
			out.println("<script>");
			out.println("alert('"
					+ Constant.getString4CurrentUser("organization.ok") + "');");
			out.println("</script>");
			out.flush();

			if (isGroupAdmin) {
				boolean continues = request.getParameterValues("cont") != null;
				return redirectModelAndView("/organization.do?method=organizationFrame&from=Account&addOrganiza=addAccount&cont="
						+ continues);
			} else {
				return redirectModelAndView("/organization.do?method=editAccount&isGroupAdmin=false&isDetail=readOnly&unitManager="
						+ unitManager + "&isRefreshTop=" + isRefreshTop);
			}
		} catch (BusinessException e) {
			out.println("<script>");
			out.println("alert('" + e.getMessage() + "!');");
			out.println("</script>");
			return redirectModelAndView("/organization.do?method=listAccount");
		}
	}

	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView updateGroupAccount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		boolean isGroupAdmin = false;
		String isGroupAdminStr = request.getParameter("isGroupAdmin");
		V3xOrgAccount account = null;
		if (isGroupAdminStr != null && isGroupAdminStr.equals("true")) {
			isGroupAdmin = true;
			account = orgManagerDirect.getRootAccount();
		} else if (!(Boolean) SysFlag.org_showGroupAccountPage.getFlag()) {
			List<V3xOrgAccount> accountList = orgManagerDirect.getAllAccounts();
			for (V3xOrgAccount acc : accountList) {
				if (!acc.getIsRoot()) {
					account = acc;
				}
			}
		} else {
			account = orgManagerDirect.getRootAccount();
		}
		boolean orgIsRoot = account.getIsRoot();
		try {
			V3xOrgAccount model = new V3xOrgAccount();
			bind(request, model);

			String oldLoginName = account.getAdminName();
			// 拷贝属性之前先将原单位的工作范围保存
			model.setLevelScope(account.getLevelScope());
			account.copyProperties(model);
			account.setIsRoot(orgIsRoot);
			account.setAccessPermission(V3xOrgEntity.ACCOUNT_ACC_ALL);
			account.setSortId(1);
			// 扩展属性
			account.setProperty("accountCategory",
					request.getParameter("accountCategory"));
			account.setProperty("accountLevel",
					request.getParameter("accountLevel"));
			account.setProperty("accountNature",
					request.getParameter("accountNature"));
			account.setProperty("chiefLeader",
					request.getParameter("chiefLeader"));
			account.setProperty("address", request.getParameter("address"));
			account.setProperty("zipCode", request.getParameter("zipCode"));
			account.setProperty("telephone", request.getParameter("telephone"));
			account.setProperty("fax", request.getParameter("fax"));
			account.setProperty("ipAddress", request.getParameter("ipAddress"));
			account.setProperty("accountMail",
					request.getParameter("accountMail"));
			boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag(); // 是否是政务版
			if (isGovVersion) {
				account.setProperty("adminiLevel",
						request.getParameter("adminiLevel")); // 政务新增 扩展字段 行政级别
			}
			// 更新管理员
			// V3xOrgMember member =
			// orgManagerDirect.getMemberByLoginName(oldLoginName);
			String adminName = request.getParameter("adminName");
			String adminPass = request.getParameter("adminPass");
			account.setAdminName(adminName);
			account.setAdminPass(adminPass);
			orgManager.updateAccount(account);

			String aCategory = request.getParameter("accountCategory");
			if (!Strings.isBlank(aCategory)) {
				long iad = Long.valueOf(aCategory).longValue();
				Metadata metadata = metadataManager
						.getMetadata(MetadataNameEnum.org_property_account_category);
				if (null != metadata) {
					MetadataItem item = metadataManager.getMetadataItem(
							metadata.getName(), Long.valueOf(iad).toString());
					if (null != item) {
						metadataManager.refMetadataItem(metadata.getId(),
								item.getId(), Long.valueOf(iad).intValue());
					}
				}
			}

			// member.setLoginName(adminName);

			// 判断当没有更改密码的时候就不去更改密码
			// if (!Strings.isEmpty(adminPass)){
			// member.setPassword(adminPass);
			// }
			// orgManagerDirect.updateEntity(member);

			User user = CurrentUser.get();
			// 记录更改管理员密码日志（根据不同版本显示不同的集团管理员名称）

			if (!V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD.equals(adminPass)
					&& !Strings.isEmpty(adminPass)) {
				String OrganizationResources = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
				String groupName = ResourceBundleUtil.getString(
						OrganizationResources,
						"org.account_form.groupAdminName.value"
								+ (String) SysFlag.EditionSuffix.getFlag());
				appLogManager.insertLog(user,
						AppLogAction.Organization_UpdateGroupAdminPassword,
						groupName);
			}

			// 记录应用日志
			appLogManager.insertLog(user,
					AppLogAction.Organization_UpdateGroupAccount,
					account.getName());
			boolean isRefreshTop = false; // 更改单位名称时需要更新TOP
			if (!isGroupAdmin) {
				if (!account.getName().equals(request.getParameter("oldName"))
						|| !account.getSecondName().equals(
								request.getParameter("oldSecondName"))) {
					isRefreshTop = true;
				}
			}

			out.println("<script>");
			out.println("alert('"
					+ Constant.getString4CurrentUser("organization.ok") + "');");
			out.println("</script>");

			if (isGroupAdmin) {
				return redirectModelAndView("/organization.do?method=organizationFrame&from=Account&addOrganiza=addAccount&cont=" + false);
			} else {
				return redirectModelAndView("/organization.do?method=editGroupAccount&isGroupAdmin=false&isDetail=readOnly&isRefreshTop="
						+ isRefreshTop);
			}
		} catch (BusinessException e) {
			return redirectModelAndView("/organization.do?method=listAccount");
		}
	}

	public String[] getIds(String strIds) {
		if (null != strIds && !strIds.equals("")) {
			strIds = strIds.substring(0, strIds.lastIndexOf(','));
			String[] arrIds = strIds.split(",");
			return arrIds;
		}
		return null;
	}

	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView destroyAccount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String strid = request.getParameter("id");
		String[] arrId = getIds(strid);
		PrintWriter out = response.getWriter();
		User user = CurrentUser.get();

		// 记录应用日志
		List<String[]> appLogs = new ArrayList<String[]>();

		if (null != arrId && arrId.length > 0) {
			V3xOrgAccount account = null;
			for (String strId : arrId) {
				Long id = Long.parseLong(strId);
				account = orgManagerDirect.getAccountById(id);
				// 单位下存在未删除的组织模型时不允许删除
				if (!isAccountCanBeDeleted(id)) {
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_alert_delAccount_no'));");
					out.println("</script>");
					return super.refreshWorkspace();
				}
				String[] log = new String[2];
				log[0] = user.getName();
				log[1] = account.getName();
				appLogs.add(log);
				orgManager.deleteAccount(account);
				eventListener.deleteAccount(account);
			}
		}

		// 记录应用日志
		appLogManager.insertLogs(user, AppLogAction.Organization_DeleteAccount,
				appLogs);

		out.println("<script>");
		out.println("alert(parent.v3x.getMessage('organizationLang.option_organization_ok'));");
		out.println("</script>");
		return super.refreshWorkspace();
	}

	/**
	 * 更改单位的状态
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView changerstate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"organization/account/changerState");
		String id = request.getParameter("id");
		V3xOrgAccount account = orgManagerDirect.getAccountById(Long
				.parseLong(id));
		result.addObject("accountEnabled", account.getEnabled());
		return result;
	}

	/**
	 * 更改单位的状态
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView changerAccountState(HttpServletRequest request,
			HttpServletResponse response) {
		String id = request.getParameter("id");
		V3xOrgAccount account = null;
		try {
			account = orgManagerDirect.getAccountById(Long.parseLong(id));
		} catch (NumberFormatException e1) {
			logger.error("", e1);
		} catch (BusinessException e1) {
			logger.error("", e1);
		}
		int enabled = Integer.parseInt(request.getParameter("enalbed"));
		try {
			if (enabled == 1)
				account.setEnabled(true);
			else
				account.setEnabled(false);
			orgManagerDirect.updateEntity(account);
			// 更新管理员
			V3xOrgMember member = orgManagerDirect.getMemberByLoginName(account
					.getAdminName());
			if (account.getEnabled()) {
				member.setEnabled(true);
			} else if (!account.getEnabled()) {
				member.setEnabled(false);
			}
			orgManagerDirect.updateEntity(member);
		} catch (BusinessException e) {
			log.error(e.getMessage(), e);
		}
		// return
		// redirectModelAndView("/organization.do?method=organizationFrame&from=Account");
		try {
			super.rendJavaScript(response, "parent.doAccount()");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 进入部门管理的数据页面方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView listDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		V3xOrgAccount account = orgManagerDirect.getAccountById(user
				.getLoginAccount());

		ModelAndView result = new ModelAndView(
				"organization/department/listDept");
		List deptlist = new ArrayList();
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		Long accountId = user.getAccountId();
		StringBuffer strbuf = new StringBuffer();
		Map<String, Object> param = new HashMap<String, Object>();
		if (Strings.isNotBlank(condition) && Strings.isNotBlank(textfield)) {
			if (!condition.equals("orgDepartmentId")) {
				strbuf.append("select a from "
						+ V3xOrgDepartment.class.getName()
						+ " a where a.orgAccountId='" + accountId
						+ "' and a.isDeleted='0' and a.isInternal = '1' ");
				if (condition.equals("name")) {
					strbuf.append(" and a.name like :textfield ");
					param.put("textfield", "%" + textfield + "%");
				} else if (condition.equals("code")) {
					strbuf.append(" and a.code like :textfield");
					param.put("textfield", "%" + textfield + "%");
				}
				strbuf.append(" order by a.sortId asc");
				deptlist = searchManager.searchByHql(strbuf.toString(), param,
						true);
			} else if (condition.equals("orgDepartmentId")) {
				V3xOrgDepartment dept = (V3xOrgDepartment) orgManagerDirect
						.getEntityNoRelation(
								V3xOrgDepartment.class.getSimpleName(), "id",
								Long.parseLong(textfield), accountId);
				String path = dept.getPath() + V3xOrgEntity.DEP_PATH_DELIMITER;
				strbuf.append("from " + V3xOrgDepartment.class.getName()
						+ " where substring(path,1," + path.length() + ") ='"
						+ path + "' and isDeleted ='0' and orgAccountId='"
						+ accountId + "' and isInternal = '1' ");
				strbuf.append(" order by sortId asc");
				deptlist = searchManager.searchByHql(strbuf.toString(), param,
						true);
			}
			Collections.sort(deptlist, CompareSortEntity.getInstance());
			result.addObject("condition", condition);
			result.addObject("textfield", textfield);

			for (Object dept : deptlist) {
				V3xOrgDepartment dep = (V3xOrgDepartment) dept;
				// 设置部门的岗位属性
				List<V3xOrgRelationship> rels = this.orgManagerDirect
						.getRelationships("type",
								V3xOrgEntity.ORGREL_TYPE_DEP_POST, "sourceId",
								dep.getId());
				for (Iterator<V3xOrgRelationship> relIt = rels.iterator(); relIt
						.hasNext();) {
					dep.addDepPost(relIt.next().getObjectiveId());
				}

				// 设置部门的角色属性
				rels = this.orgManagerDirect.getRelationships("type",
						V3xOrgEntity.ORGREL_TYPE_DEP_ROLE, "sourceId",
						dep.getId());
				for (Iterator<V3xOrgRelationship> relIt = rels.iterator(); relIt
						.hasNext();) {
					dep.addDepRole(relIt.next().getObjectiveId());
				}

			}

		} else {
			try {
				deptlist = orgManagerDirect.getEntityList(
						V3xOrgDepartment.class.getSimpleName(), "isInternal",
						true, accountId, true);
			} catch (BusinessException e) {
				log.error("", e);
			}
		}

		List<V3xOrgRole> returnrolelst = new ArrayList<V3xOrgRole>();
		List<WebV3xOrgDepartment> resultlist = new ArrayList<WebV3xOrgDepartment>();
		int rolenum = 0;
		List rolereallst = new ArrayList();
		for (int i = 0; i < deptlist.size(); i++) {
			V3xOrgDepartment dept = (com.seeyon.v3x.organization.domain.V3xOrgDepartment) deptlist
					.get(i);
			List<V3xOrgRole> roleList = new ArrayList<V3xOrgRole>();
			Iterator roleId = dept.getRoles().iterator();
			while (roleId.hasNext()) {
				V3xOrgRole role = orgManagerDirect.getRoleById(Long
						.parseLong(String.valueOf(roleId.next())));
				roleList.add(role);
			}
			// 决定表头显示几个角色
			if (i == 0) {
				rolenum = roleList.size();
				returnrolelst = roleList;
			} else {
				if (rolenum < roleList.size()) {
					rolenum = roleList.size();
					returnrolelst = roleList;
				}
			}
			rolereallst.add(i, roleList);
		}

		for (int i = 0; i < deptlist.size(); i++) {
			V3xOrgDepartment dept = (com.seeyon.v3x.organization.domain.V3xOrgDepartment) deptlist
					.get(i);
			Long longid = dept.getId();
			V3xOrgDepartment parent = orgManagerDirect
					.getParentDepartment(longid);
			WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
			webdept.setV3xOrgDepartment(dept);
			if (null != parent) {
				webdept.setParentId(parent.getId());
				webdept.setParentName(parent.getName());
			} else {
				if (dept.getPath().indexOf(".") > 0
						&& (dept.getPath().indexOf(".") == dept.getPath()
								.lastIndexOf("."))) {
					webdept.setParentId(dept.getOrgAccountId());
					webdept.setParentName(account.getName());
				}
			}

			// ------------------获取部门角色信息6-22---------------xut
			List<V3xOrgRole> roleList = (List<V3xOrgRole>) rolereallst.get(i);
			List<V3xOrgMember> ml = new ArrayList<V3xOrgMember>();
			for (V3xOrgRole role : roleList) {
				StringBuffer membersb = new StringBuffer();
				ml = orgManagerDirect.getMemberByRole(role.getBond(),
						dept.getId(), role.getId());
				if (ml != null && ml.size() != 0) {
					int vonum = 0;
					for (V3xOrgMember vom : ml) {
						if (vonum == 0) {
							membersb.append(vom.getName());
							++vonum;
						} else {
							membersb.append("、" + vom.getName());
							++vonum;
						}
					}
					if (role.getName().equals(
							V3xOrgEntity.ORGENT_META_KEY_DEPADMIN))
						webdept.setAdminNames(membersb.toString());
					else if (role.getName().equals(
							V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER))
						webdept.setManagerNames(membersb.toString());
				}
				if (returnrolelst.size() == 0) {
					if (membersb.length() != 0) {
						webdept.getRolelist().add(membersb.toString());
					} else {
						webdept.getRolelist().add("&nbsp;");
					}
				} else {
					if (membersb.length() != 0) {
						webdept.getRolelist().add(membersb.toString());
					} else {
						webdept.getRolelist().add("&nbsp;");
					}
				}
			}
			resultlist.add(webdept);
		}
		result.addObject("rolelst", returnrolelst);
		result.addObject("account", account);
		result.addObject("deptlist", resultlist);
		result.addObject("islist", "islist");

		return result;

	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator })
	public ModelAndView exportDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		String dept_list = ResourceBundleUtil.getString(resource, local,
				"org.dept_form.list");
		V3xOrgAccount account = orgManagerDirect.getAccountById(user
				.getLoginAccount());
		// --------------MODIFY BY XUT 6-28----------------
		List<V3xOrgDepartment> deptlist = orgManagerDirect.getAllDepartments(
				user.getLoginAccount(), false);
		List<WebV3xOrgDepartment> resultlist = new ArrayList<WebV3xOrgDepartment>();
		for (int i = 0; i < deptlist.size(); i++) {
			V3xOrgDepartment dept = (com.seeyon.v3x.organization.domain.V3xOrgDepartment) deptlist
					.get(i);
			Long longid = dept.getId();
			V3xOrgDepartment parent = orgManagerDirect
					.getParentDepartment(longid);
			WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
			webdept.setV3xOrgDepartment(dept);
			if (null != parent) {
				webdept.setParentId(parent.getId());
				webdept.setParentName(parent.getName());
			} else {
				if (dept.getPath().indexOf(".") > 0
						&& (dept.getPath().indexOf(".") == dept.getPath()
								.lastIndexOf("."))) {
					webdept.setParentId(dept.getOrgAccountId());
					webdept.setParentName(account.getName());
				}
			}
			resultlist.add(webdept);
		}
		DataRecord dataRecord = OrganizationHelper.exportDept(resultlist,
				request, metadataManager, response, fileToExcelManager,
				orgManagerDirect, spaceManager, account);
		OrganizationHelper.exportToExcel(request, response, fileToExcelManager,
				dept_list + "-" + user.getLoginName(), dataRecord);
		return null;
	}

	/**
	 * 增加部门编辑页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView addDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"organization/department/addDept");
		WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();

		V3xOrgDepartment dept = new V3xOrgDepartment();
		Locale local = LocaleContext.getLocale(request);
		// 获得最大排序号
		Integer maxSortNum = orgManagerDirect.getMaxSortNum(
				V3xOrgDepartment.class.getSimpleName(), CurrentUser.get()
						.getLoginAccount());
		dept.setSortId(maxSortNum + 1);
		dept.setEnabled(true);
		String parentID = "";
		if (request.getParameter("parentID") != null) {
			parentID = request.getParameter("parentID");
		}
		String parentName = "";
		if (null != parentID && !parentID.equals("")) {
			V3xOrgDepartment parentDept = orgManagerDirect
					.getDepartmentById(Long.parseLong(parentID));
			if (request.getParameter("parentName") != null) {
				parentName = request.getParameter("parentName");
			} else {
				parentName = parentDept.getName();
			}
			if (parentDept != null && !parentDept.getIsInternal()) {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('"
						+ Constant.getString("depatition.isinternal.parent",
								local) + "');");
				out.println("</script>");
				out.flush();
				return null;
			} else if (parentDept != null && !parentDept.getEnabled()) {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('"
						+ Constant.getString("depatition.isenabled.parent",
								local) + "');");
				out.println("</script>");
				out.flush();
				return null;
			}
			webdept.setParentId(Long.parseLong(parentID));
		}
		if (null != parentName && !parentName.equals("")) {
			webdept.setParentName(parentName);
		}
		webdept.setV3xOrgDepartment(dept);
		result.addObject("dept", webdept);
		// 获取部门角色
		List<V3xOrgRole> roleList = new ArrayList<V3xOrgRole>();
		dept.setOrgAccountId(CurrentUser.get().getLoginAccount());
		dept.init((CallbackAddInitialData) orgManagerDirect);
		List<Long> roles = dept.getRoles();
		Iterator allIt = roles.iterator();
		while (allIt.hasNext()) {
			V3xOrgRole role = orgManagerDirect.getRoleById(Long
					.parseLong(String.valueOf(allIt.next())));
			if (role.getName()
					.equals(EdocRoleHelper.departmentExchangeRoleName)
					&& !Functions.isEnableEdoc()) {
				continue;
			}
			roleList.add(role);
		}
		Collections.sort(roleList, CompareSortEntity.getInstance());
		result.addObject("roleList", roleList);
		result.addObject("showDeptManager", 1);
		// listMenu页面增加islist ,走列表传参刷新。
		String islist = request.getParameter("islist");
		result.addObject("islist", islist);
		result.addObject("oper", "addDept");
		return result;
	}

	/**
	 * 部门管理添加数据方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView createDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			User user = CurrentUser.get();
			long accountid = user.getLoginAccount();
			String parentId = request.getParameter("parentId");
			V3xOrgDepartment dept = new V3xOrgDepartment();
			dept.setOrgAccountId(accountid);
			dept.init((CallbackAddInitialData) orgManagerDirect);
			bind(request, dept);
			V3xOrgDepartment parent = null;
			dept.setOrgAccountId(accountid);

			Long _parent_Id = 0L;
			if (null != parentId && !parentId.equals("")) {
				parent = orgManagerDirect.getDepartmentById(Long
						.parseLong(parentId));
			} else {
				_parent_Id = accountid;
			}

			if (null != parent) {
				_parent_Id = parent.getId();
			} else {
				_parent_Id = accountid;
			}
			dept.setParentDeptId(_parent_Id);
			// 加入同级部门的校验
			List<V3xOrgDepartment> depts = orgManagerDirect
					.getChildDepartments(_parent_Id, true);
			for (V3xOrgDepartment orgDept : depts) {
				if (orgDept.getName().equals(dept.getName())) {
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_brother_dept_name'));");
					out.println("</script>");
					super.rendJavaScript(response, "parent.toEditMember()");
					return null;
				}
			}

			// 部门排序号的重复处理
			String isInsert = request.getParameter("isInsert");
			if (isInsert.equals("1")
					&& orgManagerDirect.isPropertyDuplicated(
							V3xOrgDepartment.class.getSimpleName(), "sortId",
							dept.getSortId())) {
				orgManagerDirect.insertRepeatSortNum(
						V3xOrgDepartment.class.getSimpleName(), accountid,
						dept.getSortId());
			}

			// 更新部门岗位
			List<Long> postList = new ArrayList<Long>();
			String strPostIds = request.getParameter("postIds");
			if (null != strPostIds && !strPostIds.equals("")) {
				String[] postIDs = strPostIds.split(",");
				for (String strid : postIDs) {
					Long id = Long.parseLong(strid);
					postList.add(id);
				}
			}

			dept.setPosts(postList);
			String isCreateDeptSpace = request
					.getParameter("isCreateDeptSpace");
			if (null != isCreateDeptSpace && isCreateDeptSpace.equals("1")) {
				dept.setCreateDeptSpace(true);
			}
			orgManager.addDepartment(dept);

			// 创建部门的空间
			/*
			 * if (null != isCreateDeptSpace && isCreateDeptSpace.equals("1")){
			 * spaceManager.createDepartmentSpace(addedDept.getId(),
			 * addedDept.getName(), accountid); //增加部门公告
			 * ((BaseBulletinManager)bulDataManager
			 * ).getBulletinUtils().createBulTypeByDept
			 * (dept.getName()+"公告",dept.getId(),accountid);
			 * 
			 * //~~~~~~~~~~~~~~动态增加部门讨论~~Tanmf~~~~~~~~
			 * this.bbsBoardManager.createDepartmentBbsBoard(dept.getId(),
			 * accountid, dept.getName()); }
			 */
			super.rendJavaScript(response, "parent.doEndDept('" + accountid
					+ "', 'add', '" + dept.getId() + "', '" + _parent_Id
					+ "', '" + Strings.escapeJavascript(dept.getName()) + "', "
					+ dept.getSortId() + ")");

			// 触发创建部门事件
			eventListener.addDepartment(dept);

			// 记录日志
			appLogManager.insertLog(user, AppLogAction.Organization_NewDept,
					user.getName(), dept.getName());

			return null;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 进入部门管理编辑方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView editDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"organization/department/editDept");
		String strid = request.getParameter("id");
		String click = request.getParameter("click");
		// 设置当前节点
		if (click != null) {
			// 如果为根节点
			if (click.equals("root")) {
				ModelAndView resultRoot = new ModelAndView(
						"organization/department/deptInfo");
				return resultRoot;
			}
		}
		Long _id = Long.parseLong(strid);
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(_id);
		WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
		webdept.setV3xOrgDepartment(dept);
		// 取得登陆单位
		User user = CurrentUser.get();
		V3xOrgAccount account = orgManagerDirect.getAccountById(user
				.getLoginAccount());

		V3xOrgDepartment parent = orgManagerDirect.getParentDepartment(_id);
		if (null != parent) {
			webdept.setParentId(parent.getId());
			webdept.setParentName(parent.getName());
		} else {
			if (dept.getPath().indexOf(".") > 0
					&& (dept.getPath().indexOf(".") == dept.getPath()
							.lastIndexOf("."))) {
				webdept.setParentId(dept.getOrgAccountId());
				webdept.setParentName(account.getName());
				boolean parentIsAccount = true;
				result.addObject("parentIsAccount", parentIsAccount);
			}
		}

		// 获得部门岗位
		List<V3xOrgPost> listPost = orgManagerDirect.getDepartmentPost(dept
				.getId());
		Collections.sort(listPost, CompareSortEntity.getInstance());
		result.addObject("postList", listPost);

		result.addObject("dept", webdept);
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
			result.addObject("preview", 0);
		} else {
			result.addObject("preview", 1);
		}

		// 获取部门角色
		List<V3xOrgRole> roleList = new ArrayList<V3xOrgRole>();
		Iterator roleId = dept.getRoles().iterator();
		while (roleId.hasNext()) {
			V3xOrgRole role = orgManagerDirect.getRoleById(Long
					.parseLong(String.valueOf(roleId.next())));
			if ((role.getName().equals(
					EdocRoleHelper.departmentExchangeRoleName) && !Functions
					.isEnableEdoc())) {
				continue;
			}
			roleList.add(role);
		}
		Collections.sort(roleList, CompareSortEntity.getInstance());
		List<List<String>> membersList = new ArrayList<List<String>>();
		List<List<Long>> memberIdsList = new ArrayList<List<Long>>();
		List<V3xOrgMember> ml = new ArrayList<V3xOrgMember>();
		for (V3xOrgRole role : roleList) {
			List<String> members = new ArrayList<String>();
			List<Long> ids = new ArrayList<Long>();
			ml = orgManagerDirect.getMemberByRole(role.getBond(), dept.getId(),
					role.getId());

			if (ml != null && ml.size() != 0) {
				for (V3xOrgMember vom : ml) {
					members.add(vom.getName());
					ids.add(vom.getId());
				}
			}

			membersList.add(members);
			memberIdsList.add(ids);
		}

		result.addObject("roleList", roleList);
		result.addObject("membersList", membersList);
		result.addObject("memberIdsList", memberIdsList);
		// 获取是否为内部人员
		List<V3xOrgDepartment> deptInternal = orgManagerDirect
				.getChildDepartments(_id, true);
		boolean internael = false;
		for (V3xOrgDepartment department : deptInternal) {
			internael = department.getIsInternal();
		}
		result.addObject("internael", internael);

		// 获取是否创建了部门空间
		result.addObject("isCreateDepartmentSpace",
				spaceManager.isCreateDepartmentSpace(dept.getId()));
		// 在修改的是后显示 部门管理信息
		result.addObject("showDeptManager", 1);
		// listMenu页面传参，后面用来做判读。
		String islist = request.getParameter("islist");
		result.addObject("islist", islist);

		return result;
	}

	/**
	 * 进入部门岗位管理编辑方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView editDeptPosts(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"organization/department/deptPosts");
		String strid = request.getParameter("id");
		Long _id = Long.parseLong(strid);
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(_id);
		WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
		webdept.setV3xOrgDepartment(dept);

		V3xOrgRole deptManager = orgManagerDirect
				.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPMANAGER);
		List<V3xOrgMember> managerList = null;
		if (deptManager != null) {
			managerList = orgManagerDirect
					.getMemberByRole(V3xOrgEntity.ROLE_BOND_DEPARTMENT, _id,
							deptManager.getId());
		}
		List<WebV3xOrgMember> webMemberList = new ArrayList<WebV3xOrgMember>();
		List<Long> memberIdList = new ArrayList<Long>();
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		String cntStr = ResourceBundleUtil.getString(resource, local,
				"department.cntPost");

		// 取得本部门下的直属人员
		List<V3xOrgEntity> ents = orgManagerDirect.getEntityListNoRelation(
				V3xOrgMember.class.getSimpleName(), "orgDepartmentId",
				dept.getId(), dept.getOrgAccountId(), false);
		// 为了提升性能这里将单位下的所有岗位缓存
		// Map postMap = organization.getAllEntity(V3xOrgPost.class,
		// dept.getOrgAccountId());
		// 去掉离职停用的人员
		for (V3xOrgEntity ent : ents) {
			V3xOrgMember member = (V3xOrgMember) ent;
			if (member.getEnabled()) {
				WebV3xOrgMember webMember = new WebV3xOrgMember();
				webMember.setV3xOrgMember(member);
				webMember.setTypeName(member.getName());
				V3xOrgPost post = orgManagerDirect.getPostById(member
						.getOrgPostId());
				if (post != null)
					webMember.setPostName(post.getName());
				webMemberList.add(webMember);
				memberIdList.add(member.getId());
			}
		}
		// 取得本部门下的副岗人员
		List<V3xOrgRelationship> memberPosts = orgManagerDirect
				.getRelationships("type", V3xOrgEntity.ORGREL_TYPE_MEMBER_POST,
						"objectiveId", dept.getId());
		if (memberPosts != null && memberPosts.size() > 0) {
			for (V3xOrgRelationship rel : memberPosts) {
				if (!memberIdList.contains(rel.getSourceId())) {
					V3xOrgMember member = (V3xOrgMember) orgManagerDirect
							.getMemberById(rel.getSourceId());
					if (member != null && member.getEnabled()) {
						WebV3xOrgMember webMember = new WebV3xOrgMember();
						webMember.setV3xOrgMember(member);
						webMember.setTypeName(cntStr + member.getName());
						if (rel.getBackupId() != null) {
							V3xOrgPost post = orgManagerDirect.getPostById(rel
									.getBackupId());
							if (post != null) {
								webMember.setPostName(post.getName());
							}
						}
						webMemberList.add(webMember);
						memberIdList.add(rel.getSourceId());
					}
				}
			}
		}
		// 取得本部门下的兼职人员
		List<V3xOrgRelationship> cntPosts = orgManagerDirect.getRelationships(
				"type", V3xOrgEntity.ORGREL_TYPE_CONCURRENT_POST,
				"objectiveId", dept.getId());
		if (cntPosts != null && cntPosts.size() > 0) {
			for (V3xOrgRelationship rel : cntPosts) {
				V3xOrgMember member = (V3xOrgMember) orgManagerDirect
						.getMemberById(rel.getSourceId());
				if (member != null && member.getEnabled()) {
					WebV3xOrgMember webMember = new WebV3xOrgMember();
					webMember.setV3xOrgMember(member);
					webMember.setTypeName(cntStr + member.getName());
					if (rel.getBackupId() != null) {
						V3xOrgPost post = orgManagerDirect.getPostById(rel
								.getBackupId());
						if (post != null) {
							webMember.setPostName(post.getName());
						}
					}
					webMemberList.add(webMember);
					memberIdList.add(rel.getSourceId());
				}
			}
		}

		// 获得部门岗位
		List<V3xOrgPost> listPost = orgManagerDirect.getDepartmentPost(dept
				.getId());
		Collections.sort(listPost, CompareSortEntity.getInstance());
		result.addObject("managerList", managerList);
		result.addObject("memberList", webMemberList);
		result.addObject("memberListLength", webMemberList.size());
		result.addObject("postList", listPost);
		result.addObject("postsSize", webMemberList.size());
		result.addObject("dept", webdept);
		result.addObject("readOnly", request.getParameter("readOnly"));
		return result;
	}

	/**
	 * 进入部门岗位管理修改方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView updateDeptPosts(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String deptId = request.getParameter("id");
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(new Long(
				deptId));
		V3xOrgDepartment deptBeforeUpdate = new V3xOrgDepartment();
		BeanUtils.copyProperties(deptBeforeUpdate, dept);

		// 更新部门岗位
		List<Long> postList = new ArrayList<Long>();
		String strPostIds = request.getParameter("postIds");
		if (null != strPostIds && !strPostIds.equals("")) {
			String[] postIDs = strPostIds.split(",");
			for (String strid : postIDs) {
				Long id = Long.parseLong(strid);
				postList.add(id);
			}
		}
		// 获得部门岗位
		List<V3xOrgPost> listPost = orgManagerDirect.getDepartmentPost(dept
				.getId());
		for (V3xOrgPost post : listPost) {
			if (!postList.contains(post.getId()) && post.getId() != -1) {
				// 检查部门岗位下是否有人
				List<V3xOrgMember> members = orgManagerDirect
						.getMembersByDeptPost(post.getId(), dept.getId(),
								dept.getOrgAccountId());
				if (members != null && !members.isEmpty()) {
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_del_dept_post_mem','"
							+ post.getName() + "'));");
					out.println("</script>");
					return null;
				}
			}
		}
		dept.setPosts(postList);
		orgManagerDirect.updateEntity(dept);

		// 触发更新部门事件
		eventListener.updateDepartment(deptBeforeUpdate, dept);

		super.rendJavaScript(response, "window.close();");
		return null;
	}

	/**
	 * 部门管理的修改方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView updateDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String deptId = request.getParameter("id");
		String islist = request.getParameter("islist");
		Long lDeptId = Long.valueOf(deptId);
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(lDeptId);
		User user = CurrentUser.get();

		// 如果部门信息不存在则提示用户
		if (dept == null || dept.getIsDeleted()
				|| !dept.getOrgAccountId().equals(user.getLoginAccount())) {
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_null'));");
			out.println("</script>");
			out.flush();
			return super.refreshWorkspace();
		}

		// 为事件调用记录修改前的部门
		V3xOrgDepartment deptBeforeUpdate = new V3xOrgDepartment();
		BeanUtils.copyProperties(deptBeforeUpdate, dept);

		Locale local = LocaleContext.getLocale(request);
		Integer orgSortId = dept.getSortId();
		boolean orgEnable = dept.getEnabled();
		boolean nowEnable = request.getParameter("enabled").equals("1") ? true
				: false;
		// 如果部门被停用则停用子部门
		if (orgEnable && (!nowEnable)) {
			// List<V3xOrgMember> deptMembers =
			// orgManagerDirect.getMembersByDepartment(dept.getId(), false,
			// CurrentUser.get().getLoginAccount());
			List<V3xOrgMember> deptMembers = orgManager.getMembersByDepartment(
					dept.getId(), true);
			boolean isEnabledMember = false;
			for (V3xOrgMember mem : deptMembers) {
				if (mem.isValid()) {
					isEnabledMember = true;
				}
			}
			if (deptMembers.size() > 0 && isEnabledMember) {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_unenabled_department_member'));");
				out.println("</script>");
				super.rendJavaScript(response, "parent.toEditMember()");
				return null;
			}
		} else if ((!orgEnable) && nowEnable) {
			V3xOrgDepartment parent = orgManagerDirect
					.getParentDepartment(lDeptId);
			if (parent != null) {
				if (!parent.getEnabled()) {
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert('"
							+ Constant.getString("depatition.unenable.parent",
									local) + "');");
					out.println("</script>");
					super.rendJavaScript(response, "parent.toEditMember()");
					return null;
				}
			}
		}
		// 部门排序号的重复处理
		Integer sortId = Integer.valueOf(request.getParameter("sortId"));
		String isInsert = request.getParameter("isInsert");
		if (!orgSortId.equals(sortId)
				&& isInsert.equals("1")
				&& orgManagerDirect.isPropertyDuplicated(
						V3xOrgDepartment.class.getSimpleName(), "sortId",
						sortId, dept.getId())) {
			orgManagerDirect.insertRepeatSortNum(V3xOrgDepartment.class
					.getSimpleName(), CurrentUser.get().getLoginAccount(),
					sortId);
		}
		bind(request, dept);

		String parentId = request.getParameter("parentId");

		// 检查父部门是否是自己
		if (parentId != null && StringUtils.isNotBlank(parentId)) {
			if (dept.getId().equals(Long.parseLong(parentId))) {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.organization_department_same'));");
				out.println("</script>");
				super.rendJavaScript(response, "parent.toEditMember()");
				return null;
			}
			// huangfj 2011-12-21 检查父部门是否是自己的下级部门
			V3xOrgDepartment tempdept = orgManagerDirect.getDepartmentById(Long
					.parseLong(parentId));
			// 上级部门为单位时(null)是无需判断的
			if (tempdept != null) {
				String dPath = dept.getPath();
				String pPath = tempdept.getPath();
				String[] dArray = dPath.split("[.]");
				String[] pArray = pPath.split("[.]");
				if (pPath.startsWith(dPath)) {
					// AEIGHT-9163 比较path数组的长度是为了避免类似0.2和0.23这样同级部门被判断错
					if (!(dArray.length == pArray.length)) {
						PrintWriter out = response.getWriter();
						out.println("<script>");
						out.println("alert(parent.v3x.getMessage('organizationLang.organization_department_child'));");
						out.println("</script>");
						super.rendJavaScript(response, "parent.toEditMember()");
						return null;
					}
				}

			}
		}
		// 更新父部门
		/*
		 * V3xOrgDepartment orgParent =
		 * orgManagerDirect.getParentDepartment(lDeptId); if(orgParent==null){
		 * Long pId = Long.parseLong(parentId);
		 * if(orgManagerDirect.getDepartmentById(pId)!=null){
		 * orgManagerDirect.setDepPath(dept, pId); } }else{ Long orgParentId =
		 * orgParent.getId();
		 * if(parentId!=null&&StringUtils.isNotBlank(parentId)){
		 * if(!orgParentId.equals(Long.parseLong(parentId))){
		 * orgManagerDirect.setDepPath(dept, Long.parseLong(parentId)); } } }
		 */
		dept.setParentDeptId(Long.parseLong(parentId));
		// 加入同级部门的校验
		List<V3xOrgDepartment> depts = orgManagerDirect.getChildDepartments(
				Long.parseLong(parentId), true);
		for (V3xOrgDepartment orgDept : depts) {
			if (orgDept.getName().equals(dept.getName())
					&& !orgDept.equals(dept)) {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_brother_dept_name'));");
				out.println("</script>");
				super.rendJavaScript(response, "parent.toEditMember()");
				return null;
			}
		}

		// 更新部门岗位
		List<Long> postList = new ArrayList<Long>();
		String strPostIds = request.getParameter("postIds");
		if (null != strPostIds && !strPostIds.equals("")) {
			String[] postIDs = strPostIds.split(",");
			for (String strid : postIDs) {
				Long id = Long.parseLong(strid);
				postList.add(id);
			}
		}
		// 获得部门岗位
		List<V3xOrgPost> listPost = orgManagerDirect.getDepartmentPost(dept
				.getId());
		for (V3xOrgPost post : listPost) {
			if (!postList.contains(post.getId())) {
				// 检查部门岗位下是否有人
				List<V3xOrgMember> members = orgManagerDirect
						.getMembersByDeptPost(post.getId(), dept.getId(),
								dept.getOrgAccountId());
				if (members != null && !members.isEmpty()) {
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_del_dept_post_mem','"
							+ post.getName() + "'));");
					out.println("</script>");
					super.rendJavaScript(response, "parent.toEditMember()");
					return null;
				}
			}
		}
		dept.setPosts(postList);

		boolean isCreateDeptSpace = "true".equals(request
				.getParameter("isCreateDeptSpace"));
		List<Long> deptAdmins = null;
		dept.setCreateDeptSpace(isCreateDeptSpace);
		dept.setUpdateTime(new Date());
		orgManager.updateDepartment(dept);

		String resource = "com.seeyon.v3x.system.resources.i18n.SysMgrResources";
		Locale locale = LocaleContext.getLocale(request);

		// 设置角色
		for (int loop = 0;; loop++) {
			if (request.getParameter(loop + "roleid") != null) {
				String memberIds = request.getParameter(loop + "roleid");

				if (loop == 2)
					deptAdmins = FormBizConfigUtils.parseStr2Ids(memberIds);

				List<V3xOrgMember> roleMembers = new ArrayList<V3xOrgMember>();
				String[] memberID = memberIds.split(",");
				Long[] ids = new Long[StringUtils.isBlank(memberIds) ? 0
						: memberID.length];
				for (int i = 0; i < ids.length; i++) {
					V3xOrgMember roleMem = orgManagerDirect.getMemberById(Long
							.parseLong(memberID[i]));
					roleMembers.add(roleMem);
					ids[i] = Long.parseLong(memberID[i]);
				}
				Long roleId = Long.parseLong(request.getParameter(loop + "Id"));
				V3xOrgRole role = orgManagerDirect.getRoleById(roleId);
				List<V3xOrgMember> mems = orgManagerDirect.getMemberByRole(
						V3xOrgEntity.ROLE_BOND_DEPARTMENT, lDeptId, roleId);

				// 判断当前角色的人员是否发生了变化
				// Collections.sort(mems,CompareSortEntity.getInstance());
				// Collections.sort(roleMembers,CompareSortEntity.getInstance());
				if (!Arrays.equals(mems.toArray(), roleMembers.toArray())) {
					orgManagerDirect.addRole2Member(
							V3xOrgEntity.ROLE_BOND_DEPARTMENT, lDeptId, roleId,
							ids);
					// 如果是部门管理员更改，而且原管理员存在，则强制原部门管理员下线
					if (V3xOrgEntity.ORGENT_META_KEY_DEPADMIN.equals(request
							.getParameter("roleName" + loop))
							&& mems.size() > 0) {
						OnlineRecorder.moveToOffline(
								mems.get(0).getLoginName(),
								LoginOfflineOperation.adminKickoff);
					}
					// 记录日志
					List<String[]> appLogs = new ArrayList<String[]>();
					for (V3xOrgMember mem : roleMembers) {
						String[] appLog = new String[3];
						appLog[0] = user.getName();
						appLog[1] = mem.getName();
						appLog[2] = ResourceBundleUtil.getString(resource,
								locale, "sys.role.rolename." + role.getName());
						appLogs.add(appLog);
					}
					appLogManager.insertLogs(user,
							AppLogAction.Organization_ChangeDepRole, appLogs);

				}
			} else {
				break;
			}
		}

		// 如果部门空间不存在，则创建部门空间
		if (isCreateDeptSpace) {
			long accounrId = CurrentUser.get().getLoginAccount();
			// spaceManager.createDepartmentSpace(dept.getId(), dept.getName(),
			// accounrId);
			// 增加部门讨论区
			// this.bbsBoardManager.createDepartmentBbsBoard(dept.getId(),
			// accounrId, dept.getName());

			// 增加部门公告
			((BaseBulletinManager) bulDataManager).getBulletinUtils()
					.createBulTypeByDept(
							dept.getName()
									+ ResourceBundleUtil.getString(
											Constants.BUL_RESOURCE_BASENAME,
											"bul.more"), dept.getId(),
							accounrId);

			// 部门更新后需要同时更新部门讨论版块、部门公告的管理员信息
			this.bbsBoardManager.updateDeptBBSBoardManager(lDeptId, deptAdmins);
			((BaseBulletinManager) bulDataManager).getBulletinUtils()
					.updateDeptBulTypeManagers(lDeptId, deptAdmins);
		}
		// 如果部门空间从开通变为不开通，则删除已创建的部门空间(对应的部门讨论和部门公告实际无需删除，已被屏蔽，此后部门空间再开通时直接使用之前创建的部门公告和部门讨论)
		else {
			// spaceManager.deleteDepartmentSpace(dept.getId());
		}

		// 触发更新部门事件
		eventListener.updateDepartment(deptBeforeUpdate, dept);

		// 记录日志
		appLogManager.insertLog(user, AppLogAction.Organization_UpdateDept,
				user.getName(), dept.getName());

		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('" + Constant.getString("organization.yes", local)
				+ "');");
		out.println("</script>");

		if (islist != null && !islist.equals("")) {
			super.rendJavaScript(
					response,
					"parent.parent.listFrame.location.reload(true);"
							+ "parent.parent.detailFrame.location.href=\"/seeyon/common/detail.jsp\";");
		} else {
			super.rendJavaScript(response,
					"parent.parent.location.reload(true);");
		}

		return null;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView destroyDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String strids = request.getParameter("ids");
		String islist = request.getParameter("islist");
		Locale local = LocaleContext.getLocale(request);
		User user = CurrentUser.get();
		// 日志信息
		List<String[]> appLogs = new ArrayList<String[]>();

		if (null != strids && !strids.equals("")) {
			String[] arrids = strids.split(",");
			Long longid = null;

			List<V3xOrgDepartment> deptList = new ArrayList<V3xOrgDepartment>();
			if (null != arrids && arrids.length > 0) {
				for (String id : arrids) {
					longid = Long.parseLong(id);
					V3xOrgDepartment dept1 = orgManagerDirect
							.getDepartmentById(longid);

					deptList.add(dept1);
					// 检查部门下是否存在成员
					List<V3xOrgMember> members = orgManagerDirect
							.getMembersByDepartment(dept1.getId(), false,
									dept1.getOrgAccountId());
					boolean isAllMemberUnEnabled = true;
					for (V3xOrgMember mem : members) {
						if (mem.isValid()) {
							isAllMemberUnEnabled = false;
						}
					}
					if (ListUtils.EMPTY_LIST.equals(members)
							|| isAllMemberUnEnabled) {
						// 检查部门下是否存在组
						List<V3xOrgTeam> teams = orgManagerDirect
								.getDepartmentTeam(longid);
						List<V3xOrgDepartment> childDeps = orgManagerDirect
								.getChildDepartments(longid, false);
						for (V3xOrgDepartment child : childDeps) {
							teams.addAll(orgManagerDirect
									.getDepartmentTeam(child.getId()));
						}
						boolean isAllTeamUnEnabled = true;
						for (V3xOrgTeam team : teams) {
							if (team.getEnabled()
									&& team.getType() != V3xOrgEntity.TEAM_TYPE_PERSONAL
									&& team.getType() != V3xOrgEntity.TEAM_TYPE_DISCUSS) {
								isAllTeamUnEnabled = false;
							}
						}
						if (ListUtils.EMPTY_LIST.equals(teams)
								|| isAllTeamUnEnabled) {
							orgManager.deleteDepartment(dept1);
							// 删除部门空间的讨论 ---------lucx--------
							/*
							 * try {
							 * this.bbsBoardManager.deleteV3xBbsBoard(dept1
							 * .getId()); } catch (org.springframework.dao.
							 * DataIntegrityViolationException me) {
							 * me.printStackTrace(); } //删除部门空间的公告---
							 * bulTypeManager.delDept(dept1.getId());
							 * spaceManager
							 * .deleteDepartmentSpace(dept1.getId());
							 */
						} else {
							PrintWriter out = response.getWriter();
							out.println("<script>");
							out.println("alert('"
									+ Constant.getString(
											"organization.delete.team", local)
									+ "');");
							out.println("</script>");
							out.flush();
							// return super.refreshWorkspace();
							// 去掉上面的整个工作区刷新，改成按链接刷新。hr布局用
							if (islist != null && !islist.equals("")) {
								return super
										.redirectModelAndView(
												"/organization.do?method=showframe&style=list",
												"parent.parent");
							} else {
								return super
										.redirectModelAndView(
												"/organization.do?method=showframe&style=tree",
												"parent");
							}
						}
					} else {
						// 提示用户部门下有有效成员，不能删除
						PrintWriter out = response.getWriter();
						out.println("<script>");
						out.println("alert('"
								+ Constant.getString(
										"depatition.delete.member", local)
								+ "');");
						out.println("</script>");
						out.flush();
						// return super.refreshWorkspace();
						// 去掉上面的整个工作区刷新，改成按链接刷新。hr布局用
						if (islist != null && !islist.equals("")) {
							return super
									.redirectModelAndView(
											"/organization.do?method=showframe&style=list",
											"parent.parent");
						} else {
							return super
									.redirectModelAndView(
											"/organization.do?method=showframe&style=tree",
											"parent");
						}
					}
				}
				// 全部成功触发删除部门事件
				for (V3xOrgDepartment dept : deptList) {
					eventListener.deleteDepartment(dept);
					String[] appLog = new String[2];
					appLog[0] = user.getName();
					appLog[1] = dept.getName();
					appLogs.add(appLog);
				}
			}
		}

		// 记录日志
		appLogManager.insertLogs(user, AppLogAction.Organization_DeleteDept,
				appLogs);

		// 提示用户操作成功
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('" + Constant.getString("organization.yes", local)
				+ "');");
		out.println("</script>");
		out.flush();
		// 去掉上面的整个工作区刷新，改成按链接刷新。hr布局用
		if (islist != null && !islist.equals("")) {
			return super.redirectModelAndView(
					"/organization.do?method=showframe&style=list",
					"parent.parent");
		} else {
			return super.redirectModelAndView(
					"/organization.do?method=showframe&style=tree", "parent");
		}
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView showtree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		V3xOrgAccount account = orgManagerDirect.getAccountById(user
				.getLoginAccount());
		ModelAndView result = new ModelAndView(
				"organization/department/lefttree");
		List<V3xOrgDepartment> deptlist = new ArrayList<V3xOrgDepartment>();
		List<V3xOrgEntity> list = orgManagerDirect.getEntityListNoRelation(
				V3xOrgDepartment.class.getSimpleName(), "isInternal", true,
				account.getId());
		// 为了优化后面取父部门性能，此处取出的部门需要缓存到Map
		Map<String, V3xOrgDepartment> deptPathMap = new HashMap<String, V3xOrgDepartment>();
		for (V3xOrgEntity deptEnt : list) {
			V3xOrgDepartment dept = (V3xOrgDepartment) deptEnt;
			if (!dept.isValid()) {
				continue;
			}
			deptlist.add(dept);
			deptPathMap.put(dept.getPath(), dept);
		}
		Collections.sort(deptlist, CompareSortEntity.getInstance());
		List<WebV3xOrgDepartment> resultlist = new ArrayList<WebV3xOrgDepartment>();
		for (int i = 0; i < deptlist.size(); i++) {
			V3xOrgDepartment dept = (V3xOrgDepartment) deptlist.get(i);
			if (dept.getEnabled() == null || !dept.getEnabled()) {
				continue; // 不含停用的
			}
			dept.getCode();
			V3xOrgDepartment pdept = deptPathMap.get(dept.getParentPath());
			if (pdept != null
					&& (pdept.getEnabled() == null || !pdept.getEnabled())) {
				continue; // 父部门停用的本部门也应该是停用的，这里是为了处理已有的错误数据
			}
			WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
			webdept.setV3xOrgDepartment(dept);
			if (null != pdept) {
				webdept.setParentId(pdept.getId());
				webdept.setParentName(pdept.getName());
			}
			resultlist.add(webdept);
		}
		String member = request.getParameter("member");
		if ("member".equals(member)) {
			result.addObject("member", member);
		}
		String currentId = request.getParameter("currentId");
		if (Strings.isNotBlank(currentId)) {
			result.addObject("currentId", currentId);
		}
		String currentParentId = request.getParameter("currentParentId");
		if (Strings.isNotBlank(currentParentId)) {
			result.addObject("currentParentId", currentParentId);
		}
		String deptAdmin = request.getParameter("deptAdmin");
		if (deptAdmin != null) {
			result.addObject("deptAdmin", deptAdmin);
		}
		result.addObject("account", account);
		result.addObject("deptlist", resultlist);
		return result;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView showframe(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String style = request.getParameter("style");
		if (style.equals("tree")) {
			ModelAndView result = new ModelAndView(
					"organization/department/treeIndex");
			return result;
		} else {
			ModelAndView result = new ModelAndView(
					"organization/department/listIndex");
			return result;
		}
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView showmenu(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String style = request.getParameter("style");
		if (style.equals("tree")) {
			ModelAndView result = new ModelAndView(
					"organization/department/treeMenu");
			return result;
		} else {
			ModelAndView result = new ModelAndView(
					"organization/department/listMenu");
			return result;
		}
	}

	/**
	 * 进入单位管理的数据页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin, RoleType.Administrator,
			RoleType.HrAdmin })
	public ModelAndView listPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		User user = CurrentUser.get();
		ModelAndView result = new ModelAndView("organization/post/listPost");
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		List<V3xOrgPost> postlist = new ArrayList<V3xOrgPost>();
		try {
			// 根据查询条件查询岗位列表
			if (condition != null && Strings.isNotBlank(condition)
					&& textfield != null && Strings.isNotBlank(textfield)) {
				StringBuffer strbuf = new StringBuffer();
				Map<String, Object> param = new HashMap<String, Object>();
				strbuf.append("select a from " + V3xOrgPost.class.getName()
						+ " a where a.orgAccountId='" + user.getLoginAccount()
						+ "' and a.isDeleted='0'");
				if (condition.equals("typeId") && Strings.isNotBlank(textfield)) {
					strbuf.append(" and a.typeId = :textfield ");
					param.put("textfield", Long.parseLong(textfield));
					strbuf.append(" order by a.sortId asc");
					List<V3xOrgEntity> lst = searchManager.searchByHql(
							strbuf.toString(), param, true);
					for (V3xOrgEntity postIt : lst) {
						postlist.add((V3xOrgPost) postIt);
					}
				} else if (condition.equals("name")) {
					strbuf.append(" and a.name like :textfield ");
					param.put("textfield", "%" + textfield + "%");
					strbuf.append(" order by a.sortId asc");
					List<V3xOrgEntity> lst = searchManager.searchByHql(
							strbuf.toString(), param, true);
					for (V3xOrgEntity postIt : lst) {
						postlist.add((V3xOrgPost) postIt);
					}
				} else if (condition.equals("code")) {
					strbuf.append(" and a.code like :textfield");
					param.put("textfield", "%" + textfield + "%");
					strbuf.append(" order by a.sortId asc");
					List<V3xOrgEntity> lst = searchManager.searchByHql(
							strbuf.toString(), param, true);
					for (V3xOrgEntity postIt : lst) {
						postlist.add((V3xOrgPost) postIt);
					}
				}
				Collections.sort(postlist, CompareSortEntity.getInstance());
				result.addObject("condition", condition);
				result.addObject("textfield", textfield);
			} else {
				postlist = orgManagerDirect.getAllPosts(user.getLoginAccount());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		// 组装成页面格式
		List<WebV3xOrgPost> webPostlist = new ArrayList<WebV3xOrgPost>();
		for (V3xOrgPost post : postlist) {
			WebV3xOrgPost webPost = new WebV3xOrgPost();
			webPost.setV3xOrgPost(post);
			List<V3xOrgRelationship> rels = orgManagerDirect.getRelationships(
					"type", V3xOrgEntity.ORGREL_TYPE_BENCHMARK_POST,
					"sourceId", post.getId());
			if (rels != null && rels.size() > 0) {
				webPost.setPostType(String.valueOf(2));
			} else {
				webPost.setPostType(String.valueOf(1));
			}
			webPostlist.add(webPost);
		}
		result.addObject("postlist", webPostlist);

		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		result.addObject("orgMeta", orgMeta);
		// 集团单位的ID
		Long rootAccountId = orgManagerDirect.getRootAccount().getId();
		result.addObject("rootAccountId", rootAccountId);
		// 是否显示引用集团基准岗：条件1-是否是集团版，条件2-是否是独立单位
		V3xOrgAccount account = orgManagerDirect.getAccountById(user
				.getLoginAccount());
		if (Boolean.valueOf(SysFlag.org_showAddBmPost.getFlag().toString())
				&& account.getSuperior().equals(V3xOrgEntity.DEFAULT_NULL_ID)
				&& !account.getIsRoot()) {
			result.addObject("showAddBmPost", false);
		} else {
			result.addObject("showAddBmPost",
					SysFlag.org_showAddBmPost.getFlag());
		}

		return result;
	}

	/**
	 * 单位管理员引用基准岗
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView addBmPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		User user = CurrentUser.get();
		// 添加集团基准岗
		String postIds = request.getParameter("postIds");
		if (postIds != null && !StringUtils.isBlank(postIds)) {
			String[] postIdArray = postIds.split(",");
			if (postIdArray != null && postIdArray.length > 0) {
				// 判断岗位重名
				boolean ispd = false;
				String name = "";
				boolean isHavepd = false;
				List<Long> bmPostIds = new ArrayList<Long>();
				for (int i = 0; i < postIdArray.length; i++) {
					// 判断岗位重名
					Long postId = Long.parseLong(postIdArray[i]);
					V3xOrgPost post = orgManagerDirect.getPostById(postId);
					ispd = orgManagerDirect.isPropertyDuplicated(
							V3xOrgPost.class.getSimpleName(), "name",
							post.getName());
					if (ispd) {
						if (StringUtils.isBlank(name)) {
							name = post.getName();
						} else {
							name += ("," + post.getName());
						}
						isHavepd = true;
					} else {
						bmPostIds.add(postId);
					}
				}
				if (isHavepd) {
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert('"
							+ name
							+ "'+ parent.v3x.getMessage('organizationLang.organization_double_name_2'));");
					out.println("</script>");
					out.flush();
				}

				List<String[]> applogs = new ArrayList<String[]>();
				for (Long postId : bmPostIds) {
					orgManagerDirect.addBenchMarkPostRel(postId,
							user.getLoginAccount());
					V3xOrgPost bmPost = orgManagerDirect.getPostById(postId);
					String[] appLog = new String[2];
					appLog[0] = user.getName();
					appLog[1] = bmPost.getName();
					applogs.add(appLog);
				}

				// 记录日志
				appLogManager.insertLogs(user,
						AppLogAction.Organization_NewPost, applogs);

			}
		}
		return super.redirectModelAndView("/organization.do?method=listPost");
	}

	/**
	 * 单位管理员绑定集团基准岗
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView bandPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String postIds = request.getParameter("ids");
		if (postIds != null && !StringUtils.isBlank(postIds)) {
			String[] postIdArray = postIds.split(",");
			if (postIdArray != null && postIdArray.length > 0) {
				List<String> postNames = new ArrayList<String>();
				boolean isHavepd = false;
				// 取得所有基准岗，此处是为了避免循环sql
				List<V3xOrgPost> bmPosts = orgManagerDirect.getAllPosts(
						orgManagerDirect.getRootAccount().getId(), false);
				HashMap<String, V3xOrgPost> bmPostNames = new HashMap<String, V3xOrgPost>();
				for (V3xOrgPost bmPost : bmPosts) {
					bmPostNames.put(bmPost.getName(), bmPost);
				}
				Collection<String> bmPostNameList = bmPostNames.keySet();
				for (int i = 0; i < postIdArray.length; i++) {
					Long postId = Long.parseLong(postIdArray[i]);
					V3xOrgPost post = orgManagerDirect.getPostById(postId);
					// 以名称绑定集团基准岗
					if (bmPostNameList.contains(post.getName())) {
						// 判断同名基准岗有没有被停用
						V3xOrgPost bmPost = bmPostNames.get(post.getName());
						if (bmPost.getEnabled()) {
							// 绑定基准岗
							orgManagerDirect.bandBmPost(postId);
						} else {
							isHavepd = true;
							postNames.add(post.getName());
						}
					} else {
						isHavepd = true;
						// 没有匹配到基准岗
						postNames.add(post.getName());
					}
				}
				if (isHavepd) {
					PrintWriter out = response.getWriter();
					String postStr = "";
					for (String postName : postNames) {
						if (postStr == "") {
							postStr = postName;
						} else {
							postStr += "," + postName;
						}
					}
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('organizationLang.organization_band_bmpost_fail','"
							+ postStr + "'));");
					out.println("</script>");
					out.flush();
				}
			}
		}
		return super.redirectModelAndView("/organization.do?method=listPost");
	}

	/**
	 * 进入岗位管理的添加方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin, RoleType.Administrator,
			RoleType.HrAdmin })
	public ModelAndView addPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("organization/post/addPost");
		// 获得岗位的最大排序号
		Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgPost.class
				.getSimpleName(), CurrentUser.get().getLoginAccount());
		V3xOrgPost post = new V3xOrgPost();
		post.setEnabled(true);
		post.setSortId(maxSortNum + 1);
		result.addObject("post", post);
		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		result.addObject("orgMeta", orgMeta);

		return result;
	}

	/**
	 * 完成岗位管理的添加方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin, RoleType.Administrator,
			RoleType.HrAdmin })
	public ModelAndView createPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			User user = CurrentUser.get();
			V3xOrgPost post = new V3xOrgPost();
			bind(request, post);
			post.setOrgAccountId(user.getLoginAccount());
			// 如果是集团管理员登录则验证是否在集团内重名
			if (user.isGroupAdmin()) {
				List<V3xOrgEntity> entList = orgManagerDirect
						.getEntityListNoRelation(
								V3xOrgPost.class.getSimpleName(), "name",
								post.getName(), user.getLoginAccount());
				if (entList != null && entList.size() > 0) {
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_validate_post_name_same'));");
					out.println("</script>");
					super.rendJavaScript(response, "parent.toEditMember()");
					return null;
				}
			}
			// 岗位排序号的重复处理
			String isInsert = request.getParameter("isInsert");
			if (isInsert.equals("1")
					&& orgManagerDirect.isPropertyDuplicated(
							V3xOrgPost.class.getSimpleName(), "sortId",
							post.getSortId())) {
				orgManagerDirect.insertRepeatSortNum(V3xOrgPost.class
						.getSimpleName(), CurrentUser.get().getLoginAccount(),
						post.getSortId());
			}
			orgManager.addPost(post);
			// 更改枚举项引用
			if (null != post) {
				long itemValue = post.getTypeId();
				Metadata metadata = metadataManager
						.getMetadata(MetadataNameEnum.organization_post_types);
				if (null != metadata) {
					MetadataItem item = metadataManager.getMetadataItem(
							metadata.getName(), Long.valueOf(itemValue)
									.toString());
					if (null != item) {
						metadataManager.refMetadataItem(metadata.getId(), item
								.getId(), Long.valueOf(itemValue).intValue());
					}
				}
			}
			super.rendJavaScript(response, "parent.doEndPost('"
					+ CurrentUser.get().getLoginAccount() + "')");

			// 触发创建岗位事件
			eventListener.addPost(post);

			// 记录日志
			appLogManager.insertLog(user, AppLogAction.Organization_NewPost,
					user.getName(), post.getName());

			return null;
		} catch (BusinessException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 进入岗位管理的修改方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin, RoleType.Administrator,
			RoleType.HrAdmin })
	public ModelAndView editPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("organization/post/editPost");
		String id = request.getParameter("id");
		String postType = request.getParameter("postType");
		V3xOrgPost post = orgManagerDirect.getPostById(Long.parseLong(id));
		if (post == null || post.getIsDeleted()) {
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_null'));");
			out.println("</script>");
			out.flush();
			return super.refreshWorkspace();
		}
		result.addObject("post", post);
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
			result.addObject("preview", 0);
		} else {
			result.addObject("preview", 1);
		}

		result.addObject("postType", postType);

		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		result.addObject("orgMeta", orgMeta);
		return result;
	}

	/**
	 * 完成单位管理的修改方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin, RoleType.Administrator,
			RoleType.HrAdmin })
	public ModelAndView updatePost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 是否是单位管理员修改集团基准岗
		String postType = request.getParameter("postType");
		// 是否是集团管理员登录
		User user = CurrentUser.get();

		V3xOrgPost model = new V3xOrgPost();
		bind(request, model);
		V3xOrgPost post = orgManagerDirect.getPostById(model.getId());
		// 为事件调用记录修改前的岗位
		V3xOrgPost postBeforeUpdate = new V3xOrgPost();
		BeanUtils.copyProperties(postBeforeUpdate, post);

		// 排序号的重复处理
		Integer orgSortId = post.getSortId();
		Integer sortId = Integer.valueOf(request.getParameter("sortId"));
		String isInsert = request.getParameter("isInsert");
		if (!orgSortId.equals(sortId)
				&& isInsert.equals("1")
				&& orgManagerDirect.isPropertyDuplicated(
						V3xOrgPost.class.getSimpleName(), "sortId", sortId,
						post.getId())) {
			orgManagerDirect.insertRepeatSortNum(V3xOrgPost.class
					.getSimpleName(), CurrentUser.get().getLoginAccount(),
					sortId);
		}

		// 集团管理员登录修改基准岗后各单位都更新
		if (user.isGroupAdmin()) {
			List<V3xOrgRelationship> rels = orgManagerDirect.getRelationships(
					"type", V3xOrgEntity.ORGREL_TYPE_BENCHMARK_POST,
					"objectiveId", post.getId());
			List<V3xOrgPost> posts = new ArrayList<V3xOrgPost>();
			for (V3xOrgRelationship rel : rels) {
				V3xOrgPost accountPost = orgManagerDirect.getPostById(rel
						.getSourceId());
				posts.add(accountPost);
			}
			// 如果岗位由启用变为停用则需要查询所有单位的岗位下的人员
			if (postBeforeUpdate.getEnabled() && !model.getEnabled()) {
				for (V3xOrgPost accountPost : posts) {
					List<V3xOrgMember> mems = orgManagerDirect
							.getMembersByPost(accountPost.getId());
					if (mems != null && mems.size() > 0) {
						for (V3xOrgMember mem : mems) {
							if (mem.isValid()) {
								PrintWriter out = response.getWriter();
								out.println("<script>");
								out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_unenabled_bmpost_member'));");
								out.println("</script>");
								super.rendJavaScript(response,
										"parent.toEditMember()");
								return null;
							}
						}
					}
				}
			}
			// manager层已经更新
			/*
			 * for(V3xOrgPost accountPost:posts){ if(accountPost!=null){
			 * accountPost.setName(model.getName());
			 * accountPost.setCode(model.getCode());
			 * accountPost.setDesciption(model.getDesciption());
			 * accountPost.setTypeId(model.getTypeId());
			 * accountPost.setEnabled(model.getEnabled()); }
			 * orgManager.updatePost(accountPost); }
			 */
		}

		// 如果是单位管理员修改集团基准岗则只能改排序号
		if (!postType.equals(String.valueOf(2))) {
			BeanUtils.copyProperties(post, model);
		} else {
			post.setSortId(sortId);
		}
		orgManager.updatePost(post);

		if (null != post) {
			long itemValue = post.getTypeId();
			Metadata metadata = metadataManager
					.getMetadata(MetadataNameEnum.organization_post_types);
			if (null != metadata) {
				MetadataItem item = metadataManager.getMetadataItem(
						metadata.getName(), Long.valueOf(itemValue).toString());
				if (null != item) {
					metadataManager.refMetadataItem(metadata.getId(),
							item.getId(), Long.valueOf(itemValue).intValue());
				}
			}
		}
		// 触发更新岗位事件
		eventListener.updatePost(postBeforeUpdate, post);

		// 记录日志
		appLogManager.insertLog(user, AppLogAction.Organization_UpdatePost,
				user.getName(), post.getName());
		super.rendJavaScript(
				response,
				"parent.parent.detailFrame.location.href=\"/seeyon/common/detail.jsp\";parent.parent.listFrame.location.reload(true);");
		return null;

	}

	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin, RoleType.Administrator,
			RoleType.HrAdmin })
	public ModelAndView destroyPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String ids = request.getParameter("ids");
		String postTypes = request.getParameter("postTypes");
		User user = CurrentUser.get();
		Locale local = LocaleContext.getLocale(request);
		// 日志信息
		List<String[]> appLogs = new ArrayList<String[]>();

		if (null != ids && !ids.equals("")) {
			String[] arrIDs = ids.split(",");
			String[] strPostTypes = postTypes.split(",");
			if (null != arrIDs && arrIDs.length > 0) {
				List<V3xOrgPost> postList = new ArrayList<V3xOrgPost>(
						arrIDs.length);
				for (int i = 0; i < arrIDs.length; i++) {
					String strid = arrIDs[i];
					String postType = strPostTypes[i];
					Long id = Long.parseLong(strid);
					V3xOrgPost post = orgManagerDirect.getPostById(id);
					postList.add(post);
					// 判断岗位下是否有成员存在
					// 如果是集团基准岗则查询相关单位岗位下的人员
					if (user.isGroupAdmin()) {
						List<V3xOrgRelationship> rels = orgManagerDirect
								.getRelationships(
										"type",
										V3xOrgEntity.ORGREL_TYPE_BENCHMARK_POST,
										"objectiveId", post.getId());
						boolean enable2Del = true;
						for (V3xOrgRelationship rel : rels) {
							List<V3xOrgMember> mems = orgManagerDirect
									.getMembersByPost(rel.getSourceId());
							if (ListUtils.EMPTY_LIST.equals(mems)) {// 岗位下没有人，允许删除
								break;
							}
							for (V3xOrgMember e : mems) {// 岗位下没有有效的人员，允许删除
								if (e.isValid()) {
									enable2Del = false;
									break;
								}
							}
							if (!enable2Del) {
								break;
							}
						}
						if (!enable2Del) {
							PrintWriter out = response.getWriter();
							out.println("<script>");
							out.println("alert('"
									+ Constant
											.getString(
													"organization.delete.member",
													local) + "');");
							out.println("</script>");
							out.flush();
							return super
									.redirectModelAndView(
											"/organization.do?method=organizationFrame&from=Post",
											"parent");
						} else {
							orgManager.deletePost(post);
						}
						// 集团基准岗删除关联关系
						/*
						 * for(V3xOrgRelationship rel:rels){
						 * orgManagerDirect.deleteEntity(rel);
						 * orgManagerDirect.deleteEntity(V3xOrgPost.class,
						 * rel.getSourceId(), rel.getOrgAccountId()); }
						 */
					} else {
						List<V3xOrgMember> members = orgManagerDirect
								.getMembersByPost(id);
						boolean isAllMemberUnEnabled = true;
						for (V3xOrgMember mem : members) {
							if (mem.isValid()) {
								isAllMemberUnEnabled = false;
								break;
							}
						}
						if (ListUtils.EMPTY_LIST.equals(members)
								|| isAllMemberUnEnabled) {
							orgManager.deletePost(post);
							// 集团基准岗删除关联关系
							/*
							 * if(postType.equals(String.valueOf(2))){
							 * List<V3xOrgRelationship> rels =
							 * orgManagerDirect.getRelationships
							 * ("type",V3xOrgEntity
							 * .ORGREL_TYPE_BENCHMARK_POST,"sourceId",id);
							 * orgManagerDirect.deleteEntity(rels.get(0)); }
							 */
						} else {
							PrintWriter out = response.getWriter();
							out.println("<script>");
							out.println("alert('"
									+ Constant
											.getString(
													"organization.delete.member",
													local) + "');");
							out.println("</script>");
							out.flush();
							return super
									.redirectModelAndView(
											"/organization.do?method=organizationFrame&from=Post",
											"parent");
						}
					}
				}
				// 全部成功后触发创建部门事件
				for (V3xOrgPost post : postList) {
					eventListener.deletePost(post);
					String[] appLog = new String[2];
					appLog[0] = user.getName();
					appLog[1] = post.getName();
					appLogs.add(appLog);
				}
				// 记录日志
				appLogManager.insertLogs(user,
						AppLogAction.Organization_DeletePost, appLogs);
			}
		}

		// 提示用户操作成功
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('" + Constant.getString("organization.yes", local)
				+ "');");
		out.println("</script>");

		return redirectModelAndView("/organization.do?method=listPost");
	}

	/**
	 * 进入组织模型的上下结构部分
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin, RoleType.Administrator,
			RoleType.HrAdmin, RoleType.DepartmentAdmin })
	public ModelAndView organizationFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String from = request.getParameter("group");
		String isTree = request.getParameter("isTree");
		String mav = "";
		if (!Strings.isBlank(from) && from.equals("true")) {
			mav = "organization/account/tree/groupFrame";
		} else {
			mav = "organization/organizationFrame";
		}
		String fromFlag = request.getParameter("from");
		ModelAndView result = new ModelAndView(mav);
		if ("Member".equals(fromFlag)) {
			result.addObject("member", "member");
		}
		if (isTree != null && !"".equals(isTree)) {
			result.addObject("isTree", isTree);
		} else {
			result.addObject("isTree", "0");
		}
		return result;
	}

	public ModelAndView organizationDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"organization/organizationDetail");
		// String _form = request.getParameter("from");
		// result.addObject("from", _form);
		return result;
	}

	/**
	 * 进入组织模型的职务级别管理界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView listLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView result = new ModelAndView("organization/level/listLevel");
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		List<V3xOrgLevel> levellist = null;
		if (condition != null && !"".equals(condition)) {
			levellist = orgManager.getAllLevels(user.getLoginAccount(),
					condition, textfield);
		} else {
			levellist = orgManagerDirect.getAllLevels(user.getLoginAccount());
		}
		List<WebV3xOrgLevel> levellst = new ArrayList<WebV3xOrgLevel>();
		// 将职务级别重新封装
		for (V3xOrgLevel level : levellist) {
			WebV3xOrgLevel webLevel = new WebV3xOrgLevel();
			webLevel.setV3xOrgLevel(level);
			if (level.getGroupLevelId() != null) {
				V3xOrgLevel levelFor = orgManagerDirect.getLevelById(level
						.getGroupLevelId());
				if (levelFor != null) {
					webLevel.setGroupLevelId(levelFor.getLevelId());
				}
			}
			levellst.add(webLevel);
		}
		result.addObject("levellist", levellst);
		// 是否显示集团职务级别映射
		V3xOrgAccount account = orgManagerDirect.getAccountById(CurrentUser
				.get().getLoginAccount());
		if (Boolean.valueOf(SysFlag.org_showGroupLevelMap.getFlag().toString())
				&& account.getSuperior().equals(V3xOrgEntity.DEFAULT_NULL_ID)
				&& !account.getIsRoot()) {
			result.addObject("showGroupLevel", false);
		} else {
			result.addObject("showGroupLevel",
					SysFlag.org_showGroupLevelMap.getFlag());
		}
		result.addObject("condition", condition);
		result.addObject("textfield", textfield);
		return result;
	}

	/**
	 * 进入集团职务级别管理界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView listGroupLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView result = new ModelAndView(
				"organization/level/listGroupLevel");
		List<V3xOrgLevel> levellist = orgManagerDirect.getAllLevels(user
				.getLoginAccount());
		result.addObject("levellist", levellist);
		return result;
	}

	/**
	 * 根据查询条件查询集团职务级别
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView queryGroupLevelByCondition(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView view = new ModelAndView(
				"organization/level/listGroupLevel");
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");

		List<V3xOrgLevel> levelList = null;
		List<V3xOrgLevel> result_list = new ArrayList<V3xOrgLevel>();
		StringBuilder sbHQL = new StringBuilder();
		Map<String, Object> param = new HashMap<String, Object>();

		// String hql =
		// "select a from "+V3xOrgAccount.class.getName()+" a where a.superior = -1 and a.isRoot=1";
		// List<V3xOrgAccount> voaList = searchManager.searchByHql(hql, null,
		// false);
		// if (!voaList.isEmpty()) {
		// V3xOrgAccount account = voaList.get(0);
		User user = CurrentUser.get();
		// 按名称查询
		if (condition != null && "name".equals(condition)) {
			sbHQL.append("select v from ")
					.append(V3xOrgLevel.class.getName())
					.append(" v where v.isDeleted=0 and v.orgAccountId=:accountId");
			if (textfield != null && textfield.trim().length() > 0) {
				sbHQL.append(" and v.name like :textfield");
				param.put("textfield", "%" + textfield + "%");
			}
			view.addObject("condition", condition);
			view.addObject("textfield", textfield);
		}
		// 根据是否启用查询
		else if (condition != null && "status".equals(condition)) {
			String status = request.getParameter("status");
			int enabled = 0;
			if (StringUtils.isNotBlank(status)) {
				enabled = Integer.valueOf(status);
			}
			sbHQL.append("select v from ").append(V3xOrgLevel.class.getName())
					.append(" v where v.enabled=").append(enabled)
					.append(" and v.isDeleted=0 and v.orgAccountId=:accountId");
			view.addObject("condition", condition);
			view.addObject("status", status);
		}
		// 无查询条件 查询所有
		else if (condition != null && "choice".equals(condition)) {
			sbHQL.append("select v from ")
					.append(V3xOrgLevel.class.getName())
					.append(" v where v.isDeleted=0 and v.orgAccountId=:accountId");
			view.addObject("condition", condition);
		}
		param.put("accountId", user.getLoginAccount());
		// }

		if (sbHQL.toString() != null)
			levelList = searchManager.searchByHql(sbHQL.toString(), param,
					false);

		// 此处为对分页做的处理
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(levelList.size());
		for (int i = first; i < first + pageSize; i++) {
			if (i > levelList.size() - 1)
				break;
			result_list.add(levelList.get(i));
		}

		// 保存结果集
		view.addObject("levellist", result_list);

		// 是否显示集团职务级别映射
		V3xOrgAccount account = orgManagerDirect.getAccountById(CurrentUser
				.get().getLoginAccount());
		if (Boolean.valueOf(SysFlag.org_showGroupLevelMap.getFlag().toString())
				&& account.getSuperior().equals(V3xOrgEntity.DEFAULT_NULL_ID)
				&& !account.getIsRoot()) {
			view.addObject("showGroupLevel", false);
		} else {
			view.addObject("showGroupLevel",
					SysFlag.org_showGroupLevelMap.getFlag());
		}

		return view;
	}

	/**
	 * 导出职务级别方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView exportLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		// ModelAndView result = new
		// ModelAndView("organization/level/listLevel");
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		String level_list = ResourceBundleUtil.getString(resource, local,
				"org.level_form.list");
		DataRecord dataRecord = OrganizationHelper.exportLevel(request,
				response, fileToExcelManager, orgManagerDirect);
		OrganizationHelper.exportToExcel(request, response, fileToExcelManager,
				level_list + "-" + user.getLoginName(), dataRecord);
		return null;
	}

	/**
	 * 进入添加职务级别管理方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView addLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("organization/level/addLevel");
		V3xOrgLevel level = new V3xOrgLevel();
		level.setEnabled(true);
		result.addObject("level", level);
		// 取得集团职务级别
		List<V3xOrgLevel> groupLevellist = orgManagerDirect.getAllLevels(
				orgManagerDirect.getRootAccount().getId(), false);
		// 过滤无效的级别
		List<V3xOrgLevel> groupLevellistForPage = new ArrayList<V3xOrgLevel>();
		for (V3xOrgLevel levelForPage : groupLevellist) {
			if (levelForPage.getEnabled()) {
				groupLevellistForPage.add(levelForPage);
			}
		}
		result.addObject("groupLevellist", groupLevellistForPage);
		// 是否显示集团职务级别映射
		V3xOrgAccount account = orgManagerDirect.getAccountById(CurrentUser
				.get().getLoginAccount());
		if (Boolean.valueOf(SysFlag.org_showGroupLevelMap.getFlag().toString())
				&& account.getSuperior().equals(V3xOrgEntity.DEFAULT_NULL_ID)
				&& !account.getIsRoot()) {
			result.addObject("showGroupLevel", false);
		} else {
			result.addObject("showGroupLevel",
					SysFlag.org_showGroupLevelMap.getFlag());
		}
		return result;
	}

	/**
	 * 进入添加集团职务级别管理方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView addGroupLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"organization/level/addGroupLevel");
		V3xOrgLevel level = new V3xOrgLevel();
		level.setEnabled(true);
		result.addObject("level", level);
		return result;
	}

	/**
	 * 添加职务级别方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView createLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		V3xOrgLevel level = new V3xOrgLevel();
		bind(request, level);
		level.setOrgAccountId(user.getLoginAccount());
		PrintWriter out = response.getWriter();
		Locale local = LocaleContext.getLocale(request);
		boolean continues = request.getParameterValues("cont") != null;
		// 判断集团职务级别映射
		String groupLevelIdStr = request.getParameter("groupLevelId");
		Integer groupLevelId = V3xOrgEntity.MAX_LEVEL_NUM;
		if (StringUtils.isNotBlank(groupLevelIdStr)) {
			groupLevelId = orgManagerDirect.getLevelById(
					level.getGroupLevelId()).getLevelId();
		}
		boolean isMapRight = orgManagerDirect.isGroupLevelMapRight(
				user.getLoginAccount(), level.getLevelId(), groupLevelId);
		if (!isMapRight) {
			V3xOrgLevel errorLevel = orgManagerDirect.getErrorMapLevel(
					user.getLoginAccount(), level.getLevelId(), groupLevelId);
			if (errorLevel.getLevelId().intValue() > level.getLevelId()
					.intValue()) {
				out.println("<script>");
				// branches_a8_v350_r_gov GOV-1855
				// lijl添加判断,如果是政务版本,则以政务的提示信息提示,否则以原来A8的信息提示----------Start
				out.println("alert('"
						+ Constant.getString(
								"level.map.group.low" + Functions.suffix(),
								local, errorLevel.getName()) + "');");
				// branches_a8_v350_r_gov GOV-1855
				// lijl添加判断,如果是政务版本,则以政务的提示信息提示,否则以原来A8的信息提示----------End
				out.println("</script>");
			} else {
				out.println("<script>");
				// branches_a8_v350_r_gov GOV-1855
				// lijl添加判断,如果是政务版本,则以政务的提示信息提示,否则以原来A8的信息提示----------Start
				out.println("alert('"
						+ Constant.getString(
								"level.map.group.up" + Functions.suffix(),
								local, errorLevel.getName()) + "');");
				// branches_a8_v350_r_gov GOV-1855
				// lijl添加判断,如果是政务版本,则以政务的提示信息提示,否则以原来A8的信息提示----------End
				out.println("</script>");
			}
			super.rendJavaScript(response, "parent.toEditMember()");
			return redirectModelAndView("/organization.do?method=organizationFrame&from=Level&addOrganiza=addLevel&cont="
					+ continues);
		} else {
			orgManager.addLevel(level);
			super.rendJavaScript(response, "parent.doEndLevel();");

			// 触发创建职务级别事件
			eventListener.addLevel(level);

			// 记录日志
			appLogManager.insertLog(user, AppLogAction.Organization_NewLevel,
					user.getName(), level.getName());
			return null;
		}
	}

	/**
	 * 添加集团职务级别方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView createGroupLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		V3xOrgLevel level = new V3xOrgLevel();
		bind(request, level);
		level.setOrgAccountId(user.getLoginAccount());
		orgManager.addLevel(level);
		super.rendJavaScript(response, "parent.doEndLevel();");

		// 触发创建职务级别事件
		eventListener.addLevel(level);

		// 记录日志
		appLogManager.insertLog(user, AppLogAction.Organization_NewLevel,
				user.getName(), level.getName());
		return null;
	}

	/**
	 * 进入编辑职务级别方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView editLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("organization/level/editLevel");
		String id = request.getParameter("id");
		V3xOrgLevel level = orgManagerDirect.getLevelById(Long.parseLong(id));
		result.addObject("level", level);
		// 取得集团职务级别
		List<V3xOrgLevel> groupLevellist = orgManagerDirect.getAllLevels(
				orgManagerDirect.getRootAccount().getId(), false);
		// 过滤无效的级别
		List<V3xOrgLevel> groupLevellistForPage = new ArrayList<V3xOrgLevel>();
		for (V3xOrgLevel levelForPage : groupLevellist) {
			if (levelForPage.getEnabled()) {
				groupLevellistForPage.add(levelForPage);
			}
		}
		result.addObject("groupLevellist", groupLevellistForPage);
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
			result.addObject("preview", 0);
		} else {
			result.addObject("preview", 1);
		}

		// 是否显示集团职务级别映射
		V3xOrgAccount account = orgManagerDirect.getAccountById(CurrentUser
				.get().getLoginAccount());
		if (Boolean.valueOf(SysFlag.org_showGroupLevelMap.getFlag().toString())
				&& account.getSuperior().equals(V3xOrgEntity.DEFAULT_NULL_ID)
				&& !account.getIsRoot()) {
			result.addObject("showGroupLevel", false);
		} else {
			result.addObject("showGroupLevel",
					SysFlag.org_showGroupLevelMap.getFlag());
		}

		return result;
	}

	/**
	 * 进入编辑集团职务级别方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView editGroupLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"organization/level/editGroupLevel");
		String id = request.getParameter("id");
		V3xOrgLevel level = orgManagerDirect.getLevelById(Long.parseLong(id));
		result.addObject("level", level);
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
			result.addObject("preview", 0);
		} else {
			result.addObject("preview", 1);
		}
		return result;
	}

	/**
	 * 对职务级别进行编辑
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView updateLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		V3xOrgLevel model = new V3xOrgLevel();
		bind(request, model);
		V3xOrgLevel level = orgManagerDirect.getLevelById(model.getId());
		// 为事件调用记录修改前的职务级别
		V3xOrgLevel levelBeforeUpdate = new V3xOrgLevel();
		BeanUtils.copyProperties(levelBeforeUpdate, level);

		PrintWriter out = response.getWriter();
		Locale local = LocaleContext.getLocale(request);
		// 判断集团职务级别映射
		String groupLevelIdStr = request.getParameter("groupLevelId");
		Integer groupLevelId = V3xOrgEntity.MAX_LEVEL_NUM;
		if (StringUtils.isNotBlank(groupLevelIdStr)) {
			groupLevelId = orgManagerDirect.getLevelById(
					model.getGroupLevelId()).getLevelId();
		}
		boolean isMapRight = orgManagerDirect.isGroupLevelMapRight(CurrentUser
				.get().getLoginAccount(), model.getLevelId(), groupLevelId);
		if (!isMapRight) {
			/*
			 * V3xOrgLevel errorLevel =
			 * orgManagerDirect.getErrorMapLevel(CurrentUser
			 * .get().getLoginAccount(), model.getLevelId(), groupLevelId);
			 * if(errorLevel
			 * .getLevelId().intValue()>level.getLevelId().intValue()){
			 * out.println("<script>"); out.println("alert('"+
			 * Constant.getString
			 * ("level.map.group.low",local,errorLevel.getName())+"');");
			 * out.println("</script>"); }else{ out.println("<script>");
			 * out.println("alert('"+
			 * Constant.getString("level.map.group.up",local
			 * ,errorLevel.getName())+"');"); out.println("</script>"); }
			 * super.rendJavaScript(response, "parent.toEditMember()"); return
			 * null;
			 */
			V3xOrgLevel errorLevel = orgManagerDirect.getErrorMapLevel(
					CurrentUser.get().getLoginAccount(), model.getLevelId(),
					groupLevelId);
			String str = "";
			if (errorLevel.getLevelId().intValue() > level.getLevelId()
					.intValue()) {
				// branches_a8_v350_r_gov GOV-1855
				// lijl添加判断,如果是政务版本,则以政务的提示信息提示,否则以原来A8的信息提示----------Start
				str = "alert('"
						+ Constant.getString(
								"level.map.group.low" + Functions.suffix(),
								local, errorLevel.getName()) + "');";

			} else {
				// branches_a8_v350_r_gov GOV-1855
				// lijl添加判断,如果是政务版本,则以政务的提示信息提示,否则以原来A8的信息提示----------Start
				str = "alert('"
						+ Constant.getString(
								"level.map.group.up" + Functions.suffix(),
								local, errorLevel.getName()) + "');";

			}
			super.rendJavaScript(response, "parent.toEditMember();" + str);
			return null;
		} else {
			BeanUtils.copyProperties(level, model);
			orgManager.updateLevel(level);
			// 触发更新职务级别事件
			eventListener.updateLevel(levelBeforeUpdate, level);

			// 记录日志
			User user = CurrentUser.get();
			appLogManager.insertLog(user,
					AppLogAction.Organization_UpdateLevel, user.getName(),
					level.getName());
			// 提示用户操作成功
			super.rendJavaScript(response, "parent.doEndLevel()");
			return null;
		}
	}

	/**
	 * 对集团职务级别进行编辑
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView updateGroupLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		V3xOrgLevel model = new V3xOrgLevel();
		bind(request, model);
		V3xOrgLevel level = orgManagerDirect.getLevelById(model.getId());
		PrintWriter out = response.getWriter();
		Locale local = LocaleContext.getLocale(request);
		// 校验职务级别映射
		List<V3xOrgEntity> levelList = orgManagerDirect.getEntityList(
				V3xOrgLevel.class.getSimpleName(), "groupLevelId",
				model.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		for (V3xOrgEntity ent : levelList) {
			V3xOrgLevel levelIt = (V3xOrgLevel) ent;
			boolean isMapRight = orgManagerDirect.isGroupLevelMapRight(
					levelIt.getOrgAccountId(), levelIt.getLevelId(),
					model.getLevelId());
			if (!isMapRight) {
				V3xOrgLevel errorLevel = orgManagerDirect.getErrorMapLevel(
						levelIt.getOrgAccountId(), levelIt.getLevelId(),
						model.getLevelId());
				if (errorLevel.getLevelId().intValue() > levelIt.getLevelId()
						.intValue()) {
					out.println("<script>");
					out.println("alert('"
							+ Constant.getString(
									"level.map.update.group.down",
									local,
									errorLevel.getName(),
									levelIt.getName(),
									orgManagerDirect.getAccountById(
											levelIt.getOrgAccountId())
											.getShortname()) + "');");
					out.println("</script>");
				} else {
					out.println("<script>");
					out.println("alert('"
							+ Constant.getString(
									"level.map.update.group.up",
									local,
									errorLevel.getName(),
									levelIt.getName(),
									orgManagerDirect.getAccountById(
											levelIt.getOrgAccountId())
											.getShortname()) + "');");
					out.println("</script>");
				}
				super.rendJavaScript(response, "parent.doEndLevel()");
				return null;
			}
		}
		BeanUtils.copyProperties(level, model);
		orgManager.updateLevel(level);

		// 记录日志
		User user = CurrentUser.get();
		appLogManager.insertLog(user, AppLogAction.Organization_UpdateLevel,
				user.getName(), level.getName());
		// 提示用户操作成功
		out.println("<script>");
		out.println("alert('" + Constant.getString("organization.yes", local)
				+ "');");
		out.println("</script>");
		super.rendJavaScript(response, "parent.doEndLevel()");
		return null;
	}

	/**
	 * 删除职务级别
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView destroyLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String ids = request.getParameter("id");
		Locale local = LocaleContext.getLocale(request);
		// 日志信息
		List<String[]> appLogs = new ArrayList<String[]>();
		User user = CurrentUser.get();
		if (null != ids && !ids.equals("")) {
			String[] arrID = ids.split(",");
			if (null != arrID && arrID.length > 0) {

				List<V3xOrgLevel> levelList = new ArrayList<V3xOrgLevel>();

				for (String strID : arrID) {
					Long id = Long.parseLong(strID);
					V3xOrgLevel level = orgManagerDirect.getLevelById(id);
					levelList.add(level);

					// 判断职务级别下是否有成员存在
					List<V3xOrgEntity> members = orgManagerDirect
							.getEntityList(V3xOrgMember.class.getSimpleName(),
									"orgLevelId", id, level.getOrgAccountId());
					boolean isAllMemberUnEnabled = true;
					for (V3xOrgEntity mem : members) {
						if (mem.isValid()) {
							isAllMemberUnEnabled = false;
						}
					}
					if (ListUtils.EMPTY_LIST.equals(members)
							|| isAllMemberUnEnabled) {
						orgManager.deleteLevel(level);
					} else {
						PrintWriter out = response.getWriter();
						out.println("<script>");
						out.println("alert('"
								+ Constant.getString(
										"organization.delete.member", local)
								+ "');");
						out.println("</script>");
						out.flush();
						return super
								.redirectModelAndView(
										"/organization.do?method=organizationFrame&from=Level",
										"parent");
					}
				}

				// 全部成功后触发删除职务级别事件
				for (V3xOrgLevel level : levelList) {
					eventListener.deleteLevel(level);
					String[] appLog = new String[2];
					appLog[0] = user.getName();
					appLog[1] = level.getName();
					appLogs.add(appLog);
				}
				// 记录日志
				appLogManager.insertLogs(user,
						AppLogAction.Organization_DeleteLevel, appLogs);
			}
		}
		// 提示用户操作成功
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('" + Constant.getString("organization.yes", local)
				+ "');");
		out.println("</script>");
		return redirectModelAndView("/organization.do?method=listLevel");
	}

	/**
	 * 删除集团职务级别
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView destroyGroupLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String ids = request.getParameter("id");
		Locale local = LocaleContext.getLocale(request);
		// 日志信息
		List<String[]> appLogs = new ArrayList<String[]>();
		User user = CurrentUser.get();
		if (null != ids && !ids.equals("")) {
			String[] arrID = ids.split(",");
			if (null != arrID && arrID.length > 0) {

				List<V3xOrgLevel> levelList = new ArrayList<V3xOrgLevel>();
				for (String strID : arrID) {
					Long id = Long.parseLong(strID);
					V3xOrgLevel level = orgManagerDirect.getLevelById(id);
					levelList.add(level);
					orgManager.deleteLevel(level);
				}
				// 全部成功后触发删除职务级别事件
				for (V3xOrgLevel level : levelList) {
					eventListener.deleteLevel(level);
					String[] appLog = new String[2];
					appLog[0] = user.getName();
					appLog[1] = level.getName();
					appLogs.add(appLog);
				}
				// 记录日志
				appLogManager.insertLogs(user,
						AppLogAction.Organization_DeleteLevel, appLogs);
			}
		}

		// 提示用户操作成功
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('" + Constant.getString("organization.yes", local)
				+ "');");
		out.println("</script>");

		return redirectModelAndView("/organization.do?method=listGroupLevel");
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin,
			RoleType.DepartmentAdmin })
	public ModelAndView banchEdit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("organization/member/banchEdit");
		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		result.addObject("orgMeta", orgMeta);
		// 获取职务级别列表
		List<V3xOrgLevel> levels = orgManagerDirect.getAllLevels(CurrentUser
				.get().getLoginAccount(), false);
		List<V3xOrgLevel> levelsForPage = new ArrayList<V3xOrgLevel>();
		// 过滤无效项
		for (V3xOrgLevel levelForPage : levels) {
			if (levelForPage.getEnabled())
				levelsForPage.add(levelForPage);
		}
		String deptAdmin = request.getParameter("deptAdmin");
		result.addObject("deptAdmin", deptAdmin);
		// 如果是部门管理员登录，获取部门管理员能管理的部门,拼成字符串
		if (deptAdmin != null && deptAdmin.equals("1")) {
			result.addObject("depsPathStr", getDeptPathsForAdmin(CurrentUser
					.get().getId()));
		}
		result.addObject("levels", levelsForPage);
		String ids = request.getParameter("ids");
		if (ids != null) {
			result.addObject("ids", ids);
		}
		return result;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator,
			RoleType.DepartmentAdmin, RoleType.HrAdmin })
	public ModelAndView showMemberList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("organization/member/listMember");
		String strId = request.getParameter("id");
		String accountId = request.getParameter("accountId");
		List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>();
		if ("dept".equals(request.getParameter("dept"))) {
			if (strId != null && strId.length() != 0 && accountId != null
					&& accountId.length() != 0) {
				memberlist = OrganizationHelper.searchMember("orgDepartmentId",
						strId, searchManager, orgManagerDirect, false, true,
						false);
			}
		} else {
			if (accountId != null && accountId.length() != 0) {
				memberlist = OrganizationHelper.searchMember(null, null,
						searchManager, orgManagerDirect, false, true, false);
			}
		}
		List<WebV3xOrgMember> resultlist = new ArrayList<WebV3xOrgMember>();
		long deptId = -1;
		long levelId = -1;
		long postId = -1;
		if (null != memberlist) {
			for (Object memberObj : memberlist) {
				V3xOrgMember member = (V3xOrgMember) memberObj;
				if (!member.isValid())
					continue;// Fix AEIGHT-5978 2012-05-10 lilong
								// 修改BUG点击某部门不显示部门下的人员列表将离职、停用等无效人员剔除出列表
				deptId = member.getOrgDepartmentId();
				levelId = member.getOrgLevelId();
				postId = member.getOrgPostId();

				WebV3xOrgMember webMember = new WebV3xOrgMember();
				webMember.setV3xOrgMember(member);
				V3xOrgDepartment dept = orgManagerDirect
						.getDepartmentById(deptId);
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
				showLdapLoginName(result, member, webMember);
				resultlist.add(webMember);
			}
		}
		// 分页
		resultlist = pagenate(resultlist);
		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		result.addObject("orgMeta", orgMeta);
		result.addObject("memberlist", resultlist);
		result.addObject("condition", "orgDepartmentId");
		result.addObject("textfield", strId);

		return result;
	}

	/**
	 * 组装LDAP/AD帐号
	 * 
	 * @param result
	 * @param member
	 * @param webMember
	 */
	private void showLdapLoginName(ModelAndView result, V3xOrgMember member,
			WebV3xOrgMember webMember) {
		if (LDAPConfig.getInstance().getIsEnableLdap()
				&& SystemEnvironment
						.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
								.name())) {
			try {
				webMember.setStateName(event.getLdapAdLoginName(member
						.getLoginName()));
				result.addObject("hasLDAPAD", true);
			} catch (Exception e) {
				log.error("ldap/ad 显示ldap帐号！", e);
			}

		}
	}

	/**
	 * 进入人员管理数据方法，对应角色：单位管理员、HR管理员
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView listMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return doListMember(request, response, null);
	}

	/**
	 * 进入人员管理数据方法，对应角色：部门管理员
	 */
	@CheckRoleAccess(roleTypes = RoleType.DepartmentAdmin)
	public ModelAndView listMember4Dept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return doListMember(request, response, "1");
	}

	private ModelAndView doListMember(HttpServletRequest request,
			HttpServletResponse response, String deptAdmin) throws Exception {
		ModelAndView result = new ModelAndView("organization/member/listMember");
		if ("menu".equals(request.getParameter("menu"))) {
			result = new ModelAndView("organization/member/listMemberMenu");
		}
		User user = CurrentUser.get();
		try {
			String type = request.getParameter("type");
			String condition = request.getParameter("condition");
			String textfield = request.getParameter("textfield");
			boolean isOnlyEnableMem = true;
			if (Strings.isNotBlank(condition) && Strings.isNotBlank(textfield)) {
				isOnlyEnableMem = false;
			}
			List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>();
			// 如果是部门管理员而不是hr管理员登录,则查询本部门及子部门的人员
			if (deptAdmin != null && deptAdmin.equals("1")) {
				List<V3xOrgMember> memList = new ArrayList<V3xOrgMember>();
				List<V3xOrgMember> mems = OrganizationHelper.searchMember(
						condition, textfield, searchManager, orgManagerDirect,
						isOnlyEnableMem, true, true);
				List<V3xOrgMember> memberlistForDeptAdmin = new ArrayList<V3xOrgMember>();
				V3xOrgRole depAdminRole = orgManager
						.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPADMIN);
				List<V3xOrgRelationship> rels = orgManagerDirect
						.getRelationships("type",
								V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE,
								"backupId", depAdminRole.getId(), "sourceId",
								user.getId());
				if (rels != null && rels.size() > 0) {
					for (V3xOrgRelationship rel : rels) {
						List<V3xOrgMember> deptMembers = orgManager
								.getMembersByDepartment(rel.getObjectiveId(),
										false, !isOnlyEnableMem,
										rel.getOrgAccountId());
						if (deptMembers != null && deptMembers.size() > 0) {
							for (V3xOrgMember deptMem : deptMembers) {
								if (!memberlistForDeptAdmin.contains(deptMem)
										&& deptMem.getIsInternal()) {
									if (isOnlyEnableMem) {
										if (deptMem.isValid())
											memberlistForDeptAdmin.add(deptMem);
									} else {
										memberlistForDeptAdmin.add(deptMem);
									}
								}
							}
						}
					}
				}
				if (Strings.isNotBlank(condition)
						&& Strings.isNotBlank(textfield)) {
					for (V3xOrgMember mem : mems) {
						if (memberlistForDeptAdmin.contains(mem)) {
							memList.add(mem);
						}
					}
				} else {
					memList = memberlistForDeptAdmin;
				}
				// 分页
				memberlist = pagenate(memList);
			} else {
				memberlist = OrganizationHelper.searchMember(type, condition,
						textfield, searchManager, orgManagerDirect, true, true,
						true);
			}
			Collections.sort(memberlist, CompareSortEntity.getInstance());
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
					V3xOrgDepartment dept = orgManagerDirect
							.getDepartmentById(deptId);
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
					showLdapLoginName(result, member, webMember);
					resultlist.add(webMember);
				}
			}
			result.addObject("memberlist", resultlist);
			// 判断是什么版本
			boolean showAssign = (Boolean) (SysFlag.org_showGroupAccountAssign
					.getFlag());
			result.addObject("showAssign", showAssign);
			// 判断是否含有NC插件
			boolean hasNC = SystemEnvironment.hasPlugin("nc");
			// 是否是否是部门管理员
			result.addObject("deptAdmin", deptAdmin);
			result.addObject("hasNC", hasNC);
			// 获得单位类别下拉列表中的数据
			Map<String, Metadata> orgMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.organization);
			result.addObject("orgMeta", orgMeta);
			result.addObject("condition", condition);
			result.addObject("textfield", textfield);
			return result;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private String clientAbortExceptionName = "ClientAbortException";

	@CheckRoleAccess(roleTypes = { RoleType.Administrator,
			RoleType.DepartmentAdmin, RoleType.HrAdmin })
	public ModelAndView downloadTemplate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String type = request.getParameter("type"); // member, post
		String path = "";
		String filename = "";

		response.setContentType("application/x-msdownload; charset=UTF-8");

		if (type.equals(V3xOrgEntity.ORGENT_TYPE_MEMBER)) {
			// branches_a8_v350sp1_r_gov GOV-2506 常屹 添加
			// GOV-4896 【单位管理-人员管理】单位管理员登录系统，切换到人员管理那里，导出excel，页面是"职务"，
			// 但是导出excel是职务级别。还有导出模板，excel中也是职务级别。这两个修改一下。
			boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag();
			if (isGovVersion) {
				path = SystemEnvironment.getA8ApplicationFolder()
						+ "/apps_res/edoc/file/orgnization/memberGov";
			} else {
				path = SystemEnvironment.getA8ApplicationFolder()
						+ "/apps_res/edoc/file/orgnization/member";
			}
			filename = URLEncoder.encode("member.xls", "UTF-8");

		} else if (type.equals(V3xOrgEntity.ORGENT_TYPE_POST)) {
			path = SystemEnvironment.getA8ApplicationFolder()
					+ "/apps_res/edoc/file/orgnization/post";
			filename = URLEncoder.encode("post.xls", "UTF-8");
		}

		response.setHeader("Content-disposition", "attachment;filename=\""
				+ filename + "\"");

		OutputStream out = null;
		InputStream in = null;
		try {
			in = new FileInputStream(new File(path));
			out = response.getOutputStream();

			IOUtils.copy(in, out);
		} catch (Exception e) {
			if (e.getClass().getSimpleName()
					.equals(this.clientAbortExceptionName)) {
				log.debug("用户关闭下载窗口: " + e.getMessage());
			} else {
				log.error("", e);
			}
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}

		return null;

	}

	/**
	 * 进入人员管理数据方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator,
			RoleType.DepartmentAdmin, RoleType.HrAdmin })
	public ModelAndView queryMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("organization/member/listMember");

		/**
		 * 得到查询的类型 allmember 全部 incumbency 在职人员 dimission 离职人员 enable 启用帐号
		 * disable 停用帐号 interior 内部人员 exterior 外部人员
		 */
		// 判断是否是部门管理员登录
		String deptAdmin = request.getParameter("deptAdmin");
		String type = request.getParameter("type");
		if (type == null || "".equals(type) && "null".equals(type)) {
			type = "";
		}
		User user = CurrentUser.get();
		// 取得所有的岗位,以便初始化查询条件的岗位下拉列表
		List<V3xOrgPost> postlist = orgManagerDirect.getAllPosts(
				user.getLoginAccount(), false);
		result.addObject("postlist", postlist);
		// 取得所有职务级别
		List<V3xOrgLevel> levellist = orgManagerDirect.getAllLevels(
				user.getLoginAccount(), false);
		result.addObject("levellist", levellist);
		// 取得所有的部门
		List<V3xOrgEntity> departmentlist = orgManagerDirect
				.getEntityListNoRelation(
						V3xOrgDepartment.class.getSimpleName(), "orgAccountId",
						user.getLoginAccount(), user.getLoginAccount());
		result.addObject("departmentlist", departmentlist);
		try {
			List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>();
			List<V3xOrgMember> memberlistForDeptAdmin = new ArrayList<V3xOrgMember>();
			// 是否是部门管理员登录
			boolean isdeptAdmin = false;
			// 如果是部门管理员而不是hr管理员登录,则查询本部门及子部门的人员
			if (deptAdmin != null && deptAdmin.equals("1")) {
				isdeptAdmin = true;
				V3xOrgRole depAdminRole = orgManagerDirect
						.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPADMIN);
				List<V3xOrgRelationship> rels = orgManagerDirect
						.getRelationships("type",
								V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE,
								"backupId", depAdminRole.getId(), "sourceId",
								user.getId());
				if (rels != null && rels.size() > 0) {
					for (V3xOrgRelationship rel : rels) {
						List<V3xOrgMember> deptMembers = orgManagerDirect
								.getMembersByDepartment(rel.getObjectiveId(),
										false, null, user.getLoginAccount());
						if (deptMembers != null && deptMembers.size() > 0) {
							for (V3xOrgMember deptMem : deptMembers) {
								if (!memberlistForDeptAdmin.contains(deptMem)) {
									memberlistForDeptAdmin.add(deptMem);
								}
							}
						}
					}
				}
			}

			String textfield = request.getParameter("textfield");
			String condition = null;
			// 根据条件查询（部门管理员不需要分页）
			if (deptAdmin != null && deptAdmin.equals("1")
					&& "allmember".equals(type)) {
				memberlist = memberlistForDeptAdmin;
			} else {
				if ("allmember".equals(type)) {
					condition = "all";
				} else if ("incumbency".equals(type)) {
					condition = "state";
					textfield = "1";
				} else if ("dimission".equals(type)) {
					condition = "state";
					textfield = "2";
				} else if ("enable".equals(type)) {
					condition = "enabled";
					textfield = String.valueOf(true);
				} else if ("disable".equals(type)) {
					condition = "enabled";
					textfield = String.valueOf(false);
				}
				memberlist = OrganizationHelper.searchMember(condition,
						textfield, searchManager, orgManagerDirect,
						!isdeptAdmin, true, true);
			}

			Collections.sort(memberlist, CompareSortEntity.getInstance());

			// 如果是部门管理员登录需要过滤
			if (deptAdmin != null && deptAdmin.equals("1")) {
				List<V3xOrgMember> memsForDeptAdmin = new ArrayList<V3xOrgMember>();
				for (V3xOrgMember mem : memberlist) {
					if (memberlistForDeptAdmin.contains(mem)) {
						memsForDeptAdmin.add(mem);
					}
				}
				memberlist.clear();
				// 分页
				memberlist = pagenate(memsForDeptAdmin);
			}

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
					V3xOrgDepartment dept = orgManagerDirect
							.getDepartmentById(deptId);
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

			//

			result.addObject("memberlist", resultlist);
			// 获得单位类别下拉列表中的数据
			Map<String, Metadata> orgMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.organization);
			result.addObject("orgMeta", orgMeta);
			// 判断是什么版本
			boolean showAssign = (Boolean) (SysFlag.org_showGroupAccountAssign
					.getFlag());
			result.addObject("showAssign", showAssign);
			// 判断是否含有NC插件
			boolean hasNC = SystemEnvironment.hasPlugin("nc");
			result.addObject("hasNC", hasNC);
			// 判断是否是部门管理员
			result.addObject("deptAdmin", deptAdmin);

			result.addObject("condition", condition);
			result.addObject("textfield", textfield);

			return result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 人员导出到excel xut 07-6-28
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator })
	public ModelAndView exportMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.info("exportMember");
		PrintWriter out = response.getWriter();

		User u = this.catchCurrentUser();
		if (u == null) {
			// DataUtil.outNullUserAlertScript(out);
			return null;
		}
		if (DataUtil.doingImpExp(u.getId())) {
			// DataUtil.outDoingImpExpAlertScript(out);
			return null;
		}
		Locale locale = LocaleContext.getLocale(request);
		// Locale locale = user.getLocale();
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";

		String listname = "MemberList_";
		// ResourceBundleUtil.getString(resource, locale,
		// "org.member_form.list");
		listname += u.getLoginName();

		String key = null;
		DataUtil.putImpExpAction(u.getId(), "export");
		try {
			DataRecord dataRecord = OrganizationHelper.exportMember(request,
					metadataManager, response, fileToExcelManager,
					orgManagerDirect);
			key = DataUtil.createTempSaveKey4Sheet(dataRecord);
		} catch (Exception e) {
			DataUtil.removeImpExpAction(u.getId());
			throw e;
		}
		DataUtil.removeImpExpAction(u.getId());

		String url = DataUtil.getOrgDownloadExpToExcelUrl(key, listname);
		log.info("url=" + url);
		return this.getIoManager().toExpRepeater(request, response, url);
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView exportMember1(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User u = this.catchCurrentUser();
		if (u == null) {
			// DataUtil.outNullUserAlertScript(out);
			return null;
		}
		if (DataUtil.doingImpExp(u.getId())) {
			// DataUtil.outDoingImpExpAlertScript(out);
			return null;
		}

		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");

		String listname = "MemberList_";
		listname += u.getLoginName();

		DataUtil.putImpExpAction(u.getId(), "export");
		DataRecord dataRecord = null;
		try {
			dataRecord = OrganizationHelper.exportMember(request,
					metadataManager, response, fileToExcelManager,
					orgManagerDirect, searchManager, condition, textfield);
		} catch (Exception e) {
			DataUtil.removeImpExpAction(u.getId());
			throw e;
		}
		DataUtil.removeImpExpAction(u.getId());
		OrganizationHelper.exportToExcel(request, response, fileToExcelManager,
				listname, dataRecord);
		return null;
	}

	/**
	 * 进入添加人员管理界面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator,
			RoleType.DepartmentAdmin, RoleType.HrAdmin })
	public ModelAndView addMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("organization/member/addMember");
		// 判断是否是部门管理员登录
		String deptAdmin = request.getParameter("deptAdmin");
		// 获得当前排序号的最大值
		Integer maxSortNum = orgManagerDirect.getMaxMemberSortNum(CurrentUser
				.get().getLoginAccount());
		WebV3xOrgMember webMember = new WebV3xOrgMember();
		V3xOrgMember member = new V3xOrgMember();
		Long id = member.getId();
		member.setEnabled(true);
		member.setPassword("");
		member.setSortId(maxSortNum + 1);
		member.setOrgLevelId(null);
		webMember.setV3xOrgMember(member);
		result.addObject("member", webMember);
		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		result.addObject("orgMeta", orgMeta);
		// 获取首选语言的下拉列表
		Map<String, Metadata> globalMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.global);
		result.addObject("globalMeta", globalMeta);
		// 获取职务级别列表
		List<V3xOrgLevel> levels = orgManagerDirect.getAllLevels(CurrentUser
				.get().getLoginAccount(), false);
		List<V3xOrgLevel> levelsForPage = new ArrayList<V3xOrgLevel>();
		// 过滤无效项并获得最小职务级别序号
		Integer minLevelId = 1;
		for (V3xOrgLevel level : levels) {
			if (level.getEnabled()) {
				levelsForPage.add(level);
				if (minLevelId < level.getLevelId()) {
					minLevelId = level.getLevelId();
				}
			}
		}
		// 获取默认菜单权限
		String securityIds = null;
		String securityNames = null;
		List<Security> defaultSecurities = this.menuManager
				.getDefaultSecurities();
		for (Security security : defaultSecurities) {
			if (securityIds == null) {
				securityIds = security.getId().toString();
				securityNames = security.getName();
			} else {
				securityIds += "," + security.getId();
				securityNames += "," + security.getName();
			}
		}
		// 是否是部门管理员登录
		result.addObject("deptAdmin", deptAdmin);
		// 如果是部门管理员登录，获取部门管理员能管理的部门,拼成字符串
		if (deptAdmin != null && deptAdmin.equals("1")) {
			Long userId = CurrentUser.get().getId();
			result.addObject("depsPathStr", getDeptPathsForAdmin(userId));
		}
		if (LDAPConfig.getInstance().getIsEnableLdap()
				&& SystemEnvironment
						.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
								.name())) {
			result.addObject("hasLDAPAD", true);
			result.addObject("addstate", true);
		}
		// List<SpaceModel> spaceList =
		// spaceManager.getAdminCanManagerSpace(CurrentUser.get().getLoginAccount(),
		// SpaceTypeClass.personal, "state",
		// String.valueOf(SpaceState.normal.ordinal()),false);
		// result.addObject("spaceList", spaceList);
		// Long currentSpaceId =
		// spaceManager.getPersonalSpaceId4Create(true,CurrentUser.get().getLoginAccount());
		// currentSpaceId
		// result.addObject("currentSpaceId", currentSpaceId);

		result.addObject("securityIds", securityIds);
		result.addObject("securityNames", securityNames);
		String isHRAdmin = request.getParameter("isHRAdmin");
		result.addObject("isHRAdmin", isHRAdmin);
		result.addObject("levels", levelsForPage);
		result.addObject("minLevelId", minLevelId);
		result.addObject("id", id);

		boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag();// 政务版标识

		// HR枚举
		Map<String, Metadata> hrMetadata = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.hr);
		result.addObject("hrMetadata", hrMetadata);

		// 政务版--职级--start
		// 获取职级列表
		if (isGovVersion) {
			List<V3xOrgDutyLevel> dutyLevels = orgManagerDirect
					.getAllDutyLevels(CurrentUser.get().getLoginAccount(),
							false);
			List<V3xOrgDutyLevel> dutylevelsForPage = new ArrayList<V3xOrgDutyLevel>();
			// 过滤无效项并获得最小职级序号
			Integer minDutyLevelId = 1;
			for (V3xOrgDutyLevel level : dutyLevels) {
				if (level.getEnabled()) {
					dutylevelsForPage.add(level);
					if (minDutyLevelId < level.getLevelId()) {
						minDutyLevelId = level.getLevelId();
					}
				}
			}
			result.addObject("dutyLevels", dutylevelsForPage);
			result.addObject("minDutyLevelId", minDutyLevelId);
		}
		// 政务版--职级--end

		return result;
	}

	private String getDeptPathsForAdmin(Long userId) throws BusinessException {
		V3xOrgRole role = orgManagerDirect
				.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPADMIN);
		List<V3xOrgEntity> depts = orgManagerDirect.getGroupByMemberAndRole(
				userId, role.getId());
		String depsPathStr = "";
		for (V3xOrgEntity dept : depts) {
			depsPathStr += ((V3xOrgDepartment) dept).getPath() + "|";
		}
		depsPathStr = depsPathStr.substring(0, depsPathStr.length() - 1);
		return depsPathStr;
	}

	interface MemberBuilder {
		/**
		 * 检验部门是否可用。
		 * 
		 * @param deptId
		 * @return 部门不存在或已禁用返回false，否则返回true。
		 * @throws BusinessException
		 */
		boolean checkDepartment(long deptId) throws BusinessException;

		/**
		 * 检验岗位是否可用。
		 * 
		 * @param postId
		 * @return 岗位不存在或已禁用返回false，否则返回true。
		 * @throws BusinessException
		 */
		boolean checkPost(long postId) throws BusinessException;

		/**
		 * 检验职务级别是否可用。
		 * 
		 * @param levelId
		 * @return 职务级别不存在或已禁用返回false，否则返回true。
		 * @throws BusinessException
		 */
		boolean checkLevel(long levelId) throws BusinessException;

		/**
		 * 检验排序号是否重复。
		 * 
		 * @param sortId
		 * @return
		 * @throws BusinessException
		 */
		boolean hasDuplicatedSortId(long sortId) throws BusinessException;

		/**
		 * 检验副岗。
		 * 
		 * @param member
		 * @return 副岗与主岗重复则返回false，否则返回true。
		 * @throws BusinessException
		 */
		boolean checkSecondPost(V3xOrgMember member) throws BusinessException;

		/**
		 * 检验成员的合法性。对成员的部门、岗位、职务级别和副岗等进行检验，存在任何问题均返回false。
		 * 
		 * @param member
		 * @return
		 * @throws BusinessException
		 */
		boolean check(V3xOrgMember member) throws BusinessException;

		/**
		 * 创建成员，创建前调用check进行校验。
		 * 
		 * @param member
		 * @return
		 * @throws BusinessException
		 */
		boolean build(V3xOrgMember member) throws BusinessException;

		/**
		 * 创建成员，略过校验。
		 * 
		 * @param member
		 * @return
		 * @throws BusinessException
		 */
		boolean buildWithNoCheck(V3xOrgMember member) throws BusinessException;
	}

	class SingleMemberBuilder implements MemberBuilder {
		private OrgManagerDirect orgManagerDirect;

		public boolean check(V3xOrgMember member) throws BusinessException {
			if (checkDepartment(member.getOrgDepartmentId()))
				return false;
			if (member.getIsInternal()) {
				if (checkPost(member.getOrgPostId()))
					return false;
				if (checkLevel(member.getOrgLevelId()))
					return false;
			}
			return checkSecondPost(member);

		}

		public boolean checkSecondPost(V3xOrgMember member)
				throws BusinessException {
			Long deptId = member.getOrgDepartmentId();
			Long postId = member.getOrgPostId();
			List<MemberPost> secondPostList = member.getSecond_post();
			for (MemberPost secondPost : secondPostList) {
				if (secondPost.getDepId().equals(deptId)
						&& secondPost.getPostId().equals(postId)) {
					return false;
				}
			}
			return true;
		}

		public boolean checkDepartment(long deptId) throws BusinessException {
			// V3xOrgDepartment dept =
			// orgManagerDirect.getDepartmentById(deptId);
			// return !(dept==null||!dept.getEnabled());
			String entityClassName = V3xOrgDepartment.class.getSimpleName();
			return checkEntityEnabled(deptId, entityClassName);
		}

		private boolean checkEntityEnabled(long id, String entityClassName)
				throws BusinessException {
			List<V3xOrgEntity> entities = orgManagerDirect
					.getEntityListNoRelation(entityClassName, "id", id,
							V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			if ((entities == null) || (entities.size() == 0))
				return false;
			if (entities.size() > 1)
				throw new BusinessException("find more than one entity:"
						+ entityClassName + " " + id);
			return entities.get(0).getEnabled();
		}

		public boolean checkLevel(long levelId) throws BusinessException {
			// V3xOrgLevel level = orgManagerDirect.getLevelById(levelId);
			// return !(level==null||!level.getEnabled());
			String entityClassName = V3xOrgLevel.class.getSimpleName();
			return checkEntityEnabled(levelId, entityClassName);
		}

		// 政务版--职级检查
		public boolean checkDutyLevel(long dutyLevelId)
				throws BusinessException {
			// V3xOrgLevel level = orgManagerDirect.getLevelById(levelId);
			// return !(level==null||!level.getEnabled());
			String entityClassName = V3xOrgDutyLevel.class.getSimpleName();
			return checkEntityEnabled(dutyLevelId, entityClassName);
		}

		public boolean checkPost(long postId) throws BusinessException {
			String entityClassName = V3xOrgPost.class.getSimpleName();
			return checkEntityEnabled(postId, entityClassName);
		}

		public boolean hasDuplicatedSortId(long sortId)
				throws BusinessException {
			return orgManagerDirect.isPropertyDuplicated(
					V3xOrgMember.class.getSimpleName(), "sortId", sortId);
		}

		public OrgManagerDirect getOrgManagerDirect() {
			return orgManagerDirect;
		}

		public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
			this.orgManagerDirect = orgManagerDirect;
		}

		public boolean build(V3xOrgMember member) throws BusinessException {
			if (check(member)) {
				return buildWithNoCheck(member);
			}
			return true;
		}

		public boolean buildWithNoCheck(V3xOrgMember member)
				throws BusinessException {
			return false;
		}
	}

	/**
	 * 添加人员管理
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator,
			RoleType.DepartmentAdmin, RoleType.HrAdmin })
	public ModelAndView createMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		String imageid = request.getParameter("fileId");
		String imagedate = request.getParameter("createDate");

		// 政务版：新增HR的字段到人员管理页面
		String ID_card = request.getParameter("ID_card"); // 身份证号
		String edu_level = request.getParameter("edu_level"); // 最高学历
		String political_position = request.getParameter("political_position"); // 政治面貌
		String degreeLevel = request.getParameter("degreeLevel"); // 最高学位
		boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag();// 政务版标识

		Long imageId = null;
		Date imageDate = null;
		if (Strings.isNotBlank(imageid)) {
			imageId = Long.parseLong(imageid);
		}
		if (Strings.isNotBlank(imagedate)) {
			imageDate = Datetimes.parseDatetime(imagedate);
		}
		SingleMemberBuilder builder = new SingleMemberBuilder();
		builder.setOrgManagerDirect(orgManagerDirect);
		try {
			String ldapEntry = request.getParameter("ldapUserCodes");
			String selectOU = request.getParameter("selectOU");
			User user = CurrentUser.get();
			long accountId = user.getLoginAccount();
			long deptId = Long.parseLong(request
					.getParameter("orgDepartmentId"));

			String strSecondPostIds = request.getParameter("secondPostIds");
			String[] arrSecondPosts = strSecondPostIds.split(",");
			String officeNum = request.getParameter("officeNum");
			String isInsert = request.getParameter("isInsert");
			String securityIdsStr = request.getParameter("securityIds");
			if (LDAPConfig.getInstance().getIsEnableLdap()
					&& SystemEnvironment
							.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
									.name())) {
				if (Strings.isBlank(ldapEntry) && Strings.isBlank(selectOU)
						&& (event.isDefaultOUNull(accountId) == null)) {
					out.println("<script>");
					out.println("alert(\""
							+ ResourceBundleUtil.getString(
									LDAPConfig.LDAP_RESOURCE_NAME,
									"ldap.alert.setdn", "") + "\");");
					out.println("</script>");
					super.rendJavaScript(response, "parent.toEditMember()");
					return null;
				}
			}
			V3xOrgMember member = new V3xOrgMember();

			String str = request.getParameter("id");
			Long memberId = 0L;
			if (null != str && !str.equals("")) {
				memberId = Long.parseLong(str);
			}
			member.setId(memberId);

			bind(request, member);

			member.setProperty("officeNum", officeNum);
			member.setOrgAccountId(accountId);
			/******************** 成发集团项目 *****************************/
			/*
			 * if(member.getSecretLevel() != SecretAudit.SECRET_LEVEL_NORMAL) {
			 * secretAuditManager.create(member); // 新建时人员密级默认为非密
			 * member.setSecretLevel(SecretAudit.SECRET_LEVEL_NORMAL); }
			 */
			/******************** 成发集团项目 *****************************/

			// 2017-01-13 诚佰公司 密级空值判断
			if (member.getSecretLevel() != null
					&& member.getSecretLevel() != SecretAudit.SECRET_LEVEL_NORMAL) {
				secretAuditManager.create(member);
				// 新建时人员密级默认为非密
				member.setSecretLevel(SecretAudit.SECRET_LEVEL_NORMAL);
			}
			// 诚佰公司

			// 修改BUGAEIGHT-872，克隆人员对象做事件监听参数
			V3xOrgMember newMember = new V3xOrgMember();
			BeanUtils.copyProperties(newMember, member);

			Long secondDeptId = null;
			Long secondPostId = null;
			if (null != arrSecondPosts && arrSecondPosts.length > 0) {
				for (String secondpostid : arrSecondPosts) {
					if (secondpostid.indexOf("_") != -1) {
						String[] arrDeptPosts = secondpostid.split("_");
						if (StringUtils.isNotBlank(arrDeptPosts[0])
								&& StringUtils.isNotBlank(arrDeptPosts[1])) {
							secondDeptId = new Long(arrDeptPosts[0]);
							secondPostId = new Long(arrDeptPosts[1]);
						}
					} else {
						if (StringUtils.isNotBlank(secondpostid)) {
							secondDeptId = member.getOrgDepartmentId();
							secondPostId = new Long(secondpostid);
						}
					}
					if (secondDeptId != null && secondPostId != null)
						// 延迟副岗主岗重复校验
						member.addSecondPost(secondDeptId, secondPostId);
				}
			}
			// 部门是否可用（存在且启用）
			if (!builder.checkDepartment(deptId)) {
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_unabled_property','"
						+ Constant
								.getString4CurrentUser("org.member_form.deptName.label")
						+ "'));");
				out.println("</script>");
				super.rendJavaScript(response, "parent.toEditMember()");
				return null;
			}
			if (member.getIsInternal()) {
				long postId = Long.parseLong(request.getParameter("orgPostId"));
				long levelId = Long.parseLong(request
						.getParameter("orgLevelId"));

				// 岗位和职务级别可用
				if (!builder.checkPost(postId)) {
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_unabled_property','"
							+ Constant
									.getString4CurrentUser("org.member_form.primaryPost.label")
							+ "'));");
					out.println("</script>");
					super.rendJavaScript(response, "parent.toEditMember()");
					return null;
				}
				if (!builder.checkLevel(levelId)) {
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_unabled_property','"
							+ Constant
									.getString4CurrentUser("org.member_form.levelName.label")
							+ "'));");
					out.println("</script>");
					super.rendJavaScript(response, "parent.toEditMember()");
					return null;
				}

				if (isGovVersion
						&& request.getParameter("orgDutyLevelId") != null) {
					long dutyLevelId = Long.parseLong(request
							.getParameter("orgDutyLevelId"));
					if (!builder.checkDutyLevel(dutyLevelId)) {
						out.println("<script>");
						out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_unabled_property','"
								+ Constant
										.getString4CurrentUser("org.dutylevel_form.name.label")
								+ "'));");
						out.println("</script>");
						super.rendJavaScript(response, "parent.toEditMember()");
						return null;
					}
				}
			}

			// 校验副岗是否和主岗重复
			if (!builder.checkSecondPost(member)) {
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_member_secondPost_repeat'));");
				out.println("</script>");
				// 后续操作
				super.rendJavaScript(response, "parent.toEditMember()");
				return null;
			}

			// 人员排序号的重复处理(重复序号处理：1 插入 0 重复)
			if (isInsert.equals("1")
					&& orgManagerDirect.isPropertyDuplicated(
							V3xOrgMember.class.getSimpleName(), "sortId",
							member.getSortId())) {
				orgManagerDirect.insertRepeatSortNum(
						V3xOrgMember.class.getSimpleName(), accountId,
						member.getSortId());
			}
			orgManager.addMember(member);
			// 添加个人文档库
			// docLibManager.addDocLib(member.getId());
			// 添加个人博客记录
			// blogManager.createEmployee(member.getId(), accountId);

			// 添加照片
			StaffInfo staffinfo = new StaffInfo();
			staffinfo.setOrg_member_id(member.getId());
			staffinfo.setImage_id(imageId);
			staffinfo.setImage_datetime(imageDate);

			// 政务版 添加四个字段属性到HR表
			if (isGovVersion) {
				// 身份证号
				if (ID_card != null) {
					staffinfo.setID_card(ID_card);
				}
				// 最高学历
				if (edu_level != null) {
					staffinfo.setEdu_level(Integer.parseInt(edu_level));
				}
				// 政治面貌
				if (political_position != null) {
					staffinfo.setPolitical_position(Integer
							.parseInt(political_position));
				}
				// 最高学位
				if (degreeLevel != null) {
					staffinfo.setDegreeLevel(degreeLevel);
				}
			}
			staffInfoManager.addStaffInfo(staffinfo);

			// 把副岗位添加部门岗位中
			// addDeptPost(member);

			// 添加个人菜单权限
			if (null != securityIdsStr && securityIdsStr.length() > 0) {
				String[] securityIds = securityIdsStr.split(",");
				List<Long> securityIdsList = new ArrayList<Long>();
				for (String idStr : securityIds) {
					securityIdsList.add(Long.parseLong(idStr));
				}
				this.menuManager.saveMemberSecurity(member.getId(),
						member.getOrgAccountId(), securityIdsList);
			}
			if (LDAPConfig.getInstance().getIsEnableLdap()
					&& SystemEnvironment
							.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
									.name())) {
				try {
					if (StringUtils.isBlank(ldapEntry)) {
						event.newAddLdapPerson(newMember, selectOU);
						appLogManager.insertLog(user,
								AppLogAction.LDAP_Account_Create,
								newMember.getName(), selectOU);
					} else {
						String[] errorResult = event.addMember(newMember,
								ldapEntry);
						if (errorResult != null && errorResult.length > 0) {
							String jsContent = "";
							for (int i = 0; i < errorResult.length; i++) {
								jsContent += errorResult[i] + "\\n";
							}
							out.println("<script>");
							out.println("alert('" + jsContent + "');");
							out.println("</script>");
						}
						appLogManager.insertLog(user,
								AppLogAction.LDAP_Account_Bing_Create,
								newMember.getName(), ldapEntry);
					}
				} catch (Exception e) {
					log.error("ldap/ad 添加人员绑定不成功！", e);
					// throw new BusinessException("ldap/ad 添加人员绑定不成功！",e);
					if (e.getMessage().indexOf("error code 19") != -1) {
						out.println("<script>");
						out.println("alert('"
								+ ResourceBundleUtil.getString(
										LDAPConfig.LDAP_RESOURCE_NAME,
										"ldap.log.error.tip") + "');");
						out.println("</script>");
					}
				}
			}

			String type = request.getParameter("type");
			if (!Strings.isBlank(type)) {
				long iad = Long.valueOf(type).longValue();
				Metadata metadata = metadataManager
						.getMetadata(MetadataNameEnum.org_property_member_type);
				if (null != metadata) {
					MetadataItem item = metadataManager.getMetadataItem(
							metadata.getName(), Long.valueOf(iad).toString());
					if (null != item) {
						metadataManager.refMetadataItem(metadata.getId(),
								item.getId(), Long.valueOf(iad).intValue());
					}
				}
			}

			String state = request.getParameter("state");
			if (!Strings.isBlank(state)) {
				long iad = Long.valueOf(state).longValue();
				Metadata metadata = metadataManager
						.getMetadata(MetadataNameEnum.org_property_member_state);
				if (null != metadata) {
					MetadataItem item = metadataManager.getMetadataItem(
							metadata.getName(), Long.valueOf(iad).toString());
					if (null != item) {
						metadataManager.refMetadataItem(metadata.getId(),
								item.getId(), Long.valueOf(iad).intValue());
					}
				}
			}

			// 推送模板配置。add by dongyj
			// this.templeteConfigManager.pushNewOrgEntityTemplete4Member(V3xOrgEntity.ORGENT_TYPE_MEMBER,member.getId(),member.getId());
			// 触发创建人员事件
			eventListener.addMember(newMember);

			// 支持全文搜索
			if (IndexInitConfig.hasLuncenePlugIn()) {
				try {
					indexManager.index(((IndexEnable) organizationServices)
							.getIndexInfo(member.getId()));
				} catch (Throwable e) {
					log.error("添加人员时访问全文检索服务异常：", e);
				}
			}

			// 日志
			appLogManager.insertLog(user, AppLogAction.Organization_NewMember,
					user.getName(), member.getName());
			V3xOrgMember memberNext = new V3xOrgMember();
			super.rendJavaScript(response, "parent.doEndMemberFrom('"
					+ accountId + "','addMember','" + memberNext.getId() + "')");
			return null;
		} catch (BusinessException e) {
			log.error("error add member");
			return null;
		}
	}

	/**
	 * 旧版本，重构版本稳定后删除。 添加人员管理
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView createMemberOld(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();
		try {
			User user = CurrentUser.get();
			long accountId = user.getLoginAccount();
			long deptId = Long.parseLong(request
					.getParameter("orgDepartmentId"));
			long postId = Long.parseLong(request.getParameter("orgPostId"));
			long levelId = Long.parseLong(request.getParameter("orgLevelId"));
			String strSecondPostIds = request.getParameter("secondPostIds");
			String[] arrSecondPosts = strSecondPostIds.split(",");
			String officeNum = request.getParameter("officeNum");
			String isInsert = request.getParameter("isInsert");
			String securityIdsStr = request.getParameter("securityIds");

			V3xOrgMember member = new V3xOrgMember();
			bind(request, member);
			// 部门是否可用（存在且启用）
			V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
			if (dept == null || !dept.getEnabled()) {
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_unabled_property','"
						+ Constant
								.getString4CurrentUser("org.member_form.deptName.label")
						+ "'));");
				out.println("</script>");
				super.rendJavaScript(response, "parent.toEditMember()");
				return null;
			}
			if (member.getIsInternal()) {
				// 岗位和职务级别可用
				V3xOrgPost post = orgManagerDirect.getPostById(postId);
				V3xOrgLevel level = orgManagerDirect.getLevelById(levelId);
				if (post == null || !post.getEnabled()) {
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_unabled_property','"
							+ Constant
									.getString4CurrentUser("org.member_form.primaryPost.label")
							+ "'));");
					out.println("</script>");
					super.rendJavaScript(response, "parent.toEditMember()");
					return null;
				}
				if (level == null || !level.getEnabled()) {
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_unabled_property','"
							+ Constant
									.getString4CurrentUser("org.member_form.levelName.label")
							+ "'));");
					out.println("</script>");
					super.rendJavaScript(response, "parent.toEditMember()");
					return null;
				}
			}

			member.setProperty("officeNum", officeNum);
			member.setOrgAccountId(accountId);

			if (null != arrSecondPosts && arrSecondPosts.length > 0) {
				for (String secondpostid : arrSecondPosts) {
					String[] arrDeptPosts = secondpostid.split("_");
					if (arrDeptPosts.length > 1) {
						if (null != arrDeptPosts[0]
								&& !arrDeptPosts[0].equals("")
								&& null != arrDeptPosts[1]
								&& !arrDeptPosts[1].equals("")) {
							Long secondDeptId = Long.parseLong(arrDeptPosts[0]);
							Long secondPostId = Long.parseLong(arrDeptPosts[1]);
							// 校验副岗是否和主岗重复
							if (secondDeptId
									.equals(member.getOrgDepartmentId())
									&& secondPostId.equals(member
											.getOrgPostId())) {
								out.println("<script>");
								out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_member_secondPost_repeat'));");
								out.println("</script>");
								// 后续操作
								super.rendJavaScript(response,
										"parent.toEditMember()");
								return null;
							}
							member.addSecondPost(secondDeptId, secondPostId);
						}
					}
				}
			}
			// 人员排序号的重复处理

			if (isInsert.equals("1")
					&& orgManagerDirect.isPropertyDuplicated(
							V3xOrgMember.class.getSimpleName(), "sortId",
							member.getSortId())) {
				orgManagerDirect.insertRepeatSortNum(
						V3xOrgMember.class.getSimpleName(), accountId,
						member.getSortId());
			}
			orgManagerDirect.addMember(member);
			// 添加个人文档库
			docLibManager.addDocLib(member.getId());
			// 添加个人博客记录
			blogManager.createEmployee(member.getId(), accountId);

			// 添加个人菜单权限

			if (null != securityIdsStr && securityIdsStr.length() > 0) {
				String[] securityIds = securityIdsStr.split(",");
				List<Long> securityIdsList = new ArrayList<Long>();
				for (String idStr : securityIds) {
					securityIdsList.add(Long.parseLong(idStr));
				}
				this.menuManager.saveMemberSecurity(member.getId(),
						member.getOrgAccountId(), securityIdsList);
			}

			super.rendJavaScript(response, "parent.doEndMemberFrom('"
					+ accountId + "','addMember','')");

			// 触发创建人员事件
			eventListener.addMember(member);

			return null;
		} catch (BusinessException e) {
			log.error("error add member");
			return null;
		}
	}

	/**
	 * 进入修改人员管理界面方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator,
			RoleType.DepartmentAdmin, RoleType.HrAdmin })
	public ModelAndView editMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("organization/member/editMember");
		// 判断是否是部门管理员登录
		String deptAdmin = request.getParameter("deptAdmin");
		String id = request.getParameter("id");
		V3xOrgMember member = orgManagerDirect
				.getMemberById(Long.parseLong(id));
		StaffInfo staffInfo = staffInfoManager.getStaffInfoById(Long
				.parseLong(id));
		result.addObject("staff", staffInfo);
		if (null != staffInfo) {
			if (null != staffInfo.getImage_id()
					&& !staffInfo.getImage_id().equals("")) {
				result.addObject("image", 0);
			}
		}
		// 不能把密码返回给界面
		member.setPassword(V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD);
		long deptId = member.getOrgDepartmentId();
		long levelId = member.getOrgLevelId();
		long postId = member.getOrgPostId();
		WebV3xOrgMember webMember = new WebV3xOrgMember();
		webMember.setV3xOrgMember(member);
		// 获取扩展属性
		orgManagerDirect.loadEntityProperty(member);
		webMember.setOfficeNum(member.getProperty("officeNum"));
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
		if (dept != null) {
			webMember.setDepartmentName(dept.getName());
		}

		V3xOrgLevel level = orgManagerDirect.getLevelById(levelId);
		if (null != level) {
			if (level.getEnabled()) {
				webMember.setLevelName(level.getName());
			} else {
				member.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
			}
		}

		V3xOrgPost post = orgManagerDirect.getPostById(postId);
		if (null != post) {
			if (post.getEnabled()) {
				webMember.setPostName(post.getName());
			} else {
				member.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
			}
		}

		// 取得人员的副岗
		List<MemberPost> memberPosts = member.getSecond_post();
		List<WebV3xOrgModel> secondPostList = new ArrayList<WebV3xOrgModel>();
		if (null != memberPosts && !memberPosts.isEmpty()) {
			StringBuffer deptpostbuffer = new StringBuffer();
			StringBuffer deptpostbufferId = new StringBuffer();
			for (MemberPost memberPost : memberPosts) {
				WebV3xOrgModel webModel = new WebV3xOrgModel();
				StringBuffer sbuffer = new StringBuffer();
				StringBuffer sbufferId = new StringBuffer();
				Long deptid = memberPost.getDepId();
				V3xOrgDepartment v3xdept = orgManagerDirect
						.getDepartmentById(deptid);
				Long postid = memberPost.getPostId();
				V3xOrgPost v3xpost = orgManagerDirect.getPostById(postid);
				// 只有部门岗位都是有效的才显示副岗
				if (v3xdept != null && v3xdept.getEnabled() && v3xpost != null
						&& v3xpost.getEnabled()) {
					sbuffer.append(v3xdept.getName());
					sbuffer.append("-");
					sbufferId.append(v3xdept.getId());
					sbufferId.append("_");
					sbuffer.append(v3xpost.getName());
					sbufferId.append(v3xpost.getId());
					deptpostbuffer.append(sbuffer.toString());
					deptpostbuffer.append(",");
					deptpostbufferId.append(sbufferId.toString());
					deptpostbufferId.append(",");
					webModel.setSecondPostId(v3xdept.getId() + "_"
							+ v3xpost.getId());
					webModel.setSecondPostType("Department_Post");
					secondPostList.add(webModel);
				}
			}
			if (deptpostbuffer.length() > 0) {
				String deptpostStr = deptpostbuffer.substring(0,
						deptpostbuffer.length() - 1);
				String deptpostStrId = deptpostbufferId.substring(0,
						deptpostbufferId.length() - 1);
				webMember.setSecondPosts(deptpostStr);
				result.addObject("secondPostM", deptpostStrId);
			}
		}
		result.addObject("secondPostList", secondPostList);
		result.addObject("member", webMember);
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
			result.addObject("preview", 0);
		} else {
			result.addObject("preview", 1);
		}
		// 取得人员兼职信息
		List cntList = orgManagerDirect.getAllConcurrentPostByMemberId(member
				.getId());
		if (!ListUtils.EMPTY_LIST.equals(cntList)) {
			result.addObject("cntList", cntList);
		} else {
			result.addObject("cntList", null);
		}
		// 取得个人角色
		List<String[]> roleNameList = new ArrayList<String[]>();
		List<V3xOrgRelationship> relList = orgManagerDirect
				.getRolesByMember(Long.parseLong(id));
		for (V3xOrgRelationship rel : relList) {
			String[] roleStr = new String[2];
			V3xOrgRole nowRole = orgManagerDirect
					.getRoleById(rel.getBackupId());
			if (rel.getType().equals(V3xOrgEntity.ORGREL_TYPE_MEMBER_ACCROLE)) {
				roleStr[0] = "";
				roleStr[1] = nowRole == null ? "" : nowRole.getName();
			} else if (rel.getType().equals(
					V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE)) {
				roleStr[0] = orgManagerDirect.getDepartmentById(
						rel.getObjectiveId()).getName();
				roleStr[1] = nowRole == null ? "" : nowRole.getName();
			}
			roleNameList.add(roleStr);
		}
		result.addObject("roleNameList", roleNameList);
		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		result.addObject("orgMeta", orgMeta);
		// 获取职务级别列表
		List<V3xOrgLevel> levels = orgManagerDirect.getAllLevels(CurrentUser
				.get().getLoginAccount(), false);
		List<V3xOrgLevel> levelsForPage = new ArrayList<V3xOrgLevel>();
		// 过滤无效项
		for (V3xOrgLevel levelForPage : levels) {
			if (levelForPage.getEnabled())
				levelsForPage.add(levelForPage);
		}
		// 判断是否回显密码
		result.addObject("showPassword", 1);

		// 获取该用户菜单权限
		String securityIds = null;
		String securityNames = null;
		List<Security> defaultSecurities = this.menuManager
				.getSecurityOfMember(member.getId(), member.getOrgAccountId(),
						true);
		for (Security security : defaultSecurities) {
			if (securityIds == null) {
				securityIds = security.getId().toString();
				securityNames = security.getName();
			} else {
				securityIds += "," + security.getId();
				securityNames += "," + security.getName();
			}
		}

		// 是否是部门管理员登录
		result.addObject("deptAdmin", deptAdmin);
		// 如果是部门管理员登录，获取部门管理员能管理的部门,拼成字符串
		if (deptAdmin != null && deptAdmin.equals("1")) {
			Long userId = CurrentUser.get().getId();
			result.addObject("depsPathStr", getDeptPathsForAdmin(userId));
		}
		if (LDAPConfig.getInstance().getIsEnableLdap()
				&& SystemEnvironment
						.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
								.name())) {
			// 组装LDAP/AD帐号
			try {
				result.addObject("ldapADLoginName",
						event.getLdapAdExUnitCode(member.getLoginName()));
				result.addObject("hasLDAPAD", true);
				result.addObject("editstate", true);
			} catch (Exception e) {
				log.error("显示ldap_ad帐号出错", e);
				throw new Exception("显示ldap_ad帐号出错", e);
			}

		}
		// List<SpaceModel> spaceList =
		// spaceManager.getAdminCanManagerSpace(CurrentUser.get().getLoginAccount(),
		// SpaceTypeClass.personal, "state",
		// String.valueOf(SpaceState.normal.ordinal()),false);
		// result.addObject("spaceList", spaceList);
		// Long currentSpaceId = spaceManager.getPersonalSpaceId(member);
		// currentSpaceId
		// result.addObject("currentSpaceId", currentSpaceId);

		result.addObject("securityIds", securityIds);
		result.addObject("securityNames", securityNames);
		String isHRAdmin = request.getParameter("isHRAdmin");
		result.addObject("isHRAdmin", isHRAdmin);
		result.addObject("levels", levelsForPage);

		boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag();// 政务版标识

		// HR枚举
		Map<String, Metadata> hrMetadata = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.hr);
		result.addObject("hrMetadata", hrMetadata);

		// 政务版--职级--start
		// 获取职级列表
		if (isGovVersion) {
			List<V3xOrgDutyLevel> dutyLevels = orgManagerDirect
					.getAllDutyLevels(CurrentUser.get().getLoginAccount(),
							false);
			List<V3xOrgDutyLevel> dutylevelsForPage = new ArrayList<V3xOrgDutyLevel>();
			// 过滤无效项
			for (V3xOrgDutyLevel dutylevel : dutyLevels) {
				if (dutylevel.getEnabled()) {
					dutylevelsForPage.add(dutylevel);
				}
			}
			result.addObject("dutyLevels", dutylevelsForPage);
		}
		// 政务版--职级--end

		return result;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator,
			RoleType.DepartmentAdmin, RoleType.HrAdmin })
	public ModelAndView updateBanchMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 读取输入参数并进行类型转换和初步检查
		// 要更新的人员ID
		String idsStr = request.getParameter("ids");
		List<Long> idList = new LinkedList<Long>();
		if (!StringUtils.isBlank(idsStr)) {
			String[] idStrArray = idsStr.split(",");
			for (String idStr : idStrArray) {
				try {
					idList.add(Long.valueOf(idStr));
				} catch (NumberFormatException e) {
					log.warn("无效的人员ID：" + idStr, e);
				}
			}
		}
		// 登录密码
		String password = request.getParameter("password");
		if (password != null && password.length() == 0) {
			password = null;
		}
		// 系统权限
		String securityIdsStr = request.getParameter("securityIds");
		List<Long> securityIdList = null;
		if (!StringUtils.isBlank(securityIdsStr)) {
			securityIdList = new ArrayList<Long>();
			String[] securityIdStrArray = securityIdsStr.split(",");
			for (String securityIdStr : securityIdStrArray) {
				try {
					securityIdList.add(Long.valueOf(securityIdStr));
				} catch (NumberFormatException e) {
					log.warn("无效的权限ID：" + securityIdStr);
				}
			}
		}
		// 所属部门
		String orgDepartmentIdStr = request.getParameter("orgDepartmentId");
		Long orgDepartmentId = null;
		if (!StringUtils.isBlank(orgDepartmentIdStr)) {
			try {
				orgDepartmentId = Long.valueOf(orgDepartmentIdStr);
			} catch (NumberFormatException e) {
				log.warn("无效的部门ID：" + orgDepartmentIdStr);
			}
		}
		// 职务级别
		String orgLevelIdStr = request.getParameter("orgLevelId");
		Long orgLevelId = null;
		if (!StringUtils.isBlank(orgLevelIdStr)) {
			try {
				orgLevelId = Long.valueOf(orgLevelIdStr);
			} catch (NumberFormatException e) {
				log.warn("无效的职务级别：" + orgLevelIdStr);
			}
		}
		// 主岗
		String orgPostIdStr = request.getParameter("orgPostId");
		Long orgPostId = null;
		if (!StringUtils.isBlank(orgPostIdStr)) {
			try {
				orgPostId = Long.valueOf(orgPostIdStr);
			} catch (NumberFormatException e) {
				log.warn("无效的岗位ID：" + orgPostIdStr);
			}
		}
		// 涉密等级
		String secretLevelStr = request.getParameter("secretLevel");
		Integer secretLevel = null;
		if (!StringUtils.isBlank(secretLevelStr)) {
			try {
				secretLevel = Integer.valueOf(secretLevelStr);
			} catch (NumberFormatException e) {
				log.warn("无效的涉密等级：" + secretLevelStr);
			}
		}
		// 性别
		String genderStr = request.getParameter("gender");
		Integer gender = null;
		if (!StringUtils.isBlank(genderStr)) {
			try {
				gender = Integer.valueOf(genderStr);
			} catch (NumberFormatException e) {
				log.warn("无效的性别：" + genderStr);
			}
		}
		// 人员类型
		String typeStr = request.getParameter("type");
		Byte type = null;
		if (!StringUtils.isBlank(typeStr)) {
			try {
				type = Byte.valueOf(typeStr);
			} catch (NumberFormatException e) {
				log.warn("无效的人员类型：" + typeStr);
			}
		}
		// 人员状态
		String stateStr = request.getParameter("state");
		Byte state = null;
		if (!StringUtils.isBlank(stateStr)) {
			try {
				state = Byte.valueOf(stateStr);
			} catch (NumberFormatException e) {
				log.warn("无效的人员状态：" + stateStr);
			}
		}
		// 首选语言
		String primaryLanguange = request.getParameter("primaryLanguange");
		if (StringUtils.isBlank(primaryLanguange)) {
			primaryLanguange = null;
		}
		// 账户状态
		String enabledStr = request.getParameter("enabled");
		Boolean enabled = null;
		if (!StringUtils.isBlank(enabledStr)) {
			if ("1".equals(enabledStr)) {
				enabled = Boolean.TRUE;
			} else if ("0".equals(enabledStr)) {
				enabled = Boolean.FALSE;
			} else {
				log.warn("无效的账户状态：" + enabledStr);
			}
		}
		User user = CurrentUser.get();
		// 查询Member，并更新要修改的属性
		List<V3xOrgMember> members = new LinkedList<V3xOrgMember>();
		for (Long id : idList) {
			boolean skipFlag = false;
			V3xOrgMember memberBeforeUpdate = null;
			try {
				memberBeforeUpdate = this.orgManager.getMemberById(id);
			} catch (BusinessException e) {
				log.warn("查询人员信息失败：" + id, e);
				skipFlag = false;
			}
			if (skipFlag || memberBeforeUpdate == null) {
				log.warn("查询人员信息失败：" + id);
				continue;
			}
			/******************** 成发集团项目 *****************************/
			/*
			 * if(secretLevel!= null &&
			 * !memberBeforeUpdate.getSecretLevel().equals(secretLevel)) { //
			 * 删除掉以前的待审核记录
			 * secretAuditManager.deleteWaitAudit(memberBeforeUpdate.getId());
			 * if(secretLevel.intValue() != SecretAudit.SECRET_LEVEL_NORMAL) {
			 * V3xOrgMember auditMember = new V3xOrgMember();
			 * BeanUtils.copyProperties(auditMember, memberBeforeUpdate);
			 * auditMember.setSecretLevel(secretLevel);
			 * secretAuditManager.create(auditMember); } }
			 */
			/******************** 成发集团项目 *****************************/

			// 2017-01-11 诚佰公司 密级空值判断
			if (secretLevel != null
					&& (memberBeforeUpdate.getSecretLevel() == null || !memberBeforeUpdate
							.getSecretLevel().equals(secretLevel))) {
				// 删除掉以前的待审核记录
				secretAuditManager.deleteWaitAudit(memberBeforeUpdate.getId());

				V3xOrgMember auditMember = new V3xOrgMember();
				BeanUtils.copyProperties(auditMember, memberBeforeUpdate);
				auditMember.setSecretLevel(secretLevel);
				secretAuditManager.create(auditMember);
			}
			// 诚佰公司

			V3xOrgMember member = new V3xOrgMember();
			BeanUtils.copyProperties(member, memberBeforeUpdate);
			// 登录密码
			if (password != null) {
				member.setPassword(password);
			}
			// 所属部门
			if (orgDepartmentId != null) {
				member.setOrgDepartmentId(orgDepartmentId);
			}
			// 职务级别
			if (orgLevelId != null) {
				member.setOrgLevelId(orgLevelId);
			}
			// 主岗
			if (orgPostId != null) {
				member.setOrgPostId(orgPostId);
			}
			// 性别
			if (gender != null) {
				member.setGender(gender);
			}
			// 人员类型
			if (type != null) {
				member.setType(type);
			}
			// 人员状态
			if (state != null) {
				member.setState(state);
			}
			// 首选语言
			if (primaryLanguange != null) {
				member.setPrimaryLanguange(primaryLanguange);
			}
			// 账户状态
			if (enabled != null) {
				member.setEnabled(enabled);
			}
			if (member.getEnabled() != null && member.getEnabled()) {// 启用，则改成在职状态
				if (member.getState().toString().equals("2")) {// 由离职改成在职,取消所有代理事项
					member.setState(new Byte("1"));
					this.agentIntercalateManager.cancelUserAgent(
							member.getId(), user);
				}
			}
			members.add(member);
		}
		List<V3xOrgMember> d = new LinkedList<V3xOrgMember>(members);
		// 批量更新，并处理返回信息
		OrganizationMessage msg = this.orgManager.updateMembers(members);

		UpdateBanchMemberEvent updateBanchMemberEvent = new UpdateBanchMemberEvent(
				this);
		updateBanchMemberEvent.setMembers(d);
		EventDispatcher.fireEvent(updateBanchMemberEvent);
		// 成功的
		for (OrganizationMessage.OrgMessage successMsg : msg.getSuccessMsgs()) {
			V3xOrgMember member = (V3xOrgMember) successMsg.getEnt();
			// 修改个人菜单权限
			if (securityIdList != null) {
				this.menuManager.saveMemberSecurity(member.getId(),
						member.getOrgAccountId(), securityIdList);
			}
			// 账户停用的踢出
			if (member.getEnabled() != null && !member.getEnabled()) {
				OnlineRecorder.moveToOffline(member.getLoginName(),
						LoginOfflineOperation.adminKickoff);
				this.agentIntercalateManager.cancelUserAgent(member.getId(),
						user);
			}
		}
		// 记录日志
		this.appLogManager.insertLog(user,
				AppLogAction.Organization_BanchEditMember, user.getName());
		// 反馈错误、成功信息
		ResourceBundle resourceBundle = ResourceBundleUtil
				.getResourceBundle(
						"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
						user.getLocale());
		PrintWriter out = response.getWriter();
		out.println("<script>");
		StringBuffer sb = new StringBuffer();
		if (msg.isSuccess()) {
			sb.append("alert(\'");
			sb.append(StringEscapeUtils.escapeJavaScript(ResourceBundleUtil
					.getString(resourceBundle, "batchedit.success")));
			sb.append("\');");
		} else {
			sb.append("alert(\'");
			sb.append(
					StringEscapeUtils.escapeJavaScript(ResourceBundleUtil
							.getString(resourceBundle,
									"batchedit.failure.members")))
					.append("\\n");
			for (OrganizationMessage.OrgMessage errorMsg : msg.getErrorMsgs()) {
				sb.append(StringEscapeUtils.escapeJavaScript(errorMsg.getEnt()
						.getName()));
				sb.append(":");
				sb.append(StringEscapeUtils.escapeJavaScript(ResourceBundleUtil
						.getString(resourceBundle, "MessageStatus."
								+ errorMsg.getCode().name())));
				sb.append("\\n");
			}
			sb.append("\');");
		}
		out.println(sb.toString());
		out.println("window.returnValue='true';");
		out.println("window.close();");
		out.println("</script>");
		out.flush();
		return null;
	}

	/*
	 * @CheckRoleAccess(roleTypes={RoleType.Administrator,
	 * RoleType.DepartmentAdmin, RoleType.HrAdmin}) public ModelAndView
	 * updateBanchMember(HttpServletRequest request, HttpServletResponse
	 * response) throws Exception { //马传佳写的这部分代码需要细查，可能存在隐患 PrintWriter out =
	 * response.getWriter(); String idsString = request.getParameter("ids");
	 * StringBuffer sbrPost = new StringBuffer(); StringBuffer sbrForm = new
	 * StringBuffer(); if(idsString!=null && idsString!=""){ // 人员状态 Byte state
	 * = null; String stateStr = request.getParameter("state"); if
	 * (!StringUtils.isBlank(stateStr)) { try { state = Byte.valueOf(stateStr);
	 * } catch (NumberFormatException e) { log.warn("无效的人员状态：" + stateStr, e); }
	 * } // 账户状态 Boolean enabled = null; String enabledStr =
	 * request.getParameter("enabled"); if (!StringUtils.isBlank(enabledStr)) {
	 * if ("1".equals(enabledStr)) { enabled = Boolean.TRUE; } else if
	 * ("0".equals(enabledStr)) { enabled = Boolean.FALSE; } else {
	 * log.warn("无效的账户状态：" + enabledStr); } } String [] idsArry =
	 * idsString.split(","); List<V3xOrgEntity> members = new
	 * ArrayList<V3xOrgEntity>(); for(int i=0;i<idsArry.length;i++){
	 * request.setAttribute("id", idsArry[i]); V3xOrgMember member =
	 * orgManagerDirect.getMemberById(Long.valueOf(idsArry[i]));
	 * members.add(member); // 为事件调用记录修改前的人员 V3xOrgMember memberBeforeUpdate =
	 * new V3xOrgMember(); BeanUtils.copyProperties(memberBeforeUpdate, member);
	 * if(request.getParameter("orgDepartmentId")!=null &&
	 * request.getParameter("orgDepartmentId").length()!=0){
	 * member.setOrgDepartmentId
	 * (Long.valueOf(request.getParameter("orgDepartmentId")));
	 * //如果是部门主管，离职人员跳动部门是否允许?--不允许ajax前台判断 }
	 * if(request.getParameter("orgLevelId")!=null &&
	 * request.getParameter("orgLevelId").length()!=0){
	 * member.setOrgLevelId(Long.valueOf(request.getParameter("orgLevelId"))); }
	 * if(request.getParameter("orgPostId")!=null &&
	 * request.getParameter("orgPostId").length()!=0){ List<MemberPost>
	 * memberpost = member.getSecond_post(); //主岗 boolean f = false;
	 * for(MemberPost secPost:memberpost){
	 * if(secPost.getPostId().equals(Long.valueOf
	 * (request.getParameter("orgPostId")))){
	 * if(secPost.getDepId().equals(member.getOrgDepartmentId())){ f=true;
	 * break; } } } if(!f){
	 * member.setOrgPostId(Long.valueOf(request.getParameter("orgPostId")));
	 * }else{ sbrPost.append(member.getName()).append(","); }
	 * 
	 * } if(request.getParameter("gender")!=null &&
	 * request.getParameter("gender").length()!=0){
	 * member.setGender(Integer.parseInt(request.getParameter("gender"))); }
	 * if(request.getParameter("type")!=null &&
	 * request.getParameter("type").length()!=0){
	 * member.setType(Byte.valueOf(request.getParameter("type"))); } // 人员状态 if
	 * (null != state) { member.setState(state); }
	 * if(request.getParameter("primaryLanguange")!=null &&
	 * request.getParameter("primaryLanguange").length()!=0){
	 * member.setPrimaryLanguange(request.getParameter("primaryLanguange")); }
	 * // 账户状态 if (null != enabled) { member.setEnabled(enabled); }
	 * orgManagerDirect.updateEntity(member); //修改个人菜单权限 String securityIdsStr =
	 * request.getParameter("securityIds"); if (null != securityIdsStr &&
	 * securityIdsStr.length() > 0) { String[] securityIds =
	 * securityIdsStr.split(","); List<Long> securityIdsList = new
	 * ArrayList<Long>(); for(String idStr : securityIds){
	 * securityIdsList.add(Long.parseLong(idStr)); }
	 * this.menuManager.saveMemberSecurity(member.getId(),
	 * member.getOrgAccountId(), securityIdsList); } // 触发更新人员事件
	 * eventListener.updateMember(memberBeforeUpdate, member); }
	 * 
	 * Map<String,Object> properties = new HashMap<String, Object>();
	 * if(request.getParameter("orgLevelId")!=null &&
	 * request.getParameter("orgLevelId").length()!=0){
	 * properties.put("orgLevelId"
	 * ,Long.valueOf(request.getParameter("orgLevelId"))); }
	 * if(request.getParameter("orgDepartmentId")!=null &&
	 * request.getParameter("orgDepartmentId").length()!=0){
	 * properties.put("orgDepartmentId"
	 * ,Long.valueOf(request.getParameter("orgDepartmentId"))); }
	 * if(request.getParameter("orgPostId")!=null &&
	 * request.getParameter("orgPostId").length()!=0){
	 * properties.put("orgPostId"
	 * ,Long.valueOf(request.getParameter("orgPostId"))); }
	 * if(request.getParameter("gender")!=null &&
	 * request.getParameter("gender").length()!=0){
	 * properties.put("gender",Integer
	 * .parseInt(request.getParameter("gender"))); }
	 * if(request.getParameter("type")!=null &&
	 * request.getParameter("type").length()!=0){
	 * properties.put("type",Byte.valueOf(request.getParameter("type"))); } //
	 * 人员状态 if (null != state) { properties.put("state", state); }
	 * if(request.getParameter("primaryLanguange")!=null &&
	 * request.getParameter("primaryLanguange").length()!=0){
	 * properties.put("primaryLanguange"
	 * ,request.getParameter("primaryLanguange")); } // 账户状态 if (null !=
	 * enabled) { properties.put("enabled", enabled); }
	 * orgManagerDirect.updateEntitys(members,properties); } //记录日志 User user =
	 * CurrentUser.get(); appLogManager.insertLog(user,
	 * AppLogAction.Organization_BanchEditMember, user.getName()); out =
	 * response.getWriter(); out.println("<script>");
	 * if(sbrPost.toString().length()>0){ out.println("alert(\'"+sbrPost+
	 * "\' +parent.v3x.getMessage('organizationLang.orgainzation_brother_post_name'));"
	 * ); } if(sbrForm.toString().length()>0){ out.println("alert(\'"+sbrForm+
	 * "\'+parent.v3x.getMessage('organizationLang.orgainzation_brother_form_name'));"
	 * ); } out.println("window.returnValue='true';");
	 * out.println("window.close();"); out.println("</script>"); return null; }
	 */
	/**
	 * 修改人员管理界面方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator,
			RoleType.DepartmentAdmin, RoleType.HrAdmin })
	public ModelAndView updateMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String ldapEntry = request.getParameter("ldapUserCodes");
		String imageid = request.getParameter("fileId");
		String imagedate = request.getParameter("createDate");
		String forAgentState = request.getParameter("forAgentState");

		// 政务版：新增HR的字段到人员管理页面
		String ID_card = request.getParameter("ID_card"); // 身份证号
		String edu_level = request.getParameter("edu_level"); // 最高学历
		String political_position = request.getParameter("political_position"); // 政治面貌
		String degreeLevel = request.getParameter("degreeLevel"); // 最高学位
		boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag();// 政务版标识

		Long imageId = null;
		Date imageDate = null;
		if (Strings.isNotBlank(imageid)) {
			imageId = Long.parseLong(imageid);
		}
		if (Strings.isNotBlank(imagedate)) {
			imageDate = Datetimes.parseDatetime(imagedate);
		}
		User user = CurrentUser.get();
		V3xOrgMember model = new V3xOrgMember();
		bind(request, model);
		boolean orgEnable_New = model.getEnabled();
		V3xOrgMember member = orgManagerDirect.getMemberById(model.getId());
		boolean orgEnable = member.getEnabled();
		// 如果人员信息不存在则提示用户
		if (member == null || member.getIsDeleted()) {
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_null'));");
			out.println("</script>");
			out.flush();
			return super.refreshWorkspace();
		}
		StaffInfo staffinfo = staffInfoManager.getStaffInfoById(member.getId());
		if (staffinfo == null) {
			StaffInfo staff = new StaffInfo();
			staff.setOrg_member_id(member.getId());
			staff.setImage_id(imageId);
			staff.setImage_datetime(imageDate);
			// 政务版 添加四个字段属性到HR表
			// branches_a8_v350_r_gov 杨帆 修改添加人员时设置身份证号、最高学历、政治面貌、最高学位四个字段 start
			if (isGovVersion) {
				// 身份证号
				if (ID_card != null) {
					staff.setID_card(ID_card);
				}
				// 最高学历
				if (edu_level != null) {
					staff.setEdu_level(Integer.parseInt(edu_level));
				}
				// 政治面貌
				if (political_position != null) {
					staff.setPolitical_position(Integer
							.parseInt(political_position));
				}
				// 最高学位
				if (degreeLevel != null) {
					staff.setDegreeLevel(degreeLevel);
				}
			}
			// branches_a8_v350_r_gov 杨帆 修改添加人员时设置身份证号、最高学历、政治面貌、最高学位四个字段 end
			staffInfoManager.addStaffInfo(staff);
		} else {
			staffinfo.setOrg_member_id(member.getId());
			staffinfo.setImage_id(imageId);
			staffinfo.setImage_datetime(imageDate);
			// 政务版 添加四个字段属性到HR表
			if (isGovVersion) {
				// 身份证号
				if (ID_card != null) {
					staffinfo.setID_card(ID_card);
				}
				// 最高学历
				if (edu_level != null) {
					staffinfo.setEdu_level(Integer.parseInt(edu_level));
				}
				// 政治面貌
				if (political_position != null) {
					staffinfo.setPolitical_position(Integer
							.parseInt(political_position));
				}
				// 最高学位
				if (degreeLevel != null) {
					staffinfo.setDegreeLevel(degreeLevel);
				}
			}
			staffInfoManager.updateStaffInfo(staffinfo);
		}

		// 为事件调用记录修改前的人员
		V3xOrgMember memberBeforeUpdate = new V3xOrgMember();
		BeanUtils.copyProperties(memberBeforeUpdate, member);

		// 记录登录名是否修改了
		boolean isLoginNameModifyed = false;
		String oldLoginName = "";
		if (!model.getLoginName().equals(member.getLoginName())) {
			isLoginNameModifyed = true;
			oldLoginName = member.getLoginName();
		}
		// 人员排序号的重复处理
		Integer orgSortId = member.getSortId();
		Integer sortId = Integer.valueOf(request.getParameter("sortId"));
		String isInsert = request.getParameter("isInsert");
		if (!orgSortId.equals(sortId)
				&& isInsert.equals("1")
				&& orgManagerDirect.isPropertyDuplicated(
						V3xOrgMember.class.getSimpleName(), "sortId", sortId,
						member.getId())) {
			orgManagerDirect.insertRepeatSortNum(V3xOrgMember.class
					.getSimpleName(), CurrentUser.get().getLoginAccount(),
					sortId);
		}

		PrintWriter out = response.getWriter();
		model.setStatus(member.getStatus());
		/******************** 成发集团项目 *****************************/
		if (!member.getSecretLevel().equals(model.getSecretLevel())) {
			// 删除掉以前的待审核记录
			secretAuditManager.deleteWaitAudit(model.getId());
			secretAuditManager.create(model);
			model.setSecretLevel(member.getSecretLevel());
		}
		/******************** 成发集团项目 *****************************/
		BeanUtils.copyProperties(member, model);
		member.setProperty("officeNum", request.getParameter("officeNum"));
		String strSecondPostIds = request.getParameter("secondPostIds");
		String[] arrSecondPosts = strSecondPostIds.split(",");
		Long secondDeptId = null;
		Long secondPostId = null;
		if (null != arrSecondPosts && arrSecondPosts.length > 0) {
			for (String secondpostid : arrSecondPosts) {
				if (secondpostid.indexOf("_") != -1) {
					String[] arrDeptPosts = secondpostid.split("_");
					if (StringUtils.isNotBlank(arrDeptPosts[0])
							&& StringUtils.isNotBlank(arrDeptPosts[1])) {
						secondDeptId = new Long(arrDeptPosts[0]);
						secondPostId = new Long(arrDeptPosts[1]);
					}
				} else {
					if (StringUtils.isNotBlank(secondpostid)) {
						secondDeptId = member.getOrgDepartmentId();
						secondPostId = new Long(secondpostid);
					}
				}
				if (secondDeptId != null && secondPostId != null) {
					// 校验副岗是否和主岗重复
					if (secondDeptId.equals(member.getOrgDepartmentId())
							&& secondPostId.equals(member.getOrgPostId())) {
						out.println("<script>");
						out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_member_secondPost_repeat'));");
						out.println("</script>");
						// 后续操作
						super.rendJavaScript(response, "parent.toEditMember()");
						return null;
					}
					member.addSecondPost(secondDeptId, secondPostId);
				}
			}

		}
		V3xOrgMember newMember = new V3xOrgMember();
		BeanUtils.copyProperties(newMember, member);
		orgManager.updateMember(member);
		// 检查人员密码是否进行了修改,而且没有修改登录名的情况下记录日志
		String password = newMember.getPassword();
		if (password != null && !StringUtils.isEmpty(password)
				&& !password.equals(V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD)
				&& !isLoginNameModifyed) {
			appLogManager.insertLog(user,
					AppLogAction.Systemmanager_UpdateUserPassWord,
					user.getName(), member.getName());
		}

		// 把副岗位添加部门岗位中
		// addDeptPost(member);

		// 修改个人菜单权限
		String securityIdsStr = request.getParameter("securityIds");
		if (null != securityIdsStr && securityIdsStr.length() > 0) {
			String[] securityIds = securityIdsStr.split(",");
			List<Long> securityIdsList = new ArrayList<Long>();
			for (String idStr : securityIds) {
				securityIdsList.add(Long.parseLong(idStr));
			}
			this.menuManager.saveMemberSecurity(member.getId(),
					member.getOrgAccountId(), securityIdsList);
		}
		// 如果人员被停用则强制此人员下线
		if (!orgEnable_New && orgEnable) {
			OnlineRecorder.moveToOffline(member.getLoginName(),
					LoginOfflineOperation.adminKickoff);
			agentIntercalateManager.cancelUserAgent(member.getId(), user);
		} else if ("2".equals(forAgentState) && orgEnable_New) {// 离职->在职：清除所有代理关系
			agentIntercalateManager.cancelUserAgent(member.getId(), user);
		}

		// 触发更新人员事件
		eventListener.updateMember(memberBeforeUpdate, newMember);

		// 全文检索统一入库
		try {
			if (!member.getEnabled()) {
				indexManager.deleteFromIndex(
						ApplicationCategoryEnum.organization, member.getId());
			} else {
				updateIndexManager.update(member.getId(),
						ApplicationCategoryEnum.organization.getKey());
			}
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
		}

		// 日志
		appLogManager.insertLog(user, AppLogAction.Organization_UpdateMember,
				user.getName(), member.getName());
		if (LDAPConfig.getInstance().getIsEnableLdap()
				&& SystemEnvironment
						.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
								.name())) {
			try {
				List<V3xOrgMember> memberList = null;
				V3xOrgMember memberLdap = new V3xOrgMember();
				BeanUtils.copyProperties(memberLdap, newMember);
				if (isLoginNameModifyed) {
					memberList = new ArrayList<V3xOrgMember>();
					memberLdap.setLoginName(oldLoginName);
					memberList.add(memberLdap);
					event.deleteAllBinding(orgManagerDirect, memberList);
				}
				if (event.getLdapAdExUnitCode(model.getLoginName()).equals(
						ldapEntry)
						&& !isLoginNameModifyed
						&& !com.seeyon.v3x.common.ldap.config.LDAPConfig
								.getInstance().isDisabledModifyPassWord()
						&& !StringUtils.isEmpty(password)
						&& !password
								.equals(V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD)) {
					event.changePassword(memberBeforeUpdate, memberLdap);
					appLogManager.insertLog(user,
							AppLogAction.LDAP_PassWord_Update,
							memberLdap.getName(), ldapEntry);
				} else if (!event
						.getLdapAdExUnitCode(memberLdap.getLoginName()).equals(
								ldapEntry)) {
					String[] errorResult = event.addMember(memberLdap,
							ldapEntry);

					if (errorResult != null && errorResult.length > 0) {
						String jsContent = "";
						for (int i = 0; i < errorResult.length; i++) {
							jsContent += errorResult[i] + "\\n";
						}
						out.println("<script>");
						out.println("alert('" + jsContent + "');");
						out.println("</script>");
					}
					appLogManager.insertLog(user,
							AppLogAction.LDAP_Account_Bing_Create,
							memberLdap.getName(), ldapEntry);
					if (!com.seeyon.v3x.common.ldap.config.LDAPConfig
							.getInstance().isDisabledModifyPassWord()
							&& !StringUtils.isEmpty(password)
							&& !password
									.equals(V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD)) {
						event.changePassword(memberBeforeUpdate, memberLdap);
						appLogManager.insertLog(user,
								AppLogAction.LDAP_PassWord_Update,
								memberLdap.getName(), ldapEntry);
					}
				}
			} catch (Exception e) {
				log.error("ldap/ad 添加人员绑定不成功！", e);
			}
		}
		// 如果调整部门，则追加新部门模板推送到个人首页-dongyj
		/*
		 * if(isChangeOrgInfo(memberBeforeUpdate,member)){
		 * templeteConfigManager.
		 * pushNewOrgEntityTemplete4Member(V3xOrgEntity.ORGENT_TYPE_MEMBER,
		 * member.getId(), member.getId()); }
		 */

		super.rendJavaScript(
				response,
				"parent.parent.detailFrame.location.href=\"/seeyon/common/detail.jsp\";parent.parent.listFrame.location.reload(true);");
		return null;

	}

	// 添加部门岗位 wusb
	private void addDeptPost(V3xOrgMember member) throws Exception {
		Long deptId = member.getOrgDepartmentId();
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
		if (dept != null) {
			// 部门中已有的岗位
			List<Long> deptPostList = dept.getPosts();
			List<Long> newDeptPostList = new ArrayList<Long>();
			newDeptPostList.addAll(deptPostList);
			// 人员中副岗所选择岗位
			List<MemberPost> secondPostList = member.getSecond_post();
			boolean isAdd = false;
			for (MemberPost secondPost : secondPostList) {
				if (!newDeptPostList.contains(secondPost.getPostId())) {
					// 不存在,添加到所在部门的岗位里，则添加进去
					newDeptPostList.add(secondPost.getPostId());
					isAdd = true;
				}
			}
			if (isAdd) {// 如果添加新的岗位，才进行修改部门岗位
				V3xOrgDepartment deptBeforeUpdate = new V3xOrgDepartment();
				BeanUtils.copyProperties(deptBeforeUpdate, dept);
				dept.setPosts(newDeptPostList);
				orgManagerDirect.updateEntity(dept);
				// 触发更新部门事件
				eventListener.updateDepartment(deptBeforeUpdate, dept);
			}
		}

	}

	/**
	 * 单位管理员 取消分配设置 人员调出操作
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin,
			RoleType.DepartmentAdmin })
	public ModelAndView cancelMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] membetIds = request.getParameterValues("id");
		int successNum = 0; // 调动成功人数
		String faileMemberNames = null; // 失败者姓名
		// 是否是部门管理员登录
		String deptAdmin = request.getParameter("deptAdmin");
		V3xOrgMember member = new V3xOrgMember();
		PrintWriter out = response.getWriter();
		boolean hasLdapPlugin = LDAPConfig.getInstance().getIsEnableLdap()
				&& SystemEnvironment
						.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
								.name());
		List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
		for (String string : membetIds) {
			Long ids = Long.parseLong(string);
			try {
				member = orgManager.getMemberById(ids);
				if (member == null) {
					continue;
				}
				boolean isBulAuditor = bulTypeManager.isAuditorOfBul(ids);
				boolean isInquiryAuditor = inquiryManager
						.isAuditorOfInquiry(ids);
				boolean isNewsAuditor = newsTypeManager.isAuditorOfNews(ids);
				// AEIGHT-10172
				if (isBulAuditor) {
					out = response.getWriter();
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_move_member_public_information_auditor_bul','"
							+ member.getName() + "'));");
					out.println("</script>");
					out.flush();
					return super.redirectModelAndView(
							"/organization.do?method=organizationFrame&from=Member&deptAdmin="
									+ deptAdmin, "parent");
				}
				if (isInquiryAuditor) {
					out = response.getWriter();
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_move_member_public_information_auditor_inquiry','"
							+ member.getName() + "'));");
					out.println("</script>");
					out.flush();
					return super.redirectModelAndView(
							"/organization.do?method=organizationFrame&from=Member&deptAdmin="
									+ deptAdmin, "parent");
				}
				if (isNewsAuditor) {
					out = response.getWriter();
					out.println("<script>");
					out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_move_member_public_information_auditor_news','"
							+ member.getName() + "'));");
					out.println("</script>");
					out.flush();
					return super.redirectModelAndView(
							"/organization.do?method=organizationFrame&from=Member&deptAdmin="
									+ deptAdmin, "parent");
				}

				V3xOrgMember memberBeforeUpdate = new V3xOrgMember();
				BeanUtils.copyProperties(memberBeforeUpdate, member);
				member.setIsAssigned(false);
				member.setEnabled(false);
				orgManager.updateMember(member);
				successNum++;
				// 人员停用强制下线
				OnlineRecorder.moveToOffline(member.getLoginName(),
						LoginOfflineOperation.adminKickoff);
				// 删除该用户原模板配置
				templeteConfigManager.clearTempleteConfig(ids);
				memberList.add(member);
				// 触发更新人员事件
				eventListener.updateMember(memberBeforeUpdate, member);
				if (hasLdapPlugin) {
					try {
						List<V3xOrgMember> memberList1 = new ArrayList<V3xOrgMember>();
						memberList1.add(member);
						event.deleteAllBinding(orgManagerDirect, memberList1);
					} catch (Exception e) {
						log.error("ldap_ad 删除人员绑定不成功！", e);
					}
				}
			} catch (Exception e1) {
				log.error(e1.getMessage(), e1);
			}
		}

		// 日志信息
		List<String[]> appLogs = new ArrayList<String[]>();
		User user = CurrentUser.get();
		V3xOrgAccount account = orgManagerDirect.getAccountById(user
				.getAccountId());
		for (V3xOrgMember memberLog : memberList) {
			String[] appLog = new String[3];
			appLog[2] = user.getName();
			appLog[0] = memberLog.getName();
			appLog[1] = account.getName();
			appLogs.add(appLog);
		}
		appLogManager.insertLogs(user, AppLogAction.Organization_MoveMember,
				appLogs);

		// 全不成功
		if (successNum == 0) {
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_delete_member_pendingaffair'));");
			out.println("</script>");
			out.flush();
		}// 全部成功
		else if (successNum == membetIds.length) {
			Locale local = LocaleContext.getLocale(request);
			out.println("<script>");
			out.println("alert('"
					+ Constant.getString("organization.yes", local) + "');");
			out.println("</script>");
			out.flush();
		}// 有不成功的
		else {
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_delete_someMember_pendingaffair','"
					+ faileMemberNames + "'));");
			out.println("</script>");
			out.flush();
		}
		return super.redirectModelAndView(
				"/organization.do?method=organizationFrame&from=Member&deptAdmin="
						+ deptAdmin, "parent");
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin,
			RoleType.DepartmentAdmin })
	public ModelAndView destroyMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 是否是部门管理员登录
		String deptAdmin = request.getParameter("deptAdmin");
		String isTree = request.getParameter("isTree");
		String[] membetIds = request.getParameterValues("id");
		Locale local = LocaleContext.getLocale(request);
		PrintWriter out = response.getWriter();

		List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>(
				membetIds.length);
		boolean hasLdapPlugin = LDAPConfig.getInstance().getIsEnableLdap()
				&& SystemEnvironment
						.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
								.name());
		for (String string : membetIds) {
			Long ids = Long.parseLong(string);
			// 为事件调用取人员实体
			V3xOrgMember member = orgManagerDirect.getMemberById(ids);
			if (member == null) {
				continue;
			}
			boolean isBulAuditor = bulTypeManager.isAuditorOfBul(ids);
			boolean isInquiryAuditor = inquiryManager.isAuditorOfInquiry(ids);
			boolean isNewsAuditor = newsTypeManager.isAuditorOfNews(ids);
			if (isBulAuditor || isInquiryAuditor || isNewsAuditor) {
				out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_delete_member_public_information_auditor','"
						+ member.getName() + "'));");
				out.println("</script>");
				out.flush();
				return super.redirectModelAndView(
						"/organization.do?method=organizationFrame&from=Member&deptAdmin="
								+ deptAdmin + "&isTree=" + isTree, "parent");
			}
			member.setIsDeleted(true);
			memberList.add(member);

			// projectManager.retakeClew(ids);//如果删除的人是项目管理员。那么给发起者发消息
			// AEIGHT-7191 sunj删除人员不判断签章，删除人员时会根据之前写的签章监听事件去清除签章授权
			/*
			 * if(signetManager.hasSignet(ids)) { out = response.getWriter();
			 * out.println("<script>"); out.println(
			 * "alert(parent.v3x.getMessage('organizationLang.orgainzation_delete_member_has_signet','"
			 * +member.getName()+"'));"); out.println("</script>"); out.flush();
			 * return super.redirectModelAndView(
			 * "/organization.do?method=organizationFrame&from=Member&deptAdmin="
			 * +deptAdmin, "parent"); }
			 */

			if (hasLdapPlugin) {
				try {
					List<V3xOrgMember> memberList1 = new ArrayList<V3xOrgMember>();
					memberList1.add(member);
					event.deleteAllBinding(orgManagerDirect, memberList1);
				} catch (Exception e) {
					log.error("ldap_ad 删除人员绑定不成功！", e);
					throw new Exception("ldap_ad 删除人员绑定不成功！", e);
				}
			}
			// 强制此人员下线
			OnlineRecorder.moveToOffline(member.getLoginName(),
					LoginOfflineOperation.adminKickoff);
			// 删除代理信息
			agentIntercalateManager.cancelUserAgent(member.getId(),
					CurrentUser.get());
			orgManager.deleteMember(member);
			// 删除掉以前的待审核记录
			secretAuditManager.deleteWaitAudit(member.getId());
			// 全文检索删除此人
			try {
				indexManager.deleteFromIndex(
						ApplicationCategoryEnum.organization, ids);
			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		// 日志信息
		List<String[]> appLogs = new ArrayList<String[]>();
		User user = CurrentUser.get();
		// 全部成功后触发删除人员事件
		for (V3xOrgMember member : memberList) {
			eventListener.deleteMember(member);
			String[] appLog = new String[2];
			appLog[0] = user.getName();
			appLog[1] = member.getName();
			appLogs.add(appLog);
		}
		appLogManager.insertLogs(user, AppLogAction.Organization_DeleteMember,
				appLogs);

		// 提示用户操作成功
		out.println("<script>");
		out.println("alert('" + Constant.getString("organization.yes", local)
				+ "');");
		out.println("</script>");
		out.flush();
		return super.redirectModelAndView(
				"/organization.do?method=organizationFrame&from=Member&deptAdmin="
						+ deptAdmin + "&isTree=" + isTree, "parent");
	}

	/**
	 * 进入组的数据方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin, RoleType.Administrator,
			RoleType.HrAdmin })
	public ModelAndView listTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ListSearchHelper.pickupExpression(request, null);
		User user = CurrentUser.get();
		String accountName = orgManagerDirect.getAccountById(
				user.getLoginAccount()).getName();
		ModelAndView result = null;
		if (user.isGroupAdmin()) {
			result = new ModelAndView("organization/team/groupListTeam");
		} else {
			result = new ModelAndView("organization/team/listTeam");
		}
		List<V3xOrgEntity> teamlist = orgManagerDirect.getEntityList(
				V3xOrgTeam.class.getSimpleName(), "type",
				V3xOrgEntity.TEAM_TYPE_SYSTEM,
				Long.valueOf(user.getLoginAccount()), true);

		List<WebV3xOrgTeam> resultlist = new ArrayList<WebV3xOrgTeam>();

		for (V3xOrgEntity teamEnt : teamlist) {
			V3xOrgTeam team = (V3xOrgTeam) teamEnt;
			WebV3xOrgTeam webTeam = new WebV3xOrgTeam();
			webTeam.setV3xOrgTeam(team);
			// 组主管解析
			List<Long> lead = team.getLeaders();
			String leads = "";
			for (Long id : lead) {
				V3xOrgMember vm = orgManagerDirect.getMemberById(id);
				if (vm.getEnabled() && !vm.getIsDeleted())
					leads += (String.valueOf(id) + ",");
			}
			if (!StringUtils.isEmpty(leads)) {
				leads.substring(leads.length() - 1, leads.length());
			}
			webTeam.setMemberIDs(leads);

			// 取得组的部门名称
			V3xOrgEntity dept = orgManagerDirect.getEntityNoRelation(
					V3xOrgDepartment.class.getSimpleName(), "id",
					team.getDepId(), user.getLoginAccount());
			if (null != dept) {
				webTeam.setDept((V3xOrgDepartment) dept);
			} else {
				V3xOrgDepartment accountDept = new V3xOrgDepartment();
				accountDept.setName(accountName);
				webTeam.setDept(accountDept);
			}

			resultlist.add(webTeam);
		}
		result.addObject("teamlist", resultlist);
		result.addObject("isGroupAdmin", user.isGroupAdmin());
		return result;
	}

	/**
	 * 进入组的数据方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 *             说明：添加组织模型，部门管理员登录后只能查看其所在的部门下的组信息
	 */
	@CheckRoleAccess(roleTypes = { RoleType.DepartmentAdmin })
	public ModelAndView listTeamDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ListSearchHelper.pickupExpression(request, null);
		User user = CurrentUser.get();
		String accountName = orgManagerDirect.getAccountById(
				user.getLoginAccount()).getName();
		ModelAndView result = null;
		result = new ModelAndView("organization/team/listTeam");
		String deptAdmin = request.getParameter("deptAdmin");
		// 定义保存指定部门相关的组列表
		List<V3xOrgEntity> teamListByDeptAdmin = new ArrayList<V3xOrgEntity>();
		// 定义保存全部基于Web组列表
		List<WebV3xOrgTeam> resultlist = new ArrayList<WebV3xOrgTeam>();
		// 保存全部的部门及其子部门
		List<V3xOrgDepartment> departments = new ArrayList<V3xOrgDepartment>();
		if (deptAdmin != null && "1".equals(deptAdmin)) {
			// 定义部门角色
			V3xOrgRole depAdminRole = orgManagerDirect
					.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPADMIN);
			// 根据所在的部门角色，取得相应的关系列表
			List<V3xOrgRelationship> rels = orgManagerDirect.getRelationships(
					"type", V3xOrgEntity.ORGREL_TYPE_MEMBER_DEPROLE,
					"backupId", depAdminRole.getId(), "sourceId", user.getId());

			if (rels != null && rels.size() > 0) {
				for (V3xOrgRelationship rel : rels) {
					// 将部门管理员所对应的部门加入部门列表中
					V3xOrgDepartment department = orgManagerDirect
							.getDepartmentById(rel.getObjectiveId());
					if (department != null && !departments.contains(department)) {
						// 添加部门到部门列表中
						departments.add(department);
						// 查询出当前部门对应的部门组列表
						List<V3xOrgTeam> deptTeams = orgManagerDirect
								.getDepartmentTeam(department.getId());
						if (deptTeams != null && deptTeams.size() > 0) {
							for (V3xOrgTeam deptTeam : deptTeams) {
								if (deptTeam.getType() == V3xOrgEntity.TEAM_TYPE_SYSTEM
										&& !teamListByDeptAdmin
												.contains(deptTeam)) {
									teamListByDeptAdmin.add(deptTeam);
								}
							}
						}
					}
					// 不采用分页查询取得全部的子部门
					List<V3xOrgDepartment> subDepartments = orgManagerDirect
							.getChildDepartments(rel.getObjectiveId(), false);
					// 查询子部门相关的组信息
					if (subDepartments != null && subDepartments.size() > 0) {
						for (V3xOrgDepartment dept : subDepartments) {
							if (!departments.contains(dept)) {
								departments.add(dept);
								// 根据所在的的部门编号，查询出相关的部门对应的组列表
								List<V3xOrgTeam> deptTeams = orgManagerDirect
										.getDepartmentTeam(dept.getId());
								if (deptTeams != null && deptTeams.size() > 0) {
									for (V3xOrgTeam deptTeam : deptTeams) {
										// 如果部门相应的组列表中，不包含当相关的的组成员，，同时部门成员对象是内人员
										if (deptTeam.getType() == V3xOrgEntity.TEAM_TYPE_SYSTEM
												&& !teamListByDeptAdmin
														.contains(deptTeam)) {
											teamListByDeptAdmin.add(deptTeam);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		// 分页
		teamListByDeptAdmin = pagenate(teamListByDeptAdmin);

		for (V3xOrgEntity teamEnt : teamListByDeptAdmin) {
			V3xOrgTeam team = (V3xOrgTeam) teamEnt;
			WebV3xOrgTeam webTeam = new WebV3xOrgTeam();
			webTeam.setV3xOrgTeam(team);
			// 组主管解析
			List<Long> lead = team.getLeaders();
			String leads = "";
			for (Long id : lead) {
				V3xOrgMember vm = orgManagerDirect.getMemberById(id);
				if (vm.getEnabled() && !vm.getIsDeleted())
					leads += (String.valueOf(id) + ",");
			}
			if (!StringUtils.isEmpty(leads)) {
				leads.substring(leads.length() - 1, leads.length());
			}
			webTeam.setMemberIDs(leads);

			// 取得组的部门名称
			boolean flag = false;
			for (V3xOrgDepartment dept : departments) {
				if (dept.getId().equals(team.getDepId())) {
					webTeam.setDept(dept);
					flag = true;
					break;
				}
			}
			if (!flag) {
				V3xOrgDepartment accountDept = new V3xOrgDepartment();
				accountDept.setName(accountName);
				webTeam.setDept(accountDept);
			}
			resultlist.add(webTeam);
		}
		result.addObject("teamlist", resultlist);
		result.addObject("deptAdmin", deptAdmin);
		return result;
	}

	/**
	 * 进入添加组成员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.GroupAdmin,
			RoleType.DepartmentAdmin, RoleType.HrAdmin })
	// && Functions.isRole(V3xOrgEntity.ORGENT_META_KEY_DEPADMIN,
	// CurrentUser.get()) url地址拼接参数，可能还需要这个条件判断
	public ModelAndView addTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("organization/team/team");

		// 取得最大排序号
		Integer maxSortNum = orgManagerDirect.getMaxSortNum(V3xOrgTeam.class
				.getSimpleName(), CurrentUser.get().getLoginAccount());
		WebV3xOrgTeam webTeam = new WebV3xOrgTeam();
		V3xOrgTeam team = new V3xOrgTeam();
		team.setEnabled(true);
		team.setIsPrivate(true);
		team.setSortId(maxSortNum + 1);
		if (CurrentUser.get().isGroupAdmin()) {
			result = new ModelAndView("organization/team/groupTeam");
			team.setOrgAccountId(orgManagerDirect.getRootAccount().getId());
		}

		/** *************************新增内容开始************************************* */
		// 取是部门管理员的参数：deptAdmin=1
		String deptAdmin = request.getParameter("deptAdmin");
		// 是否是部门管理员登录
		result.addObject("deptAdmin", deptAdmin);
		// 如果是部门管理员登录，获取部门管理员能管理的部门,拼成字符串
		if (deptAdmin != null && deptAdmin.equals("1")) {
			// 查询出当前用户所在的部门
			Long deptId = CurrentUser.get().getDepartmentId();
			// 如果当前用户所在的部门存在，则取出当前用户所在的部门
			// 否则，查询当前用户所在的单位，作为当前用户所在的单位
			if (deptId != null) {
				V3xOrgDepartment orgDept = new V3xOrgDepartment();
				orgDept.setId(CurrentUser.get().getDepartmentId());
				orgDept.setName(orgManagerDirect.getDepartmentById(deptId)
						.getName());
				webTeam.setDept(orgDept);

			}

			V3xOrgRole role = orgManagerDirect
					.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPADMIN);
			List<V3xOrgEntity> depts = orgManagerDirect
					.getGroupByMemberAndRole(CurrentUser.get().getId(),
							role.getId());
			String depsPathStr = "";
			for (V3xOrgEntity dept : depts) {
				depsPathStr += ((V3xOrgDepartment) dept).getPath() + "|";
			}
			depsPathStr = depsPathStr.substring(0, depsPathStr.length() - 1);
			result.addObject("depsPathStr", depsPathStr);
		}

		webTeam.setV3xOrgTeam(team);
		result.addObject("teamForm", "createTeam");
		result.addObject("selectType", 0);
		// 判断继续添加的复选框是否显示 0 不显示 1 显示
		result.addObject("isShowChecked", 0);
		result.addObject("team", webTeam);

		/** ***************************新增内容结束*********************************** */

		return result;
	}

	/**
	 * 添加组成员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.GroupAdmin,
			RoleType.DepartmentAdmin, RoleType.HrAdmin })
	public ModelAndView createTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		V3xOrgTeam team = new V3xOrgTeam();
		Locale local = LocaleContext.getLocale(request);
		bind(request, team);
		team.setOrgAccountId(user.getLoginAccount());
		// 加入组重名的校验
		if (orgManagerDirect.isPropertyDuplicated(
				V3xOrgTeam.class.getSimpleName(), "name", team.getName())) {
			V3xOrgTeam dupTeam = (V3xOrgTeam) orgManagerDirect.getEntityList(
					V3xOrgTeam.class.getSimpleName(), "name", team.getName(),
					CurrentUser.get().getLoginAccount()).get(0);
			if (dupTeam.getType() == team.getType()) {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert('"
						+ Constant.getString("team.edit.same.name", local)
						+ "');");
				// out.println("self.history.back();");
				// super.printV3XJS(out);
				out.println("parent.getA8Top().endProc();");
				out.println("parent.disabledTeamButton(false);");
				out.println("</script>");
				return null;
			}
		}
		// 组主管
		String strcharge = request.getParameter("teamChargeIDs");
		List<Long> members1 = new ArrayList<Long>();
		if (null != strcharge && !strcharge.equals("")) {
			String[] memids = strcharge.split(",");
			for (String strid : memids) {
				Long id = Long.parseLong(strid);
				V3xOrgMember member = orgManagerDirect.getMemberById(id);
				members1.add(member.getId());
			}
		}
		team.addTeamMember(members1, V3xOrgEntity.ORGREL_TYPE_TEAM_LEADER);
		// 组成员
		String strmember = request.getParameter("teamMemIDs");
		List<Long> members = new ArrayList<Long>();
		if (null != strmember && !strmember.equals("")) {
			String[] memids = strmember.split(",");
			for (String strid : memids) {
				Long id = Long.parseLong(strid);
				V3xOrgMember member = orgManagerDirect.getMemberById(id);
				members.add(member.getId());
			}
		}
		team.addTeamMember(members, V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
		// 组领导
		String strlead = request.getParameter("teamLeadIDs");
		List<Long> members2 = new ArrayList<Long>();
		if (null != strlead && !strlead.equals("")) {
			String[] memids = strlead.split(",");
			for (String strid : memids) {
				Long id = Long.parseLong(strid);
				V3xOrgMember member = orgManagerDirect.getMemberById(id);
				members2.add(member.getId());
			}
		}
		team.addTeamMember(members2, V3xOrgEntity.ORGREL_TYPE_TEAM_SUPERV);
		// 组关联人员
		String strrela = request.getParameter("teamRelaIDs");
		List<Long> members3 = new ArrayList<Long>();
		if (null != strrela && !strrela.equals("")) {
			String[] memids = strrela.split(",");
			for (String strid : memids) {
				Long id = Long.parseLong(strid);
				V3xOrgMember member = orgManagerDirect.getMemberById(id);
				members3.add(member.getId());
			}
		}
		team.addTeamMember(members3, V3xOrgEntity.ORGREL_TYPE_TEAM_RELATIVE);

		// 排序号的重复处理
		String isInsert = request.getParameter("isInsert");
		if (isInsert.equals("1")
				&& orgManagerDirect.isPropertyDuplicated(
						V3xOrgTeam.class.getSimpleName(), "sortId",
						team.getSortId())) {
			orgManagerDirect.insertRepeatSortNum(V3xOrgTeam.class
					.getSimpleName(), CurrentUser.get().getLoginAccount(), team
					.getSortId());
		}
		orgManagerDirect.addTeam(team);
		// 日志
		appLogManager.insertLog(user, AppLogAction.Organization_NewTeam,
				user.getName(), team.getName());
		super.rendJavaScript(response, "parent.doEndTeam('"
				+ CurrentUser.get().getLoginAccount() + "')");

		return null;
	}

	/**
	 * 编辑组
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator,
			RoleType.DepartmentAdmin, RoleType.HrAdmin })
	public ModelAndView editTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("organization/team/team");
		String strid = request.getParameter("id");
		Long id = Long.parseLong(strid);
		V3xOrgTeam team = orgManagerDirect.getTeamById(id);
		WebV3xOrgTeam webTeam = new WebV3xOrgTeam();
		webTeam.setV3xOrgTeam(team);
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(team
				.getDepId());
		if (null != dept) {
			webTeam.setDept(dept);
		} else {
			V3xOrgDepartment accountDept = new V3xOrgDepartment();
			accountDept.setId(CurrentUser.get().getLoginAccount());
			accountDept.setName(orgManagerDirect.getAccountById(
					CurrentUser.get().getLoginAccount()).getName());
			webTeam.setDept(accountDept);
		}
		if (null != team) {
			processTeam(team, webTeam);
			// webTeam.setMemberList(memberlist);
		}
		result.addObject("team", webTeam);
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
		}
		result.addObject("teamForm", "updateTeam");
		result.addObject("selectType", 1);

		/** *************************新增内容开始************************************* */
		// 取是部门管理员的参数：deptAdmin=1
		String deptAdmin = request.getParameter("deptAdmin");
		// 是否是部门管理员登录
		result.addObject("deptAdmin", deptAdmin);
		// 如果是部门管理员登录，获取部门管理员能管理的部门,拼成字符串
		if (deptAdmin != null && deptAdmin.equals("1")) {
			V3xOrgRole role = orgManagerDirect
					.getRoleByName(V3xOrgEntity.ORGENT_META_KEY_DEPADMIN);
			List<V3xOrgEntity> depts = orgManagerDirect
					.getGroupByMemberAndRole(CurrentUser.get().getId(),
							role.getId());
			String depsPathStr = "";
			for (V3xOrgEntity dep : depts) {
				depsPathStr += ((V3xOrgDepartment) dep).getPath() + "|";
			}
			depsPathStr = depsPathStr.substring(0, depsPathStr.length() - 1);
			result.addObject("depsPathStr", depsPathStr);
		}
		/** ***************************新增内容结束*********************************** */

		return result;
	}

	private void processTeam(V3xOrgTeam team, WebV3xOrgTeam webTeam)
			throws BusinessException {
		// 组领导解析
		List<Long> supervisors = team.getSupervisors();
		List<V3xOrgMember> supervisorsMembers = new ArrayList<V3xOrgMember>();
		for (Long ids : supervisors) {
			V3xOrgMember vm = orgManagerDirect.getMemberById(ids);
			if (vm.getEnabled() && !vm.getIsDeleted())
				supervisorsMembers.add(vm);
		}
		webTeam.setMemberSupervisors(supervisorsMembers);
		// 取得组的成员
		List<Long> member = team.getMembers();
		List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
		for (Long ids : member) {
			V3xOrgMember vm = orgManagerDirect.getMemberById(ids);
			if (vm.getEnabled() && !vm.getIsDeleted())
				memberList.add(vm);
		}
		webTeam.setMemberNames(memberList);

		// 组主管解析
		List<Long> lead = team.getLeaders();
		List<V3xOrgMember> leadMembers = new ArrayList<V3xOrgMember>();
		for (Long ids : lead) {
			V3xOrgMember vm = orgManagerDirect.getMemberById(ids);
			if (vm.getEnabled() && !vm.getIsDeleted())
				leadMembers.add(vm);
		}
		webTeam.setMemberLead(leadMembers);
		// 关联人员的解析
		List<Long> relatives = team.getRelatives();
		List<V3xOrgMember> relativesMembers = new ArrayList<V3xOrgMember>();
		for (Long ids : relatives) {
			V3xOrgMember vm = orgManagerDirect.getMemberById(ids);
			if (vm.getEnabled() && !vm.getIsDeleted())
				relativesMembers.add(vm);
		}
		webTeam.setMemberRelatives(relativesMembers);
	}

	/**
	 * 编辑集团组
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView editGroupTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView("organization/team/groupTeam");
		String strid = request.getParameter("id");
		Long id = Long.parseLong(strid);
		V3xOrgTeam team = orgManagerDirect.getTeamById(id);
		WebV3xOrgTeam webTeam = new WebV3xOrgTeam();
		webTeam.setV3xOrgTeam(team);

		V3xOrgDepartment dept = null;

		if (team.getDepId() != null) {
			dept = orgManagerDirect.getDepartmentById(team.getDepId());
			if (null == dept) { // 有可能是单位
				V3xOrgAccount account = orgManagerDirect.getAccountById(team
						.getDepId());
				if (account != null) {
					dept = new V3xOrgDepartment();
					dept.setId(account.getId());
					dept.setName(account.getName());
				}
			}
		}

		if (dept == null) { // 如果没有值，则用当前登录者单位的
			dept = new V3xOrgDepartment();
			dept.setId(CurrentUser.get().getLoginAccount());
			dept.setName(orgManagerDirect.getAccountById(
					CurrentUser.get().getLoginAccount()).getName());
		}

		webTeam.setDept(dept);

		if (null != team) {
			processTeam(team, webTeam);
			// webTeam.setMemberList(memberlist);
		}
		result.addObject("team", webTeam);
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
		}
		result.addObject("teamForm", "updateTeam");
		result.addObject("selectType", 1);
		return result;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.GroupAdmin,
			RoleType.DepartmentAdmin, RoleType.HrAdmin })
	public ModelAndView updateTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		V3xOrgTeam model = new V3xOrgTeam();
		Locale local = LocaleContext.getLocale(request);
		bind(request, model);

		// 对修改组时，部门管理员删除组进行检查
		V3xOrgTeam team = orgManagerDirect.getTeamById(model.getId());
		if (team.getIsDeleted()) {
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('"
					+ Constant.getString("team.edit.check.delete", local)
					+ "');");
			out.println("</script>");
			super.rendJavaScript(response,
					"parent.parent.listFrame.location.reload(true);");
			return null;
		}
		// 加入组重名的校验
		if (orgManagerDirect.isPropertyDuplicated(
				V3xOrgTeam.class.getSimpleName(), "name", model.getName(),
				model.getId())) {
			List<V3xOrgEntity> teamLst = orgManagerDirect.getEntityList(
					V3xOrgTeam.class.getSimpleName(), "name", model.getName(),
					CurrentUser.get().getLoginAccount());
			for (V3xOrgEntity dupTeam : teamLst) {
				V3xOrgTeam myTeam = (V3xOrgTeam) dupTeam;
				if (!myTeam.getId().equals(model.getId())
						&& myTeam.getType() == model.getType()) {
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert('"
							+ Constant.getString("team.edit.same.name", local)
							+ "');");
					out.println("</script>");
					super.rendJavaScript(response,
							"parent.parent.listFrame.location.reload(true);");
					return null;
				}
			}
		}
		// 排序号的重复处理
		Integer orgSortId = team.getSortId();
		Integer sortId = Integer.valueOf(request.getParameter("sortId"));
		String isInsert = request.getParameter("isInsert");
		if (!orgSortId.equals(sortId)
				&& isInsert.equals("1")
				&& orgManagerDirect.isPropertyDuplicated(
						V3xOrgTeam.class.getSimpleName(), "sortId", sortId,
						team.getId())) {
			orgManagerDirect.insertRepeatSortNum(V3xOrgTeam.class
					.getSimpleName(), CurrentUser.get().getLoginAccount(),
					sortId);
		}
		BeanUtils.copyProperties(team, model);

		// 组主管
		String strcharge = request.getParameter("teamChargeIDs");
		if (strcharge != null && !strcharge.equals("")) {
			List<Long> members1 = new ArrayList<Long>();
			if (null != strcharge && !strcharge.equals("")) {
				String[] memids = strcharge.split(",");
				for (String strid : memids) {
					Long id = Long.parseLong(strid);
					V3xOrgMember member = orgManagerDirect.getMemberById(id);
					members1.add(member.getId());
				}
			}
			team.addTeamMember(members1, V3xOrgEntity.ORGREL_TYPE_TEAM_LEADER);
		}
		// 组成员
		String strmember = request.getParameter("teamMemIDs");
		if (strmember != null && !strmember.equals("")) {
			List<Long> members = new ArrayList<Long>();
			if (null != strmember && !strmember.equals("")) {
				String[] memids = strmember.split(",");
				for (String strid : memids) {
					Long id = Long.parseLong(strid);
					V3xOrgMember member = orgManagerDirect.getMemberById(id);
					members.add(member.getId());
				}
			}
			team.addTeamMember(members, V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
		}
		// 组领导
		String strlead = request.getParameter("teamLeadIDs");
		if (strlead != null && !strlead.equals("")) {
			List<Long> members2 = new ArrayList<Long>();
			if (null != strlead && !strlead.equals("")) {
				String[] memids = strlead.split(",");
				for (String strid : memids) {
					Long id = Long.parseLong(strid);
					V3xOrgMember member = orgManagerDirect.getMemberById(id);
					members2.add(member.getId());
				}
			}
			team.addTeamMember(members2, V3xOrgEntity.ORGREL_TYPE_TEAM_SUPERV);
		}
		// 组关联人员
		String strrela = request.getParameter("teamRelaIDs");
		if (strrela != null && !strrela.equals("")) {
			List<Long> members3 = new ArrayList<Long>();
			if (null != strrela && !strrela.equals("")) {
				String[] memids = strrela.split(",");
				for (String strid : memids) {
					Long id = Long.parseLong(strid);
					V3xOrgMember member = orgManagerDirect.getMemberById(id);
					members3.add(member.getId());
				}
			}
			team.addTeamMember(members3, V3xOrgEntity.ORGREL_TYPE_TEAM_RELATIVE);
		}
		orgManagerDirect.updateEntity(team);

		/**
		 * 推送模板到首页
		 */
		UpdateTeamEvent event = new UpdateTeamEvent(this);
		event.setTeam(team);
		EventDispatcher.fireEvent(event);

		// 日志
		User user = CurrentUser.get();
		appLogManager.insertLog(user, AppLogAction.Organization_UpdateTeam,
				user.getName(), team.getName());
		super.rendJavaScript(
				response,
				"parent.parent.detailFrame.location.href=\"/seeyon/common/detail.jsp\";parent.parent.listFrame.location.reload(true);");
		return null;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.GroupAdmin,
			RoleType.DepartmentAdmin, RoleType.HrAdmin })
	public ModelAndView destroyTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String ids = request.getParameter("id");
		Locale local = LocaleContext.getLocale(request);
		// 日志信息
		List<String[]> appLogs = new ArrayList<String[]>();
		User user = CurrentUser.get();

		if (null != ids && ids.length() > 0) {
			String[] arrIDs = ids.split(",");
			if (null != arrIDs && arrIDs.length > 0) {
				for (String strid : arrIDs) {
					Long id = Long.parseLong(strid);
					V3xOrgTeam team = orgManagerDirect.getTeamById(id);
					orgManagerDirect.deleteEntity(team);
					String[] appLog = new String[2];
					appLog[0] = user.getName();
					appLog[1] = team.getName();
					appLogs.add(appLog);
				}
				appLogManager.insertLogs(user,
						AppLogAction.Organization_DeleteTeam, appLogs);
			}
		}

		// 提示用户操作成功
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('" + Constant.getString("organization.yes", local)
				+ "');");
		out.println("</script>");
		out.flush();
		// 新增方法， 用来判断，如果为部门管理员则转向至显示部门管理员对应部门的组信息
		// 增加标志位：deptAdmin=1
		String deptAdmin = request.getParameter("deptAdmin");
		if (deptAdmin != null && deptAdmin.equals("1")) {
			return redirectModelAndView("/organization.do?method=listTeamDept&deptAdmin=1&flag='success'");
		}
		return redirectModelAndView("/organization.do?method=listTeam&flag='success'");
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView exportPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();

		User u = this.catchCurrentUser();
		if (u == null) {
			// DataUtil.outNullUserAlertScript(out);
			return null;
		}
		if (DataUtil.doingImpExp(u.getId())) {
			// DataUtil.outDoingImpExpAlertScript(out);
			return null;
		}

		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";

		String listname = "PostList_";
		// ResourceBundleUtil.getString(resource, local, "org.post_form.list");
		listname += u.getLoginName();

		String key = null;
		DataUtil.putImpExpAction(u.getId(), "export");
		try {
			DataRecord dataRecord = OrganizationHelper.exportPost(request,
					metadataManager, response, fileToExcelManager,
					orgManagerDirect);
			key = DataUtil.createTempSaveKey4Sheet(dataRecord);
		} catch (Exception e) {
			DataUtil.removeImpExpAction(u.getId());
			throw e;
		}
		DataUtil.removeImpExpAction(u.getId());
		String url = DataUtil.getOrgDownloadExpToExcelUrl(key, listname);
		log.info("url=" + url);
		return this.getIoManager().toExpRepeater(request, response, url);
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView exportPost1(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User u = this.catchCurrentUser();
		if (u == null) {
			// DataUtil.outNullUserAlertScript(out);
			return null;
		}
		if (DataUtil.doingImpExp(u.getId())) {
			// DataUtil.outDoingImpExpAlertScript(out);
			return null;
		}

		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";

		/**
		 * String listname = "PostList_";
		 * //ResourceBundleUtil.getString(resource, local,
		 * "org.post_form.list"); listname+=u.getLoginName();
		 **/
		String listname = ResourceBundleUtil.getString(resource, local,
				"org.post_form.list");
		listname += u.getLoginName();
		DataUtil.putImpExpAction(u.getId(), "export");
		DataRecord dataRecord = null;
		try {
			dataRecord = OrganizationHelper.exportPost(request,
					metadataManager, response, fileToExcelManager,
					orgManagerDirect);
		} catch (Exception e) {
			DataUtil.removeImpExpAction(u.getId());
			throw e;
		}
		DataUtil.removeImpExpAction(u.getId());
		OrganizationHelper.exportToExcel(request, response, fileToExcelManager,
				listname, dataRecord);
		return null;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.GroupAdmin })
	public ModelAndView exportTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		String team_list = ResourceBundleUtil.getString(resource, local,
				"team.list");

		DataRecord dataRecord = OrganizationHelper
				.exportTeam(request, metadataManager, response,
						fileToExcelManager, orgManagerDirect);
		OrganizationHelper.exportToExcel(request, response, fileToExcelManager,
				team_list + "-" + user.getLoginName(), dataRecord);
		return null;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.GroupAdmin })
	public ModelAndView expOrgToExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DataRecord[] drArray = new DataRecord[5];
		User user = CurrentUser.get();
		// ----------------------------------accountSheet---------------------
		Locale local = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		String dept_list = ResourceBundleUtil.getString(resource, local,
				"org.dept_form.list");
		V3xOrgAccount account = orgManagerDirect.getAccountById(user
				.getLoginAccount());
		List<V3xOrgDepartment> deptlist = orgManagerDirect
				.getAllDepartments(user.getLoginAccount());
		List<WebV3xOrgDepartment> resultlist = new ArrayList<WebV3xOrgDepartment>();
		for (int i = 0; i < deptlist.size(); i++) {
			V3xOrgDepartment dept = (com.seeyon.v3x.organization.domain.V3xOrgDepartment) deptlist
					.get(i);
			Long longid = dept.getId();
			V3xOrgDepartment parent = orgManagerDirect
					.getParentDepartment(longid);
			WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
			webdept.setV3xOrgDepartment(dept);
			if (null != parent) {
				webdept.setParentId(parent.getId());
				webdept.setParentName(parent.getName());
			} else {
				if (dept.getPath().indexOf(".") > 0
						&& (dept.getPath().indexOf(".") == dept.getPath()
								.lastIndexOf("."))) {
					webdept.setParentId(dept.getOrgAccountId());
					webdept.setParentName(account.getName());
				}
			}
			resultlist.add(webdept);
		}
		drArray[0] = OrganizationHelper.exportDept(resultlist, request,
				metadataManager, response, fileToExcelManager,
				orgManagerDirect, spaceManager, account);

		// ----------------------------------deptSheet------------------------

		// ----------------------------------teamSheet------------------------
		String team_list = ResourceBundleUtil.getString(resource, local,
				"team.list");
		drArray[1] = OrganizationHelper.exportTeam(request, metadataManager,
				response, fileToExcelManager, orgManagerDirect);
		// -----------------------------teamSheet-------------------------

		// -----------------------------levelSheet------------------------
		String level_list = ResourceBundleUtil.getString(resource, local,
				"org.level_form.list");
		drArray[2] = OrganizationHelper.exportLevel(request, response,
				fileToExcelManager, orgManagerDirect);
		// --------------------------levelSheet---------------------------

		// --------------------------postSheet----------------------------
		String post_list = ResourceBundleUtil.getString(resource, local,
				"org.post_form.list");
		drArray[3] = OrganizationHelper.exportPost(request, metadataManager,
				response, fileToExcelManager, orgManagerDirect);
		// -------------------------------postSheet-----------------------

		// -------------------------------memberSheet---------------------
		String member_list = ResourceBundleUtil.getString(resource, local,
				"org.member_form.list");
		String organization_list = ResourceBundleUtil.getString(resource,
				local, "org.list");
		drArray[4] = OrganizationHelper.exportMember(request, metadataManager,
				response, fileToExcelManager, orgManagerDirect);

		try {
			fileToExcelManager.save(request, response, organization_list + "-"
					+ user.getLoginName(), "location.href", drArray);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 导入单位
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public ModelAndView importAccount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		V3xOrgAccount account = null;
		V3xOrgAccount superior = null;
		String impURL = request.getParameter("impURL");
		String fileURL = impURL.replace("\\", "/");
		String repeat = request.getParameter("repeat");
		String language = request.getParameter("language");
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		Locale locale = null;
		// 根据选择导入文件语言获取对应locale对象
		if (language.equals("zh_CN")) {
			locale = Locale.CHINA;
		} else if (language.equals("en")) {
			locale = Locale.ENGLISH;
		}
		String state_Enabled = ResourceBundleUtil.getString(resource, locale,
				"org.account_form.enable.use");
		String permission_all = ResourceBundleUtil.getString(resource, locale,
				"org.metadata.access_permission.all");
		String permission_up = ResourceBundleUtil.getString(resource, locale,
				"org.metadata.access_permission.up");
		String permission_upAnddown = ResourceBundleUtil.getString(resource,
				locale, "org.metadata.access_permission.upAnddown");
		String permission_upAndpar = ResourceBundleUtil.getString(resource,
				locale, "org.metadata.access_permission.upAndpar");
		String add = ResourceBundleUtil.getString(resource, locale,
				"import.report.add");
		String overcast = ResourceBundleUtil.getString(resource, locale,
				"import.report.overcast");
		String overleap = ResourceBundleUtil.getString(resource, locale,
				"import.report.overleap");
		String fail_accountNameNull = ResourceBundleUtil.getString(resource,
				locale, "import.account.report.fail.nameNull");
		String fail_accountShortNameNull = ResourceBundleUtil.getString(
				resource, locale, "import.account.report.fail.shortNameNull");
		String fail_accountAliasNull = ResourceBundleUtil.getString(resource,
				locale, "import.account.report.fail.aliasNull");
		String fail_accountCodeNameNull = ResourceBundleUtil.getString(
				resource, locale, "import.account.report.fail.codeNull");
		String fail_accountAdminNameNull = ResourceBundleUtil.getString(
				resource, locale, "import.account.report.fail.adminNameNull");
		String fail = ResourceBundleUtil.getString(resource, locale,
				"import.report.fail");
		String company_isRoot_yes = ResourceBundleUtil.getString(resource,
				locale, "org.account_form.isRoot.yes");
		PrintWriter out = response.getWriter();
		try {
			File file = new File(fileURL);
			List<List<String>> accountList = fileToExcelManager.readExcel(file);
			List<String> proList = new ArrayList<String>();
			DataRow[] datarow = new DataRow[accountList.size() - 2];
			int loop = 0;
			// 从第三行开始才是实际要导入数据
			for (int i = 2; i < accountList.size(); i++) {
				DataRow row = new DataRow();
				V3xOrgRole role = null;
				List<Long> roleList = new ArrayList<Long>();
				List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
				V3xOrgMember member = null;
				Long[] memberIds = null;
				String[] members = null;
				proList = accountList.get(i);
				row.addDataCell(proList.get(0), 1);
				account = orgManagerDirect.getAccountByName(proList.get(0));
				if (account == null) {
					account = new V3xOrgAccount();
					// 添加单位文档
					docLibManager.addSysDocLibs(account.getId());
					// 名称
					if (proList.get(0) != null && !proList.get(0).equals("")) {
						account.setName(proList.get(0));
					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_accountNameNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}
					// 简称
					if (proList.get(1) != null && !proList.get(1).equals("")) {
						account.setShortname(proList.get(1));
					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_accountShortNameNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}
					// 外文名称
					account.setSecondName(proList.get(2));
					// 别名
					if (proList.get(3) != null && !proList.get(3).equals("")) {
						account.setAlias(proList.get(3));
					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_accountAliasNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}
					// 排序号
					account.setSortId(Integer.parseInt(proList.get(4)));
					// 代码
					if (proList.get(5) != null && !proList.get(5).equals("")) {
						account.setCode(proList.get(5));
					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_accountCodeNameNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}
					// 创建时间
					account.setCreateTime(Datetimes.parseDate(proList.get(6)));
					// 状态
					if (proList.get(7).equals(state_Enabled)) {
						account.setEnabled(true);
					} else {
						account.setEnabled(false);
					}
					// 是否为集团根单位
					if (proList.get(8).equals(company_isRoot_yes)) {
						account.setIsRoot(true);
					} else {
						account.setIsRoot(false);
					}
					// 集团简写名称
					account.setGroupShortname(proList.get(9));
					// 上级单位
					superior = orgManagerDirect.getAccountByName(proList
							.get(10));
					if (superior != null && !superior.equals("")) {
						account.setSuperior(superior.getId());
					}
					// 被访问权限
					if (proList.get(11) != null && !proList.get(11).equals("")) {
						if (proList.get(11).equals(permission_all)) {
							account.setAccessPermission(1);
						} else if (proList.get(11).equals(permission_up)) {
							account.setAccessPermission(2);
						} else if (proList.get(11).equals(permission_upAndpar)) {
							account.setAccessPermission(3);
						} else if (proList.get(11).equals(permission_upAnddown)) {
							account.setAccessPermission(4);
						}
					}
					// 单位描述
					account.setDecription(proList.get(22));
					// 管理员登录名
					if (proList.get(23) != null && !proList.get(23).equals("")) {
						account.setAdminName(proList.get(23));
					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_accountAdminNameNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}
					orgManagerDirect.addAccount(account);
					// 加管理员
					V3xOrgMember accountManager = new V3xOrgMember();

					String adminNameValue = ResourceBundleUtil
							.getString(
									"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
									"org.account_form.adminName.value", "");

					String adminName = proList.get(23);
					accountManager.setLoginName(adminName);
					String passWord = proList.get(24);
					if (passWord != null && !passWord.equals("")) {
						accountManager.setPassword(passWord);
					} else {
						accountManager.setPassword("123");
					}
					accountManager.setName(adminNameValue);
					accountManager.setIsAdmin(true);
					accountManager.setOrgAccountId(account.getId());
					accountManager.setOrgDepartmentId(account.getId());
					orgManagerDirect.addMember(accountManager);
					// 增加角色
					V3xOrgRole role_manager = new V3xOrgRole();
					role_manager.setName(adminNameValue);
					role_manager.setOrgAccountId(account.getId());
					orgManagerDirect.addRole(role_manager);
					// 获取扩展属性
					List<String> extProList = orgManagerDirect
							.extPropertyNames(account.getEntityType());
					if (null != extProList && extProList.size() > 0) {
						for (String extpro : extProList) {
							if (extpro.equals("chiefLeader")) {
								account.setProperty(extpro, proList.get(15));
								continue;
							} else if (extpro.equals("address")) {
								account.setProperty(extpro, proList.get(16));
								continue;
							} else if (extpro.equals("zipCode")) {
								account.setProperty(extpro, proList.get(17));
								continue;
							} else if (extpro.equals("telephone")) {
								account.setProperty(extpro, proList.get(18));
								continue;
							} else if (extpro.equals("fax")) {
								account.setProperty(extpro, proList.get(19));
								continue;
							} else if (extpro.equals("ipAddress")) {
								account.setProperty(extpro, proList.get(20));
								continue;
							} else if (extpro.equals("accountMail")) {
								account.setProperty(extpro, proList.get(21));
								continue;
							}
						}
					}
					orgManagerDirect.updateEntity(account);
					// 单位角色指派的标识
					int rolebs = 25;

					while (proList.size() > rolebs) {

						if (proList.get(rolebs) != null
								&& !proList.get(rolebs).equals("")) {

							role = orgManagerDirect.getRoleByName(proList
									.get(rolebs));
							if (role != null) {
								roleList.add(role.getId());
								if (proList.get(rolebs + 1) != null
										&& !proList.get(rolebs + 1).equals("")) {
									members = proList.get(rolebs + 1)
											.split(",");
									memberIds = new Long[members.length];
									int k = 0;
									for (String memberName : members) {
										if (memberList != null
												&& memberList.size() != 0) {
											member = memberList.get(0);
											memberIds[k] = member.getId();
											k++;
										}
									}
									orgManagerDirect.addRole2Member(
											role.getBond(), account.getId(),
											role.getId(), memberIds);
								}
							}
						}
						rolebs += 2;
					}
					row.addDataCell(add, 1);
				} else {
					// 简称
					if (proList.get(1) != null && !proList.get(1).equals("")) {
						account.setShortname(proList.get(1));
					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_accountShortNameNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}
					// 外文名称
					account.setSecondName(proList.get(2));
					// 别名
					if (proList.get(3) != null && !proList.get(3).equals("")) {
						account.setAlias(proList.get(3));
					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_accountAliasNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}
					// 排序号
					account.setSortId(Integer.parseInt(proList.get(4)));
					// 代码
					if (proList.get(5) != null && !proList.get(5).equals("")) {
						account.setCode(proList.get(5));
					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_accountCodeNameNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}
					// 创建时间
					account.setCreateTime(Datetimes.parseDate(proList.get(6)));
					// 状态
					if (proList.get(7).equals(state_Enabled)) {
						account.setEnabled(true);
					} else {
						account.setEnabled(false);
					}
					// 是否为集团根单位
					if (proList.get(8).equals(company_isRoot_yes)) {
						account.setIsRoot(true);
					} else {
						account.setIsRoot(false);
					}
					// 集团简写名称
					account.setGroupShortname(proList.get(9));
					// 上级单位
					superior = orgManagerDirect.getAccountByName(proList
							.get(10));
					if (superior != null && !superior.equals("")) {
						account.setSuperior(superior.getId());
					}
					// 被访问权限
					if (proList.get(11) != null && !proList.get(11).equals("")) {
						if (proList.get(11).equals(permission_all)) {
							account.setAccessPermission(1);
						} else if (proList.get(11).equals(permission_up)) {
							account.setAccessPermission(2);
						} else if (proList.get(11).equals(permission_upAndpar)) {
							account.setAccessPermission(3);
						} else if (proList.get(11).equals(permission_upAnddown)) {
							account.setAccessPermission(4);
						}
					}
					// 单位描述
					account.setDecription(proList.get(22));
					// 管理员登录名
					if (proList.get(23) != null && !proList.get(23).equals("")) {
						V3xOrgMember accountManager = orgManagerDirect
								.getMemberByLoginName(account.getAdminName());
						accountManager.setLoginName(proList.get(23));
						if (proList.get(24) != null
								&& !proList.get(24).equals("")) {
							accountManager.setPassword(proList.get(24));
						} else {
							accountManager.setPassword("123");
						}
						account.setAdminName(proList.get(23));

					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_accountAdminNameNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}
					if (repeat.equals("0")) {
						orgManagerDirect.updateEntity(account);
						// 单位角色指派的标识
						int rolebs = 25;
						while (proList.size() > rolebs) {
							if (proList.get(rolebs) != null
									&& !proList.get(rolebs).equals("")) {

								role = orgManagerDirect.getRoleByName(proList
										.get(rolebs));
								if (role != null) {
									roleList.add(role.getId());
									account.addAccountRole(roleList);
									if (proList.get(rolebs + 1) != null
											&& !proList.get(rolebs + 1).equals(
													"")) {
										members = proList.get(rolebs + 1)
												.split(",");
										memberIds = new Long[members.length];
										int k = 0;
										for (String memberName : members) {
											if (memberList != null
													&& memberList.size() != 0) {
												member = memberList.get(0);
												memberIds[k] = member.getId();
												k++;
											}
										}
										orgManagerDirect.addRole2Member(
												role.getBond(),
												account.getId(), role.getId(),
												memberIds);
									}
								}
							}
							rolebs += 2;
						}
						row.addDataCell(overcast, 1);
					} else if (repeat.equals("1")) {
						row.addDataCell(overleap, 1);
					}
				}
				datarow[loop] = row;
				loop++;
			}

			HttpSession session = request.getSession();
			// 导入文件名
			session.setAttribute("fileName",
					fileURL.substring(fileURL.lastIndexOf("/") + 1));
			// 报告数据
			session.setAttribute("datarow", datarow);
			// 导入语言
			session.setAttribute("language", language);
			// 导入选项
			session.setAttribute("repeat", repeat);
			// 导入类别
			session.setAttribute("importType", "account");
			// 执行上层页面中的一段脚本(弹出导入报告窗口)
			out.println("<script>");
			out.println("parent.importReport();");
			out.println("</script>");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return super.refreshWorkspace();
	}

	/**
	 * 导入职务级别
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView importLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		V3xOrgLevel level = null;
		V3xOrgAccount account = null;
		// 判断是否覆盖重复项 如果是0覆盖 1跳过
		String repeat = request.getParameter("repeat");
		// 导入文件语言 zh中文 en英文
		String language = request.getParameter("language");
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		Locale locale = null;
		if (language.equals("zh_CN")) {
			locale = Locale.CHINA;
		} else if (language.equals("en")) {
			locale = Locale.ENGLISH;
		}
		// 国际化
		String state_Enabled = ResourceBundleUtil.getString(resource, locale,
				"org.account_form.enable.use");
		String fail_levelNameNull = ResourceBundleUtil.getString(resource,
				locale, "import.level.report.fail.nameNull");
		String fail_sortIdNull = ResourceBundleUtil.getString(resource, locale,
				"import.level.report.fail.sortIdNull");
		String fail = ResourceBundleUtil.getString(resource, locale,
				"import.report.fail");
		String add = ResourceBundleUtil.getString(resource, locale,
				"import.report.add");
		String overcast = ResourceBundleUtil.getString(resource, locale,
				"import.report.overcast");
		String overleap = ResourceBundleUtil.getString(resource, locale,
				"import.report.overleap");
		// 从前台获取要导入文件的路径
		String impURL = request.getParameter("impURL");
		String fileURL = impURL.replace("\\", "/");
		PrintWriter out = response.getWriter();
		try {
			File file = new File(fileURL);
			List<List<String>> levelList = fileToExcelManager.readExcel(file);
			List<String> proList = new ArrayList<String>();
			DataRow[] datarow = new DataRow[levelList.size() - 2];
			int loop = 0;
			// 从第三行开始才是实际要导入数据
			for (int i = 2; i < levelList.size(); i++) {
				DataRow row = new DataRow();
				proList = levelList.get(i);
				level = orgManagerDirect.getLevelByName(proList.get(0));
				row.addDataCell(proList.get(0), 1);
				if (level == null) {
					level = new V3xOrgLevel();
					if (proList.get(0) != null && !proList.get(0).equals("")) {
						level.setName(proList.get(0)); // 职务级别名称
					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_levelNameNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}
					level.setCode(proList.get(1)); // 职务级别代码
					if (proList.get(2) != null && !proList.get(2).equals("")) {
						level.setSortId(Integer.parseInt(proList.get(2))); // 排序号
					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_sortIdNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}
					if (proList.get(3).equals(state_Enabled)) {
						level.setEnabled(true); // 状态
					} else {
						level.setEnabled(false);
					}
					account = orgManagerDirect.getAccountByName(proList.get(4)); // 获取单位信息
					level.setOrgAccountId(account.getId());
					level.setDescription(proList.get(5)); // 描述
					orgManagerDirect.addLevel(level);
					row.addDataCell(add, 1);
					row.addDataCell("", 1);
				} else {
					level.setCode(proList.get(1)); // 职务级别代码
					if (proList.get(2) != null && !proList.get(2).equals("")) {
						level.setSortId(Integer.parseInt(proList.get(2))); // 排序号
					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_sortIdNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}
					if (proList.get(3).equals(state_Enabled)) {
						level.setEnabled(true); // 状态
					} else {
						level.setEnabled(false);
					}
					account = orgManagerDirect.getAccountByName(proList.get(4)); // 获取单位信息
					level.setOrgAccountId(account.getId());
					level.setDescription(proList.get(5)); // 描述
					// 如果重复项标识是0覆盖(update)否则跳过
					if (repeat.equals("0")) {
						orgManagerDirect.updateEntity(level);
						row.addDataCell(overcast, 1);
						row.addDataCell("", 1);
					} else if (repeat.equals("1")) {
						row.addDataCell(overleap, 1);
						row.addDataCell("", 1);
					}
				}
				datarow[loop] = row;
				loop++;
			}
			HttpSession session = request.getSession();
			// 导入文件名
			session.setAttribute("fileName",
					fileURL.substring(fileURL.lastIndexOf("/") + 1));
			// 报告数据
			session.setAttribute("datarow", datarow);
			// 导入语言
			session.setAttribute("language", language);
			// 导入选项
			session.setAttribute("repeat", repeat);
			// 导入类别
			session.setAttribute("importType", "level");
			// 执行上层页面中的一段脚本(弹出导入报告窗口)
			out.println("<script>");
			out.println("parent.listFrame.importReport();");
			out.println("</script>");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return super.refreshWorkspace();
	}

	/**
	 * 导入岗位
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView importPost(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		V3xOrgPost post = null;
		V3xOrgAccount account = null;
		String repeat = request.getParameter("repeat");
		String language = request.getParameter("language");
		PrintWriter out = response.getWriter();
		// 获取岗位类别列表
		List<MetadataItem> itemList = metadataManager
				.getMetadataItems("organization_post_types");
		HashMap<String, String> itemMap = new HashMap<String, String>();
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		Locale locale = null;
		if (language.equals("zh_CN")) {
			locale = Locale.CHINA;
		} else if (language.equals("en")) {
			locale = Locale.ENGLISH;
		}
		// 国际化
		String state_Enabled = ResourceBundleUtil.getString(resource, locale,
				"org.account_form.enable.use");
		String fail_postNameNull = ResourceBundleUtil.getString(resource,
				locale, "import.post.report.fail.postNameNull");
		String fail_postTypeNull = ResourceBundleUtil.getString(resource,
				locale, "import.post.report.fail.postTypeNull");
		String fail = ResourceBundleUtil.getString(resource, locale,
				"import.report.fail");
		String add = ResourceBundleUtil.getString(resource, locale,
				"import.report.add");
		String overcast = ResourceBundleUtil.getString(resource, locale,
				"import.report.overcast");
		String overleap = ResourceBundleUtil.getString(resource, locale,
				"import.report.overleap");
		String lable = "";
		for (MetadataItem item : itemList) {
			itemMap.put(item.getLabel(), item.getValue());
		}
		for (MetadataItem item : itemList) {
			lable = ResourceBundleUtil.getString(resource, locale,
					item.getLabel());
			if (!lable.equals("")) {
				itemMap.put(lable, item.getValue());
			}
		}
		// 从前台获取要导入文件的路径
		String impURL = request.getParameter("impURL");
		String fileURL = impURL.replace("\\", "/");
		try {
			File file = new File(fileURL);
			List<List<String>> postList = fileToExcelManager.readExcel(file);
			DataRow[] datarow = new DataRow[postList.size() - 2];
			List<String> proList = new ArrayList<String>();
			int loop = 0;
			// 从第三行开始才是实际要导入数据
			for (int i = 2; i < postList.size(); i++) {
				DataRow row = new DataRow();
				proList = postList.get(i);
				post = orgManagerDirect.getPostByName(proList.get(0));
				row.addDataCell(proList.get(0), 1);
				if (post == null) {
					post = new V3xOrgPost();
					// 岗位名称
					if (proList.get(0) != null && !proList.get(0).equals("")) {
						post.setName(proList.get(0));
					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_postNameNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}
					// 岗位类别
					if (itemMap.get(proList.get(1)) != null
							&& !itemMap.get(proList.get(1)).equals("")) {
						post.setTypeId(new Long(itemMap.get(proList.get(1))));
					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_postTypeNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}

					post.setCode(proList.get(2)); // 岗位代码
					post.setSortId(Integer.parseInt(proList.get(3))); // 排序号

					if (proList.get(4).equals(state_Enabled)) {
						post.setEnabled(true); // 状态
					} else {
						post.setEnabled(false);
					}
					account = orgManagerDirect.getAccountByName(proList.get(5)); // 获取单位信息
					post.setOrgAccountId(account.getId());
					post.setDesciption(proList.get(6)); // 描述
					orgManagerDirect.addPost(post);
					row.addDataCell(add, 1);
				} else {
					// 岗位类别
					if (itemMap.get(proList.get(1)) != null
							&& !itemMap.get(proList.get(1)).equals("")) {
						post.setTypeId(new Long(itemMap.get(proList.get(1))));
					} else {
						row.addDataCell(fail, 1);
						row.addDataCell(fail_postTypeNull, 1);
						datarow[loop] = row;
						loop++;
						continue;
					}
					post.setCode(proList.get(2)); // 岗位代码
					post.setSortId(Integer.parseInt(proList.get(3))); // 排序号
					if (proList.get(4).equals(state_Enabled)) {
						post.setEnabled(true); // 状态
					} else {
						post.setEnabled(false);
					}
					account = orgManagerDirect.getAccountByName(proList.get(5)); // 获取单位信息
					post.setOrgAccountId(account.getId());
					post.setDesciption(proList.get(6)); // 描述
					if (repeat.equals("0")) {
						orgManagerDirect.updateEntity(post);
						row.addDataCell(overcast, 1);
					} else if (repeat.equals("1")) {
						row.addDataCell(overleap, 1);
					}
				}
				datarow[loop] = row;
				loop++;
			}

			HttpSession session = request.getSession();
			// 导入文件名
			session.setAttribute("fileName",
					fileURL.substring(fileURL.lastIndexOf("/") + 1));
			// 报告数据
			session.setAttribute("datarow", datarow);
			// 导入语言
			session.setAttribute("language", language);
			// 导入选项
			session.setAttribute("repeat", repeat);
			// 导入类别
			session.setAttribute("importType", "post");
			// 执行上层页面中的一段脚本(弹出导入报告窗口)
			out.println("<script>");
			out.println("parent.listFrame.importReport();");
			out.println("</script>");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return super.refreshWorkspace();
	}

	/**
	 * 导入组
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView importTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return super.refreshWorkspace();
	}

	/**
	 * 导入部门
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView importDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return super.refreshWorkspace();
	}

	/**
	 * 导入人员
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public ModelAndView importMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin, RoleType.Administrator,
			RoleType.HrAdmin, RoleType.DepartmentAdmin })
	public ModelAndView importExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/selectImportExcel");
		String importType = request.getParameter("importType");
		modelAndView.addObject("importType", importType);
		HttpSession session = request.getSession();
		session.setAttribute("importType", importType);
		List accountlst = orgManagerDirect.getAllAccounts();
		modelAndView.addObject("accountlst", accountlst);
		return modelAndView;
	}

	public ModelAndView importReport(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// ModelAndView modelAndView = new
		// ModelAndView("organization/importReport");
		return this.getIoManager().importReport(request, response);
	}

	public ModelAndView importOrgReport(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		List resultlst = (List) session.getAttribute("resultlst");
		ModelAndView modelAndView = new ModelAndView(
				"organization/importReport");
		modelAndView.addObject("resultlst", resultlst);
		session.removeAttribute("resultlst");
		return modelAndView;
	}

	public ModelAndView exportReport(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return this.getIoManager().exportReport(request, response);
	}

	public ModelAndView exportReport1(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		DataRow[] datarow = (DataRow[]) session.getAttribute("datarowlist");
		String importType = (String) session.getAttribute("importType");
		String language = (String) session.getAttribute("language");
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		Locale locale = null;
		// 根据选择导入文件语言获取对应locale对象
		if (language.equals("zh_CN")) {
			locale = Locale.CHINA;
		} else if (language.equals("en")) {
			locale = Locale.ENGLISH;
		} else if (language.equals("zh")) {
			locale = Locale.TAIWAN;
		}
		String import_level_report = ResourceBundleUtil.getString(resource,
				locale, "import.level.report");
		String import_post_report = ResourceBundleUtil.getString(resource,
				locale, "import.post.report");
		String import_team_report = ResourceBundleUtil.getString(resource,
				locale, "import.team.report");
		String import_member_report = ResourceBundleUtil.getString(resource,
				locale, "import.member.report");
		String import_dept_report = ResourceBundleUtil.getString(resource,
				locale, "import.dept.report");
		String import_account_report = ResourceBundleUtil.getString(resource,
				locale, "import.account.report");
		String import_report = ResourceBundleUtil.getString(resource, locale,
				"import.report");
		String import_data = ResourceBundleUtil.getString(resource, locale,
				"import.data");
		String import_result = ResourceBundleUtil.getString(resource, locale,
				"import.result");
		String import_description = ResourceBundleUtil.getString(resource,
				locale, "import.description");
		String title = "";
		String sheetName = "";
		// session.removeAttribute("datarow");
		if (importType.equals("level")) {
			title = import_level_report;
			sheetName = import_level_report;
		} else if (importType.equals("post")) {
			title = import_post_report;
			sheetName = import_post_report;
		} else if (importType.equals("team")) {
			title = import_team_report;
			sheetName = import_team_report;
		} else if (importType.equals("member")) {
			title = import_member_report;
			sheetName = import_member_report;
		} else if (importType.equals("department")) {
			title = import_dept_report;
			sheetName = import_dept_report;
		} else if (importType.equals("account")) {
			title = import_account_report;
			sheetName = import_account_report;
		}
		// 将导入结果添加到excel中
		DataRecord dataRecord = new DataRecord();
		try {
			dataRecord.addDataRow(datarow);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		String[] columnName = { import_data, import_result, import_description };
		dataRecord.setColumnName(columnName);
		dataRecord.setTitle(title);
		dataRecord.setSheetName(sheetName);

		try {
			fileToExcelManager.save(request, response, import_report,
					"location.href", dataRecord);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 页面跳转
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView matchField(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		File file = uploadFile(request);
		String path = file.getAbsolutePath() + ".xls";
		File realfile = new File(path);
		DataUtil.CopyFile(file, realfile);

		HttpSession session = request.getSession();
		String selectvalue = request.getParameter("selectvalue");

		session.setAttribute("selectvalue", selectvalue);
		if (selectvalue == null || "".equals(selectvalue)
				|| "null".equals(selectvalue)) {
			throw new Exception("请上传文件对应的表！");
		}
		String radiovalue = request.getParameter("radiovalue");

		session.setAttribute("radiovalue", radiovalue);
		if (radiovalue == null || "".equals(radiovalue)
				|| "null".equals(radiovalue)) {
			throw new Exception("请选择单、多表！");
		}
		String sheetnumber = request.getParameter("sheetnumber");

		session.setAttribute("sheetnumber", sheetnumber);
		if ("multi".equals(radiovalue)) {
			if (selectvalue == null || "".equals(selectvalue)
					|| "null".equals(selectvalue)) {
				throw new Exception("请选择上传表对应的工作簿的位置！");
			}
		}

		String language = request.getParameter("languagevalue");
		session.setAttribute("language", language);

		// 从后台取出数据字段列表
		List datastrulst = getDataManagerImpl().getDataStructure(selectvalue);
		DataUtil du = new DataUtil(selectvalue);
		datastrulst = du.getCHNString(datastrulst, request);
		session.setAttribute("datastrulst", datastrulst);
		Map allmap = new HashMap();

		List<List<String>> accountList = null;
		if ("multi".equals(radiovalue)) {
			accountList = fileToExcelManager.readExcel(realfile);
		} else if ("single".equals(radiovalue)) {
			// 读取默认工作簿的数据
			accountList = fileToExcelManager.readExcel(realfile);

			if (accountList != null && accountList.size() > 2) {
				log.info("读取默认工作簿的数据，其中个行大小如下");
				for (int i = 2; i < accountList.size(); i++) {
					log.info("accountList i=" + i);
					List l = accountList.get(i);
					if (l == null) {
						log.info("accountList'subList is null");
						continue;
					}
					log.info("accountList'subList size=" + l.size());
				}
			}

		}
		session.setAttribute("excellst", accountList);
		// 读取表头数据，一般为第二行
		List proList = accountList.get(1);

		// 得到匹配的list,及不匹配的list 组成的map
		allmap = DataUtil.getMatchList(proList, datastrulst);

		session.setAttribute("allmap", allmap);

		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("parent.getA8Top().contentFrame.topFrame.matchfiled();");
		out.println("</script>");
		return null;

	}

	public ModelAndView popMatchPage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		ModelAndView modelAndView = new ModelAndView("organization/matchField");
		Map allmap = (Map) session.getAttribute("allmap");

		// 用于页面已匹配下拉列表框
		modelAndView.addObject("matchlst", allmap.get("0"));
		// 用于页面的新增下拉列表框
		modelAndView.addObject("excellst", allmap.get("1"));
		// 用于页面的删除下拉列表框
		modelAndView.addObject("strulst", allmap.get("2"));

		modelAndView.addObject("language",
				(String) session.getAttribute("language"));
		modelAndView.addObject("selectvalue",
				(String) session.getAttribute("selectvalue"));
		modelAndView.addObject("radiovalue",
				(String) session.getAttribute("radiovalue"));
		modelAndView.addObject("sheetnumber",
				(String) session.getAttribute("sheetnumber"));

		session.removeAttribute("allmap");
		return modelAndView;
	}

	/**
	 * 页面跳转
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView doImport(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter out = response.getWriter();

		User u = this.catchCurrentUser();
		if (u == null) {
			// DataUtil.outNullUserAlertScript(out);
			return null;
		}
		if (DataUtil.doingImpExp(u.getId())) {
			// DataUtil.outDoingImpExpAlertScript(out);
			return null;
		}

		this.getIoManager().setOpUser(u);
		this.getIoManager().setVaccountByUser();

		String reportUrl = null;
		DataUtil.putImpExpAction(u.getId(), "import");
		try {
			reportUrl = this.getIoManager()
					.doImport4Redirect(request, response);
			log.info("reportUrl=" + reportUrl);
		} catch (Exception e) {
			DataUtil.removeImpExpAction(u.getId());
			throw e;
		}
		DataUtil.removeImpExpAction(u.getId());
		// 记录日志
		User user = CurrentUser.get();
		String selectvalue = request.getParameter("selectvalue");
		if (selectvalue == null) {
			selectvalue = (String) request.getAttribute("selectvalue");
		}
		if (selectvalue == null || "".equals(selectvalue)
				|| "null".equals(selectvalue)) {
			throw new Exception("请上传文件对应的表！");
		} else {
			if (selectvalue.equals("post")) {
				appLogManager.insertLog(user,
						AppLogAction.Organization_BatchAddPost, user.getName());
			} else if (selectvalue.equals("member")) {
				appLogManager.insertLog(user,
						AppLogAction.Organization_BatchAddMember,
						user.getName());
			}
		}

		out.println("<script>");
		out.println("window.returnValue=\"" + reportUrl + "\";");
		out.println("window.close();");
		out.println("</script>");
		out.flush();
		return null;
	}

	public ModelAndView doImportResult(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String reportUrl = request.getParameter("reportUrl");
		return this.redirectModelAndView(reportUrl.replace("|", "&"));
	}

	public ModelAndView doImport1(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		HttpSession session = request.getSession();

		String selectvalue = request.getParameter("selectvalue");
		if (selectvalue == null || "".equals(selectvalue)
				|| "null".equals(selectvalue)) {
			throw new Exception("请上传文件对应的表！");
		}
		String radiovalue = request.getParameter("radiovalue");
		if (radiovalue == null || "".equals(radiovalue)
				|| "null".equals(radiovalue)) {
			throw new Exception("请选择单、多表！");
		}
		String sheetnumber = request.getParameter("sheetnumber");
		if ("multi".equals(radiovalue)) {
			if (selectvalue == null || "".equals(selectvalue)
					|| "null".equals(selectvalue)) {
				throw new Exception("请选择上传表对应的工作簿的位置！");
			}
		}
		// 跳过为 1 覆盖为 0
		String repeat = request.getParameter("repeat");
		if (repeat == null || "".equals(repeat) || "null".equals(repeat)) {
			throw new Exception("请选择上传策略！");
		}
		String language = request.getParameter("language");
		modelAndView.addObject("language", language);
		session.setAttribute("language", language);
		Locale locale = null;
		// 根据选择导入文件语言获取对应locale对象
		if (language.equals("zh_CN")) {
			locale = Locale.CHINA;
		} else if (language.equals("en")) {
			locale = Locale.ENGLISH;
		} else if (language.equals("zh")) {
			locale = Locale.TAIWAN;
		}

		String impURL = request.getParameter("impURL");
		modelAndView.addObject("impURL", impURL);
		String accountid = String.valueOf(CurrentUser.get().getAccountId());

		List datalst = (List) session.getAttribute("datastrulst");
		DataUtil du = new DataUtil(selectvalue);
		List DataObjectlst = DataUtil.setMatchList(request, datalst);
		List accountList = (List) session.getAttribute("excellst");
		List volst = du.getMatchValue(orgManagerDirect, this.metadataManager,
				Long.valueOf(accountid), accountList, DataObjectlst);

		// 注入 单位id
		if (!"account".equals(selectvalue)) {
			DataUtil.setAccountId(accountid, volst);
		}

		// tanglh
		// 后面因为volst的对象ID会变掉，这里先缓存先
		List volold = new ArrayList();
		if (volst != null && selectvalue.equals("member")) {
			for (int i = 0; i < volst.size(); i++) {
				V3xOrgMember vm = (V3xOrgMember) volst.get(i);
				if (vm == null)
					continue;
				V3xOrgMember vom = new V3xOrgMember();
				vom.setId(vm.getId());
				vom.setLoginName(vm.getLoginName());
				vom.setPassword(vm.getPassword());

				volold.add(vom);
			}
		}

		// tanglh

		Map mp = du.devideVo(orgManagerDirect, volst);
		List inserlst = new ArrayList();
		List updatelst = new ArrayList();
		if ("1".equals(repeat)) {
			inserlst = du.getCreateSQL((List) mp.get("new"));
		} else {
			inserlst = du.getCreateSQL((List) mp.get("new"));
			updatelst = du.getUpdateSQL((List) mp.get("dup"));
			inserlst.addAll(updatelst);
		}
		List resultlst = new ArrayList();

		// 执行sql语句
		getDataManagerImpl().execSQLList(inserlst);

		PrintWriter out = response.getWriter();

		DataUtil.setResultList(resultlst, (List) mp.get("new"), "", locale);
		DataUtil.setResultList(resultlst, (List) mp.get("dup"), repeat, locale);

		DataUtil.setResultToSession(resultlst, session);
		session.setAttribute("resultlst", resultlst);
		session.removeAttribute("excellst");
		session.removeAttribute("datastrulst");
		out.println("<script>");
		out.println("parent.getA8Top().contentFrame.topFrame.importReport();");
		out.println("</script>");
		if (selectvalue.equals("level")) {
			orgManagerDirect.reloadOrganizationModel();
			DataUtil.setResultList(resultlst, (List) mp.get("new"), "", locale);
			DataUtil.setResultList(resultlst, (List) mp.get("dup"), repeat,
					locale);

			DataUtil.setResultToSession(resultlst, session);
			session.setAttribute("resultlst", resultlst);
			session.removeAttribute("excellst");
			session.removeAttribute("datastrulst");
			out.println("<script>");
			out.println("parent.getA8Top().contentFrame.topFrame.importReport();");
			out.println("</script>");
			return null;
		} else if (selectvalue.equals("post")) {
			orgManagerDirect.reloadOrganizationModel();
			DataUtil.setResultList(resultlst, (List) mp.get("new"), "", locale);
			DataUtil.setResultList(resultlst, (List) mp.get("dup"), repeat,
					locale);
			DataUtil.setResultToSession(resultlst, session);
			session.setAttribute("resultlst", resultlst);
			session.removeAttribute("excellst");
			session.removeAttribute("datastrulst");
			out.println("<script>");
			out.println("parent.getA8Top().contentFrame.topFrame.importReport();");
			out.println("</script>");
			return null;
		} else if (selectvalue.equals("team")) {
			orgManagerDirect.reloadOrganizationModel();
			DataUtil.setResultList(resultlst, (List) mp.get("new"), "", locale);
			DataUtil.setResultList(resultlst, (List) mp.get("dup"), repeat,
					locale);
			DataUtil.setResultToSession(resultlst, session);
			session.setAttribute("resultlst", resultlst);
			session.removeAttribute("excellst");
			session.removeAttribute("datastrulst");
			out.println("<script>");
			out.println("parent.getA8Top().contentFrame.topFrame.importReport();");
			out.println("</script>");
			return null;
		} else if (selectvalue.equals("member")) {
			List fnew = (List) mp.get("new");
			fnew = this.addMemberLoginname(fnew);
			mp.put("new", fnew);
			if (!"1".equals(repeat)) {
				List fup = (List) mp.get("dup");
				fup = this.addMemberLoginname(fup);
				mp.put("dup", fup);
			}
			orgManagerDirect.reloadOrganizationModel();
			DataUtil.setResultList(resultlst, (List) mp.get("new"), "", locale);
			DataUtil.setResultList(resultlst, (List) mp.get("dup"), repeat,
					locale);
			DataUtil.setResultToSession(resultlst, session);
			session.setAttribute("resultlst", resultlst);
			session.removeAttribute("excellst");
			session.removeAttribute("datastrulst");
			out.println("<script>");
			out.println("parent.getA8Top().contentFrame.topFrame.importReport();");
			out.println("</script>");
			return null;
		} else if (selectvalue.equals("department")) {
			orgManagerDirect.reloadOrganizationModel();
			List<V3xOrgDepartment> deptlst = orgManagerDirect
					.getAllDepartments(Long.valueOf(accountid), false);
			for (V3xOrgDepartment dept : deptlst) {
				dept.init((CallbackAddInitialData) orgManagerDirect);
				addRelationShip(dept);
			}
			DataUtil.setResultList(resultlst, (List) mp.get("new"), "", locale);
			DataUtil.setResultList(resultlst, (List) mp.get("dup"), repeat,
					locale);

			DataUtil.setResultToSession(resultlst, session);
			session.setAttribute("resultlst", resultlst);
			session.removeAttribute("excellst");
			session.removeAttribute("datastrulst");
			out.println("<script>");
			out.println("parent.getA8Top().contentFrame.topFrame.importReport();");
			out.println("</script>");
			return null;
		} else if (selectvalue.equals("account")) {
			List accountlst = (List) mp.get("new");
			for (int i = 0; i < accountlst.size(); i++) {
				V3xOrgAccount voa = (V3xOrgAccount) accountlst.get(i);
				orgManagerDirect.reloadAccountData(voa.getId());
				// 加管理员
				V3xOrgMember member = new V3xOrgMember();

				String adminNameValue = ResourceBundleUtil
						.getString(
								"com.seeyon.v3x.organization.resources.i18n.OrganizationResources",
								"org.account_form.adminName.value", "");

				member.setLoginName(voa.getAdminName());
				member.setPassword("000000");
				member.setName(adminNameValue);
				member.setIsAdmin(true);
				member.setOrgAccountId(voa.getId());
				member.setOrgDepartmentId(voa.getId());
				orgManagerDirect.addMember(member);
				addAccountInitialRole(voa);
				// 添加单位文档
				docLibManager.addSysDocLibs(voa.getId());
				// --向新增的单位增加套红模板..
				edocDocTemplateManager.addEdocTemplate(voa.getId());

			}
			DataUtil.setResultList(resultlst, (List) mp.get("new"), "", locale);
			DataUtil.setResultList(resultlst, (List) mp.get("dup"), repeat,
					locale);

			DataUtil.setResultToSession(resultlst, session);
			session.setAttribute("resultlst", resultlst);
			session.removeAttribute("excellst");
			session.removeAttribute("datastrulst");
			out.println("<script>");
			out.println("parent.getA8Top().contentFrame.topFrame.importReport();");
			out.println("</script>");
			return null;
		} else {
			DataUtil.setResultList(resultlst, (List) mp.get("new"), "", locale);
			DataUtil.setResultList(resultlst, (List) mp.get("dup"), repeat,
					locale);

			DataUtil.setResultToSession(resultlst, session);
			session.setAttribute("resultlst", resultlst);
			session.removeAttribute("excellst");
			session.removeAttribute("datastrulst");
			out.println("<script>");
			out.println("parent.getA8Top().contentFrame.topFrame.importReport();");
			out.println("</script>");
			return null;
		}
	}

	private List addMemberLoginname(List members) {// tanglh
		if (members == null)
			return members;
		List ms = new ArrayList();

		for (int i = 0; i < members.size(); i++) { //
			V3xOrgMember vomember = (V3xOrgMember) members.get(i);
			log.info("vomember.id=" + vomember.getId());// tanglh
			log.info("vomember.getLoginName()=" + vomember.getLoginName());// tanglh

			V3xOrgMember mm = null;
			try {
				mm = orgManagerDirect.getMemberById(vomember.getId());
			} catch (Exception e) {

			}
			if (mm == null || mm.getIsDeleted()) {
				log.info("null mm");
				continue;
			}
			boolean done = true;
			if (!org.springframework.util.StringUtils.hasText(mm.getPassword())) {
				mm.setPassword("123456");
				done = false;
			} else {
				log.info("password=" + mm.getPassword());
			}
			if (!org.springframework.util.StringUtils
					.hasText(mm.getLoginName())
					|| "null".equals(mm.getLoginName())) {
				mm.setLoginName(vomember.getLoginName());
				done = false;
			} else {
				log.info("LoginName=" + mm.getLoginName());
			}
			if (!done) {
				try {
					orgManagerDirect.updateEntity(mm);
					log.info("update member ok!");

				} catch (Exception e) {
					log.info("update member error");
					StringBuffer sql = new StringBuffer();
					sql.append("delete from v3x_org_member where id="
							+ mm.getId());
					List sqls = new ArrayList();

					try {
						sqls.add(sql);
						getDataManagerImpl().execSQLList(sqls);
						log.info("del member ok");
					} catch (Exception ee) {
						log.info("del member ok", ee);
					}
					continue;
				}
				try {
					// 添加个人文档库
					docLibManager.addDocLib(mm.getId());
				} catch (Exception e) {

				}
			}
			ms.add(vomember);
		}
		return ms;
	}

	private void addRelationShip(V3xOrgDepartment department) throws Exception {
		List<Long> roles = department.getRoles();
		for (Iterator<Long> it = roles.iterator(); it.hasNext();) {
			V3xOrgRelationship rel = new V3xOrgRelationship();
			rel.setSourceId(department.getId());
			rel.setObjectiveId(it.next());
			rel.setType(V3xOrgEntity.ORGREL_TYPE_DEP_ROLE);
			rel.setOrgAccountId(department.getOrgAccountId());
			orgManagerDirect.updateEntity(rel);
		}

	}

	private void addAccountInitialRole(V3xOrgAccount account) throws Exception {
		// 添加系统预设的角色

		// 单位角色
		List<MetadataItem> roleNames = metadataManager
				.getMetadataItems(MetadataNameEnum.organization_account_role);
		for (Iterator<MetadataItem> it = roleNames.iterator(); it.hasNext();) {
			// 将单位角色添加到单位下
			V3xOrgRole role = new V3xOrgRole();
			MetadataItem mDataIt = it.next();
			role.setBond(V3xOrgEntity.ROLE_BOND_ACCOUNT);
			role.setName(mDataIt.getValue());
			role.setSortId(mDataIt.getSort());
			role.setOrgAccountId(account.getId());
			orgManagerDirect.addRole(role);
			account.addAccountRole(role.getId());

		}

		// 部门角色
		roleNames = metadataManager
				.getMetadataItems(MetadataNameEnum.organization_department_role);
		for (Iterator<MetadataItem> it = roleNames.iterator(); it.hasNext();) {
			V3xOrgRole role = new V3xOrgRole();
			MetadataItem mDataIt = it.next();
			role.setName(mDataIt.getValue());
			role.setBond(V3xOrgEntity.ROLE_BOND_DEPARTMENT);
			role.setSortId(mDataIt.getSort());
			role.setOrgAccountId(account.getId());
			orgManagerDirect.addRole(role);
		}
	}

	/**
	 * 页面跳转
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView closeWin(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		String selectvalue = (String) session.getAttribute("importType");
		session.removeAttribute("selectvalue");
		session.removeAttribute("radiovalue");
		session.removeAttribute("sheetnumber");
		session.removeAttribute("language");
		session.removeAttribute("datastrulst");
		session.removeAttribute("excellst");
		session.removeAttribute("importType");
		session.removeAttribute("allmap");
		session.removeAttribute("datarowlist");
		session.removeAttribute("importType");
		if (selectvalue.equals("level")) {
			return redirectModelAndView("/organization.do?method=organizationFrame&from=Level");
		} else if (selectvalue.equals("post")) {
			return redirectModelAndView("/organization.do?method=organizationFrame&from=Post");
		} else if (selectvalue.equals("team")) {
			return redirectModelAndView("/organization.do?method=organizationFrame&from=Team");
		} else if (selectvalue.equals("member")) {
			return redirectModelAndView("/organization.do?method=organizationFrame&from=Member");
		} else if (selectvalue.equals("department")) {
			return redirectModelAndView("/organization.do?method=showframe&style=list");
		} else if (selectvalue.equals("account")) {
			return redirectModelAndView("/organization.do?method=organizationFrame&from=Account");
		} else {
			return null;
		}
	}

	private File uploadFile(HttpServletRequest request) throws Exception {
		Map<String, V3XFile> v3xFiles = new HashMap<String, V3XFile>();
		File fil = null;
		try {
			V3XFile v3x = null;
			v3xFiles = fileManager.uploadFiles(request, "xls", null);
			String key = "";
			if (v3xFiles != null) {
				Iterator<String> keys = v3xFiles.keySet().iterator();
				while (keys.hasNext()) {
					key = keys.next();
					v3x = (V3XFile) v3xFiles.get(key);
				}
			}
			fil = fileManager.getFile(v3x.getId(), v3x.getCreateDate());
		} catch (Exception e) {
		}
		return fil;
	}

	@SuppressWarnings("deprecation")
	public ModelAndView importOrganization(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

	/**
	 * 将组织机构报告导出结果
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView exportOrgReport(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		List<DataRow[]> datarowList = (List<DataRow[]>) session
				.getAttribute("datarowList");
		String language = (String) session.getAttribute("language");
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		Locale locale = null;
		Pagination.setNeedCount(false);
		// 根据选择导入文件语言获取对应locale对象
		if (language.equals("zh_CN")) {
			locale = Locale.CHINA;
		} else if (language.equals("en")) {
			locale = Locale.ENGLISH;
		} else if (language.equals("zh")) {
			locale = Locale.TAIWAN;
		}
		String import_organization_report = ResourceBundleUtil.getString(
				resource, locale, "import.organization.report");
		String import_data = ResourceBundleUtil.getString(resource, locale,
				"import.data");
		String import_result = ResourceBundleUtil.getString(resource, locale,
				"import.result");
		String import_description = ResourceBundleUtil.getString(resource,
				locale, "import.description");
		DataRecord[] dataRecordArray = new DataRecord[datarowList.size()];

		for (int sheet = 0; sheet < datarowList.size(); sheet++) {
			// 将导入结果添加到excel中
			DataRecord dataRecord = new DataRecord();
			DataRow[] datarowOld = datarowList.get(sheet);
			DataRow[] datarowNew = new DataRow[datarowOld.length - 2];
			int newLoop = 0;
			for (int oldLoop = 2; oldLoop < datarowOld.length; oldLoop++) {
				datarowNew[newLoop] = datarowOld[oldLoop];
				newLoop++;
			}
			try {
				dataRecord.addDataRow(datarowNew);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			String[] columnName = { import_data, import_result,
					import_description };
			dataRecord.setColumnName(columnName);
			dataRecord.setTitle(datarowOld[0].getCell()[1].getContent());
			dataRecord.setSheetName(datarowOld[0].getCell()[1].getContent());
			dataRecordArray[sheet] = dataRecord;
		}

		try {
			fileToExcelManager.save(request, response,
					import_organization_report, "location.href",
					dataRecordArray);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public ModelAndView downloadExpToExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String key = (String) request.getParameter("key");
		String filename = (String) request.getParameter("filename");

		String url = DataUtil.getRealDownloadExpToExcelUrl(key, filename);
		url = request.getContextPath() + url;
		log.info("url=" + url);
		response.sendRedirect(url);
		return null;
	}

	public ModelAndView toImpBase(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return this.getIoManager().toImpBase(request, response);
	}

	public ModelAndView toExpBase(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return this.getIoManager().toExpBase(request, response);
	}

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}

	public void setEdocDocTemplateManager(
			EdocDocTemplateManager edocDocTemplateManager) {
		this.edocDocTemplateManager = edocDocTemplateManager;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView departManageInitPage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(
				"organization/department/InitPageShow");
		return mav;
	}

	public CommonPhraseManager getPhraseManager() {
		return phraseManager;
	}

	public void setPhraseManager(CommonPhraseManager phraseManager) {
		this.phraseManager = phraseManager;
	}

	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public IOManager getIoManager() {
		return ioManager;
	}

	public void setIoManager(IOManager ioManager) {
		this.ioManager = ioManager;
	}

	private User catchCurrentUser() {
		return CurrentUser.get();
	}

	private void generateInitData(long accountId) throws Exception {
		// --end--

		// --start-- 每新建一个单位,复制一套公文与协同得节点权限过去
		FlowPermHelper.generateFlowPermByAccountId(accountId);
		// --end--

		// --start-- 每新建一个单位,复制一套公文单
		// EdocHelper.generateEdocFormByAccountId(account.getId());
		// --end--
		EdocHelper.generateZipperFleet(accountId);

		// -- start-- 每新建一个单位，复制常用语与公共资源
		resourceManager.generateResource(accountId);
		phraseManager.generateCommonPharse(accountId);
		// --end--

		// --start-- 每新建一个单位,增加默认的表单分类
		OperHelper.generateFormsortByAccountId(accountId);
		// --end--

		// --start-- 每新建一个单位,增加默认的新闻分类
		this.newsTypeManager.initNewsType(accountId);
		// --end--

		// --start-- 每新建一个单位,增加默认的公告分类
		this.bulTypeManager.initBulType(accountId);
		// --end--

		// --start-- 每新建一个单位,增加默认的调查分类
		this.inquiryManager.initInquiryType(accountId);
		// --end--

		// --start-- 每新建一个单位,增加默认的讨论分类
		this.bbsBoardManager.initBbsBoard(accountId);
		// --end--

		// --start-- 每新建一个单位,增加默认的项目分类
		this.projectManager.initProjectType(accountId);
		// --end--
	}

	/*
	 * 判断人员的代办事项(改为只判断表单)
	 */
	public int getMemberPending(Long memId) {
		try {
			// 此部分注释掉是因为代办事项应用逻辑修改，改为只判断表单
			/*
			 * Integer[] apps = new Integer[7]; apps[0] =
			 * ApplicationCategoryEnum.collaboration.key(); apps[1] =
			 * ApplicationCategoryEnum.edocSend.key(); apps[2] =
			 * ApplicationCategoryEnum.edocRec.key(); apps[3] =
			 * ApplicationCategoryEnum.edocSign.key(); apps[4] =
			 * ApplicationCategoryEnum.exSend.key(); apps[5] =
			 * ApplicationCategoryEnum.exSign.key(); apps[6] =
			 * ApplicationCategoryEnum.edocRegister.key(); Set appInts =
			 * affairManager.hasPending2(apps, memId).keySet();
			 * if(!appInts.isEmpty()){ return (Integer)appInts.toArray()[0];
			 * }else if(inquiryManager.hasInquiryNoCheck(memId)){ return
			 * ApplicationCategoryEnum.inquiry.key(); }else
			 * if(bulDataManager.hasPendingOfUser(memId)){ return
			 * ApplicationCategoryEnum.bulletin.key(); }else
			 * if(newsDataManager.hasPendingOfUser(memId)){ return
			 * ApplicationCategoryEnum.news.key(); }else
			 * if(iOperBase.queryOwnerListByownerid(memId)){ return
			 * ApplicationCategoryEnum.form.key(); }else
			 * if(officeAdminInfoTarget.hasAdminInUse(memId)){ return
			 * ApplicationCategoryEnum.office.key(); }
			 */
			if (iOperBase.queryOwnerListByownerid(memId)) {
				return ApplicationCategoryEnum.form.key();
			}
		} catch (Exception ex) {
			log.error("error find member's pending affair", ex);
		}
		return -1;
	}

	/**
	 * 分页
	 * 
	 */
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}

	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public SignetManager getSignetManager() {
		return signetManager;
	}

	public void setSignetManager(SignetManager signetManager) {
		this.signetManager = signetManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView externalHome(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/external/externalHome");
		return modelAndView;
	}

	// 2017-4-1 诚佰公司 文件下载管理主界面
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView filedownloadHome(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/filedownload/filedownloadHome");
		return modelAndView;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView externalHomeEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/external/externalHomeEntry");
		String settingType = request.getParameter("settingType");
		if (!StringUtils.isEmpty(settingType)) {
			modelAndView.addObject("settingType",
					request.getParameter("settingType"));
		} else {
			modelAndView.addObject("settingType", "Dept");
		}
		return modelAndView;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView listExternalDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/external/listExternalDept");
		List<V3xOrgEntity> externalDepts = orgManagerDirect
				.getEntityListNoRelation(
						V3xOrgDepartment.class.getSimpleName(), "isInternal",
						false, CurrentUser.get().getLoginAccount(), true);
		List<WebV3xOrgDepartment> resultlist = new ArrayList<WebV3xOrgDepartment>();
		for (V3xOrgEntity externalDept : externalDepts) {
			V3xOrgDepartment dept = (V3xOrgDepartment) externalDept;
			V3xOrgDepartment parent = orgManagerDirect.getParentDepartment(dept
					.getId());
			WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
			webdept.setV3xOrgDepartment(dept);
			if (null != parent) {
				webdept.setParentId(parent.getId());
				webdept.setParentName(parent.getName());
			} else {
				if (dept.getPath().indexOf(".") > 0
						&& (dept.getPath().indexOf(".") == dept.getPath()
								.lastIndexOf("."))) {
					webdept.setParentId(dept.getOrgAccountId());
					V3xOrgAccount account = orgManagerDirect
							.getAccountById(CurrentUser.get().getLoginAccount());
					webdept.setParentName(account.getName());
				}
			}
			resultlist.add(webdept);
		}
		modelAndView.addObject("deptlist", resultlist);
		return modelAndView;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView addExternalDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/external/addExternalDept");
		WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
		V3xOrgDepartment dept = new V3xOrgDepartment();
		// 获得最大排序号
		Integer maxSortNum = orgManagerDirect
				.getMaxExternalDeptSortNum(CurrentUser.get().getLoginAccount());
		dept.setSortId(maxSortNum + 1);
		dept.setEnabled(true);
		dept.setIsInternal(false);
		webdept.setV3xOrgDepartment(dept);
		modelAndView.addObject("dept", webdept);
		modelAndView.addObject("oper", "addDept");
		return modelAndView;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView createExternalDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		long accountid = user.getLoginAccount();
		String parentId = request.getParameter("parentId");
		V3xOrgDepartment dept = new V3xOrgDepartment();
		bind(request, dept);
		V3xOrgDepartment parent = null;
		dept.setOrgAccountId(accountid);

		Long _parent_Id = 0L;
		if (null != parentId && !parentId.equals("")) {
			parent = orgManagerDirect.getDepartmentById(Long
					.parseLong(parentId));
		} else {
			_parent_Id = accountid;
		}

		if (null != parent) {
			_parent_Id = parent.getId();
		} else {
			_parent_Id = accountid;
		}

		// 加入同级部门的校验
		List<V3xOrgDepartment> depts = orgManagerDirect.getChildDepartments(
				_parent_Id, true);
		for (V3xOrgDepartment orgDept : depts) {
			if (orgDept.getName().equals(dept.getName())) {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_brother_dept_name'));");
				out.println("</script>");
				super.rendJavaScript(response, "parent.toEditMember()");
				return null;
			}
		}

		// 部门排序号的重复处理
		String isInsert = request.getParameter("isInsert");
		if (isInsert.equals("1")
				&& orgManagerDirect.isPropertyDuplicated(
						V3xOrgDepartment.class.getSimpleName(), "sortId",
						dept.getSortId())) {
			orgManagerDirect.insertRepeatSortNum(
					V3xOrgDepartment.class.getSimpleName(), accountid,
					dept.getSortId());
		}
		dept.setIsInternal(false);
		V3xOrgDepartment addedDept = orgManagerDirect.addDepartment(dept,
				_parent_Id);

		// 触发创建部门事件
		eventListener.addDepartment(addedDept);
		// 记录日志
		appLogManager.insertLog(user,
				AppLogAction.Organization_NewExternalDept, user.getName(),
				addedDept.getName());

		super.rendJavaScript(
				response,
				"parent.doEndExternalDept('" + accountid + "', 'add', '"
						+ dept.getId() + "', '" + _parent_Id + "', '"
						+ Strings.escapeJavascript(dept.getName()) + "', "
						+ dept.getSortId() + ")");
		return null;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView editExternalDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/external/editExternalDept");
		String strid = request.getParameter("id");
		Long _id = Long.parseLong(strid);
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(_id);
		WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
		webdept.setV3xOrgDepartment(dept);
		// 取得登陆单位
		User user = CurrentUser.get();
		V3xOrgAccount account = orgManagerDirect.getAccountById(user
				.getLoginAccount());

		V3xOrgDepartment parent = orgManagerDirect.getParentDepartment(_id);
		if (null != parent) {
			webdept.setParentId(parent.getId());
			webdept.setParentName(parent.getName());
		} else {
			if (dept.getPath().indexOf(".") > 0
					&& (dept.getPath().indexOf(".") == dept.getPath()
							.lastIndexOf("."))) {
				webdept.setParentId(dept.getOrgAccountId());
				webdept.setParentName(account.getName());
				boolean parentIsAccount = true;
				modelAndView.addObject("parentIsAccount", parentIsAccount);
			}
		}
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			modelAndView.addObject("readOnly", readOnly);
			modelAndView.addObject("preview", 0);
		} else {
			modelAndView.addObject("preview", 1);
		}
		modelAndView.addObject("dept", webdept);
		return modelAndView;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView updateExternalDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String deptId = request.getParameter("id");
		Long lDeptId = Long.valueOf(deptId);
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(lDeptId);

		// 为事件调用记录修改前的部门
		V3xOrgDepartment deptBeforeUpdate = new V3xOrgDepartment();
		BeanUtils.copyProperties(deptBeforeUpdate, dept);

		Locale local = LocaleContext.getLocale(request);
		Integer orgSortId = dept.getSortId();
		boolean orgEnable = dept.getEnabled();
		boolean nowEnable = request.getParameter("enabled").equals("1") ? true
				: false;
		// 如果部门被停用则停用子部门
		if (orgEnable && (!nowEnable)) {
			List<V3xOrgMember> enabledMembers = new ArrayList<V3xOrgMember>();
			List<V3xOrgMember> deptMembers = orgManagerDirect
					.getMembersByDepartment(dept.getId(), true, null,
							CurrentUser.get().getLoginAccount());
			for (V3xOrgMember member : deptMembers) {
				if (member.getEnabled() == true) {
					enabledMembers.add(member);
				}
			}
			if (enabledMembers.size() > 0) {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_unenabled_department_member'));");
				out.println("</script>");
				super.rendJavaScript(response, "parent.toEditMember()");
				return null;
			}
		} else if ((!orgEnable) && nowEnable) {
			V3xOrgDepartment parent = orgManagerDirect
					.getParentDepartment(lDeptId);
			if (parent != null) {
				if (!parent.getEnabled()) {
					PrintWriter out = response.getWriter();
					out.println("<script>");
					out.println("alert('"
							+ Constant.getString("depatition.unenable.parent",
									local) + "');");
					out.println("</script>");
					super.rendJavaScript(response, "parent.toEditMember()");
					return null;
				}
			}
		}
		// 部门排序号的重复处理
		Integer sortId = Integer.valueOf(request.getParameter("sortId"));
		String isInsert = request.getParameter("isInsert");
		if (!orgSortId.equals(sortId)
				&& isInsert.equals("1")
				&& orgManagerDirect.isPropertyDuplicated(
						V3xOrgDepartment.class.getSimpleName(), "sortId",
						sortId, dept.getId())) {
			orgManagerDirect.insertRepeatSortNum(V3xOrgDepartment.class
					.getSimpleName(), CurrentUser.get().getLoginAccount(),
					sortId);
		}
		bind(request, dept);

		String parentId = request.getParameter("parentId");
		// 更新父部门
		V3xOrgDepartment orgParent = orgManagerDirect
				.getParentDepartment(new Long(deptId));
		if (orgParent == null) {
			Long pId = Long.parseLong(parentId);
			if (orgManagerDirect.getDepartmentById(pId) != null) {
				orgManagerDirect.setDepPath(dept, pId);
			}
		} else {
			Long orgParentId = orgParent.getId();
			if (parentId != null && StringUtils.isNotBlank(parentId)) {
				if (!orgParentId.equals(Long.parseLong(parentId))) {
					orgManagerDirect.setDepPath(dept, Long.parseLong(parentId));
				}
			}
		}

		// 加入同级部门的校验
		List<V3xOrgDepartment> depts = orgManagerDirect.getChildDepartments(
				Long.parseLong(parentId), true);
		for (V3xOrgDepartment orgDept : depts) {
			if (orgDept.getName().equals(dept.getName())
					&& !orgDept.equals(dept)) {
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_brother_dept_name'));");
				out.println("</script>");
				super.rendJavaScript(response, "parent.toEditMember()");
				return null;
			}
		}
		orgManagerDirect.updateEntity(dept);
		// 触发更新部门事件
		eventListener.updateDepartment(deptBeforeUpdate, dept);

		// 记录日志
		User user = CurrentUser.get();
		appLogManager.insertLog(user,
				AppLogAction.Organization_UpdateExternalDept, user.getName(),
				dept.getName());

		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('" + Constant.getString("organization.yes", local)
				+ "');");
		out.println("</script>");

		super.rendJavaScript(
				response,
				"parent.parent.listFrame.location.reload(true);"
						+ "parent.parent.detailFrame.location.href=\"/seeyon/common/detail.jsp\";");
		return null;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView destroyExternalDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String strids = request.getParameter("ids");
		Locale local = LocaleContext.getLocale(request);
		// 日志信息
		List<String[]> appLogs = new ArrayList<String[]>();
		User user = CurrentUser.get();
		if (null != strids && !strids.equals("")) {
			String[] arrids = strids.split(",");
			Long longid = null;

			List<V3xOrgDepartment> deptList = new ArrayList<V3xOrgDepartment>();
			if (null != arrids && arrids.length > 0) {
				for (String id : arrids) {
					longid = Long.parseLong(id);
					V3xOrgDepartment dept1 = orgManagerDirect
							.getDepartmentById(longid);
					deptList.add(dept1);
					// 检查部门下是否存在成员
					List<V3xOrgMember> members = orgManagerDirect
							.getMembersByProperty("orgDepartmentId",
									dept1.getId(), dept1.getOrgAccountId());
					boolean isAllMemberUnEnabled = true;
					for (V3xOrgMember mem : members) {
						if (mem.getEnabled()) {
							isAllMemberUnEnabled = false;
						}
					}
					if (ListUtils.EMPTY_LIST.equals(members)
							|| isAllMemberUnEnabled) {
						orgManagerDirect.deleteEntity(dept1);
						// 删除部门空间的讨论 ---------lucx--------
						try {
							this.bbsBoardManager.deleteV3xBbsBoard(dept1
									.getId());
						} catch (org.springframework.dao.DataIntegrityViolationException me) {
							log.error(me.getMessage(), me);
						}
						// 删除部门空间的公告---
						bulTypeManager.delDept(dept1.getId());
					} else {
						// 提示用户部门下有有效成员，不能删除
						PrintWriter out = response.getWriter();
						out.println("<script>");
						out.println("alert('"
								+ Constant.getString(
										"ext.depatition.delete.member", local)
								+ "');");
						out.println("</script>");
						out.flush();
						return super.refreshWindow("parent");
					}
				}

				// 全部成功触发删除部门事件
				for (V3xOrgDepartment dept : deptList) {
					eventListener.deleteDepartment(dept);
					String[] appLog = new String[2];
					appLog[0] = user.getName();
					appLog[1] = dept.getName();
					appLogs.add(appLog);
				}
			}
		}

		// 记录日志
		appLogManager.insertLogs(user,
				AppLogAction.Organization_DeleteExternalDept, appLogs);

		return super.refreshWindow("parent");
	}

	// 2017-4-1 诚佰公司 文件下载管理列表
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView listFiledownload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/filedownload/listFiledownload");
		List<FileDownload> resultlist = new ArrayList<FileDownload>();
		long deptId = -1;
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		resultlist = fileDownloadManager.searchFiledownload(condition,
				textfield, searchManager, true);

		modelAndView.addObject("condition", condition);
		modelAndView.addObject("textfield", textfield);
		modelAndView.addObject("resultlist", resultlist);
		return modelAndView;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView listExternalMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/external/listExternalMember");
		List<WebV3xOrgMember> resultlist = new ArrayList<WebV3xOrgMember>();
		long deptId = -1;
		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");
		List<V3xOrgMember> memberlist = new ArrayList<V3xOrgMember>();
		memberlist = OrganizationHelper.searchMember(condition, textfield,
				searchManager, orgManagerDirect, true, false, true);
		if (null != memberlist) {
			for (V3xOrgEntity memberEnt : memberlist) {
				V3xOrgMember member = (V3xOrgMember) memberEnt;
				deptId = member.getOrgDepartmentId();

				WebV3xOrgMember webMember = new WebV3xOrgMember();
				webMember.setV3xOrgMember(member);
				V3xOrgEntity dept = orgManagerDirect.getEntityNoRelation(
						V3xOrgDepartment.class.getSimpleName(), "id", deptId,
						CurrentUser.get().getLoginAccount());
				if (dept != null) {
					webMember.setDepartmentName(dept.getName());
				}
				// 设置工作范围
				String workscopeNames = null;
				List<V3xOrgRelationship> rels = orgManagerDirect
						.getRelationships("type",
								V3xOrgEntity.ORGREL_TYPE_EXTERNAL_SCOPE,
								"sourceId", member.getId());
				List<V3xOrgEntity> ents = new ArrayList<V3xOrgEntity>();
				for (V3xOrgRelationship rel : rels) {
					if (rel.getExtend3() != null) {
						V3xOrgEntity ent = orgManagerDirect
								.getEntityNoRelation(
										OrgHelper.getEntityType(
												rel.getExtend3())
												.getSimpleName(), "id", rel
												.getObjectiveId(),
										V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
						if (ent != null) {
							String entName = ent.getName();
							if (workscopeNames == null) {
								workscopeNames = entName;
							} else {
								workscopeNames += "," + entName;
							}
							ents.add(ent);
						}
					}
				}
				webMember.setWorkscope(ents);
				webMember.setStateName(workscopeNames);
				if (LDAPConfig.getInstance().getIsEnableLdap()
						&& SystemEnvironment
								.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
										.name())) {
					// 组装LDAP/AD帐号
					try {
						webMember.setTypeName(event.getLdapAdLoginName(member
								.getLoginName()));
						modelAndView.addObject("hasLDAPAD", true);
					} catch (Exception e) {
						log.error("ldap/ad 显示ldap帐号！", e);
					}

				}
				resultlist.add(webMember);
			}
		}
		modelAndView.addObject("condition", condition);
		modelAndView.addObject("textfield", textfield);
		modelAndView.addObject("memberlist", resultlist);
		return modelAndView;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView addExternalMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/external/addExternalMember");
		// 获得当前排序号的最大值
		Integer maxSortNum = orgManagerDirect
				.getMaxExternalMemberSortNum(CurrentUser.get()
						.getLoginAccount());
		WebV3xOrgMember webMember = new WebV3xOrgMember();
		V3xOrgMember member = new V3xOrgMember();
		member.setEnabled(true);
		member.setPassword("");
		member.setSortId(maxSortNum + 1);
		webMember.setV3xOrgMember(member);
		modelAndView.addObject("member", webMember);
		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		modelAndView.addObject("orgMeta", orgMeta);
		// 获取首选语言的下拉列表
		Map<String, Metadata> globalMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.global);
		modelAndView.addObject("globalMeta", globalMeta);
		// 获取默认菜单权限
		String securityIds = null;
		String securityNames = null;

		// 增加默认权限
		Security extSecurity = this.menuManager.getSecurity(4L);
		if (extSecurity != null && extSecurity.isAvailable()) {
			securityIds = extSecurity.getId().toString();
			securityNames = extSecurity.getName();
		} else {
			List<Security> defaultSecurities = this.menuManager
					.getDefaultSecurities();
			for (Security security : defaultSecurities) {
				if (securityIds == null) {
					securityIds = security.getId().toString();
					securityNames = security.getName();
				} else {
					securityIds += "," + security.getId();
					securityNames += "," + security.getName();
				}
			}
		}
		if (LDAPConfig.getInstance().getIsEnableLdap()
				&& SystemEnvironment
						.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
								.name())) {
			modelAndView.addObject("hasLDAPAD", true);
			modelAndView.addObject("addstate", true);
		}
		// List<SpaceModel> spaceList =
		// spaceManager.getAdminCanManagerSpace(CurrentUser.get().getLoginAccount(),
		// SpaceTypeClass.personal, "state",
		// String.valueOf(SpaceState.normal.ordinal()));
		// modelAndView.addObject("spaceList", spaceList);
		// Long currentSpaceId =
		// spaceManager.getPersonalSpaceId4Create(false,CurrentUser.get().getLoginAccount());
		// currentSpaceId
		// modelAndView.addObject("currentSpaceId", currentSpaceId);

		modelAndView.addObject("securityIds", securityIds);
		modelAndView.addObject("securityNames", securityNames);
		return modelAndView;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView createExternalMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		PrintWriter out = response.getWriter();

		SingleMemberBuilder builder = new SingleMemberBuilder();
		builder.setOrgManagerDirect(orgManagerDirect);
		try {
			String ldapEntry = request.getParameter("ldapUserCodes");
			String selectOU = request.getParameter("selectOU");
			User user = CurrentUser.get();
			long accountId = user.getLoginAccount();
			long deptId = Long.parseLong(request
					.getParameter("orgDepartmentId"));

			String officeNum = request.getParameter("officeNum");
			String isInsert = request.getParameter("isInsert");
			String securityIdsStr = request.getParameter("securityIds");
			String workscopeidtype = request.getParameter("workscopeidtype");

			V3xOrgMember member = new V3xOrgMember();
			bind(request, member);
			member.setProperty("officeNum", officeNum);
			member.setProperty("levelName", request.getParameter("levelName"));
			member.setProperty("postName", request.getParameter("postName"));

			// branches_a8_v350_r_gov GOV-1277 杨帆 添加外部人员职级 start
			boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag(); // 政务版标识
			if (isGovVersion) {
				member.setProperty("dutyLevelName",
						request.getParameter("dutyLevelName"));
			}
			// branches_a8_v350_r_gov GOV-1277 杨帆 添加外部人员职级 end

			member.setOrgAccountId(accountId);
			// 外部人员的涉密等级为 非密
			member.setSecretLevel(1);
			// 部门是否可用（存在且启用）
			if (!builder.checkDepartment(deptId)) {
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_unabled_property','"
						+ Constant
								.getString4CurrentUser("org.member_form.deptName.label")
						+ "'));");
				out.println("</script>");
				super.rendJavaScript(response, "parent.toEditMember()");
				return null;
			}
			// 人员排序号的重复处理(重复序号处理：1 插入 0 重复)
			if (isInsert.equals("1")
					&& orgManagerDirect.isPropertyDuplicated(
							V3xOrgMember.class.getSimpleName(), "sortId",
							member.getSortId())) {
				orgManagerDirect.insertRepeatSortNum(
						V3xOrgMember.class.getSimpleName(), accountId,
						member.getSortId());
			}
			member.setIsInternal(false);
			V3xOrgMember newMember = new V3xOrgMember();
			BeanUtils.copyProperties(newMember, member);
			orgManagerDirect.addMember(member);
			// 添加个人文档库
			docLibManager.addDocLib(member.getId());
			// 添加个人博客记录
			blogManager.createEmployee(member.getId(), accountId);

			// 添加个人菜单权限
			if (null != securityIdsStr && securityIdsStr.length() > 0) {
				String[] securityIds = securityIdsStr.split(",");
				List<Long> securityIdsList = new ArrayList<Long>();
				for (String idStr : securityIds) {
					securityIdsList.add(Long.parseLong(idStr));
				}
				this.menuManager.saveMemberSecurity(member.getId(),
						member.getOrgAccountId(), securityIdsList);
			}
			// 添加人员访问权限
			if (null != workscopeidtype && workscopeidtype.length() > 0) {
				String[] workscopeidtypes = workscopeidtype.split(",");
				List<V3xOrgRelationship> rels = new ArrayList<V3xOrgRelationship>();
				for (String idtypeStr : workscopeidtypes) {
					V3xOrgRelationship rel = new V3xOrgRelationship();
					rel.setType(V3xOrgEntity.ORGREL_TYPE_EXTERNAL_SCOPE);
					rel.setOrgAccountId(member.getOrgAccountId());
					rel.setSourceId(member.getId());
					String[] idtypeArray = idtypeStr.split("_");
					rel.setObjectiveId(Long.parseLong(idtypeArray[1]));
					rel.setExtend3(idtypeArray[0]);
					rels.add(rel);
				}
				orgManagerDirect.updateExternalMemberWorkScope(member.getId(),
						rels);
			}
			// 触发创建人员事件
			eventListener.addMember(newMember);
			// 日志
			appLogManager.insertLog(user,
					AppLogAction.Organization_NewExternalMember,
					user.getName(), member.getName());

			try {
				indexManager.index(((IndexEnable) organizationServices)
						.getIndexInfo(member.getId()));
			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
			}
			if (LDAPConfig.getInstance().getIsEnableLdap()
					&& SystemEnvironment
							.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
									.name())) {
				try {
					if (StringUtils.isBlank(ldapEntry)) {
						event.newAddLdapPerson(newMember, selectOU);
						appLogManager.insertLog(user,
								AppLogAction.LDAP_Account_Create,
								newMember.getName(), selectOU);
					} else {
						String[] errorResult = event.addMember(newMember,
								ldapEntry);
						if (errorResult != null && errorResult.length > 0) {
							String jsContent = "";
							for (int i = 0; i < errorResult.length; i++) {
								jsContent += errorResult[i] + "\\n";
							}
							out.println("<script>");
							out.println("alert('" + jsContent + "');");
							out.println("</script>");
							out.close();
						}
						appLogManager.insertLog(user,
								AppLogAction.LDAP_Account_Bing_Create,
								newMember.getName(), ldapEntry);
					}
				} catch (Exception e) {
					log.error("ldap/ad 添加人员绑定不成功！", e);
					// throw new BusinessException("ldap/ad 添加人员绑定不成功！",e);
					if (e.getMessage().indexOf("error code 19") != -1) {
						out.println("<script>");
						out.println("alert('"
								+ ResourceBundleUtil.getString(
										LDAPConfig.LDAP_RESOURCE_NAME,
										"ldap.log.error.tip") + "');");
						out.println("</script>");
					}
				}
			}
			super.rendJavaScript(response, "parent.doEndMember('" + accountId
					+ "')");
			return null;
		} catch (BusinessException e) {
			log.error("error add member");
			return null;
		}
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView editExternalMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/external/editExternalMember");
		String id = request.getParameter("id");
		V3xOrgMember member = orgManagerDirect
				.getMemberById(Long.parseLong(id));
		long deptId = member.getOrgDepartmentId();
		WebV3xOrgMember webMember = new WebV3xOrgMember();
		webMember.setV3xOrgMember(member);
		// 获取扩展属性
		orgManagerDirect.loadEntityProperty(member);
		webMember.setOfficeNum(member.getProperty("officeNum"));
		webMember.setPostName(member.getProperty("postName"));
		webMember.setLevelName(member.getProperty("levelName"));
		// branches_a8_v350_r_gov GOV-1277 杨帆 添加外部人员职级 start
		boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag(); // 政务版标识
		if (isGovVersion) {
			webMember.setDutyLevelName(member.getProperty("dutyLevelName"));
		}
		// branches_a8_v350_r_gov GOV-1277 杨帆 添加外部人员职级 end
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
		if (dept != null) {
			webMember.setDepartmentName(dept.getName());
		}
		modelAndView.addObject("member", webMember);
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			modelAndView.addObject("readOnly", readOnly);
			modelAndView.addObject("preview", 0);
		} else {
			modelAndView.addObject("preview", 1);
		}
		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		modelAndView.addObject("orgMeta", orgMeta);
		// 判断是否回显密码
		modelAndView.addObject("showPassword", 1);

		// 获取该用户菜单权限
		String securityIds = null;
		String securityNames = null;
		List<Security> defaultSecurities = this.menuManager
				.getSecurityOfMember(member.getId(), member.getOrgAccountId(),
						true);
		for (Security security : defaultSecurities) {
			if (securityIds == null) {
				securityIds = security.getId().toString();
				securityNames = security.getName();
			} else {
				securityIds += "," + security.getId();
				securityNames += "," + security.getName();
			}
		}

		// 获取人员访问权限
		List<V3xOrgEntity> ents = new ArrayList<V3xOrgEntity>();
		String workscopeIds = null;
		String workscopeNames = null;
		List<V3xOrgRelationship> rels = orgManagerDirect.getRelationships(
				"type", V3xOrgEntity.ORGREL_TYPE_EXTERNAL_SCOPE, "sourceId",
				member.getId());
		for (V3xOrgRelationship rel : rels) {
			if (rel.getExtend3() != null) {
				V3xOrgEntity ent = orgManagerDirect.getEntityNoRelation(
						OrgHelper.getEntityType(rel.getExtend3())
								.getSimpleName(), "id", rel.getObjectiveId(),
						V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
				if (ent != null) {
					String entName = ent.getName();
					if (workscopeIds == null) {
						workscopeIds = rel.getObjectiveId().toString();
						workscopeNames = entName;
					} else {
						workscopeIds += "," + rel.getObjectiveId().toString();
						workscopeNames += "," + entName;
					}
					ents.add(ent);
				}
			}
		}
		if (LDAPConfig.getInstance().getIsEnableLdap()
				&& SystemEnvironment
						.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
								.name())) {
			// 组装LDAP/AD帐号
			try {
				modelAndView.addObject("ldapADLoginName",
						event.getLdapAdExUnitCode(member.getLoginName()));
				modelAndView.addObject("hasLDAPAD", true);
				modelAndView.addObject("editstate", true);
			} catch (Exception e) {
				log.error("显示ldap_ad帐号出错", e);
				throw new Exception("显示ldap_ad帐号出错", e);
			}

		}
		// List<SpaceModel> spaceList =
		// spaceManager.getAdminCanManagerSpace(CurrentUser.get().getLoginAccount(),
		// SpaceTypeClass.personal, "state",
		// String.valueOf(SpaceState.normal.ordinal()));
		// modelAndView.addObject("spaceList", spaceList);
		// Long currentSpaceId = spaceManager.getPersonalSpaceId(member);
		// currentSpaceId
		// modelAndView.addObject("currentSpaceId", currentSpaceId);

		modelAndView.addObject("workscopeids", workscopeIds);
		modelAndView.addObject("workscopenames", workscopeNames);
		modelAndView.addObject("securityIds", securityIds);
		modelAndView.addObject("securityNames", securityNames);
		modelAndView.addObject("ents", ents);
		return modelAndView;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView updateExternalMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String ldapEntry = request.getParameter("ldapUserCodes");
		User user = CurrentUser.get();
		V3xOrgMember model = new V3xOrgMember();
		bind(request, model);
		V3xOrgMember member = orgManagerDirect.getMemberById(model.getId());
		// 为事件调用记录修改前的人员
		V3xOrgMember memberBeforeUpdate = new V3xOrgMember();
		BeanUtils.copyProperties(memberBeforeUpdate, member);

		// 记录登录名是否修改了
		boolean isLoginNameModifyed = false;
		String oldLoginName = "";
		if (!model.getLoginName().equals(member.getLoginName())) {
			isLoginNameModifyed = true;
			oldLoginName = member.getLoginName();
		}
		// 人员排序号的重复处理
		Integer orgSortId = member.getSortId();
		Integer sortId = Integer.valueOf(request.getParameter("sortId"));
		String isInsert = request.getParameter("isInsert");
		if (!orgSortId.equals(sortId)
				&& isInsert.equals("1")
				&& orgManagerDirect.isPropertyDuplicated(
						V3xOrgMember.class.getSimpleName(), "sortId", sortId,
						member.getId())) {
			orgManagerDirect.insertRepeatSortNum(V3xOrgMember.class
					.getSimpleName(), CurrentUser.get().getLoginAccount(),
					sortId);
		}
		model.setStatus(member.getStatus());
		BeanUtils.copyProperties(member, model);
		member.setProperty("officeNum", request.getParameter("officeNum"));
		member.setProperty("levelName", request.getParameter("levelName"));
		member.setProperty("postName", request.getParameter("postName"));
		// branches_a8_v350_r_gov GOV-1277 杨帆 添加外部人员职级 start
		boolean isGovVersion = (Boolean) SysFlag.is_gov_only.getFlag(); // 政务版标识
		if (isGovVersion) {
			member.setProperty("dutyLevelName",
					request.getParameter("dutyLevelName"));
		}
		// branches_a8_v350_r_gov GOV-1277 杨帆 添加外部人员职级 end
		member.setIsInternal(false);
		// orgManagerDirect.updateEntity(member);
		V3xOrgMember newMember = new V3xOrgMember();
		BeanUtils.copyProperties(newMember, member);
		orgManager.updateMember(member);
		// 检查人员密码是否进行了修改,而且没有修改登录名的情况下记录日志
		String password = newMember.getPassword();
		if (password != null && !StringUtils.isEmpty(password)
				&& !password.equals(V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD)
				&& !isLoginNameModifyed) {
			if (user.isAdministrator()) {
				operationlogManager.insertOplog(member.getId(),
						ApplicationCategoryEnum.modifyPassword,
						"org.member_form.modify.password",
						"org.member_form.modify.password.label",
						member.getName());
			} else {
				operationlogManager.insertOplog(member.getId(),
						ApplicationCategoryEnum.modifyPassword,
						"org.member_form.modify.password",
						"org.member_form.modify.password.hr.label",
						user.getName(), member.getName());
			}
		}

		// 修改个人菜单权限
		String securityIdsStr = request.getParameter("securityIds");
		if (null != securityIdsStr && securityIdsStr.length() > 0) {
			String[] securityIds = securityIdsStr.split(",");
			List<Long> securityIdsList = new ArrayList<Long>();
			for (String idStr : securityIds) {
				securityIdsList.add(Long.parseLong(idStr));
			}
			this.menuManager.saveMemberSecurity(member.getId(),
					member.getOrgAccountId(), securityIdsList);
		}

		String workscopeidtype = request.getParameter("workscopeidtype");
		// 修改人员访问权限
		if (null != workscopeidtype && workscopeidtype.length() > 0) {
			String[] workscopeidtypes = workscopeidtype.split(",");
			List<V3xOrgRelationship> rels = new ArrayList<V3xOrgRelationship>();
			for (String idtypeStr : workscopeidtypes) {
				V3xOrgRelationship rel = new V3xOrgRelationship();
				rel.setType(V3xOrgEntity.ORGREL_TYPE_EXTERNAL_SCOPE);
				rel.setOrgAccountId(member.getOrgAccountId());
				rel.setSourceId(member.getId());
				String[] idtypeArray = idtypeStr.split("_");
				rel.setObjectiveId(Long.parseLong(idtypeArray[1]));
				rel.setExtend3(idtypeArray[0]);
				rels.add(rel);
			}
			orgManagerDirect
					.updateExternalMemberWorkScope(member.getId(), rels);
		}

		// 触发更新人员事件
		eventListener.updateMember(memberBeforeUpdate, newMember);
		// 日志
		appLogManager.insertLog(user,
				AppLogAction.Organization_UpdateExternalMember, user.getName(),
				member.getName());

		if (LDAPConfig.getInstance().getIsEnableLdap()
				&& SystemEnvironment
						.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
								.name())) {
			try {
				List<V3xOrgMember> memberList = null;
				V3xOrgMember memberLdap = new V3xOrgMember();
				BeanUtils.copyProperties(memberLdap, newMember);
				if (isLoginNameModifyed) {
					memberList = new ArrayList<V3xOrgMember>();
					memberLdap.setLoginName(oldLoginName);
					memberList.add(memberLdap);
					event.deleteAllBinding(orgManagerDirect, memberList);
				}
				if (event.getLdapAdExUnitCode(model.getLoginName()).equals(
						ldapEntry)
						&& !isLoginNameModifyed
						&& password != null
						&& !StringUtils.isEmpty(password)
						&& !password
								.equals(V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD)
						&& !com.seeyon.v3x.common.ldap.config.LDAPConfig
								.getInstance().isDisabledModifyPassWord()) {
					event.changePassword(memberBeforeUpdate, memberLdap);
					appLogManager.insertLog(user,
							AppLogAction.LDAP_PassWord_Update,
							memberLdap.getName(), ldapEntry);
				} else if (!event
						.getLdapAdExUnitCode(memberLdap.getLoginName()).equals(
								ldapEntry)) {
					PrintWriter out = response.getWriter();
					String[] errorResult = event.addMember(memberLdap,
							ldapEntry);

					if (errorResult != null && errorResult.length > 0) {
						String jsContent = "";
						for (int i = 0; i < errorResult.length; i++) {
							jsContent += errorResult[i] + "\\n";
						}
						out.println("<script>");
						out.println("alert('" + jsContent + "');");
						out.println("</script>");
						log.debug("jsContent" + "alert('" + jsContent + "');");
					}
					appLogManager.insertLog(user,
							AppLogAction.LDAP_Account_Bing_Create,
							memberLdap.getName(), ldapEntry);
					if (password != null
							&& !StringUtils.isEmpty(password)
							&& !password
									.equals(V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD)) {
						event.changePassword(memberBeforeUpdate, memberLdap);
						appLogManager.insertLog(user,
								AppLogAction.LDAP_PassWord_Update,
								memberLdap.getName(), ldapEntry);
					}
				}
			} catch (Exception e) {
				log.error("ldap/ad 添加人员绑定不成功！", e);
			}
		}
		super.rendJavaScript(
				response,
				"parent.parent.detailFrame.location.href=\"/seeyon/common/detail.jsp\";parent.parent.listFrame.location.reload(true);");
		return null;

	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView destroyExternalMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] membetIds = request.getParameterValues("id");
		PrintWriter out = response.getWriter();

		List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>(
				membetIds.length);
		for (String string : membetIds) {
			Long ids = Long.parseLong(string);
			// 为事件调用取人员实体
			V3xOrgMember member = orgManagerDirect.getMemberById(ids);
			memberList.add(member);
			// 判断是否有授权的印章
			if (signetManager.hasSignet(ids)) {
				V3xOrgMember mem = orgManagerDirect.getMemberById(ids);
				out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage('organizationLang.orgainzation_delete_member_has_signet','"
						+ mem.getName() + "'));");
				out.println("</script>");
				out.flush();
				return super.refreshWindow("parent");
			}
			if (LDAPConfig.getInstance().getIsEnableLdap()
					&& SystemEnvironment
							.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
									.name())) {
				try {
					List<V3xOrgMember> memberList1 = new ArrayList<V3xOrgMember>();
					memberList1.add(member);
					event.deleteAllBinding(orgManagerDirect, memberList1);
				} catch (Exception e) {
					log.error("ldap_ad 删除人员绑定不成功！", e);
					throw new Exception("ldap_ad 删除人员绑定不成功！", e);
				}
			}
			orgManagerDirect.deleteEntity(V3xOrgEntity.ORGENT_TYPE_MEMBER, ids);
			try {
				indexManager.deleteFromIndex(
						ApplicationCategoryEnum.organization, ids);
			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}

		// 日志信息
		List<String[]> appLogs = new ArrayList<String[]>();
		User user = CurrentUser.get();
		// 全部成功后触发删除人员事件
		for (V3xOrgMember member : memberList) {
			eventListener.deleteMember(member);
			String[] appLog = new String[2];
			appLog[0] = user.getName();
			appLog[1] = member.getName();
			appLogs.add(appLog);
		}
		appLogManager.insertLogs(user,
				AppLogAction.Organization_DeleteExternalMember, appLogs);

		return super.refreshWindow("parent");
	}

	// 2017-4-1 诚佰公司 文件下载记录删除
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView destroyFiledownload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] ids = request.getParameterValues("id");
		PrintWriter out = response.getWriter();

		for (String string : ids) {
			Long id = Long.parseLong(string);
			fileDownloadManager.deleteFileDownload(id);
		}

		return super.refreshWindow("parent");
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView exportExternalMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User u = this.catchCurrentUser();
		if (u == null) {
			// DataUtil.outNullUserAlertScript(out);
			return null;
		}
		if (DataUtil.doingImpExp(u.getId())) {
			// DataUtil.outDoingImpExpAlertScript(out);
			return null;
		}

		String condition = request.getParameter("condition");
		String textfield = request.getParameter("textfield");

		String listname = "ExternalMemberList_";
		// ResourceBundleUtil.getString(resource, local, "org.post_form.list");
		listname += u.getLoginName();

		DataUtil.putImpExpAction(u.getId(), "export");
		DataRecord dataRecord = null;
		try {
			dataRecord = OrganizationHelper.exportMember(request,
					orgManagerDirect, searchManager, condition, textfield,
					false);
		} catch (Exception e) {
			DataUtil.removeImpExpAction(u.getId());
			throw e;
		}
		DataUtil.removeImpExpAction(u.getId());
		OrganizationHelper.exportToExcel(request, response, fileToExcelManager,
				listname, dataRecord);
		return null;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView transfer2Internal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(
				"organization/member/editMember");
		String id = request.getParameter("id");
		V3xOrgMember member = new V3xOrgMember(
				orgManagerDirect.getMemberById(Long.parseLong(id)));
		int sortNum = orgManagerDirect.getMaxMemberSortNum(member
				.getOrgAccountId());
		member.setOrgDepartmentId(null);
		member.setCode("");
		member.setIsInternal(false);
		member.setOrgPostId(null);
		member.setOrgLevelId(null);
		member.setSortId(sortNum + 1);
		WebV3xOrgMember webMember = new WebV3xOrgMember();
		webMember.setV3xOrgMember(member);
		// 获取扩展属性
		orgManagerDirect.loadEntityProperty(member);
		webMember.setOfficeNum(member.getProperty("officeNum"));
		modelAndView.addObject("member", webMember);
		// 取得是否是详细页面标志
		modelAndView.addObject("preview", 1);
		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);
		modelAndView.addObject("orgMeta", orgMeta);
		// 判断是否回显密码
		modelAndView.addObject("showPassword", 1);

		// 获取职务级别列表
		List<V3xOrgLevel> levels = orgManagerDirect.getAllLevels(CurrentUser
				.get().getLoginAccount(), false);
		List<V3xOrgLevel> levelsForPage = new ArrayList<V3xOrgLevel>();
		// 过滤无效项并获得最小职务级别序号
		Integer minLevelId = 1;
		for (V3xOrgLevel level : levels) {
			if (level.getEnabled()) {
				levelsForPage.add(level);
				if (minLevelId < level.getLevelId()) {
					minLevelId = level.getLevelId();
				}
			}
		}

		// 获取该用户菜单权限
		String securityIds = null;
		String securityNames = null;
		List<Security> defaultSecurities = this.menuManager
				.getDefaultSecurities();
		for (Security security : defaultSecurities) {
			if (securityIds == null) {
				securityIds = security.getId().toString();
				securityNames = security.getName();
			} else {
				securityIds += "," + security.getId();
				securityNames += "," + security.getName();
			}
		}

		// 获取人员访问权限
		List<V3xOrgEntity> ents = new ArrayList<V3xOrgEntity>();
		String workscopeIds = null;
		String workscopeNames = null;
		List<V3xOrgRelationship> rels = orgManagerDirect.getRelationships(
				"type", V3xOrgEntity.ORGREL_TYPE_EXTERNAL_SCOPE, "sourceId",
				member.getId());
		for (V3xOrgRelationship rel : rels) {
			if (rel.getExtend3() != null) {
				V3xOrgEntity ent = orgManagerDirect.getEntityNoRelation(
						OrgHelper.getEntityType(rel.getExtend3())
								.getSimpleName(), "id", rel.getObjectiveId(),
						V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
				if (ent != null) {
					String entName = ent.getName();
					if (workscopeIds == null) {
						workscopeIds = rel.getObjectiveId().toString();
						workscopeNames = entName;
					} else {
						workscopeIds += "," + rel.getObjectiveId().toString();
						workscopeNames += "," + entName;
					}
					ents.add(ent);
				}
			}
		}
		if (LDAPConfig.getInstance().getIsEnableLdap()
				&& SystemEnvironment
						.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD
								.name())) {
			// 组装LDAP/AD帐号
			try {
				modelAndView.addObject("ldapADLoginName",
						event.getLdapAdExUnitCode(member.getLoginName()));
				modelAndView.addObject("hasLDAPAD", true);
				modelAndView.addObject("editstate", true);
			} catch (Exception e) {
				log.error("显示ldap_ad帐号出错", e);
				throw new Exception("显示ldap_ad帐号出错", e);
			}

		}
		List<SpaceModel> spaceList = spaceManager.getAdminCanManagerSpace(
				CurrentUser.get().getLoginAccount(), SpaceTypeClass.personal,
				"state", String.valueOf(SpaceState.normal.ordinal()), false);
		modelAndView.addObject("spaceList", spaceList);
		Long currentSpaceId = spaceManager.getPersonalSpaceId(member);
		// currentSpaceId
		modelAndView.addObject("currentSpaceId", currentSpaceId);

		modelAndView.addObject("workscopeids", workscopeIds);
		modelAndView.addObject("workscopenames", workscopeNames);
		modelAndView.addObject("securityIds", securityIds);
		modelAndView.addObject("securityNames", securityNames);
		modelAndView.addObject("ents", ents);
		modelAndView.addObject("levels", levelsForPage);
		modelAndView.addObject("minLevelId", minLevelId);
		return modelAndView;
	}

	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView exportDepartmentMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User u = this.catchCurrentUser();
		if (u == null) {
			return null;
		}
		if (DataUtil.doingImpExp(u.getId())) {
			return null;
		}

		String pathvalue = request.getParameter("pathvalue");
		String listname = "MemberList_";
		listname += u.getLoginName();

		DataUtil.putImpExpAction(u.getId(), "export");
		DataRecord dataRecord = null;
		try {
			dataRecord = OrganizationHelper.exportDepartmentMember(request,
					metadataManager, response, fileToExcelManager,
					orgManagerDirect, principalManager, searchManager,
					pathvalue);
		} catch (Exception e) {
			DataUtil.removeImpExpAction(u.getId());
			throw e;
		}
		DataUtil.removeImpExpAction(u.getId());
		OrganizationHelper.exportToExcel(request, response, fileToExcelManager,
				listname, dataRecord);
		return null;
	}

	/**
	 * 单位是否允许删除判断工具类</br> 原则：单位下不能存在人员、部门、岗位、组、子单位等组织模型</br>
	 * 同时也不能存在已停用的部门，停用部门同样属于有效部门</br>
	 * 
	 * @param accountId
	 * @return
	 * @throws Exception
	 */
	private boolean isAccountCanBeDeleted(Long accountId) throws Exception {
		// 单位下存在未删除的组织模型时不允许删除
		List members = orgManagerDirect.getAllMembers(accountId, false);
		List depts = orgManagerDirect.getAllDepartments(accountId, false);
		List posts = orgManagerDirect.getAllPosts(accountId, false);
		List teams = orgManagerDirect.getAllTeams(accountId, false);
		List accounts = orgManagerDirect.getChildAccount(accountId,
				orgManagerDirect.getAllAccounts());
		List<V3xOrgEntity> entits = new ArrayList<V3xOrgEntity>();

		if (null != members) {
			entits.addAll(members);
		}
		if (null != depts) {
			entits.addAll(depts);
		}
		if (null != posts) {
			entits.addAll(posts);
		}
		if (null != teams) {
			entits.addAll(teams);
		}
		if (null != accounts) {
			entits.addAll(accounts);
		}

		return this.checkAccountCanDel(entits);
	}

	/**
	 * 
	 * @param entits
	 * @return true 可删除 false 不可删除
	 */
	private boolean checkAccountCanDel(List<V3xOrgEntity> entits) {
		if (null == entits || entits.size() == 0) {
			return true;
		}
		for (V3xOrgEntity entity : entits) {
			if (!entity.getIsDeleted()) {
				log.info("删除单位失败，该单位下存在该实体未被删除，该实体为:" + entity.getEntityType()
						+ entity.getName());
				return false;
			}
		}
		return false;
	}

	/**
	 * 单位是否允许停用</br> 原则：不允许存在未删除或启用的人员、部门、岗位、组、子单位</br> 可以存在停用的组织模型
	 */
	private boolean isAccountCanBeDisenable(Long accountId) throws Exception {
		// 单位下存在未删除或未停用的组织模型时不允许停用
		// Fix BUG AEIGHT-8952 2012-12-28单位下组织模型都停用就可以停用单位，不必校验是否已删除
		List members = orgManager.getAllMembers(accountId, false);
		List depts = orgManager.getAllDepartments(accountId, false);
		List posts = orgManager.getAllPosts(accountId, false);
		List teams = orgManager.getAllTeams(accountId, false);
		List accounts = orgManager.getChildAccount(accountId, false);
		if (this.checkValid4Disenable(members)
				|| this.checkValid4Disenable(depts)
				|| this.checkValid4Disenable(posts)
				|| this.checkValid4Disenable(teams)
				|| this.checkValid4Disenable(accounts)) {
			return false;
		}
		return true;
	}

	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView moveDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"organization/department/groupDept");
		return result;
	}

	@CheckRoleAccess(roleTypes = { RoleType.GroupAdmin })
	public ModelAndView moveDeptResult(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"organization/department/groupDeptResult");
		// 获得调入单位
		Long accountId = Long.parseLong(request.getParameter("accountId"));
		// 获得调整部门
		String deptIds = request.getParameter("deptIds");
		// 记录原单位
		List<Long> accountIds = new ArrayList<Long>();
		// 记录调整部门信息
		List<V3xOrgDepartment> moveDepts = new ArrayList<V3xOrgDepartment>();
		// 校验并移动部门
		if (null != deptIds && !deptIds.equals("")) {
			String[] deptIdArray = deptIds.split(",");
			List<WebV3xOrgResult> resultList = new ArrayList<WebV3xOrgResult>();
			boolean isMoveDept = false;
			for (String strid : deptIdArray) {
				WebV3xOrgResult resultModel = new WebV3xOrgResult();
				List<String[]> validateListStr = new ArrayList<String[]>();
				List<String[]> moveLogList = new ArrayList<String[]>();
				Long id = Long.parseLong(strid);
				String str1 = "";
				if (id != null) {
					V3xOrgDepartment dept = orgManagerDirect
							.getDepartmentById(id);
					str1 = dept.getName();
					List<String> validateList = validateMoveDept(id, accountId);
					// 判断是否有调入的两个重名部门
					for (V3xOrgDepartment addDept : moveDepts) {
						if (dept.getName().equals(addDept.getName())) {
							validateList.add("1");
						}
					}
					if (validateList.size() > 0) {
						for (String str : validateList) {
							String[] strArray = str.split("-");
							validateListStr.add(strArray);
						}
					} else if (validateList.size() == 0) {
						if (!accountIds.contains(dept.getOrgAccountId())) {
							accountIds.add(dept.getOrgAccountId());
						}
						moveLogList = organizationServices.moveDept(id,
								accountId);
						// 重新设置部门空间的所属单位
						spaceManager.transplantDepartmentSpace(id, accountId);
						V3xOrgAccount account = orgManagerDirect
								.getAccountById(accountId);
						// 记录调整部门
						moveDepts.add(dept);
						// 记录日志
						String OrganizationResources = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
						String groupName = ResourceBundleUtil.getString(
								OrganizationResources,
								"org.account_form.groupAdminName.value"
										+ (String) SysFlag.EditionSuffix
												.getFlag());
						appLogManager.insertLog(CurrentUser.get(),
								AppLogAction.Organization_MoveDept,
								dept.getName(), account.getName(), groupName);
						isMoveDept = true;

						// 事件
						V3xOrgDepartment d = new V3xOrgDepartment();
						BeanUtils.copyProperties(d, dept);
						d.setOrgAccountId(accountId);
						MoveDepartmentEvent gkeMoveDeptEvent = new MoveDepartmentEvent(
								this);
						gkeMoveDeptEvent.setDept(d);
						EventDispatcher.fireEvent(gkeMoveDeptEvent);
					}
					V3xOrgAccount account = orgManagerDirect
							.getAccountById(dept.getOrgAccountId());
					if (account != null)
						str1 += "(" + account.getShortname() + ")";
				}
				resultModel.setStr1(str1);
				resultModel.setStr2(String.valueOf(accountId));
				resultModel.setValidateList(validateListStr);
				resultModel.setMoveLogList(moveLogList);
				resultList.add(resultModel);
			}
			if (isMoveDept) {
				// 重新载入内存
				for (Long aId : accountIds) {
					organizationServices.reloadAccountData(aId);
				}
				organizationServices.reloadAccountData(accountId);
			}
			result.addObject("resultList", resultList);
		}

		return result;
	}

	private List<String> validateMoveDept(Long deptId, Long accountId)
			throws Exception {
		log.info(DateUtil.formatDate(new Date()) + "开始移动部门校验");
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
		V3xOrgAccount account = orgManagerDirect.getAccountById(accountId);
		List<String> validateList = new ArrayList<String>();
		if (account != null) {
			if (dept != null) {
				// 校验调整部门是否是调入单位的部门
				if (dept.getOrgAccountId().equals(accountId)) {
					validateList.add("5");
				}
				// 校验调入单位是否已经存在同级同名部门
				List<V3xOrgDepartment> accdepts = orgManagerDirect
						.getChildDepartments(accountId, true);
				if (accdepts != null && accdepts.size() > 0) {
					for (V3xOrgDepartment accdept : accdepts) {
						if (accdept.getName().equals(dept.getName())) {
							validateList.add("1");
						}
					}
				}
				// 校验部门下人员
				List<V3xOrgEntity> deptMems = orgManagerDirect.getEntityList(
						V3xOrgMember.class.getSimpleName(), "orgDepartmentId",
						deptId, dept.getOrgAccountId());
				if (deptMems != null) {
					for (V3xOrgEntity deptMemEnt : deptMems) {
						// 部门下人员的职务级别是否存在
						V3xOrgMember deptMem = (V3xOrgMember) deptMemEnt;
						V3xOrgLevel memlevel = orgManagerDirect
								.getLevelById(deptMem.getOrgLevelId());
						if (memlevel != null) {
							List<V3xOrgEntity> levelList = orgManagerDirect
									.getEntityListNoRelation(
											V3xOrgLevel.class.getSimpleName(),
											"name", memlevel.getName(),
											accountId);
							if (levelList == null || levelList.size() == 0) {
								validateList.add("2-" + deptMem.getName() + "-"
										+ memlevel.getName());
							} else {
								for (V3xOrgEntity level : levelList) {
									if (!level.getEnabled()) {
										validateList.add("3-"
												+ deptMem.getName() + "-"
												+ memlevel.getName());
									}
								}
							}
						}
					}
				}
			}
		}
		log.info(DateUtil.formatDate(new Date()) + "完成移动部门校验");
		return validateList;
	}

	private List<String> validateMoveMember(Long memberId, Long deptId)
			throws Exception {
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
		V3xOrgMember member = orgManagerDirect.getMemberById(memberId);
		List<String> validateList = new ArrayList<String>();
		if (dept != null) {
			if (member != null) {
				// 部门下人员的职务级别是否存在
				V3xOrgLevel memlevel = orgManagerDirect.getLevelById(member
						.getOrgLevelId());
				if (memlevel != null) {
					List<V3xOrgEntity> levelList = orgManagerDirect
							.getEntityListNoRelation(
									V3xOrgLevel.class.getSimpleName(), "name",
									memlevel.getName(), dept.getOrgAccountId());
					if (levelList == null || levelList.size() == 0) {
						validateList.add("2-" + member.getName() + "-"
								+ memlevel.getName());
					} else {
						for (V3xOrgEntity level : levelList) {
							if (!level.getEnabled()) {
								validateList.add("3-" + member.getName() + "-"
										+ memlevel.getName());
							}
						}
					}
				}

			}
		}
		return validateList;
	}

	public OrganizationServices getOrganizationServices() {
		return organizationServices;
	}

	public void setOrganizationServices(
			OrganizationServices organizationServices) {
		this.organizationServices = organizationServices;
	}

	/**
	 * 政务版新增——进入单位的职级列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView listDutyLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		ModelAndView result = new ModelAndView(
				"organization/dutyLevel/listDutyLevel");

		String condition = request.getParameter("condition");
		List<V3xOrgDutyLevel> levellist = null;
		if (condition != null && !"".equals(condition)) {
			String value = request.getParameter("textfield");
			levellist = orgManager.getAllDutyLevels(user.getLoginAccount(),
					condition, value);
		} else {
			levellist = orgManagerDirect.getAllDutyLevels(user
					.getLoginAccount());
		}

		// List<WebV3xOrgLevel> levellst = new ArrayList<WebV3xOrgLevel>();
		// //将职务级别重新封装
		// for(V3xOrgLevel level:levellist){
		// WebV3xOrgLevel webLevel = new WebV3xOrgLevel();
		// webLevel.setV3xOrgLevel(level);
		// if(level.getGroupLevelId()!=null){
		// V3xOrgLevel levelFor =
		// orgManagerDirect.getLevelById(level.getGroupLevelId());
		// if(levelFor!=null){
		// webLevel.setGroupLevelId(levelFor.getLevelId());
		// }
		// }
		// levellst.add(webLevel);
		// }
		result.addObject("levellist", levellist);
		// 是否显示集团职务级别映射
		// V3xOrgAccount account =
		// orgManagerDirect.getAccountById(CurrentUser.get().getLoginAccount());
		// if(Boolean.valueOf(SysFlag.org_showGroupLevelMap.getFlag().toString())
		// &&account.getSuperior().equals(V3xOrgEntity.DEFAULT_NULL_ID)
		// &&!account.getIsRoot()){
		// result.addObject("showGroupLevel", false);
		// }else{
		// result.addObject("showGroupLevel",
		// SysFlag.org_showGroupLevelMap.getFlag());
		// }
		return result;
	}

	/**
	 * 政务版——进入添加职级管理方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView addDutyLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"organization/dutyLevel/addDutyLevel");
		V3xOrgDutyLevel level = new V3xOrgDutyLevel();
		level.setEnabled(true);
		result.addObject("level", level);
		// //取得集团职务级别
		// List<V3xOrgLevel> groupLevellist =
		// orgManagerDirect.getAllLevels(orgManagerDirect.getRootAccount().getId(),false);
		// //过滤无效的级别
		// List<V3xOrgLevel> groupLevellistForPage = new
		// ArrayList<V3xOrgLevel>();
		// for(V3xOrgLevel levelForPage:groupLevellist){
		// if(levelForPage.getEnabled()){
		// groupLevellistForPage.add(levelForPage);
		// }
		// }
		// result.addObject("groupLevellist", groupLevellistForPage);
		// //是否显示集团职务级别映射
		// V3xOrgAccount account =
		// orgManagerDirect.getAccountById(CurrentUser.get().getLoginAccount());
		// if(Boolean.valueOf(SysFlag.org_showGroupLevelMap.getFlag().toString())
		// &&account.getSuperior().equals(V3xOrgEntity.DEFAULT_NULL_ID)
		// &&!account.getIsRoot()){
		// result.addObject("showGroupLevel", false);
		// }else{
		// result.addObject("showGroupLevel",
		// SysFlag.org_showGroupLevelMap.getFlag());
		// }
		return result;
	}

	/**
	 * 政务版-添加职级方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView createDutyLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		V3xOrgDutyLevel level = new V3xOrgDutyLevel();
		bind(request, level);
		level.setOrgAccountId(user.getLoginAccount());
		PrintWriter out = response.getWriter();
		Locale local = LocaleContext.getLocale(request);
		boolean continues = request.getParameterValues("cont") != null; // 继续添加
																		// 标识
		// 判断集团职务映射(政务不需要，因为没有组织职级)
		// String groupLevelIdStr = request.getParameter("groupLevelId");
		// Integer groupLevelId = V3xOrgEntity.MAX_LEVEL_NUM;
		// if(StringUtils.isNotBlank(groupLevelIdStr)){
		// groupLevelId =
		// orgManagerDirect.getLevelById(level.getGroupLevelId()).getLevelId();
		// }
		// boolean isMapRight =
		// orgManagerDirect.isGroupLevelMapRight(user.getLoginAccount(),
		// level.getLevelId(), groupLevelId);
		// if(!isMapRight){
		// V3xOrgLevel errorLevel =
		// orgManagerDirect.getErrorMapLevel(user.getLoginAccount(),
		// level.getLevelId(), groupLevelId);
		// if(errorLevel.getLevelId().intValue()>level.getLevelId().intValue()){
		// out.println("<script>");
		// out.println("alert('"+
		// Constant.getString("level.map.group.low",local,errorLevel.getName())+"');");
		// out.println("</script>");
		// }else{
		// out.println("<script>");
		// out.println("alert('"+
		// Constant.getString("level.map.group.up",local,errorLevel.getName())+"');");
		// out.println("</script>");
		// }
		// super.rendJavaScript(response, "parent.toEditMember()");
		// return
		// redirectModelAndView("/organization.do?method=organizationFrame&from=Level&addOrganiza=addLevel&cont="+continues);
		// }else{
		orgManager.addDutyLevel(level);
		super.rendJavaScript(response, "parent.doEndDutyLevel();");

		// 触发创建职务级别事件
		eventListener.addDutyLevel(level);

		// 记录日志
		appLogManager.insertLog(user, AppLogAction.Organization_NewDutyLevel,
				user.getName(), level.getName());
		return null;
		// }
	}

	/**
	 * 政务版——进入编辑职级方法
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView editDutyLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView result = new ModelAndView(
				"organization/dutyLevel/editDutyLevel");
		String id = request.getParameter("id");
		V3xOrgDutyLevel level = orgManagerDirect.getDutyLevelById(Long
				.parseLong(id));
		result.addObject("level", level);
		// //取得集团职务级别
		// List<V3xOrgLevel> groupLevellist =
		// orgManagerDirect.getAllLevels(orgManagerDirect.getRootAccount().getId(),false);
		// //过滤无效的级别
		// List<V3xOrgLevel> groupLevellistForPage = new
		// ArrayList<V3xOrgLevel>();
		// for(V3xOrgLevel levelForPage:groupLevellist){
		// if(levelForPage.getEnabled()){
		// groupLevellistForPage.add(levelForPage);
		// }
		// }
		// result.addObject("groupLevellist", groupLevellistForPage);
		// 取得是否是详细页面标志
		String isDetail = request.getParameter("isDetail");
		boolean readOnly = false;
		if (null != isDetail && isDetail.equals("readOnly")) {
			readOnly = true;
			result.addObject("readOnly", readOnly);
			result.addObject("preview", 0);
		} else {
			result.addObject("preview", 1);
		}

		// //是否显示集团职务级别映射
		// V3xOrgAccount account =
		// orgManagerDirect.getAccountById(CurrentUser.get().getLoginAccount());
		// if(Boolean.valueOf(SysFlag.org_showGroupLevelMap.getFlag().toString())
		// &&account.getSuperior().equals(V3xOrgEntity.DEFAULT_NULL_ID)
		// &&!account.getIsRoot()){
		// result.addObject("showGroupLevel", false);
		// }else{
		// result.addObject("showGroupLevel",
		// SysFlag.org_showGroupLevelMap.getFlag());
		// }

		return result;
	}

	/**
	 * 政务版——对职级进行编辑
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView updateDutyLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		V3xOrgDutyLevel model = new V3xOrgDutyLevel();
		bind(request, model);
		V3xOrgDutyLevel level = orgManagerDirect
				.getDutyLevelById(model.getId());
		// 为事件调用记录修改前的职务级别
		V3xOrgDutyLevel levelBeforeUpdate = new V3xOrgDutyLevel();
		BeanUtils.copyProperties(levelBeforeUpdate, level);

		PrintWriter out = response.getWriter();
		Locale local = LocaleContext.getLocale(request);
		// //判断集团职务级别映射
		// String groupLevelIdStr = request.getParameter("groupLevelId");
		// Integer groupLevelId = V3xOrgEntity.MAX_LEVEL_NUM;
		// if(StringUtils.isNotBlank(groupLevelIdStr)){
		// groupLevelId =
		// orgManagerDirect.getLevelById(model.getGroupLevelId()).getLevelId();
		// }
		// boolean isMapRight =
		// orgManagerDirect.isGroupLevelMapRight(CurrentUser.get().getLoginAccount(),
		// model.getLevelId(), groupLevelId);
		// if(!isMapRight){
		// /*V3xOrgLevel errorLevel =
		// orgManagerDirect.getErrorMapLevel(CurrentUser.get().getLoginAccount(),
		// model.getLevelId(), groupLevelId);
		// if(errorLevel.getLevelId().intValue()>level.getLevelId().intValue()){
		// out.println("<script>");
		// out.println("alert('"+
		// Constant.getString("level.map.group.low",local,errorLevel.getName())+"');");
		// out.println("</script>");
		// }else{
		// out.println("<script>");
		// out.println("alert('"+
		// Constant.getString("level.map.group.up",local,errorLevel.getName())+"');");
		// out.println("</script>");
		// }
		// super.rendJavaScript(response, "parent.toEditMember()");
		// return null;*/
		// V3xOrgLevel errorLevel =
		// orgManagerDirect.getErrorMapLevel(CurrentUser.get().getLoginAccount(),
		// model.getLevelId(), groupLevelId);
		// String str="";
		// if(errorLevel.getLevelId().intValue()>level.getLevelId().intValue()){
		// str="alert('"+
		// Constant.getString("level.map.group.low",local,errorLevel.getName())+"');";
		//
		// }else{
		// str="alert('"+
		// Constant.getString("level.map.group.up",local,errorLevel.getName())+"');";
		//
		// }
		// super.rendJavaScript(response, "parent.toEditMember();"+str);
		// return null;
		// }else{
		BeanUtils.copyProperties(level, model);
		orgManager.updateDutyLevel(level);
		// 触发更新职务级别事件
		eventListener.updateDutyLevel(levelBeforeUpdate, level);

		// 记录日志
		User user = CurrentUser.get();
		appLogManager.insertLog(user,
				AppLogAction.Organization_UpdateDutyLevel, user.getName(),
				level.getName());
		// 提示用户操作成功
		super.rendJavaScript(response, "parent.doEndDutyLevel()");
		return null;
		// }
	}

	/**
	 * 政务版_删除职级
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes = { RoleType.Administrator, RoleType.HrAdmin })
	public ModelAndView destroyDutyLevel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String ids = request.getParameter("id");
		Locale local = LocaleContext.getLocale(request);
		// 日志信息
		List<String[]> appLogs = new ArrayList<String[]>();
		User user = CurrentUser.get();
		if (null != ids && !ids.equals("")) {
			String[] arrID = ids.split(",");
			if (null != arrID && arrID.length > 0) {

				List<V3xOrgDutyLevel> levelList = new ArrayList<V3xOrgDutyLevel>();

				for (String strID : arrID) {
					Long id = Long.parseLong(strID);
					V3xOrgDutyLevel level = orgManagerDirect
							.getDutyLevelById(id);
					levelList.add(level);

					// 判断职级下是否有成员存在
					List<V3xOrgEntity> members = orgManagerDirect
							.getEntityList(V3xOrgMember.class.getSimpleName(),
									"orgDutyLevelId", id,
									level.getOrgAccountId());
					boolean isAllMemberUnEnabled = true;
					for (V3xOrgEntity mem : members) {
						if (((V3xOrgMember) mem).getEnabled()) {
							isAllMemberUnEnabled = false;
						}
					}
					if (ListUtils.EMPTY_LIST.equals(members)
							|| isAllMemberUnEnabled) {
						orgManager.deleteDutyLevel(level);
					} else {
						PrintWriter out = response.getWriter();
						out.println("<script>");
						out.println("alert('"
								+ Constant.getString(
										"organization.delete.member", local)
								+ "');");
						out.println("</script>");
						out.flush();
						return super
								.redirectModelAndView(
										"/organization.do?method=organizationFrame&from=DutyLevel",
										"parent");
					}
				}

				// 全部成功后触发删除职务级别事件
				for (V3xOrgDutyLevel level : levelList) {
					eventListener.deleteLevel(level);
					String[] appLog = new String[2];
					appLog[0] = user.getName();
					appLog[1] = level.getName();
					appLogs.add(appLog);
				}
				// 记录日志
				appLogManager.insertLogs(user,
						AppLogAction.Organization_DeleteDutyLevel, appLogs);
			}
		}
		// 提示用户操作成功
		PrintWriter out = response.getWriter();
		out.println("<script>");
		out.println("alert('" + Constant.getString("organization.yes", local)
				+ "');");
		out.println("</script>");
		return redirectModelAndView("/organization.do?method=listDutyLevel");
	}

	/**
	 * 停用某单位，查找该单位下启用的组织模型信息</br> 是否存在有效组织结构，部门，岗位，组，子部门，人员</br>
	 * 
	 * @param list
	 *            从内存中获取该单位下的组织模型列表
	 * @return true存在未删除有效实体false不存在有效
	 * @author lilong
	 * @date 2012-04-06
	 */
	private boolean checkValid4Disenable(List<V3xOrgEntity> list) {
		if (null == list || list.size() == 0) {
			return false;
		}
		for (V3xOrgEntity entity : list) {
			if (entity.getEnabled()) {
				log.info("停用单位失败，该单位下存在该实体未被删除或未被停用，该实体为:"
						+ entity.getEntityType() + entity.getName());
				return true;
			}
		}
		return false;
	}

	/**
	 * 设置secretAuditManager
	 * 
	 * @param secretAuditManager
	 *            secretAuditManager
	 */
	public void setSecretAuditManager(SecretAuditManager secretAuditManager) {
		this.secretAuditManager = secretAuditManager;
	}

	@SetContentType
	public ModelAndView loadTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String spid = request.getParameter("pid");
		StringBuilder sb = new StringBuilder();
		sb.append("<tree>");
		if (!Strings.isEmpty(spid)) {
			long pid = Long.parseLong(spid);
			List children;
			if (true) {
				children = orgManager.getChildAccount(pid);
			} else {
				// TODO 扩展实现部门树
				children = orgManager.getChildDepartments(pid, true);
			}
			Collections.sort(children, CompareSortEntity.getInstance());// AEIGHT-10050
			for (Object o : children) {
				V3xOrgEntity dept = (V3xOrgEntity) o;
				sb.append("<tree ")
						.append(" businessId=\"")
						// .append((dept.getId()+"").replace("-","_"))
						.append(dept.getId())
						.append("\"")
						.append(" text=\"")
						.append(dept.getName())
						.append("\"")
						.append(" src=\"organization.do?method=loadTree&amp;pid=")
						.append(dept.getId()).append("\"").append(" action=\"")
						.append("javascript:showAccountInfo('")
						.append(dept.getId()).append("');").append("\"")
						.append(" DBlClick=\"")
						.append("javascript:editAccountInfo('")
						.append(dept.getId()).append("');").append("\"")
						.append(" icon=\"")
						.append("common/images/left/icon/5104.gif")
						.append("\"").append(" openIcon=\"")
						.append("common/images/left/icon/5104.gif")
						.append("\"").append("/>");
			}
		}
		sb.append("</tree>");
		response.setContentType("text/xml;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		out.println(sb);
		out.close();
		return null;
	}
}