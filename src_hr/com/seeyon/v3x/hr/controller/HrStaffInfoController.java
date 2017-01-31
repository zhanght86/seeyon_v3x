package com.seeyon.v3x.hr.controller;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;

import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.agent.manager.AgentIntercalateManager;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.collaboration.templete.manager.TempleteConfigManager;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.Constants.LoginOfflineOperation;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.ldap.config.LDAPConfig;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataItem;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.online.OnlineRecorder;
import com.seeyon.v3x.common.operationlog.manager.OperationlogManager;
import com.seeyon.v3x.common.search.manager.SearchManager;
import com.seeyon.v3x.common.security.roleauthcheck.CheckRoleAccess;
import com.seeyon.v3x.common.security.roleauthcheck.RoleType;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.common.web.util.ExportHelper;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.hr.PagePropertyConstant;
import com.seeyon.v3x.hr.domain.Assess;
import com.seeyon.v3x.hr.domain.ContactInfo;
import com.seeyon.v3x.hr.domain.EduExperience;
import com.seeyon.v3x.hr.domain.Page;
import com.seeyon.v3x.hr.domain.PageLabel;
import com.seeyon.v3x.hr.domain.PageProperty;
import com.seeyon.v3x.hr.domain.PostChange;
import com.seeyon.v3x.hr.domain.PropertyLabel;
import com.seeyon.v3x.hr.domain.Relationship;
import com.seeyon.v3x.hr.domain.Repository;
import com.seeyon.v3x.hr.domain.RewardsAndPunishment;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.domain.WorkRecord;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.hr.manager.UserDefinedManager;
import com.seeyon.v3x.hr.util.HqlSearchHelper;
import com.seeyon.v3x.hr.util.SalaryUserDefinedHelper;
import com.seeyon.v3x.hr.webmodel.WebLabel;
import com.seeyon.v3x.hr.webmodel.WebProperty;
import com.seeyon.v3x.hr.webmodel.WebStaffInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.menu.domain.Security;
import com.seeyon.v3x.menu.manager.MenuManager;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.office.admin.manager.AdminManager;
import com.seeyon.v3x.organization.OrganizationHelper;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.CompareSortEntity;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.event.OrganizationEventComposite;
import com.seeyon.v3x.organization.event.OrganizationEventListener;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.principal.PrincipalManager;
import com.seeyon.v3x.organization.services.OrganizationServices;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgMember;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgModel;
import com.seeyon.v3x.plugin.ldap.manager.OrganizationLdapEvent;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.space.Constants.SpaceState;
import com.seeyon.v3x.space.Constants.SpaceTypeClass;
import com.seeyon.v3x.space.domain.SpaceModel;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

@CheckRoleAccess(roleTypes = RoleType.HrAdmin)
public class HrStaffInfoController extends BaseController  {
	private transient static final Log LOG = LogFactory
	.getLog(HrStaffInfoController.class);
	private MetadataManager metadataManager;
	private StaffInfoManager staffInfoManager;
	private OrgManager orgManager;
	private AttachmentManager attachmentManager;
	private UserDefinedManager userDefinedManager;
	private OrgManagerDirect orgManagerDirect;
	private SearchManager searchManager;
	private FileToExcelManager fileToExcelManager;
	private AffairManager affairManager;
	private InquiryManager inquiryManager;
	private BulDataManager bulDataManager;
	private NewsDataManager newsDataManager;
	private IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
	private AdminManager officeAdminInfoTarget;
	private MenuManager menuManager;
	private OperationlogManager operationlogManager;
	private ProjectManager projectManager;
	private AppLogManager appLogManager;
	private AgentIntercalateManager agentIntercalateManager;
	private OrganizationLdapEvent event=(OrganizationLdapEvent)ApplicationContextHolder.getBean("organizationLdapEvent");
	private OrganizationEventListener eventListener = OrganizationEventComposite.getInstance();
	private PrincipalManager principalManager;
	private SpaceManager spaceManager;
	private IndexManager indexManager;
	private UpdateIndexManager updateIndexManager;
	private OrganizationServices organizationServices;
	private TempleteConfigManager templeteConfigManager;
	
	public OrganizationServices getOrganizationServices() {
		return organizationServices;
	}
	public void setOrganizationServices(OrganizationServices organizationServices) {
		this.organizationServices = organizationServices;
	}
	public UpdateIndexManager getUpdateIndexManager() {
		return updateIndexManager;
	}
	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}
	public PrincipalManager getPrincipalManager() {
		return principalManager;
	}
	public void setPrincipalManager(PrincipalManager principalManager) {
		this.principalManager = principalManager;
	}
	public void setOperationlogManager(OperationlogManager operationlogManager) {
		this.operationlogManager = operationlogManager;
	}
	public MenuManager getMenuManager() {
		return menuManager;
	}
	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}
	public AdminManager getOfficeAdminInfoTarget() {
		return officeAdminInfoTarget;
	}
	public void setOfficeAdminInfoTarget(AdminManager officeAdminInfoTarget) {
		this.officeAdminInfoTarget = officeAdminInfoTarget;
	}
	public IOperBase getIOperBase() {
		return iOperBase;
	}
	public void setIOperBase(IOperBase operBase) {
		iOperBase = operBase;
	}
	public NewsDataManager getNewsDataManager() {
		return newsDataManager;
	}
	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}
	public BulDataManager getBulDataManager() {
		return bulDataManager;
	}
	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}
	public InquiryManager getInquiryManager() {
		return inquiryManager;
	}
	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}
	public AffairManager getAffairManager() {
		return affairManager;
	}
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
	public MetadataManager getMetadataManager()
    {
        return metadataManager;
    }

    public void setMetadataManager(MetadataManager metadataManager)
    {
        this.metadataManager = metadataManager;
    }
	public OrgManager getOrgManager() {
		return orgManager;
	}
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public StaffInfoManager getStaffInfoManager() {
		return staffInfoManager;
	}
	public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
		this.staffInfoManager = staffInfoManager;
	}
	public UserDefinedManager getUserDefinedManager() {
		return userDefinedManager;
	}

	public void setUserDefinedManager(UserDefinedManager userDefinedManager) {
		this.userDefinedManager = userDefinedManager;
	}
	public SearchManager getSearchManager() {
		return searchManager;
	}
	public void setSearchManager(SearchManager searchManager) {
		this.searchManager = searchManager;
	}
	public FileToExcelManager getFileToExcelManager() {
		return fileToExcelManager;
	}

	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}
	
	public IndexManager getIndexManager() {
		return indexManager;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
	
	public void setTempleteConfigManager(TempleteConfigManager templeteConfigManager) {
		this.templeteConfigManager = templeteConfigManager;
	}
	
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}
	
	
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
	
	public Long getCurrentUserAccount() {
		Long accountId = V3xOrgEntity.DEFAULT_NULL_ID;
		User user = CurrentUser.get();
		if (user == null)
			return accountId;
		if (user.getLoginAccount() == V3xOrgEntity.DEFAULT_NULL_ID ||user.getAccountId() == V3xOrgEntity.VIRTUAL_ACCOUNT_ID)
			return accountId;
		return user.getAccountId();
	}
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView initInfoHome(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/infohome");

		String staffid = request.getParameter("staffId");
		Long staffId = null;
		if(null!=staffid&&!"".equals(staffid)){
			staffId = Long.valueOf(staffid);
		}
		else{
			staffId = CurrentUser.get().getId();
		}
		mav.addObject("staffId", staffId);
		
		int infotype = RequestUtils.getIntParameter(request, "infoType", 1);
		List<WebProperty> webPages = new ArrayList<WebProperty>();
		List<Page> pages = this.userDefinedManager.getPageByModelName("staff");
		for(Page page : pages){
			WebProperty webPage = new WebProperty();
			List<PageLabel> labels = this.userDefinedManager.getPageLabelByPageId(page.getId());
			for(PageLabel label : labels){
				if(label.getLanguage().equals("zh_CN"))
					webPage.setPageName_zh(label.getPageLabelValue());
				else if(label.getLanguage().equals("en"))
					webPage.setPageName_en(label.getPageLabelValue());
			}
			webPage.setPage_id(page.getId());
			webPage.setPageNo(page.getPageNo());
			webPages.add(webPage);
		}
		
		String pageID = request.getParameter("page_id");
		if(null!=pageID && !"".equals(pageID)){
			infotype = 0;
			mav.addObject("page_id", Long.valueOf(pageID));
		}		
		String isNew = request.getParameter("isNew");
		mav.addObject("webPages", webPages);
		mav.addObject("infoType", infotype);
		mav.addObject("isManager", request.getParameter("isManager"));
		mav.addObject("isReadOnly", request.getParameter("isReadOnly"));
		mav.addObject("isNew", isNew);
		
		// TODO 结构不佳，必要时需重构 选人全在iframe外层封装，内部无法传入初始选择项
		// 取得人员的副岗
		if(null!=isNew && isNew.equals("New")){
    		mav.addObject("secondPostList", "");  
        }
		else{
	        V3xOrgMember member = orgManagerDirect.getMemberById(staffId);

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
    				V3xOrgDepartment v3xdept = orgManagerDirect.getDepartmentById(deptid);
    				sbuffer.append(v3xdept.getName());
    				sbuffer.append("-");
    				sbufferId.append(v3xdept.getId());
    				sbufferId.append("_");				
    				Long postid = memberPost.getPostId();
    				V3xOrgPost v3xpost = orgManagerDirect.getPostById(postid);
    				sbuffer.append(v3xpost.getName());
    				sbufferId.append(v3xpost.getId());
    				deptpostbuffer.append(sbuffer.toString());
    				deptpostbuffer.append(",");
    				deptpostbufferId.append(sbufferId.toString());
    				deptpostbufferId.append(",");
    				webModel.setSecondPostId(v3xdept.getId()+"_"+v3xpost.getId());
    				webModel.setSecondPostType("Department_Post");
    				secondPostList.add(webModel);
    			}
    		}	
    		mav.addObject("secondPostList", secondPostList);    
		}		
		//添加load标记，用来区分是否是点击新建，如果是跳转到infohome.jsp,否跳转到各详细jsp
		if("1".equals(request.getParameter("load"))){
			return initSpace(request,response);
		}else{
			return mav;
		}
	}
	
	/**
	 * 花名册--部门单位解析
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView initNameList(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		try {
			ModelAndView mav = new ModelAndView("hr/staffInfo/nameList");
			
			String isPage = request.getParameter("ispage");			
			List<WebStaffInfo> resultlist = new ArrayList<WebStaffInfo>();
			String staffIds = request.getParameter("staffIds");
			String[][] staffAreas = Strings.getSelectPeopleElements(staffIds);
			Long accountId = Long.valueOf(CurrentUser.get().getLoginAccount());

			if(staffAreas != null){
				List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
				boolean isAccount = false;
				for(int y = 0 ; y < staffAreas.length ; y ++ ){
					if(staffAreas[y][0].equals("Department")){
						List<V3xOrgMember> memberList1 = orgManager.getMembersByDepartment(Long.parseLong(staffAreas[y][1]),false);	
						for(V3xOrgMember mem:memberList1){
							//去掉外单位兼职人员
							if(!memberList.contains(mem)&&mem.getOrgAccountId().equals(accountId)){
								memberList.add(mem);
							}
						}						
						Collections.sort(memberList,CompareSortEntity.getInstance());
					}
					else if(staffAreas[y][0].equals("Account")){	
						isAccount = true;
					}					
				}
				
				if(isAccount){
					memberList.clear();
					memberList = orgManager.getAllMembers(accountId);	
					Collections.sort(memberList,CompareSortEntity.getInstance());
				}
				if(null!=isPage && isPage.equals("page")){
					memberList = this.pagenate(memberList);
				}
				List<Long> memberIdList = new ArrayList<Long>(memberList.size());
				for (V3xOrgMember member : memberList) {
					memberIdList.add(member.getId());
				}
				Map<Long, StaffInfo> staffs = staffInfoManager.getStaffInfos(memberIdList);
				int i = 1 + Pagination.getFirstResult();
				for (V3xOrgMember member : memberList) {														
					WebStaffInfo staffinfo = this.translateV3xOrgMember(member);
					 staffinfo.setPeople_type(member.getIsInternal());
					 
					    if(null!=member.getBirthday()){
					    	staffinfo.setAge(this.setAgeByBirthday(member.getBirthday()));
	                    }  
	                    
	                    StaffInfo staff= staffs.get(member.getId()); //staffInfoManager.getStaffInfoById(member.getId());
	                    if(null!=staff){
		                    staffinfo.setSex(member.getGender()+"");
		                    staffinfo.setNation(staff.getNation());
		                    staffinfo.setSpecialty(staff.getSpecialty());
		                    staffinfo.setID_card(staff.getID_card());
		                    staffinfo.setEdu_level(staff.getEdu_level());
		                    staffinfo.setPolitical_position(staff.getPolitical_position());
		                    staffinfo.setMarriage(staff.getMarriage());
		                    staffinfo.setWork_starting_date(staff.getWork_starting_date());
		                    staffinfo.setRecord_wage(staff.getRecord_wage()); 
		                    staffinfo.setBirthday(staff.getBirthday());
		                    
	                    }
	                    
	                    staffinfo.setNameList_number(i);
	                    i+=1;
						resultlist.add(staffinfo);
				}				
			}
			

			if(null!=isPage && isPage.equals("page")){
//				resultlist = this.pagenate(resultlist);
				mav.addObject("isPage", "true");
			}
			else{
				mav.addObject("isPage", "false");
			}

			mav.addObject("staffIds", staffIds);
			mav.addObject("memberlist", resultlist);
			String title = request.getParameter("title");
			mav.addObject("title", title);
			
			String items = request.getParameter("items");
			mav.addObject("items", items);
			// 获得单位类别下拉列表中的数据
			Map<String, Metadata> orgMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.organization);
			mav.addObject("orgMeta", orgMeta);
			return mav;
		} catch (Exception e) {
			LOG.error("", e);
		}
		return null;
	}
	
	/**
	 * 设置年龄
	 * @author lucx
	 *
	 */
	public int setAgeByBirthday(Date birthday) {
		int age = 1;
		if (null != birthday) {
			Calendar now = Calendar.getInstance();
			int year1 = now.get(Calendar.YEAR);
			Calendar bd = Calendar.getInstance();
			bd.setTime(birthday);
			int year2 = bd.get(Calendar.YEAR);
			age = year1 - year2;
		}
		if(age==0){
	       age=1;
		}
		return age;	
	}

	/**
	 * 导出数据（excel）
	 */
	@SuppressWarnings("unchecked")
	@CheckRoleAccess(roleTypes = RoleType.HrAdmin)
	public ModelAndView export(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String exportType = request.getParameter("exportType");
		if ("page".equals(exportType)) {
			return ExportHelper.excutePageMethod(this, request, response,
					"pageMethod");
		} else {
			// 不分页
			Pagination.withoutPagination(null);
			Pagination.setFirstResult(0);
			Pagination.setMaxResults(Integer.MAX_VALUE);
			ModelAndView mav = ExportHelper.excutePageMethod(this, request,
					response, "pageMethod");
			if (mav != null) {
				List<WebStaffInfo> webStaffInfoList = (List<WebStaffInfo>) mav
						.getModel().get("memberlist");
				if (webStaffInfoList != null && !webStaffInfoList.isEmpty()) {
					if ("excel".equals(exportType)) {
						// 参考exportNameListExcel()
						exportNameListAsExcel(request, response,
								webStaffInfoList);
					}
				}
			}
			return null;
		}
	}

	/**
	 * 参照exportNameListExcel()导出花名册Excel
	 */
	private void exportNameListAsExcel(HttpServletRequest request,
			HttpServletResponse response, List<WebStaffInfo> webStaffInfoList) {
		String title = request.getParameter("title");		
		String[] items = request.getParameter("items").split(",");
		DataRecord record = new DataRecord();
		record.setSheetName(title);
		record.setTitle(title);
		String resource = "com.seeyon.v3x.hr.resource.i18n.HRResources";
		String fileName = ResourceBundleUtil.getString(resource, "hr.nameList.label");
		String str = ResourceBundleUtil.getString(resource, "hr.nameList.number.label")+",";
		int i = 1;
		String sex_female = ResourceBundleUtil.getString(resource, "hr.staffInfo.female.label");
		String sex_male = ResourceBundleUtil.getString(resource, "hr.staffInfo.male.label"); 
		String juniorschool = ResourceBundleUtil.getString(resource, "hr.staffInfo.juniorschool.label");
		String seniorschool = ResourceBundleUtil.getString(resource, "hr.staffInfo.seniorschool.label");
		String juniorcollege = ResourceBundleUtil.getString(resource, "hr.staffInfo.juniorcollege.label");
		String university = ResourceBundleUtil.getString(resource, "hr.staffInfo.university.label");
		String postgraduate = ResourceBundleUtil.getString(resource, "hr.staffInfo.postgraduate.label");
		String doctor = ResourceBundleUtil.getString(resource, "hr.staffInfo.doctor.label");
		String edu_level_other = ResourceBundleUtil.getString(resource, "hr.staffInfo.other.label");
		String commie= ResourceBundleUtil.getString(resource, "hr.staffInfo.commie.label");
		String others = ResourceBundleUtil.getString(resource, "hr.staffInfo.others.label");
		String single= ResourceBundleUtil.getString(resource, "hr.staffInfo.single.label");
		String married = ResourceBundleUtil.getString(resource, "hr.staffInfo.married.label");
		// 获得单位类别下拉列表中的数据
		Map<String, Metadata> orgMeta = metadataManager
				.getMetadataMap(ApplicationCategoryEnum.organization);

		for (WebStaffInfo staffinfo : webStaffInfoList) {
			DataRow row = new DataRow();						
            row.addDataCell(String.valueOf(i), DataCell.DATA_TYPE_TEXT);
            
            //~~~~~~~~~~`
            for(String item : items){
                if("1".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.name.label")+",";
             	   row.addDataCell(staffinfo.getName(),DataCell.DATA_TYPE_TEXT);
                }
                else if("2".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.sex.label")+",";
             	   if("1".equals(staffinfo.getSex())){
             		   row.addDataCell(sex_male, DataCell.DATA_TYPE_TEXT);
             	   }
             	   else if("2".equals(staffinfo.getSex())){
             		   row.addDataCell(sex_female, DataCell.DATA_TYPE_TEXT);
             	   } 
             	   else{
             		   row.addDataCell("", DataCell.DATA_TYPE_TEXT);
             	   }            	   
                }
                else if("3".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.nation.label")+",";
             	   row.addDataCell(staffinfo.getNation(),DataCell.DATA_TYPE_TEXT);
                }
                else if("4".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.age.label")+",";
             	   if(0!=staffinfo.getAge()){
             		   row.addDataCell(String.valueOf(staffinfo.getAge()),DataCell.DATA_TYPE_TEXT);
             	   }
             	   else{
             		   row.addDataCell("",DataCell.DATA_TYPE_TEXT);
             	   }
             	    
                }
                else if("5".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.specialty.label")+",";
             	   row.addDataCell(staffinfo.getSpecialty(),DataCell.DATA_TYPE_TEXT); 
                }
                else if("6".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.IDcard.label")+",";
             	   row.addDataCell(staffinfo.getID_card(),DataCell.DATA_TYPE_TEXT);
                }
                else if("7".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.edulevel.label")+",";
             	   int edu = staffinfo.getEdu_level();
             	   if(edu==1){
             		   row.addDataCell(juniorschool ,DataCell.DATA_TYPE_TEXT);
             	   }
             	   else if(edu==2){
             		   row.addDataCell(seniorschool ,DataCell.DATA_TYPE_TEXT);
             	   }
             	   else if(edu==3){
             		   row.addDataCell(juniorcollege ,DataCell.DATA_TYPE_TEXT);
             	   }
             	   else if(edu==4){
             		   row.addDataCell(university ,DataCell.DATA_TYPE_TEXT);
             	   }
             	   else if(edu==5){
             		   row.addDataCell(postgraduate ,DataCell.DATA_TYPE_TEXT);
             	   }
             	   else if(edu==6){
             		   row.addDataCell(doctor ,DataCell.DATA_TYPE_TEXT);
             	   }
             	   else if(edu==7){
             		   row.addDataCell(edu_level_other ,DataCell.DATA_TYPE_TEXT);
             	   }  
             	   else{
             		   row.addDataCell("" ,DataCell.DATA_TYPE_TEXT);
             	   }
                }
                else if("8".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.position.label")+",";           	   
             	   if(1==staffinfo.getPolitical_position()){
             		   row.addDataCell(commie, DataCell.DATA_TYPE_TEXT); 
             	   }
             	   else if(2==staffinfo.getPolitical_position()){
             		   row.addDataCell(others, DataCell.DATA_TYPE_TEXT);           		   
             	   }
             	   else{
             		   row.addDataCell("" ,DataCell.DATA_TYPE_TEXT);
             	   }
                }
                else if("9".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.marriage.label")+",";
             	   
             	   if(1==staffinfo.getMarriage()){
             		   row.addDataCell(single, DataCell.DATA_TYPE_TEXT);
             	   }
             	   else if(2==staffinfo.getMarriage()){
             		   row.addDataCell(married , DataCell.DATA_TYPE_TEXT);
             	   }
             	   else{
             		   row.addDataCell("" ,DataCell.DATA_TYPE_TEXT);
             	   }
             	   
                }
                else if("10".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.workStartingDate.label")+",";    	   
             	   if(null!=staffinfo.getWork_starting_date()){ 
             	       row.addDataCell(Datetimes.formatDate(staffinfo.getWork_starting_date()).toString(), DataCell.DATA_TYPE_TEXT);     
             	   }
             	   else {
             		   row.addDataCell("", DataCell.DATA_TYPE_TEXT); 
             	   }
                }
                else if("11".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.recordWage.label")+","; 
             	   if(null==staffinfo.getRecord_wage()){
             		   row.addDataCell("", DataCell.DATA_TYPE_TEXT); 
             	   }
             	   else{
             		   row.addDataCell(String.valueOf(staffinfo.getRecord_wage()), DataCell.DATA_TYPE_TEXT); 
             	   }	   
                }
                else if("12".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.memberno.label")+",";
             	   row.addDataCell(staffinfo.getCode(), DataCell.DATA_TYPE_TEXT);
                }
                else if("13".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.peopleType.label")+",";
             	   String isInternal = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "people.isInternal");
             	   if(false==staffinfo.getPeople_type()){
             		   isInternal = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "people.out");
             	   }
             	   row.addDataCell(isInternal, DataCell.DATA_TYPE_TEXT);
                }
                else if("14".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.staffstate.label")+",";
             	   String state = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.metadata.member_state.in");
             	   if(2==staffinfo.getState()){
             		   state = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.metadata.member_state.out");
             	   }
             	   row.addDataCell(state, DataCell.DATA_TYPE_TEXT);
                }
                else if("15".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.stafftype.label")+",";
             	   String type = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.metadata.member_type.nomal");
//             	   if(2==staffinfo.getType()){
//             		   type = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.metadata.member_type.unnomal");
//             	   }
             	   Metadata memberStateMeta = orgMeta.get("org_property_member_type");
             	   List<MetadataItem> memberStateMetaItems = memberStateMeta.getItems();
             	   for (MetadataItem metadataItem : memberStateMetaItems) {
             		   if(Integer.parseInt(metadataItem.getValue()) == staffinfo.getType()){
             			  type = ResourceBundleUtil.getString(memberStateMeta.getResourceBundle(), metadataItem.getLabel());
             		   }
             	   }
             	   row.addDataCell(type, DataCell.DATA_TYPE_TEXT);
                }
                else if("16".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.department.label")+",";
             	   row.addDataCell(staffinfo.getDepartment_name(), DataCell.DATA_TYPE_TEXT);
                }     
                else if("17".equals(item)){
                   //branches_a8_v350_r_gov GOV-1931 杨帆 修改花名册导出excel中，职务级别为职务 start
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.postlevel.label"+(Functions.suffix()))+",";
             	   //branches_a8_v350_r_gov GOV-1931 杨帆 修改花名册导出excel中，职务级别为职务 end
             	   row.addDataCell(staffinfo.getLevel_name(), DataCell.DATA_TYPE_TEXT);
                }
                else if("18".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.primaryPostId.label")+",";
             	   row.addDataCell(staffinfo.getPost_name(), DataCell.DATA_TYPE_TEXT);
                }
                else if("19".equals(item)){
             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.secondPostId.label")+",";
             	   row.addDataCell(staffinfo.getSecond_posts(), DataCell.DATA_TYPE_TEXT);
                }else if ("20".equals(item)) {
                	str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.birthday.label")+",";
                	row.addDataCell(staffinfo.getBirthdayStr(), DataCell.DATA_TYPE_DATE);
				}
 			}
            //~~~~~~~~~~
            record.addDataRow(row);
            if(i==1){
              String[] columnNames = str.split(",");
              record.setColumnName(columnNames);
            }
            i+=1;
		}
		try {
			fileToExcelManager.save(request,response, fileName, "location.href",record);
		} catch (Exception e) {
			LOG.error("", e);
		}
	}

	/**
	 * 花名册导出excel
	 * @author lucx
	 * @deprecated 请用export()
	 */
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView exportNameListExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
//		String[] arrIDs = request.getParameter("staffIds").split(",");
		String staffIds = request.getParameter("staffIds");
		String[][] staffAreas = Strings.getSelectPeopleElements(staffIds);//页面传来的部门或者单位ID Department|123456,Account|123456
		String title = request.getParameter("title");		
		String[] items = request.getParameter("items").split(",");
		DataRecord record = new DataRecord();
		record.setSheetName(title);
		record.setTitle(title);
		String resource = "com.seeyon.v3x.hr.resource.i18n.HRResources";
		String str = ResourceBundleUtil.getString(resource, "hr.nameList.number.label")+",";
		int i = 1;
		String sex_female = ResourceBundleUtil.getString(resource, "hr.staffInfo.female.label");
		String sex_male = ResourceBundleUtil.getString(resource, "hr.staffInfo.male.label"); 
		String juniorschool = ResourceBundleUtil.getString(resource, "hr.staffInfo.juniorschool.label");
		String seniorschool = ResourceBundleUtil.getString(resource, "hr.staffInfo.seniorschool.label");
		String juniorcollege = ResourceBundleUtil.getString(resource, "hr.staffInfo.juniorcollege.label");
		String university = ResourceBundleUtil.getString(resource, "hr.staffInfo.university.label");
		String postgraduate = ResourceBundleUtil.getString(resource, "hr.staffInfo.postgraduate.label");
		String doctor = ResourceBundleUtil.getString(resource, "hr.staffInfo.doctor.label");
		String edu_level_other = ResourceBundleUtil.getString(resource, "hr.staffInfo.other.label");
		String commie= ResourceBundleUtil.getString(resource, "hr.staffInfo.commie.label");
		String others = ResourceBundleUtil.getString(resource, "hr.staffInfo.others.label");
		String single= ResourceBundleUtil.getString(resource, "hr.staffInfo.single.label");
		String married = ResourceBundleUtil.getString(resource, "hr.staffInfo.married.label");
		
		//加入此列表是为了判断人员列表中是否有重复项
		List<V3xOrgMember> mems  = new ArrayList<V3xOrgMember>();
		
		if (null != staffAreas && staffAreas.length > 0) {
			for(int y = 0 ; y < staffAreas.length ; y ++ ){			
			if(staffAreas[y][0].equals("Department")){
				List<V3xOrgMember> memberList  = new ArrayList<V3xOrgMember>();
				Set<V3xOrgMember> memberList2 = new HashSet<V3xOrgMember>();	
				List<V3xOrgMember> memberList1 = orgManager.getMembersByDepartment(Long.parseLong(staffAreas[y][1]),false);
				memberList2.addAll(memberList1);
				
				for ( V3xOrgMember meb : memberList2) {
					if(!mems.contains(meb)){
						mems.add(meb);
						memberList.add(meb);
					}					
				}
				List<Long> memberIdList = new ArrayList<Long>(memberList.size());
				for (V3xOrgMember member : memberList) {
					memberIdList.add(member.getId());
				}				
				Map<Long, StaffInfo> staffs = staffInfoManager.getStaffInfos(memberIdList);
				for ( V3xOrgMember member : memberList) {
					DataRow row = new DataRow();						
					WebStaffInfo staffinfo = this.translateV3xOrgMember(member);
					if(null!=member.getBirthday()){
						staffinfo.setAge(this.setAgeByBirthday(member.getBirthday()));
					}
					
		            staffinfo.setPeople_type(member.getIsInternal());		            
		            StaffInfo staff= staffs.get(member.getId());//staffInfoManager.getStaffInfoById(member.getId());
		            if(null!=staff){
		 	           staffinfo.setSex(member.getGender()+"");
		 	           staffinfo.setNation(staff.getNation());
		 	           //年龄从组织模型的日期来判断
		 	          /* if(null!=staff.getAge()&&!"".equals(staff.getAge())){
		 	        	   staffinfo.setAge(Integer.valueOf(staff.getAge()));  
		 	           }*/
		 	           staffinfo.setSpecialty(staff.getSpecialty());
		 	           staffinfo.setID_card(staff.getID_card());
		 	           staffinfo.setEdu_level(staff.getEdu_level());
		 	           staffinfo.setPolitical_position(staff.getPolitical_position());
		 	           staffinfo.setMarriage(staff.getMarriage());
		 	           if(null!=staff.getWork_starting_date()){
		 	        	   staffinfo.setWork_starting_date(staff.getWork_starting_date());
		 	           } 
		 	           if(null!=staff.getRecord_wage()){
		 	        	   staffinfo.setRecord_wage(staff.getRecord_wage()); 
		 	           }	           
		             }
		            staffinfo.setNameList_number(i);            
		            row.addDataCell(String.valueOf(i), DataCell.DATA_TYPE_TEXT);
		            
		            //~~~~~~~~~~`
		            for(String item : items){
		                if("1".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.name.label")+",";
		             	   row.addDataCell(staffinfo.getName(),DataCell.DATA_TYPE_TEXT);
		                }
		                else if("2".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.sex.label")+",";
		             	   if("1".equals(staffinfo.getSex())){
		             		   row.addDataCell(sex_male, DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if("2".equals(staffinfo.getSex())){
		             		   row.addDataCell(sex_female, DataCell.DATA_TYPE_TEXT);
		             	   } 
		             	   else{
		             		   row.addDataCell("", DataCell.DATA_TYPE_TEXT);
		             	   }            	   
		                }
		                else if("3".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.nation.label")+",";
		             	   row.addDataCell(staffinfo.getNation(),DataCell.DATA_TYPE_TEXT);
		                }
		                else if("4".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.age.label")+",";
		             	   if(0!=staffinfo.getAge()){
		             		   row.addDataCell(String.valueOf(staffinfo.getAge()),DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else{
		             		   row.addDataCell("",DataCell.DATA_TYPE_TEXT);
		             	   }
		             	    
		                }
		                else if("5".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.specialty.label")+",";
		             	   row.addDataCell(staffinfo.getSpecialty(),DataCell.DATA_TYPE_TEXT); 
		                }
		                else if("6".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.IDcard.label")+",";
		             	   row.addDataCell(staffinfo.getID_card(),DataCell.DATA_TYPE_TEXT);
		                }
		                else if("7".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.edulevel.label")+",";
		             	   int edu = staffinfo.getEdu_level();
		             	   if(edu==1){
		             		   row.addDataCell(juniorschool ,DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if(edu==2){
		             		   row.addDataCell(seniorschool ,DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if(edu==3){
		             		   row.addDataCell(juniorcollege ,DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if(edu==4){
		             		   row.addDataCell(university ,DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if(edu==5){
		             		   row.addDataCell(postgraduate ,DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if(edu==6){
		             		   row.addDataCell(doctor ,DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if(edu==7){
		             		   row.addDataCell(edu_level_other ,DataCell.DATA_TYPE_TEXT);
		             	   }  
		             	   else{
		             		   row.addDataCell("" ,DataCell.DATA_TYPE_TEXT);
		             	   }
		                }
		                else if("8".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.position.label")+",";           	   
		             	   if(1==staffinfo.getPolitical_position()){
		             		   row.addDataCell(commie, DataCell.DATA_TYPE_TEXT); 
		             	   }
		             	   else if(2==staffinfo.getPolitical_position()){
		             		   row.addDataCell(others, DataCell.DATA_TYPE_TEXT);           		   
		             	   }
		             	   else{
		             		   row.addDataCell("" ,DataCell.DATA_TYPE_TEXT);
		             	   }
		                }
		                else if("9".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.marriage.label")+",";
		             	   
		             	   if(1==staffinfo.getMarriage()){
		             		   row.addDataCell(single, DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if(2==staffinfo.getMarriage()){
		             		   row.addDataCell(married , DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else{
		             		   row.addDataCell("" ,DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   
		                }
		                else if("10".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.workStartingDate.label")+",";    	   
		             	   if(null!=staffinfo.getWork_starting_date()){ 
		             	       row.addDataCell(Datetimes.formatDate(staffinfo.getWork_starting_date()).toString(), DataCell.DATA_TYPE_TEXT);     
		             	   }
		             	   else {
		             		   row.addDataCell("", DataCell.DATA_TYPE_TEXT); 
		             	   }
		                }
		                else if("11".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.recordWage.label")+","; 
		             	   if(null==staffinfo.getRecord_wage()){
		             		   row.addDataCell("", DataCell.DATA_TYPE_TEXT); 
		             	   }
		             	   else{
		             		   row.addDataCell(String.valueOf(staffinfo.getRecord_wage()), DataCell.DATA_TYPE_TEXT); 
		             	   }	   
		                }
		                else if("12".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.memberno.label")+",";
		             	   row.addDataCell(staffinfo.getCode(), DataCell.DATA_TYPE_TEXT);
		                }
		                else if("13".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.peopleType.label")+",";
		             	   String isInternal = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "people.isInternal");
		             	   if(false==staffinfo.getPeople_type()){
		             		   isInternal = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "people.out");
		             	   }
		             	   row.addDataCell(isInternal, DataCell.DATA_TYPE_TEXT);
		                }
		                else if("14".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.staffstate.label")+",";
		             	   String state = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.metadata.member_state.in");
		             	   if(2==staffinfo.getState()){
		             		   state = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.metadata.member_state.out");
		             	   }
		             	   row.addDataCell(state, DataCell.DATA_TYPE_TEXT);
		                }
		                else if("15".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.stafftype.label")+",";
		             	   String type = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.metadata.member_type.nomal");
		             	   if(2==staffinfo.getType()){
		             		   type = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.metadata.member_type.unnomal");
		             	   }
		             	   row.addDataCell(type, DataCell.DATA_TYPE_TEXT);
		                }
		                else if("16".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.department.label")+",";
		             	   row.addDataCell(staffinfo.getDepartment_name(), DataCell.DATA_TYPE_TEXT);
		                }     
		                else if("17".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.postlevel.label")+",";
		             	   row.addDataCell(staffinfo.getLevel_name(), DataCell.DATA_TYPE_TEXT);
		                }
		                else if("18".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.primaryPostId.label")+",";
		             	   row.addDataCell(staffinfo.getPost_name(), DataCell.DATA_TYPE_TEXT);
		                }
		                else if("19".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.secondPostId.label")+",";
		             	   row.addDataCell(staffinfo.getSecond_posts(), DataCell.DATA_TYPE_TEXT);
		                }
		 			}
		            //~~~~~~~~~~
		            record.addDataRow(row);
		            if(i==1){
		              String[] columnNames = str.split(",");
		              record.setColumnName(columnNames);
		            }
		            i+=1;
				}
								            
			}else if(staffAreas[y][0].equals("Account")){
				List<V3xOrgMember> memberList  = new ArrayList<V3xOrgMember>();
				Set<V3xOrgMember> memberList2 = new HashSet<V3xOrgMember>();	
				List<V3xOrgMember> memberList1 = orgManagerDirect.getAllMembers(Long.parseLong(staffAreas[y][1]),false );
				memberList2.addAll(memberList1);
				
				for ( V3xOrgMember mem : memberList2) {
					if(!mems.contains(mem)){
						mems.add(mem);
						memberList.add(mem);
					}					
				}
				List<Long> memberIdList = new ArrayList<Long>(memberList.size());
				for (V3xOrgMember member : memberList) {
					memberIdList.add(member.getId());
				}				
				Map<Long, StaffInfo> staffs = staffInfoManager.getStaffInfos(memberIdList);				
				for ( V3xOrgMember member : memberList) {
					DataRow row = new DataRow();						
					WebStaffInfo staffinfo = this.translateV3xOrgMember(member);
					//年龄从组织模型取数据
		            staffinfo.setPeople_type(member.getIsInternal());
		            if(null!=member.getBirthday()){
						staffinfo.setAge(this.setAgeByBirthday(member.getBirthday()));
					}
		            StaffInfo staff=staffs.get(member.getId());//staffInfoManager.getStaffInfoById(member.getId());
		            if(null!=staff){
		 	           staffinfo.setSex(member.getGender()+"");
		 	           staffinfo.setNation(staff.getNation());
		 	           /*if(null!=staff.getAge()&&!"".equals(staff.getAge())){
		 	        	   staffinfo.setAge(Integer.valueOf(staff.getAge()));  
		 	           }*/
		 	           staffinfo.setSpecialty(staff.getSpecialty());
		 	           staffinfo.setID_card(staff.getID_card());
		 	           staffinfo.setEdu_level(staff.getEdu_level());
		 	           staffinfo.setPolitical_position(staff.getPolitical_position());
		 	           staffinfo.setMarriage(staff.getMarriage());
		 	           if(null!=staff.getWork_starting_date()){
		 	        	   staffinfo.setWork_starting_date(staff.getWork_starting_date());
		 	           } 
		 	           if(null!=staff.getRecord_wage()){
		 	        	   staffinfo.setRecord_wage(staff.getRecord_wage()); 
		 	           }	           
		             }
		            staffinfo.setNameList_number(i);            
		            row.addDataCell(String.valueOf(i), DataCell.DATA_TYPE_TEXT);
		            
		            //~~~~~~~~~~`
		            for(String item : items){
		                if("1".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.name.label")+",";
		             	   row.addDataCell(staffinfo.getName(),DataCell.DATA_TYPE_TEXT);
		                }
		                else if("2".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.sex.label")+",";
		             	   if("1".equals(staffinfo.getSex())){
		             		   row.addDataCell(sex_male, DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if("2".equals(staffinfo.getSex())){
		             		   row.addDataCell(sex_female, DataCell.DATA_TYPE_TEXT);
		             	   } 
		             	   else{
		             		   row.addDataCell("", DataCell.DATA_TYPE_TEXT);
		             	   }            	   
		                }
		                else if("3".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.nation.label")+",";
		             	   row.addDataCell(staffinfo.getNation(),DataCell.DATA_TYPE_TEXT);
		                }
		                else if("4".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.age.label")+",";
		             	   if(0!=staffinfo.getAge()){
		             		   row.addDataCell(String.valueOf(staffinfo.getAge()),DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else{
		             		   row.addDataCell("",DataCell.DATA_TYPE_TEXT);
		             	   }
		             	    
		                }
		                else if("5".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.specialty.label")+",";
		             	   row.addDataCell(staffinfo.getSpecialty(),DataCell.DATA_TYPE_TEXT); 
		                }
		                else if("6".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.IDcard.label")+",";
		             	   row.addDataCell(staffinfo.getID_card(),DataCell.DATA_TYPE_TEXT);
		                }
		                else if("7".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.edulevel.label")+",";
		             	   int edu = staffinfo.getEdu_level();
		             	   if(edu==1){
		             		   row.addDataCell(juniorschool ,DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if(edu==2){
		             		   row.addDataCell(seniorschool ,DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if(edu==3){
		             		   row.addDataCell(juniorcollege ,DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if(edu==4){
		             		   row.addDataCell(university ,DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if(edu==5){
		             		   row.addDataCell(postgraduate ,DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if(edu==6){
		             		   row.addDataCell(doctor ,DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if(edu==7){
		             		   row.addDataCell(edu_level_other ,DataCell.DATA_TYPE_TEXT);
		             	   }  
		             	   else{
		             		   row.addDataCell("" ,DataCell.DATA_TYPE_TEXT);
		             	   }
		                }
		                else if("8".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.position.label")+",";           	   
		             	   if(1==staffinfo.getPolitical_position()){
		             		   row.addDataCell(commie, DataCell.DATA_TYPE_TEXT); 
		             	   }
		             	   else if(2==staffinfo.getPolitical_position()){
		             		   row.addDataCell(others, DataCell.DATA_TYPE_TEXT);           		   
		             	   }
		             	   else{
		             		   row.addDataCell("" ,DataCell.DATA_TYPE_TEXT);
		             	   }
		                }
		                else if("9".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.marriage.label")+",";
		             	   
		             	   if(1==staffinfo.getMarriage()){
		             		   row.addDataCell(single, DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else if(2==staffinfo.getMarriage()){
		             		   row.addDataCell(married , DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   else{
		             		   row.addDataCell("" ,DataCell.DATA_TYPE_TEXT);
		             	   }
		             	   
		                }
		                else if("10".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.workStartingDate.label")+",";    	   
		             	   if(null!=staffinfo.getWork_starting_date()){ 
		             	       row.addDataCell(Datetimes.formatDate(staffinfo.getWork_starting_date()).toString(), DataCell.DATA_TYPE_TEXT);     
		             	   }
		             	   else {
		             		   row.addDataCell("", DataCell.DATA_TYPE_TEXT); 
		             	   }
		                }
		                else if("11".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.recordWage.label")+","; 
		             	   if(null==staffinfo.getRecord_wage()){
		             		   row.addDataCell("", DataCell.DATA_TYPE_TEXT); 
		             	   }
		             	   else{
		             		   row.addDataCell(String.valueOf(staffinfo.getRecord_wage()), DataCell.DATA_TYPE_TEXT); 
		             	   }	   
		                }
		                else if("12".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.memberno.label")+",";
		             	   row.addDataCell(staffinfo.getCode(), DataCell.DATA_TYPE_TEXT);
		                }
		                else if("13".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.peopleType.label")+",";
		             	   String isInternal = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "people.isInternal");
		             	   if(false==staffinfo.getPeople_type()){
		             		   isInternal = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "people.out");
		             	   }
		             	   row.addDataCell(isInternal, DataCell.DATA_TYPE_TEXT);
		                }
		                else if("14".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.staffstate.label")+",";
		             	   String state = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.metadata.member_state.in");
		             	   if(2==staffinfo.getState()){
		             		   state = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.metadata.member_state.out");
		             	   }
		             	   row.addDataCell(state, DataCell.DATA_TYPE_TEXT);
		                }
		                else if("15".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.stafftype.label")+",";
		             	   String type = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.metadata.member_type.nomal");
		             	   if(2==staffinfo.getType()){
		             		   type = ResourceBundleUtil.getString("com.seeyon.v3x.organization.resources.i18n.OrganizationResources", "org.metadata.member_type.unnomal");
		             	   }
		             	   row.addDataCell(type, DataCell.DATA_TYPE_TEXT);
		                }
		                else if("16".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.department.label")+",";
		             	   row.addDataCell(staffinfo.getDepartment_name(), DataCell.DATA_TYPE_TEXT);
		                }     
		                else if("17".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.postlevel.label")+",";
		             	   row.addDataCell(staffinfo.getLevel_name(), DataCell.DATA_TYPE_TEXT);
		                }
		                else if("18".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.primaryPostId.label")+",";
		             	   row.addDataCell(staffinfo.getPost_name(), DataCell.DATA_TYPE_TEXT);
		                }
		                else if("19".equals(item)){
		             	   str = str+ResourceBundleUtil.getString(resource, "hr.staffInfo.secondPostId.label")+",";
		             	   row.addDataCell(staffinfo.getSecond_posts(), DataCell.DATA_TYPE_TEXT);
		                }
		 			}
		            //~~~~~~~~~~
		            record.addDataRow(row);
		            if(i==1){
		              String[] columnNames = str.split(",");
		              record.setColumnName(columnNames);
		            }
		            i+=1;
				}
			}
			
		  }	
			try {
				fileToExcelManager.save(request,response, "staffInfo", "location.href",record);
			} catch (Exception e) {
				LOG.error("", e);
			}
		}		
	    return null;
	}
	
	/**
	 * 导出人员
	 * @author lucx
	 *
	 */
	public ModelAndView exportMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		String resource1 = "com.seeyon.v3x.hr.resource.i18n.HRResources";
		List<V3xOrgMember> memberlist = orgManagerDirect.getAllMembers(user.getLoginAccount());
		V3xOrgAccount account = null;
		Map<String, Metadata> orgMeta = metadataManager.getMetadataMap(ApplicationCategoryEnum.organization);
		StaffInfo staffer = null;
		MetadataItem memberTypeItem = null;
		MetadataItem memberStateItem = null;
		String memberType = "";
		String memberState = "";
		V3xOrgMember member = null;
		V3xOrgDepartment dept = null;
		String deptName = "";
		V3xOrgPost post = null;
		V3xOrgPost secondPost = null;
		String postName = "";
		String secondPostName = "";
		V3xOrgLevel level = null;
		String levelName = "";
		V3xOrgRole role = null;
		String roleNames = "";
		List<V3xOrgEntity> roleList = null;
		String primaryLanguange = "";
//		导出excel文件的国际化
		String state_Enabled = ResourceBundleUtil.getString(resource, "org.account_form.enable.use");
		String state_Disabled = ResourceBundleUtil.getString(resource, "org.account_form.enable.unuse");
		String member_primaryLanguange_zh_CN = ResourceBundleUtil.getString(resource, "org.member_form.primaryLanguange.zh_CN");
		String member_primaryLanguange_zh = ResourceBundleUtil.getString(resource, "org.member_form.primaryLanguange.zh");
		String member_primaryLanguange_en = ResourceBundleUtil.getString(resource, "org.member_form.primaryLanguange.en");
		String member_type_inner = ResourceBundleUtil.getString(resource, "org.member_form.type.inner");
		String member_type_out = ResourceBundleUtil.getString(resource, "org.member_form.type.out");
		String member_name = ResourceBundleUtil.getString(resource, "org.member_form.name.label");
		String member_loginName = ResourceBundleUtil.getString(resource, "org.member_form.loginName.label");
		String member_password = ResourceBundleUtil.getString(resource, "org.member_form.password.label");
		String member_primaryLanguange = ResourceBundleUtil.getString(resource, "org.member_form.primaryLanguange");
		String member_kind = ResourceBundleUtil.getString(resource, "org.member_form.kind");
		String member_state = ResourceBundleUtil.getString(resource, "org.state.lable");
		String member_code = ResourceBundleUtil.getString(resource, "org.member_form.code");
		String member_sortId = ResourceBundleUtil.getString(resource, "org.member_form.sort");
		String member_deptName = ResourceBundleUtil.getString(resource, "org.member_form.deptName.label");
		String member_primaryPost = ResourceBundleUtil.getString(resource, "org.member_form.primaryPost.label");
		String member_secondPost = ResourceBundleUtil.getString(resource, "org.member_form.secondPost.label");
		String member_levelName = ResourceBundleUtil.getString(resource, "org.member_form.levelName.label");
		String member_type = ResourceBundleUtil.getString(resource, "org.member_form.type");
		String member_memberState = ResourceBundleUtil.getString(resource, "org.member_form.member.state");
		String member_roles = ResourceBundleUtil.getString(resource, "org.member_form.roles");
		String member_tel = ResourceBundleUtil.getString(resource, "org.member_form.tel");
		String member_account = ResourceBundleUtil.getString(resource, "org.member_form.account");
		String member_description = ResourceBundleUtil.getString(resource, "org.member_form.description");
		String member_list = ResourceBundleUtil.getString(resource, "org.member_form.list");
		
		String staff_sex = ResourceBundleUtil.getString(resource1, "hr.staffInfo.sex.label");
		String staff_nation = ResourceBundleUtil.getString(resource1, "hr.staffInfo.nation.label");
		String staff_birthplace = ResourceBundleUtil.getString(resource1, "hr.staffInfo.birthplace.label");
		String staff_age = ResourceBundleUtil.getString(resource1, "hr.staffInfo.age.label");
		String staff_usedName = ResourceBundleUtil.getString(resource1, "hr.staffInfo.usedname.label");
		String staff_brithday = ResourceBundleUtil.getString(resource1, "hr.staffInfo.birthday.label");
		String staff_IDCard = ResourceBundleUtil.getString(resource1, "hr.staffInfo.IDcard.label");
		String staff_specialty = ResourceBundleUtil.getString(resource1, "hr.staffInfo.specialty.label");
		String staff_workingtime = ResourceBundleUtil.getString(resource1, "hr.staffInfo.workingtime.label");
		String staff_workStartingDate = ResourceBundleUtil.getString(resource1, "hr.staffInfo.workStartingDate.label");
		String staff_recordWage = ResourceBundleUtil.getString(resource1, "hr.staffInfo.recordWage.label");
		String staff_edulevel = ResourceBundleUtil.getString(resource1, "hr.staffInfo.edulevel.label");
		String staff_position = ResourceBundleUtil.getString(resource1, "hr.staffInfo.position.label");
		String staff_marriage = ResourceBundleUtil.getString(resource1, "hr.staffInfo.marriage.label");
		String staff_hobby = ResourceBundleUtil.getString(resource1, "hr.staffInfo.hobby.label");
		String staff_remark = ResourceBundleUtil.getString(resource1, "hr.staffInfo.remark.label");
		
		String sex_female = ResourceBundleUtil.getString(resource1, "hr.staffInfo.female.label");
		String sex_male = ResourceBundleUtil.getString(resource1, "hr.staffInfo.male.label"); 
		String juniorschool = ResourceBundleUtil.getString(resource1, "hr.staffInfo.juniorschool.label");
		String seniorschool = ResourceBundleUtil.getString(resource1, "hr.staffInfo.seniorschool.label");
		String juniorcollege = ResourceBundleUtil.getString(resource1, "hr.staffInfo.juniorcollege.label");
		String university = ResourceBundleUtil.getString(resource1, "hr.staffInfo.university.label");
		String postgraduate = ResourceBundleUtil.getString(resource1, "hr.staffInfo.postgraduate.label");
		String doctor = ResourceBundleUtil.getString(resource1, "hr.staffInfo.doctor.label");
		String edu_level_other = ResourceBundleUtil.getString(resource1, "hr.staffInfo.other.label");
		String commie= ResourceBundleUtil.getString(resource1, "hr.staffInfo.commie.label");
		String others = ResourceBundleUtil.getString(resource1, "hr.staffInfo.others.label");
		String single= ResourceBundleUtil.getString(resource1, "hr.staffInfo.single.label");
		String married = ResourceBundleUtil.getString(resource1, "hr.staffInfo.married.label");
		
		if (null != memberlist && memberlist.size() > 0) {
			DataRow[] datarow = new DataRow[memberlist.size()];
			for (int i = 0; i < memberlist.size(); i++) {				
				member = memberlist.get(i);
				staffer = staffInfoManager.getStaffInfoById(member.getId());
				primaryLanguange = "";
				DataRow row = new DataRow();
				row.addDataCell(member.getName(), 1);//1.姓名
				row.addDataCell(member.getLoginName(), 1);//2.登陆名
//				登录密码	默认置为空	可以手动改
				row.addDataCell("", 1);//3.登陆密码 TODO
				if(member.getPrimaryLanguange().equals("zh_CN")){
					primaryLanguange = member_primaryLanguange_zh_CN;
				}else if(member.getPrimaryLanguange().equals("en")){
					primaryLanguange = member_primaryLanguange_en;
				}else if(member.getPrimaryLanguange().equals("zh")){
					primaryLanguange = member_primaryLanguange_zh;
				}
				row.addDataCell(primaryLanguange, 1);//4.首选语言
				//5.人员性质
				if(member.getIsInternal()){
					row.addDataCell(member_type_inner, 1);
				}else{
					row.addDataCell(member_type_out, 1);
				}
				//6.帐户状态
				if (member.getEnabled()) {
					row.addDataCell(state_Enabled, 1);
				} else {
					row.addDataCell(state_Disabled, 1);
				}
				
				row.addDataCell(member.getCode(), 1);//7.代码/人员编号		

		
				row.addDataCell(member.getSortId().toString(), 1);//8.排序号
				dept = orgManagerDirect.getDepartmentById(member.getOrgDepartmentId());
				if(dept!=null){
					deptName = dept.getName();
				}
				row.addDataCell(deptName, 1);	//9.所属部门
				post = orgManagerDirect.getPostById(member.getOrgPostId());
				//此处做了副岗调整
				List<MemberPost> memberPosts = member.getSecond_post();
				if(null!=memberPosts&&!memberPosts.isEmpty()){
					for(MemberPost memberPost:memberPosts){
						V3xOrgDepartment sndDept = orgManagerDirect.getDepartmentById(memberPost.getDepId());
						V3xOrgPost sndPost = orgManagerDirect.getPostById(memberPost.getPostId());
						if(sndDept!=null&&sndPost!=null){
							String sndName = sndDept.getName()+"-"+sndPost.getName();
							secondPostName += sndName+",";
						}					 
					}					
				}			
				if (null != post) {
					postName = post.getName();
				}
				row.addDataCell(postName, 1);	//10.主要岗位
				row.addDataCell(secondPostName, 1);	//11.副岗
				level = orgManagerDirect.getLevelById(member.getOrgLevelId());
				if (null != level) {
					levelName = level.getName();
				}
				row.addDataCell(levelName, 1);	//12.职务级别
				memberTypeItem = orgMeta.get("org_property_member_type").getItem(member.getType().toString());
				if (null != memberTypeItem) {
					memberType = ResourceBundleUtil.getString(resource, memberTypeItem.getLabel(), "");
				}
				row.addDataCell(memberType, 1);	//13.人员类型
				memberStateItem = orgMeta.get("org_property_member_state").getItem(member.getState().toString());
				if (null != memberStateItem) {
					memberState = ResourceBundleUtil.getString(resource, memberStateItem.getLabel(), "");
				}
				row.addDataCell(memberState, 1);	//14.人员状态
				
//				获取个人角色信息---------xut
/*TODO!!!				roleList = orgManagerDirect.getUserDomain(member.getId(), V3xOrgEntity.ORGENT_TYPE_ROLE);
				if(roleList!=null){
					for(int j = 0;j<roleList.size();j++){
						role = (V3xOrgRole)roleList.get(j);
						roleNames+=role.getName()+",";
					}
				}*/
				
				row.addDataCell(roleNames, 1);		//15.个人角色
				if(member.getTelNumber()!=null&&!member.getTelNumber().equals("")){
					row.addDataCell(member.getTelNumber(), 1);//16.移动电话号码
				}else{
					row.addDataCell("", 1);
				}
				
				account = orgManagerDirect.getAccountById(member.getOrgAccountId());
				row.addDataCell(account.getName(), 1);		//17.所属单位				
				
				//StaffInfo
                //18.性别
				if(null!=staffer){
	         	    if("1".equals(member.getGender()+"")){
	         	    	row.addDataCell(sex_male,DataCell.DATA_TYPE_TEXT);
	         	    }
	         	    else if("2".equals(member.getGender()+"")){
	         	    	row.addDataCell(sex_female,DataCell.DATA_TYPE_TEXT);
	         	    }
	         	    row.addDataCell(staffer.getNation(), DataCell.DATA_TYPE_TEXT);//19.民族
	         	    row.addDataCell(staffer.getBirthplace(), DataCell.DATA_TYPE_TEXT);//20.籍贯
	         	    row.addDataCell(staffer.getAge(), DataCell.DATA_TYPE_TEXT);//21.年龄
	         	    row.addDataCell(staffer.getUsedname(), DataCell.DATA_TYPE_TEXT);//22.曾用名
                    //23.出生日期
	         	    if(null!=member.getBirthday()){
	         	    	row.addDataCell(Datetimes.formatDate(member.getBirthday()).toString(), DataCell.DATA_TYPE_TEXT);
	         	    }
	         	    else{
	         	    	row.addDataCell("", DataCell.DATA_TYPE_TEXT); 
	         	    } 
	         	    row.addDataCell(staffer.getID_card(), DataCell.DATA_TYPE_TEXT);//24.身份证
	         	    row.addDataCell(staffer.getSpecialty(), DataCell.DATA_TYPE_TEXT);//25.专业  
	         	    row.addDataCell(String.valueOf(staffer.getWorking_time()), DataCell.DATA_TYPE_TEXT);//26.工龄
                    //27.入职时间
	         	    if(null!=staffer.getWork_starting_date()){
	         	    	row.addDataCell(Datetimes.formatDate(staffer.getWork_starting_date()).toString(), DataCell.DATA_TYPE_TEXT);
	         	    }
	         	    else{
	         	    	row.addDataCell("", DataCell.DATA_TYPE_TEXT); 
	         	    }
	         	    //28.档案工资
	         	    if(null!=staffer.getRecord_wage()){
	         	    	row.addDataCell(String.valueOf(staffer.getRecord_wage()), DataCell.DATA_TYPE_TEXT);
	         	    }
	         	    else{
	         	    	row.addDataCell("", DataCell.DATA_TYPE_TEXT); 
	         	    }
	         	    
	            	int edu = staffer.getEdu_level();
	            	//29.最高学历
	         	    if(edu==1){
	         		   row.addDataCell(juniorschool ,DataCell.DATA_TYPE_TEXT);
	         	    }
	         	    else if(edu==2){
	         		   row.addDataCell(seniorschool ,DataCell.DATA_TYPE_TEXT);
	         	    }
	         	    else if(edu==3){
	         		   row.addDataCell(juniorcollege ,DataCell.DATA_TYPE_TEXT);
	         	    }
	         	    else if(edu==4){
	         		   row.addDataCell(university ,DataCell.DATA_TYPE_TEXT);
	         	    }
	         	    else if(edu==5){
	         		   row.addDataCell(postgraduate ,DataCell.DATA_TYPE_TEXT);
	         	    }
	         	    else if(edu==6){
	         		   row.addDataCell(doctor ,DataCell.DATA_TYPE_TEXT);
	         	    }
	         	    else if(edu==7){
	         		   row.addDataCell(edu_level_other ,DataCell.DATA_TYPE_TEXT);
	         	    } 
	         	    else {
	         	       row.addDataCell("" ,DataCell.DATA_TYPE_TEXT);
	         	    }
 
	                //30.政治面貌
	         	    if(1==staffer.getPolitical_position()){
	        		   row.addDataCell(commie, DataCell.DATA_TYPE_TEXT); 
	        	    }
	        	    else if(2==staffer.getPolitical_position()){
	        		   row.addDataCell(others, DataCell.DATA_TYPE_TEXT);         		   
	        	    }
	        	    else {
		         	       row.addDataCell("" ,DataCell.DATA_TYPE_TEXT);
		            }
	                //31.婚姻状况
	                if(1==staffer.getMarriage()){
	        		   row.addDataCell(single, DataCell.DATA_TYPE_TEXT);
	        	    }
	        	    if(2==staffer.getMarriage()){
	        		   row.addDataCell(married , DataCell.DATA_TYPE_TEXT);
	        	    }
	        	    else {
		         	   row.addDataCell("" ,DataCell.DATA_TYPE_TEXT);
		            }
	
	            	row.addDataCell(staffer.getHobby(), DataCell.DATA_TYPE_TEXT);//32.业余爱好
	            	row.addDataCell(staffer.getRemark(), DataCell.DATA_TYPE_TEXT);//33.备注
				}
				else{
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);
					row.addDataCell("",DataCell.DATA_TYPE_TEXT);		

			
				}
             
				row.addDataCell(member.getDescription(), 1);//34.描述

				datarow[i] = row;
			}

			DataRecord dataRecord = new DataRecord();
			try {
				dataRecord.addDataRow(datarow);
			} catch (Exception e) {
				LOG.error("", e);
			}
			String[] columnName = { member_name, member_loginName ,member_password, member_primaryLanguange , member_kind , member_state ,member_code,member_sortId,member_deptName,
					     member_primaryPost,member_secondPost,member_levelName,member_type,member_memberState,member_roles,member_tel,member_account,staff_sex,
					     staff_nation,staff_birthplace,staff_age,staff_usedName,staff_brithday,staff_IDCard,staff_specialty,staff_workingtime,staff_workStartingDate,staff_recordWage,
					     staff_edulevel,staff_position,staff_marriage,staff_hobby,staff_remark,member_description};
			dataRecord.setColumnName(columnName);
			dataRecord.setTitle(member_list);
			dataRecord.setSheetName(member_list);

			try {
				fileToExcelManager.save(request, response, member_list+"-"
						+ user.getLoginName(), "location.href", dataRecord);
			} catch (Exception e) {
				LOG.error("", e);
			}
		}

		return null;
	}
	
	public ModelAndView importExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView("hr/staffInfo/selectImportExcel");
		return modelAndView;
	}
	
	/**
	 * 导入人员---目前HR不支持
	 * @author lucx
	 *
	 */
	public ModelAndView importMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		V3xOrgAccount account = null;
		V3xOrgDepartment dept = null;
		V3xOrgMember member = null;
		V3xOrgPost post = null;
		V3xOrgLevel level = null;
		StaffInfo staffer = null;
		String repeat = request.getParameter("repeat");
		String language = request.getParameter("language");
			
		String resource = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";
		String resource1 = "com.seeyon.v3x.hr.resource.i18n.HRResources";
		Locale locale = null;
		if(language.equals("zh_CN")){
			locale = Locale.CHINA;
		}else if(language.equals("en")){
			locale = Locale.ENGLISH;
		}
        //国际化
		String sex_female = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.female.label");
		String sex_male = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.male.label"); 
		String juniorschool = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.juniorschool.label");
		String seniorschool = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.seniorschool.label");
		String juniorcollege = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.juniorcollege.label");
		String university = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.university.label");
		String postgraduate = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.postgraduate.label");
		String doctor = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.doctor.label");
		String edu_level_other = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.other.label");
		String commie= ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.commie.label");
		String others = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.others.label");
		String single= ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.single.label");
		String married = ResourceBundleUtil.getString(resource1, locale, "hr.staffInfo.married.label");
		
		String state_Enabled = ResourceBundleUtil.getString(resource, locale, "org.account_form.enable.use");
		String member_primaryLanguange_zh_CN = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryLanguange.zh_CN");
		String member_primaryLanguange_zh = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryLanguange.zh");
		String member_primaryLanguange_en = ResourceBundleUtil.getString(resource, locale, "org.member_form.primaryLanguange.en");
		String member_type_inner = ResourceBundleUtil.getString(resource, locale, "org.member_form.type.inner");
		String member_type_out = ResourceBundleUtil.getString(resource, locale, "org.member_form.type.out");
		List<MetadataItem> memberTypes = metadataManager.getMetadataItems("org_property_member_type");
		Map<String,String> typeMap = new HashMap<String,String>();
		for(MetadataItem item : memberTypes){
			typeMap.put(item.getLabel(), item.getValue());
		}
		String typelable = "";
		for(MetadataItem item : memberTypes){
			typelable = ResourceBundleUtil.getString(resource, locale, item.getLabel(), "");
			if(!typelable.equals("")){
				typeMap.put(typelable, item.getValue());
			}
		}
		List<MetadataItem> memberStates = metadataManager.getMetadataItems("org_property_member_state");
		Map<String,String> stateMap = new HashMap<String,String>();
		for(MetadataItem item : memberStates){
			stateMap.put(item.getLabel(), item.getValue());
		}
		String statelable = "";
		for(MetadataItem item : memberStates){
			statelable = ResourceBundleUtil.getString(resource, locale, item.getLabel(), "");
			if(!statelable.equals("")){
				stateMap.put(statelable, item.getValue());
			}
		}
		List<Long> myrole = new ArrayList<Long>();
		String[] roles = null;
		V3xOrgRole role = null;
		boolean isMemberNull = false;
		boolean isStaffNull = false;
		//从前台获取要导入文件的路径
		String impURL = request.getParameter("impURL");
		String fileURL = impURL.replace("\\", "/");
		try {
			File file = new File(fileURL);
			List<List<String>> memberList = fileToExcelManager.readExcel(file);
			List<String> proList = new ArrayList<String>();
			//从第三行开始才是实际要导入数据
			for(int i = 2 ; i < memberList.size() ; i++){
				HashMap<Long,Long> second_post = new HashMap<Long,Long>();
				proList = memberList.get(i);
				//根据登录名取得人员
				member = orgManagerDirect.getMemberByLoginName(proList.get(1));
				if(member==null){
					isMemberNull=true;
					member = new V3xOrgMember();
				}
				if(proList.get(16)!=null&&!proList.get(16).equals("")){
					account = orgManagerDirect.getAccountByName(proList.get(16));		//获取单位信息
				}			
                //1.人员名称
				if(proList.get(0)!=null&&!proList.get(0).equals("")){
					member.setName(proList.get(0));	
				}
                //2.登录名
				if(proList.get(1)!=null&&!proList.get(1).equals("")){
					member.setLoginName(proList.get(1));
				}
                //3.登录密码		如果excel中没设密码将密码置为123
				if(proList.get(2)!=null&&!proList.get(2).equals("")){
	     			member.setPassword(proList.get(2));
				}else{
					member.setPassword("123");
				}
				//4.首选语言
				if(proList.get(3).equals(member_primaryLanguange_zh_CN)){
					member.setPrimaryLanguange("zh_CN");
				}else if(proList.get(3).equals(member_primaryLanguange_zh)){
					member.setPrimaryLanguange("zh");
				}else if(proList.get(3).equals(member_primaryLanguange_en)){
					member.setPrimaryLanguange("en");
				}					
				//5.人员性质
				if(proList.get(4).equals(member_type_inner)){
					member.setIsInternal(true);
				}else if(proList.get(4).equals(member_type_out)){
					member.setIsInternal(false);
				}
                //6.状态
				if(proList.get(5).equals(state_Enabled)){
					member.setEnabled(true);
				}else{
					member.setEnabled(false);
				}				
				//7.代码
				member.setCode(proList.get(6));				
				//8.排序号
				member.setSortId(Integer.parseInt(proList.get(7)));		

			
				//9.所属部门
                /*TODO  
                dept = orgManagerDirect.getDepartmentByName(proList.get(8), account.getId());
				if(dept!=null){
					member.setOrgDepartmentId(dept.getId());
				}
                */				
				//10.主要岗位
				if(proList.get(9)!=null&&!proList.get(9).equals("")){
					post = orgManagerDirect.getPostByName(proList.get(9));
					if(i==2){
					}
					if(post!=null){
						member.setOrgPostId(post.getId());		

				
					}
				}	
				//11.副岗//此处做了副岗调整，请同步调整代码
				/*
				if(proList.get(10)!=null&&!proList.get(10).equals("")){
					post = orgManagerDirect.getPostByName(proList.get(10));
					if(post!=null){
						second_post.put(member.getId(), post.getId());
						member.setSecond_post(second_post);
					}
				}*/					
				//12.职务级别
				if(proList.get(11)!=null&&!proList.get(11).equals("")){
					level = orgManagerDirect.getLevelByName(proList.get(11));
					if(level!=null){
					    member.setOrgLevelId(level.getId());
					}
				}
                //13.人员类型
				if(typeMap.get(proList.get(12))!=null&&!typeMap.get(proList.get(12)).equals("")){
					member.setType(new Byte(typeMap.get(proList.get(12))));
				}					
                //14.人员状态
				if(stateMap.get(proList.get(13))!=null&&!stateMap.get(proList.get(13)).equals("")){
					member.setState(new Byte(stateMap.get(proList.get(13))));
				}					
                //15.个人角色
				if(proList.get(14)!=null&&!proList.get(14).equals("")){
					roles = proList.get(14).split(",");
					for(String roleName : roles){
						role = orgManagerDirect.getRoleByName(roleName);
						if(role!=null){
							myrole.add(role.getId());	
						}
					}
						member.setMyrole(myrole);
				}
				member.setTelNumber(proList.get(15));//16.移动电话号码		
				member.setOrgAccountId(account.getId());//17.所属单位		
				member.setDescription(proList.get(33));	//34.说明		

	
				if(isMemberNull){
					orgManagerDirect.addMember(member);
				}else{					
                    //如果重复项标识是0覆盖(update)否则跳过
					if(repeat.equals("0")){
						orgManagerDirect.updateEntity(member);
					}
				}	
				staffer = staffInfoManager.getStaffInfoById(member.getId());
				if(staffer==null){
					isStaffNull = true;
					staffer = new StaffInfo();
				}
				staffer.setOrg_member_id(member.getId());
				//18.性别
				if(sex_male.equals(proList.get(17))){
					member.setGender(1);	
				}
				else if(sex_female.equals(proList.get(17))){
					member.setGender(2);	
				}
				staffer.setNation(proList.get(18));//19.民族
				staffer.setBirthplace(proList.get(19));//20.籍贯
				staffer.setUsedname(proList.get(21));//22.曾用名
				if(null!=proList.get(22)&&!"".equals(proList.get(22))){
					member.setBirthday(Datetimes.parse(proList.get(22), "yyyy-MM-dd"));//23.出生日期	
				}				
				staffer.setID_card(proList.get(23));//24.身份证
				staffer.setSpecialty(proList.get(24));//25.专业
				if(null!=proList.get(26)&&!"".equals(proList.get(26))){
					staffer.setWork_starting_date(Datetimes.parse(proList.get(26), "yyyy-MM-dd"));//27.入职时间
				}	
				if(null!=proList.get(27)&&!"".equals(proList.get(27))){
					staffer.setRecord_wage(Float.valueOf(proList.get(27)));//28.档案工资
				}
				//29.最高学历
				if(juniorschool.equals(proList.get(28))){
					staffer.setEdu_level(1);
				}
				else if(seniorschool.equals(proList.get(28))){
					staffer.setEdu_level(2);
				}
				else if(juniorcollege.equals(proList.get(28))){
					staffer.setEdu_level(3);
				}
				else if(university.equals(proList.get(28))){
					staffer.setEdu_level(4);
				}
				else if(postgraduate.equals(proList.get(28))){
					staffer.setEdu_level(5);
				}
				else if(doctor.equals(proList.get(28))){
					staffer.setEdu_level(6);
				}
				else if(edu_level_other.equals(proList.get(28))){
					staffer.setEdu_level(7);
				}
				//30.政治面貌
				if(commie.equals(proList.get(29))){
					staffer.setPolitical_position(1);
				}
				else if(others.equals(proList.get(29))){
					staffer.setPolitical_position(2);
				}
				//31.婚姻状况
                if(single.equals(proList.get(30))){
					staffer.setMarriage(1);
				}
				else if(married.equals(proList.get(30))){
					staffer.setMarriage(2);
				}
                staffer.setHobby(proList.get(31));//32.业余爱好
                staffer.setRemark(proList.get(32));//33.备注
                if(isStaffNull){
					staffInfoManager.addStaffInfo( staffer);
				}else{					
                    //如果重复项标识是0覆盖(update)否则跳过
					if(repeat.equals("0")){
						staffInfoManager.updateStaffInfo(staffer);
					}
				}		
			}
				

		} catch (Exception e) {
			LOG.error("", e);
		}
		return super.redirectModelAndView("/hrStaff.do?method=initStaffInfoList");
	}
	
	/**
	 * 花名册选人界面
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView initSetNameList(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("hr/staffInfo/setNameList");
		return mav;
	}
	//高级查询
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView highLevelSerchList(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("hr/staffInfo/setSearchList");
		return mav;
	}
//	高级查询框架
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView initStaffListFrame(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
//		try{
//			User user = CurrentUser.get() ;
//			if(!Functions.isRole(V3xOrgEntity.ORGENT_META_KEY_HRADMIN, user)){
//				LOG.info("人员登录员工档案管理--"+user.getId()+request.getRemoteAddr()) ;
//				return null ;
//			}			
//		}catch(Exception e){
//			LOG.info("人员登录员工档案管理--"+request.getRemoteAddr()) ;
//			return null ;
//		}
		ModelAndView mav = new ModelAndView("hr/staffInfo/staffListFrame");
		return mav;
	}
	
	/**
	 * Hr进入--员工档案管理
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView initStaffInfoList(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		try {
			User user = CurrentUser.get();
			String condition = request.getParameter("condition");
			String textfield = request.getParameter("textfield");
			List<V3xOrgMember> memberlist = OrganizationHelper.searchMember(condition, textfield, searchManager, orgManagerDirect,true,true,true);

			ModelAndView result = new ModelAndView("hr/staffInfo/staffInfoList");

			List<WebV3xOrgMember> resultlist = new ArrayList<WebV3xOrgMember>();
			Long deptId = Long.valueOf(-1);
			Long levelId = Long.valueOf(-1);
			Long postId = Long.valueOf(-1);
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
					  if(LDAPConfig.getInstance().getIsEnableLdap()&&SystemEnvironment.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD.name()))
                      {
                          try
                          {
                              webMember.setStateName(event.getLdapAdLoginName(member.getLoginName()));
                              result.addObject("hasLDAPAD", true);
                          }
                          catch (Exception e)
                          {
                              LOG.error(this.getClass().getName()+" ldap_ad 显示ldap帐号 error :",e);
                          }
                      }
					resultlist.add(webMember);
				}
			}

			result.addObject("memberlist", resultlist);
			// 获得单位类别下拉列表中的数据
			Map<String, Metadata> orgMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.organization);
			result.addObject("orgMeta", orgMeta);
			// 取得所有的岗位,以便初始化查询条件的岗位下拉列表
			List<V3xOrgPost> postlist = orgManager.getAllPosts(user.getLoginAccount());// orgManagerDirect.getAllPosts(user.getLoginAccount(), false);
			result.addObject("postlist", postlist);
			// 取得所有职务级别
			List<V3xOrgLevel> levellist = orgManager.getAllLevels(user.getLoginAccount()); //orgManagerDirect.getAllLevels(user.getLoginAccount(), false);
			result.addObject("levellist", levellist);
			// 取得所有的部门
			List<V3xOrgDepartment> departmentlist = orgManager.getAllDepartments(user.getLoginAccount());//orgManagerDirect.getAllDepartments(user.getLoginAccount(), false);
			result.addObject("departmentlist", departmentlist);

			return result;
		} catch (Exception e) {
			LOG.error("", e);
		}
		return null;
	}	
	/**
	 * 高级查询方法
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView highLevelQueryList(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		
		ModelAndView result = new ModelAndView("hr/staffInfo/staffInfoList");
		
		String de = request.getParameter("department");//部门
		String le = request.getParameter("level");//职务级别
		String po = request.getParameter("post");//岗位
		
		String se = request.getParameter("sex");//性别
		String st = request.getParameter("study");//学历
		String pol = request.getParameter("polity");//政治面貌
		String ma = request.getParameter("marriage");//婚姻
	
		String fT1 = request.getParameter("fromTime1");//出生日期 -从
		String tT1 = request.getParameter("toTime1"); //出生日期 -到
		String fT2= request.getParameter("fromTime2");//入职时间 -从
		String tT2 = request.getParameter("toTime2");//入职时间 -到
		
		
		try {		
			User user = CurrentUser.get();
			List<V3xOrgMember> memberlist=new ArrayList<V3xOrgMember>();
			
			if((st!=null&&!st.equals("")&& Integer.valueOf(st)!=-1)||(pol!=null&&!pol.equals("")&& Integer.valueOf(pol)!=-1)
					||(ma!=null&&!ma.equals("")&& Integer.valueOf(ma)!=-1)||(fT1!=null&&!fT1.equals(""))
					||(fT2!=null&&!fT2.equals(""))||(tT1!=null&&!tT1.equals(""))||(tT2!=null&&!tT2.equals("")))
			{
				memberlist = HqlSearchHelper.highSearchMember( de,le,po,se,st,pol,ma,fT1,tT1,fT2,tT2, searchManager, orgManagerDirect,principalManager,true);		
				
			}else{
				memberlist = HqlSearchHelper.highSearchMember(se, de,le,po, searchManager, orgManagerDirect,principalManager,true);
			}
			

			List<WebV3xOrgMember> resultlist = new ArrayList<WebV3xOrgMember>();
			Long deptId = Long.valueOf(-1);
			Long levelId = Long.valueOf(-1);
			Long postId = Long.valueOf(-1);
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
			// 获得单位类别下拉列表中的数据
			Map<String, Metadata> orgMeta = metadataManager
					.getMetadataMap(ApplicationCategoryEnum.organization);
			result.addObject("orgMeta", orgMeta);
			// 取得所有的岗位,以便初始化查询条件的岗位下拉列表
			List<V3xOrgPost> postlist = orgManager.getAllPosts(user.getLoginAccount());
			result.addObject("postlist", postlist);
			// 取得所有职务级别
			List<V3xOrgLevel> levellist = orgManager.getAllLevels(user.getLoginAccount());
			result.addObject("levellist", levellist);
			// 取得所有的部门
			List<V3xOrgDepartment> departmentlist = orgManager.getAllDepartments(user.getLoginAccount());
			result.addObject("departmentlist", departmentlist);

			return result;
		} catch (Exception e) {
			LOG.error("", e);
		}
		return null;
	}	
	
	/**
	 * 菜单管理
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes = RoleType.HrAdmin)
	public ModelAndView initInfoToolBar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {		
		ModelAndView mav=new ModelAndView("hr/staffInfo/infotoolbar");
		int infotype = RequestUtils.getIntParameter(request, "infoType", 1);
		LOG.debug("infoType  :   "+infotype);
		List<WebProperty> webPages = new ArrayList<WebProperty>();
		List<Page> pages = this.userDefinedManager.getPageByModelName("staff");
		for(Page page : pages){
			WebProperty webPage = new WebProperty();
			List<PageLabel> labels = this.userDefinedManager.getPageLabelByPageId(page.getId());
			for(PageLabel label : labels){
				if(label.getLanguage().equals("zh_CN"))
					webPage.setPageName_zh(label.getPageLabelValue());
				else
					webPage.setPageName_en(label.getPageLabelValue());
			}
			webPage.setPage_id(page.getId());
			webPage.setPageNo(page.getPageNo());
			webPages.add(webPage);
		}
		
		String pageID = request.getParameter("page_id");
		if(null!=pageID && !"".equals(pageID)){
			infotype = 0;
			mav.addObject("page_id", Long.valueOf(pageID));
		}		
		LOG.debug("webPages  size  : "+webPages.size());
		mav.addObject("webPages", webPages);
		mav.addObject("infoType", infotype);
		mav.addObject("staffId", request.getParameter("staffId"));
		mav.addObject("isManager", request.getParameter("isManager"));
		mav.addObject("isNew", request.getParameter("isNew"));
		return mav;
	}
	@CheckRoleAccess(roleTypes = RoleType.HrAdmin)
	public ModelAndView initDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView("hr/staffInfo/detailHome");
		String infoType = request.getParameter("infoType");
		int infotype = 0;
		if(null!=infoType){
		   infotype = Integer.parseInt(infoType); 
		}
		
		String type = "";
		if (infotype==3)   type = "Relationship"; 
		if (infotype==4)   type = "WorkRecord"; 
		if (infotype==5)   type = "EduExperience"; 
		if (infotype==6)   type = "PostChange"; 
		if (infotype==7)   type = "Assess"; 
		if (infotype==8)   type = "RewardsAndPunishment";  
		mav.addObject("type", type);
		mav.addObject("id", request.getParameter("id"));
		mav.addObject("isNew", request.getParameter("isNew"));
		
		mav.addObject("isReadOnly", request.getParameter("isReadOnly"));
		mav.addObject("isManager", request.getParameter("isManager"));
		mav.addObject("staffId", request.getParameter("staffId"));
		return mav;
	}
	
	/**
	 * 进入人员附加信息界面的跳转
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView initSpace(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		ModelAndView mav = new ModelAndView();
		int infotype = RequestUtils.getIntParameter(request, "infoType" , 1);
		if (infotype==1)   mav=this.initStafferInfo(request,response);
		else if (infotype==2)   mav=this.initContactInfo(request, response);
		//else if (infotype==3)   mav=this.initRelationship(request, response);
		//else if (infotype==4)   mav=this.initWorkRecord(request, response);
		//else if (infotype==5)   mav=this.initEduExperience(request, response);
		//else if (infotype==6)   mav=this.initPostChange(request, response);
		//else if (infotype==7)   mav=this.initAssess(request, response);
		//else if (infotype==8)   mav=this.initRewardsAndPunishment(request, response);
		else mav=this.initHome(request, response);
		return mav;
	}
	@CheckRoleAccess(roleTypes = RoleType.HrAdmin)
	public ModelAndView initHome(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
        ModelAndView mav = new ModelAndView("hr/staffInfo/home");
        int infotype = RequestUtils.getIntParameter(request, "infoType");
        String listType = "";
        if(infotype==3)   listType="Relationship"; 
        else if (infotype==4)   listType="WorkRecord"; 
		else if (infotype==5)   listType="EduExperience"; 
		else if (infotype==6)   listType="PostChange"; 
		else if (infotype==7)   listType="Assess"; 
		else if (infotype==8)   listType="RewardsAndPunishment"; 
        mav.addObject("listType", listType);
        mav.addObject("isManager", request.getParameter("isManager"));       
        mav.addObject("staffId", request.getParameter("staffId"));
        return mav;
	}
	
	/**
	 * hr添加修改人员
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes = RoleType.HrAdmin)
	public ModelAndView initStafferInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {	
		ModelAndView mav = new ModelAndView("hr/staffInfo/staffInfo");
		StaffInfo staffInfo = new StaffInfo();
		V3xOrgMember member = new V3xOrgMember();
		String isNew = request.getParameter("isNew");
		if(null!=isNew && isNew.equals("New")){
			mav.addObject("staff", staffInfo);
			member.setPassword("");
			member.setSortId(orgManagerDirect.getMaxMemberSortNum(CurrentUser.get().getLoginAccount())+1);
    		mav.addObject("member", member);

    		//获取默认菜单权限
    		String securityIds = null;
    		String securityNames = null;
            List<Security> defaultSecurities = this.menuManager.getDefaultSecurities();
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
            mav.addObject("securityIds", securityIds);
            mav.addObject("securityNames", securityNames);
    			
        }
		else{
			mav.addObject("editstate", true);
			Long staffid = RequestUtils.getLongParameter(request, "staffId");
            member = orgManagerDirect.getMemberById(staffid);
    	    staffInfo=staffInfoManager.getStaffInfoById(staffid);

    		mav.addObject("staff", staffInfo);
    		if (null != staffInfo) {
    			if(null!=staffInfo.getImage_id() && !staffInfo.getImage_id().equals("")){
					mav.addObject("image", 0);
				}
				mav.addObject("attachments", attachmentManager.getByReference(
						staffInfo.getId(), staffInfo.getId()));
			}
    		
    		mav.addObject("member", member);   		
     		
    		WebStaffInfo webMember = this.translateV3xOrgMember(member);
    		   		
    		mav.addObject("webMember", webMember);
    		
//    		获取该用户菜单权限
            String securityIds = null;
            String securityNames = null;
            List<Security> defaultSecurities = this.menuManager.getSecurityOfMember(member.getId(), member.getOrgAccountId(), true);
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
            mav.addObject("securityIds", securityIds);
            mav.addObject("securityNames", securityNames);
		}
		
        //获得单位类别下拉列表中的数据
        Map<String, Metadata> orgMeta = metadataManager
                .getMetadataMap(ApplicationCategoryEnum.organization);
        mav.addObject("orgMeta", orgMeta);
        Map<String, Metadata> hrMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.hr);
        mav.addObject("hrMetadata", hrMetadata);
        //获取职务级别列表
		List<V3xOrgLevel> levels = orgManagerDirect.getAllLevels(CurrentUser.get().getLoginAccount(), false);
		List<V3xOrgLevel> levelsForPage = new ArrayList<V3xOrgLevel>();
        //过滤无效项并获得最小职务级别序号
		Integer minLevelId = 1;
		for(V3xOrgLevel level:levels){
			if(level.getEnabled()){
				levelsForPage.add(level);
				if(minLevelId<level.getLevelId()){
					minLevelId = level.getLevelId();
				}
			}				
		}
		mav.addObject("levels", levelsForPage);
		mav.addObject("minLevelId", minLevelId);
        
        String isReadOnly = request.getParameter("isReadOnly");
		boolean readOnly = false;
		if(null!=isReadOnly && isReadOnly.equals("ReadOnly")){
			readOnly = true;
		}
		mav.addObject("ReadOnly", readOnly);
		
		String isManager = request.getParameter("isManager");
		boolean manager = false;
		if("Manager".equals(isManager)){
			manager = true;
		}
		String Manager = request.getParameter("Manager");
		if(null!=Manager && Manager.equals("true")){
			manager = true;
		}
	    if(LDAPConfig.getInstance().getIsEnableLdap()&&SystemEnvironment.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD.name()))
        {
            try
            {
            	if(null!=isNew && isNew.equals("New"))
            	{
            		mav.addObject("addstate", true);
            	}
            	else
            	{
            		mav.addObject("editstate", true);            		
            		mav.addObject("ldapADLoginName", event.getLdapAdExUnitCode(member.getLoginName()));
            	}
                mav.addObject("hasLDAPAD", true);
            }
            catch (Exception e)
            {
               LOG.error("ldap_ad",e);
            }
        }
	    List<SpaceModel> spaceList = spaceManager.getAdminCanManagerSpace(CurrentUser.get().getLoginAccount(), SpaceTypeClass.personal, "state", String.valueOf(SpaceState.normal.ordinal()),false);
        mav.addObject("spaceList", spaceList);
        Long currentSpaceId = null;
        if(null!=isNew && isNew.equals("New")){
        	currentSpaceId = spaceManager.getPersonalSpaceId4Create(true,CurrentUser.get().getLoginAccount());
        }else{
        	currentSpaceId = spaceManager.getPersonalSpaceId(member);
        }
        //currentSpaceId
        mav.addObject("currentSpaceId", currentSpaceId);
        
		mav.addObject("isManager", isManager);
		mav.addObject("Manager", manager);
		mav.addObject("isNew", isNew);
		return mav;
	}
	
	/**
	 * 联系信息页签
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes = RoleType.HrAdmin)
	public ModelAndView initContactInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long staffid = RequestUtils.getLongParameter(request, "staffId");
		ModelAndView mav = new ModelAndView("hr/staffInfo/contactInfo");
			
	    ContactInfo contactInfo = staffInfoManager.getContactInfoById(staffid);
		V3xOrgMember member = orgManagerDirect.getMemberById(staffid);
		mav.addObject("contactInfo", contactInfo);//个人情况，包括家庭电话
		mav.addObject("member", member);
		//--得到办公电话的扩展属性
		orgManager.loadEntityProperty(member);
		String officeNum = member.getProperty("officeNum");
		mav.addObject("officeNum", officeNum);
		//--
		
		String isReadOnly = request.getParameter("isReadOnly");
		boolean readOnly = false;
		if(null!=isReadOnly && isReadOnly.equals("ReadOnly")){
			readOnly = true;
		}
		mav.addObject("ReadOnly", readOnly);
		
		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}
	
	/**
	 * 家庭成员与社会关系
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView initRelationship(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long staffid = RequestUtils.getLongParameter(request,"staffId");
		List<Relationship> list = staffInfoManager.getRelationshipByStafferId(staffid);
		
		ModelAndView mav = new ModelAndView("hr/staffInfo/relationship");
		
		mav.addObject("list", this.pagenate(list));
		V3xOrgMember member = orgManagerDirect.getMemberById(staffid);
		mav.addObject("staff", member);	
		
//		String ID = request.getParameter("detailId");
//		if(null!=ID && !ID.equals("")){
//			Relationship relationship = staffInfoManager.getRelationshipById(Long.valueOf(ID));
//			mav.addObject("relationship", relationship);
//		}
		
		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}
	
	/**
	 * 工作履历
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView initWorkRecord(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long staffid = RequestUtils.getLongParameter(request,"staffId");
		ModelAndView mav = new ModelAndView("hr/staffInfo/workRecord");
       
		List<WorkRecord> list = staffInfoManager.getWorkRecordByStafferId(staffid);

		mav.addObject("list", this.pagenate(list));
		
		V3xOrgMember member = orgManagerDirect.getMemberById(staffid);
		mav.addObject("staff", member);

		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}
	
	/**
	 * 教育培训
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView initEduExperience(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("hr/staffInfo/eduExperience");
		Long staffid = RequestUtils.getLongParameter(request,"staffId");
		List<EduExperience> list = staffInfoManager.getEduExperienceByStafferId(staffid);
        mav.addObject("list", this.pagenate(list));
		
		V3xOrgMember member = orgManagerDirect.getMemberById(staffid);
		mav.addObject("staff", member);
		
		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}
	
	/**
	 * 职务变动
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView initPostChange(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/postChange");
		Long staffid = RequestUtils.getLongParameter(request,"staffId");
		List<PostChange> list = staffInfoManager.getPostChangeByStafferId(staffid);
        mav.addObject("list", this.pagenate(list));
		
		V3xOrgMember member = orgManagerDirect.getMemberById(staffid);
		mav.addObject("staff", member);
			
//		String isManager = request.getParameter("isManager");
//		boolean manager = false;
//		if(null!=isManager && isManager.equals("Manager")){
//			manager = true;
//		}
//		String Manager = request.getParameter("Manager");
//		if(null!=Manager && Manager.equals("true")){
//			manager = true;
//		}
//		mav.addObject("Manager", manager);
		
		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}
	
	/**
	 * 考核情况
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView initAssess(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/assess");
		Long staffid = RequestUtils.getLongParameter(request,"staffId");
		List<Assess> list = staffInfoManager.getAssessByStafferId(staffid);
        mav.addObject("list", this.pagenate(list));
		
		V3xOrgMember member = orgManagerDirect.getMemberById(staffid);
		mav.addObject("staff", member);
		
		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}
	
	/**
	 * 奖惩档案
	 * @author lucx
	 *
	 */
	@CheckRoleAccess(roleTypes=RoleType.HrAdmin)
	public ModelAndView initRewardsAndPunishment(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/rewardsAndPunishment");
		Long staffid = RequestUtils.getLongParameter(request,"staffId");
		List<RewardsAndPunishment> list = staffInfoManager.getRewardsAndPunishmentByStafferId(staffid);
        mav.addObject("list", this.pagenate(list));
		
		V3xOrgMember member = orgManagerDirect.getMemberById(staffid);
		mav.addObject("staff", member);
		
		mav.addObject("isManager", request.getParameter("isManager"));
		return mav;
	}

	/**
	 * 根据人员取到人员的附加信息
	 * @author lucx
	 *
	 */
	public WebStaffInfo translateV3xOrgMember(V3xOrgMember member) throws BusinessException {
		Long accId = member.getOrgAccountId();
		Long deptId = member.getOrgDepartmentId();
		Long levelId = member.getOrgLevelId();
		Long postId = member.getOrgPostId();
		WebStaffInfo webstaffinfo = new WebStaffInfo();
		V3xOrgAccount acc = orgManager.getAccountById(accId);
		if(acc != null){
			webstaffinfo.setOrg_name(acc.getName());
		}
		V3xOrgEntity dept = orgManager.getDepartmentById(deptId);
		if (dept != null) {
			webstaffinfo.setDepartment_name(dept.getName());
		}
		V3xOrgEntity level = orgManager.getLevelById(levelId);
		if (null != level) {
			if(level.getEnabled()){
				webstaffinfo.setLevel_name(level.getName());
				webstaffinfo.setOrgLevelId(level.getId());				
			}else{
				member.setOrgLevelId(V3xOrgEntity.DEFAULT_NULL_ID);
			}
		}		
		V3xOrgEntity post = orgManager.getPostById(postId);
		if (null != post) {
			if(post.getEnabled()){
				webstaffinfo.setPost_name(post.getName());
				webstaffinfo.setOrgPostId(post.getId());				
			}else{
				member.setOrgPostId(V3xOrgEntity.DEFAULT_NULL_ID);
			}
		}
		webstaffinfo.setSex(member.getGender()+"");//取的性别
		webstaffinfo.setName(member.getName());
		webstaffinfo.setType(member.getType());
		webstaffinfo.setCode(member.getCode());
		webstaffinfo.setState(member.getState());
		webstaffinfo.setId(member.getId());
		webstaffinfo.setPeople_type(member.getIsInternal());
		 
		// 取得人员的副岗
		List<MemberPost> Memberposts = member.getSecond_post();
		if (null != Memberposts && !Memberposts.isEmpty()) {
			StringBuffer deptpostbuffer = new StringBuffer();
			StringBuffer deptpostIdsBuffer = new StringBuffer();
			for (MemberPost memberPost : Memberposts) {
				StringBuffer sbuffer = new StringBuffer();
				StringBuffer idsbuffer = new StringBuffer();
				Long deptid = memberPost.getDepId();
				V3xOrgDepartment v3xdept = orgManager.getDepartmentById(deptid);
				Long postid = memberPost.getPostId();
				V3xOrgPost v3xpost = orgManager.getPostById(postid);
				if(v3xdept!=null&&v3xdept.getEnabled()&&v3xpost!=null&&v3xpost.getEnabled()){
					sbuffer.append(v3xdept.getName());
					sbuffer.append("-");
					idsbuffer.append(deptid);
					idsbuffer.append("_");
					sbuffer.append(v3xpost.getName());				
					idsbuffer.append(postid);
					deptpostbuffer.append(sbuffer.toString());
					deptpostIdsBuffer.append(idsbuffer.toString());
					deptpostbuffer.append(",");
					deptpostIdsBuffer.append(",");					
				}
			}
			if (deptpostbuffer.length() > 0) {
				String deptpostStr = deptpostbuffer.toString();
				String deptpostIdsStr = deptpostIdsBuffer.toString();
				deptpostStr = deptpostStr.substring(0, deptpostStr.length() - 1);
				deptpostIdsStr = deptpostIdsStr.substring(0, deptpostIdsStr.length() - 1);
				webstaffinfo.setSecond_posts(deptpostStr);
				webstaffinfo.setSecond_posts_ids(deptpostIdsStr);
			}
		}
		return webstaffinfo;
	}
	
	
	public ModelAndView editRelationship(HttpServletRequest request,
		     HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/editRelationship");
		String ID = request.getParameter("id");
		if(null!=ID && !ID.equals("")){
			Relationship relationship = staffInfoManager.getRelationshipById(Long.valueOf(ID));
			mav.addObject("relationship", relationship);
		}
		mav.addObject("staffId", request.getParameter("staffId"));
		
		String isManager = request.getParameter("isManager");
		boolean manager = false;
		if(null!=isManager && isManager.equals("Manager")){
			manager = true;
		}
		mav.addObject("Manager", manager);
		
		String isReadOnly = request.getParameter("isReadOnly");
		boolean readOnly = false;
		if(null!=isReadOnly && isReadOnly.equals("ReadOnly")){
			readOnly = true;
		}
		mav.addObject("ReadOnly", readOnly);
		return mav;
	}

	public ModelAndView editWorkRecord(HttpServletRequest request,
		    HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/editWorkRecord");
		String ID = request.getParameter("id");
		if(null!=ID && !ID.equals("")){
			WorkRecord workRecord = staffInfoManager.getWorkRecordById(Long.valueOf(ID));
			mav.addObject("workRecord", workRecord);
		}
		 
		mav.addObject("staffId", request.getParameter("staffId"));
		
		String isManager = request.getParameter("isManager");
		boolean manager = false;
		if(null!=isManager && isManager.equals("Manager")){
			manager = true;
		}
		mav.addObject("Manager", manager);
		
		String isReadOnly = request.getParameter("isReadOnly");
		boolean readOnly = false;
		if(null!=isReadOnly && isReadOnly.equals("ReadOnly")){
			readOnly = true;
		}
		mav.addObject("ReadOnly", readOnly);
		return mav;
	}
	
	public ModelAndView editEduExperience(HttpServletRequest request,
		    HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/editEduExperience");
		String ID = request.getParameter("id");
		if(null!=ID && !ID.equals("")){
			EduExperience eduExperience = staffInfoManager.getEduExperienceById(Long.valueOf(ID));
			mav.addObject("eduExperience", eduExperience);
			mav.addObject("attachments", attachmentManager.getByReference(eduExperience.getId(),eduExperience.getId()));
		}
		 
		mav.addObject("staffId", request.getParameter("staffId"));
		
		String isManager = request.getParameter("isManager");
		boolean manager = false;
		if(null!=isManager && isManager.equals("Manager")){
			manager = true;
		}
		mav.addObject("Manager", manager);
		
		String isReadOnly = request.getParameter("isReadOnly");
		boolean readOnly = false;
		if(null!=isReadOnly && isReadOnly.equals("ReadOnly")){
			readOnly = true;
		}
		mav.addObject("ReadOnly", readOnly);
		return mav;
	}
	
	public ModelAndView editPostChange(HttpServletRequest request,
		    HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/editPostChange");
		String ID = request.getParameter("id");
		if(null!=ID && !ID.equals("")){
			PostChange postChange = staffInfoManager.getPostChangeById(Long.valueOf(ID));
			mav.addObject("postChange", postChange);
		}
		 
		mav.addObject("staffId", request.getParameter("staffId"));
		
		String isManager = request.getParameter("isManager");
		boolean manager = false;
		if(null!=isManager && isManager.equals("Manager")){
			manager = true;
		}
		mav.addObject("Manager", manager);
		
		String isReadOnly = request.getParameter("isReadOnly");
		boolean readOnly = false;
		if(null!=isReadOnly && isReadOnly.equals("ReadOnly")){
			readOnly = true;
		}
		mav.addObject("ReadOnly", readOnly);
		return mav;
	}
	
	public ModelAndView editAssess(HttpServletRequest request,
		    HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/editAssess");
		String ID = request.getParameter("id");
		if(null!=ID && !ID.equals("")){
			Assess assess = staffInfoManager.getAssessById(Long.valueOf(ID));
			mav.addObject("assess", assess);
		}
		 
		mav.addObject("staffId", request.getParameter("staffId"));
		
		String isManager = request.getParameter("isManager");
		boolean manager = false;
		if(null!=isManager && isManager.equals("Manager")){
			manager = true;
		}
		mav.addObject("Manager", manager);
		
		String isReadOnly = request.getParameter("isReadOnly");
		boolean readOnly = false;
		if(null!=isReadOnly && isReadOnly.equals("ReadOnly")){
			readOnly = true;
		}
		mav.addObject("ReadOnly", readOnly);
		return mav;
	}
	
	public ModelAndView editRewardsAndPunishment(HttpServletRequest request,
		    HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/editRewardsAndPunishment");
		String ID = request.getParameter("id");
		if(null!=ID && !ID.equals("")){
			RewardsAndPunishment rewardsAndPunishment = staffInfoManager.getRewardsAndPunishmentById(Long.valueOf(ID));
			mav.addObject("rewardsAndPunishment", rewardsAndPunishment);
		}
		 
		mav.addObject("staffId", request.getParameter("staffId"));
		
		String isManager = request.getParameter("isManager");
		boolean manager = false;
		if(null!=isManager && isManager.equals("Manager")){
			manager = true;
		}
		mav.addObject("Manager", manager);
		
		String isReadOnly = request.getParameter("isReadOnly");
		boolean readOnly = false;
		if(null!=isReadOnly && isReadOnly.equals("ReadOnly")){
			readOnly = true;
		}
		mav.addObject("ReadOnly", readOnly);
		return mav;
	}
	
	
	/**
	 * 更新人员信息
	 * @author lucx
	 *
	 */
	public ModelAndView updateStaffer(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String ldapEntry=request.getParameter("ldapUserCodes");
		String selectOU = request.getParameter("selectOU");
		String isNew = request.getParameter("isNew");
		User user = CurrentUser.get();
		if(null!=isNew && isNew.equals("New")){
    		V3xOrgMember member = new V3xOrgMember();
			bind(request, member);
            // 副岗
			String strSecondPostIds = request.getParameter("second_post_ids");
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
			
			member.setOrgAccountId(user.getLoginAccount());
				
			StaffInfo staffinfo = new StaffInfo();
			staffinfo.setIdIfNew();
			String wage = request.getParameter("recordWage");			

	
			if(null!=wage && !wage.equals("")){
				Float record_wage = Float.valueOf(wage);
				staffinfo.setRecord_wage(record_wage);
			}
			String workingTime = request.getParameter("workingTime");
			if(null!=workingTime && !workingTime.equals("")){
				staffinfo.setWorking_time(Integer.valueOf(workingTime));
			}
			bind(request, staffinfo);
			staffinfo.setOrg_member_id(member.getId());
			staffInfoManager.addStaffInfo(request,staffinfo,member);
			//~~~~~~~~~~~~~~~
//			添加个人菜单权限
            String securityIdsStr = request.getParameter("securityIds");
            if (null != securityIdsStr && securityIdsStr.length() > 0) {
                String[] securityIds = securityIdsStr.split(",");
                List<Long> securityIdsList = new ArrayList<Long>();
                for(String idStr : securityIds){
                    securityIdsList.add(Long.parseLong(idStr));
                }
                this.menuManager.saveMemberSecurity(member.getId(), member.getOrgAccountId(), securityIdsList);
            }
			//~~~~~~~~~~~~~~~`
            if(LDAPConfig.getInstance().getIsEnableLdap()&&SystemEnvironment.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD.name()))
            {
                try
                {
                    if(StringUtils.isBlank(ldapEntry))
                    {
                        event.newAddLdapPerson(member,selectOU);
                        appLogManager.insertLog(user, AppLogAction.LDAP_Account_Create,  member.getName(),selectOU);
                    }
                    else
                    {
                    	PrintWriter out=response.getWriter();
                        String[] errorResult=event.addMember(member,ldapEntry);
                        if(errorResult!=null&&errorResult.length>0)
                        {
                            String jsContent="";
                            for (int i = 0; i < errorResult.length; i++)
                            {
                                jsContent+=errorResult[i]+"\\n";
                            }
                            out.println("<script>");
                            out.println("alert('"+jsContent+"');");
                            out.println("</script>");
                            out.close();
                        }
                        appLogManager.insertLog(user, AppLogAction.LDAP_Account_Bing_Create,  member.getName(),ldapEntry);
                    }
                }
                catch (Exception e)
                {
                   LOG.error("ldap/ad 添加人员绑定不成功！",e);
                }
            }
            
            //推送模板配置。add by renw
            templeteConfigManager.pushNewOrgEntityTemplete4Member(V3xOrgEntity.ORGENT_TYPE_MEMBER,member.getId(),member.getId());
        	// 触发创建人员事件
			eventListener.addMember(member);
			indexManager.index(((IndexEnable)organizationServices).getIndexInfo(member.getId()));
			PrintWriter out;
			try {
				out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage(\"HRLang.hr_staffInfo_operationSuccessful_label\"))");
				out.println("</script>");
			} catch (IOException e) {
				LOG.error("", e);
			}
			//添加应用日志
			appLogManager.insertLog(user,  AppLogAction.Hr_NewStaffInfo,  user.getName(),  member.getName());
			return super.redirectModelAndView("/hrStaff.do?method=initInfoHome&staffId="+member.getId()+"&isReadOnly=ReadOnly&isManager=Manager","parent");
	    }else{
			Long staffid = RequestUtils.getLongParameter(request,"staffId");
			V3xOrgMember member = orgManagerDirect.getMemberById(staffid);
			PrintWriter	out;
			StaffInfo staffinfo = staffInfoManager.getStaffInfoById(staffid);


		    //事件调用前的member和之后的member
            V3xOrgMember memberBeforeUpdate = new V3xOrgMember();
            BeanUtils.copyProperties(memberBeforeUpdate, member);
            // 副岗
			String strSecondPostIds = request.getParameter("second_post_ids");
			String[] arrSecondPosts = strSecondPostIds.split(",");

			if (null != arrSecondPosts && arrSecondPosts.length > 0) {
				// 清除原副岗
				member.setSecond_post(new ArrayList<MemberPost>());
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
			member.setBirthday(null);
			bind(request, member);
			//记录登录名是否修改了
			boolean isLoginNameModifyed = false;
			  String  oldLoginName="";
				if(!memberBeforeUpdate.getLoginName().equals(member.getLoginName())){
					isLoginNameModifyed = true;
					oldLoginName=memberBeforeUpdate.getLoginName();
				}
			V3xOrgMember newMember = new V3xOrgMember();
			BeanUtils.copyProperties(newMember, member);
			if(staffinfo==null){
				StaffInfo staff = new StaffInfo();
				staff.setIdIfNew();
				String wage = request.getParameter("recordWage");
				if(null!=wage && !wage.equals("")){
					Float record_wage = Float.valueOf(wage);
					staff.setRecord_wage(record_wage);
				}
				String workingTime = request.getParameter("workingTime");
				if(null!=workingTime && !workingTime.equals("")){
					staff.setWorking_time(Integer.valueOf(workingTime));
				}
				bind(request, staff);
				staff.setOrg_member_id(staffid);
				staffInfoManager.updateStaffInfo(request,staff, member, true);	
			}
			else{
				String wage = request.getParameter("recordWage");
				if(null!=wage && !wage.equals("")){
					Float record_wage = Float.valueOf(wage);
					staffinfo.setRecord_wage(record_wage);
				}
				String workingTime = request.getParameter("workingTime");
				if(null!=workingTime && !workingTime.equals("")){
					staffinfo.setWorking_time(Integer.valueOf(workingTime));
				}
				bind(request, staffinfo);
				staffinfo.setOrg_member_id(staffid);
		    	staffInfoManager.updateStaffInfo(request,staffinfo, member, false);
			}
			//如果人员被停用则强制人员下线  取消代理信息
			if(memberBeforeUpdate.getEnabled()&&!member.getEnabled()){
				OnlineRecorder.moveToOffline(member.getLoginName(), LoginOfflineOperation.adminKickoff);
				agentIntercalateManager.cancelUserAgent(member.getId(),user);
			}
			//触发更新人员事件
			eventListener.updateMember(memberBeforeUpdate, newMember);
			eventListener.changePassword(null, newMember);
			if(LDAPConfig.getInstance().getIsEnableLdap()&&SystemEnvironment.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD.name()))
            {
                try
                {
                    List<V3xOrgMember>  memberList=null;
                    V3xOrgMember memberLdap=new V3xOrgMember();
                    BeanUtils.copyProperties(memberLdap, newMember);
                    if(isLoginNameModifyed)
                 {
                     memberList=new ArrayList<V3xOrgMember>();
                     memberLdap.setLoginName(oldLoginName);
                     memberList.add(memberLdap);
                     event.deleteAllBinding(orgManagerDirect, memberList);
                 }
                    String password = newMember.getPassword();
                  if(event.getLdapAdExUnitCode(member.getLoginName()).equals(ldapEntry)&&!isLoginNameModifyed&&password!=null
      					&&!StringUtils.isEmpty(password)
    					&&!password.equals(V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD))
                  {
                	  event.changePassword(memberBeforeUpdate, newMember);
                	  appLogManager.insertLog(user, AppLogAction.LDAP_PassWord_Update,  member.getName(),ldapEntry);
                  }else if(!event.getLdapAdExUnitCode(member.getLoginName()).equals(ldapEntry))
                  {
                	  String[] errorResult=event.addMember(member,ldapEntry);
                	  
                	  if(errorResult!=null&&errorResult.length>0)
                	  {
                		  out = response.getWriter();
                		  String jsContent="";
                		  for (int i = 0; i < errorResult.length; i++)
                		  {
                			  jsContent+=errorResult[i]+"\\n";
                		  }
                		  out.println("<script>");
                		  out.println("alert('"+jsContent+"');");
                		  out.println("</script>");
                		  LOG.debug("jsContent"+"alert('"+jsContent+"');");
                	  }
                	  appLogManager.insertLog(user, AppLogAction.LDAP_Account_Bing_Create,  member.getName(),ldapEntry);
                			if(password!=null
                					&&!StringUtils.isEmpty(password)
                					&&!password.equals(V3xOrgEntity.DEFAULT_INTERNAL_PASSWORD)){
                				event.changePassword(memberBeforeUpdate, member);
                				appLogManager.insertLog(user, AppLogAction.LDAP_PassWord_Update,  member.getName(),ldapEntry);
                			}
                  }  
                }
                catch (Exception e)
                {
                   LOG.error("ldap/ad 添加人员绑定不成功！",e);
                }
            }
			//~~~~~~~~~~~~~~
//			修改个人菜单权限
	        String securityIdsStr = request.getParameter("securityIds");
	        if (null != securityIdsStr && securityIdsStr.length() > 0) {
	            String[] securityIds = securityIdsStr.split(",");
	            List<Long> securityIdsList = new ArrayList<Long>();
	            for(String idStr : securityIds){
	                securityIdsList.add(Long.parseLong(idStr));
	            }
	            this.menuManager.saveMemberSecurity(member.getId(), member.getOrgAccountId(), securityIdsList);
	        }
			//~~~~~~~~~~~~~~
			
	        try{
				//如果调整部门，则追加新部门模板推送到个人首页
				if(isChangeOrgInfo(memberBeforeUpdate,newMember)){
					templeteConfigManager.pushNewOrgEntityTemplete4Member(V3xOrgEntity.ORGENT_TYPE_MEMBER, newMember.getId(), newMember.getId());
				}
			}catch(Exception e){
				logger.error("推送模板报错",e);
			}
	        
	    	try {
				out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage(\"HRLang.hr_staffInfo_operationSuccessful_label\"))");
				out.println("</script>");
			} catch (IOException e) {
				LOG.error("", e);
			}
			//添加应用日志
			appLogManager.insertLog(user,  AppLogAction.Hr_UpdateStaffInfo,  user.getName(),  member.getName());
			
			if(!member.getEnabled())
			{
				indexManager.deleteFromIndex(ApplicationCategoryEnum.organization, member.getId());
			}else{
				updateIndexManager.update(member.getId(),ApplicationCategoryEnum.organization.getKey());
			}
		}		
		String isManager = request.getParameter("isManager");
		String staId = RequestUtils.getStringParameter(request,"staffId");
		if(isManager!=null && !isManager.equals("")){
			//return super.redirectModelAndView("/hrStaff.do?method=initStaffListFrame","parent");//返回倒列表
			//添加load参数，弹出窗改为模态窗口后，跳转的页面就不跳到infohome.jsp
			return super.redirectModelAndView("/hrStaff.do?method=initInfoHome&staffId="+staId+"&isReadOnly=ReadOnly&isManager=Manager&load=1","parent");
		}else{
			return super.refreshWorkspace();
		}	
	}
	
	private boolean isChangeOrgInfo(V3xOrgMember memberBeforeUpdate,V3xOrgMember member){
		//判断部门 判断职务级别 判断副岗 判断岗位
		if(memberBeforeUpdate.getOrgDepartmentId().longValue() != member.getOrgDepartmentId().longValue()
				|| memberBeforeUpdate.getOrgLevelId().longValue() != member.getOrgLevelId().longValue()
				|| memberBeforeUpdate.getOrgPostId().longValue() != member.getOrgPostId().longValue()){
			return true;
		}
		List<MemberPost> secondPostBeforUpdate = memberBeforeUpdate.getSecond_post();
		List<MemberPost> secondPost = member.getSecond_post();
		if(secondPost.size() == secondPostBeforUpdate.size()){
			int i = 0;
			for(MemberPost post : secondPost){
				if(secondPostBeforUpdate.contains(post)){
					i++;
				}
			}
			if(i != secondPost.size()){
				return true;
			}
		}else{
			return true;
		}
		return false;
	}
	
	//HR员工信息维护-联系信息修改
	public ModelAndView updateContactInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Long staffid = RequestUtils.getLongParameter(request,"staffId");
        V3xOrgMember member = orgManagerDirect.getMemberById(staffid);
        User user = CurrentUser.get();
        //事件调用前的member和之后的member
        V3xOrgMember memberBeforeUpdate = new V3xOrgMember();
        BeanUtils.copyProperties(memberBeforeUpdate, member);
    	member.setTelNumber(request.getParameter("telNumber"));
    	member.setEmailAddress(request.getParameter("email"));
    	member.setProperty("officeNum", request.getParameter("telephone"));
    	
    	ContactInfo contact = staffInfoManager.getContactInfoById(staffid);
        if(contact == null){
        	ContactInfo contactInfo = new ContactInfo();
        	bind(request,contactInfo);
        	contactInfo.setMember_id(staffid);
        	staffInfoManager.addContactInfo(contactInfo,member);
        }
        else{
        	bind(request,contact);
        	contact.setMember_id(staffid);	
        	staffInfoManager.updateContactInfo(contact,member);
        }
        
     // 触发更新人员事件
		eventListener.updateMember(memberBeforeUpdate, member);
		//添加应用日志
		appLogManager.insertLog(user,  AppLogAction.Hr_UpdateStaffInfo,  user.getName(),  member.getName());

		return super.redirectModelAndView("/hrStaff.do?method=initContactInfo&staffId="+staffid+"&infoType=2&isReadOnly=ReadOnly");
	}
	
	public ModelAndView updateRelationship(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String staffid = request.getParameter("staffId");
		
		String ID = request.getParameter("id");
		if(ID==null || ID.equals("")){
			Relationship relationship = new Relationship();
			bind(request,relationship);
			relationship.setMember_id(Long.valueOf(staffid));
			staffInfoManager.addRelationship(relationship);
		}
		else{
			Relationship rela = staffInfoManager.getRelationshipById(Long.valueOf(ID));
			bind(request,rela);
			staffInfoManager.updateRelationship(rela);
		}
		
		String isManager = "";
		String Manager = request.getParameter("Manager");
		if(null!=Manager && Manager.equals("true")){
			isManager = "Manager";
		}
		User user = CurrentUser.get();
		V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(staffid));
		//添加应用日志
		appLogManager.insertLog(user,  AppLogAction.Hr_UpdateStaffInfo,  user.getName(),  member.getName());
		
		return super.redirectModelAndView("/hrStaff.do?method=initHome&infoType=3&staffId="+staffid+"&isManager="+isManager,"parent.parent");
	}
	
	public ModelAndView updateWorkRecord(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String staffid = request.getParameter("staffId");

		String ID = request.getParameter("id");
		if(null==ID || "".equals(ID)){
			WorkRecord workrecord = new WorkRecord();
			bind(request,workrecord);
			workrecord.setMember_id(Long.valueOf(staffid));
			staffInfoManager.addWorkRecord(workrecord);
		}
		else{
			WorkRecord work = staffInfoManager.getWorkRecordById(Long.valueOf(ID));
			bind(request,work);
			staffInfoManager.updateWorkRecord(work);
		}
		
		String isManager = "";
		String Manager = request.getParameter("Manager");
		if(null!=Manager && Manager.equals("true")){
			isManager = "Manager";
		}
		User user = CurrentUser.get();
		V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(staffid));
		//添加应用日志
		appLogManager.insertLog(user,  AppLogAction.Hr_UpdateStaffInfo,  user.getName(),  member.getName());

		return super.redirectModelAndView("/hrStaff.do?method=initHome&infoType=4&staffId="+staffid+"&isManager="+isManager,"parent.parent");
	}
	
	public ModelAndView updateEduExperience(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String staffid = request.getParameter("staffId");
	
		String ID = request.getParameter("id");
		if(ID==null || ID.equals("")){
			EduExperience eduExperience = new EduExperience();
			bind(request,eduExperience);
			eduExperience.setMember_id(Long.valueOf(staffid));
			staffInfoManager.addEduExperience(eduExperience);
			attachmentManager.create(ApplicationCategoryEnum.hr, eduExperience.getId()
					, eduExperience.getId(), request);
		}
		else{
			EduExperience edu = staffInfoManager.getEduExperienceById(Long.valueOf(ID));
			bind(request,edu);
			staffInfoManager.updateEduExperience(edu);
			attachmentManager.deleteByReference(edu.getId(), edu.getId());
			attachmentManager.create(ApplicationCategoryEnum.hr, edu.getId()
					, edu.getId(), request);
		}
		
		String isManager = "";
		String Manager = request.getParameter("Manager");
		if(null!=Manager && Manager.equals("true")){
			isManager = "Manager";
		}

		User user = CurrentUser.get();
		V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(staffid));
		//添加应用日志
		appLogManager.insertLog(user,  AppLogAction.Hr_UpdateStaffInfo,  user.getName(),  member.getName());

		return super.redirectModelAndView("/hrStaff.do?method=initHome&infoType=5&staffId="+staffid+"&isManager="+isManager,"parent.parent");
	}
	
	public ModelAndView updatePostChange(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String staffid = request.getParameter("staffId");
		
		String ID = request.getParameter("id");
		if(ID==null || ID.equals("")){
			PostChange postChange = new PostChange();
			bind(request,postChange);
			postChange.setMember_id(Long.valueOf(staffid));
			staffInfoManager.addPostChange(postChange);
		}
		else{
			PostChange post = staffInfoManager.getPostChangeById(Long.valueOf(ID));
			bind(request,post);
			staffInfoManager.updatePostChange(post);
		}
		String isManager = "";
		String Manager = request.getParameter("Manager");
		if(null!=Manager && Manager.equals("true")){
			isManager = "Manager";
		}
		
		User user = CurrentUser.get();
		V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(staffid));
		//添加应用日志
		appLogManager.insertLog(user,  AppLogAction.Hr_UpdateStaffInfo,  user.getName(),  member.getName());
		
		return super.redirectModelAndView("/hrStaff.do?method=initHome&infoType=6&staffId="+staffid+"&isManager="+isManager,"parent.parent");
	}
	
	public ModelAndView updateAssess(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String staffid = request.getParameter("staffId");
		
		String ID = request.getParameter("id");
		if(ID==null || ID.equals("")){
			Assess assess = new Assess();
			bind(request,assess);
			assess.setMember_id(Long.valueOf(staffid));
			staffInfoManager.addAssess(assess);
		}
		else{
			Assess ass = staffInfoManager.getAssessById(Long.valueOf(ID));
			bind(request,ass);
			staffInfoManager.updateAssess(ass);
		}
		String isManager = "";
		String Manager = request.getParameter("Manager");
		if(null!=Manager && Manager.equals("true")){
			isManager = "Manager";
		}
		
		User user = CurrentUser.get();
		V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(staffid));
		//添加应用日志
		appLogManager.insertLog(user,  AppLogAction.Hr_UpdateStaffInfo,  user.getName(),  member.getName());
		
		return super.redirectModelAndView("/hrStaff.do?method=initHome&infoType=7&staffId="+staffid+"&isManager="+isManager,"parent.parent");
	}
	
	public ModelAndView updateRewardsAndPunishment(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String staffid = request.getParameter("staffId");
		
		String ID = request.getParameter("id");
		if(ID==null || ID.equals("")){
			RewardsAndPunishment rewardsAndPunishment = new RewardsAndPunishment();
			bind(request,rewardsAndPunishment);
			rewardsAndPunishment.setMember_id(Long.valueOf(staffid));
			staffInfoManager.addRewardsAndPunishment(rewardsAndPunishment);
		}
		else{
			RewardsAndPunishment reward = staffInfoManager.getRewardsAndPunishmentById(Long.valueOf(ID));
			bind(request,reward);
			staffInfoManager.updateRewardsAndPunishment(reward);
		}
		String isManager = "";
		String Manager = request.getParameter("Manager");
		if(null!=Manager && Manager.equals("true")){
			isManager = "Manager";
		}
		
		User user = CurrentUser.get();
		V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(staffid));
		//添加应用日志
		appLogManager.insertLog(user,  AppLogAction.Hr_UpdateStaffInfo,  user.getName(),  member.getName());

		return super.redirectModelAndView("/hrStaff.do?method=initHome&infoType=8&staffId="+staffid+"&isManager="+isManager,"parent.parent");
	}
	
	public String[] getIds(String strIds) {
		if (null != strIds && !strIds.equals("")) {
			strIds = strIds.substring(0, strIds.lastIndexOf(','));
			String[] arrIds = strIds.split(",");
			return arrIds;
		}
		return null;
	}

	/**
	 * 删除人员--hr管理员
	 * @author lucx
	 *
	 */
	public ModelAndView deleteStaffer(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] membetIds = request.getParameter("staffIds").split(",");
		
		PrintWriter out = response.getWriter();
		User user = CurrentUser.get();
		//记录日志参数列表
		List<String[]> logLabels = new ArrayList<String[]>();
		for (String string : membetIds) {			
			Long ids = Long.parseLong(string);
			V3xOrgMember mem = orgManagerDirect.getMemberById(ids);
			mem.setIsDeleted(true);
		    if(LDAPConfig.getInstance().getIsEnableLdap()&&SystemEnvironment.hasPlugin(com.seeyon.v3x.product.ProductInfo.PluginNoMapper.LDAP_AD.name()))
		    {
		        try
		        {
		            List<V3xOrgMember>  memberList=new ArrayList<V3xOrgMember>();
		            memberList.add(mem);
		            LOG.debug(mem.getLoginName()+"delete HR Mem");
		            event.deleteAllBinding(orgManagerDirect, memberList);
		        }
		        catch (Exception e)
		        {
		            LOG.error("ldap_ad 删除人员绑定不成功！",e);
		        }
		    }
		    //强制此人员下线
		    OnlineRecorder.moveToOffline(mem.getLoginName(), LoginOfflineOperation.adminKickoff);
			staffInfoManager.deleteStaffInfo(ids);
			String[] delLog = new String[2];
			delLog[0] = user.getName();
			delLog[1] = mem.getName();
			logLabels.add(delLog);
			// 全部成功后触发删除人员事件		
			eventListener.deleteMember(mem);			
			
			indexManager.deleteFromIndex(ApplicationCategoryEnum.organization, ids);
		}
	
		try {			
			out = response.getWriter();
			out.println("<script>");		
			out.println("alert(\""+ResourceBundleUtil.getString("com.seeyon.v3x.hr.resource.i18n.HRResources", "hr.staffInfo.operationSuccessful.label")+"\");");
			out.println("</script>");			
			
		} catch (IOException e) {
			LOG.error("", e);
		}

		//添加应用日志
		appLogManager.insertLogs(user,  AppLogAction.Hr_DeleteStaffInfo,  logLabels);

		return super.redirectModelAndView("/hrStaff.do?method=initStaffInfoList");
	}
	
	public ModelAndView deleteRelationship(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] arrIDs = this.getIds(request.getParameter("ids"));
		String isManager = request.getParameter("isManager");
		String staffId   = request.getParameter("staffId");
		V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(staffId)) ; 
		//RequestUtils.getLongParameter(request,"staffId")
		if (null != arrIDs && arrIDs.length > 0) {
			for (String strID : arrIDs) {
				Long id = Long.parseLong(strID);
				staffInfoManager.deleteRelationship(id);
				operationlogManager.insertOplog(member.getOrgAccountId(),
						com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
						ApplicationCategoryEnum.hr, 
						"hr.staffInfo.other.delete.label",
						"hr.staffInfo.Relationship.delete.label", 
						member.getName());
			}
		}
		
		User user = CurrentUser.get();
		//添加应用日志
		appLogManager.insertLog(user,  AppLogAction.Hr_UpdateStaffInfo,  user.getName(),  member.getName());

		return super.redirectModelAndView("/hrStaff.do?method=initHome&infoType=3&staffId="+staffId+"&isManager="+isManager, "parent");
	}
	
	public ModelAndView deleteWorkRecord(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] arrIDs = this.getIds(request.getParameter("ids"));
		String isManager = request.getParameter("isManager");
		String staffId   = request.getParameter("staffId");
		V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(staffId)) ; 
		if (null != arrIDs && arrIDs.length > 0) {
			for (String strID : arrIDs) {
				Long id = Long.parseLong(strID);
				staffInfoManager.deleteWorkRecord(id);
				operationlogManager.insertOplog(member.getOrgAccountId(),
						com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
						ApplicationCategoryEnum.hr, 
						"hr.staffInfo.other.delete.label",
						"hr.staffInfo.WorkRecord.delete.label", 
						member.getName());
			}
		}	
		
		User user = CurrentUser.get();
		//添加应用日志
		appLogManager.insertLog(user,  AppLogAction.Hr_UpdateStaffInfo,  user.getName(),  member.getName());
				
		return super.redirectModelAndView("/hrStaff.do?method=initHome&infoType=4&staffId="+staffId+"&isManager="+isManager, "parent");
	}
	
	public ModelAndView deleteEduExperience(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] arrIDs = this.getIds(request.getParameter("ids"));
		String isManager = request.getParameter("isManager");
		String staffId   = request.getParameter("staffId");
		V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(staffId)) ;
		if (null != arrIDs && arrIDs.length > 0) {
			for (String strID : arrIDs) {
				Long id = Long.parseLong(strID);
				staffInfoManager.deleteEduExperience(id);
				attachmentManager.deleteByReference(id);
				operationlogManager.insertOplog(member.getOrgAccountId(),
						com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
						ApplicationCategoryEnum.hr, 
						"hr.staffInfo.other.delete.label",
						"hr.staffInfo.EduExperience.delete.label", 
						member.getName());
			}
		}
		
		User user = CurrentUser.get();
		//添加应用日志
		appLogManager.insertLog(user,  AppLogAction.Hr_UpdateStaffInfo,  user.getName(),  member.getName());

		return super.redirectModelAndView("/hrStaff.do?method=initHome&infoType=5&staffId="+staffId+"&isManager="+isManager, "parent");
	}
	
	public ModelAndView deletePostChange(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] arrIDs = this.getIds(request.getParameter("ids"));
		String isManager = request.getParameter("isManager");
		String staffId   = request.getParameter("staffId");
		V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(staffId)) ;
		if (null != arrIDs && arrIDs.length > 0) {
			for (String strID : arrIDs) {
				Long id = Long.parseLong(strID);
				staffInfoManager.deletePostChange(id);
				operationlogManager.insertOplog(member.getOrgAccountId(),
						com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
						ApplicationCategoryEnum.hr, 
						"hr.staffInfo.other.delete.label",
						"hr.staffInfo.PostChange.delete.label", 
						member.getName());
			}
		}
		
		User user = CurrentUser.get();
		//添加应用日志
		appLogManager.insertLog(user,  AppLogAction.Hr_UpdateStaffInfo,  user.getName(),  member.getName());

		return super.redirectModelAndView("/hrStaff.do?method=initHome&infoType=6&staffId="+staffId+"&isManager="+isManager, "parent");
	}
	
	public ModelAndView deleteAssess(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] arrIDs = this.getIds(request.getParameter("ids"));
		String isManager = request.getParameter("isManager");
		String staffId   = request.getParameter("staffId");
		V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(staffId)) ;
		if (null != arrIDs && arrIDs.length > 0) {
			for (String strID : arrIDs) {
				Long id = Long.parseLong(strID);
				staffInfoManager.deleteAssess(id);
				operationlogManager.insertOplog(member.getOrgAccountId(),
						com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
						ApplicationCategoryEnum.hr, 
						"hr.staffInfo.other.delete.label",
						"hr.staffInfo.Assess.delete.label", 
						member.getName());
			}
		}
		
		User user = CurrentUser.get();
		//添加应用日志
		appLogManager.insertLog(user,  AppLogAction.Hr_UpdateStaffInfo,  user.getName(),  member.getName());
		
		return super.redirectModelAndView("/hrStaff.do?method=initHome&infoType=7&staffId="+staffId+"&isManager="+isManager, "parent");
	}
	
	public ModelAndView deleteRewardsAndPunishment(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String[] arrIDs = this.getIds(request.getParameter("ids"));
		String isManager = request.getParameter("isManager");
		String staffId   = request.getParameter("staffId");
		V3xOrgMember member = orgManagerDirect.getMemberById(Long.parseLong(staffId)) ;
		if (null != arrIDs && arrIDs.length > 0) {
			for (String strID : arrIDs) {
				Long id = Long.parseLong(strID);
				staffInfoManager.deleteRewardsAndPunishment(id);
				operationlogManager.insertOplog(member.getOrgAccountId(),
						com.seeyon.v3x.hr.util.Constants.MODULE_STAFF,
						ApplicationCategoryEnum.hr, 
						"hr.staffInfo.other.delete.label",
						"hr.staffInfo.RewardsAndPunishment.delete.label", 
						member.getName());
			}
		}
		
		User user = CurrentUser.get();
		//添加应用日志
		appLogManager.insertLog(user,  AppLogAction.Hr_UpdateStaffInfo,  user.getName(),  member.getName());
		
		return super.redirectModelAndView("/hrStaff.do?method=initHome&infoType=8&staffId="+staffId+"&isManager="+isManager, "parent");
	}

	/**
	 * 配置页签的显示
	 */
	public ModelAndView userDefinedHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("hr/staffInfo/userDefinedHome");
	}
	
	/**
	 * 配置页签的显示
	 */
	@SuppressWarnings("unchecked")
	public ModelAndView initUserDefinedPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("hr/staffInfo/userDefinedPage");
		Long page_id = RequestUtils.getLongParameter(request, "page_id");
		Page page = this.userDefinedManager.getPageById(page_id);
		if (page != null) {
			mav.addObject("repair", page.getRepair());
		}
		List<PageLabel> pageLabels = this.userDefinedManager.getPageLabelByPageId(page_id);
		String pageLabelName_zh = "";
		String pageLabelName_en = "";
		for (PageLabel label : pageLabels) {
			if (label.getLanguage().equals("zh_CN")) {
				pageLabelName_zh = label.getPageLabelValue();
			} else {
				pageLabelName_en = label.getPageLabelValue();
			}
		}

		String staffid = request.getParameter("staffId");
		Long staffId = null;
		if (Strings.isNotBlank(staffid)) {
			staffId = NumberUtils.toLong(staffid);
		} else {
			staffId = CurrentUser.get().getId();
		}
		V3xOrgMember staff = orgManagerDirect.getMemberById(staffId);

		List<WebProperty> webProperties = new ArrayList<WebProperty>();
		List<WebLabel> webLabels_zh = new ArrayList<WebLabel>();
		List<WebLabel> webLabels_en = new ArrayList<WebLabel>();
		List<Integer> propertyTypes = new ArrayList<Integer>();
		List<Map<Long, Repository>> propertyValues = new ArrayList<Map<Long, Repository>>();
		List<PageProperty> properties = this.userDefinedManager.getPropertyByPageId(page_id);
		String ids = request.getParameter("ids");
		List<Long> reposityIds = FormBizConfigUtils.parseStr2Ids(ids);
		int i = 0;

		if (CollectionUtils.isNotEmpty(properties)) {
			for (PageProperty property : properties) {
				Long propertyId = property.getId();
				int propertyType = property.getType().intValue();
				propertyTypes.add(propertyType);
				
				WebProperty webProperty = new WebProperty();
				webProperty.setPage_id(page_id);
				webProperty.setProperty_id(propertyId);
				webProperty.setPropertyType(propertyType);
				webProperty.setPageName_zh(pageLabelName_zh);
				webProperty.setPageName_en(pageLabelName_en);
				if (property.getNot_null() == PagePropertyConstant.Page_Property_NotNull) {
					webProperty.setNot_null("no");
				} else {
					webProperty.setNot_null("yes");
				}
				List<PropertyLabel> labels = this.userDefinedManager.getPropertyLabelByPropertyId(propertyId);
				for (PropertyLabel label : labels) {
					WebLabel webLabel = new WebLabel();
					String value = label.getPropertyLabelValue();
					if (label.getLanguage().equals("zh_CN")) {
						webProperty.setLabelName_zh(value);
						webLabel.setLabelName_zh(value);
						webLabels_zh.add(webLabel);
					} else {
						webProperty.setLabelName_en(value);
						webLabel.setLavelName_en(value);
						webLabels_en.add(webLabel);
					}
				}
				
				if (CollectionUtils.isNotEmpty(reposityIds)) {
					Long reposityId = reposityIds.get(i);
					Repository repository = this.userDefinedManager.getRepositoryById(reposityId);
					webProperty.setRepository_id(reposityId);
					if (property.getType() == PagePropertyConstant.Page_Property_Integer) {
						webProperty.setF1(repository.getF1());
					} else if (property.getType() == PagePropertyConstant.Page_Property_Float) {
						webProperty.setF2(repository.getF2());
					} else if (property.getType() == PagePropertyConstant.Page_Property_Date) {
						webProperty.setF3(repository.getF3());
					} else if (property.getType() == PagePropertyConstant.Page_Property_Varchar) {
						webProperty.setF4(repository.getF4());
					} else {
						webProperty.setF5(repository.getF5());
					}
					i++;
				}
				webProperties.add(webProperty);
				
				Map<Long, Repository> repositories = this.userDefinedManager.getRepositoryByMemberIdAndPropertyIdAndPageId(staffId, propertyId, page_id);
				propertyValues.add(repositories);
			}
		}
		
		mav.addObject("staffId", staffId);
		mav.addObject("staff", staff);
		mav.addObject("page_id", page_id);
		mav.addObject("pageLabelName_zh", pageLabelName_zh);
		mav.addObject("pageLabelName_en", pageLabelName_en);
		mav.addObject("webProperties", webProperties);
		mav.addObject("webLabels_zh", webLabels_zh);
		mav.addObject("webLabels_en", webLabels_en);
		if (CollectionUtils.isNotEmpty(webLabels_zh)) {
			mav.addObject("label", "label");
		}
		mav.addObject("propertyTypes", propertyTypes);
		mav.addObject("propertyValues", propertyValues);
		mav.addObject("ids", ids);

		mav.addObject("operation", request.getParameter("operation"));
		mav.addObject("show", "Show".equals(request.getParameter("isShow")));
		mav.addObject("save", "Save".equals(request.getParameter("isSave")));
		mav.addObject("dis", RequestUtils.getBooleanParameter(request, "dis", true));
		return mav;
	}
	
	/**
	 * 在配置页签上添加、修改信息项数据
	 */
	public ModelAndView addUserDefined(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String operation = request.getParameter("operation");
		Long member_id = RequestUtils.getLongParameter(request, "staffId");
		Long page_id = RequestUtils.getLongParameter(request, "page_id");
		List<Repository> repositorys = new ArrayList<Repository>();
		if ("Save".equals(operation)) {
			List<PageProperty> pageProperties = userDefinedManager.getPropertyByPageId(page_id);
			Long operationId = UUIDLong.longUUID();
			for (PageProperty property : pageProperties) {
				Repository repository = new Repository();
				repository.setIdIfNew();
				repository.setMemberId(member_id);
				repository.setPage_id(page_id);
				repository.setProperty_id(property.getId());
				repository.setOperation_id(operationId);
				Date createTime = new Date();
				repository.setCreateTime(createTime);

				int propertyType = property.getType().intValue();
				String propertyValue = request.getParameter(String.valueOf(property.getId()));
				SalaryUserDefinedHelper.addRepository(propertyType, propertyValue, repository);
				
				repositorys.add(repository);
			}
			this.userDefinedManager.addAllRepository(repositorys);
		} else if ("Update".equals(operation)) {
			List<Long> repositoryIds = FormBizConfigUtils.parseStr2Ids(request, "ids");
			for (Long repositoryId : repositoryIds) {
				Repository repository = this.userDefinedManager.getRepositoryById(repositoryId);
				
				int propertyType = RequestUtils.getIntParameter(request, repositoryId + "_Type");
				String propertyValue = request.getParameter(String.valueOf(repository.getId()));
				SalaryUserDefinedHelper.updateRepository(propertyType, propertyValue, repository);
				
				repositorys.add(repository);
			}
			this.userDefinedManager.updateAllRepository(repositorys);
		}
		return super.redirectModelAndView("/hrStaff.do?method=userDefinedHome&staffId=" + member_id + "&page_id=" + page_id, "parent");
	}
	
	/**
	 * 在配置页签上删除信息项数据
	 */
	public ModelAndView detoryUserDefined(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Long> repositoryIds = FormBizConfigUtils.parseStr2Ids(request, "ids");
		this.userDefinedManager.deleteRepositoryByIds(repositoryIds);
		return super.redirectModelAndView("/hrStaff.do?method=userDefinedHome&staffId=" + RequestUtils.getLongParameter(request, "staffId") + "&page_id=" + RequestUtils.getLongParameter(request, "page_id"), "parent");
	}

	public AttachmentManager getAttachmentManager() {
		return attachmentManager;
	}

	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public ProjectManager getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	public OperationlogManager getOperationlogManager() {
		return operationlogManager;
	}

	public AppLogManager getAppLogManager() {
		return appLogManager;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}

	public void setAgentIntercalateManager(AgentIntercalateManager agentIntercalateManager) {
		this.agentIntercalateManager = agentIntercalateManager;
	}
	
}