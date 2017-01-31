/**
 * 
 */
package com.seeyon.v3x.common.selectPeople;

import static com.seeyon.v3x.edoc.domain.EdocObjTeam.ENTITY_TYPE_OrgTeam;
import static com.seeyon.v3x.exchange.domain.ExchangeAccount.ENTITY_TYPE_EXCHANGEACCOUNT;
import static com.seeyon.v3x.organization.domain.OrgModifyState.HeadModifyState;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_ACCOUNT;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_DEPARTMENT;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_LEVEL;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_MEMBER;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_POST;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_ROLE;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.ORGENT_TYPE_TEAM;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.TOXML_PROPERTY_Email;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.TOXML_PROPERTY_Mobile;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.TOXML_PROPERTY_NAME;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.TOXML_PROPERTY_id;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.TOXML_PROPERTY_isInternal;
import static com.seeyon.v3x.organization.domain.V3xOrgEntity.VIRTUAL_ACCOUNT_ID;
import static com.seeyon.v3x.peoplerelate.domain.PeopleRelate.ENTITY_TYPE_RelatePeople;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import www.seeyon.com.v3x.form.base.SeeyonForm_Runtime;
import www.seeyon.com.v3x.form.controller.formservice.inf.IOperBase;
import www.seeyon.com.v3x.form.controller.pageobject.SessionObject;
import www.seeyon.com.v3x.form.controller.pageobject.TableFieldDisplay;

import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.flag.SysFlag;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.taglibs.functions.MainFunction;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.login.CurrentUserToSeeyonApp;
import com.seeyon.v3x.common.web.util.WebUtil;
import com.seeyon.v3x.edoc.domain.EdocObjTeam;
import com.seeyon.v3x.edoc.manager.EdocObjTeamManager;
import com.seeyon.v3x.exchange.domain.ExchangeAccount;
import com.seeyon.v3x.exchange.manager.ExchangeAccountManager;
import com.seeyon.v3x.main.AccountSymbol;
import com.seeyon.v3x.main.MainDataLoader;
import com.seeyon.v3x.mobile.message.manager.MobileMessageManager;
import com.seeyon.v3x.online.util.OuterWorkerAuthUtil;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgLevel;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.domain.V3xOrgRole;
import com.seeyon.v3x.organization.domain.V3xOrgTeam;
import com.seeyon.v3x.organization.domain.secondarypost.ConcurrentPost;
import com.seeyon.v3x.organization.domain.secondarypost.MemberPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.peoplerelate.RelationType;
import com.seeyon.v3x.peoplerelate.manager.PeopleRelateManager;
import com.seeyon.v3x.space.Constants;
import com.seeyon.v3x.space.SpaceException;
import com.seeyon.v3x.space.domain.SpaceModel;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Strings;

/**
 * 
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2006-10-13
 */
public class SelectPeopleManager {
	private static final Log log = LogFactory.getLog(SelectPeopleManager.class);
	
	private static final String OrganizationResources = "com.seeyon.v3x.organization.resources.i18n.OrganizationResources";

	private OrgManager orgManager;
	
	private SpaceManager spaceManager;
	
	private ExchangeAccountManager exchangeAccountManager;
	
	private EdocObjTeamManager edocObjTeamManager;
	
	private PeopleRelateManager peoplerelateManager;
	
	private MobileMessageManager mobileMessageManager;
	
	public void setPeoplerelateManager(PeopleRelateManager peoplerelateManager) {
		this.peoplerelateManager = peoplerelateManager;
	}
	
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	
	public void setExchangeAccountManager(
			ExchangeAccountManager exchangeAccountManager) {
		this.exchangeAccountManager = exchangeAccountManager;
	}

	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}
	
	private Map<String, Date> orgDate = new Hashtable<String, Date>();
	private Map<String, String> orgString = new Hashtable<String, String>();
	
	public void init(){
		try {
			long startTime = System.currentTimeMillis();

			List<V3xOrgAccount> allAccounts = this.orgManager.getAllAccounts();
			Date time = new Date(0);
			this.getAllOrgEnt_Account(time);
			
			for (V3xOrgAccount account : allAccounts) {
				if(account.getIsRoot()){
					continue;
				}
				
				long accountId = account.getId();
				this.getAllOrgEnt_Department(time, accountId);
				this.getAllOrgEnt_Level(time, accountId);
				this.getAllOrgEnt_Member(time, accountId);
				this.getAllOrgEnt_Post(time, accountId);
				this.getAllRole(time, accountId);
			}
			
			this.getAllAdmin(time, V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			
			log.info("初始化选人界面数据！" + (System.currentTimeMillis() - startTime) + " MS");
		}
		catch (BusinessException e) {
			log.error("", e);
		}
	}

	/**
	 * 加载组织模型，不加载单位信息，此方法为AJAX服务
	 * 
	 * @param timestamp
	 *            ，时间戳，如：Department=123123423523;Post=34509u2394213p054;
	 * @param loginAccountId
	 * @param memberId
	 * @return
	 */
	public StringBuilder getOrgModel(String timestamp, long loginAccountId, long memberId) {
		User user = CurrentUser.get();
		Map<String, Date> map = new HashMap<String, Date>();
		if(StringUtils.isNotBlank(timestamp)){
			StringTokenizer timestamps = new StringTokenizer(timestamp, "=;");
	
			while (timestamps.hasMoreTokens()) {
				String key = timestamps.nextToken();
				Date value = new Date(new Long(timestamps.nextToken()));
	
				map.put(key, value);
			}
		}
		
		V3xOrgMember member = null;
		try {
			member = this.orgManager.getMemberById(memberId);
		}
		catch (Exception e1) {
		}
		
		Date timestampDateRoot = map.get(HeadModifyState + "Root");
		Date timestampDate = map.get(HeadModifyState);
		if(timestampDate == null){
			timestampDate = new Date(0L);
		}
		if(timestampDateRoot == null){
			timestampDateRoot = new Date(0L);
		}

		StringBuilder result = new StringBuilder();
		result.append("{");
		boolean isRootAccount = false;
		
		try {
			isRootAccount = this.orgManager.getRootAccount().getId().equals(loginAccountId);
		}
		catch (Exception e) {
		}
		
		try {
			// 加载单位
			String allAccount = getAllOrgEnt_Account(timestampDate);
			if (allAccount != null) {
				result.append(ORGENT_TYPE_ACCOUNT).append(" : ").append(allAccount).append(", ");
			}
			
			// 加载部门
			if(!isRootAccount){
				String allDepartment = getAllOrgEnt_Department(timestampDate, loginAccountId);
				if (allDepartment != null) {
					result.append(ORGENT_TYPE_DEPARTMENT).append(" : ").append(allDepartment).append(", ");
				}
			}
			
			// 加载人员
			if(!isRootAccount){
				String allMember = getAllOrgEnt_Member(timestampDate, loginAccountId);
				if (allMember != null) {
					result.append(ORGENT_TYPE_MEMBER).append(" : ").append(allMember).append(", ");
				}
			}
	
			// 加载岗位
			String allPost = getAllOrgEnt_Post(timestampDate, loginAccountId);
			if (allPost != null) {
				result.append(ORGENT_TYPE_POST).append(" : ").append(allPost).append(", ");
			}
			
			// 加载职务级别
			String allLevel = getAllOrgEnt_Level(timestampDate, loginAccountId);
			if (allLevel != null) {
				result.append(ORGENT_TYPE_LEVEL).append(" : ").append(allLevel).append(", ");
			}
	
			// 加载组
			String allTeam = getAllOrgEnt_Team(timestampDate, timestampDateRoot, loginAccountId, memberId);
			if (allTeam != null) {
				result.append(ORGENT_TYPE_TEAM).append(" : ").append(allTeam).append(", ");
			}
	
			// 加载角色
			String allRole = getAllRole(timestampDate, loginAccountId);
			if (allRole != null) {
				result.append(ORGENT_TYPE_ROLE).append(" : ").append(allRole).append(", ");
			}
			
			//兼职
			String concurent = getConcurent(timestampDate, loginAccountId);
			if(concurent != null){
				result.append("Concurent").append(" : ").append(concurent.toString()).append(", ");
			}
			
			//加载外部单位，用于公文交换
			String allExchangeAccount = getExchangeAccount(map.get(ENTITY_TYPE_EXCHANGEACCOUNT), loginAccountId); 
			if(Strings.isNotBlank(allExchangeAccount)){
				result.append(ENTITY_TYPE_EXCHANGEACCOUNT).append(" : ").append(allExchangeAccount).append(", ");
			}
			
			String allOrgTeam = getOrgTeam(map.get(ENTITY_TYPE_OrgTeam), loginAccountId); 
			if(Strings.isNotBlank(allOrgTeam)){
				result.append(ENTITY_TYPE_OrgTeam).append(" : ").append(allOrgTeam).append(", ");
			}
			
			String allPeoplerelate = getPeoplerelate(map.get(ENTITY_TYPE_RelatePeople), loginAccountId, memberId); 
			if(Strings.isNotBlank(allPeoplerelate)){
				result.append(ENTITY_TYPE_RelatePeople).append(" : ").append(allPeoplerelate).append(", ");
			}
			
			//外部人员，取出我的工作范围
			if(member != null && !member.getIsInternal()){
				String emw = getExternalMemberWorkScope(timestampDate, member.getId(), member.getOrgAccountId());
				if(Strings.isNotBlank(emw)){
					result.append("ExternalMemberWorkScope : ").append(emw).append(", ");
				}
			}
			
			String extMembers = "";
			if(user.isAdmin()){
				//系统管理员，能访问所有单位的外部人员
				extMembers = getAllAccountsExtMember(timestampDate, loginAccountId);
			} else if(member != null && member.getIsInternal()){
				//内部人员，能访问哪些外部人员
				extMembers = getExtMemberScopeOfInternal(timestampDate, member.getId(), member.getOrgAccountId());
			}
			if(Strings.isNotBlank(extMembers)){
				result.append("ExtMemberScopeOfInternal : ").append(extMembers).append(", ");
			}
			
			//管理员
			String allAdmins = getAllAdmin(timestampDate, V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
			if(Strings.isNotBlank(allAdmins)){
				result.append("Admin:").append(allAdmins).append(", ");
			}
			
			//表单控件，只能从表单制作环节使用，它相从sessionObject中取数据
			String formFields = getFormFields();
			if(Strings.isNotBlank(formFields)){
				result.append("FormField:").append(formFields).append(", ");
			}

			result.append("timestamp").append(" : \"").append(getModifiedTimeStamps(loginAccountId)).append("\"");
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		result.append("}");
		
		return result;
	}
	
	/**
	 * 选人界面扩展查询方法增加参数，是否检查工作范围
	 * @param name 名称
	 * @param isNeedCheckLevelScope 是否检查工作范围 true检查false不检查
	 * @return 选人界面人员json串
	 * add by lilong 2012-11-30
	 * Fix BUG AEIGHT-8870
	 */
	public StringBuilder getQueryOrgModel(String name, Boolean isNeedCheckLevelScope) {
		if(null == isNeedCheckLevelScope) isNeedCheckLevelScope = true;
		if(isNeedCheckLevelScope) {
			return getQueryOrgModel(name);
		} else {
			User user = CurrentUser.get();
			StringBuilder sb = new StringBuilder();
			sb.append("{");

			try {
				List<V3xOrgMember> allMembers = new ArrayList<V3xOrgMember>();
				
				List<V3xOrgAccount> accessableAccounts = orgManager.accessableAccounts(user.getId());
				for (V3xOrgAccount account : accessableAccounts) {
					List<V3xOrgMember> members = orgManager.getAllMembers(account.getId());
					if (CollectionUtils.isNotEmpty(members)) {
						allMembers.addAll(members);
					}
				}
				if (CollectionUtils.isNotEmpty(allMembers)) {
					StringBuilder sb2 = new StringBuilder();
					sb2.append(ORGENT_TYPE_MEMBER).append(" : ").append("[");
					boolean needMobile = mobileMessageManager.isValidateMobileMessage();
					int i = 0;
					for (V3xOrgMember member : allMembers) {
						if(null == member.getName()) continue;//AEIGHT-10030
						if (member.getName().toLowerCase().indexOf(name) != -1) {
							
							if (i ++ != 0) {
								sb2.append(",");
							}
							
							sb2.append("{");
							sb2.append(TOXML_PROPERTY_id).append(":\"").append(member.getId()).append("\"");
							sb2.append(",S:").append(member.getSortId());
							sb2.append(",P:\"").append(member.getOrgPostId()).append("\"");
							sb2.append(",L:\"").append(member.getOrgLevelId()).append("\"");
							sb2.append(",D:\"").append(member.getOrgDepartmentId()).append("\"");
							String deptName = "";
							V3xOrgDepartment dept = Functions.getDepartment(member.getOrgDepartmentId());
							if (dept != null) {
								deptName = dept.getName();
							}
							sb2.append(",DM:\"").append(Strings.escapeJavascript(deptName)).append("\"");
							sb2.append(",A:\"").append(member.getOrgAccountId()).append("\"");

							if (!member.getIsInternal()) {
								sb2.append(",").append(TOXML_PROPERTY_isInternal).append(":0");
							}
							
							sb2.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(member.getName())).append("\"");

							if (Strings.isNotBlank(member.getEmailAddress())) {
								sb2.append(",").append(TOXML_PROPERTY_Email).append(":\"").append(Strings.escapeJavascript(member.getEmailAddress())).append("\"");
							}
							
							if (needMobile && Strings.isNotBlank(member.getTelNumber())) {
								sb2.append(",").append(TOXML_PROPERTY_Mobile).append(":\"").append(Strings.escapeJavascript(member.getTelNumber())).append("\"");
							}
							sb2.append("}");
						}
					}
					
					sb2.append("]");
					
					sb.append(sb2);
				}
			} catch (Exception e) {
				log.error("全集团范围内查询部门、人员: ", e);
			}

			sb.append("}");
			return sb;
		}
	}
	
	/**
	 * 选人界面在全集团范围内查询部门、人员
	 */
	public StringBuilder getQueryOrgModel(String name) {
		User user = CurrentUser.get();
		StringBuilder sb = new StringBuilder();
		sb.append("{");

		try {
			List<V3xOrgMember> allMembers = new ArrayList<V3xOrgMember>();
			
			List<V3xOrgAccount> accessableAccounts = orgManager.accessableAccounts(user.getId());
			for (V3xOrgAccount account : accessableAccounts) {
				List<V3xOrgMember> members = orgManager.getAllMembers(account.getId());
				if (null != members && CollectionUtils.isNotEmpty(members)) {
					allMembers.addAll(members);
				}
			}

			if (CollectionUtils.isNotEmpty(allMembers)) {
				StringBuilder sb2 = new StringBuilder();
				sb2.append(ORGENT_TYPE_MEMBER).append(" : ").append("[");
				boolean needMobile = mobileMessageManager.isValidateMobileMessage();
				int i = 0;
				for (V3xOrgMember member : allMembers) {
					if(null == member.getName()) continue;//AEIGHT-10030
					if (member.getName().toLowerCase().indexOf(name) != -1) {
						if (!user.isAdmin() && !Functions.checkLevelScope(user.getId(), member.getId())) {
							continue;
						}

						if (i ++ != 0) {
							sb2.append(",");
						}
						
						sb2.append("{");
						sb2.append(TOXML_PROPERTY_id).append(":\"").append(member.getId()).append("\"");
						sb2.append(",S:").append(member.getSortId());
						sb2.append(",P:\"").append(member.getOrgPostId()).append("\"");
						sb2.append(",L:\"").append(member.getOrgLevelId()).append("\"");
						sb2.append(",D:\"").append(member.getOrgDepartmentId()).append("\"");
						String deptName = "";
						V3xOrgDepartment dept = Functions.getDepartment(member.getOrgDepartmentId());
						if (dept != null) {
							deptName = dept.getName();
						}
						sb2.append(",DM:\"").append(Strings.escapeJavascript(deptName)).append("\"");
						sb2.append(",A:\"").append(member.getOrgAccountId()).append("\"");

						if (!member.getIsInternal()) {
							sb2.append(",").append(TOXML_PROPERTY_isInternal).append(":0");
						}
						
						sb2.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(member.getName())).append("\"");

						if (Strings.isNotBlank(member.getEmailAddress())) {
							sb2.append(",").append(TOXML_PROPERTY_Email).append(":\"").append(Strings.escapeJavascript(member.getEmailAddress())).append("\"");
						}
						
						if (needMobile && Strings.isNotBlank(member.getTelNumber())) {
							sb2.append(",").append(TOXML_PROPERTY_Mobile).append(":\"").append(Strings.escapeJavascript(member.getTelNumber())).append("\"");
						}

						if (member.getSecond_post() != null && !member.getSecond_post().isEmpty()) {
							sb2.append(",F:[");
							int j = 0;
							for (MemberPost memberPost : member.getSecond_post()) {
								V3xOrgDepartment dep = orgManager.getDepartmentById(memberPost.getDepId());
								if (dep != null) {
									V3xOrgPost post = orgManager.getPostById(memberPost.getPostId());
									if (post != null) {
										if (j ++ != 0) {
											sb2.append(",");
										}
										
										sb2.append("[");
										sb2.append(dep.getId());
										sb2.append(",");
										sb2.append(post.getId());
										sb2.append("]");
									}
								}
							}
							
							sb2.append("]");
						}

						sb2.append("}");
					}
				}
				
				sb2.append("]");
				
				sb.append(sb2);
			}
		} catch (Exception e) {
			log.error("全集团范围内查询部门、人员: ", e);
		}

		sb.append("}");
		return sb;
	}

	/**
	 * 加载组织模型的时间戳 timestamp
	 * 格式为 
	 * 
	 * @param entType
	 * @return
	 */
	private String getModifiedTimeStamps(long loginAccountId) {
		StringBuilder timeStamps = new StringBuilder();
		try {			
			timeStamps.append(HeadModifyState + "=" + orgManager.getModifiedTimeStamp(HeadModifyState, loginAccountId).getTime()).append(";");
			timeStamps.append(HeadModifyState + "Root=" + orgManager.getModifiedTimeStamp(HeadModifyState, orgManager.getRootAccount().getId()).getTime()).append(";");
			timeStamps.append(ENTITY_TYPE_EXCHANGEACCOUNT + "=" + this.exchangeAccountManager.getLastModifyTimestamp().getTime()).append(";");
			timeStamps.append(ENTITY_TYPE_OrgTeam + "=" + this.edocObjTeamManager.getLastModifyTimestamp().getTime());
		}
		catch (Exception e) {
			log.error("下行组织模型时间戳异常", e);
		}
		
		return timeStamps.toString();
	}
	
	private String getAllOrgEnt_Account(Date time){
		try {
			String key = ORGENT_TYPE_ACCOUNT + "_" + VIRTUAL_ACCOUNT_ID;
			Date lastM = orgDate.get(key);
			if(lastM == null){
				lastM = new Date(0L);
			}
			
			if (orgManager.isModified(HeadModifyState, lastM, VIRTUAL_ACCOUNT_ID)){
				StringBuilder a = new StringBuilder();
				a.append("[");
				
				int i = 0;
				List<V3xOrgAccount> allAccounts = orgManager.getAllAccounts();
				for (V3xOrgAccount account : allAccounts) {
					if(i++ != 0){
						a.append(",");
					}
					account.toJsonString(a);
				}
				a.append("]");
				
				lastM = orgManager.getModifiedTimeStamp(HeadModifyState, VIRTUAL_ACCOUNT_ID);
				this.orgString.put(key, a.toString());
				this.orgDate.put(key, lastM);
			}
			
			if (!lastM.equals(time)) { // 前段数据时间戳与后段一直，不用加载
				return this.orgString.get(key);
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return null;
	}

	/**
	 * 加载部门信息
	 * 
	 * @param loginAccountId
	 * @return
	 */
	private String getAllOrgEnt_Department(Date time,
			long loginAccountId) {
		try {
			String key;// = ORGENT_TYPE_DEPARTMENT + "_" +loginAccountId;
			User user = CurrentUser.get();
			boolean externalMember = user != null && !user.isInternal();
			if (externalMember) { // 外部人员
				key = ORGENT_TYPE_DEPARTMENT + "_" + loginAccountId + "_"
						+ user.getId();
			} else { // 内部人员
				key = ORGENT_TYPE_DEPARTMENT + "_" + loginAccountId;
			}
			Date lastM = orgDate.get(key);
			if(lastM == null){
				lastM = new Date(0L);
			}
			
			if (orgManager.isModified(HeadModifyState, lastM, loginAccountId)){
				StringBuilder a = new StringBuilder();
				a.append("[");
				
				int i = 0;
				Collection<V3xOrgDepartment> deps;
				if (externalMember) { // 外部人员
					deps = OuterWorkerAuthUtil.getCanAccessDep(user.getId(),
							user.getDepartmentId(), user.getAccountId(),
							orgManager);
				} else { // 内部人员
					deps = orgManager.getAllDepartments(loginAccountId);
				}
				for (V3xOrgDepartment department : deps) {
					if(i++ != 0){
						a.append(",");
					}
					department.toJsonString(a, orgManager);
				}
				a.append("]");
				
				lastM = orgManager.getModifiedTimeStamp(HeadModifyState, loginAccountId);
				this.orgString.put(key, a.toString());
				this.orgDate.put(key, lastM);
				
			}
			
			if (!lastM.equals(time)) { // 前段数据时间戳与后段一直，不用加载
				return this.orgString.get(key);
			}
		}
		catch (Exception e) {
			log.error("", e);
		}

		return null;
	}

	/**
	 * 加载所有人员
	 * 
	 * @param loginAccountId
	 * @return [{D:0,K:"3000740460148573035",N:"徐石",P:0,L:7},{D:1,K:"-2509390303980075869",N:"张三",P:1,L:8}]
	 */
	private String getAllOrgEnt_Member(Date time,
			long loginAccountId) {
		try {
			String key = ORGENT_TYPE_MEMBER + "_" +loginAccountId;
			Date lastM = orgDate.get(key);
			if(lastM == null){
				lastM = new Date(0L);
			}
			
			if (orgManager.isModified(HeadModifyState, lastM, loginAccountId)) { // 前段数据时间戳与后段一直，不用加载
				
				boolean needMobile = mobileMessageManager.isValidateMobileMessage();
				
				StringBuilder a = new StringBuilder();
				a.append("[");
				List<V3xOrgMember> members = orgManager.getAllMembers(loginAccountId);
				List<V3xOrgMember> extMembers = orgManager.getAllExtMembers(loginAccountId);

				int i = 0;
				if(members != null){
					for (V3xOrgMember member : members) {
						if(i++ != 0){
							a.append(",");
						}
						member.toJsonString(a, orgManager, needMobile);
					}
				}
				
				if(extMembers != null){
					for (V3xOrgMember member : extMembers) {
						if(i++ != 0){
							a.append(",");
						}
						member.toJsonString(a, orgManager, needMobile);
					}
				}
				
				a.append("]");
				
				lastM = orgManager.getModifiedTimeStamp(HeadModifyState, loginAccountId);
				this.orgString.put(key, a.toString());
				this.orgDate.put(key, lastM);
			}
			
			if (!lastM.equals(time)) { // 前段数据时间戳与后段一直，不用加载
				return this.orgString.get(key);
			}
		}
		catch (Exception e) {
			log.error("", e);
		}

		return null;
	}

	/**
	 * 加载所有岗位
	 * 
	 * @param myLoginDepartId
	 * @return
	 */
	private String getAllOrgEnt_Post(Date time, long loginAccountId) {
		try {
			String key = ORGENT_TYPE_POST + "_" +loginAccountId;
			Date lastM = orgDate.get(key);
			if(lastM == null){
				lastM = new Date(0L);
			}
			
			if (orgManager.isModified(HeadModifyState, lastM, loginAccountId)) { // 前段数据时间戳与后段一直，不用加载
				StringBuilder a = new StringBuilder();
				a.append("[");
				
				int i = 0;
				List<V3xOrgPost> posts = orgManager.getAllPosts(loginAccountId);
				for (V3xOrgPost post : posts) {
					if(i++ != 0){
						a.append(",");
					}
					post.toJsonString(a);
				}
				a.append("]");
				
				lastM = orgManager.getModifiedTimeStamp(HeadModifyState, loginAccountId);
				this.orgString.put(key, a.toString());
				this.orgDate.put(key, lastM);
			}
			
			if (!lastM.equals(time)) { // 前段数据时间戳与后段一直，不用加载
				return this.orgString.get(key);
			}
		}
		catch (Exception e) {
			log.error("", e);
		}

		return null;
	}

	/**
	 * 加载所有职务级别
	 * 
	 * @param myLoginDepartId
	 * @return
	 */
	private String getAllOrgEnt_Level(Date time, long loginAccountId) {
		try {
			String key = ORGENT_TYPE_LEVEL + "_" +loginAccountId;
			Date lastM = orgDate.get(key);
			if(lastM == null){
				lastM = new Date(0L);
			}
			
			if (orgManager.isModified(HeadModifyState, lastM, loginAccountId)) { // 前段数据时间戳与后段一直，不用加载
				StringBuilder a = new StringBuilder();
				a.append("[");
				
				int i = 0;
				List<V3xOrgLevel> levels = orgManager.getAllLevels(loginAccountId);
				for (V3xOrgLevel level : levels) {
					if(i++ != 0){
						a.append(",");
					}
					level.toJsonString(a);
				}
				a.append("]");
				
				lastM = orgManager.getModifiedTimeStamp(HeadModifyState, loginAccountId);
				this.orgString.put(key, a.toString());
				this.orgDate.put(key, lastM);
			}
			
			if (!lastM.equals(time)) { // 前段数据时间戳与后段一直，不用加载
				return this.orgString.get(key);
			}
		}
		catch (Exception e) {
			log.error("", e);
		}

		return null;
	}

	/**
	 * 加载所有组
	 * 
	 * @param myLoginDepartId
	 * @return
	 */
	private String getAllOrgEnt_Team(Date time, Date timestampDateRoot, long loginAccountId, long memberId) {
		try {
			if (orgManager.isModified(HeadModifyState, timestampDateRoot, orgManager.getRootAccount().getId()) 
					|| orgManager.isModified(HeadModifyState, time, loginAccountId)) { // 前段数据时间戳与后段一直，不用加载
				StringBuilder a = new StringBuilder();
				a.append("[");
				
				List<V3xOrgTeam> teams = orgManager.getTeamsByMember(memberId, loginAccountId);
				teams = new LinkedList<V3xOrgTeam>(teams);
				V3xOrgMember mem = orgManager.getMemberById(memberId);
				Long deptId = null;
				if (mem != null) {
					deptId = mem.getOrgDepartmentId();
				}
				boolean isAccountAdmin = CurrentUser.get().isAdministrator();
				boolean isSystemAdmin = CurrentUser.get().isSystemAdmin();
				boolean isGroupAdmin = CurrentUser.get().isGroupAdmin();
				// Cache
				Map<Long,List<Long>> childAccountCache  = new HashMap<Long,List<Long>>();
				Map<Long,List<Long>> concurrentAccountCache  = new HashMap<Long,List<Long>>();
				Map<Long,List<V3xOrgDepartment>> childDepartmentCache = new HashMap<Long,List<V3xOrgDepartment>>();
				for (Iterator<V3xOrgTeam> iter = teams.iterator(); iter.hasNext();) {
					V3xOrgTeam team = iter.next();
		            //修改 去掉公开系统组不能被不在部门范围内的人看到。by wusb at 2010-12-21
		            Long teamDepartmentId = team.getDepId();
					if(teamDepartmentId!=null && teamDepartmentId!=-1){
						if (team.getIsPrivate() != null && !team.getIsPrivate()) { // 公开组，按是否同一集团/单位/部门检查
							Long teamAccountId = team.getOrgAccountId();
							if (teamDepartmentId.longValue() == teamAccountId.longValue()) {
								// 组属于集团/单位
								List<Long> accountIds = new ArrayList<Long>();
								accountIds.add(teamAccountId);
								List<Long> childAccountIds = childAccountCache.get(teamAccountId);
								if(childAccountIds==null){
									 childAccountIds = CommonTools.getEntityIds(orgManager.getChildAccount(teamAccountId, false));
									 childAccountCache.put(teamAccountId,childAccountIds);
								}
								accountIds.addAll(childAccountIds);

								List<Long> concurrentAccountIds = concurrentAccountCache.get(memberId);
								if(concurrentAccountIds==null){
									concurrentAccountIds = CommonTools.getEntityIds(orgManager.concurrentAccount(memberId));
									concurrentAccountCache.put(memberId, concurrentAccountIds);
								}

								List<Long> intersection = CommonTools.getIntersection(accountIds, concurrentAccountIds);
								if (mem != null && !team.contains(mem) && CollectionUtils.isEmpty(intersection) 
										&& !isSystemAdmin) {//AEIGHT-6751 系统管理员可见集团/单位公开组 2012-07-17 lilong
									//TODO 整理组可见性问题，公开组不需要过滤,不需要判断是否属于组成员team.contains(mem)
									iter.remove();
								}
							} else {
								// 组属于部门
								List<V3xOrgDepartment> depts = childDepartmentCache.get(teamDepartmentId);
								if(depts==null){
									depts = orgManager.getChildDepartments(teamDepartmentId, false);
									childDepartmentCache.put(teamDepartmentId, depts);
								}
								List<Long> deptIds = new ArrayList<Long>();
								deptIds.add(teamDepartmentId);
								for (V3xOrgDepartment dept : depts) {
									deptIds.add(dept.getId());
								}
								// 部门公开组，组成员、所属部门人员、单位管理员可见
								if (deptId != null && !team.contains(mem) && !deptIds.contains(deptId)  && !isAccountAdmin) {
									iter.remove();
								}
							}
						} else { // 私有组
							switch (team.getType()) {
							case V3xOrgEntity.TEAM_TYPE_SYSTEM: // 系统组，按是否组人员检查
								// 单位管理员也有权限
								if (!isMyTeam(memberId, team) && !isAccountAdmin 
										&& !isGroupAdmin) {//Fix AEIGHT-7597 集团私有系统组，集团管理员也有权限查看
									iter.remove();
								}
								break;
							case V3xOrgEntity.TEAM_TYPE_PROJECT: // 项目组，按是否组人员检查
								if (!isMyTeam(memberId, team)) {
									iter.remove();
								}
								break;
							case V3xOrgEntity.TEAM_TYPE_PERSONAL: // 个人组，按是否组的主人检查
								if (team.getOwnerId().longValue() != memberId) {
									iter.remove();
								}
								break;
							case V3xOrgEntity.TEAM_TYPE_DISCUSS: // 讨论组，不在选人界面出现
							default:
								iter.remove();
								break;
							}
						}
		            }
				}
				
				Collections.sort(teams, new Comparator<V3xOrgTeam>() {
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
				
				int i = 0;
				for (V3xOrgTeam team : teams) {
					if(team.isValid()){
						if(i++ != 0){
							a.append(",");
						}
						
						team.toJsonString(a, this.orgManager);
					}
				}
				a.append("]");
				
				return a.toString();
			}
		}
		catch (Exception e) {
			log.error("", e);
		}

		return null;
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
	 * 加载特殊角色
	 * 
	 * @param time
	 * @param myLoginDepartId
	 * @return
	 */
	private String getAllRole(Date time, long loginAccountId) {
		try {
			String key = ORGENT_TYPE_ROLE + "_" +loginAccountId;
			Date lastM = orgDate.get(key);
			if(lastM == null){
				lastM = new Date(0L);
			}
			
			if (orgManager.isModified(HeadModifyState, lastM, loginAccountId)) { // 前段数据时间戳与后段一直，不用加载
				List<V3xOrgRole> list = this.orgManager.getRelativeRoles();
				list.addAll(this.orgManager.getAllRoles(loginAccountId));
				StringBuilder a = new StringBuilder();
				a.append("[");
				
				int i = 0;
				for (V3xOrgRole role : list) {
					if(i++ != 0){
						a.append(",");
					}
					role.toJsonString(a);
				}
				a.append("]");
				
				lastM = orgManager.getModifiedTimeStamp(HeadModifyState, loginAccountId);
				this.orgString.put(key, a.toString());
				this.orgDate.put(key, lastM);
			}
			if (!lastM.equals(time)) { // 前段数据时间戳与后段一直，不用加载
				return this.orgString.get(key);
			}
		}
		catch (BusinessException e) {
			log.error("", e);
		}
		
		return null;
	}
	
	/**
	 * 加载所有管理员，包括系统管理员、集团/组织管理员、审计管理员、所有单位管理员 
	 * @param time
	 * @return
	 */
	private String getAllAdmin(Date time, long loginAccountId){
		try{
			String key = "Admin_" +loginAccountId;
			Date lastM = orgDate.get(key);
			if(lastM == null){
				lastM = new Date(0L);
			}
			if (orgManager.isModified(HeadModifyState, lastM, loginAccountId)) { // 前段数据时间戳与后段一致，不用加载
				StringBuilder a = new StringBuilder();
				String name = "";
				a.append("[");
				a.append("{");
				a.append(TOXML_PROPERTY_id).append(":\"").append(1L).append("\"");
				name = ResourceBundleUtil.getString(OrganizationResources, "org.account_form.systemAdminName.value");
				a.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(name).append("\"");
				a.append("},{");
				a.append(TOXML_PROPERTY_id).append(":\"").append(0L).append("\"");
				name = ResourceBundleUtil.getString(OrganizationResources, "org.auditAdminName.value");
				a.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(name).append("\"");
				a.append("}");
				
				String groupLabel = ResourceBundleUtil.getString(OrganizationResources, "org.account_form.groupAdminName.value" + (String)SysFlag.EditionSuffix.getFlag());
				String accountLabel = ResourceBundleUtil.getString(OrganizationResources, "org.account_form.adminName.value");
				
				List<V3xOrgAccount> accounts = orgManager.getAllAccounts();
				for(V3xOrgAccount account : accounts){
					V3xOrgMember member = orgManager.getMemberByLoginName(account.getAdminName());
					// 企业版按登录名取集团管理员member返回null,忽略
					if(member==null) continue;
					a.append(",{");
					a.append(TOXML_PROPERTY_id).append(":\"").append(member.getId()).append("\"");
					if(account.getIsRoot()){
						a.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(groupLabel).append("\"");
					} else {
						a.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(account.getName())).append(accountLabel).append("\"");
					}
					a.append("}");
				}
				a.append("]");
				lastM = orgManager.getModifiedTimeStamp(HeadModifyState, loginAccountId);
				this.orgString.put(key, a.toString());
				this.orgDate.put(key, lastM);
			}
			if (!lastM.equals(time)) { // 前段数据时间戳与后段一致，不用加载
				return this.orgString.get(key);
			}
		} catch (BusinessException e){
			log.error("", e);
		}
		return "";
	}
	
	private String getFormFields(){
		StringBuilder a = new StringBuilder();
		try {
			SessionObject sessionobject = (SessionObject)WebUtil.getRequest().getSession(false).getAttribute("SessionObject");
			if(sessionobject==null) return null;
			IOperBase iOperBase = (IOperBase)SeeyonForm_Runtime.getInstance().getBean("iOperBase");
			
			List<TableFieldDisplay> tableFieldDisplays = iOperBase.getSelectPeopleFieldIncludingRef(sessionobject);
			if(tableFieldDisplays != null && !tableFieldDisplays.isEmpty()){
				a.append("[");
				int i = 0;
				for (TableFieldDisplay t : tableFieldDisplays) {
					if(i++ != 0){
						a.append(",");
					}
					a.append("{");
					a.append("N:\"").append(Strings.escapeJavascript(t.getName())).append("\"");
					a.append(",BN:\"").append(Strings.escapeJavascript(t.getBindname())).append("\"");
					a.append("}");
				}
				a.append("]");
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		
		return a.toString();
	}
	
	/**
	 * 兼职人员
	 * 
	 * @param time
	 * @param loginAccountId
	 * @return
	 */
	public String getConcurent(Date time, long loginAccountId){
		try {
			String key = "Concurent_" +loginAccountId;
			Date lastM = orgDate.get(key);
			if(lastM == null){
				lastM = new Date(0L);
			}
			
			if (orgManager.isModified(HeadModifyState, lastM, loginAccountId)) { // 前段数据时间戳与后段一直，不用加载
				
				boolean needMobile = mobileMessageManager.isValidateMobileMessage();
				
				StringBuilder o = new StringBuilder();
				o.append("{");
				
				int i = 0;
				
				Map<Long, List<ConcurrentPost>> concurents = this.orgManager.getConcurentPosts(loginAccountId);
				if(concurents != null && !concurents.isEmpty()){
					Set<Map.Entry<Long, List<ConcurrentPost>>> set = concurents.entrySet();
					for (Map.Entry<Long, List<ConcurrentPost>> entry : set) {
						if(i++ != 0){
							o.append(",");
						}
						
						o.append("\"" + entry.getKey() + "\":");
						
						o.append("[");
						
						int j = 0;
						List<ConcurrentPost> concurrentPosts = entry.getValue();
						for (ConcurrentPost c : concurrentPosts) {
							if(j++ != 0){
								o.append(",");
							}
							o.append("{");
							
							long id = c.getMemberId();
							
							V3xOrgMember member = this.orgManager.getMemberById(id);
							if(member == null){
								continue;
							}
							
							o.append(TOXML_PROPERTY_id).append(":\"").append(id).append("\"");
							o.append(",").append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(member.getName())).append("\"");
							if(c.getCntPostId() != null){
								o.append(",P:\"").append(String.valueOf(c.getCntPostId())).append("\""); //兼职岗位id
							}
							o.append(",S:").append(c.getNumber()).append(""); //在兼职单位的排序号
							
							o.append(",A:\"").append(member.getOrgAccountId()).append("\""); //原单位id
							if(c.getCntLevelId() != null){
								o.append(",L:\"").append(String.valueOf(c.getCntLevelId())).append("\""); //在兼职单位的职务级别
							}
							
							V3xOrgDepartment d = this.orgManager.getDepartmentById(member.getOrgDepartmentId());;
							
							if(d != null){
								o.append(",DN:\"").append(Strings.escapeJavascript(d.getName())).append("\""); //原部门名称
							}
							
							if(Strings.isNotBlank(member.getEmailAddress())){
								o.append(",").append(TOXML_PROPERTY_Email).append(":\"").append(Strings.escapeJavascript(member.getEmailAddress())).append("\"");
							}
							if(needMobile && Strings.isNotBlank(member.getTelNumber())){
								o.append(",").append(TOXML_PROPERTY_Mobile).append(":\"").append(Strings.escapeJavascript(member.getTelNumber())).append("\"");
							}
							
							o.append("}");
						}
						
						o.append("]");
					}
				}
				
				o.append("}");
				
				lastM = orgManager.getModifiedTimeStamp(HeadModifyState, loginAccountId);
				this.orgString.put(key, o.toString());
				this.orgDate.put(key, lastM);
			}
			
			if (!lastM.equals(time)) { // 前段数据时间戳与后段一直，不用加载
				return this.orgString.get(key);
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return null;
	}
	
	/**
	 * 外部单位，用于公文交换
	 * 
	 * @return
	 */
	private String getExchangeAccount(Date time, long loginAccountId){
		try {
			String key = ENTITY_TYPE_EXCHANGEACCOUNT + "_" +loginAccountId;
			Date lastM = orgDate.get(key);
			if(lastM == null){
				lastM = new Date(0L);
			}
			
			if(this.exchangeAccountManager.isModifyExchangeAccounts(lastM)){
				StringBuilder a = new StringBuilder();
				a.append("[");
				int i = 0;
				List<ExchangeAccount> eas =null;
				boolean isGovVersion = (Boolean)SysFlag.is_gov_only.getFlag();
				if(isGovVersion){
					eas = this.exchangeAccountManager.getExternalAccounts(loginAccountId,-1);
				}else{
					eas = this.exchangeAccountManager.getExternalAccounts(loginAccountId);
				}
				for (ExchangeAccount t : eas) {
					if(i++ != 0){
						a.append(",");
					}
					t.toJsonString(a);
				}
				a.append("]");
				
				lastM = exchangeAccountManager.getLastModifyTimestamp();
				this.orgString.put(key, a.toString());
				this.orgDate.put(key, lastM);
			}
			
			if (!lastM.equals(time)) { // 前段数据时间戳与后段一直，不用加载
				return this.orgString.get(key);
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return null;
	}
	
	private String getOrgTeam(Date time, long loginAccountId){
		try {
			String key = ENTITY_TYPE_OrgTeam + "_" +loginAccountId;
			Date lastM = orgDate.get(key);
			if(lastM == null){
				lastM = new Date(0L);
			}
			
			if(this.edocObjTeamManager.isModifyExchangeAccounts(lastM)){
				StringBuilder a = new StringBuilder();
				a.append("[");
				int i = 0;
				List<EdocObjTeam> eas = this.edocObjTeamManager.findAllNotPager(loginAccountId);
				for (EdocObjTeam t : eas) {
					if(i++ != 0){
						a.append(",");
					}
					t.toJsonString(a);
				}
				a.append("]");
				
				lastM = edocObjTeamManager.getLastModifyTimestamp();
				this.orgString.put(key, a.toString());
				this.orgDate.put(key, lastM);
			}
			
			if (!lastM.equals(time)) { // 前段数据时间戳与后段一直，不用加载
				return this.orgString.get(key);
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param time
	 * @param loginAccountId
	 * @param memberId
	 * @return {R:[{id:"13242345234",T:1},{id:"13242345234",T:1}],E:[{id:"13242345234",N:"张三"},{}]}
	 */
	private String getPeoplerelate(Date time, long loginAccountId, long memberId){
		try {
			Map<RelationType, List<V3xOrgMember>> peopleRelatesList = peoplerelateManager.getAllRelateMembers(memberId);
			if(peopleRelatesList != null && !peopleRelatesList.isEmpty()){
				StringBuilder a = new StringBuilder();
				a.append("[");
				
				boolean needSplit = false;
				
				List<V3xOrgMember> elements = peopleRelatesList.get(RelationType.leader);
				if(elements != null && !elements.isEmpty()){
					relateMember2JsonString(a, RelationType.leader.key(), loginAccountId, elements);
					needSplit = true;
				}
				
				elements = peopleRelatesList.get(RelationType.assistant);
				if(elements != null && !elements.isEmpty()){
					if(needSplit == true){
						a.append(",");
					}
					relateMember2JsonString(a, RelationType.assistant.key(), loginAccountId, elements);
					needSplit = true;
				}
				
				elements = peopleRelatesList.get(RelationType.junior);
				if(elements != null && !elements.isEmpty()){
					if(needSplit == true){
						a.append(",");
					}
					relateMember2JsonString(a, RelationType.junior.key(), loginAccountId, elements);
					needSplit = true;
				}
				
				elements = peopleRelatesList.get(RelationType.confrere);
				if(elements != null && !elements.isEmpty()){
					if(needSplit == true){
						a.append(",");
					}
					relateMember2JsonString(a, RelationType.confrere.key(), loginAccountId, elements);
				}
				
				a.append("]");
				
				return a.toString();
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return null;
	}
	
	private void relateMember2JsonString(StringBuilder o, int relateType, long loginAccountId, List<V3xOrgMember> relateMembers) {
		int i = 0;
		for (V3xOrgMember relateMember : relateMembers) {
			if(i++ != 0){
				o.append(",");
			}
			
			o.append("{");
			o.append(TOXML_PROPERTY_id).append(":\"").append(relateMember.getId()).append("\"");
			o.append(",T:").append(relateType);
			
			try{
				if(relateMember != null && loginAccountId != relateMember.getOrgAccountId().longValue()){ //不是一个单位的
	
					o.append(",E:{");
					
					o.append(TOXML_PROPERTY_NAME).append(":\"").append(Strings.escapeJavascript(relateMember.getName())).append("\"");
	
					o.append(",A:\"").append(relateMember.getOrgAccountId()).append("\"");
					
					if(Strings.isNotBlank(relateMember.getEmailAddress())){
						o.append(",").append(TOXML_PROPERTY_Email).append(":\"").append(Strings.escapeJavascript(relateMember.getEmailAddress())).append("\"");
					}
					if(Strings.isNotBlank(relateMember.getTelNumber())){
						o.append(",").append(TOXML_PROPERTY_Mobile).append(":\"").append(Strings.escapeJavascript(relateMember.getTelNumber())).append("\"");
					}
					
					o.append("}");
				}
			}
			catch (Exception e) {
			}
			
			o.append("}");
		}
	}
	
	/**
	 * 系统管理员，可以访问所有单位的外部人员
	 * @param time
	 * @param memberId
	 * @param accountId
	 * @return
	 */
	private String getAllAccountsExtMember(Date time, long accountId){
		try {
			if(orgManager.isModified(HeadModifyState, time, accountId)){
				Map<Long, List<Long>> ms = new HashMap<Long, List<Long>>();
				List<V3xOrgMember> ws = orgManager.getAllAccountsExtMember(false);
				StringBuilder a = new StringBuilder();
				a.append("{");
				if(ws != null && !ws.isEmpty()){
					for (V3xOrgMember l : ws) {
						Strings.addToMap(ms, l.getOrgDepartmentId(), l.getId());
					}
					
					int i = 0;
					for (Map.Entry<Long, List<Long>> l : ms.entrySet()) {
						if(i++ != 0){
							a.append(",");
						}

						a.append("\"").append(l.getKey()).append("\":[");
						int j = 0;
						for (Long eMemberId : l.getValue()) {
							if(j++ != 0){
								a.append(",");
							}
							a.append("\"").append(eMemberId).append("\"");
						}
						a.append("]");
					}
					
				}
				
				a.append("}");
				return a.toString();
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return null;
	}
	
	/**
	 * 内部人员能访问哪些外部人员
	 * @param time
	 * @param memberId
	 * @param accountId
	 * @return
	 */
	private String getExtMemberScopeOfInternal(Date time, long memberId, long accountId){
		try {
			if(orgManager.isModified(HeadModifyState, time, accountId)){
				Map<Long, List<Long>> ms = new HashMap<Long, List<Long>>();
				List<V3xOrgMember> ws = orgManager.getMemberWorkScopeForExternal(memberId, false);
				StringBuilder a = new StringBuilder();
				a.append("{");
				if(ws != null && !ws.isEmpty()){
					for (V3xOrgMember l : ws) {
						Strings.addToMap(ms, l.getOrgDepartmentId(), l.getId());
					}
					
					int i = 0;
					for (Map.Entry<Long, List<Long>> l : ms.entrySet()) {
						if(i++ != 0){
							a.append(",");
						}

						a.append("\"").append(l.getKey()).append("\":[");
						int j = 0;
						for (Long eMemberId : l.getValue()) {
							if(j++ != 0){
								a.append(",");
							}
							a.append("\"").append(eMemberId).append("\"");
						}
						a.append("]");
					}
					
				}
				
				a.append("}");
				return a.toString();
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return null;
	}
	
	/**
	 * 外部人员，能访问哪些内部人员
	 * 
	 * @param time
	 * @param memberId
	 * @param accountId
	 * @return
	 */
	private String getExternalMemberWorkScope(Date time, long memberId, long accountId){
		try {
			if(orgManager.isModified(HeadModifyState, time, accountId)){
				List<V3xOrgEntity> ws = orgManager.getExternalMemberWorkScope(memberId, false);
				if(ws != null && !ws.isEmpty()){
					StringBuilder a = new StringBuilder();
					a.append("[");
					
					int i = 0;
					for (V3xOrgEntity l : ws) {
						if(l instanceof V3xOrgAccount){//单位
							return "[\"A\"]";
						}
						else if(l instanceof V3xOrgDepartment){
							if(i++ != 0){
								a.append(",");
							}

							a.append("\"D").append(((V3xOrgDepartment)l).makeLiushuihao()).append("\"");
						}
						else if(l instanceof V3xOrgMember){
							if(i++ != 0){
								a.append(",");
							}

							a.append("\"M").append(((V3xOrgMember)l).getId()).append("\"");
						}
					}
					
					a.append("]");
					
					return a.toString();
				}
			}
		}
		catch (Exception e) {
			log.error("", e);
		}
		
		return null;
	}

	/**
	 * 根据登录名取得他的可登录的公司
	 * 
	 * @param loginName
	 * @return
	 */
	public List<V3xOrgAccount> getLoginDeparment(String loginName) {
		List<V3xOrgAccount> loginDeparts = new ArrayList<V3xOrgAccount>();
		try {
			loginDeparts = orgManager.getAllAccounts();
		}
		catch (Exception e) {
			log.error("", e);
		}

		return loginDeparts;
	}
	
    @SuppressWarnings("unchecked")
	public Map chanageAccount(long newLoginAccountId) throws SpaceException{
		CurrentUserToSeeyonApp.changeLoginAccount(newLoginAccountId);
		Map<String, Object> result = realignSpaceMenu(newLoginAccountId);		
        //导入单位标示设置数据
		try{
            AccountSymbol accountSymbol = MainDataLoader.getInstance().getAccountSymbol(newLoginAccountId);
	        result.put("logoFileName", accountSymbol.getLogoImagePath());
	        result.put("isHiddenLogo", accountSymbol.isHiddenLogo());
	        result.put("bannerFileName", accountSymbol.getBannerImagePath());
	        result.put("isTileBanner", accountSymbol.isTileBanner());
	        result.put("isHiddenAccountName", accountSymbol.isHiddenAccountName());
	        boolean isShowGroupShortName = (Boolean)(SysFlag.frontPage_showGroupShortName.getFlag());
	        result.put("isHiddenGroupName", !(isShowGroupShortName && !accountSymbol.isHiddenGroupName()));
		}
		catch(Exception e){
			log.error("", e);
		}
		
		return result;
	}
    
    /**
     * 重新排列首页空间菜单
     */
	@SuppressWarnings("unchecked")
	public Map realignSpaceMenu(long newLoginAccountId) throws SpaceException{
        Map<String, Object> result = new HashMap<String, Object>();
        User user = CurrentUser.get();
        Long userId = user.getId();
        
        Map<Constants.SpaceType, List<SpaceModel>> spacePath = this.spaceManager.getAccessSpace(userId, newLoginAccountId);
        List<String[]> spaceSort = this.spaceManager.getSpaceSort(userId, newLoginAccountId, user.getLocale(), false, spacePath);
        
        //空间入口
        String spaceMenu = MainFunction.showSpaceMenu(spacePath, spaceSort,StringUtils.isBlank(user.getUserSSOFrom())?null:"nc");
        //我能管理的部门、单位、集团
        List<Long> managerDepartments = this.spaceManager.getCanManagerSpace(userId);
        if(managerDepartments != null){
            result.put("managerDepartments", managerDepartments);
        }
        result.put("spaceMenu", spaceMenu);
        result.put("departmentSpaces", spacePath.get(Constants.SpaceType.department));
        
        return result;
    }
    
    /**
     * 判断当前用户所登录的单位 是否是集团下的单位<br>
     * 非集团下的单位不能访问集团空间、集团文档等
     * @param accountId
     * @return
     * @throws BusinessException
     */
    public boolean isAccountInGroup(long accountId){
        try {
            return orgManager.isAccountInGroupTree(accountId);
        }
        catch (BusinessException e) {
            log.error("", e);
            return false;
        }
    }
    
    
    /**
     * 根据部门ID取得部门path<br>
     * 在线人员处调用
     * @param departmentId
     * @return
     */
    public String getDepartmentPath(long departmentId){
        String deptPath = "";
        try {
            V3xOrgDepartment dept = orgManager.getDepartmentById(departmentId);
            if(dept != null){
                deptPath = dept.getPath();
            }
        }
        catch (BusinessException e) {
            log.error("", e);
        }
        return deptPath;
        
    }

	public void setEdocObjTeamManager(EdocObjTeamManager edocObjTeamManager) {
		this.edocObjTeamManager = edocObjTeamManager;
	}

	public void setMobileMessageManager(MobileMessageManager mobileMessageManager) {
		this.mobileMessageManager = mobileMessageManager;
	}

}