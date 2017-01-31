/**
 * $Id: AddressBookController.java,v 1.197 2011/11/01 03:13:25 renhy Exp $
 `* 
 * Licensed to the UFIDA
 */
package com.seeyon.v3x.addressbook.controller;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.addressbook.domain.AddressBookMember;
import com.seeyon.v3x.addressbook.domain.AddressBookTeam;
import com.seeyon.v3x.addressbook.domain.Csv;
import com.seeyon.v3x.addressbook.domain.VCard;
import com.seeyon.v3x.addressbook.manager.AddressBookManager;
import com.seeyon.v3x.addressbook.webmodel.WebWithPropV3xOrgMember;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.V3XFile;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.flag.BrowserFlag;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ExportHelper;
import com.seeyon.v3x.common.web.util.ListSearchHelper;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.event.EventDispatcher;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.hr.domain.ContactInfo;
import com.seeyon.v3x.hr.domain.StaffInfo;
import com.seeyon.v3x.hr.manager.StaffInfoManager;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.hr.webmodel.WebStaffInfo;
import com.seeyon.v3x.online.util.OuterWorkerAuthUtil;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.CompareSortEntity;
import com.seeyon.v3x.organization.domain.MemberHelper;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgDutyLevel;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgRelationship;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.event.UpdateTeamEvent;
import com.seeyon.v3x.organization.inexportutil.DataUtil;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgDepartment;
import com.seeyon.v3x.organization.webmodel.WebV3xOrgTeam;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.UniqueList;
import com.seeyon.v3x.webmail.domain.MailBoxCfg;
import com.seeyon.v3x.webmail.manager.MailBoxManager;

/**
 * 
 * <p/> Title: 通讯录<控制器>
 * </p>
 * <p/> Description: 通讯录<控制器>
 * </p>
 * <p/> Date: 2007-5-25
 * </p>
 * 
 * @author paul(qdlake@gmail.com) TODO 事务性的处理过程将移入到Manager
 */
public class AddressBookController extends BaseController {
	private transient static final Log log = LogFactory
			.getLog(AddressBookController.class);
	private OrgManager orgManager;
	private OrgManagerDirect orgManagerDirect;
	private StaffInfoManager staffInfoManager;
	private AddressBookManager addressBookManager;
	private FileToExcelManager fileToExcelManager;
	private SystemConfig systemConfig;
	private FileManager fileManager;
	private String jsonView;
	private String clientAbortExceptionName = "ClientAbortException";
	
	private String contentTypeCharset = "GBK";
	private MetadataManager metadataManager;
	
	

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	/**
	 * 用户关闭下载窗口时候，有servlet容器抛出的异常
	 * @param clientAbortExceptionName 类的simapleName，如<code>ClientAbortException</code>
	 */
	public void setClientAbortExceptionName(String clientAbortExceptionName) {
		this.clientAbortExceptionName = clientAbortExceptionName;
	}
	public String getJsonView() {
		return jsonView;
	}
	public void setJsonView(String jsonView) {
		this.jsonView = jsonView;
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

	public AddressBookManager getAddressBookManager() {
		return addressBookManager;
	}

	public void setAddressBookManager(AddressBookManager addressBookManager) {
		this.addressBookManager = addressBookManager;
	}

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}
	/*
	 * （非 Javadoc）
	 * 
	 * @see com.seeyon.v3x.common.web.BaseController#index(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO 自动生成方法存根
		return null;
	}

	public ModelAndView selectPrivatedPeople(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(jspView(2, "selectPrivatedPeople"));
		User user = CurrentUser.get();
		List<AddressBookMember> members = addressBookManager
				.getMembersByCreatorId(user.getId());
		mav.addObject("members", members);
		return mav;
	}
	
	/**
	 * 根据addressbookType参数来判断是员工通讯录/外部联系人
	 * 如果root==true,则不判断addressbookType，直接返回根目录+page
	 * 如果root==false,则根据addressbookType参数来判断是员工通讯录/外部联系人,返回根目录+public/private+page
	 * 
	 * @param addressbookType
	 *            通讯录类型
	 * @param page
	 *            显示页面名称
	 * @param root
	 *            表示是否根目录
	 * @return 显示页面上一层路径(格式如下：addressbook/public/)
	 */
	private String jspView(int addressbookType, String page, boolean root) {
		String path = "addressbook/home";
		if (root)
			path = "addressbook/" + page;
		else {
			if (1 == addressbookType || 3 == addressbookType || 4 == addressbookType) //员工通讯录
				path ="addressbook/public/" + page;
			else if (2 == addressbookType) //私人通讯录
				path = "addressbook/private/" + page;
		}
		return path;
	}
	
	/**
	 * 页面跳转
	 * @author lucx
	 *
	 */
	private String myJspView(int addressbookType, String page) {
		String path = "";
		
			if (1 == addressbookType) //员工通讯录
				path = "addressbook/public/" + page;
			else if(3 == addressbookType)
				path = "addressbook/public/listSysMember";
			else if(4 == addressbookType)
                path = "addressbook/public/listOwnGroup";
			else if (2 == addressbookType) //私人通讯录
				path = "addressbook/private/listMembers";
		
		return path;
	}

	private String jspView(int addressbookType, String page) {
		return jspView(addressbookType, page, false);
	}

	private String jspView(String page) {
		return jspView(1, page, true);
	}

	/**
	 * 分页 
	 * @author lucx
	 *
	 */
	private <T> List<T> pagenate(List<T> list) {
		return FormBizConfigUtils.pagenate(list);
//		if (null == list || list.size() == 0)
//			return new ArrayList<T>(0);
//		Integer first = Pagination.getFirstResult();
//		Integer pageSize = Pagination.getMaxResults();
//		Pagination.setRowCount(list.size());
//		List<T> subList = null;
//		if (first + pageSize > list.size()) {
//			subList = list.subList(first, list.size());
//		} else {
//			subList = list.subList(first, first + pageSize);
//		}
//		return subList;
	}
	
	/**
	 * 获取人的附加信息---办公电话扩展属性从member取
	 * @author lucx
	 *
	 */
	private List<WebWithPropV3xOrgMember> parse(V3xOrgMember member,Long accountId,Long depId) throws Exception {
		
		List<WebWithPropV3xOrgMember> listWebMember = translateV3xOrgMemberToWebV3xOrgMember(member,accountId,depId);
		
//		~~~~~~~~~~~~~办公电话的扩展属性
		if(listWebMember!=null && !listWebMember.isEmpty()){
			for(WebWithPropV3xOrgMember web : listWebMember){
				String offNum = member.getProperty("officeNum");
				web.setFamilyPhone(offNum);
				web.setMobilePhone(member.getTelNumber());
				web.setEmail(member.getEmailAddress());
				web.setDescription(member.getDescription()) ;
			}
		}
		return listWebMember;
	}
	
	/**
	 * 分别取人的属性添加到list里
	 * @author lucx
	 *
	 */
	private List<WebWithPropV3xOrgMember> translateList(List<V3xOrgMember> members,int type) throws Exception {
		Map<Long, List<ConcurrentPost>>  map = orgManager.getConcurentPostsByMemberId(CurrentUser.get().getAccountId(), CurrentUser.get().getId());
		User user = CurrentUser.get();
		V3xOrgMember currentMember = orgManager.getMemberById(user.getId());
		List<V3xOrgEntity> outerRight = null;
		if(!user.isInternal()){
			outerRight = orgManager.getExternalMemberWorkScope(currentMember.getId(),false);
		}
		List<WebWithPropV3xOrgMember> results = new ArrayList<WebWithPropV3xOrgMember>();
		if (null != members && members.size() > 0) {
			for (V3xOrgMember member : members) {
				if(type==4 || checkLevelScope(member , currentMember,outerRight, null)){//检测 个人组不需要检测
					Set<Long> set = map.keySet();
					boolean isPartTime = false;
					if(set!=null){
						for(Long l : set){
							V3xOrgDepartment dep = orgManager.getDepartmentById(l);
							if(orgManager.isInDomain(l, member.getId()) && !member.getOrgAccountId().equals(dep.getOrgAccountId())){
								results.addAll(parse(member,CurrentUser.get().getAccountId(),l));
								isPartTime = true;
							}
						}
					}
					if(!isPartTime){//非兼职的人员
						results.addAll(parse(member,null,null));
					}
				}
			}
		}
		return results;
	}
	
	/**
	 * 分别取人的属性添加到list里
	 * @author lucx
	 *
	 */
	private List<WebWithPropV3xOrgMember> parseList(List<V3xOrgMember> members,Long accountId,Long depId) throws Exception {
		List<WebWithPropV3xOrgMember> results = new ArrayList<WebWithPropV3xOrgMember>();
		if (null != members && members.size() > 0) {
			for (V3xOrgMember member : members) {
				results.addAll(parse(member,accountId,depId));
			}
		}
		return results;
	}

	/**
	 * 取人的部门岗位职务的方法
	 * @param member 兼职人员
	 * @param webMember 
	 * @param accountId 兼职的单位ID
	 * @param depId 兼职的部门ID
	 * @throws BusinessException
	 */
	private List<WebWithPropV3xOrgMember> translateV3xOrgMemberToWebV3xOrgMember(V3xOrgMember member,Long accountId,Long depId) throws BusinessException {
		
		List<WebWithPropV3xOrgMember> listWebMember = new ArrayList<WebWithPropV3xOrgMember>();
		long deptId = member.getOrgDepartmentId();
		long levelId = member.getOrgLevelId();
		long postId = member.getOrgPostId();
		if(depId != null && deptId != depId){
			deptId = depId;
			List<MemberPost> secondPost = member.getSecondPostByDeptId(depId);
			for(MemberPost post:secondPost){
				if(post.getDepId().equals(deptId)){
					postId = post.getPostId();
				}
			}
		}
		if(accountId != null && !member.getOrgAccountId().equals(accountId)){
			Map<Long, List<ConcurrentPost>>  map = orgManager.getConcurentPostsByMemberId(accountId, member.getId());
			if(map!=null && !map.isEmpty()){
				if(depId == null) depId = map.keySet().iterator().next();// 2012-04-24 AEIGHT-5137 by lilong 只选择兼职单位根时取兼职的第一个信息
				List<ConcurrentPost> list = map.get(depId);
				if(list!=null && !list.isEmpty()){
					for(ConcurrentPost p : list){
						if(p.getCntAccountId().equals(accountId) && p.getCntDepId().equals(depId)&&p.getMemberId().equals(member.getId())){
							Long  deId = p.getCntDepId();
							Long posId = p.getCntPostId();
							Long levId = p.getCntLevelId();
							deptId = deId!=null?deId.longValue():0L;
							postId = posId!=null?posId.longValue():0L;
							levelId = levId!=null?levId.longValue():0L;
							WebWithPropV3xOrgMember webMembernew = setWebMemberPLD(member,postId,levelId,deptId);
							listWebMember.add(webMembernew);
							break;//遇到第一个兼职就跳出来，只显示在这个部门下的第一个兼职信息 2012-04-24 AEIGHT-5137 by lilong 
						}
					}
				}else{
					WebWithPropV3xOrgMember webMembernew = setWebMemberPLD(member,postId,levelId,deptId);
					listWebMember.add(webMembernew);
				}
			}else{
				WebWithPropV3xOrgMember webMembernew = setWebMemberPLD(member,postId,levelId,deptId);
				listWebMember.add(webMembernew);
			}
		}else{
			WebWithPropV3xOrgMember webMembernew = setWebMemberPLD(member,postId,levelId,deptId);
			listWebMember.add(webMembernew);
		}
		
		return listWebMember;
	}
	
	
	private WebWithPropV3xOrgMember setWebMemberPLD(V3xOrgMember member,Long postId,Long levelId,Long deId) throws BusinessException{
		WebWithPropV3xOrgMember webMember = new WebWithPropV3xOrgMember();
		
		V3xOrgDepartment dept = orgManager.getDepartmentById(deId);
		if (dept != null) {
			webMember.setDepartmentName(dept.getName());
		}

		V3xOrgLevel level = orgManager.getLevelById(levelId);
		if (null != level) {
			webMember.setLevelId(levelId);
			webMember.setLevelName(level.getName());
		} else {
			webMember.setLevelName(member.getProperty("levelName"));
		}

		V3xOrgPost post = orgManager.getPostById(postId);
		if (null != post) {
			if(postId == -1){
				webMember.setPostName(member.getProperty("postName"));
			} else {
				webMember.setPostId(postId);
				webMember.setPostName(post.getName());
			}
		}
	    
		webMember.setV3xOrgMember(member);
		webMember.setMemberName(member.getName());
		return webMember;
	}
	
	/**
	 * 根据传组list 返回解析后的组成员
	 * @author lucx
	 *
	 */
	private List<WebV3xOrgTeam> translateV3xOrgTeam(List<V3xOrgTeam> teams)
			throws BusinessException {
		List<WebV3xOrgTeam> result = new ArrayList<WebV3xOrgTeam>();
		if (null == teams || teams.size() == 0)
			return result;
		for (V3xOrgTeam team : teams) {
			WebV3xOrgTeam webTeam = new WebV3xOrgTeam();
			webTeam.setV3xOrgTeam(team);
			// 取得组的部门名称
			V3xOrgDepartment dept = orgManager.getDepartmentById(team
					.getDepId());
			if (null != dept) {
				webTeam.setDeptName(dept.getName());
			}
			// 取得组的成员
			List<V3xOrgMember> teamMembers = orgManager.getTeamMember(team
					.getId());

			if (null != teamMembers) {
				StringBuffer strBuffNames = new StringBuffer();
				StringBuffer strBuffIDs = new StringBuffer();
				for (V3xOrgMember member : teamMembers) {
					strBuffNames.append(member.getName());
					strBuffNames.append(",");
					strBuffIDs.append(member.getId());
					strBuffIDs.append(",");
				}
				if (strBuffIDs.length() > 0) {
					String memNames = strBuffNames.toString();
					memNames = memNames.substring(0, memNames.lastIndexOf(","));
					String memIDs = strBuffIDs.toString();
					memIDs = memIDs.substring(0, memIDs.lastIndexOf(","));
					webTeam.setMemberIDs(memIDs);
				}
			}

			result.add(webTeam);
		}
		return result;
	}

	/**
	 * 转换类型
	 * @author lucx
	 *
	 */
	private List<Long> toLongList(String mIdStr) throws BusinessException {
		List<Long> mIds = new ArrayList<Long>();
		if (null != mIdStr && !mIdStr.equals("")) {
			String[] memIds = mIdStr.split(",");
			for (String strId : memIds) {
				if (null == strId || "".equals(strId))
					continue;
				Long id = Long.parseLong(strId);
				mIds.add(id);
			}
		}
		return mIds;
	}

	/**
	 * 进去通讯录的框架
	 * @author lucx
	 *
	 */
	public ModelAndView home(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int addressbookType = RequestUtils.getIntParameter(request,
				"addressbookType", 1);
		ModelAndView mav = new ModelAndView(jspView("home"));
		mav.addObject("addressbookType", addressbookType);
		return mav;
	}
	
	/**
	 * 发送Mail
	 * @author lucx
	 *
	 */
	public ModelAndView sendMail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		MailBoxCfg mbc = MailBoxManager.findUserDefaultMbc(String.valueOf(user.getId()));
		if(mbc == null){
			ModelAndView mav = new ModelAndView("webmail/error");
			mav.addObject("errorMsg", "2");
			mav.addObject("url", "?method=list&jsp=set");
			return mav;
		}
		ModelAndView mav = new ModelAndView("webmail/new/send");
		String mailAdd = request.getParameter("mailAdd");
		mav.addObject("mailAdd", mailAdd);
		mav.addObject("originalAttsNeedClone", "true");
		return mav;
	}

	/**
	 * 框架中转
	 * @author lucx
	 *
	 */
	public ModelAndView homeEntry(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(jspView("homeEntry"));
		return mav;
	}
	
	/**
	 * 进入通讯录显示页签的方法
	 * @author lucx
	 *
	 */
	public ModelAndView initSpace(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int addressbookType = RequestUtils.getIntParameter(request,
				"addressbookType", 1);
		ModelAndView mav = new ModelAndView(jspView("space"));
		mav.addObject("addressbookType", Integer.valueOf(addressbookType));
		return mav;
	}

	/**
	 * 根据列表查询条件过滤系统组人员集合
	 * 
	 * @param members
	 * @return
	 */
	private Collection<WebWithPropV3xOrgMember> filtWebWithPropV3xOrgMembers(Collection<WebWithPropV3xOrgMember> members) throws Exception {
		User user = CurrentUser.get();
		String expressionType = ListSearchHelper.getExpressionType();
		if (expressionType != null) {
			V3xOrgMember currentMember = orgManager.getMemberById(user.getId());
//			List<V3xOrgEntity> outerRight = null;
//			if(!user.isInternal()){
//				outerRight = orgManager.getExternalMemberWorkScope(user.getId(),false);
//			}
			
			Iterator<WebWithPropV3xOrgMember> it = members.iterator();
			while (it.hasNext()) {
				WebWithPropV3xOrgMember member = it.next();
				
				if (!Functions.checkLevelScope(currentMember.getId(), member.getV3xOrgMember().getId())) {
					it.remove();
					continue;
				}
				
				if ("name".equals(expressionType)) { // 姓名，模糊匹配
					if (member.getMemberName() == null || member.getMemberName().indexOf(
							ListSearchHelper.getExpressionValueString()) < 0) {
						it.remove();
					}
				} else if ("post".equals(expressionType)) { // 岗位，多值
					boolean matchFlag = false;
					for (Long validPostId : ListSearchHelper
							.getExpressionValueLongArray(",")) {
						if (validPostId.equals(member.getPostId())) {
							matchFlag = true;
							break;
						}
					}
					if (!matchFlag) {
						it.remove();
					}
				} else if ("level".equals(expressionType)) { // 职务级别，单值
					if (!ListSearchHelper.getExpressionValueLong().equals(
							member.getLevelId())) {
						it.remove();
					}
				} else if ("mobilePhone".equals(expressionType)) { // 手机号码，模糊匹配
					if (member.getMobilePhone() == null || member.getMobilePhone().indexOf(
							ListSearchHelper.getExpressionValueString()) < 0) {
						it.remove();
					}
				} else if ("companyPhone".equals(expressionType)) {// 办公电话，模糊匹配
					String companyPhone = member.getFamilyPhone(); // TODO: 为什么办公电话在familyPhone中？
					if (companyPhone == null || companyPhone.indexOf(
							ListSearchHelper.getExpressionValueString()) < 0) {
						it.remove();
					}
				}
			}
		}
		return members;
	}

	/**
	 * 根据列表查询条件过滤外部联系人集合
	 * 
	 * @param members
	 * @return
	 */
	private Collection<AddressBookMember> filtAddressBookMembers(
			Collection<AddressBookMember> members) {
		String expressionType = ListSearchHelper.getExpressionType();
		if (expressionType != null) {
			Iterator<AddressBookMember> it = members.iterator();
			while (it.hasNext()) {
				AddressBookMember member = it.next();
				if ("name".equals(expressionType)) { // 姓名，模糊匹配
					if (member.getName() == null || member.getName().indexOf(
							ListSearchHelper.getExpressionValueString()) < 0) {
						it.remove();
					}
				} else if ("abPost".equals(expressionType)) { // 岗位，模糊匹配
					if (member.getCompanyPost() == null || member.getCompanyPost().indexOf(
							ListSearchHelper.getExpressionValueString()) < 0) {
						it.remove();
					}
				} else if ("abLevel".equals(expressionType)) { // 职务级别，模糊匹配
					if (member.getCompanyLevel() == null || member.getCompanyLevel().indexOf(
							ListSearchHelper.getExpressionValueString()) < 0) {
						it.remove();
					}
				} else if ("mobilePhone".equals(expressionType)) { // 手机号码，模糊匹配
					if (member.getMobilePhone() == null || member.getMobilePhone().indexOf(
							ListSearchHelper.getExpressionValueString()) < 0) {
						it.remove();
					}
				} else if ("companyPhone".equals(expressionType)) {// 办公电话，模糊匹配
					if (member.getCompanyPhone() == null || member.getCompanyPhone().indexOf(
							ListSearchHelper.getExpressionValueString()) < 0) {
						it.remove();
					}
				}
			}
		}
		return members;
	}
	
	/**
	 * 默认显示的通讯录人员
	 */
	private List<V3xOrgMember> initDepartmentMembers(User user) throws Exception {
		List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
		if (user.getAccountId() != user.getLoginAccount()) {
			members = this.loadMembers(user);
		} else {
			List<V3xOrgMember> memberList = orgManager.getMembersByDepartment(user.getDepartmentId(), true, false, user.getAccountId(), true);
			members = this.listDeptMembers(user.getDepartmentId(), user.getAccountId(), memberList);
		}
		return members;
	}

	public ModelAndView initList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ListSearchHelper.pickupExpression(request, null, "condition", "textfield");
		/**
		 * 跳转页面
		 * 1.//员工通讯录  addressbook/public/listMembers
		 * 2.//私人通讯录  addressbook/private/listMembers
		 * 3.//系统组		 addressbook/public/listSysMember
		 * 4.//个人组		 addressbook/public/listOwnGroup
		 */
		int addressbookType = RequestUtils.getIntParameter(request, "addressbookType", 1);
		ModelAndView mav = new ModelAndView(myJspView(addressbookType, "listMembers"));
		User user = CurrentUser.get();
		Long loginAccId = user.getLoginAccount();
		String accountId = request.getParameter("accountId");
		if(StringUtils.isNotBlank(accountId)){
			loginAccId = Long.parseLong(accountId);
		}
		List<V3xOrgLevel> levellist = orgManager.getAllLevels(loginAccId);
		if(levellist == null){
			levellist = new ArrayList<V3xOrgLevel>();
		}
		mav.addObject("levellist", levellist);
		
		//判断是否公开显示通讯录的职务级别
		boolean isEnableLevel = isEnableLevel();
		mav.addObject("isEnableLevel", isEnableLevel);
		if (1 == addressbookType) { // 员工通讯录
			List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
			Long realAccountId = user.getAccountId();
			//如果是切换单位后，显示不了部门的内容了。。。。。。。
			if (Strings.isNotBlank(accountId)) {
				realAccountId = Long.parseLong(accountId);
				if (user.getLoginAccount() == Long.parseLong(accountId)) {
					members = this.initDepartmentMembers(user);
				} else {
					//选择的单位是否为集团根
			        V3xOrgAccount selectAccount = orgManagerDirect.getAccountById(NumberUtils.toLong(accountId));
			        boolean isRoot = selectAccount != null && selectAccount.getIsRoot();
			        String condition = request.getParameter("condition");
			        String textfield = request.getParameter("textfield");
			        if (isRoot && StringUtils.isNotBlank(condition) && StringUtils.isNotBlank(textfield)) {
			        	Long value = NumberUtils.toLong(textfield);
			        	if ("groupPost".equals(condition)) {
			        		members = orgManager.getMembersByPost(value);
			        	} else if("groupLevel".equals(condition)) {
			        		members = orgManager.getMembersByLevel(value);
			        	} else {
			        		members = orgManager.getAllMembers(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			        	}
			        	
			        	mav.addObject("isRootQuery", true);
			        }
					
			        mav.addObject("isRoot", isRoot);
				}
			} else {
				members = this.initDepartmentMembers(user);
			}
			List<WebWithPropV3xOrgMember> results = pagenate((List<WebWithPropV3xOrgMember>) filtWebWithPropV3xOrgMembers(parseList(members, realAccountId, user.getDepartmentId())));
			mav.addObject("members", results);
		} else if (2 == addressbookType) { // 私人通讯录
			
			//点击私人通讯录进入就查询所有的自己建立的人员
			final Long creatorId = Long.valueOf(user.getId());
			List<AddressBookMember> members = addressBookManager.getMembersByCreatorId(creatorId);
			filtAddressBookMembers(members);
			mav.addObject("members", pagenate(members));
			mav.addObject("resultCount", members.size());
		}else if(3 == addressbookType ){//系统组 
			List<WebWithPropV3xOrgMember> members = new ArrayList<WebWithPropV3xOrgMember>();
			filtWebWithPropV3xOrgMembers(members);
			mav.addObject("members", pagenate(members));
			mav.addObject("resultCount", members.size());
		}else if(4 == addressbookType){//个人组
			Long teamId = null;
			List<V3xOrgTeam> teamlist = orgManager.getTeamsByOwner(user.getId());
			if(teamlist.size()>0){
				teamId = teamlist.get(0).getId();
				
				V3xOrgTeam team = orgManager.getTeamById(teamId);
				List<Long> memberIds = new ArrayList<Long>(team.getLeaders());
				memberIds.addAll(team.getMembers());
				List<V3xOrgMember> mems = new ArrayList<V3xOrgMember>();
				for(Long memberId : memberIds){
					mems.add(orgManager.getMemberById(memberId));
				}
				List<V3xOrgMember> members = this.filterMemberByAssigned(mems);
				List<WebWithPropV3xOrgMember> results = pagenate((List<WebWithPropV3xOrgMember>) filtWebWithPropV3xOrgMembers(translateList(members, 4)));
				mav.addObject("members", results);
				mav.addObject("resultCount", members.size());
			}else{
				List<WebWithPropV3xOrgMember> members = new ArrayList<WebWithPropV3xOrgMember>();
				mav.addObject("members", pagenate(members));
				mav.addObject("resultCount", members.size());
			}		
		}
		return mav;
	}
	/**
	 * 判断是否公开显示通讯录的职务级别
	 * @return isEnableLevel
	 */
	private boolean isEnableLevel() {
		boolean isEnableLevel = true;
		String enableLevelConfig = systemConfig.get(IConfigPublicKey.LEVEL_STATE_ENABLE);
		if(enableLevelConfig != null && "disable".equals(enableLevelConfig)){
			isEnableLevel = false;
		}
		return isEnableLevel;
	}

	public ModelAndView initDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView(jspView("detail"));
		return mav;
	}

	public ModelAndView initTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return null;
	}

	// 显示员工列表(全部/部门/系统组/个人组)
	@SuppressWarnings("unchecked")
	public ModelAndView listAllMembers(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ListSearchHelper.pickupExpression(request, null, "condition", "textfield");
		int addressbookType = RequestUtils.getIntParameter(request,"addressbookType", 1);
		ModelAndView mav = new ModelAndView(jspView(addressbookType,"listMembers"));
		
		boolean isEnableLevel = isEnableLevel();
		mav.addObject("isEnableLevel", isEnableLevel);

		
		if (1 == addressbookType) { // 员工
			User user = CurrentUser.get();
			String accountid = request.getParameter("accountId");
			Long accountId = Strings.isNotBlank(accountid)?Long.parseLong(accountid):0L;
			
			List<V3xOrgMember> mems = orgManager.getAllMembers(accountId);// 单位人员，包括兼职人员
			
			List<V3xOrgMember> outMembers = new ArrayList<V3xOrgMember>();// 获取单位下 所有的外部人员
			List<V3xOrgMember> allOutMembers = orgManager.getAllExtMembers(accountId);
			for (V3xOrgMember outMember : allOutMembers) {
				V3xOrgDepartment outDept = orgManager.getDepartmentById(outMember.getOrgDepartmentId());
				if (OuterWorkerAuthUtil.canAccessOuterDep(user.getId(), user.getDepartmentId(), user.getLoginAccount(), outDept,
						orgManager)) {
					outMembers.add(outMember);
				}
			}
			List<V3xOrgMember> members = this.filterMemberByAssigned(mems);
			List<V3xOrgMember> resultList = new ArrayList<V3xOrgMember>();
			List<V3xOrgMember> outList = new ArrayList<V3xOrgMember>();
			
			Long currentUserId = user.getId();
			V3xOrgMember currentMember = orgManager.getMemberById(currentUserId);
			
			List<V3xOrgEntity> outerRight = null;
			if(!currentMember.getIsInternal()){
				outerRight = orgManager.getExternalMemberWorkScope(currentMember.getId(),false);
			}
			if(members != null && !members.isEmpty()){
				for(V3xOrgMember member : members){
					boolean cls = Functions.checkLevelScope(currentMember.getId(), member.getId());
					if(cls)
						resultList.add(member);
				}
			}
			if(outMembers != null && !outMembers.isEmpty()){
				for(V3xOrgMember member : outMembers){
					boolean cls = Functions.checkLevelScope(currentMember.getId(), member.getId());
					if(cls)
						outList.add(member);
				}
			}
			Collections.sort(resultList,CompareSortEntity.getInstance());
			List<WebWithPropV3xOrgMember> results = parseList(resultList,accountId,null);
			List<WebWithPropV3xOrgMember> outresults = parseList(outList,accountId,null);
			List<WebWithPropV3xOrgMember> allresults = new ArrayList<WebWithPropV3xOrgMember>();
			allresults.addAll(results);
			allresults.addAll(outresults);
			filtWebWithPropV3xOrgMembers(allresults);
			mav.addObject("members", pagenate(allresults));
		} else if (2 == addressbookType) { // 外部联系人
			User user = CurrentUser.get();
			List<AddressBookMember> members = addressBookManager.getMembersByCreatorId(user.getId());
			filtAddressBookMembers(members);
			mav.addObject("members", members);
		}
		return mav;
	}

	/**
	 * 点击树结构的部门，显示部门的人员
	 */
	public ModelAndView listDeptMembers(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ListSearchHelper.pickupExpression(request, null, "condition", "textfield");
		int addressbookType = RequestUtils.getIntParameter(request, "addressbookType", 1);
		Long deptId = RequestUtils.getLongParameter(request, "pId");
		ModelAndView mav = new ModelAndView(jspView(addressbookType, "listMembers"));

		boolean isEnableLevel = isEnableLevel();
		mav.addObject("isEnableLevel", isEnableLevel);
		Long accountid = RequestUtils.getLongParameter(request, "accountId");

		V3xOrgDepartment dep = this.orgManager.getDepartmentById(deptId);
		List<V3xOrgMember> members = this.orgManager.getMembersByDepartment(dep.getId(), true, false, dep.getOrgAccountId(), true);
		List<V3xOrgMember> member1 = this.listDeptMembers(deptId, accountid, members);

		// 查询之后的结果集暂先按照第一页抽取显示
		if (Strings.isNotBlank(request.getParameter("condition"))) {
			Pagination.setFirstResult(0);
		}

		List<WebWithPropV3xOrgMember> results = pagenate((List<WebWithPropV3xOrgMember>) filtWebWithPropV3xOrgMembers(parseList(member1, accountid, deptId)));
		
		mav.addObject("members", results);
		mav.addObject("resultCount", results.size());
		return mav;
	}

	/**
	 * 组装部门人员数据
	 */
	private List<V3xOrgMember> listDeptMembers(Long deptId, Long accountId, List<V3xOrgMember> members) throws Exception {
		User user = CurrentUser.get();
		Long userId = user.getId();
		V3xOrgMember currentMember = orgManager.getMemberById(userId);
		List<V3xOrgMember> result = new ArrayList<V3xOrgMember>();
		if (!user.isInternal()) {//外部人员访问内部人员权限不用判断了, 因为员工通讯录中就是他能看到的部门, 这个部门内的人员他都能查看.
			result = members;
		} else {
			if (CollectionUtils.isNotEmpty(members)) {
				for (V3xOrgMember member : members) {
					if (!accountId.equals(member.getOrgAccountId())) {
						Map<Long, List<ConcurrentPost>> concurrentPosts = this.orgManager.getConcurentPostsByMemberId(accountId, member.getId());
						
						if (!concurrentPosts.isEmpty()) {
							List<ConcurrentPost> conPostList = concurrentPosts.get(deptId);
							if (CollectionUtils.isNotEmpty(conPostList)) {
								ConcurrentPost conPost = conPostList.get(0);
								V3xOrgMember mem = this.orgManager.getMemberById(member.getId());
								if (mem == null || !mem.isValid())
									continue;
								member = new V3xOrgMember(mem);
								if (conPost.getCntDepId() != null)
									member.setOrgDepartmentId(conPost.getCntDepId());
								if (conPost.getCntLevelId() != null)
									member.setOrgLevelId(conPost.getCntLevelId());
							}
						}
					}
					//2012-04-10修改BUG_AEIGHT-4176，设置工作范围向上访问权限后无法再通讯录中查看到外单位人员，改为使用公共组件方法类判断，职务级别
					boolean cls = Functions.checkLevelScope(currentMember.getId(), member.getId());
					if (cls)
						result.add(member);
				}
			}
		}
		return result;
	}

	/**
	 * 显示系统组的人员
	 * @author lucx
	 *
	 */
	public ModelAndView listSysTeamMembers(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ListSearchHelper.pickupExpression(request, null, "condition", "textfield");
		int addressbookType = RequestUtils.getIntParameter(request,
				"addressbookType", 1);
		Long teamId = RequestUtils.getLongParameter(request, "tId");
		ModelAndView mav = new ModelAndView(jspView(addressbookType,
				"listSysMember"));
		boolean isEnableLevel = isEnableLevel();
		mav.addObject("isEnableLevel", isEnableLevel);
		List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
		List<WebWithPropV3xOrgMember> results = new ArrayList<WebWithPropV3xOrgMember>();
		V3xOrgTeam team = orgManagerDirect.getTeamById(teamId);
		List<Long> leaderIds = team.getLeaders();
		List<Long> memberIds = team.getMembers();
		List<Long> relations = team.getRelatives();
		List<Long> supervisors = team.getSupervisors();
		leaderIds.addAll(memberIds);
		leaderIds.addAll(relations);
		leaderIds.addAll(supervisors);
		for(Long memberId : leaderIds){
			V3xOrgMember member = orgManagerDirect.getMemberById(memberId);
			if(member != null)
				if(members.size() == 0)
					members.add(member);
				else{
					if(!members.contains(member))
						members.add(member);
				}
		}
		List<V3xOrgMember> temp = this.filterMemberByAssigned(members);
		if(members.size() != 0)
			results = pagenate((List<WebWithPropV3xOrgMember>) filtWebWithPropV3xOrgMembers(translateList(temp,addressbookType)));
		mav.addObject("members", results);
		mav.addObject("resultCount", temp.size());
		return mav;
	}

	/**
	 * 显示个人组的人员
	 * @author lucx
	 *
	 */
	public ModelAndView listOwnTeamMembers(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ListSearchHelper.pickupExpression(request, null, "condition", "textfield");
		int addressbookType = RequestUtils.getIntParameter(request,
				"addressbookType", 1);
		Long teamId = RequestUtils.getLongParameter(request, "tId");

		if (1 == addressbookType) {
			ModelAndView mav = new ModelAndView(jspView(addressbookType,
			"listOwnGroup"));
			boolean isEnableLevel = isEnableLevel();
			mav.addObject("isEnableLevel", isEnableLevel);
			V3xOrgTeam team = orgManagerDirect.getTeamById(teamId);
			List<Long> memberIds = new UniqueList<Long>();
			memberIds.addAll(team.getLeaders());
			memberIds.addAll(team.getMembers());
			List<V3xOrgMember> mems = new ArrayList<V3xOrgMember>();
			for(Long memberId : memberIds){
				mems.add(orgManagerDirect.getMemberById(memberId));
			}
			List<V3xOrgMember> members = this.filterMemberByAssigned(mems);
			List<WebWithPropV3xOrgMember> results = pagenate((List<WebWithPropV3xOrgMember>) filtWebWithPropV3xOrgMembers(translateList(members,4)));
			mav.addObject("members", results);
			mav.addObject("resultCount", members.size());
			return mav;
		} else if (2 == addressbookType) {
			ModelAndView mav = new ModelAndView(myJspView(addressbookType,
			"listMembers"));
			boolean isEnableLevel = isEnableLevel();
			mav.addObject("isEnableLevel", isEnableLevel);
			List<AddressBookMember> results = pagenate((List<AddressBookMember>) this.filtAddressBookMembers(this.addressBookManager.getMembersByTeamId(teamId)));
			mav.addObject("members", results);
			mav.addObject("resultCount", results.size());
			return mav;
		}
		return null;
	}

	// 显示左边树(部门/系统组/个人组)
	public ModelAndView treeDept(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int addressbookType = RequestUtils.getIntParameter(request, "addressbookType", 1);
		User user = CurrentUser.get();
		
		boolean isSameAccount = false;
		Long accId = user.getLoginAccount();
		Long accId2 = user.getAccountId();
		boolean isAccId = accId.equals(accId2);
		ModelAndView result = new ModelAndView(jspView(addressbookType, "treeDept"));
		
		List<WebV3xOrgDepartment> resultlist = null;
		
		List<WebV3xOrgDepartment> inlist = null;
		List<WebV3xOrgDepartment> externallist = null;
		inlist = new ArrayList<WebV3xOrgDepartment>();
		externallist = new ArrayList<WebV3xOrgDepartment>();
		//根据树上的下拉列表传来的单位ID
		//当前登录者是内部人员，可以看到所有部门
		boolean isMyAccount = false;
		if(user.isInternal()){
			resultlist = new ArrayList<WebV3xOrgDepartment>();
			List<V3xOrgDepartment> deptlist = null;
			
			String accountId =request.getParameter("accountId");
			if(Strings.isNotBlank(accountId)){
				accId = Long.parseLong(accountId);
				
				if(orgManager.getRootAccount().getId().equals(accId)){
					List<V3xOrgAccount> childAccountList = orgManager.getChildAccount(accId, false, true);
	        		Collections.sort(childAccountList, CompareSortEntity.getInstance());
	        		result.addObject("isRoot", true);
	        		result.addObject("childAccountList", childAccountList);
				}
				
				//根据列表切换到的单位下的所有的部门
				deptlist = orgManager.getAllDepartments(accId);
				if(isAccId && accId == user.getLoginAccount()){
					isSameAccount = true;
				}
				if(accId2.longValue() == accId){
					isMyAccount = true;
				}
			}else{
				//当前登陆人的单位下所有的部门
				deptlist = orgManager.getAllDepartments();
				if(isAccId){
					isSameAccount = true;
				}
				isMyAccount = true;
			}
			for (V3xOrgDepartment dept:deptlist) {
				if(isMyAccount){
					if(dept.getIsInternal()){
						WebV3xOrgDepartment webdept = getWebOrgDepartmentObj(dept,accId);
						resultlist.add(webdept);
					}else if(OuterWorkerAuthUtil.canAccessOuterDep(user.getId(),user.getDepartmentId(),user.getLoginAccount(),dept,orgManager)){
						WebV3xOrgDepartment webdept = getWebOrgDepartmentObj(dept,accId);
						resultlist.add(webdept);
					}
				}else{
					if(dept.getIsInternal()){//当前登录用户只能看到外单位的内部部门
						WebV3xOrgDepartment webdept = getWebOrgDepartmentObj(dept,accId);
						resultlist.add(webdept);
					}
				}
			}
		}else{
			resultlist = OuterWorkerAuthUtil.getOuterDeptList(result, user, accId, orgManager);
		}
		for(WebV3xOrgDepartment d:resultlist){
			if(d.getV3xOrgDepartment().getIsInternal()==true){
				inlist.add(d);
			}else{
				externallist.add(d);
			}
		}
		/******************/
		/*
		List<V3xOrgAccount> accessableAccounts = null;
		Set<Long> accessableAccountIds  = new HashSet<Long>();
		V3xOrgAccount rootAccount = this.orgManager.getRootAccount();
		List<V3xOrgAccount> _accessableAccounts = this.orgManager.accessableAccounts(user.getId());//获取可访问的单位列表
		for (V3xOrgAccount a : _accessableAccounts) {
			accessableAccountIds.add(a.getId());
		}
		boolean isAccountInGroup = this.orgManager.isAccountInGroupTree(user.getLoginAccount());
		accessableAccounts = new ArrayList<V3xOrgAccount>(_accessableAccounts.size());
		for (V3xOrgAccount a : _accessableAccounts) {
			V3xOrgAccount _account = new V3xOrgAccount(a);//此处修改上级id,采用new一个单位的方法,以免影响缓存
			if(_account.getSuperior().longValue() != -1 && !accessableAccountIds.contains(_account.getSuperior())){
				_account.setSuperior(isAccountInGroup ? rootAccount.getId() : -1);//处理无根单位
			}
			accessableAccounts.add(_account);
		}
		if (user.getLoginAccount() != 1 && isAccountInGroup) {
			accessableAccounts.add(new V3xOrgAccount(rootAccount));
		}
		boolean isGroupAccessable = Functions.isGroupAccessable(user.getLoginAccount());
		if(!isGroupAccessable){
			accessableAccounts.remove(rootAccount);
		}
		result.addObject("accountsList", accessableAccounts);
		*/
		/**********************/
		V3xOrgAccount account = orgManager.getAccountById(accId);
		//判断是否是集团版本---做集团化通讯录
		boolean isGroupVer=(Boolean)(SysFlag.sys_isGroupVer.getFlag()) && user.isInternal();//判断是否为集团版 并且不是外部人员
		boolean isGroupAccessable = Functions.isGroupAccessable(user.getLoginAccount());
		if(isGroupVer){
			List<V3xOrgAccount> accountsList = orgManager.accessableAccounts(user.getId());
			V3xOrgAccount rootAccount = orgManagerDirect.getRootAccount();
			
			if (isGroupAccessable) {
				accountsList.add(rootAccount);
			}
			
			Collections.sort(accountsList, CompareSortEntity.getInstance());
			result.addObject("accountsList", accountsList);
		}
		result.addObject("isSameAccount", isSameAccount);
		result.addObject("isGroupVer", isGroupVer);
		result.addObject("account", account);
		result.addObject("deptlist", resultlist);
		result.addObject("inlist", inlist);
		result.addObject("externallist", externallist);
		
		return result;
	}
	
	
	private WebV3xOrgDepartment getWebOrgDepartmentObj(V3xOrgDepartment dept,Long accId){
		Long longid = dept.getId();
		V3xOrgDepartment parent = null;
		try {
			parent = orgManager.getParentDepartment(longid,accId);
		} catch (BusinessException e) {
			logger.error("", e);
		}
		WebV3xOrgDepartment webdept = new WebV3xOrgDepartment();
		webdept.setV3xOrgDepartment(dept);
		if (null != parent) {
			webdept.setParentId(parent.getId());
			webdept.setParentName(parent.getName());
		}
		return webdept;
	}
	
	/**
	 * 系统组。左边树结构数据显示
	 * @author lucx
	 *
	 */
	public ModelAndView treeSysTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int addressbookType = RequestUtils.getIntParameter(request,
				"addressbookType", 1);
		ModelAndView mav = new ModelAndView(jspView(addressbookType,
				"treeSysTeam"));
		User user = CurrentUser.get();
		Long accId = user.getLoginAccount();		
		
		boolean isSameAccount = false;
		
		//根据树上的下拉列表传来的单位ID
//		当前登录者是内部人员，可以看到所有部门
		
		List<V3xOrgTeam> teamlist = null;
		String accountId =request.getParameter("accountId");
		if(accountId!=null && !"".equals(accountId)){
			accId = Long.parseLong(accountId);
			//根据列表切换到的单位下的所有的组
			teamlist = orgManager.getTeamsByMember(user.getId(),accId);
			if(accId == user.getLoginAccount()){
				isSameAccount = true;
			}
		}else{
			//当前登陆人的单位下所有的组
			teamlist = orgManager.getTeamsByMember(user.getId(),accId);
			isSameAccount = true;
		}
		
		for (Iterator<V3xOrgTeam> iter = teamlist.iterator(); iter.hasNext();) {
			V3xOrgTeam team = iter.next();
			boolean flag = true;
			if(team.getType() == V3xOrgTeam.TEAM_TYPE_PERSONAL || team.getType() == V3xOrgTeam.TEAM_TYPE_DISCUSS || team.getType() == V3xOrgTeam.TEAM_TYPE_COLTEAM){
				iter.remove();
				flag = false;
			}
			if(!team.isValid()){
                iter.remove();
                flag = false;
            }
            //修改 去掉公开系统组不能被不在部门范围内的人看到。by wusb at 2010-12-21
			if(team.getDepId()!=null && team.getDepId()!=-1){
	            List<V3xOrgDepartment> depts = orgManager.getChildDepartments(team.getDepId(),false);
				List<Long> deptIds=new ArrayList<Long>();
				deptIds.add(team.getDepId());
				for (V3xOrgDepartment dept : depts) {
					deptIds.add(dept.getId());
				}
				if(!deptIds.contains(user.getDepartmentId()) && !isMyTeam(user.getId(),team)){
					boolean remove = true;
					for(Long l : orgManager.getConcurentPostsByMemberId(user.getLoginAccount(),user.getId()).keySet()){
						if(deptIds.contains(l)){
							remove = false;
						}
					}
					if(remove && flag){
						iter.remove();
					}
				}
            }
		}

		//组按类别 再按 拼音排序 huangfj 2012-07-19
		Collections.sort(teamlist, new Comparator<V3xOrgTeam>() {
			public int compare(V3xOrgTeam c1, V3xOrgTeam c2) {
				//type: 1个人组; 3项目组; 2系统组; 4讨论组
				int type1 = c1.getType();
				int type2 = c2.getType();
				
				if(type1 == 1){ type1 = -2; }
				if(type1 == 3){ type1 = -1; }
				if(type2 == 1){ type2 = -2; }
				if(type2 == 3){ type2 = -1; }
				
				if(type1 == type2){
					Collator myCollator = Collator.getInstance(CurrentUser.get().getLocale());
					return myCollator.compare(c1.getName(), c2.getName());
				}
				else{
					return type1 < type2 ? -1 : 1;
				}
			}
		});
		
		// 判断是否是集团版本---做集团化通讯录
		boolean isGroupVer = (Boolean) (SysFlag.sys_isGroupVer.getFlag());// 判断是否为集团版
		if (isGroupVer) {
			List<V3xOrgAccount> accountsList = orgManager.accessableAccounts(user.getId());
			V3xOrgAccount rootAccount = orgManagerDirect.getRootAccount();

			if (!accountsList.contains(rootAccount)) {
				accountsList.add(rootAccount);
			}

			mav.addObject("accountsList", accountsList);
		}
		
		mav.addObject("isSameAccount", isSameAccount);
		mav.addObject("isGroupVer", isGroupVer);
		
		List<WebV3xOrgTeam> results = translateV3xOrgTeam(teamlist);
		V3xOrgAccount account = orgManager.getAccountById(accId);
		mav.addObject("account", account);
		mav.addObject("teamlist", results);
		return mav;
	}
	
	private boolean isMyTeam(Long memberId, V3xOrgTeam team) {
		if (team.getMembers().contains(memberId))
			return true;
		if (team.getLeaders().contains(memberId))
			return true;
		if (team.getSupervisors().contains(memberId))
			return true;
		if (team.getRelatives().contains(memberId))
			return true;
		return false;
	}

	/**
	 * 个人组，私人通讯录，左边树结构数据
	 * @author lucx
	 *
	 */
	public ModelAndView treeOwnTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int addressbookType = RequestUtils.getIntParameter(request,
				"addressbookType", 1);
		ModelAndView mav = new ModelAndView(jspView(addressbookType,
				"treeOwnTeam"));
		User user = CurrentUser.get();

		if (1 == addressbookType) {// 员工
			V3xOrgAccount account = orgManager.getAccountById(user
					.getLoginAccount());
			List<V3xOrgTeam> teamlist = orgManager
					.getTeamsByOwner(user.getId());
			List<WebV3xOrgTeam> results = translateV3xOrgTeam(teamlist);
			mav.addObject("account", account);
			mav.addObject("teamlist", results);
		} else if (2 == addressbookType) { // 外部联系人
			List<AddressBookTeam> teamList = new ArrayList<AddressBookTeam>();
			teamList = addressBookManager.getTeamsByCreatorId(user.getId());
			AddressBookTeam abt = new AddressBookTeam();
			abt.setId(-1L);
			abt.setType(2);
			Locale locale = LocaleContext.getLocale(request);
			String resource = "com.seeyon.v3x.addressbook.resource.i18n.AddressBookResources";
			abt.setName(ResourceBundleUtil.getString(resource, locale, "addressbook.unfiled.address.label"));
			abt.setCreatorId(user.getId());
			abt.setCreatorName(user.getName());
			if(teamList!=null){
				teamList.add(abt);
			}else{
				teamList = new ArrayList<AddressBookTeam>();
				teamList.add(abt);
			}
			
			mav.addObject("teamlist", teamList);
		}
		return mav;
	}

	// 显示个人详细信息(包含附加属性)
	public ModelAndView viewMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int addressbookType = RequestUtils.getIntParameter(request,
				"addressbookType", 1);
		ModelAndView mav = new ModelAndView(jspView(addressbookType,"viewMember"));
		boolean isEnableLevel = isEnableLevel();
		mav.addObject("isEnableLevel", isEnableLevel);
		String memberid = request.getParameter("mId");
		Long memberId = 0L;
		if(Strings.isNotBlank(memberid)){
			memberId = Long.parseLong(memberid);
		}
		StaffInfo staff = staffInfoManager.getStaffInfoById(memberId);
		mav.addObject("staff", staff);
		if (null != staff) {
			if(null!=staff.getImage_id() ){
				mav.addObject("image", 0);
			}
		}
		if (1 == addressbookType) { // 员工
			//获取员工基本信息
			V3xOrgMember member = orgManagerDirect.getMemberById(memberId);
			if(member==null){				
				PrintWriter out = response.getWriter();
				out.println("<script>");
				out.println("alert(parent.v3x.getMessage(\"ADDRESSBOOKLang.addressbook_delete_member_label\"))");
				out.println("parent.window.close();");
				out.println("</script>");
				return null;
			}
			String aId = request.getParameter("accountId");
			Long accountId = 0l;
			if(Strings.isBlank(aId)){
				accountId = CurrentUser.get().getAccountId();
			}
			
			//兼职岗位
			StringBuffer deptpostbuffer = new StringBuffer();
			if(accountId != null){
	        	//得到所有兼职单位
	        	List<V3xOrgAccount> conAccount = orgManager.concurrentAccount(member.getId());
	        	for(V3xOrgAccount account : conAccount){
	        		Long conAccountId = account.getId();
	        		//单位下的兼职
	        		Map<Long,List<ConcurrentPost>> map = orgManager.getConcurentPostsByMemberId(conAccountId, member.getId());
	            	if(!map.isEmpty()){
	            		Set<Long> depList = map.keySet();
	            		for(Long depid : depList){
	            			V3xOrgDepartment v3xdept = orgManagerDirect.getDepartmentById(depid);
	            			List<ConcurrentPost> conPostList = map.get(depid);
	            			for(ConcurrentPost conPost:conPostList){
	            				StringBuffer sbsuffer = new StringBuffer();
	            				Long conPostId = conPost.getCntPostId();
	            				if(conPostId==null) continue;
	            				V3xOrgPost v3xpost = orgManagerDirect.getPostById(conPostId);
	            				sbsuffer.append("("+account.getShortname()+")");
	                			sbsuffer.append(v3xdept.getName());
	            				sbsuffer.append("-");
	            				sbsuffer.append(v3xpost.getName());
	            				if(deptpostbuffer.length() != 0){
	            					deptpostbuffer.append(",");
	            				}
	            				deptpostbuffer.append(sbsuffer.toString());
	            			}
	            			
	            		}
	            	}
	        	}
	        }
	        mav.addObject("specialSecondPost", deptpostbuffer.toString());
			
			//获取员工联系方式
			ContactInfo contactInfo = staffInfoManager.getContactInfoById(memberId);
			WebStaffInfo webStaffInfos = this.translateV3xOrgMembers(member,null);
			//~~~~~~~~~~~~~办公电话的扩展属性
			//orgManager.loadEntityProperty(member);
			String officeNum = member.getProperty("officeNum");
			mav.addObject("officeNum", officeNum);
			//~~~~~~~~~~~~~~~`
			mav.addObject("member", webStaffInfos);
			mav.addObject("contact", contactInfo);
			mav.addObject("tel", member);
			String isDetail = request.getParameter("isDetail");
			boolean readOnly = false;
			if (null != isDetail && isDetail.equals("readOnly")) {
				readOnly = true;
				mav.addObject("readOnly", readOnly);
			}

		} else if (2 == addressbookType){ // 外部联系人
			AddressBookMember member = new AddressBookMember();
			User user = CurrentUser.get();
			if (0L != memberId)
				member = this.addressBookManager.getMember(memberId);
			mav.addObject("member", member);
			mav.addObject("memberId", user.getId());//人员的ID
            mav.addObject("readOnly", false);
			boolean isCreated = false;
			if (0L == memberId)
            {
			    isCreated = true;
            }else
            {
                if(request.getParameter("edit")!=null)
                {
                    mav.addObject("disabled", false);
                }
                else
                {
                    mav.addObject("disabled", true);
                }
            }
			List<AddressBookTeam> teams = this.addressBookManager.getTeamsByCreatorId(CurrentUser.get().getId());
			mav.addObject("teams", teams);
			mav.addObject("isCreated", isCreated);
		}
		
        //HR枚举
        Map<String, Metadata> hrMetadata = metadataManager.getMetadataMap(ApplicationCategoryEnum.hr);
        mav.addObject("hrMetadata", hrMetadata);
		return mav;
	}

	
	//ajax
	/**
	 * 验证
	 * @author lucx
	 *
	 */
	public ModelAndView isExist(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int type = RequestUtils.getIntParameter(request, "type", 0);
		String mail = request.getParameter("mail");
		String memberId = request.getParameter("memberId");
		String category = request.getParameter("category");
		String ownTeam = request.getParameter("ownTeam");
		User user = CurrentUser.get();
		Long createId = user.getId();
		Long accountId = user.getLoginAccount();
		boolean isAjax = RequestUtils.getRequiredBooleanParameter(request, "ajax");		
		boolean isExist = false;
		if(type == 3){
			isExist = addressBookManager.isExist(type, mail, createId, accountId, memberId);
		}
		else if(type == 2){
			isExist = addressBookManager.isExist(type, category, createId, accountId, memberId);
		}
		else if(type == 1){
			isExist = addressBookManager.isExist(type, ownTeam, createId, accountId, memberId);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.putOpt("isExist", isExist);

		String view = null;
		if (isAjax) view = this.getJsonView();
		return new ModelAndView(view, Constants.AJAX_JSON, jsonObject);		
	}
	
	/**
	 * 更新人员信息，和私人通讯录的人员信息
	 * @author lucx
	 *
	 */
	public ModelAndView updateMember(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int addressbookType = RequestUtils.getIntParameter(request, "addressbookType", 1);
		if (1 == addressbookType) { // 员工
			WebWithPropV3xOrgMember model = new WebWithPropV3xOrgMember();
			bind(request, model);
			Long memberId = RequestUtils.getLongParameter(request, "id");
			Long orgAccountId = RequestUtils.getLongParameter(request, "orgAccountId");
			V3xOrgMember member = orgManager.getMemberById(memberId);
			member.setOrgAccountId(orgAccountId);
			member.setProperties(model.properties());
			orgManager.updateEntity(member);
		} else if (2 == addressbookType) { // 外部联系人
			boolean isCreated = RequestUtils.getBooleanParameter(request, "isCreated", false);
			String crtId = request.getParameter("categoryId");
			AddressBookMember member = new AddressBookMember();
			bind(request, member);
			member.setCategory(NumberUtils.toLong(crtId, -1l));
			member.setCreatorId(CurrentUser.get().getId());
			member.setCreatorName(CurrentUser.get().getLoginName());
			Date operatingTime = new Date();
			member.setCreatedTime(operatingTime);
			member.setModifiedTime(operatingTime);
			
			// 修改到其它联系组时
			boolean categoryChanged = !isCreated && (NumberUtils.toLong(request.getParameter("oldCategoryId")) != member.getCategory().longValue());
			
			// 修改姓名时
			boolean nameChanged = false;
			String oldName = request.getParameter("oldName");
			if (StringUtils.isNotBlank(oldName)) {
				nameChanged = !isCreated && !oldName.equals(member.getName());
			}
			
            if((isCreated || categoryChanged || nameChanged) && addressBookManager.isExistSameUserName(member, CurrentUser.get().getId()))
            {
                PrintWriter out = response.getWriter();
                out.println("<script>");
                out.println("alert(parent.v3x.getMessage(\"ADDRESSBOOKLang.addressbook_isexist_name\"));");
                out.println("</script>");
                out.flush();
				return null;
            }
            
			if (isCreated) {
				this.addressBookManager.addMember(member);
			} else {
				this.addressBookManager.updateMember(member);
			}
			
			Boolean f = (Boolean)(BrowserFlag.OpenDivWindow.getFlag(request));
			PrintWriter out = response.getWriter();
			out.println("<script>");
			if(f){
				out.println("var rv = [\"" + member.getId()+ "\", \"" + StringEscapeUtils.escapeJavaScript(member.getName()) +"\"];");
				out.println("parent.window.returnValue = rv;");
				out.println("parent.window.close();");
			}else{
				out.println("parent.parent.location.href = parent.parent.location;");
			}
			out.println("</script>");
			
			return null;
		}
		// 提示用户操作成功
		return super.redirectModelAndView("/addressbook.do?method=home&addressbookType="+addressbookType, "parent.parent");
	}

	/**
	 * 删除私人通讯录的人员
	 * @author lucx
	 *
	 */
	public ModelAndView destroyMember(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int addressbookType = RequestUtils.getIntParameter(request,
				"addressbookType", 1);
		if (2 == addressbookType) {
			String mIdStr = request.getParameter("mIds");
			this.addressBookManager.removeCategoryMembersByIds(CurrentUser.get().getId(), this.toLongList(mIdStr));
			this.addressBookManager.removeMembersByIds(CurrentUser.get().getId(), this.toLongList(mIdStr));
		}
		return super.redirectModelAndView("/addressbook.do?method=home&addressbookType="+addressbookType, "parent.parent");
	}

	// 个人组/类别
	public ModelAndView viewOwnTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int addressbookType = RequestUtils.getIntParameter(request,
				"addressbookType", 1);
		
		ModelAndView mav = new ModelAndView(jspView(addressbookType,
				"viewOwnTeam"));
		
		String id = request.getParameter("tId");
		if(id!=null && !"".equals(id)){
			if(addressbookType != 2){
				V3xOrgTeam team = orgManagerDirect.getTeamById(Long.valueOf(id));
				List<Long> teamLeaderIDs = team.getMemberList(V3xOrgEntity.ORGREL_TYPE_TEAM_LEADER);
				List<Long> teamMemIDs = team.getMemberList(V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
				StringBuffer str1 = new StringBuffer();
				StringBuffer str2 = new StringBuffer();
				StringBuffer str3 = new StringBuffer();
				StringBuffer str4 = new StringBuffer();
				for(Long leaderid : teamLeaderIDs){					
					str1.append(orgManager.getMemberById(leaderid).getName());
					str1.append(",");
					str3.append(leaderid.toString());
					str3.append(",");
				}
				for(Long memberid : teamMemIDs){
					str2.append(orgManager.getMemberById(memberid).getName());
					str2.append(",");
					str4.append(memberid.toString());
					str4.append(",");
				}
				mav.addObject("teamLeaderIDs",Strings.isNotBlank(str3.toString())?str3.deleteCharAt(str3.lastIndexOf(",")).toString():null);
				mav.addObject("teamMemIDs", Strings.isNotBlank(str4.toString())?str4.deleteCharAt(str4.lastIndexOf(",")).toString():null);
				mav.addObject("leaderNames",Strings.isNotBlank(str1.toString())?str1.deleteCharAt(str1.lastIndexOf(",")).toString():null);
				mav.addObject("memberNames",Strings.isNotBlank(str2.toString())?str2.deleteCharAt(str2.lastIndexOf(",")).toString():null);
				mav.addObject("team", team);
				mav.addObject("tId", team.getId());
			}
			else{
				AddressBookTeam team = addressBookManager.getTeam(Long.valueOf(id));
				mav.addObject("team", team);
				mav.addObject("tId", team.getId());
			}
		}
		else mav.addObject("isNew", "New");
		
		
		
		return mav;
	}

	/**
	 * 新建\更新   --个人组，私人组
	 * @author lucx
	 *
	 */
	public ModelAndView newOwnTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int addressbookType = RequestUtils.getIntParameter(request,
				"addressbookType", 1);
		User user = CurrentUser.get();
		String id = "";
		String name = "";
		if (4 == addressbookType) {
			V3xOrgTeam team = new V3xOrgTeam();
			bind(request, team);

			team.setOrgAccountId(user.getLoginAccount());
			team.setOwnerId(user.getId());
			team.setType(V3xOrgEntity.TEAM_TYPE_PERSONAL);
			team.setIsPrivate(true);
			team.setDepId(user.getDepartmentId());

			String leaderStr = request.getParameter("teamLeaderIDs");
			List<Long> leaders = toLongList(leaderStr);
//			TODO!!! orgManager.addTeamMember(leaders, team.getId(),	V3xOrgEntity.ORGREL_TYPE_TEAM_LEADER);
			team.addTeamMember(leaders, V3xOrgEntity.ORGREL_TYPE_TEAM_LEADER);
			
			String memberStr = request.getParameter("teamMemIDs");
			List<Long> members = toLongList(memberStr);
			team.addTeamMember(members, V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
			team.setDescription(request.getParameter("memo"));
//			TODO!!! orgManager.addTeamMember(members, team.getId(),	V3xOrgEntity.ORGREL_TYPE_TEAM_MEMBER);
			String isNew = request.getParameter("isNew");
			if(isNew != null && "New".equals(isNew)){
			  orgManagerDirect.addTeam(team);
			}
			else{
				orgManagerDirect.updateEntity(team);
				UpdateTeamEvent event = new UpdateTeamEvent(this);
				event.setTeam(team);
				EventDispatcher.fireEvent(event);
			}
			id = team.getId().toString();
			name = team.getName();
		} else if (2 == addressbookType) {
			AddressBookTeam category = new AddressBookTeam();
			bind(request, category);

			// add category
			category.setCreatorId(user.getId());
			category.setCreatorName(user.getLoginName());
			category.setType(AddressBookTeam.TYPE_CATEGORY);
			Date createdTime = new Date();
			category.setCreatedTime(createdTime);
			category.setModifiedTime(createdTime);

			String isNew = request.getParameter("isNew");
			if(isNew != null && "New".equals(isNew)){
				this.addressBookManager.addTeam(category);
			}
			else{
				this.addressBookManager.updateTeam(category);
			}
			id = category.getId().toString();
			name = category.getName();
		}
		Boolean f = (Boolean)(BrowserFlag.OpenDivWindow.getFlag(request));
		PrintWriter out = response.getWriter();
		out.println("<script>");
		if(f){
			out.println("var rv = [\"" + id+ "\", \"" + StringEscapeUtils.escapeJavaScript(name) +"\"];");
			out.println("parent.window.returnValue = rv;");
			out.println("parent.window.close();");
		}else{
			out.println("parent.reloadWindow();");
		}
		out.println("</script>");

		return null;
	}

	/**
	 * 删除个人组和私人通讯录组
	 * @author lucx
	 *
	 */
	public ModelAndView destroyOwnTeam(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int addressbookType = RequestUtils.getIntParameter(request,
				"addressbookType", 1);
		Long teamId = RequestUtils.getLongParameter(request, "tId");
		if (4 == addressbookType) {
			V3xOrgTeam team = orgManagerDirect.getTeamById(teamId);
//			TODO!!! orgManager.deleteEntity(team);
			orgManagerDirect.deleteEntity(team);
			UpdateTeamEvent event = new UpdateTeamEvent(this);
			event.setTeam(team);
			EventDispatcher.fireEvent(event);
		} else if (2 == addressbookType) {
			this.addressBookManager.removeTeamById(teamId);
		}
		return super.redirectModelAndView("/addressbook.do?method=home&addressbookType="+addressbookType, "parent.parent");
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * 查询方法
	 * @author lucx
	 *
	 */
	public ModelAndView search(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int addressbookType = RequestUtils.getIntParameter(request,
				"addressbookType", 1);
		String condition = request.getParameter("condition");
		String searchType = request.getParameter("textfield").trim();
		String accId = request.getParameter("accountId");//判断是否是切换单位传来的ID 。
		String click = request.getParameter("click");
		ModelAndView mav = new ModelAndView(myJspView(addressbookType, "listMembers"));
		User user = CurrentUser.get();
		Long accountId = CurrentUser.get().getLoginAccount();
		if(Strings.isNotBlank(accId)){
			accountId = Long.parseLong(accId);
		}
		List<V3xOrgLevel> levellist = orgManager.getAllLevels(accountId);
		if(levellist == null){
			levellist = new ArrayList<V3xOrgLevel>();
		}
		mav.addObject("levellist", levellist);
		
		if(null !=condition && (condition.equals("name") || condition.equals("tel"))){ //根据姓名查询
			if(1 == addressbookType){//1.代表员工通讯录
				List<V3xOrgMember> members = null;
			
				if(Strings.isNotBlank(click)&&!click.equals("[object]")){
					if(click.equals("dept")){
						String mem = request.getParameter("mem");
						if(mem != null && mem.equals("all"))
							members = this.filterMemberByAssigned(this.getAllMembersByLoginAccount(accountId));
						else{
							Long deptId = RequestUtils.getLongParameter(request, "deptId");
							V3xOrgDepartment department = orgManager.getDepartmentById(deptId);
							if(department != null){
								if(department.getIsInternal()){
									members = this.filterMemberByAssigned(orgManager.getMembersByDepartment(deptId,false, true));
								}else{
									members = this.filterMemberByAssigned(orgManager.getExtMembersByDepartment(deptId,false));
								}
							}
						}
					}
				}else if(Strings.isBlank(click)){
					members = this.filterMemberByAssigned(orgManager.getMembersByDepartment(user.getDepartmentId(),false, true));
				}
				
				List<V3xOrgMember> result = this.departmentByName(condition, members, searchType);
				List<WebWithPropV3xOrgMember> results = translateList(pagenate(result),1);
				mav.addObject("members", results);
			}
			if(2 == addressbookType){//2.代表私人通讯录
				List<AddressBookMember> members = new ArrayList<AddressBookMember>();
				List<AddressBookMember> result = new ArrayList<AddressBookMember>();
				if(condition.equals("name")){
					result = this.addressBookManager.getMemberByName(searchType);
				}else{
					result = this.addressBookManager.getMemberByTel(searchType);
				}
				members = pagenate(result);
				mav.addObject("members", members);
			}
			if(3 == addressbookType){//3.代表系统组
				List<WebWithPropV3xOrgMember> results = new ArrayList<WebWithPropV3xOrgMember>();
				List<V3xOrgTeam> teamlist = new ArrayList<V3xOrgTeam>();
				List<WebWithPropV3xOrgMember> members = new ArrayList<WebWithPropV3xOrgMember>();
				teamlist = orgManager.getTeamsByMember(user.getId(),accountId);//有我的项目组和系统私有组，和公开组
				
				members = this.getAllMembersByName(teamlist, searchType, condition);
				results = pagenate(members);
				mav.addObject("members", results);
			}
			if(4 == addressbookType){//4.代表个人组
				List<WebWithPropV3xOrgMember> results = new ArrayList<WebWithPropV3xOrgMember>();
				List<V3xOrgTeam> teamlist = new ArrayList<V3xOrgTeam>();
				List<WebWithPropV3xOrgMember> members = new ArrayList<WebWithPropV3xOrgMember>();
				teamlist = orgManager.getTeamsByOwner(CurrentUser.get().getId());
				members = this.getAllMembersByName(teamlist, searchType, condition);
				results = pagenate(members);
				mav.addObject("members", results);
			}
		}
		if(null !=condition && condition.equals("level")){ //根据职务级别查询
			if(1 == addressbookType){
				List<V3xOrgMember> members = this.filterMemberByAssigned(this.getAllMembersByLoginAccount(accountId));
				List<V3xOrgMember> result = this.departmentByLevel(members, searchType);
				List<WebWithPropV3xOrgMember> results = translateList(pagenate(result),addressbookType);
				mav.addObject("members", results);
				
				
			}
			if(2 == addressbookType){
				List<AddressBookMember> members = this.pagenate(this.addressBookManager.getMemberByLevelName(searchType));
				mav.addObject("members", members);
			}
			if(3 == addressbookType){
				List<WebWithPropV3xOrgMember> results = new ArrayList<WebWithPropV3xOrgMember>();
				List<V3xOrgTeam> teamlist = new ArrayList<V3xOrgTeam>();
				List<WebWithPropV3xOrgMember> members = new ArrayList<WebWithPropV3xOrgMember>();
				teamlist = orgManager.getTeamsByMember(user.getId(),accountId);//有我的项目组和系统私有组，和公开组

				members = this.getAllMembersByName(teamlist, searchType, condition);
				results = pagenate(members);
				mav.addObject("members", results);
			}
			if(4 == addressbookType){
				List<WebWithPropV3xOrgMember> results = new ArrayList<WebWithPropV3xOrgMember>();
				List<V3xOrgTeam> teamlist = new ArrayList<V3xOrgTeam>();
				List<WebWithPropV3xOrgMember> members = new ArrayList<WebWithPropV3xOrgMember>();
				teamlist = orgManager.getTeamsByOwner(CurrentUser.get().getId());
				members = this.getAllMembersByName(teamlist, searchType, condition);
				results = pagenate(members);
				mav.addObject("members", results);
			}
		}
		return mav;
	}

	public OrgManagerDirect getOrgManagerDirect() {
		return orgManagerDirect;
	}

	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}

	public StaffInfoManager getStaffInfoManager() {
		return staffInfoManager;
	}

	public void setStaffInfoManager(StaffInfoManager staffInfoManager) {
		this.staffInfoManager = staffInfoManager;
	}
	
	private WebStaffInfo translateV3xOrgMembers(V3xOrgMember member,Long accountId) throws BusinessException {
		Long accId = member.getOrgAccountId();
		Long deptId = member.getOrgDepartmentId();
		Long levelId = member.getOrgLevelId();
		Long postId = member.getOrgPostId();
		WebStaffInfo webstaffinfo = new WebStaffInfo();
		V3xOrgAccount acc = orgManagerDirect.getAccountById(accId);
		if(acc != null){
			webstaffinfo.setOrg_name(acc.getName());

		}
		V3xOrgDepartment dept = orgManagerDirect.getDepartmentById(deptId);
		if (dept != null) {
			webstaffinfo.setDepartment_name(dept.getName());
		}
		V3xOrgLevel level = orgManagerDirect.getLevelById(levelId);
		if (null != level) {
			webstaffinfo.setLevel_name(level.getName());
			webstaffinfo.setOrgLevelId(level.getId());
		}
		
		//政务版-HR新增职级
		if((Boolean)SysFlag.is_gov_only.getFlag()){
			Long dutyLevelId=member.getOrgDutyLevelId();
			V3xOrgDutyLevel dutyLevel = orgManagerDirect.getDutyLevelById(dutyLevelId);
			if (null != dutyLevel) {
				webstaffinfo.setDutyLevelName(dutyLevel.getName());
				//webstaffinfo.setOrgDutyLevelId(dutyLevel.getId());
			}
		}

		
		V3xOrgPost post = orgManagerDirect.getPostById(postId);
		if (null != post) {
			webstaffinfo.setPost_name(post.getName());
			webstaffinfo.setOrgPostId(post.getId());
		}
		
		webstaffinfo.setName(Functions.showMemberName(member));
		webstaffinfo.setType(member.getType());
		webstaffinfo.setCode(member.getCode());
		webstaffinfo.setState(member.getState());
		webstaffinfo.setId(member.getId());
		webstaffinfo.setPeople_type(member.getIsInternal());
		 
		// 取得人员的副岗
		List<MemberPost> memberPosts = member.getSecond_post();
		StringBuffer deptpostbuffer = new StringBuffer();
        if (null != memberPosts && !memberPosts.isEmpty())
        {
            for (MemberPost memberPost:memberPosts)
            {
            	StringBuffer sbuffer = new StringBuffer();
                Long deptid = memberPost.getDepId();
                V3xOrgDepartment v3xdept = orgManagerDirect.getDepartmentById(deptid);
                sbuffer.append(v3xdept.getName());
                sbuffer.append("-");
                Long postid = memberPost.getPostId();
                V3xOrgPost v3xpost = orgManagerDirect.getPostById(postid);
                sbuffer.append(v3xpost.getName());
                if(deptpostbuffer.length() !=0){
                	deptpostbuffer.append(",");
                }
                deptpostbuffer.append(sbuffer.toString());
            }
        }
        //是否去兼职
        if(accountId != null){
        	//得到所有兼职单位
        	List<V3xOrgAccount> conAccount = orgManager.concurrentAccount(member.getId());
        	for(V3xOrgAccount account : conAccount){
        		Long conAccountId = account.getId();
        		//单位下的兼职
        		Map<Long,List<ConcurrentPost>> map = orgManager.getConcurentPostsByMemberId(conAccountId, member.getId());
            	if(!map.isEmpty()){
            		Set<Long> depList = map.keySet();
            		for(Long depid : depList){
            			V3xOrgDepartment v3xdept = orgManagerDirect.getDepartmentById(depid);
            			List<ConcurrentPost> conPostList = map.get(depid);
            			for(ConcurrentPost conPost:conPostList){
            				StringBuffer sbsuffer = new StringBuffer();
            				Long conPostId = conPost.getCntPostId();
            				if(conPostId==null) continue;
            				V3xOrgPost v3xpost = orgManagerDirect.getPostById(conPostId);
            				sbsuffer.append("("+account.getShortname()+")");
                			sbsuffer.append(v3xdept.getName());
            				sbsuffer.append("-");
            				sbsuffer.append(v3xpost.getName());
            				if(deptpostbuffer.length() != 0){
            					deptpostbuffer.append(",");
            				}
            				deptpostbuffer.append(sbsuffer.toString());
            			}
            			
            		}
            	}
        	}
        }
        if (deptpostbuffer.length() > 0)
        {
            webstaffinfo.setSecond_posts(deptpostbuffer.toString());
        }
		return webstaffinfo;
	}

	/**
	 * 导出数据（excel/csv）
	 */
	@SuppressWarnings("rawtypes")
	public ModelAndView export(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String exportType = request.getParameter("exportType");
		if ("page".equals(exportType)) {
			return ExportHelper.excutePageMethod(this, request, response, "pageMethod");
		} else {
			// 不分页
			Pagination.withoutPagination(null);
			Pagination.setFirstResult(0);
			Pagination.setMaxResults(Integer.MAX_VALUE);
			ModelAndView mav = ExportHelper.excutePageMethod(this, request, response, "pageMethod");
			if (mav != null) {
				// 从ModelAndView中获得List<WebWithPropV3xOrgMember>或List<AddressBookMember>
				List data = (List) mav.getModel().get("members");
				if (data != null && !data.isEmpty()) {
					if ("excel".equals(exportType)) {
						// 参考download()
						exportAsExcel(request, response, data);
					} else if ("csv".equals(exportType)) {
						// 参考csvExport()
						exportAsCsv(request, response, data);
					}
				}
			}
			return null;
		}
	}

	/**
	 * 参照download()导出Excel
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void exportAsExcel(HttpServletRequest request,
			HttpServletResponse response, List data) throws Exception {
		List<WebWithPropV3xOrgMember> webOrgMembers = null;
		List<AddressBookMember> addressMembers = null;
		if (data == null || data.isEmpty()) {
			return;
		} else if (data.get(0) instanceof WebWithPropV3xOrgMember) {
			webOrgMembers = data;
		} else if (data.get(0) instanceof AddressBookMember) {
			addressMembers = data;
		}
		// 以下参照download()
		String resource = "com.seeyon.v3x.addressbook.resource.i18n.AddressBookResources";
		Locale locale = LocaleContext.getLocale(request);
		DataRecord record = new DataRecord();
		String fileName;
		
		boolean isEnableLevel = isEnableLevel();
		
		Map<Long, ContactInfo> contactInfoMap = staffInfoManager.getAllContactInfo();
		
		if(webOrgMembers != null){
			initDataRecord(record,request,true);
			for(WebWithPropV3xOrgMember webOrgMember : webOrgMembers){
				V3xOrgMember orgMember = webOrgMember.getV3xOrgMember();
				ContactInfo contactInfo = contactInfoMap.get(orgMember.getId());
				WebStaffInfo webStaffInfo = this.translateV3xOrgMembers(orgMember,null);
//				~~~~~~~~~~~~~办公电话的扩展属性
				String officeNum = orgMember.getProperty("officeNum");				
				DataRow row = new DataRow();
				row.addDataCell(webStaffInfo.getName()==null?"":webStaffInfo.getName(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(webStaffInfo.getCode()==null?"":webStaffInfo.getCode(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(webStaffInfo.getDepartment_name()==null?"":webStaffInfo.getDepartment_name(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(webStaffInfo.getPost_name()==null?"":webStaffInfo.getPost_name(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(webStaffInfo.getSecond_posts()==null?"":webStaffInfo.getSecond_posts(), DataCell.DATA_TYPE_TEXT);
				
				if (isEnableLevel) {
					row.addDataCell(webStaffInfo.getLevel_name() == null ? "" : webStaffInfo.getLevel_name(), DataCell.DATA_TYPE_TEXT);
				}
				
				row.addDataCell(Strings.isBlank(orgMember.getEmailAddress())?"":orgMember.getEmailAddress(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(contactInfo==null?"":contactInfo.getBlog(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(contactInfo==null?"":contactInfo.getWebsite(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(Strings.isBlank(officeNum)?"":officeNum, DataCell.DATA_TYPE_TEXT);
				row.addDataCell(Strings.isBlank(orgMember.getTelNumber())?"":orgMember.getTelNumber(), DataCell.DATA_TYPE_TEXT);				
				row.addDataCell(contactInfo==null?"":contactInfo.getAddress(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(contactInfo==null?"":contactInfo.getPostalcode(), DataCell.DATA_TYPE_TEXT);
				record.addDataRow(row);
			}
			fileName = ResourceBundleUtil.getString(resource, locale, "addressbook.download.filename");
			this.fileToExcelManager.save(request,response, fileName, "location.href",record);
		}else if(addressMembers != null){
			initDataRecord(record,request,false);
			for(AddressBookMember addressMember : addressMembers){
				boolean isNotCategories = true;//标识未分类
				DataRow row = new DataRow();
				row.addDataCell(addressMember.getName(), DataCell.DATA_TYPE_TEXT);
				List<AddressBookTeam> teams = this.addressBookManager.getTeamsByCreatorId(CurrentUser.get().getId());
				if(teams!=null && teams.size()>0){
					for(AddressBookTeam team : teams){
						if(addressMember.getCategory().equals(team.getId())){
							row.addDataCell(team.getName(), DataCell.DATA_TYPE_TEXT);
							isNotCategories = false;
							break;
						}
					}
					if(isNotCategories)
						row.addDataCell("未分类联系人", DataCell.DATA_TYPE_TEXT);
				}else{
					row.addDataCell("未分类联系人", DataCell.DATA_TYPE_TEXT);
				}

				row.addDataCell(addressMember.getCompanyName()==null?"":addressMember.getCompanyName(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getCompanyDept()==null?"":addressMember.getCompanyDept(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getCompanyPost()==null?"":addressMember.getCompanyPost(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getCompanyLevel()==null?"":addressMember.getCompanyLevel(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getEmail()==null?"":addressMember.getEmail(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getBlog()==null?"":addressMember.getBlog(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getWebsite()==null?"":addressMember.getWebsite(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getMsn()==null?"":addressMember.getMsn(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getQq()==null?"":addressMember.getQq(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getCompanyPhone()==null?"":addressMember.getCompanyPhone(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getFamilyPhone()==null?"":addressMember.getFamilyPhone(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getMobilePhone()==null?"":addressMember.getMobilePhone(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getFax()==null?"":addressMember.getFax(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getAddress()==null?"":addressMember.getAddress(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getPostcode()==null?"":addressMember.getPostcode(), DataCell.DATA_TYPE_TEXT);
				record.addDataRow(row);
			}
			fileName = ResourceBundleUtil.getString(resource, locale, "addressbook.download.personfilename");
			this.fileToExcelManager.save(request,response, fileName, "location.href",record);
		}
	}

	/**
	 * 下载
	 * @author lucx
	 * @deprecated 请用export()
	 */
	public ModelAndView download(HttpServletRequest request,
			HttpServletResponse response)throws Exception{
		
		Map<String,List> map = getDownList(request);
		Locale locale = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.addressbook.resource.i18n.AddressBookResources";
		DataRecord record = new DataRecord();
		String fileName = "" ;
		List<V3xOrgMember> orgMembers = map.get("orgMembers");
		List<AddressBookMember> addressMembers = map.get("addressMembers");
		
		if(orgMembers != null){
			initDataRecord(record,request,true);
			for(V3xOrgMember orgMember : orgMembers){
				ContactInfo contactInfo = null;
				contactInfo = staffInfoManager.getContactInfoById(orgMember.getId());
				WebStaffInfo webStaffInfo = this.translateV3xOrgMembers(orgMember,null);
//				~~~~~~~~~~~~~办公电话的扩展属性
				String officeNum = orgMember.getProperty("officeNum");				
				DataRow row = new DataRow();
				row.addDataCell(webStaffInfo.getName()==null?"":webStaffInfo.getName(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(webStaffInfo.getCode()==null?"":webStaffInfo.getCode(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(webStaffInfo.getDepartment_name()==null?"":webStaffInfo.getDepartment_name(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(webStaffInfo.getPost_name()==null?"":webStaffInfo.getPost_name(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(webStaffInfo.getSecond_posts()==null?"":webStaffInfo.getSecond_posts(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(webStaffInfo.getLevel_name()==null?"":webStaffInfo.getLevel_name(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(Strings.isBlank(orgMember.getEmailAddress())?"":orgMember.getEmailAddress(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(contactInfo==null?"":contactInfo.getBlog(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(contactInfo==null?"":contactInfo.getWebsite(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(Strings.isBlank(officeNum)?"":officeNum, DataCell.DATA_TYPE_TEXT);
				row.addDataCell(Strings.isBlank(orgMember.getTelNumber())?"":orgMember.getTelNumber(), DataCell.DATA_TYPE_TEXT);				
				row.addDataCell(contactInfo==null?"":contactInfo.getAddress(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(contactInfo==null?"":contactInfo.getPostalcode(), DataCell.DATA_TYPE_TEXT);
				record.addDataRow(row);
			}
			fileName = ResourceBundleUtil.getString(resource, locale, "addressbook.download.filename");
			this.fileToExcelManager.save(request,response, fileName, "location.href",record);
		}else if(addressMembers != null){
			initDataRecord(record,request,false);
			for(AddressBookMember addressMember : addressMembers){
				boolean isNotCategories = true;//标识未分类
				DataRow row = new DataRow();
				row.addDataCell(addressMember.getName(), DataCell.DATA_TYPE_TEXT);
				List<AddressBookTeam> teams = this.addressBookManager.getTeamsByCreatorId(CurrentUser.get().getId());
				if(teams!=null && teams.size()>0){
					for(AddressBookTeam team : teams){
						if(addressMember.getCategory().equals(team.getId())){
							row.addDataCell(team.getName(), DataCell.DATA_TYPE_TEXT);
							isNotCategories = false;
							break;
						}
					}
					if(isNotCategories)
						row.addDataCell("未分类联系人", DataCell.DATA_TYPE_TEXT);
				}else{
					row.addDataCell("未分类联系人", DataCell.DATA_TYPE_TEXT);
				}

				row.addDataCell(addressMember.getCompanyName()==null?"":addressMember.getCompanyName(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getCompanyDept()==null?"":addressMember.getCompanyDept(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getCompanyPost()==null?"":addressMember.getCompanyPost(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getCompanyLevel()==null?"":addressMember.getCompanyLevel(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getEmail()==null?"":addressMember.getEmail(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getBlog()==null?"":addressMember.getBlog(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getWebsite()==null?"":addressMember.getWebsite(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getMsn()==null?"":addressMember.getMsn(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getQq()==null?"":addressMember.getQq(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getCompanyPhone()==null?"":addressMember.getCompanyPhone(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getFamilyPhone()==null?"":addressMember.getFamilyPhone(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getMobilePhone()==null?"":addressMember.getMobilePhone(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getFax()==null?"":addressMember.getFax(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getAddress()==null?"":addressMember.getAddress(), DataCell.DATA_TYPE_TEXT);
				row.addDataCell(addressMember.getPostcode()==null?"":addressMember.getPostcode(), DataCell.DATA_TYPE_TEXT);
				record.addDataRow(row);
			}
			fileName = ResourceBundleUtil.getString(resource, locale, "addressbook.download.personfilename");
			this.fileToExcelManager.save(request,response, fileName, "location.href",record);
		}else{
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert(parent.v3x.getMessage(\"ADDRESSBOOKLang.addressbook_group_download_label\"))");			
			out.println("</script>");
		}
		return null;
	}
	
	private Map<String,List> getDownList(HttpServletRequest request) throws Exception{
		
		String condition = request.getParameter("condition");
		String click = request.getParameter("click");
		String accountId = request.getParameter("accountId");//判断是否是切换单位传来的ID 。
		int type = RequestUtils.getIntParameter(request, "type", 1);
		
		List<V3xOrgMember> orgMembers = null;
		List<AddressBookMember> addressMembers = null;
		User user = CurrentUser.get();
		Long accId = user.getLoginAccount();
		if(Strings.isNotBlank(accountId)){
			accId = Long.parseLong(accountId);
		}
		Long deptId = null;
		if(Strings.isNotBlank(condition)&&!condition.equals("[object]")){
			String searchType = RequestUtils.getStringParameter(request, "textfield");
			if(condition.equals("name")){
				if(type == 1){
					orgMembers = this.filterMemberByAssigned(this.departmentByName("name", this.getAllMembersByLoginAccount(accId),searchType));
				}else if(type == 2){
					addressMembers = this.addressBookManager.getMemberByName(searchType);
				}else if(type == 3){
					List<V3xOrgTeam> teamlist = new ArrayList<V3xOrgTeam>();
					teamlist = orgManager.getTeamsByMember(user.getId(),accId);
					orgMembers = this.getAllMembers(teamlist, searchType, "name");
				}else if(type ==4){
					List<V3xOrgTeam> teamlist = new ArrayList<V3xOrgTeam>();
					teamlist = orgManager.getTeamsByOwner(CurrentUser.get().getId());
					orgMembers = this.getAllMembers(teamlist, searchType, "name");
				}
					
			}else if(condition.equals("level")){
				if(type == 1){
                    //by Yongzhang 2008-11-14
                    orgMembers = this.departmentByLevel(this.filterMemberByAssigned(this.getAllMembersByLoginAccount(accId)), searchType);
				}else if(type == 2){
					addressMembers = this.addressBookManager.getMemberByLevelName(searchType);
				}else if(type == 3){
					List<V3xOrgTeam> teamlist = new ArrayList<V3xOrgTeam>();
					teamlist = orgManager.getTeamsByMember(user.getId(),accId);
					orgMembers = this.getAllMembers(teamlist, searchType, "level");
				}else if(type ==4){
					List<V3xOrgTeam> teamlist = new ArrayList<V3xOrgTeam>();
					teamlist = orgManager.getTeamsByOwner(CurrentUser.get().getId());
					orgMembers = this.getAllMembers(teamlist, searchType, "level");
				}
			}else if(condition.equals("tel")){
				if(type == 1){
					orgMembers = this.filterMemberByAssigned(this.departmentByName("tel", this.getAllMembersByLoginAccount(accId),searchType));
				}else if(type == 2){
					addressMembers = this.addressBookManager.getMemberByTel(searchType);
				}else if(type == 3){
					List<V3xOrgTeam> teamlist = new ArrayList<V3xOrgTeam>();
					teamlist = orgManager.getTeamsByMember(user.getId(),accId);
					orgMembers = this.getAllMembers(teamlist, searchType, "tel");
				}else if(type ==4){
					List<V3xOrgTeam> teamlist = new ArrayList<V3xOrgTeam>();
					teamlist = orgManager.getTeamsByOwner(CurrentUser.get().getId());
					orgMembers = this.getAllMembers(teamlist, searchType, "tel");
				}
					
			}
		}else if(Strings.isNotBlank(click)&&!click.equals("[object]")){
			if(click.equals("dept")){
				deptId = RequestUtils.getLongParameter(request, "deptId");
				String mem = request.getParameter("mem");
				if(mem != null && mem.equals("all")){
					List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
					members.addAll(getAllMembersByLoginAccount(deptId));
					
					orgMembers = this.filterMemberByAssigned(members);
					
				}
				else{
					
					orgMembers = this.filterMemberByAssigned(orgManager.getMembersByDepartment(deptId,false, true,accId,true));
					
				}
			}else if(click.equals("sysTeam")){
				Long sysId = RequestUtils.getLongParameter(request, "sysId");
				V3xOrgTeam team = orgManagerDirect.getTeamById(sysId);
				List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
				List<Long> leaderIds = team.getLeaders();
				List<Long> memberIds = team.getMembers();
				List<Long> supervisors = team.getSupervisors();
				List<Long> relations = team.getRelatives();
				leaderIds.addAll(memberIds);
				leaderIds.addAll(supervisors);
				leaderIds.addAll(relations);
				for(Long memberId : leaderIds){
					V3xOrgMember member = orgManagerDirect.getMemberById(memberId);
					if(member != null)
						if(members.size() == 0)
							members.add(member);
						else{
							if(!members.contains(member))
								members.add(member);
						}
				}
				orgMembers = this.filterMemberByAssigned(members);
			}else if(click.equals("ownTeam")){
				Long otId = RequestUtils.getLongParameter(request, "otId");
				if(type == 4){
					orgMembers = orgManager.getTeamMember(otId);
				}
				else{
					addressMembers = this.addressBookManager.getMembersByTeamId(otId);
				}
			}
		}else{
			if(type == 1){
				if(user.getAccountId() != user.getLoginAccount()){
					orgMembers = this.loadMembers(user/*,false*/);
				}else
					orgMembers = this.filterMemberByAssigned(orgManager.getMembersByDepartment(user.getDepartmentId(), true));
			}else if(type == 2){
				final Long creatorId = Long.valueOf(user.getId());
				addressMembers = addressBookManager
						.getMembersByCreatorId(creatorId);
			}
		}
		V3xOrgMember currentMember = orgManager.getMemberById(user.getId());
		boolean isInner = currentMember.getIsInternal();
		
		List<V3xOrgEntity> outerRight = null;
		if(!isInner){
			outerRight = orgManager.getExternalMemberWorkScope(currentMember.getId(),false);
		}
		List<V3xOrgMember> member1 = null;
		if(orgMembers != null){
			member1 = new ArrayList<V3xOrgMember>();
			for(V3xOrgMember member : orgMembers){
				boolean cls = this.checkLevelScope(member , currentMember,outerRight, deptId);
				if(cls)
					member1.add(member);
			}
		}
		
		Map<String,List> map = new HashMap<String,List>();
		map.put("orgMembers", member1);
		map.put("addressMembers", addressMembers);
		return map;
	}
	
	private void initDataRecord(DataRecord record,HttpServletRequest request,boolean bool){
		Locale locale = LocaleContext.getLocale(request);
		String resource = "com.seeyon.v3x.addressbook.resource.i18n.AddressBookResources";
		//员工通讯录
		String state_Public = ResourceBundleUtil.getString(resource, locale, "addressbook.menu.private.label");
		//私人通讯录
		String state_Private = ResourceBundleUtil.getString(resource, locale, "addressbook.menu.public.label");		
		//姓名
		String state_Name = ResourceBundleUtil.getString(resource, locale, "addressbook.username.label");
		//部门
		String state_Department = ResourceBundleUtil.getString(resource, locale, "addressbook.company.department.label");
		//主要岗位
		String state_PrimaryPost = ResourceBundleUtil.getString(resource, locale, "addressbook.account_form.primaryPost.label");
		//副岗
		String state_SecondPost = ResourceBundleUtil.getString(resource, locale, "addressbook.account_form.secondPost.label");
		//职务
		//branches_a8_v350_r_gov GOV-1891 lijl添加判断是不是政务版本,如果是则显示为职务,否则显示为职务级别---------------------------------------------------Start
		String state_Level = "";
        boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();
        if(isGovVersion){
        	state_Level = ResourceBundleUtil.getString(resource, locale, "addressbook.company.level.label.GOV");
        }else{
        	state_Level = ResourceBundleUtil.getString(resource, locale, "addressbook.company.level.label");
        }
      //lijl添加判断是不是政务版本,如果是则显示为职务,否则显示为职务级别---------------------------------------------------End
		//电子邮件
		String state_Email = ResourceBundleUtil.getString(resource, locale, "addressbook.email.label");
		//博客
		String state_Blog = ResourceBundleUtil.getString(resource, locale, "addressbook.account_form.blog.label");
		//网址
		String state_Website = ResourceBundleUtil.getString(resource, locale, "addressbook.account_form.website.labe");
		//电话
		String state_Familyphone = ResourceBundleUtil.getString(resource, locale, "addressbook.company.telephone.label");
		//手机
		String state_Mobilephone = ResourceBundleUtil.getString(resource, locale, "addressbook.mobilephone.label");
		//家庭住址
		String state_Address = ResourceBundleUtil.getString(resource, locale, "addressbook.account_form.address.label");
		//邮政编码
		String state_Postcode = ResourceBundleUtil.getString(resource, locale, "addressbook.account_form.postcode.label");
		//工号
		String state_Code = ResourceBundleUtil.getString(resource, locale, "addressbook.account_form.code.label");
		//类别
		String state_Category = ResourceBundleUtil.getString(resource, locale, "addressbook.account_form.category.label");
		//单位名称
		String state_CompanyName = ResourceBundleUtil.getString(resource, locale, "addressbook.account_form.companyName.label");
		//岗位
		String state_Post = ResourceBundleUtil.getString(resource, locale, "addressbook.company.post.label");
		//MSN
		String state_MSN = ResourceBundleUtil.getString(resource, locale, "addressbook.account_form.msn.label");
		//QQ
		String state_QQ = ResourceBundleUtil.getString(resource, locale, "addressbook.account_form.qq.label");		
		//单位电话
		String state_Telephone = ResourceBundleUtil.getString(resource, locale, "addressbook.company.telephone.label");
		//传真
		String state_Fax = ResourceBundleUtil.getString(resource, locale, "addressbook.account_form.fax.labe");		
		if(bool){
			record.setSheetName(state_Public);
			record.setTitle(state_Public);
			
			boolean isEnableLevel = isEnableLevel();
			String[] columnNames;
			if (isEnableLevel) {
				columnNames = new String[] { state_Name, state_Code, state_Department, state_PrimaryPost, state_SecondPost, state_Level, state_Email, state_Blog, state_Website, state_Familyphone, state_Mobilephone, state_Address, state_Postcode };
			} else {
				columnNames = new String[] { state_Name, state_Code, state_Department, state_PrimaryPost, state_SecondPost, state_Email, state_Blog, state_Website, state_Familyphone, state_Mobilephone, state_Address, state_Postcode };
			}
			
			record.setColumnName(columnNames);
		}else{
			record.setSheetName(state_Private);
			record.setTitle(state_Private);
			String[] columnNames = {state_Name,state_Category,state_CompanyName,state_Department,state_Post,state_Level,state_Email,state_Blog,state_Website,state_MSN,state_QQ,state_Telephone,state_Familyphone,state_Mobilephone,state_Fax,state_Address,state_Postcode};
			record.setColumnName(columnNames);
		}
	}
	
	/**
	 * 取得个人组内的所有成员--过滤重复的
	 * @author lucx
	 *
	 */
	private List<WebWithPropV3xOrgMember> getAllMembersByName(List<V3xOrgTeam> teamlist, String searchType,String type)throws Exception{
		List<WebWithPropV3xOrgMember> webMembers = new ArrayList<WebWithPropV3xOrgMember>();
		Set<V3xOrgMember> teams2 = new HashSet<V3xOrgMember>();
		List<V3xOrgMember> teams3 = new ArrayList<V3xOrgMember>();
		for(V3xOrgTeam team : teamlist){
			List<V3xOrgMember> teams =null;
			List<V3xOrgMember> teams1 = null;
			
			teams = orgManager.getTeamMember(team.getId()); //获取组的成员。由组长、组员构成
			teams1 = orgManager.getTeamRelative(team.getId());//获取组的相关人员。由组领导、关联人员构成
			teams2.addAll(teams);
			teams2.addAll(teams1);
						
		}
		teams3.addAll(teams2);//过滤掉重复的的人员显示
			List<WebWithPropV3xOrgMember> searchMembers = translateList(this.filterMemberByAssigned(teams3),-1);
			if(searchMembers != null){
				for(WebWithPropV3xOrgMember member : searchMembers){
					String con = "";
					if(type.equals("name")){
						con = member.getV3xOrgMember().getName();
						if(con.contains(searchType)){
							webMembers.add(member);
						}
					}else if(type.equals("level")){
						con = String.valueOf(member.getV3xOrgMember().getOrgLevelId());
						if(Strings.isNotBlank(con) && con.equals(searchType)){
							webMembers.add(member);
						}
					}else{
						con = member.getV3xOrgMember().getTelNumber();
						if(Strings.isNotBlank(con) && con.contains(searchType)){
							webMembers.add(member);
						}
					}
				}
			}
		
		return webMembers;
	}
	
	/**
	 * 取的所有的成员
	 * @author lucx
	 *
	 */
	private List<V3xOrgMember> getAllMembers(List<V3xOrgTeam> teamlist,String searchType, String type)throws Exception{
		List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
		List<WebWithPropV3xOrgMember> webMembers = this.getAllMembersByName(teamlist, searchType, type);
		for(WebWithPropV3xOrgMember webMember : webMembers){
			members.add(webMember.getV3xOrgMember());
		}
		return members;
	}
	
	/**
	 * 过滤掉其他单位的人员
	 * @author lucx
	 *
	 */
	private List<V3xOrgMember> getMemberByAccount(List<V3xOrgMember> members){
		List<V3xOrgMember> v3xMembers = new ArrayList<V3xOrgMember>();
		User user = CurrentUser.get();
		if(members != null){
			for(V3xOrgMember member : members){
				if(member.getOrgAccountId().equals(user.getAccountId())){
					v3xMembers.add(member);
				}
			}
		}
		return v3xMembers;
	}
	
	/**
	 * 获取当前登陆单位下所有的人员
	 * @author lucx
	 *
	 */
	private List<V3xOrgMember> getAllMembersByLoginAccount(Long accountId)throws BusinessException{
		List<V3xOrgMember> members = null;
		List<V3xOrgMember> members1 = null;
		members = orgManager.getAllMembers(accountId);
		members1 = orgManager.getAllExtMembers(accountId);//单位下所以外部的人员
		members.addAll(members1);
		List<V3xOrgRelationship> concurrentPosts = orgManagerDirect.getAllSidelineByAccount(accountId);//获取但前登录单位的兼职列表，不分页
		if(concurrentPosts != null && !concurrentPosts.isEmpty()){
			for(V3xOrgRelationship concurrentPost : concurrentPosts){
				members.add(orgManager.getMemberById(concurrentPost.getSourceId()));
			}
		}
		return members;
	}
	
	/**
	 * 检测 当前登录用户是否看到被检测者
	 * @param currentMemberId
	 * @param memberId
	 * @return
	 * @deprecated
	 * @see com.seeyon.v3x.common.taglibs.functions.Functions
	 */
	 public  boolean checkLevelScope(long currentMemberId, long memberId){
	    	try {
				V3xOrgMember currentMember = orgManager.getMemberById(currentMemberId); // 当前登录者
				V3xOrgMember member = orgManager.getMemberById(memberId); // 被检测的人

				if (currentMember == null || member == null) {
					return false;
				}
				
				//同一个部门
				if(currentMember.getOrgDepartmentId().longValue() == member.getOrgDepartmentId().longValue()){
					return true;
				}
				
				//相同的职务级别
				if(currentMember.getOrgLevelId().longValue() == member.getOrgLevelId().longValue()){
					return true;
				}
				
				//内部人员都可以看到外部人员
				if(currentMember.getOrgLevelId() == -1 || member.getOrgLevelId() == -1){
					return currentMember.getOrgLevelId() != -1;
				}
				
				// 副岗在这个部门的有权限
				if (MemberHelper.isSndPostContainDept(currentMember, member.getOrgDepartmentId())) {
					return true;
				}
				
				V3xOrgLevel currentMemberLevel = orgManager.getLevelById(currentMember.getOrgLevelId());
				int currentMemberLevelSortId = currentMemberLevel!=null ? currentMemberLevel.getLevelId() : 0;
				V3xOrgLevel level = orgManager.getLevelById(member.getOrgLevelId());
				V3xOrgLevel lowerLevel = orgManagerDirect.getLowestLevel(currentMember.getOrgAccountId());
				int memberLevelSortId = level!=null?level.getLevelId():lowerLevel!=null?lowerLevel.getLevelId():-1000;
				
				int currentAccountLevelScope = orgManager.getAccountById(currentMember.getOrgAccountId()).getLevelScope();

				if ((currentMember.getOrgDepartmentId().equals(member.getOrgDepartmentId()))
						|| currentAccountLevelScope < 0) {
					return true;
				}

				if (currentMemberLevelSortId - memberLevelSortId <= currentAccountLevelScope) {
					return true;
				}
			}
	    	catch (Exception e) {
	    		log.error("检测工作范围", e);
			}
	    	
	    	return false;
	    }
	
	/**
	 * 在人员列表中得到包含name的人员
	 */
	private List<V3xOrgMember> departmentByName(String condition, List<V3xOrgMember> members, String value){
		List<V3xOrgMember> results = new ArrayList<V3xOrgMember>();
		CharSequence charBuffer = new StringBuffer(value);
		if(members != null && !members.isEmpty()){
			for(V3xOrgMember member : members){
				if(condition.equals("name")){
					if(member.getName().contains(charBuffer) && !results.contains(member)){
						results.add(member);
					}
				}else{
					if(Strings.isNotBlank(member.getTelNumber()) && member.getTelNumber().contains(charBuffer) && !results.contains(member)){
						results.add(member);
					}
				}
			}
		}
		return results;
	}
	
	/**
	 * 在人员列表中得到包含level的人员
	 */
	private List<V3xOrgMember> departmentByLevel(List<V3xOrgMember> members, String level){
		List<V3xOrgMember> results = new ArrayList<V3xOrgMember>();
		if(members != null && !members.isEmpty()){
			for(V3xOrgMember member : members){
				if(member.getOrgLevelId() == Long.parseLong(level))
					results.add(member);
			}
		}
		return results;
	}
	
	/**
	 * 得到进入兼职单位时默认的人员列表
	 */
	private List<V3xOrgMember> loadMembers(User user/* ,  boolean isPagenate*/)throws BusinessException{
		List<V3xOrgMember> members = new ArrayList<V3xOrgMember>();
		List<V3xOrgDepartment> listdept = orgManager.getDepartmentsByUser(user.getId());
		for(V3xOrgDepartment dept : listdept){
			if(dept.getOrgAccountId() == user.getLoginAccount()){
				List<V3xOrgMember> temp = orgManager.getMembersByDepartment(dept.getId(), true);
				if(temp != null && !temp.isEmpty())
					members.addAll(temp);
				break;
			}
		}
		members.remove(orgManager.getMemberById(user.getId()));
//		filtV3xOrgMembers(members);
//		if(isPagenate) members = pagenate(members);
		return this.filterMemberByAssigned(members);
	}
	
	/**
	 * 人员过滤--去掉离职的人员
	 * @author lucx
	 *
	 */
	private List<V3xOrgMember> filterMemberByAssigned(List<V3xOrgMember> members){
		List<V3xOrgMember> results = new ArrayList<V3xOrgMember>();
		if(members != null && !members.isEmpty()){
			for(V3xOrgMember member : members){
				if(member.isValid())
					results.add(member);
			}
		}
		return results;
	}
	
	/**
	 * 过滤职务级别权限</br>
	 * 不要使用此方法，使用统一Functions中方法
	 * @author lucx
	 * @deprecated
	 */
	private  boolean checkLevelScope(V3xOrgMember member ,V3xOrgMember currentMember,List<V3xOrgEntity> outerRight, Long departmentId){
		try {
			
			if (currentMember == null || member == null) {
				return false;
			}
			
			//同一个部门
			if(currentMember.getOrgDepartmentId().longValue() == member.getOrgDepartmentId().longValue()){
				return true;
			}
			
			//相同的职务级别
			if(currentMember.getOrgLevelId().longValue() == member.getOrgLevelId().longValue()){
				return true;
			}
			
			//内部人员查看外部人员 1.跨靠部门 2.外部人员有权限 3.同一组
			if(currentMember.getIsInternal() && !member.getIsInternal()){
				V3xOrgDepartment curDep = orgManager.getDepartmentById(currentMember.getOrgDepartmentId());
				V3xOrgDepartment memDep = orgManager.getDepartmentById(member.getOrgDepartmentId());
				//跨靠部门
				if(curDep.getOrgAccountId().longValue() == memDep.getOrgAccountId() && curDep.getParentPath().equals(memDep.getParentPath())){
					return true;
				}
				//外部人员权限内部
				Long lonAccountId = CurrentUser.get().getLoginAccount();
				List<V3xOrgEntity> canReadList = orgManager.getExternalMemberWorkScope(member.getId(),false);
				List<Long> depIds = orgManager.getUserDomainIDs(currentMember.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT);
				for(V3xOrgEntity entity : canReadList){
					if(entity.getId().longValue() == currentMember.getId() || entity.getId().longValue() == currentMember.getOrgDepartmentId() || entity.getId().longValue() == currentMember.getOrgAccountId()||entity.getId().longValue()==lonAccountId || depIds.contains(entity.getId().longValue())){
						return true;
					}else{
						Map<Long, List<ConcurrentPost>> map = orgManager.getConcurentPostsByMemberId(lonAccountId, currentMember.getId());
						if(map!=null && map.containsKey(entity.getId())){
							return true;
						}
					}
				}
				//同一组
				List<V3xOrgTeam> teams = orgManager.getTeamsByMember(member.getId());
				for(V3xOrgTeam t : teams){
					List<Long> m1 = t.getAllMembers();
					for(Long mm : m1){
						if( mm.longValue() == currentMember.getId()){
							return true;
						}
					}
				}
				return false;
			}else if(!currentMember.getIsInternal() && member.getIsInternal()){//外部访问内部
				V3xOrgDepartment curDep = orgManager.getDepartmentById(currentMember.getOrgDepartmentId());
				V3xOrgDepartment memDep = orgManager.getDepartmentById(member.getOrgDepartmentId());
				//跨靠部门
				if(curDep.getOrgAccountId().longValue() == memDep.getOrgAccountId() && curDep.getParentPath().equals(memDep.getParentPath())){
					return true;
				}
				List<Long> depIds = orgManager.getUserDomainIDs(member.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_DEPARTMENT);
				List<Long> accountIds = orgManager.getUserDomainIDs(member.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID,V3xOrgEntity.ORGENT_TYPE_ACCOUNT);
				if(outerRight != null && !outerRight.isEmpty()){
					for(V3xOrgEntity entity : outerRight){
						if(entity.getId().longValue() == member.getId()){
							return true;
						}
						if(entity.getId().longValue() == member.getOrgDepartmentId()){
							return true;
						}
						if(entity.getId().longValue() == member.getOrgAccountId()){
							return true;
						}
						if(depIds.contains(entity.getId().longValue())){
							return true;
						}
						if(accountIds.contains(entity.getId().longValue())){
							return true;
						}
					}
				}
				//同一组
				List<V3xOrgTeam> teams = orgManager.getTeamsByMember(currentMember.getId());
				for(V3xOrgTeam t : teams){
					List<Long> m1 = t.getAllMembers();
					for(Long mm : m1){
						if( mm.longValue() == member.getId()){
							return true;
						}
					}
				}
				return false;
			}
			
			// 副岗在这个部门的有权限
			if (MemberHelper.isSndPostContainDept(currentMember, member.getOrgDepartmentId())) {
				return true;
			}
/*
			int currentAccountLevelScope = orgManager.getAccountById(currentMember.getOrgAccountId()).getLevelScope();
			if ((currentMember.getOrgDepartmentId().equals(member.getOrgDepartmentId()))
					|| currentAccountLevelScope < 0) {
				return true;
			}
			//切换的单位级别
			int newAccountLevelScope = orgManager.getAccountById(member.getOrgAccountId()).getLevelScope();
			if (newAccountLevelScope < 0) {
				return true;
			}
			int currentMemberLevelSortId=0;
			int accountLevelScope=0;
			V3xOrgLevel memberLevel = orgManager.getLevelById(member.getOrgLevelId());
			int memberLevelSortId = memberLevel!=null ? memberLevel.getLevelId() : 0;
			if(currentMember.getOrgAccountId().equals(member.getOrgAccountId())){
				V3xOrgLevel currentMemberLevel = orgManager.getLevelById(currentMember.getOrgLevelId());
				currentMemberLevelSortId = currentMemberLevel!=null ? currentMemberLevel.getLevelId() : 0;
				accountLevelScope = currentAccountLevelScope;
			}else{
				currentMemberLevelSortId = mappingLevelSortId(member,currentMember);
				accountLevelScope = newAccountLevelScope;
			}
			if (currentMemberLevelSortId - memberLevelSortId <= accountLevelScope) {
				return true;
			}
*/			return canAccess(departmentId, member);
		}
    	catch (Exception e) {
    		log.error("检测工作范围", e);
		}
    	
    	return false;
	}

	private List<ConcurrentPost> getConcurentPosts(Long memberId,
			Long accountId, Long departmentId) throws BusinessException {
		Map<Long, List<ConcurrentPost>> concurentPostsByMemberId = orgManager
				.getConcurentPostsByMemberId(accountId, memberId);
		if (concurentPostsByMemberId != null
				&& !concurentPostsByMemberId.isEmpty()) {
			return concurentPostsByMemberId.get(departmentId);
		} else {
			return null;
		}
	}

	/**
	 * 当前用户能否在部门departmentId内访问observedMember
	 * 
	 * @param departmentId
	 * @param observedMember
	 * @return
	 * @throws BusinessException
	 */
	private boolean canAccess(Long departmentId, V3xOrgMember observedMember)
			throws BusinessException {
		User user = CurrentUser.get();
		// 当前单位
		V3xOrgAccount currentAccount = orgManager.getAccountById(user
				.getLoginAccount());
		// 当前单位的工作范围设置
		int currentAccountLevelScope = currentAccount.getLevelScope();
		if (currentAccountLevelScope < 0) { // 无限制，则不必检查了
			return true;
		}
		if (departmentId != null) { // 在指定部门内是否可访问
			// 当前用户在当前部门的职务级别，取最高（数值最小）的那个
			int currentUserLevelId = Integer.MAX_VALUE;
			if (user.getAccountId() == currentAccount.getId().longValue()) { // 当前单位是当前用户的主岗单位
				currentUserLevelId = orgManager.getLevelById(user.getLevelId())
						.getLevelId();
			} else { // 当前单位是当前用户的兼职单位
				// 当前用户在当前部门的兼职职务级别
				List<ConcurrentPost> concurentPosts = getConcurentPosts(
						user.getId(), currentAccount.getId(), departmentId);
				if (concurentPosts != null) {
					for (ConcurrentPost cp : concurentPosts) {
						if (cp.getCntLevelId() != null) {
							int s = orgManager.getLevelById(cp.getCntLevelId())
									.getLevelId();
							if (s < currentUserLevelId) {
								currentUserLevelId = s;
							}
						}
					}
				}
			}
			// 当前待检人员在当前部门的职务级别，取最低（数值最大）的那个
			int observedLevelId = Integer.MIN_VALUE;
			if (observedMember.getOrgAccountId() == currentAccount.getId()
					.longValue()) { // 当前单位是当前待检人员的主岗单位
				observedLevelId = orgManager.getLevelById(
						observedMember.getOrgLevelId()).getLevelId();
			} else { // 当前单位是当前待检人员的兼职单位
				// 当前待检人员在当前部门的兼职职务级别
				List<ConcurrentPost> concurentPosts = getConcurentPosts(
						observedMember.getId(), currentAccount.getId(),
						departmentId);
				if (concurentPosts != null) {
					for (ConcurrentPost cp : concurentPosts) {
						if (cp.getCntLevelId() != null) {
							int s = orgManager.getLevelById(cp.getCntLevelId())
									.getLevelId();
							if (s > observedLevelId) {
								observedLevelId = s;
							}
						}
					}
				}
			}
			if (currentUserLevelId - currentAccountLevelScope <= observedLevelId) {
				return true;
			} else {
				return false;
			}
		} else { // 在指定单位内，逐个检查下属部门
			List<V3xOrgDepartment> departments = orgManager
					.getAllDepartments(currentAccount.getId());
			if (departments != null) {
				for (V3xOrgDepartment department : departments) {
					boolean canAccess = canAccess(department.getId(),
							observedMember);
					if (canAccess) {
						return true;
					}
				}
			}
			return false;
		}
	}

	//映射集团职务   by wusb 2010-09-25
	private int mappingLevelSortId(V3xOrgMember member, V3xOrgMember currentMember) throws BusinessException{
		int currentMemberLevelSortId=0;
		V3xOrgLevel level = null;
		User user = CurrentUser.get();
		boolean isNeedCheckLevelScope=true;
		if(user.isAdministrator() || user.isGroupAdmin() || user.isSystemAdmin()){ //管理员默认不限制
			isNeedCheckLevelScope = false;
		}
		if(isNeedCheckLevelScope){
			Map<Long, List<ConcurrentPost>> concurrentPostMap= orgManager.getConcurentPostsByMemberId(member.getOrgAccountId(),currentMember.getId());
			if(concurrentPostMap != null && !concurrentPostMap.isEmpty()){ //我在当前单位兼职
				Iterator<List<ConcurrentPost>> it = concurrentPostMap.values().iterator();
				boolean isExist=false;
				while(it.hasNext()){
					List<ConcurrentPost> cnPostList = it.next();
					for(ConcurrentPost cnPost : cnPostList){
						if(cnPost!=null){
							Long cnLevelId = cnPost.getCntLevelId();
							if(cnLevelId!=null){
								V3xOrgLevel cnLevel = orgManager.getLevelById(cnLevelId);
								if(cnLevel!=null){
									currentMemberLevelSortId = cnLevel.getLevelId();
									isExist=true;
									break;
								}else{ 
									level = orgManagerDirect.getLowestLevel(member.getOrgAccountId());
									currentMemberLevelSortId = level!=null ? level.getLevelId().intValue():0;
								}
							}
						}
					}
					if(isExist) break;
				}
				return currentMemberLevelSortId;
			}
			Long levelIdOfGroup = (currentMember.getOrgLevelId()!=-1) ? orgManager.getLevelById(currentMember.getOrgLevelId()).getGroupLevelId() : -1; //当前登录者对应集团的职务级别id
			//切换单位的所有职务级别
			List<V3xOrgLevel> levels = orgManagerDirect.getAllLevels(member.getOrgAccountId());
			for(V3xOrgLevel _level:levels){
				if(levelIdOfGroup!=null){
					if(levelIdOfGroup.equals(_level.getGroupLevelId())){
						level=_level;
						break;
					}
				}
			}
			if(level==null){
				level = orgManagerDirect.getLowestLevel(member.getOrgAccountId()); //最低职务级别
			}
			
			if(level!=null){
				currentMemberLevelSortId = level.getLevelId();
			}
		}
		return currentMemberLevelSortId;
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~``

	/**
	 * vcard保存
	 * @author lucx
	 *
	 */
	public ModelAndView vcard(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int addressbookType = RequestUtils.getIntParameter(request,"addressbookType");
		Long memberId = RequestUtils.getLongParameter(request, "memberId");
		WebStaffInfo webStaffInfos = new WebStaffInfo();
		String officeNum = "";
		String filename = "";
		String filename1 = "";
		V3xOrgMember member = new V3xOrgMember();
		AddressBookMember ABmember = new AddressBookMember();
		
		VCard vc = new VCard();
		if(1==addressbookType){
			if(memberId!=null){
				 member = orgManagerDirect.getMemberById(memberId);
			    webStaffInfos = this.translateV3xOrgMembers(member,null);
				//~~~~~~~~~~~~~办公电话的扩展属性
				//orgManager.loadEntityProperty(member);
			    officeNum = member.getProperty("officeNum");
				
			}
			String orgName = webStaffInfos.getOrg_name();
			String depName = webStaffInfos.getDepartment_name();
			filename = member.getName();
			filename1 = member.getLoginName();//登陆名
			vc.setName(member.getName());//卢呈祥
			vc.setComment(Strings.isBlank(webStaffInfos.getRemark())?"":webStaffInfos.getRemark());
			
			if (isEnableLevel()) {
				vc.setTitle(Strings.isBlank(webStaffInfos.getLevel_name()) ? "" : webStaffInfos.getLevel_name()); // 职务 （ 例 ：初级，高级）
			}
			
			vc.setOrganisation((Strings.isBlank(orgName)?"":orgName)+";"+(Strings.isBlank(depName)?"":depName));//      用友致远软件技术有限公司;研发二部
			vc.setAddress(Strings.isBlank(webStaffInfos.getAddress())?"": webStaffInfos.getAddress());//家庭住址
			vc.setPhone(Strings.isBlank(officeNum)?"": officeNum);//商务电话
			vc.setFax("");//商务传真
			vc.setHomeP(Strings.isBlank(webStaffInfos.getTelephone())?"" : webStaffInfos.getTelephone());//住宅电话
			vc.setMobilePhone(Strings.isBlank(member.getTelNumber())?"": member.getTelNumber());////移动电话
			vc.setEmail(Strings.isBlank(member.getEmailAddress())?"": member.getEmailAddress());
			
			
			
		}else{//外部联系人
			if(memberId!=null){
				ABmember = addressBookManager.getMember(memberId);
			}
			String companyName = ABmember.getCompanyName();
			String departName = ABmember.getCompanyDept();
			String company = Strings.isNotBlank(companyName)?companyName:"";
			String depart = Strings.isNotBlank(departName)?departName:"";
			 filename = ABmember.getName();//登陆名
			 filename1 = ABmember.getName();//姓名
			 	vc.setName(Strings.isBlank(ABmember.getName())?"":ABmember.getName());//卢呈祥
				vc.setComment(Strings.isBlank(ABmember.getMemo())?"":ABmember.getMemo());
				vc.setTitle(Strings.isBlank(ABmember.getCompanyLevel())?"":ABmember.getCompanyLevel());   //    职务    （ 例 ：初级，高级）
				vc.setOrganisation(company+";"+depart);//      用友致远软件技术有限公司;研发二部
				vc.setAddress(Strings.isBlank(ABmember.getAddress())? "" : ABmember.getAddress());//家庭住址
				vc.setPhone(Strings.isBlank(ABmember.getCompanyPhone())? "" : ABmember.getCompanyPhone());//商务电话
				vc.setFax(Strings.isBlank(ABmember.getFax())? "" : ABmember.getFax());//商务传真
				vc.setHomeP(Strings.isBlank(ABmember.getFamilyPhone())? "" : ABmember.getFamilyPhone());//住宅电话
				vc.setMobilePhone(Strings.isBlank(ABmember.getMobilePhone())? "" : ABmember.getMobilePhone());////移动电话
				vc.setEmail(Strings.isBlank(ABmember.getEmail())? "" : ABmember.getEmail());//email
				
		}
		
		String name1 = ""; 
		String name2 = ""; 
		char[] tempChar = filename.toCharArray(); 
		for (int kk = 0; kk < tempChar.length; kk++) { 
			if(kk==0){
				name1 += tempChar[kk];
			}else{
				name2+=tempChar[kk];
			}
			 
		} 
		vc.setNName(name1+";"+name2);//    卢;呈祥
		
		String str = vc.getVCard();
		
		String title1 = filename1+".vcf";
		request.setCharacterEncoding(contentTypeCharset);
		response.setCharacterEncoding(contentTypeCharset);
		response.setContentType("application/x-msdownload; charset=" + contentTypeCharset);
		title1 = URLEncoder.encode(title1, "UTF-8");
		response.setHeader("Content-disposition", "attachment;filename=\"" + title1 + "\"");
		
		OutputStream out = null;
		try {
			out = response.getOutputStream();
			
			IOUtils.write(str, out);
		}
		catch (Exception e) {
			if (e.getClass().getSimpleName().equals(this.clientAbortExceptionName)) {
				log.error("用户关闭下载窗口: " + e.getMessage());
			}
			else{
				log.error("", e);
			}
		}
		finally{
			IOUtils.closeQuietly(out);
		}
		
		return null;
	}

	/**
	 * 参照csvExport()导出CSV
	 */
	private void exportAsCsv(HttpServletRequest request,
			HttpServletResponse response, List data) throws Exception {
		List<V3xOrgMember> colleagues = null;
		List<AddressBookMember> personals = null;
		if (data == null || data.isEmpty()) {
			return;
		} else if (data.get(0) instanceof WebWithPropV3xOrgMember) {
			colleagues = new ArrayList<V3xOrgMember>(data.size());
			List<WebWithPropV3xOrgMember> l = (List<WebWithPropV3xOrgMember>) data;
			for (WebWithPropV3xOrgMember webOrgMember : l) {
				colleagues.add(webOrgMember.getV3xOrgMember());
			}
		} else if (data.get(0) instanceof AddressBookMember) {
			personals = data;
		}
		// 以下参照csvExport()
		List<Csv> csvs = null;
		if(colleagues!=null){
			csvs = generateCsvByColleague(colleagues);
		}else{
			if(personals!=null){
				csvs = generateCsvByPersonals(personals);
			}
		}
		String exportStr = getCsvStringByCsvs(csvs);
		String title1 = "WLMContacts"+".csv";
		//request.setCharacterEncoding(contentTypeCharset);
		//response.setCharacterEncoding(contentTypeCharset);
		title1 = URLEncoder.encode(title1, "UTF-8");
		response.setHeader("Content-disposition", "attachment;filename=\"" + title1 + "\"");
		response.setContentType("text/csv; charset=" + contentTypeCharset);
		//response.setContentType("application/x-msdownload; charset=" + contentTypeCharset);
		
		OutputStream out = null;
		try {
			byte[] b = exportStr.getBytes();
			out = response.getOutputStream();
			IOUtils.write(b, out);
		}
		catch (Exception e) {
			if (e.getClass().getSimpleName().equals(this.clientAbortExceptionName)) {
				log.error("用户关闭下载窗口: " + e);
			}
			else{
				log.error("", e);
			}
		}
		finally{
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * CSV 导出
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 * @deprecated 请用export()
	 */
	public ModelAndView csvExport(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		Map<String,List> map = getDownList(request);
		if(map!=null){
			List<V3xOrgMember> colleagues = map.get("orgMembers");
			List<AddressBookMember>  personals = map.get("addressMembers");
			List<Csv> csvs = null;
			if(colleagues!=null){
				csvs = generateCsvByColleague(colleagues);
			}else{
				if(personals!=null){
					csvs = generateCsvByPersonals(personals);
				}
			}
			String exportStr = getCsvStringByCsvs(csvs);
			String title1 = "WLMContacts"+".csv";
			//request.setCharacterEncoding(contentTypeCharset);
			//response.setCharacterEncoding(contentTypeCharset);
			title1 = URLEncoder.encode(title1, "UTF-8");
			response.setHeader("Content-disposition", "attachment;filename=\"" + title1 + "\"");
			response.setContentType("text/csv; charset=" + contentTypeCharset);
			//response.setContentType("application/x-msdownload; charset=" + contentTypeCharset);
			
			OutputStream out = null;
			try {
				byte[] b = exportStr.getBytes();
				out = response.getOutputStream();
				IOUtils.write(b, out);
			}
			catch (Exception e) {
				if (e.getClass().getSimpleName().equals(this.clientAbortExceptionName)) {
					log.error("用户关闭下载窗口: " + e);
				}
				else{
					log.error("", e);
				}
			}
			finally{
				IOUtils.closeQuietly(out);
			}
		}
		
		return null;
	}
	
	private List<Csv> generateCsvByColleague(List<V3xOrgMember> list)throws Exception{
		List<Csv> csvs = new ArrayList<Csv>();
		if(list!=null){
			for(V3xOrgMember m : list){
				V3xOrgLevel leavel = orgManager.getLevelById(m.getOrgLevelId());
				String leavelName = leavel!=null?leavel.getName():"";
				Csv c = new Csv();
				c.setDepartmentName(orgManager.getDepartmentById(m.getOrgDepartmentId()).getName());
				c.setMobilePhone(m.getTelNumber());
				c.setEmail(m.getEmailAddress());
				c.setCompayName(orgManager.getAccountById(m.getOrgAccountId()).getName());
				c.setLevelName(leavelName);
				c.setMobilePhone(m.getTelNumber());
				c.setName(Functions.showMemberName(m));
				c.setOfficePhone(m.getCode());
				csvs.add(c);
			}
		}
		return csvs;
	}
	
	private List<Csv> generateCsvByPersonals(List<AddressBookMember> list){
		List<Csv> csvs = new ArrayList<Csv>();
		if(list!=null){
			for(AddressBookMember m : list){
				Csv c = new Csv();
				c.setCompayName(m.getCompanyName());
				c.setMobilePhone(m.getMobilePhone());
				c.setEmail(m.getEmail());
				c.setBlogUrl(m.getBlog());
				c.setDepartmentName(m.getCompanyDept());
				c.setFamilyAddress(m.getAddress());
				c.setFamilyPost(m.getPostcode());
				c.setLevelName(m.getCompanyLevel());
				c.setMobilePhone(m.getMobilePhone());
				c.setName(m.getName());
				c.setOfficePhone(m.getCompanyPhone());
				csvs.add(c);
			}
		}
		return csvs;
	}
	private String getCsvStringByCsvs(List<Csv> csvs){
		boolean isEnableLevel = isEnableLevel();
		StringBuffer buffer = new StringBuffer(getTitle(isEnableLevel));
		buffer.append("\r\n");
		if(csvs!=null){
			for(Csv c : csvs){
				buffer.append(c.getCsv(isEnableLevel));
			}
		}
		return buffer.toString();
	}
	
	private String getTitle(boolean isEnableLevel){
		//String s = "名,姓,中文称谓,单位,部门,职务,住宅地址 街道,住宅地址 邮政编码,单位主要电话,移动电话,电子邮件地址,电子邮件类型,电子邮件显示名称,网页";
		String resource = "com.seeyon.v3x.addressbook.resource.i18n.AddressBookResources";
		//员工通讯录
		String s = ResourceBundleUtil.getString(resource, "export.csv.title", isEnableLevel ? 1 : 0);
		
		String[] str = s.split(",");
		StringBuffer ss = new StringBuffer();
		for(String st : str){
			if (Strings.isNotBlank(st)) {
				ss.append("\"");
				ss.append(st);
				ss.append("\"");
				ss.append(",");
			}
		}
		return ss.toString();
	}
	
	public ModelAndView importCard(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("addressbook/io/importVcard");
		return mav;
		}

	public ModelAndView importExcel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("addressbook/io/importExcel");
		return mav;
		}	
	
	public ModelAndView doVcardImoprt(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		String memberId = request.getParameter("memberId");
		String categoryId = request.getParameter("categoryId");
		File file = uploadFile(request);
		String path = file.getAbsolutePath()+".vcf";
		File realfile = new File(path);
		DataUtil.CopyFile(file,realfile);
		
		String result = addressBookManager.doImport(realfile, categoryId, memberId);
		
		if("OK".equals(result)){
			super.rendJavaScript(response, "parent.window.location.reload();");
		}else if("ExistSameName".equals(result)){
			super.rendJavaScript(response, "alert(parent.v3x.getMessage(\"ADDRESSBOOKLang.addressbook_isexist_name\"));");
		}
		
		return null;
	}
	
	public ModelAndView doCSVImoprt(HttpServletRequest request,HttpServletResponse response)throws Exception{
		
		String memberId = request.getParameter("memberId");
		String categoryId = request.getParameter("categoryId");
		File file = uploadFile(request);
		String path = file.getAbsolutePath()+".csv";
		File realfile = new File(path);
		DataUtil.CopyFile(file,realfile);
		
		addressBookManager.doCsvImport(realfile, categoryId, memberId);
		super.rendJavaScript(response, "parent.window.location.reload();");
		return null;
	}
	
	private File uploadFile(HttpServletRequest request) throws Exception {
		File fil = null;
		try {
			V3XFile v3x = null;
			List v3xFiles = fileManager.create(ApplicationCategoryEnum.edoc, request);
			if(null!=v3xFiles && v3xFiles.size()>0){
				v3x = (V3XFile)v3xFiles.get(0);
				fil = fileManager.getFile(v3x.getId(),v3x.getCreateDate());
			}
		} catch (Exception e) {
			log.error("", e);
		}
		return fil;
	}
	/**
	 * @return the fileManager
	 */
	public FileManager getFileManager() {
		return fileManager;
	}
	/**
	 * @param fileManager the fileManager to set
	 */
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	
}