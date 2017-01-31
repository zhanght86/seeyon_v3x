package com.seeyon.v3x.inquiry.manager;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.affair.constants.StateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.affair.manager.AffairManager;
import com.seeyon.v3x.agent.manager.AgentUtil;
import com.seeyon.v3x.cluster.notification.NotificationManager;
import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.constants.ApplicationSubCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.quartz.QuartzListener;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.doc.util.Constants;
import com.seeyon.v3x.index.share.datamodel.AuthorizationInfo;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexUtil;
import com.seeyon.v3x.inquiry.dao.InquiryAuthDao;
import com.seeyon.v3x.inquiry.dao.InquiryBasicDao;
import com.seeyon.v3x.inquiry.dao.InquiryDao;
import com.seeyon.v3x.inquiry.dao.InquiryItemDao;
import com.seeyon.v3x.inquiry.dao.InquiryScopeDAO;
import com.seeyon.v3x.inquiry.dao.InquiryTypeDao;
import com.seeyon.v3x.inquiry.domain.InquiryAuthority;
import com.seeyon.v3x.inquiry.domain.InquiryScope;
import com.seeyon.v3x.inquiry.domain.InquirySubsurvey;
import com.seeyon.v3x.inquiry.domain.InquirySubsurveyitem;
import com.seeyon.v3x.inquiry.domain.InquirySurveybasic;
import com.seeyon.v3x.inquiry.domain.InquirySurveydiscuss;
import com.seeyon.v3x.inquiry.domain.InquirySurveytype;
import com.seeyon.v3x.inquiry.domain.InquirySurveytypeextend;
import com.seeyon.v3x.inquiry.util.FinishInquiry;
import com.seeyon.v3x.inquiry.util.InquiryLock;
import com.seeyon.v3x.inquiry.util.InquiryRemind;
import com.seeyon.v3x.inquiry.webmdoel.AuthUserCompose;
import com.seeyon.v3x.inquiry.webmdoel.DiscussAndUserCompose;
import com.seeyon.v3x.inquiry.webmdoel.SubsurveyAndItemsCompose;
import com.seeyon.v3x.inquiry.webmdoel.SurveyAuthCompose;
import com.seeyon.v3x.inquiry.webmdoel.SurveyBasicCompose;
import com.seeyon.v3x.inquiry.webmdoel.SurveyTypeCompose;
import com.seeyon.v3x.organization.directmanager.OrgManagerDirect;
import com.seeyon.v3x.organization.domain.V3xOrgAccount;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.space.manager.SpaceManager;
import com.seeyon.v3x.util.CommonTools;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.SQLWildcardUtil;
import com.seeyon.v3x.util.Strings;

/**
 * @author lin tian
 * 2007-2-27
 */
public class InquiryManagerImpl implements InquiryManager,IndexEnable {
	private static Log log = LogFactory.getLog(InquiryManagerImpl.class);
	private InquiryDao inquiryDao;
	private OrgManager orgManager;
	private InquiryScopeDAO inquiryScopeDAO;
	private InquiryTypeDao inquiryTypeDao;
	private InquiryBasicDao inquiryBasicDao;
	private InquiryAuthDao inquiryAuthDao;
	private InquiryItemDao inquiryitemDao;
	private UserMessageManager userMessageManager;
	private AffairManager affairManager;
	private IndexManager indexManager;
	private AppLogManager appLogManager;
	private AttachmentManager attachmentManager;
	private OrgManagerDirect orgManagerDirect;
	private SpaceManager spaceManager;
//	所有驻入内存调查类型
	private static Map<Long, InquirySurveytype> allSurveyTypeMap = new HashMap<Long, InquirySurveytype>();
//	所有驻入内存调查类型
	private List<InquirySurveytype> allSurveyTypeList = new ArrayList<InquirySurveytype>();
//	某人有权限发布的调查类型   key人id  value调查类型id集合
	private Map<Long,Set<Long>> canIssueSurveyTypeMap = new HashMap<Long,Set<Long>>();
//	某人有权限管理的调查类型   key人id  value调查类型id集合
	private Map<Long,Set<Long>> canManageSurveyTypeMap = new HashMap<Long,Set<Long>>();
//	某人有权限审核的调查类型   key人id  value调查类型id集合
	private Map<Long,Set<Long>> canAuditSurveyTypeMap = new HashMap<Long,Set<Long>>();
//	对调查基本信息加上一个文件锁
	private Map<Long, InquiryLock> inquiryLockMap;
	
	public Collection<InquirySurveytype> getAllInquiryTypes() {
		return allSurveyTypeMap.values();
	}
	
	public void setSpaceManager(SpaceManager spaceManager) {
		this.spaceManager = spaceManager;
	}
	
	public void setOrgManagerDirect(OrgManagerDirect orgManagerDirect) {
		this.orgManagerDirect = orgManagerDirect;
	}
	
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}

	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	
	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
	
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}

	public void setInquiryAuthDao(InquiryAuthDao inquiryAuthDao) {
		this.inquiryAuthDao = inquiryAuthDao;
	}

	public void setInquiryBasicDao(InquiryBasicDao inquiryBasicDao) {
		this.inquiryBasicDao = inquiryBasicDao;
	}

	public void setInquiryTypeDao(InquiryTypeDao inquiryTypeDao) {
		this.inquiryTypeDao = inquiryTypeDao;
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setInquiryDao(InquiryDao inquiryDao) {
		this.inquiryDao = inquiryDao;
	}
	
	public void setInquiryitemDao(InquiryItemDao inquiryitemDao)
	{
		this.inquiryitemDao = inquiryitemDao;
	}
	/**
	 * 获取已创建调查名称列表
	 * 
	 * @return
	 * @deprecated 未区分单位
	 * @throws Exception
	 */
	public List<String> getTypeNameList(boolean isGroup) throws Exception {
		return inquiryTypeDao.getTypeNameList(isGroup);
	}
	
	public List<String> getTypeNameList(boolean isGroup, Long loginAccountId) throws Exception {
		return inquiryTypeDao.getTypeNameList(isGroup, loginAccountId);
	}

	public List<String> getTypeNameList(Long loginAccountId, int spaceType) throws Exception {
		return inquiryTypeDao.getTypeNameList(loginAccountId, spaceType);
	}
	
	/**
	 * 管理员选择删除当前调查类型下发布的调查列表
	 * 
	 * @param bid
	 * @throws Exception
	 */
	public void removeSendBasicByManager(String[] bid) throws Exception {
		for (String string : bid) {
			this.deleteInquiryBasic(Long.parseLong(string));
		}
	}

	/**
	 * 删除调查
	 * 
	 * @param inquirytype
	 * @return
	 */
	public void deleteInquiryBasic(long id) throws Exception {
		inquiryBasicDao.updateInquiryBasic(id);
		//删除调查时将审核员的待办删掉(目前公告和新闻在删除时没有删除代办，为了保持一致先注释掉)
		//affairManager.deleteByObject(ApplicationCategoryEnum.inquiry, id);
	}

	/**
	 * 取消发布调查
	 * 
	 * @param inquirytype
	 * @return
	 */
	public void cancelInquiryBasic(long id) throws Exception {
		inquiryBasicDao.cancelInquiryBasic(id);
		InquirySurveybasic basic = inquiryBasicDao.getInquirySurveybasicID(id, false);
		this.sendMessage(basic, "inquiry.cancel", null);
	}
	
	/**
	 * 保存新调查类型
	 * 
	 * @param inquirytype
	 * @throws Exception
	 */
	public void saveInquiryType(InquirySurveytype inquirytype) throws Exception {
		if (inquirytype == null) {
			return;
		}
		inquiryDao.save(inquirytype);
//		加入内存
//		allSurveyTypeMap.put(inquirytype.getId(), inquirytype);
//		allSurveyTypeList.add(inquirytype);
		initAllSurveyType();
	}

	public void updateInquiryType(InquirySurveytype type, Set<InquirySurveytypeextend> set) throws Exception {
		updateInquiryType(type, set, true);
	}

	public void updateInquiryType(InquirySurveytype type, Set<InquirySurveytypeextend> set, boolean isReloadAllType) throws Exception {
		this.updateInquiryType(type, set, null, isReloadAllType);
	}

	public void updateInquiryType(InquirySurveytype type, Set<InquirySurveytypeextend> set, Set<InquiryAuthority> removeAuthSet, boolean isReloadAllType) throws Exception {
		if (type == null || set == null) {
			return;
		}

		Set<InquirySurveytypeextend> eset = type.getInquirySurveytypeextends();
		if (CollectionUtils.isNotEmpty(eset)) {
			eset.removeAll(eset);
		}
		
		for (InquirySurveytypeextend o : set) {
			if (type.getInquirySurveytypeextends() == null) {
				type.setInquirySurveytypeextends(new HashSet<InquirySurveytypeextend>());
			}
			type.getInquirySurveytypeextends().add(o);
		}

		if (removeAuthSet != null) {
			for (InquiryAuthority auth : removeAuthSet) {
				type.getInquiryAuthorities().remove(auth);
				inquiryAuthDao.remove(auth);
			}
		}

		inquiryDao.update(type);
		
		// 重新加载内存
		if (isReloadAllType)
			initAllSurveyType();
	}

	public void updateInquiryType(InquirySurveytype type) throws Exception {
		updateInquiryType(type, true);
	}

	public void updateInquiryType(InquirySurveytype type, boolean isReloadAllType) throws Exception {
		if (type == null) {
			return;
		}
		
		inquiryDao.update(type);
		
		// 重新加载内存
		if (isReloadAllType)
			initAllSurveyType();
	}

	/**
	 * 获取调查列表
	 * 
	 * @return
	 */
	public List<SurveyTypeCompose> getInquiryList(User user) throws Exception {
		
		List<InquirySurveytype> surveytypes = null;
			
		V3xOrgAccount account = new V3xOrgAccount();
		
		try {
			account = orgManager.getAccountById(user.getLoginAccount());
		} catch (BusinessException e) {
			log.error("获取单位失败", e);
		}
		
		//判断是否集团
		if(account.getIsRoot()){
			surveytypes = getGroupSurveyTypeList();
//			inquiryTypeDao.getInquiryTypeList();
		}else{
			surveytypes = getAccountSurveyTypeList(user.getLoginAccount());
		}
		 
		List<SurveyTypeCompose> comlist =  new ArrayList<SurveyTypeCompose>();
		comlist = this.getSurveyTypeCompose(comlist, surveytypes, true);

		return comlist;
	}
	
	/**
	 * 获取某单位下调查版块
	 * @param accountId
	 * @return
	 * @throws Exception
	 */
	public List<InquirySurveytype> getAccountSurveyTypeList(Long accountId) throws Exception{
		List<InquirySurveytype> types = new ArrayList<InquirySurveytype>();
		for(InquirySurveytype type : allSurveyTypeList){
			if(type.getFlag()!=1 && type.getAccountId().intValue()==accountId.intValue() && type.getSpaceType() == 2){
				types.add(type);
			}
		}
		
//		排序
		Comparator<InquirySurveytype> comp = new InquirySurveytype();
		
		Collections.sort(types,comp);
		
		return types;
		
	}
	
	/**
	 * 获取某自定义单位或集团下调查版块
	 * @param accountId
	 * @return
	 * @throws Exception
	 */
	public List<SurveyTypeCompose> getCustomAccInquiryList(Long spaceId, String spaceType) throws Exception{
		List<InquirySurveytype> types = new ArrayList<InquirySurveytype>();
		int spaceTypeInt = "public_custom".equalsIgnoreCase(spaceType) ? 5 : 6;
		for(InquirySurveytype type : allSurveyTypeList){
			if(type.getFlag()!=1 && type.getAccountId().intValue()==spaceId.intValue() && type.getSpaceType() == spaceTypeInt){
				types.add(type);
			}
		}
//		排序getCustomAccInquiryList
		Comparator<InquirySurveytype> comp = new InquirySurveytype();
		Collections.sort(types,comp);
		List<SurveyTypeCompose> comlist =  new ArrayList<SurveyTypeCompose>();
		comlist = this.getSurveyTypeCompose(comlist, types, true);
		return comlist;
	}
	
	/**
	 * 获取某自定义单位下调查版块
	 * @param accountId
	 * @param spaceType
	 * @return
	 * @throws Exception
	 */
	public List<InquirySurveytype> getCustomAccInquiryTypeList(Long spaceId, int spaceType) throws Exception{
		List<InquirySurveytype> types = new ArrayList<InquirySurveytype>();
		for(InquirySurveytype type : allSurveyTypeList){
			if(type.getFlag()!=1 && type.getAccountId().intValue()==spaceId.intValue() && type.getSpaceType() == spaceType){
				types.add(type);
			}
		}
		Comparator<InquirySurveytype> comp = new InquirySurveytype();
		Collections.sort(types,comp);
		return types;
	}
	
	/**
	 * 获取集团调查版块
	 * @return
	 * @throws Exception
	 */
	public List<InquirySurveytype> getGroupSurveyTypeList() throws Exception{
		List<InquirySurveytype> types = new ArrayList<InquirySurveytype>();
		for(InquirySurveytype type : allSurveyTypeList){
			if(type.getFlag().intValue()!=1 && type.getSpaceType().intValue()==InquirySurveytype.Space_Type_Group.intValue()){
				types.add(type);
			}
		}
		
//		排序
		Comparator<InquirySurveytype> comp = new InquirySurveytype();
		
		Collections.sort(types,comp);
		
		return types;
		
	}

	public List<InquirySurveytype> getSurveytypeList() throws Exception {
		return inquiryTypeDao.getTypeList();
	}

	/**
	 * 获取用户首页调查类型列表
	 * 
	 * @return
	 */
	public List<SurveyTypeCompose> getUserIndexInquiryList(boolean isGroup,boolean needSurveyCountOfType) throws Exception {
//		7-18增加了单位的过滤条件	只显示单位为当前登录人单位的调查类型还有集团调查类型
//		String hql = "From " + InquirySurveytype.class.getName() + " AS ins Where ins.flag=? and ins.accountId=?";// 查询正常状态下的调查类型列表
		User user = CurrentUser.get();
//		外部人员查看权限过滤
		boolean isInternal = user.isInternal();
		String authID = "";
		
		if(isInternal){
			authID = orgManager.getUserIDDomain(user.getId() , V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Account","Department", "Team","Member", "Post", "Level", "Role");
		}else{
			authID = orgManager.getUserIDDomain(user.getId() , V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Department", "Team","Member", "Post", "Level", "Role");
		}
		
		Long accountId = user.getLoginAccount();
		
		List<InquirySurveytype> surveytypes = null;
		//查出当前用户所在单位的所有调查类型
		if(isGroup){
			surveytypes = getGroupSurveyTypeList();
		}else{
			surveytypes = getAccountSurveyTypeList(accountId);
		}
		
		List<SurveyTypeCompose> comlist = new ArrayList<SurveyTypeCompose>();
		if(surveytypes!=null && surveytypes.size()>0) {
			for (InquirySurveytype surveytype : surveytypes) {
				SurveyTypeCompose stCompose = new SurveyTypeCompose();
				//判断有没有调查类型的个数
				if(needSurveyCountOfType){
					int count = getCountByType(surveytype.getId(),authID);
					stCompose.setCount(count);
				}
				stCompose.setInquirySurveytype(surveytype);
				
				ArrayList<V3xOrgMember> manager = new ArrayList<V3xOrgMember>();
				
				Set<InquirySurveytypeextend> surveytypeextends = surveytype.getInquirySurveytypeextends();
				
				//保持用户设定管理员的顺序 added by Meng Yang at 2009-06-30
				List<InquirySurveytypeextend> sortedList = new ArrayList<InquirySurveytypeextend>();
				if(surveytypeextends!=null && surveytypeextends.size()>0) {
					sortedList.addAll(surveytypeextends);
					Collections.sort(sortedList);
				}
				
				if(sortedList!=null && sortedList.size()>0) {
					for (InquirySurveytypeextend surveytypeextend : sortedList) {
						V3xOrgMember member = this.orgManager.getEntityById(V3xOrgMember.class, surveytypeextend.getManagerId());// 获取当前用户对象
	    				if (surveytypeextend.getManagerDesc().intValue() == InquirySurveytypeextend.MANAGER_SYSTEM.intValue()) {// 管理员
	    					manager.add(member);
	    				} else if (surveytypeextend.getManagerDesc().intValue() == InquirySurveytypeextend.MANAGER_CHECK.intValue()) {// 审核人员
	    					stCompose.setChecker(member);
	    				}
					}
				}   
				
				stCompose.setManagers(manager);
				comlist.add(stCompose);
			}
		}
		return comlist;
	}
	/**
	 * 获取用户自定义单位或集团首页调查类型列表
	 */
	public List<SurveyTypeCompose> getUserIndexInquiryList(long spaceId, int spaceType, boolean needSurveyCountOfType) throws Exception {
		User user = CurrentUser.get();
		boolean isInternal = user.isInternal();
		String authID = "";
		if(isInternal){
			authID = orgManager.getUserIDDomain(user.getId() , V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Account","Department", "Team","Member", "Post", "Level", "Role");
		}else{
			authID = orgManager.getUserIDDomain(user.getId() , V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Department", "Team","Member", "Post", "Level", "Role");
		}
		List<InquirySurveytype> surveytypes = null;
		//查出当前用户所在单位的所有调查类型
		surveytypes = getCustomAccInquiryTypeList(spaceId, spaceType);
		List<SurveyTypeCompose> comlist = new ArrayList<SurveyTypeCompose>();
		if(surveytypes!=null && surveytypes.size()>0) {
			for (InquirySurveytype surveytype : surveytypes) {
				SurveyTypeCompose stCompose = new SurveyTypeCompose();
				//判断有没有调查类型的个数
				if(needSurveyCountOfType){
					int count = getCountByType(surveytype.getId(),authID);
					stCompose.setCount(count);
				}
				stCompose.setInquirySurveytype(surveytype);
				ArrayList<V3xOrgMember> manager = new ArrayList<V3xOrgMember>();
				Set<InquirySurveytypeextend> surveytypeextends = surveytype.getInquirySurveytypeextends();
				//保持用户设定管理员的顺序 
				List<InquirySurveytypeextend> sortedList = new ArrayList<InquirySurveytypeextend>();
				if(surveytypeextends!=null && surveytypeextends.size()>0) {
					sortedList.addAll(surveytypeextends);
					Collections.sort(sortedList);
				}
				if(sortedList!=null && sortedList.size()>0) {
					for (InquirySurveytypeextend surveytypeextend : sortedList) {
						V3xOrgMember member = this.orgManager.getEntityById(V3xOrgMember.class, surveytypeextend.getManagerId());// 获取当前用户对象
	    				if (surveytypeextend.getManagerDesc().intValue() == InquirySurveytypeextend.MANAGER_SYSTEM.intValue()) {// 管理员
	    					manager.add(member);
	    				} else if (surveytypeextend.getManagerDesc().intValue() == InquirySurveytypeextend.MANAGER_CHECK.intValue()) {// 审核人员
	    					stCompose.setChecker(member);
	    				}
					}
				}   
				stCompose.setManagers(manager);
				comlist.add(stCompose);
			}
		}
		return comlist;
	}
	
	/**
	 * 获取集团空间调查类型列表
	 * 
	 * @return
	 */
	public List<SurveyTypeCompose> getGroupInquiryTypeList() throws Exception {
//		7-18增加了单位的过滤条件	只显示单位为当前登录人单位的调查类型还有集团调查类型
//		String hql = "From " + InquirySurveytype.class.getName()
//				+ " AS ins Where ins.flag=? and ins.spaceType=?";// 查询正常状态下的调查类型列表
//		外部人员查看权限过滤
		User user = CurrentUser.get();
		boolean isInternal = user.isInternal();
		String authID = "";
		
		if(isInternal){
			authID = orgManager.getUserIDDomain(user.getId() , "Account" , "Department", "Team", "Member", "Post", "Level");
		}else{
			authID = orgManager.getUserIDDomain(user.getId() , "Department", "Team", "Member", "Post", "Level");
		}
		
		List<InquirySurveytype> surveytypes = getGroupSurveyTypeList();
		
		List<SurveyTypeCompose> comlist = new ArrayList<SurveyTypeCompose>();
		for (InquirySurveytype surveytype : surveytypes) {
			SurveyTypeCompose stCompose = new SurveyTypeCompose();
			int count = getCountByType(surveytype.getId(),authID);
			stCompose.setCount(count);
			stCompose.setInquirySurveytype(surveytype);
			ArrayList<V3xOrgMember> manager = new ArrayList<V3xOrgMember>();
			Set<InquirySurveytypeextend> surveytypeextends = surveytype.getInquirySurveytypeextends();
			
			//保持用户设定管理员的顺序 added by Meng Yang at 2009-06-30
			List<InquirySurveytypeextend> sortedList = new ArrayList<InquirySurveytypeextend>();
			if(surveytypeextends!=null && surveytypeextends.size()>0) {
				sortedList.addAll(surveytypeextends);
				Collections.sort(sortedList);
			}
			
			if(sortedList!=null && sortedList.size()>0) {
				for (InquirySurveytypeextend surveytypeextend : sortedList) {
					V3xOrgMember member = this.orgManager.getEntityById(V3xOrgMember.class, surveytypeextend.getManagerId());// 获取当前用户对象
    				if (surveytypeextend.getManagerDesc().intValue() == InquirySurveytypeextend.MANAGER_SYSTEM.intValue()) {// 管理员
    					manager.add(member);
    				} else if (surveytypeextend.getManagerDesc().intValue() == InquirySurveytypeextend.MANAGER_CHECK.intValue()) {// 审核人员
    					stCompose.setChecker(member);
    				}
				}
			}   
			
			stCompose.setManagers(manager);
			comlist.add(stCompose);
		}
		return comlist;
	}

	/**
	 * 发布审核通过的调查
	 * 
	 * @param did
	 * @return
	 * @throws Exception
	 */
	public void creatorSendBasic(String[] bids, String tid , String name , Long id) throws Exception {
		if (bids.length < 1) {
			return;
		} else {
			User member = CurrentUser.get();
			long memberid = member.getId();// 获取当前用户ID
			for (String string : bids) {
				InquirySurveybasic basic = inquiryBasicDao.getNOSendBasicByCreator(Long.parseLong(tid), memberid,Long.parseLong(string));
				basic.setInquirySurveytype(allSurveyTypeMap.get(Long.parseLong(tid)));//取类型存basic里
				if (basic.getCensor().intValue() == InquirySurveybasic.CENSOR_NO_PASS.intValue()) {// 审核未通过
					if (basic.getInquirySurveytype().getCensorDesc().intValue() == InquirySurveytype.CENSOR_NO_PASS.intValue()) {// 需要审核
						inquiryBasicDao.updateCreatorBasicSendB(Long.parseLong(string));
					} else {
//						如果当前时间比发布时间晚  状态变为已发送
						if(basic.getSendDate().getTime()<=System.currentTimeMillis()){
							inquiryBasicDao.updateCreatorBasicSend(Long.parseLong(string),true);
						}else{
							inquiryBasicDao.updateCreatorBasicSend(Long.parseLong(string),false);
						}
					}
				} else if (basic.getCensor().intValue() == InquirySurveybasic.CENSOR_PASS_NO_SEND.intValue()) {// 审核通过
					
					if(basic.getSendDate().getTime()<=System.currentTimeMillis()){
						inquiryBasicDao.updateCreatorBasicSend(Long.parseLong(string),true);
					}else{
						inquiryBasicDao.updateCreatorBasicSend(Long.parseLong(string),false);
					}
					
//					调查结束时   状态置为已结束
					if(basic.getCloseDate()!=null){
						this.finishSurvey(basic);
					}
					
				} else if (basic.getCensor().intValue() == InquirySurveybasic.CENSOR_DRAUGHT
						.intValue()) {// 草稿
					if (basic.getInquirySurveytype().getCensorDesc().intValue() == InquirySurveytype.CENSOR_NO_PASS.intValue()) {// 需要审核
						inquiryBasicDao.updateCreatorBasicSendB(Long.parseLong(string));
					} else {
						if(basic.getSendDate().getTime()<=System.currentTimeMillis()){
							inquiryBasicDao.updateCreatorBasicSend(Long.parseLong(string),true);
						}else{
							inquiryBasicDao.updateCreatorBasicSend(Long.parseLong(string),false);
						}
					}
					
					if(basic.getCloseDate()!=null){
						this.finishSurvey(basic);
					}
				} else if (basic.getCensor().intValue() == InquirySurveybasic.CENSOR_CLOSE.intValue()) {// 终止
					if(basic.getSendDate().getTime()<=System.currentTimeMillis()){
						inquiryBasicDao.updateCreatorBasicSend(Long.parseLong(string),true);
					}else{
						inquiryBasicDao.updateCreatorBasicSend(Long.parseLong(string),false);
					}
				} else if (basic.getCensor().intValue() == InquirySurveybasic.CENSOR_NO.intValue()) {// 未审核
					if (basic.getInquirySurveytype().getCensorDesc().intValue() == InquirySurveytype.CENSOR_PASS.intValue()) {// 不需要审核
						if(basic.getSendDate().getTime()<=System.currentTimeMillis()){
							inquiryBasicDao.updateCreatorBasicSend(Long.parseLong(string),true);
						}else{
							inquiryBasicDao.updateCreatorBasicSend(Long.parseLong(string),false);
						}
					}
				}
				this.addPendingAffair(basic, ApplicationSubCategoryEnum.inquiry_write);
				this.sendMessage(basic);
				
			}
		}
	}

	/**
	 * 获取某条调查类型详细信息
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SurveyTypeCompose getSurveyTypeComposeBYID(long id) throws Exception {
		
		SurveyTypeCompose sbcompose = new SurveyTypeCompose();
		InquirySurveytype type = this.getSurveyTypeById(id);
		Set<InquirySurveytypeextend> surveytypeextends = type.getInquirySurveytypeextends();
		
		//保持用户设定管理员的顺序 added by Meng Yang at 2009-06-29
		List<InquirySurveytypeextend> sortedList = new ArrayList<InquirySurveytypeextend>();
		if(surveytypeextends!=null && surveytypeextends.size()>0) {
			sortedList.addAll(surveytypeextends);
			Collections.sort(sortedList);
		}
		
		if(sortedList!=null && sortedList.size()>0) {
			List<V3xOrgMember> manager = new ArrayList<V3xOrgMember>();
			for (InquirySurveytypeextend surveytypeextend : sortedList) {
				V3xOrgMember member = this.orgManager.getEntityById(
						V3xOrgMember.class, surveytypeextend.getManagerId());// 
				if (surveytypeextend.getManagerDesc().intValue() == InquirySurveytypeextend.MANAGER_SYSTEM
						.intValue()) {// 管理员
					manager.add(member);
				} else if (surveytypeextend.getManagerDesc().intValue() == InquirySurveytypeextend.MANAGER_CHECK
						.intValue()) {// 审核人员
					sbcompose.setChecker(member);
				}
			}
			sbcompose.setManagers(manager);
		}
		
		sbcompose.setInquirySurveytype(type);
		
		return sbcompose;
		
	}

	/**
	 * 根据ID获取InquirySurveytype
	 * 
	 * @param id
	 * @return
	 */
	public InquirySurveytype getSurveyTypeById(Long id) throws Exception {
		
		if(id == null){
			return null;
		}
		
		InquirySurveytype type = allSurveyTypeMap.get(id);
		return type!=null && type.getFlag()!=1 ? type : null ;
	}
	
	public boolean getInquirytypeById(Long typeId) throws Exception{
		InquirySurveytype inquirytype = getSurveyTypeById(typeId);
		return inquirytype==null ? false : true;
	}
	
	public boolean hasInquiryExist(Long bid) throws Exception{
		InquirySurveybasic ibasic = inquiryBasicDao.getInquirySurveybasicID(bid,false);
		return ibasic==null ? false : true;
	}
	
	public InquirySurveytype getInquirySurveytypeByIdNoFlag(long id) throws Exception {
		return inquiryTypeDao.getInquirySurveytypeByIdNoFlag(id);
	}

	/**
	 * 获取当前用户有权发布的调查类型列表
	 */
	public List<InquirySurveytype> getInquiryTypeListByUserAuth()
			throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		String authID = orgManager.getUserIDDomain(memberid, "Account","Department",
				"Team", "Member", "Post", "Level");
		List<InquirySurveytype> alist = inquiryTypeDao
				.getInquiryTypeListByUserAuth(authID,memberid);
		return alist;
	}
	/**
	 * 获取当前自定义空间用户有权发布的调查类型列表
	 */
	public List<InquirySurveytype> getInquiryTypeListByUserAuth(long spaceId) throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		String authID = orgManager.getUserIDDomain(memberid, "Account","Department", "Team", "Member", "Post", "Level");
		List<InquirySurveytype> alist = inquiryTypeDao.getCustomInquiryTypeByUserAuth(spaceId, authID, memberid);
		return alist;
	}
	
	/**
	 * 获取当前用户有权发布的集团调查类型列表
	 */
	public List<InquirySurveytype> getGroupInquiryTypeListByUserAuth()
			throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
//		String authID = orgManager.getUserIDDomain(memberid,"Account", "Department",
//				"Team", "Member", "Post", "Level");
		String authID = Constants.getOrgIdsOfUser(memberid);
		List<InquirySurveytype> alist = inquiryTypeDao
				.getGroupInquiryTypeListByUserAuth(authID,memberid);
		return alist;
	}

	/**
	 * 判断当前用户是否为当前调查类型的管理员
	 * 
	 * @param inquirytype
	 * @return
	 */
	public boolean isInquiryManager(long typeid) throws Exception {
		try {
			User member = CurrentUser.get();
			long memberid = member.getId();// 获取当前用户ID
//			String hql = "SELECT DISTINCT ise From "
//					+ InquirySurveytypeextend.class.getName()
//					+ " ise, "
//					+ InquirySurveytype.class.getName()
//					+ " t "
//					+ " Where  t.id=? AND t.id=ise.inquirySurveytype.id AND t.flag="
//					+ InquirySurveytype.FLAG_NORMAL.intValue()
//					+ " AND ise.managerId=? and ise.managerDesc=?";
//			List ise = inquiryDao.find(hql, typeid, memberid, InquirySurveytypeextend.MANAGER_SYSTEM.intValue());
			Set<Long> canManageTypes = canManageSurveyTypeMap.get(Long.valueOf(memberid));
			if(canManageTypes!= null && canManageTypes.size()>0){
				return canManageTypes.contains(typeid);
			}else{
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	public List<String> getAuthorities(InquirySurveytype type) throws Exception {
		try {
			List<String> list = new ArrayList<String>();
			User member = CurrentUser.get();
			long memberid = member.getId();// 获取当前用户ID
			String hql = "SELECT DISTINCT ise From "
					+ InquirySurveytypeextend.class.getName() + " ise"
					+ " Where  ise.inquirySurveytype.id = ? AND ise.managerId=? ";
			List<Long> params=new ArrayList<Long>();
			params.add(type.getId());
			params.add(memberid);
			List<InquirySurveytypeextend> ise = inquiryDao.find(hql,null, params);
			for (InquirySurveytypeextend surveytypeextend : ise) {
				if (surveytypeextend.getManagerDesc().intValue() == surveytypeextend.MANAGER_SYSTEM
						.intValue())// 管理员
					list.add("manager");
				if (surveytypeextend.getManagerDesc().intValue() == surveytypeextend.MANAGER_CHECK
						.intValue())// 管理员
					list.add("checker");
			}
			return list;
		} catch (Exception e) {
			throw new Exception("");
		}
	}

	@SuppressWarnings("unchecked")
	public boolean isInquiryManagerInSys(User user,boolean isGroup) throws Exception {
		long memberid = user.getId();
		String hql = "From " + InquirySurveytypeextend.class.getName()
		             + " AS ise Where ise.managerId=? AND ise.managerDesc=? AND ise.inquirySurveytype.flag=?"
		             + " and ise.inquirySurveytype.accountId=? and ise.inquirySurveytype.spaceType=?";
		Object[] paramValues = new Object[] {
										memberid, 
										InquirySurveytypeextend.MANAGER_SYSTEM.intValue(), 
										InquirySurveytype.FLAG_NORMAL.intValue(),
										user.getLoginAccount(),
										(isGroup ? InquirySurveytype.Space_Type_Group : InquirySurveytype.Space_Type_Account).intValue()
										};
		List<InquirySurveytypeextend> ise = inquiryDao.find(hql, paramValues);
		if (ise != null && ise.size() > 0) {
			return true;
		}
		
		return false;
	}

	/**
	 * 判断当前用户是否有当前调查类型下的审核权限
	 * 
	 * @param inquirytype
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public boolean isInquiryChecker(long typeid) throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		String hql = "SELECT DISTINCT ise From "
				+ InquirySurveytypeextend.class.getName()
				+ " ise, "
				+ InquirySurveytype.class.getName()
				+ " t "
				+ " Where  t.id=? AND t.id=ise.inquirySurveytype.id AND t.flag="
				+ InquirySurveytype.FLAG_NORMAL.intValue()
				+ " AND ise.managerId=? and ise.managerDesc=?";
		List<InquirySurveytypeextend> ise = inquiryDao.find(hql, typeid, memberid,
				InquirySurveytypeextend.MANAGER_CHECK.intValue());
		if (ise.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前用户是否有任一调查类型下审核权限
	 * 
	 * @param inquirytype
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public boolean isInquiryChecker() throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		//HQL语句清理 modified by Meng Yang 2009-05-31
		String hql = "From "
				+ InquirySurveytypeextend.class.getName()
				+ " AS ise Where ise.managerId=? AND ise.managerDesc=? AND ise.inquirySurveytype.flag =?";
				//+ InquirySurveytype.FLAG_NORMAL.intValue();
		List<InquirySurveytypeextend> ise = inquiryDao.find(hql, memberid,
				InquirySurveytypeextend.MANAGER_CHECK.intValue(), InquirySurveytype.FLAG_NORMAL.intValue());
		if (ise.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前用户是否有任一调查类型下的发布权限
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isInquiryAuthorities() throws Exception {
		//HQL语句清理 modified by Meng Yang 2009-05-31
		String hql = "From InquirySurveytype AS ist Where ist.flag=?";
				//+ InquirySurveytype.FLAG_NORMAL;
		List<InquirySurveytype> tlist = inquiryDao.find(hql, InquirySurveytype.FLAG_NORMAL);
		for (InquirySurveytype inquirytype : tlist) {
			if (this.isInquiryAuthorities(inquirytype.getId())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断当前用户是否有任一集团空间调查类型下的发布权限
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean isInquiryAuthoritiesOfGroup() throws Exception {
		//HQL语句清理 modified by Meng Yang 2009-05-31
		/*String hql = "From InquirySurveytype AS ist Where ist.flag="
				+ InquirySurveytype.FLAG_NORMAL +" and ist.spaceType = " + InquirySurveytype.Space_Type_Group;*/
		String hql = "From InquirySurveytype AS ist Where ist.flag=? and ist.spaceType=?";
		List<InquirySurveytype> tlist = inquiryDao.find(hql, InquirySurveytype.FLAG_NORMAL, InquirySurveytype.Space_Type_Group);
		for (InquirySurveytype inquirytype : tlist) {
			if (this.isInquiryAuthorities(inquirytype.getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断当前用户是否有当前调查类型下的发布权限
	 * 
	 * @param inquirytype
	 * @return
	 */
	public boolean isInquiryAuthorities(long inquirytype) throws Exception {
		if (this.isAuthoritiesUser(inquirytype)) {
			return true;
		}
		return false;
	}
	
	public boolean isInquiryAuthorities(long inquirytype,List<V3xOrgMember> managers) throws Exception{
		String managerIds = "";
		for(V3xOrgMember member : managers){
			managerIds+=member.getId()+",";
		}
		if (this.isAuthoritiesUser(inquirytype,managerIds)) {
			return true;
		}
		return false;
	}

	/**
	 * 当前用户布当前调查类型发布权限判断
	 * 
	 * @param inquirytype
	 * @return
	 * @throws Exception
	 */
	private boolean isAuthoritiesUser(long type) throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		//以前做法：1，首先数据库存的全是Member，（现在改授权可以授给Post,level，Team，Dept）,所以以前直接拿取到的人员ID 和数据库管理的板块相比较，否则就是没权限。
		//以前做发：2，授权方式一改，被授权的人员如果是通过Post或者其他方式，每次走else，都要去数据库查询一次，性能有问题，没有充分用到板块，管理员 加内存的好处。
		//现在改：  3，授权方式一改，首先取到关于人的所以关联的ID（包括dept,Team,Post....）,然后去内存板块管理员处匹配。
//		String authID = orgManager.getUserIDDomain(memberid, "Department", "Team", "Member", "Post", "Level");
//		List<InquiryAuthority> alist = inquiryAuthDao.getInquiryAuthorityByUser(type, authID);
//		if (alist != null && alist.size() > 0) {
//			return true;
//		}
//		return false;
		
		/*
		 * 
		 * 7.21 lucx
		 * 
		 */
//		Set<Long> canIssueTypes = canIssueSurveyTypeMap.get(memberid);
		Set<Long> canIssueTypes = new HashSet<Long>();
		List<Long> orgIds=null;
		try {
			orgIds = orgManager.getUserDomainIDs(memberid, V3xOrgEntity.VIRTUAL_ACCOUNT_ID,
					V3xOrgEntity.ORGENT_TYPE_ACCOUNT,
					V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
					V3xOrgEntity.ORGENT_TYPE_TEAM,
					V3xOrgEntity.ORGENT_TYPE_POST,
					V3xOrgEntity.ORGENT_TYPE_ROLE,
					V3xOrgEntity.ORGENT_TYPE_LEVEL,
					V3xOrgEntity.ORGENT_TYPE_MEMBER);
		} catch (BusinessException e) {
			log.error("获取人员关联的实体ID失败", e);
		}
		
		if(orgIds != null){
			for(long key : canIssueSurveyTypeMap.keySet()){
				if(orgIds.contains(key))
					canIssueTypes.addAll(canIssueSurveyTypeMap.get(key));				
			}
		}
		
		if(canIssueTypes!=null && canIssueTypes.size()>0){
			return canIssueTypes.contains(type);
		}else{
			return false;
			//以前是又取一次数据库
			/*String authID = orgManager.getUserIDDomain(memberid, "Department", "Team", "Member", "Post", "Level");
			List<InquiryAuthority> alist = inquiryAuthDao.getInquiryAuthorityByUser(type, authID);
			if (alist != null && alist.size() > 0) {
				return true;
			}
			return false;*/
			
		}
		
	}
	
	private boolean isAuthoritiesUser(long type,String managers) throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		String authID = managers+orgManager.getUserIDDomain(memberid, "Department",
				"Team", "Member", "Post", "Level");
		List<InquiryAuthority> alist = inquiryAuthDao
				.getInquiryAuthorityByUser(type, authID);
		if (alist != null && alist.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 保存授权用户
	 */
	public void saveInquiryAuthorities(long tid, String authscope) throws Exception {
		InquirySurveytype type = allSurveyTypeMap.get(tid);
		Set<InquiryAuthority> auth = type.getInquiryAuthorities();
		// 先清空该调查类型下的权限
		List<InquiryAuthority> authoritylist = inquiryAuthDao.getAuthorityList(tid);
		for (InquiryAuthority authority : authoritylist) {
			inquiryAuthDao.remove(authority);

			// 清除内存中某个人的发布授权
			Set<Long> issueType = canIssueSurveyTypeMap.get(authority.getAuthId());
			if(issueType!=null && issueType.size()>0){
				if(issueType.contains(tid)){
					issueType.remove(tid);
				}
			}
			canIssueSurveyTypeMap.put(authority.getAuthId(), issueType);
			
			// 清除当前调查类型下发布授权
			auth.clear();
		}
		if (authscope != null) {
			java.util.StringTokenizer entities = new java.util.StringTokenizer( authscope , ",|" );
			
			while (entities.hasMoreTokens()) {
				// 保存当前的新用户权限
				InquiryAuthority iAuth = new InquiryAuthority();
				iAuth.setIdIfNew();// 加入ID
				iAuth.setInquirySurveytype(type);// 加入调查类型
				iAuth.setAuthDesc(entities.nextToken());
				
				Long userId = Long.parseLong(entities.nextToken());
				Set<Long> issueType = canIssueSurveyTypeMap.get(userId);
				
				iAuth.setAuthId(userId);// 加入授权用户ID
				inquiryBasicDao.save(iAuth);
				
				if(issueType!=null && issueType.size()>0){
					if(!issueType.contains(tid)){
						issueType.add(tid);
					}
				}else{
					issueType = new HashSet<Long>();
					issueType.add(tid);
				}
				
				if(auth == null){
					auth = new HashSet<InquiryAuthority>();
					auth.add(iAuth);
				}else{
					auth.add(iAuth);
				}
				canIssueSurveyTypeMap.put(userId, issueType);
			}
		}
	}
	
	/**
	 * 获取授权的用户组织列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public SurveyAuthCompose getAuthoritiesList(long typeid) throws Exception {

		if (!this.isInquiryManager(typeid)) {// 当前用户不是管理员
			return null;
		}
		SurveyAuthCompose sac = new SurveyAuthCompose();
		String hql = "SELECT DISTINCT ia From "
				+ InquiryAuthority.class.getName()
				+ " ia ,"
				+ InquirySurveytype.class.getName()
				+ " t "
				+ " Where (ia.inquirySurveytype.id=t.id) AND t.id=? AND t.flag=?";
				//+ InquirySurveytype.FLAG_NORMAL.intValue();
		List<InquiryAuthority> alist = inquiryAuthDao.find(hql, typeid, InquirySurveytype.FLAG_NORMAL.intValue());
		List<AuthUserCompose> list = new ArrayList<AuthUserCompose>();
		String authidlist = "";
		for (InquiryAuthority authority : alist) {
			AuthUserCompose au = new AuthUserCompose();
			long egid = authority.getAuthId();
			String desc = authority.getAuthDesc();
			V3xOrgEntity org = this.orgManager.getGlobalEntity(desc, egid);
			au.setUname(org.getName());
			authidlist += authority.getAuthDesc() + "|" + authority.getAuthId()
					+ ",";
			list.add(au);
		}
		sac.setAuths(list);
		sac.setAuthlist(authidlist);
		return sac;
	}

	/**
	 * 新建调查
	 * 
	 * @param basic
	 * @throws Exception
	 */
	public void saveSurveyBasic(InquirySurveybasic basic, String bid)
			throws Exception {
		if (basic == null) {
			return;
		} else {
			if (bid != null && !bid.equals("")) {
				InquirySurveybasic b = inquiryBasicDao.getInquirySurveybasicID(Long.parseLong(bid),false);
				if (b == null) {
					return;
				}
				inquiryBasicDao.remove(b);
				//同时删除对应的待办事项记录，避免在审核员处理之后待办事项中的旧有记录无法清空，且在点击时提示信息不够友好 added by Meng Yang at 2009-07-14
				affairManager.deleteByObject(ApplicationCategoryEnum.inquiry, Long.valueOf(bid));
			}
			inquiryBasicDao.save(basic);
			if(basic.getCensor().intValue() == InquirySurveybasic.CENSOR_PASS ){
				this.addPendingAffair(basic, ApplicationSubCategoryEnum.inquiry_write);
				this.sendMessage(basic);
				//如果设置了结束时间  在调查结束时设个定时管理   将状态置为结束
				if(basic.getCloseDate()!=null){
					this.finishSurvey(basic);
				}
			}else if(basic.getCensor().intValue() == InquirySurveybasic.CENSOR_NO){		//如果为未审核将此调查添加到审核员得个人待办列表中
				this.addPendingAffair(basic, ApplicationSubCategoryEnum.inquiry_audit);
			}
			
		}
	}

	/**
	 * 生成待办事项
	 */
	public void addPendingAffair(InquirySurveybasic basic, ApplicationSubCategoryEnum subApp) throws BusinessException {
		Set<Long> receiverIds = new HashSet<Long>();
		if (subApp == ApplicationSubCategoryEnum.inquiry_audit) {
			// 审核人
			receiverIds.add(basic.getCensorId());
		} else if (subApp == ApplicationSubCategoryEnum.inquiry_write) {
			// 发布范围
			Set<InquiryScope> scopeset = basic.getInquiryScopes();
			for (InquiryScope scope : scopeset) {
				Set<V3xOrgMember> v3xMermberList = this.orgManager.getMembersByType(scope.getScopeDesc(), scope.getScopeId());
				for (V3xOrgMember member2 : v3xMermberList) {
					if (member2 != null && member2.isValid()) {
						receiverIds.add(member2.getId());
					}
				}
			}
		}
		
		InquirySurveytype inquirySurveytype = allSurveyTypeMap.get(basic.getSurveyTypeId());

		List<Affair> affairs = new ArrayList<Affair>(receiverIds.size());
		for (Long memberId : receiverIds) {
			Affair affair = new Affair();
			affair.setIdIfNew();
			affair.setObjectId(basic.getId());
			affair.setState(StateEnum.col_pending.key());
			affair.setApp(ApplicationCategoryEnum.inquiry.getKey());
			affair.setSubApp(subApp.key());
			affair.setMemberId(memberId);
			affair.setSenderId(basic.getCreaterId());
			affair.setSubject(basic.getSurveyName());
			affair.setCreateDate(basic.getSendDate());
			affair.setReceiveTime(basic.getSendDate());
			affair.setUpdateDate(new Date());
			
			affair.addExtProperty("spaceType", inquirySurveytype.getSpaceType());
			affair.addExtProperty("spaceId", inquirySurveytype.getAccountId());
			affair.addExtProperty("typeId", basic.getSurveyTypeId());
			affair.serialExtProperties();
			
			affairs.add(affair);
		}
		affairManager.createAffairs(affairs);
	}

	/**
	 * 保存调查模板
	 * 
	 * @param basic
	 * @throws Exception
	 */
	public void saveSurveyBasicTemp(InquirySurveybasic basic, String bid)
			throws Exception {
		if (basic == null) {
			return;
		} else {
			if (bid != null && !bid.equals("")) {
				InquirySurveybasic b = inquiryBasicDao.getInquirySurveybasicID(Long.parseLong(bid),true);
				if (b != null) {
					inquiryBasicDao.remove(b);
				}
			}
			inquiryBasicDao.save(basic);
			
		}
	}

	/**
	 * 更新调查
	 * 
	 * @param basic
	 * @throws Exception
	 */
	public void updateSurveyBasic(InquirySurveybasic basic) throws Exception {
		if (basic == null) {
			return;
		} else {
			inquiryBasicDao.update(basic);
		}
	}
	
	private List<Long> getDomainIds() {
		return this.getDomainIds(CurrentUser.get());
	}
	
	private List<Long> getDomainIds(User user) {
		List<Long> result = null;
		try {
			if(user.isInternal()){
				result = this.orgManager.getUserDomainIDs(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, 
						V3xOrgEntity.ORGENT_TYPE_ACCOUNT,
						V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
						V3xOrgEntity.ORGENT_TYPE_TEAM,
						V3xOrgEntity.ORGENT_TYPE_POST,
						V3xOrgEntity.ORGENT_TYPE_LEVEL,
						V3xOrgEntity.ORGENT_TYPE_MEMBER);
			} else {
				result = this.orgManager.getUserDomainIDs(user.getId(), V3xOrgEntity.VIRTUAL_ACCOUNT_ID, 
						V3xOrgEntity.ORGENT_TYPE_DEPARTMENT,
						V3xOrgEntity.ORGENT_TYPE_TEAM,
						V3xOrgEntity.ORGENT_TYPE_POST,
						V3xOrgEntity.ORGENT_TYPE_LEVEL,
						V3xOrgEntity.ORGENT_TYPE_MEMBER);
			}
			
		} catch (BusinessException e) {
			log.error("获取当前人员各种组织模型实体ID失败", e);
		}
		return result;
	}

	/**
	 * 获取对当前用户可见的调查列表
	 * 
	 * @return
	 */
	public List<SurveyBasicCompose> getInquiryBasicListByUserID(int size) throws Exception {		
		
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		
		// long memberid =Long.parseLong("5428894131317720573");
		String authID = orgManager.getUserIDDomain(memberid, V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Account" , "Department", "Team", "Member", "Post", "Level");
		
		List<InquirySurveybasic> scopelist = inquiryBasicDao.getInquiryBasicListByUserScope(authID, size);
		
		for(InquirySurveybasic basic : scopelist){
			basic.setInquirySurveytype(allSurveyTypeMap.get(basic.getSurveyTypeId()));
		}
		
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		for (InquirySurveybasic ibasic : scopelist) {
			bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
		}
		
		return bscomposelist;
	}
	/**
	 * 自定义空间获取对当前用户可见的调查列表
	 * @param spaceId
	 * @param size
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getCustomInquiryBasicListByUserScope(long spaceId, int size) throws Exception {		
		User member = CurrentUser.get();
		long memberid = member.getId();
		String authID = orgManager.getUserIDDomain(memberid, "Account" , "Department", "Team", "Member", "Post", "Level");
		List<InquirySurveybasic> scopelist = inquiryBasicDao.getCustomInquiryBasicListByUserScope(spaceId, authID, size);
		for(InquirySurveybasic basic : scopelist){
			basic.setInquirySurveytype(allSurveyTypeMap.get(basic.getSurveyTypeId()));
		}
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		for (InquirySurveybasic ibasic : scopelist) {
			bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
		}
		return bscomposelist;
	}
	
	/**
	 * 集团空间调查列表
	 * 
	 * @return
	 */
	public List<SurveyBasicCompose> getGroupInquiryBasicList(int size) throws Exception {
		List<Long> domainIds = this.getDomainIds();
		List<InquirySurveybasic> scopelist = inquiryBasicDao.getGroupInquiryBasicListByUserScope(domainIds, size, CurrentUser.get().getId());
		
		for(InquirySurveybasic basic : scopelist){
			basic.setInquirySurveytype(allSurveyTypeMap.get(basic.getSurveyTypeId()));
		}
		
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		for (InquirySurveybasic ibasic : scopelist) {
			bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
		}
		return bscomposelist;
	}

	/**
	 * 获取对当前用户全部可见的调查列表
	 * 
	 * @return
	 */
	public List<SurveyBasicCompose> getALLInquiryBasicListByUserID( String typeId , String condition , String textfield , String textfield1 , boolean isGroup)
			throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		boolean isInternal = member.isInternal();
		String authID = "";
		if(isInternal){
			authID = orgManager.getUserIDDomain(memberid , V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Account","Department", "Team","Member", "Post", "Level", "Role");
		}else{
			authID = orgManager.getUserIDDomain(memberid ,  V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Department", "Team", "Member", "Post", "Level", "Role");
		}
		List<InquirySurveybasic> scopelist = inquiryBasicDao.getALLBasicListByUserScope(authID , condition , textfield , textfield1 , isGroup , typeId );
		
		for(InquirySurveybasic basic : scopelist ){
			basic.setInquirySurveytype(allSurveyTypeMap.get(basic.getSurveyTypeId()));
		}
		
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		for (InquirySurveybasic ibasic : scopelist) {
			bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
		}
		return bscomposelist;
	}
	
	/**
	 * 获取对当前用户全部可见的自定义空间调查列表
	 */
	public List<SurveyBasicCompose> getALLCustomInquiryBasicListByUserID(long spaceId, int spaceType, String typeId , String condition , String textfield , String textfield1 , boolean isGroup)
			throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		boolean isInternal = member.isInternal();
		String authID = "";
		if(isInternal){
			authID = orgManager.getUserIDDomain(memberid , "Account","Department", "Team","Member", "Post", "Level", "Role");
		}else{
			authID = orgManager.getUserIDDomain(memberid ,  "Department", "Team", "Member", "Post", "Level", "Role");
		}
		List<InquirySurveybasic> scopelist = inquiryBasicDao.getALLCustomBasicListByUserScope(spaceType, authID , condition , textfield , textfield1 , isGroup , typeId );
		List<InquirySurveytype> alist = inquiryTypeDao.getAllCustomInquiryType(spaceId);
		List<Long> surveyTypeIds = new ArrayList<Long>();
		for(InquirySurveytype surveytype : alist){
			surveyTypeIds.add(surveytype.getId());
		}
		for(InquirySurveybasic basic : scopelist ){
			if(surveyTypeIds.contains(basic.getSurveyTypeId())){
				basic.setInquirySurveytype(allSurveyTypeMap.get(basic.getSurveyTypeId()));
			}
		}
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		for (InquirySurveybasic ibasic : scopelist) {
			if(surveyTypeIds.contains(ibasic.getSurveyTypeId())){
				bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
			}
		}
		return bscomposelist;
	}
	
	/**
	 * 获取对当前用户全部可见的调查列表   不需要分页
	 * 
	 * @return
	 */
	public List<SurveyBasicCompose> getALLInquiryBasicListByUserID(boolean isGroup)
			throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		boolean isInternal = member.isInternal();
		String authID = "";
		if(isInternal){
			authID = orgManager.getUserIDDomain(memberid , "Account" , "Department", "Team", "Member", "Post", "Level");
		}else{
			authID = orgManager.getUserIDDomain(memberid , "Department", "Team", "Member", "Post", "Level");
		}
		List<InquirySurveybasic> scopelist = inquiryBasicDao.getALLBasicListByUserScope(authID , isGroup );
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		for (InquirySurveybasic ibasic : scopelist) {
			bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
		}
		return bscomposelist;
	}
	
	
	/**
	 * 获取对当前用户全部可见的集团空间调查列表
	 * 
	 * @return
	 */
	public List<SurveyBasicCompose> getGroupInquiryList( String typeId , String condition , String textfield , String textfield1 )
			throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		
		boolean isInternal = member.isInternal();
		String authID = "";
		if(isInternal){
			authID = orgManager.getUserIDDomain(memberid , V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Account","Department", "Team","Member", "Post", "Level", "Role");
		}else{
			authID = orgManager.getUserIDDomain(memberid , V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Department", "Team","Member", "Post", "Level", "Role");
		}
		
		List<InquirySurveybasic> scopelist = inquiryBasicDao.getGroupBasicListByUserScope(authID , condition , textfield , textfield1 , typeId );
		
		for(InquirySurveybasic basic : scopelist ){
			basic.setInquirySurveytype(allSurveyTypeMap.get(basic.getSurveyTypeId()));
		}
		
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		for (InquirySurveybasic ibasic : scopelist) {
			bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
		}
		return bscomposelist;
	}

	/**
	 * 获取当前调查类型下当前用户可见的调查列表
	 * 
	 * @param inquirytype
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getSurveyBasicListByType(long typeid, String mid , String condition, String textfield, String textfield1) throws Exception {
		List<InquirySurveybasic> blist = null;
		Timestamp nowtime = new Timestamp(System.currentTimeMillis());
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		boolean isInternal = member.isInternal();
		String authID = "";
		if(isInternal){
			authID = orgManager.getUserIDDomain(memberid , "Account" , "Department", "Team", "Member", "Post", "Level");
		}else{
			authID = orgManager.getUserIDDomain(memberid , "Department", "Team", "Member", "Post", "Level");
		}
		if (Strings.isNotBlank(mid)) {	// 该用户从管理入口操作
			blist = inquiryBasicDao.getManagerBasicListByType(typeid , condition, textfield, textfield1);
		} else {						// 普通入口操作
			blist = inquiryBasicDao.getInquiryBasicListByUserScopeAndType(nowtime, nowtime, authID, typeid , condition, textfield, textfield1 );
		}
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		if(blist!=null && blist.size()>0) {
			for (InquirySurveybasic ibasic : blist) {
				bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
			}
		}
		return bscomposelist;
	}
	
	/**
	 * 获取当前调查类型下的调查列表   按条数取   首页栏目用到
	 * 
	 * @param inquirytype
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getSurveyBasicListByType( long typeId, int size ) throws Exception{
		
		List<InquirySurveybasic> blist = null;
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		boolean isInternal = member.isInternal();
		String authID = "";
		
		if(isInternal){
			authID = orgManager.getUserIDDomain(memberid , V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Account" , "Department", "Team", "Member", "Post", "Level");
		}else{
			authID = orgManager.getUserIDDomain(memberid , V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Department", "Team", "Member", "Post", "Level");
		}
		
		blist = inquiryBasicDao.getInquiryBasicListByUserScopeAndType( typeId , authID , size);
			
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		
		for (InquirySurveybasic ibasic : blist) {
			bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
		}
		
		return bscomposelist;
	}

	/**
	 * 根据调查ID查找当前用户有权看见的调查
	 * 
	 * @param basicid
	 * @return
	 * @throws Exception
	 */
	public SurveyBasicCompose getInquiryBasicByUserIDAndBasicID(long id)
			throws Exception {
		
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		boolean isInternal = member.isInternal();
		String authID = "";
		
		if(isInternal){
			authID = orgManager.getUserIDDomain(memberid , V3xOrgEntity.VIRTUAL_ACCOUNT_ID,"Account" , "Department", "Team", "Member", "Post", "Level");
		}else{
			authID = orgManager.getUserIDDomain(memberid , "Department", "Team", "Member", "Post", "Level");
		}
		
		Timestamp nowtime = new Timestamp(System.currentTimeMillis());
		InquirySurveybasic ibasic = inquiryBasicDao.getInquiryBasicByUserScopeAndBasicID(nowtime, nowtime, authID, id);
		
		if(ibasic ==null)
			 return null;
		
		ibasic.setInquirySurveytype(allSurveyTypeMap.get(ibasic.getSurveyTypeId()));
		
		Set<InquiryScope> scopeset = ibasic.getInquiryScopes();
		Set<InquirySubsurvey> subsurveySet = ibasic.getInquirySubsurveys();
		Set<InquirySubsurveyitem> itemSet = ibasic.getInquirySubsurveyitems();
		SurveyBasicCompose sbcompose = this.surveyBasicComposeViewObject(
				ibasic, scopeset, subsurveySet, itemSet);
		// 判断该用户是否点击过该调查
		if (inquiryBasicDao.getClickByUser(memberid, id)) {// 没有点击过
			inquiryBasicDao.updateBasicByCilckCount(id);// 更新点击次数
			inquiryBasicDao.updatClick(memberid, id);// 更新点击记录
		}
		return sbcompose;
	}

	/**
	 * 判断当前用户有权看见该调查否
	 * 
	 * @param basicid
	 * @return
	 * @throws Exception
	 */
	public boolean isPowerDatialBasic(long id) throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		boolean isInternal = member.isInternal();
		String authID = "";
		if(isInternal){
			authID = orgManager.getUserIDDomain(memberid , "Account" , "Department", "Team", "Member", "Post", "Level");
		}else{
			authID = orgManager.getUserIDDomain(memberid , "Department", "Team", "Member", "Post", "Level");
		}
		Timestamp nowtime = new Timestamp(System.currentTimeMillis());
		InquirySurveybasic ibasic = inquiryBasicDao
				.getInquiryBasicByUserScopeAndBasicID(nowtime, nowtime, authID,
						id);
		if (ibasic != null) {
			return true;
		}
		return false;
	}

	/**
	 * 将某一调查拼成VO
	 * 
	 * @param ibasic
	 * @return
	 * @throws Exception
	 */
	 public SurveyBasicCompose surveyBasicComposeViewObject(
			InquirySurveybasic ibasic, Set<InquiryScope> scopeset,
			Set<InquirySubsurvey> subsurveySet,
			Set<InquirySubsurveyitem> itemSet) throws Exception {
		SurveyBasicCompose sbcompose = new SurveyBasicCompose();
		sbcompose.setInquirySurveybasic(ibasic);
		V3xOrgMember sender = this.orgManager.getEntityById(V3xOrgMember.class,
				ibasic.getIssuerId());// 获取发布者
		V3xOrgMember conser = new V3xOrgMember();
		if (ibasic.getCensorId() != null) {// 审核人不为空
			conser = this.orgManager.getEntityById(V3xOrgMember.class, ibasic
					.getCensorId());// 获取审核人姓名
		}
		V3xOrgDepartment department = this.orgManager.getEntityById(
				V3xOrgDepartment.class, ibasic.getDepartmentId());// 获取发布部门

		List<V3xOrgEntity> eglist = new ArrayList<V3xOrgEntity>();
		for (InquiryScope scope : scopeset) {// 发布对象
			long egid = scope.getScopeId();
			String desc = scope.getScopeDesc();
			V3xOrgEntity org = this.orgManager.getEntity(desc, egid);
			eglist.add(org);
		}
		sbcompose.setEntity(eglist);
		sbcompose.setSender(sender);
		sbcompose.setConser(conser);
		sbcompose.setDeparmentName(department);

		List<SubsurveyAndItemsCompose> sAndIcomList = new ArrayList<SubsurveyAndItemsCompose>();
		for (InquirySubsurvey subsurvey : subsurveySet) {
			SubsurveyAndItemsCompose siCompose = new SubsurveyAndItemsCompose();
			siCompose.setInquirySubsurvey(subsurvey);
			List<InquirySubsurveyitem> itemList = new ArrayList<InquirySubsurveyitem>();
			for (InquirySubsurveyitem subsurveyitem : itemSet) {
				if (subsurveyitem.getSubsurveyId() == subsurvey.getId()) {
					// 将问题中SubsurveyId等于subsurvey的问题加到一起
					itemList.add(subsurveyitem);
				}
			}
			siCompose.setItems(itemList);
			sAndIcomList.add(siCompose);
		}
		sbcompose.setSubsurveyAndICompose(sAndIcomList);
		return sbcompose;
	}

	/**
	 * 将某一系列调查拼成VO列表
	 * 
	 * @param bscomposelist
	 * @param ibasic
	 * @param scopeObject
	 * @return
	 * @throws Exception
	 */
	private List<SurveyBasicCompose> getSurveyBasicInterior(
			List<SurveyBasicCompose> bscomposelist, InquirySurveybasic ibasic)
			throws Exception {
		SurveyBasicCompose sbcompose = new SurveyBasicCompose();
		sbcompose.setInquirySurveybasic(ibasic);
		bscomposelist.add(sbcompose);
		return bscomposelist;
	}

	/**
	 * 获取当前调查审核员待审核的最新5条调查
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getCheckSurveyBasicListByChecher()
			throws Exception {
		
		User member = CurrentUser.get();
		
		long memberid = member.getId();// 获取当前用户ID
		
		List<InquirySurveybasic> basiclist = inquiryBasicDao.getCheckSurveyBasicListByChecker(memberid);
		
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();

		for (InquirySurveybasic ibasic : basiclist) {
			
			bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
			
		}
		
		return bscomposelist;
	}

	/**
	 * 获取当前调查审核员待审核的调查列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getWaitCensorBasicListByChecker(String condition, String textfield, String textfield1,String surveyTypeId)throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		List<InquirySurveybasic> basiclist = inquiryBasicDao.getWaitCensorBasicListByChecker( memberid , condition , textfield , textfield1,surveyTypeId);
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		
		for (InquirySurveybasic ibasic : basiclist) {
//			bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);//修改前循环就这一句代码
//			调查类型加入
			InquirySurveytype type = this.getSurveyTypeById(ibasic.getSurveyTypeId());
			SurveyBasicCompose sbcompose = new SurveyBasicCompose();
			ibasic.setInquirySurveytype(type);
			sbcompose.setInquirySurveybasic(ibasic);			
			bscomposelist.add(sbcompose);
		}
		return bscomposelist;
	}
	/**
	 * 自定义空间获取当前调查审核员待审核的调查列表
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @param surveyTypeId
	 * @param spaceId
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getWaitCensorBasicListByChecker(String condition, String textfield, String textfield1,String surveyTypeId,long spaceId) throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();
		List<InquirySurveybasic> basiclist = inquiryBasicDao.getCustomWaitCensorBasicListByChecker( memberid , condition , textfield , textfield1,surveyTypeId,spaceId);
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		for (InquirySurveybasic ibasic : basiclist) {
			InquirySurveytype type = this.getSurveyTypeById(ibasic.getSurveyTypeId());
			SurveyBasicCompose sbcompose = new SurveyBasicCompose();
			ibasic.setInquirySurveytype(type);
			sbcompose.setInquirySurveybasic(ibasic);			
			bscomposelist.add(sbcompose);
		}
		return bscomposelist;
	}
	
	/**
	 * 获取当前调查审核员待审核的集团调查列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getWaitCensorGroupBasicListByChecker(String condition, String textfield, String textfield1,String surveyTypeId)
			throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		List<InquirySurveybasic> basiclist = inquiryBasicDao.getWaitCensorGroupBasicListByChecker(memberid , condition , textfield , textfield1, surveyTypeId);
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
	
		for (InquirySurveybasic ibasic : basiclist) {
//			bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);//修改前循环就这一句代码
			//调查类型加入
			InquirySurveytype type = null;
			SurveyBasicCompose sbcompose = new SurveyBasicCompose();
			type  = this.getSurveyTypeById(ibasic.getSurveyTypeId());
			ibasic.setInquirySurveytype(type);
			sbcompose.setInquirySurveybasic(ibasic);	
			
			bscomposelist.add(sbcompose);
			
		}
		return bscomposelist;
	}
	
	
	/**
	 * 获取当前调查审核员待审核的调查列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getWaitCensorBasicListByCheckerInt(String condition, String textfield, String textfield1,String surveyTypeId)throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		int size=inquiryBasicDao.getWaitCensorBasicListByCheckerInt(memberid, condition, textfield, textfield1, surveyTypeId);
		return size;
	}
	/**
	 * 自定义空间获取当前调查审核员待审核的调查列表
	 * @param condition
	 * @param textfield
	 * @param textfield1
	 * @param surveyTypeId
	 * @param spaceId
	 * @return
	 * @throws Exception
	 */
	public int getCustomWaitCensorBasicListByCheckerInt(String condition, String textfield, String textfield1, String surveyTypeId, long spaceId) throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();
		int size=inquiryBasicDao.getCustomWaitCensorBasicListByCheckerInt(memberid, condition, textfield, textfield1, surveyTypeId, spaceId);
		return size;
	}
	
	/**
	 * 获取当前调查审核员待审核的集团调查列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getWaitCensorGroupBasicListByCheckerInt(String condition, String textfield, String textfield1,String surveyTypeId)
			throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		int size=inquiryBasicDao.getWaitCensorGroupBasicListByCheckerInt(memberid, condition, textfield, textfield1, surveyTypeId);
		return size;
	}

	/**
	 * 根据ID获取调查
	 * 
	 * @param bid
	 * @param tid
	 * @return
	 * @throws Exception
	 */
	public SurveyBasicCompose getInquiryBasic(String bid) throws Exception {
		
		InquirySurveybasic ibasic = inquiryBasicDao.getInquiryBasic(Long.parseLong(bid));
		
		ibasic.setInquirySurveytype(allSurveyTypeMap.get(ibasic.getSurveyTypeId()));
		
		Set<InquiryScope> scopeset = ibasic.getInquiryScopes();
		Set<InquirySubsurvey> subsurveySet = ibasic.getInquirySubsurveys();
		Set<InquirySubsurveyitem> itemSet = ibasic.getInquirySubsurveyitems();
		SurveyBasicCompose sbcompose = this.surveyBasicComposeViewObject( ibasic, scopeset, subsurveySet, itemSet);
		
		return sbcompose;
	}

	/**
	 * 保存审核人员的操作
	 * 
	 * @param bid
	 * @param hid
	 * @param tid
	 * @throws Exception
	 */
	public boolean saveCheckerHandle(String bid, String hid,String name ,Long id , String checkMind) throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		InquirySurveybasic basic = inquiryBasicDao.getInquirySurveybasicID(Long.parseLong(bid),false);
//		直接清除待办事项
		affairManager.deleteByObject(ApplicationCategoryEnum.inquiry, basic.getId());
		if (basic.getCloseDate() !=null && basic.getCloseDate().getTime() < (new Timestamp(System.currentTimeMillis())).getTime()) {
			// 如果当前审核对象的结束时间早于当前时间 强制为审核未通过
			inquiryBasicDao.updateInquiryBasicNOPass(Long.parseLong(bid),memberid,checkMind);
			return false;
		}
		
		InquirySurveytype type = basic.getInquirySurveytype();
		List<InquirySurveytypeextend> typeExtends = inquiryBasicDao.getSerById(type.getId(),InquirySurveytypeextend.MANAGER_CHECK);
		Long auditId = null;
		Long agentId = null;
		for (InquirySurveytypeextend surveytypeextend : typeExtends) {
			auditId = surveytypeextend.getManagerId();
			agentId = AgentUtil.getAgentByApp(auditId, ApplicationCategoryEnum.inquiry.getKey());
		}
		Long senderId = member.getId();
		String senderName = member.getName();
		int proxyType = 0;
		if(agentId != null && agentId.equals(senderId)){
			proxyType = 1;
			V3xOrgMember m = orgManager.getMemberById(auditId);
			senderId = m.getId();
			senderName = m.getName();
		}

		if (hid.equals(InquiryManager.CENSOR_PASS)) {// 操作为审核通过
			inquiryBasicDao.updateInquiryBasicPass(Long.parseLong(bid), auditId ,checkMind);
			userMessageManager.sendSystemMessage(MessageContent.get("inq.alreadyauditing", basic.getSurveyName(), senderName, proxyType, member.getName()),
					ApplicationCategoryEnum.inquiry, senderId,
					MessageReceiver.get(basic.getId(), basic.getCreaterId(),"message.link.inq.alreadyauditing", String.valueOf(basic.getId())) , basic.getSurveyTypeId());
			
			//审合调查通过加日志
			appLogManager.insertLog(member, AppLogAction.Inquiry_AuditPass, member.getName(), basic.getSurveyName());
			
		} else if (hid.equals(InquiryManager.CENSOR_NO)) {// 审核不通过
			inquiryBasicDao.updateInquiryBasicNOPass(Long.parseLong(bid), auditId , checkMind);
			userMessageManager.sendSystemMessage(MessageContent.get("inq.alreadyauditing.not", basic.getSurveyName(), senderName, proxyType, member.getName()),
					ApplicationCategoryEnum.inquiry, senderId,
					MessageReceiver.get(basic.getId(), basic.getCreaterId(),"message.link.inq.alreadyauditing", String.valueOf(basic.getId())) , basic.getSurveyTypeId());
			
			//审合调查没有通过加日志
			appLogManager.insertLog(member, AppLogAction.Inquiry_AduitNotPass, member.getName(), basic.getSurveyName());
			
		} else if (hid.equals(InquiryManager.CENSOR_SEND)) {	// 立即发送
			inquiryBasicDao.updateInquiryBasicSend(Long.parseLong(bid), auditId, checkMind);
			this.addPendingAffair(basic, ApplicationSubCategoryEnum.inquiry_write);
			this.sendMessage(basic);
			if(basic.getCloseDate()!=null){
				this.finishSurvey(basic);
			}
			
			//直接发布加日志
			appLogManager.insertLog(member, AppLogAction.Inquiry_AuditPublish, member.getName(), basic.getSurveyName());
			
			//直接审核发送加入全文检索
			try {
				
				IndexInfo indexInfo = new IndexInfo();
				indexInfo.setTitle(basic.getSurveyName());
				
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_HTMLSTR);
				StringBuffer content=new StringBuffer();
				if(basic.getSurveydesc() != null)
					content.append(basic.getSurveydesc());
				Set<InquirySubsurveyitem> items=basic.getInquirySubsurveyitems();
				for(InquirySubsurveyitem item:items){
					content.append(item.getContent());
				}
				
				indexInfo.setContent(content.toString());
				
				indexInfo.setEntityID(basic.getId());
				indexInfo.setAppType(ApplicationCategoryEnum.inquiry);
				indexInfo.setCreateDate(new Date(basic.getSendDate().getTime()));//目前设定的是发布日期，此处存疑
				indexInfo.setAuthor(member.getName());
				
				List<String> ownerList=new ArrayList<String>();
				List<String> departmentList=new ArrayList<String>();
				List<String> accountList=new ArrayList<String>();
				List<String> postList=new ArrayList<String>();
				
				for(InquiryScope inquiryScope : basic.getInquiryScopes()){
					if("Member".equals(inquiryScope.getScopeDesc())){
						ownerList.add(inquiryScope.getScopeId().toString());
						}else if("Department".equals(inquiryScope.getScopeDesc())){
							Long departmentId=inquiryScope.getScopeId();
							try {
								//如果有子部门则加入它
								List<V3xOrgDepartment> departments=orgManager.getChildDepartments(departmentId, false);
								if(departments!=null){
									for(V3xOrgDepartment department:departments){
										departmentList.add(department.getId().toString());
									}
								}
							} catch (Exception e) {
								log.error("获取子部门人员出错", e);
							}
							
							departmentList.add(departmentId.toString());
						}else if("Account".equals(inquiryScope.getScopeDesc())){
                            //判断是否是集团
							V3xOrgAccount account = orgManager.getAccountById(inquiryScope.getScopeId());
							if(account != null && account.getIsRoot())
								ownerList.add("ALL");
							else
								accountList.add(inquiryScope.getScopeId().toString());
						}else if("Post".equals(inquiryScope.getScopeDesc())){
							postList.add(inquiryScope.getScopeId().toString());
						}
					
				}
				
				AuthorizationInfo authorizationInfo=new AuthorizationInfo();
				authorizationInfo.setOwner(ownerList);
				authorizationInfo.setDepartment(departmentList);
				authorizationInfo.setAccount(accountList);
				authorizationInfo.setPost(postList);
				indexInfo.setAuthorizationInfo(authorizationInfo);
				IndexUtil.convertToAccessory(indexInfo);
				
				indexManager.index(indexInfo);
			} catch (Exception e) {
				log.error("调查加入全文检索时出错", e);
			}
				
			
		}
		
		return true;
	}

	/**
	 * 发送消息
	 * 
	 * @param member
	 * @param basic
	 * @param receiverIds
	 * @throws MessageException
	 */
	public void sendMessage(User member, InquirySurveybasic basic,
			List<Long> receiverIds) throws MessageException {
		userMessageManager.sendSystemMessage(MessageContent.get("inquiry.send",
				member.getName(), basic.getSurveyName()),
				ApplicationCategoryEnum.inquiry, member.getId(),
				MessageReceiver.get(basic.getId(), receiverIds,
						"message.link.inquiry_send", String.valueOf(basic
								.getId()), String.valueOf(basic.getSurveyTypeId())),basic.getSurveyTypeId());
	}

	/**
	 * 消息提醒
	 * 
	 * @param basic
	 * @throws BusinessException 
	 */
	public void remindMessage(InquirySurveybasic basic) throws BusinessException {
		try {
			
			Scheduler sched = QuartzListener.getScheduler();
			Timestamp createTime = basic.getSendDate();
			V3xOrgMember sender = orgManager.getMemberById(basic.getCreaterId());
			String senderName = sender.getName();
//			构造消息提醒接受者的id串
			Set<InquiryScope> scopeset = basic.getInquiryScopes();
			String ids = "";
			for (InquiryScope scope : scopeset) {// 发布对象
				
				long egid = scope.getScopeId();
				String desc = scope.getScopeDesc();
				V3xOrgEntity entity =orgManager.getEntity(desc, egid);
				String orgdesc = entity.getEntityType();
				
				if (orgdesc.equals(V3xOrgEntity.ORGENT_TYPE_MEMBER)) {
					ids+=entity.getId()+",";

				} else if (orgdesc.equals(V3xOrgEntity.ORGENT_TYPE_DEPARTMENT)) {
					V3xOrgDepartment dept = orgManager.getDepartmentById(entity.getId());
					List<V3xOrgMember> v3xMermberList = orgManager.getMembersByDepartment(entity.getId(), false,dept.getOrgAccountId());
					for (V3xOrgMember member2 : v3xMermberList) {
						ids+=member2.getId()+",";
					}
				} else if (orgdesc.equals(V3xOrgEntity.ORGENT_TYPE_POST)) {
					List<V3xOrgMember> v3xMermberList = orgManager
							.getMembersByPost(entity.getId());
					for (V3xOrgMember member3 : v3xMermberList) {
						ids+=member3.getId()+",";
					}
				} else if (orgdesc.equals(V3xOrgEntity.ORGENT_TYPE_LEVEL)) {
					List<V3xOrgMember> v3xMermberList = orgManager
							.getMembersByLevel(entity.getId());
					for (V3xOrgMember member4 : v3xMermberList) {
						ids+=member4.getId()+",";
					}
				}else if (orgdesc.equals(V3xOrgEntity.ORGENT_TYPE_ACCOUNT)) {
					List<V3xOrgMember> v3xMermberList = orgManager.getAllMembers(egid);
					for (V3xOrgMember member5 : v3xMermberList) {
						ids+=member5.getId()+",";
					}
				}
			}

			Long advanceRemindTime = createTime.getTime();
			
			if (createTime.getTime() < (new Timestamp(System.currentTimeMillis())).getTime() && basic.getCloseDate().getTime() > (new Timestamp(System.currentTimeMillis())).getTime()) {
				advanceRemindTime = (new Timestamp(System.currentTimeMillis())).getTime() + Long.parseLong("1") * 6000;
			}
			if (basic.getCloseDate().getTime() < (new Timestamp(System.currentTimeMillis())).getTime()) {
				return;
			}
			Date runTime = new Date(advanceRemindTime);
			Long jobId = UUIDLong.longUUID();
			String jobName = jobId.toString();
			Long groupId = UUIDLong.longUUID();
			String groupName = groupId.toString();
			Long triggerId = UUIDLong.longUUID();
			String triggerName = triggerId.toString();
			SimpleTrigger trigger = new SimpleTrigger(triggerName , groupName , runTime);
			JobDataMap datamap = new JobDataMap();
			Long basicID = basic.getId();
			datamap.putAsString("basicID", basicID.longValue());
			datamap.put("remindIds", ids);
			datamap.put("sender", senderName);
			JobDetail job = new JobDetail(jobName, groupName , InquiryRemind.class);
			job.setJobDataMap(datamap);
			sched.scheduleJob(job, trigger);

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 发送消息
	 */
	public void sendMessage(InquirySurveybasic basic) throws BusinessException {
		// 构造消息提醒接受者
		Set<Long> receiverIds = new HashSet<Long>();
		InquirySurveytype type = null;
		try {
			type = this.getSurveyTypeById(basic.getSurveyTypeId());
		} catch (Exception e) {
			log.error("", e);
		}

		Set<InquiryScope> scopeset = basic.getInquiryScopes();
		List<Long> scopeList = new ArrayList<Long>();
		// 调查发布范围
		for (InquiryScope scope : scopeset) {
			Set<V3xOrgMember> v3xMermberList = this.orgManager.getMembersByType(scope.getScopeDesc(), scope.getScopeId());
			List<Long> ids = CommonTools.getEntityIds(v3xMermberList);
			if (CollectionUtils.isNotEmpty(ids)) {
				scopeList.addAll(ids);
			}
		}

		// 过滤掉非空间人员
		List<V3xOrgMember> customMembers = null;
		if (type != null) {
			if (type.getSpaceType() == 4) {// 获取自定义团队空间人员
				customMembers = spaceManager.getSpaceMemberBySecurity(basic.getSurveyTypeId(), -1);
			} else if (type.getSpaceType() == 5 || type.getSpaceType() == 6) {// 获取自定义单位、集团空间人员
				customMembers = spaceManager.getSpaceMemberBySecurity(type.getAccountId(), -1);
			}
		}
		if (CollectionUtils.isNotEmpty(customMembers)) {
			List<Long> customList = CommonTools.getEntityIds(customMembers);
			scopeList = CommonTools.getIntersection(scopeList, customList);
		}

		if (CollectionUtils.isNotEmpty(scopeList)) {
			for (Long memberId : scopeList) {
				receiverIds.add(memberId);
			}
		}

		if (type != null && type.getSpaceType() == 4) {// 加入自定义团队空间管理员
			List<V3xOrgMember> managers = spaceManager.getSpaceMemberBySecurity(basic.getSurveyTypeId(), 1);
			if (CollectionUtils.isNotEmpty(managers)) {
				for (V3xOrgMember member : managers) {
					receiverIds.add(member.getId());
				}
			}
		}
		Long memberId = basic.getCreaterId();
		V3xOrgMember member = orgManager.getMemberById(memberId);// 取创建者的名字
		// 发送提醒消息
		try {
			this.addAdmins2MsgReceivers(receiverIds, basic);
			userMessageManager.sendSystemMessage(MessageContent.get("inquiry.send", basic.getSurveyName(), member.getName()), ApplicationCategoryEnum.inquiry, basic.getCreaterId(), MessageReceiver.get(basic.getId(), receiverIds, "message.link.inq.alreadyauditing", String.valueOf(basic.getId())), basic.getSurveyTypeId());
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	/**
	 * 发送消息
	 */
	public void sendMessage(InquirySurveybasic basic, String content, String url) throws BusinessException {
			User user = CurrentUser.get();
			long senderId = user.getId();
			//构造消息提醒接受者
			Set<Long> receiverIds = new HashSet<Long>();
			Set<InquiryScope> scopeset = basic.getInquiryScopes();
			
			for (InquiryScope scope : scopeset) {// 发布对象
				Set<V3xOrgMember> v3xMermberList = this.orgManager.getMembersByType(scope.getScopeDesc(), scope.getScopeId());
				for (V3xOrgMember member2 : v3xMermberList) {
					if(member2!=null && member2.isValid()) {
						receiverIds.add(member2.getId());
					}
				}
				
			}
			Long memberId = basic.getCreaterId();
			//V3xOrgMember member = orgManager.getMemberById(memberId);//取创建者的名字
			//当发起者是板块管理员的时候，不给发起者发送消息
			if(senderId != memberId)
				receiverIds.add(memberId);
			else
				receiverIds.remove(memberId);
			//发送提醒消息
			try{
				userMessageManager.sendSystemMessage(MessageContent.get(content, basic.getSurveyName(), user.getName()),
												ApplicationCategoryEnum.inquiry, 
												senderId, 
												MessageReceiver.get(basic.getId(), receiverIds, url,String.valueOf(basic.getId())),basic.getSurveyTypeId());
			}catch(Exception e){
				log.error("", e);
			}
			
	}
	/**
	 * 发布调查时，发送消息对象中加入当前调查板块的管理员
	 * @param receivers   发布范围内的消息接受对象
	 * @param basic	  	  所发布的调查所在的调查板块
	 */
	private void addAdmins2MsgReceivers(Collection<Long> receivers, InquirySurveybasic basic) throws Exception {
		InquirySurveytype type = this.getSurveyTypeById(basic.getSurveyTypeId());
		Set<InquirySurveytypeextend> inquirySurveytypeextends = type.getInquirySurveytypeextends();
		if(inquirySurveytypeextends!=null && inquirySurveytypeextends.size()>0) {
			for(InquirySurveytypeextend admin : inquirySurveytypeextends) {
				if(admin.getManagerDesc().equals(InquirySurveytypeextend.MANAGER_SYSTEM) && !receivers.contains(admin.getManagerId())) {
					receivers.add(admin.getManagerId());
				}
			}
		}
	}
	
	/**
	 * 结束调查
	 */
	public void finishSurvey(InquirySurveybasic basic) throws BusinessException {
		try {
			Scheduler sched = QuartzListener.getScheduler();
			Date runTime = new Date(basic.getCloseDate().getTime());
			Long jobId = UUIDLong.longUUID();
			String jobName = jobId.toString();
			Long groupId = UUIDLong.longUUID();
			String groupName = groupId.toString();
			Long triggerId = UUIDLong.longUUID();
			String triggerName = triggerId.toString();
			SimpleTrigger trigger = new SimpleTrigger(triggerName, groupName, runTime);
			JobDataMap datamap = new JobDataMap();
			Long basicID = basic.getId();
			datamap.putAsString("basicID", basicID.longValue());
			JobDetail job = new JobDetail(jobName, groupName,FinishInquiry.class);
			job.setJobDataMap(datamap);
			sched.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取当前用户在当前调查类型下发布的调查列表
	 * 
	 * @param tid
	 *            调查类型ID
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getSendBasicListByCreator(long tid)
			throws Exception {
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		List<InquirySurveybasic> sendlist = inquiryBasicDao
				.getSendBasicListByCreator(tid, memberid);
		for (InquirySurveybasic ibasic : sendlist) {
			bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
		}
		return bscomposelist;
	}

	/**
	 * 获取当前用户在当前调查类型下未发布的调查列表
	 * 
	 * @param tid
	 *            调查类型ID
	 * @return
	 * @throws Exception
	 * 
	 */
	public List<SurveyBasicCompose> getNOSendBasicListByCreator(long tid,String condition, String textfield, String textfield1)
			throws Exception {
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		List<InquirySurveybasic> nosendlist = inquiryBasicDao.getNOSendBasicListByCreator(tid, memberid , condition, textfield, textfield1 );
		InquirySurveytype isType = this.getSurveyTypeById(tid);
		List<InquirySurveybasic> noCensorDes = new ArrayList<InquirySurveybasic>();
		if (isType.getCensorDesc().equals(1)){
			for (InquirySurveybasic ibasic : nosendlist) {
				if(InquirySurveybasic.CENSOR_NO.equals(ibasic.getCensor())){
					ibasic.setCensor(InquirySurveybasic.CENSOR_DRAUGHT);
				}
				noCensorDes.add(ibasic);
			}
			for (InquirySurveybasic ibasic : noCensorDes) {
				bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
			}
		}else if (isType.getCensorDesc().equals(0)){
			// for (InquirySurveybasic ibasic : nosendlist) {
			// if(InquirySurveybasic.CENSOR_DRAUGHT.equals(ibasic.getCensor())){
			// ibasic.setCensor(InquirySurveybasic.CENSOR_NO);
			// }
			// noCensorDes.add(ibasic);
			//			}
			for (InquirySurveybasic ibasic : nosendlist) {
				bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
			}
		}
		return bscomposelist;
	}

	/**
	 * 选择删除当前用户在当前调查类型下未发布的调查列表
	 * 
	 * @param tid
	 * @param bid
	 * @throws Exception
	 */
	public void removeNoSendBasicByCreator(long tid, String[] bid)
			throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		for (int i = 0; i < bid.length; i++) {
			InquirySurveybasic basic = inquiryBasicDao.getNOSendBasicByCreator(
					tid, memberid, Long.parseLong(bid[i]));
			if (basic == null) {
				continue;
			}
			inquiryBasicDao.remove(basic);
		}
	}
	
	/**
	 * 删除个人调查模板
	 * @param tid
	 * @throws Exception
	 */
	public void removeTemplate(String[] tid)throws Exception{
		for (int i = 0; i < tid.length; i++) {
			InquirySurveybasic basic = inquiryBasicDao.getInquirySurveybasicID(Long.parseLong(tid[i]),true);
			if (basic == null) {
				continue;
			}
			inquiryBasicDao.remove(basic);
		}
	}

	/**
	 * 选择终止当前用户发布的调查类型
	 * 
	 * @param tid
	 * @param bid
	 * @throws Exception
	 */
	public void closeSendBasicByCreator(String bid) throws Exception {
		inquiryBasicDao.updateBasicClose(Long.parseLong(bid));

	}

	/**
	 * 根据用户输入内容查找调查
	 * 
	 * @param oid
	 *            查询选项
	 * @param content
	 *            查询内容
	 * @param typeid
	 *            调查版块ID
	 * @param map
	 *            查询页面状态
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getBasicByUserQuery(String oid,
			String[] content, String typeid, Map<String, String> map , boolean isOtherAccount )
			throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		boolean isInternal = member.isInternal();
		String authID = "";
		if(isInternal){
			authID = orgManager.getUserIDDomain(memberid , "Account" , "Department", "Team", "Member", "Post", "Level");
		}else{
			authID = orgManager.getUserIDDomain(memberid , "Department", "Team", "Member", "Post", "Level");
		}
		List<InquirySurveybasic> blist = null;
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		long tid = Long.parseLong(typeid);
		if (oid.equals("subject")) {// 按标题查找调查 content[0]
			switch (this.setIntValues(map)) {
			case 1: // 查看列表查询
				blist = this.getBasicByUserQueryAndTitle(authID, tid,
						content[0] , isOtherAccount);
				break;
			case 2: // 未发布
				blist = this.getBasicByCreatorAndTitle(content[0], typeid,
						memberid, false);
				break;
			}
		} else if (oid.equals("importantLevel")) {// 按发布者查找 只有查看列表才有该操作
				blist = this.getBasicByUserQueryAndSendUser(authID, tid, Long
						.parseLong(content[1]));
		} else if (oid.equals("createDate")) {// 按发布日期
			String content_one = content[2] + " 00:00:00";
			Date odate = Datetimes.parseDatetime(content_one);
			Timestamp date_one = new Timestamp(odate.getTime());
			String content_two = content[2] + " 23:59:59";
			Date tdate = Datetimes.parseDatetime(content_two);
			Timestamp date_two = new Timestamp(tdate.getTime());
			switch (this.setIntValues(map)) {
			case 1: // 列表查询
				blist = this.getBasicByUserQueryAndSendTime(authID, tid,
						date_one, date_two);
				break;
			case 2: // 未发布
				blist = this.getBasicByCreatorAndSendTime(date_one, typeid,
						memberid, false);
				break;
  		}
		}
		if(blist!=null && blist.size()>0) {
			for (InquirySurveybasic surveybasic : blist) {
				bscomposelist = this.getSurveyBasicInterior(bscomposelist,
						surveybasic);
			}
		}
		return bscomposelist;
	}
	/**
	 * 管理入口查询调查
	 * @param content
	 * @param typeid
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getAdminQuery(String oid,String[] content, String typeid) throws Exception{
		List<InquirySurveybasic> blist = null;
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		long tid = Long.parseLong(typeid);
		if (oid.equals("subject")) {// 按标题查找调查 content[0]
				blist = this.getBasicByManagerQueryAndTitle(tid, content[0]);
		} else if (oid.equals("importantLevel")) {// 按发布者查找
				blist = this.getBasicByManageQueryAndSendUser(tid, Long
						.parseLong(content[1]));
		} else if (oid.equals("createDate")) {// 按发布日期
			String content_one = content[2] + " 00:00:00";
			Date odate = Datetimes.parseDatetime(content_one);
			Timestamp date_one = new Timestamp(odate.getTime());
			String content_two = content[2] + " 23:59:59";
			Date tdate = Datetimes.parseDatetime(content_two);
			Timestamp date_two = new Timestamp(tdate.getTime());
				blist =  this.getBasicByManagQueryAndSendTime(tid, date_one,
						date_two);
		}
		if(blist!=null && blist.size()>0) {
			for (InquirySurveybasic surveybasic : blist) {
				bscomposelist = this.getSurveyBasicInterior(bscomposelist,
						surveybasic);
			}
		}
		return bscomposelist;
	}

	/**
	 * 判定查询操作类型
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	private int setIntValues(Map<String, String> map) throws Exception {
		try {
			int value = 1; // 普通操作
			String publicBasic = map.get("basic_list");
			String nopublicBasic = map.get("nopublicBasic");
			if (publicBasic != null && !"".equals(publicBasic)) {
				value = 1; // 查看列表状态下查询
			} else if (nopublicBasic != null && !"".equals(nopublicBasic)) {
				value = 2; // 未发布状态下查询
			}
			return value;
		} catch (Exception e) {
			return 1;
		}
	}

	/**
	 * 按标题查询当前用户创建调查
	 * 
	 * @param titles
	 * @param typeid
	 * @param memberid
	 * @param b:ture:为发布状态的，flase为未发布状态的
	 * @return
	 * @throws Exception
	 */
	private List<InquirySurveybasic> getBasicByCreatorAndTitle(String title,
			String typeid, long memberid, boolean b) throws Exception {
		return inquiryBasicDao.getBasicByCreatorAndTitle(title, typeid,
				memberid, b);
	}
	
	public List<InquirySurveybasic> getInquirySurveyByTypeId(Long typeId) {
		return inquiryBasicDao.getInquirySurveybasicByTypeId(typeId);
	}

	/**
	 * 按发布时间查询当前用户创建调查
	 * 
	 * @param date_one：开始时间
	 * @param date_two：结束时间
	 * @param typeid
	 * @param blist
	 * @param memberid
	 * @param b
	 *            :ture:为发布状态的，flase为未发布状态的
	 * @return
	 * @throws Exception
	 */
	private List<InquirySurveybasic> getBasicByCreatorAndSendTime(
			Timestamp date_one, String typeid, long memberid, boolean b)
			throws Exception {
		return inquiryBasicDao.getBasicByCreatorAndSendTime(date_one, typeid,
				memberid, b);
	}

	/**
	 * 按标题查询当前用户可见调查(普通操作)
	 * 
	 * @param title
	 * @param typeid
	 * @param memberid
	 * @param isOtherAccount 判断是否为外单位
	 * @return
	 * @throws Exception
	 */
	private List<InquirySurveybasic> getBasicByUserQueryAndTitle(String authID,
			long tid, String title , boolean isOtherAccount) throws Exception {
		return inquiryBasicDao.getUserQuerySurveyByTitle(authID, tid, title , isOtherAccount );
	}

	/**
	 * 按标题查询当前用户可见调查(管理操作)
	 * 
	 * @param title
	 * @param typeid
	 * @param memberid
	 * @return
	 * @throws Exception
	 */
	private List<InquirySurveybasic> getBasicByManagerQueryAndTitle(long tid,
			String title) throws Exception {
		return inquiryBasicDao.getManagerQuerySurveyByTitle(tid, title);
	}

	/**
	 * 按发布时间查询当前用户可见调查(普通操作)
	 * 
	 * @param tid
	 * @param title
	 * @return
	 * @throws Exception
	 */
	private List<InquirySurveybasic> getBasicByUserQueryAndSendTime(
			String authID, long tid, Timestamp date_one, Timestamp date_two)
			throws Exception {
		return inquiryBasicDao.getUserQuerySurveyBySendDate(authID, tid,
				date_one, date_two);
	}

	/**
	 * 按发布时间查询当前用户可见调查(管理操作)
	 * 
	 * @param tid
	 * @param title
	 * @return
	 * @throws Exception
	 */
	private List<InquirySurveybasic> getBasicByManagQueryAndSendTime(long tid,
			Timestamp date_one, Timestamp date_two) throws Exception {
		return inquiryBasicDao.getManagerQuerySurveyBySendDate(tid, date_one,
				date_two);
	}

	/**
	 * 按发布人查询当前用户可见调查（普通操作）
	 * 
	 * @param authID
	 * @param tid
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	private List<InquirySurveybasic> getBasicByUserQueryAndSendUser(
			String authID, long tid, long uid) throws Exception {
		return inquiryBasicDao.getUserQuerySurveyByCreator(authID, tid, uid);
	}

	/**
	 * 按发布人查询当前用户可见调查（管理操作）
	 * 
	 * @param authID
	 * @param tid
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	private List<InquirySurveybasic> getBasicByManageQueryAndSendUser(long tid,
			long uid) throws Exception {
		return inquiryBasicDao.getManagerQuerySurveyByCreator(tid, uid);
	}

	/**
	 * 用户投票操作 true 投票成功 false :失败
	 * @param bid
	 */
	public boolean getUserVoteBasic(long bid) throws Exception {
		return inquiryBasicDao.canUserHandleTheInquiry(CurrentUser.get().getId(), bid);
	}

	/**
	 * 保存投票结果
	 * 
	 * @param sbid
	 * @return
	 * @throws Exception
	 */
	public void updateBasicAndVote(long bid, List<Object> objlist,
			List<String> alist) throws Exception {
		if (alist.size() < 0) {
			return;
		} else {
			for (Object obj : objlist) {
				inquiryBasicDao.save(obj);
			}
			for (String string : alist) {
				long itemID = Long.parseLong(string);
				inquiryBasicDao.updateInquiryItem(itemID);
			}
			inquiryBasicDao.updateInquiryBasicByVote(bid);
		}
	}

	/**
	 * 根据调查ID和问题ID获取评论列表
	 * 
	 * @param bid
	 * @param pid
	 * @return
	 * @throws Exception
	 */
	public List<DiscussAndUserCompose> getDiscussList(long bid, long pid)
			throws Exception {
		List<InquirySurveydiscuss> list = null;
		List<InquirySurveydiscuss> list1 = inquiryDao.getDiscussListNotPage(pid, bid);
		
		list = this.pagenate(list1);//分页
		
		List<DiscussAndUserCompose> dlist = new ArrayList<DiscussAndUserCompose>();
		if(list!=null && list.size()>0) {
			for (InquirySurveydiscuss surveydiscuss : list) {
				DiscussAndUserCompose duser = new DiscussAndUserCompose();
				duser.setDcs(surveydiscuss);
				V3xOrgMember sender = this.orgManager.getEntityById(
						V3xOrgMember.class, surveydiscuss.getUserId());// 获取发布者姓名
				duser.setUser(sender);
				dlist.add(duser);
			}
		}
		Collections.sort(list);
		return dlist;
	}
//分页
	private <T> List<T> pagenate(List<T> list) {
		if (null == list || list.size() == 0)
			return new ArrayList<T>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		log.debug("first: " + first + ", pageSize: " + pageSize + ", size: "
				+ list.size());
		List<T> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}

	
	/**
	 * 根据ID删除评论
	 * 
	 * @param did
	 * @return
	 * @throws Exception
	 */
	public void removeDiscuss(long did) throws Exception {
		inquiryBasicDao.removeDiscuss(did);
	}

	/**
	 * 合并调查项
	 * 
	 * @param items
	 * @param newItem
	 * @throws Exception
	 */
	public void saveMergeInquiry(String[] items, String newItem, String bid)
			throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		InquirySurveybasic basic = inquiryBasicDao
				.getInquirySurveybasicByCrestorAndID(memberid, Long
						.parseLong(bid));
		if (items.length < 2 || basic == null) {
			return;
		} else {
			String subID = "";
			for (String its : items) {// 判断用户选择的合并项是否为一个问题下的
				String[] ids = its.split(",");
				if (!"".equals(subID) && !subID.equals(ids[0])) {
					return;
				}
				subID = ids[0];
			}
			int count = 0;
			int sort = 0;
			for (String its : items) {
				// items的格式为"123456|987654" 前面数字表示问题ID 后面数字表示合并项ID
				String[] ids = its.split(",");
				subID = ids[0];
				String string = ids[1];
				String hql = "From " + InquirySubsurveyitem.class.getName()
						+ " i Where i.id=?";
				InquirySubsurveyitem it = (InquirySubsurveyitem) inquiryDao
						.find(hql, Long.parseLong(string)).get(0);
				count += it.getVoteCount().intValue();
				if (sort == 0) {// 取合并条目第一个排序号
					sort = it.getSort().intValue();
				}
				inquiryDao.remove(it);
			}
			InquirySubsurveyitem item = new InquirySubsurveyitem();
			item.setIdIfNew();
			item.setContent(newItem);
			item.setInquirySurveybasic(basic);
			item.setSort(sort);
			item.setVoteCount(count);
			item.setSubsurveyId(Long.parseLong(subID));
			item.setOtherOption(0);
			inquiryDao.save(item);
		}
	}

	/**
	 * 获取调查模板列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<InquirySurveybasic> getTemplateList() throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		// String hql = "From " + InquirySurveybasic.class.getName()
		// + " b Where b.flag=" + InquirySurveybasic.FLAG_TEM.intValue();
		return inquiryBasicDao.getTemplateList(memberid);
	}
	
	/**
	 * 获取调查模板列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<InquirySurveybasic> getSpaceTemplateList(String spaceType) throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		return inquiryBasicDao.getSpaceTemplateList(memberid, spaceType);
	}
	
	/**
	 * 获取调查模板列表
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<InquirySurveybasic> getAccOrGroupTemplateList( String group) throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		return inquiryBasicDao.getAccOrGroupTemplateList(memberid,group);
	}

	/**
	 * 获取调查模板
	 * 
	 * @return
	 * @throws Exception
	 */
	public SurveyBasicCompose getTemplateListByID(long id , boolean getTemp) throws Exception {
		InquirySurveybasic ibasic = inquiryBasicDao.getInquirySurveybasicID(id,getTemp);
		Set<InquiryScope> scopeset = ibasic.getInquiryScopes();
		Set<InquirySubsurvey> subsurveySet = ibasic.getInquirySubsurveys();
		Set<InquirySubsurveyitem> itemSet = ibasic.getInquirySubsurveyitems();
		SurveyBasicCompose sbcompose = this.surveyBasicComposeViewObject(
				ibasic, scopeset, subsurveySet, itemSet);

		return sbcompose;
	}


	/**
	 * 将InquirySurveytype拼成一个VO 根据isNeedCount判断是否需要计算调查版块下的调查总数
	 * 
	 * @param comlist
	 * @param surveytypes
	 * @return
	 * @throws Exception
	 */
	private List<SurveyTypeCompose> getSurveyTypeCompose( List<SurveyTypeCompose> comlist, List<InquirySurveytype> surveytypes, boolean isNeedCount)
			throws Exception {
		
		for (InquirySurveytype surveytype : surveytypes) {
			SurveyTypeCompose stCompose = new SurveyTypeCompose();
			
			int count = getCountByType(surveytype.getId());
			
			stCompose.setCount(count);
			
			stCompose.setInquirySurveytype(surveytype);
			
			ArrayList<V3xOrgMember> manager = new ArrayList<V3xOrgMember>();
			
			Set<InquirySurveytypeextend> surveytypeextends = surveytype.getInquirySurveytypeextends();
			// 保持用户设定管理员的顺序 added by Meng Yang at 2009-06-29
			List<InquirySurveytypeextend> sortedList = new ArrayList<InquirySurveytypeextend>();
			// 新的系统刚开始时初始化的几个调查版块没有管理员和审核员，会导致以下的添加方法抛空指针异常，需防护
			if(surveytypeextends!=null && surveytypeextends.size()>0) {
				sortedList.addAll(surveytypeextends);
				Collections.sort(sortedList);
			}				

			if(sortedList!=null && sortedList.size()>0) {
				for (InquirySurveytypeextend surveytypeextend : sortedList) {
					V3xOrgMember member = this.orgManager.getEntityById(V3xOrgMember.class, surveytypeextend.getManagerId());// 获取当前用户对象
					if (surveytypeextend.getManagerDesc().intValue() == InquirySurveytypeextend.MANAGER_SYSTEM.intValue()) {// 管理员
						manager.add(member);
					} else if (surveytypeextend.getManagerDesc().intValue() == InquirySurveytypeextend.MANAGER_CHECK.intValue()) {// 审核人员
						stCompose.setChecker(member);
					}
				}
			}
			
			stCompose.setManagers(manager);
			comlist.add(stCompose);
			
		}
		return comlist;

	}

	/**
	 * 根据ID获取调查
	 * 
	 * @param basicid
	 * @return
	 * @throws Exception
	 */
	public SurveyBasicCompose getInquiryBasicByBasicID(long basicid)
			throws Exception {
		
		InquirySurveybasic ibasic = inquiryBasicDao.getInquirySurveybasicID(basicid,false);
		if(ibasic==null)
			return null;
		ibasic.setInquirySurveytype(allSurveyTypeMap.get(ibasic.getSurveyTypeId()));
		
		Set<InquiryScope> scopeset = ibasic.getInquiryScopes();
		Set<InquirySubsurvey> subsurveySet = ibasic.getInquirySubsurveys();
		Set<InquirySubsurveyitem> itemSet = ibasic.getInquirySubsurveyitems();
		SurveyBasicCompose sbcompose = this.surveyBasicComposeViewObject( ibasic, scopeset, subsurveySet, itemSet);
		return sbcompose;
	}

	/**
	 * 根据ID获取调查
	 * 
	 * @param basicid
	 * @return
	 * @throws Exception
	 */
	public InquirySurveybasic getBasicByID(long basicid) throws Exception {
		
		InquirySurveybasic ibasic = inquiryBasicDao.getInquirySurveybasicID(basicid,false);
		if(ibasic!=null)
			ibasic.setInquirySurveytype(allSurveyTypeMap.get(ibasic.getSurveyTypeId()));
		
		return ibasic;
		
	}

	/**
	 * 获取当前调查版块的调查列表
	 * 
	 * @param tid
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getCheckListByType(long tid)
			throws Exception {
		List<InquirySurveybasic> basiclist = inquiryBasicDao.getCheckListByType(tid);
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		for (InquirySurveybasic ibasic : basiclist) {
			bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
		}
		return bscomposelist;

	}

	/**
	 * 判断是否有同名的模板调查
	 * 
	 * @param tid
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public boolean isTheSameName(String name) throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		List<InquirySurveybasic> basiclist = inquiryBasicDao.isTheSameName(
				name, memberid);
		if (basiclist == null || basiclist.size() == 0) {
			return true;
		}
		return false;
	}
	/**
	 * 是否有同名调查
	 */
	public boolean isInquiryExist(String name,Long typeId) throws Exception {
		boolean b = false;
		List<InquirySurveybasic> basiclist = inquiryBasicDao.isInquiryExist(name, typeId);
		if (basiclist == null || basiclist.size() == 0) {
			return true;
		}
		return b;
	}

	/**
	 * 归档调查
	 * 
	 * @param inquirytype
	 * @return
	 */
	public void pigeonholeInquiry(String values) throws Exception {
		String[] thisID = values.split(",");
		for (String string : thisID) {
			inquiryBasicDao.pigeonholeInquiry(Long.parseLong(string));
			indexManager.deleteFromIndex(ApplicationCategoryEnum.inquiry, Long.parseLong(string));
		}
	}

	/**
	 * 获取当前用户有管理权限的调查版块
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SurveyTypeCompose> getAuthoritiesTypeList() throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
//		List<InquirySurveytype> surveytypes = inquiryTypeDao.getAuthoritiesTypeList(memberid);
		
		List<InquirySurveytype> surveytypes = new ArrayList<InquirySurveytype>();
		Set<Long> canManageSurveyTypes = canManageSurveyTypeMap.get(memberid);
		
		if(canManageSurveyTypes!=null && canManageSurveyTypes.size()>0){
			
			for(Long typeId : canManageSurveyTypes){
				InquirySurveytype surveyType = allSurveyTypeMap.get(typeId);
				if(surveyType.getAccountId().longValue()==member.getLoginAccount()){
					surveytypes.add(surveyType);
				}
			}
		}
		
		List<SurveyTypeCompose> comlist =  new ArrayList<SurveyTypeCompose>();
		comlist = this.getSurveyTypeCompose(comlist, surveytypes, true);
		return comlist;
	}
	/**
	 * 自定义空间获取当前用户有管理权限的调查版块
	 * @param spaceId
	 * @return
	 * @throws Exception
	 */
	public List<SurveyTypeCompose> getCustomAuthoritiesTypeList(long spaceId) throws Exception {
		User member = CurrentUser.get();
		long memberid = member.getId();
		List<InquirySurveytype> surveytypes = new ArrayList<InquirySurveytype>();
		Set<Long> canManageSurveyTypes = canManageSurveyTypeMap.get(memberid);
		if(canManageSurveyTypes!=null && canManageSurveyTypes.size()>0){
			for(Long typeId : canManageSurveyTypes){
				InquirySurveytype surveyType = allSurveyTypeMap.get(typeId);
				if(surveyType.getAccountId().longValue() == spaceId){
					surveytypes.add(surveyType);
				}
			}
		}
		List<SurveyTypeCompose> comlist =  new ArrayList<SurveyTypeCompose>();
		comlist = this.getSurveyTypeCompose(comlist, surveytypes, true);
		return comlist;
	}
	
	/**
	 * 获取当前用户有管理权限的集团调查版块
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<SurveyTypeCompose> getAuthoritiesGroupTypeList() throws Exception {
		
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		
//		List<InquirySurveytype> surveytypes = inquiryTypeDao.getAuthoritiesGroupTypeList(memberid);
		
		List<InquirySurveytype> surveytypes = new ArrayList<InquirySurveytype>();
		Set<Long> canManageSurveyTypes = canManageSurveyTypeMap.get(memberid);
		
		if(canManageSurveyTypes!=null && canManageSurveyTypes.size()>0){
			
			for(Long typeId : canManageSurveyTypes){
				InquirySurveytype surveyType = allSurveyTypeMap.get(typeId);
				if(surveyType.getSpaceType().equals(InquirySurveytype.Space_Type_Group)){
					surveytypes.add(surveyType);
				}
			}
		}
		
		List<SurveyTypeCompose> comlist =  new ArrayList<SurveyTypeCompose>();
		comlist = this.getSurveyTypeCompose(comlist, surveytypes, true);
		return comlist;
		
	}
	//根据ID取得调查的可索引的数据
	public IndexInfo getIndexInfo(long id) throws Exception {
		InquirySurveybasic basic=getBasicByID(id);
		if(basic==null) return null;
		IndexInfo indexInfo=new IndexInfo();
		indexInfo.setTitle(basic.getSurveyName());
		indexInfo.setStartMemberId(basic.getIssuerId());
		indexInfo.setHasAttachment(basic.getAttachmentsFlag());
		indexInfo.setTypeId(basic.getSurveyTypeId());
		
		indexInfo.setContentType(IndexInfo.CONTENTTYPE_HTMLSTR);
		StringBuffer content=new StringBuffer();
		
		//调查发起部门
		V3xOrgDepartment dept = orgManager.getDepartmentById(basic.getDepartmentId());
		content.append(dept.getName() + " ");
		
		//调查描述
		if(basic.getSurveydesc() != null){
			content.append(basic.getSurveydesc() + " ");
		}
		
		//调查题目+调查题目描述
		Set<InquirySubsurvey> subsurveys = basic.getInquirySubsurveys();
		for(InquirySubsurvey subsurvey : subsurveys){
			content.append(subsurvey.getTitle() + " ");
			if(subsurvey.getSubsurveyDesc() != null){
				content.append(subsurvey.getSubsurveyDesc()  + " ");
			}
		}
		
		//调查题目评论
		Set<InquirySurveydiscuss> discusss = basic.getInquirySurveydiscusses();
		if(CollectionUtils.isNotEmpty(discusss)){
			for(InquirySurveydiscuss discuss : discusss){
				if(discuss.getDiscussContent() != null){
					content.append(discuss.getDiscussContent() + " ");
				}
			}
		}
		
		//调查选项
		Set<InquirySubsurveyitem> items = basic.getInquirySubsurveyitems();
		for(InquirySubsurveyitem item : items){
			content.append(item.getContent() + " ");
		}
		
		indexInfo.setEntityID(basic.getId());
		indexInfo.setAppType(ApplicationCategoryEnum.inquiry);
		indexInfo.setCreateDate(new Date(basic.getSendDate().getTime()));//目前设定的是发布日期，此处存疑
		V3xOrgMember member=orgManager.getMemberById(basic.getIssuerId());
		indexInfo.setAuthor(member.getName());
		
		List<String> ownerList=new ArrayList<String>();
		List<String> departmentList=new ArrayList<String>();
		List<String> accountList=new ArrayList<String>();
		List<String> postList=new ArrayList<String>();
		
		Set<InquiryScope> inquiryScopes=basic.getInquiryScopes();
		for(InquiryScope inquiryScope:inquiryScopes){
			if("Member".equals(inquiryScope.getScopeDesc())){
				ownerList.add(inquiryScope.getScopeId().toString());
				V3xOrgMember m = orgManager.getMemberById(inquiryScope.getScopeId());
				content.append(m.getName() + " ");
				}else if("Department".equals(inquiryScope.getScopeDesc())){
					Long departmentId=inquiryScope.getScopeId();
					try {
						//如果有子部门则加入它
						List<V3xOrgDepartment> departments=orgManager.getChildDepartments(departmentId, false);
						if(departments!=null){
							for(V3xOrgDepartment department:departments){
								departmentList.add(department.getId().toString());
								content.append(department.getName() + " ");
							}
						}
					} catch (Exception e) {
//						e.printStackTrace();
						log.error(e.getMessage(),e);
					}
					
					V3xOrgDepartment dept1 = orgManager.getDepartmentById(basic.getDepartmentId());
					content.append(dept1.getName() + " ");
					departmentList.add(departmentId.toString());
				}else if("Account".equals(inquiryScope.getScopeDesc())){
                    //判断是否是集团
					V3xOrgAccount account = orgManager.getAccountById(inquiryScope.getScopeId());
					content.append(account.getName() + " ");
					if(account != null && account.getIsRoot())
						ownerList.add("ALL");
					else
						accountList.add(inquiryScope.getScopeId().toString());
				}else if("Post".equals(inquiryScope.getScopeDesc())){
					V3xOrgPost post=orgManager.getPostById(inquiryScope.getScopeId());
					content.append(post.getName() + " ");
					postList.add(inquiryScope.getScopeId().toString());
				}
		}
		AuthorizationInfo authorizationInfo=new AuthorizationInfo();
		authorizationInfo.setOwner(ownerList);
		authorizationInfo.setDepartment(departmentList);
		authorizationInfo.setAccount(accountList);
		authorizationInfo.setPost(postList);
		indexInfo.setContent(content.toString());
		indexInfo.setAuthorizationInfo(authorizationInfo);
		IndexUtil.convertToAccessory(indexInfo);
		return indexInfo;
	}

	public List<InquirySurveytypeextend> getSerById(Long surveryId,int type) throws Exception {
		return inquiryBasicDao.getSerById(surveryId,type);
	}

	public List<InquiryAuthority> authorityList(long tid) throws Exception {
		List<InquiryAuthority> list = inquiryAuthDao.getAuthorityList(tid);
		if(list != null){
			return list; 
		}
		return null;
	}
	
	/**
	 * 判断当前用户是否有集团空间下的管理/审核权限
	 * @return
	 * @throws Exception
	 */
	public boolean hasManageAuthForGroupSpace() throws Exception {
		
//		int count = inquiryAuthDao.getCountOfGroupSpaceManage();
//		if(count>0){
//			return true;
//		}else{
//			return false;
//		}
		
		boolean result = false;
		User user = CurrentUser.get();
		if(user!=null) {//避免出现空指针异常 added by Meng Yang 2009-05-18
			Set<Long> canAuditTypes = canAuditSurveyTypeMap.get(user.getId());
			Set<Long> canManageTypes = canManageSurveyTypeMap.get(user.getId());
			
			if(canManageTypes!=null && canManageTypes.size()>0){
				for(Long typeId : canManageTypes){
					InquirySurveytype surveyType = allSurveyTypeMap.get(typeId);
					if(surveyType.getSpaceType().equals(InquirySurveytype.Space_Type_Group)){
						result = true;
						break;
					}
				}
			}
			
			if(canAuditTypes!=null && canAuditTypes.size()>0 && !result ){
				for(Long typeId : canAuditTypes){
					InquirySurveytype surveyType = allSurveyTypeMap.get(typeId);
					if(surveyType.getSpaceType().equals(InquirySurveytype.Space_Type_Group)){
						result = true;
						break;
					}
				}
			}
		}
			
		return result;
		
	}
	
	public boolean showManagerMenuOfCustomSpace(Long memberId, Long spaceId, int spaceType) throws Exception {
		boolean result = false;
		Set<Long> canManageTypes = canManageSurveyTypeMap.get(memberId);
		Set<Long> canAuditTypes = canAuditSurveyTypeMap.get(memberId);
		if (CollectionUtils.isNotEmpty(canManageTypes)) {
			for (Long typeId : canManageTypes) {
				InquirySurveytype surveyType = allSurveyTypeMap.get(typeId);
				if (surveyType.getSpaceType() == spaceType && surveyType.getAccountId().equals(spaceId)) {
					result = true;
					break;
				}
			}
		}
		if (CollectionUtils.isNotEmpty(canAuditTypes) && !result) {
			for (Long typeId : canAuditTypes) {
				InquirySurveytype surveyType = allSurveyTypeMap.get(typeId);
				if (surveyType.getSpaceType() == spaceType && surveyType.getAccountId().equals(spaceId)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * 判断单位空间下管理/审核权限   用于判断是否显示公共信息菜单   PublicInfoMenuCheckImpl调用
	 * @return
	 * @throws Exception
	 */
	public boolean hasManageAuthForAccountSpace(Long memberId) throws Exception {
		
		boolean result = false;
		
		Set<Long> canAuditTypes = canAuditSurveyTypeMap.get(memberId);
		Set<Long> canManageTypes = canManageSurveyTypeMap.get(memberId);
		
		if(canManageTypes!=null && canManageTypes.size()>0){
			for(Long typeId : canManageTypes){
				InquirySurveytype surveyType = allSurveyTypeMap.get(typeId);
				if(surveyType.getSpaceType().equals(InquirySurveytype.Space_Type_Account)){
					result = true;
					break;
				}
			}
		}
		
		if(canAuditTypes!=null && canAuditTypes.size()>0 && !result ){
			for(Long typeId : canAuditTypes){
				InquirySurveytype surveyType = allSurveyTypeMap.get(typeId);
				if(surveyType.getSpaceType().equals(InquirySurveytype.Space_Type_Account)){
					result = true;
					break;
				}
			}
		}
		
		return result;
		
	}
	
	/**
	 * 上述方法在跨单位兼职办公时失效，需加入对用户登录单位的判断
	 * @param memberId 当前用户
	 * @param accountId 登陆单位
	 * @return 是否为当前登陆单位的调查管理员/审核员，以便决定点击公共信息管理时调查管理是否亮显
	 * @throws Exception
	 * @author Meng Yang 2009-07-14
	 */
	public boolean hasManageAuthForAccountSpace(Long memberId, long accountId) throws Exception {
		boolean result = false;
		
		Set<Long> canAuditTypes = canAuditSurveyTypeMap.get(memberId);
		Set<Long> canManageTypes = canManageSurveyTypeMap.get(memberId);
		
		if(canManageTypes!=null && canManageTypes.size()>0){
			for(Long typeId : canManageTypes){
				InquirySurveytype surveyType = allSurveyTypeMap.get(typeId);
				//不仅要求存在单位调查板块，而且要求该板块所在单位与当前用户登录单位一致
				if(surveyType.getSpaceType().equals(InquirySurveytype.Space_Type_Account) && surveyType.getAccountId().longValue()==accountId ){
					result = true;
					break;
				}
			}
		}
		
		if(canAuditTypes!=null && canAuditTypes.size()>0 && !result ){
			for(Long typeId : canAuditTypes){
				InquirySurveytype surveyType = allSurveyTypeMap.get(typeId);
				if(surveyType.getSpaceType().equals(InquirySurveytype.Space_Type_Account) && surveyType.getAccountId().longValue()==accountId ){
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	

	/**
	 * 获取外单位调查
	 * @return
	 * @throws Exception
	 */
	public List<SurveyBasicCompose> getOtherAccountSurveyBasicList( String condition , String textfield , String textfield1 ) throws Exception{
		
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		
		boolean isInternal = member.isInternal();
		String authID = "";
		if(isInternal){
			authID = orgManager.getUserIDDomain(memberid , "Account" , "Department", "Team", "Member", "Post", "Level");
		}else{
			authID = orgManager.getUserIDDomain(memberid , "Department", "Team", "Member", "Post", "Level");
		}
		
		List<InquirySurveybasic> otherAccountSurveyList = inquiryBasicDao
		.getALLOtherAccountBasicList( authID , condition , textfield , textfield1 );
		
		List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
		for (InquirySurveybasic ibasic : otherAccountSurveyList) {
			bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
		}
		
		return bscomposelist;
	}
	
	/**
	 * 获取外单位调查总数
	 * @return
	 * @throws Exception
	 */
	public int getOtherAccountSurveyBasicCount() throws Exception{
		
		User member = CurrentUser.get();
		long memberid = member.getId();// 获取当前用户ID
		
		boolean isInternal = member.isInternal();
		String authID = "";
		if(isInternal){
			authID = orgManager.getUserIDDomain(memberid , "Account" , "Department", "Team", "Member", "Post", "Level");
		}else{
			authID = orgManager.getUserIDDomain(memberid , "Department", "Team", "Member", "Post", "Level");
		}
		
		int otherAccountSurveyCount = inquiryBasicDao
		.getALLOtherAccountBasicCount( authID );
		
		return otherAccountSurveyCount;
	}
	
	
	/**
	 * 判断是否有某板块的审核权限
	 * @param isGroup   区分集团与单位空间
	 * @return
	 */
	public boolean hasCheckAuth(boolean isGroup ){
//		int count = inquiryAuthDao.getCountOfCheckAuth(isGroup);
		boolean result = false;
		Set<Long> canAuditTypes = canAuditSurveyTypeMap.get(CurrentUser.get().getId());
		if(canAuditTypes!= null && canAuditTypes.size()>0){
			for(Long typeId : canAuditTypes){
				InquirySurveytype surveyType = allSurveyTypeMap.get(typeId);
				if(isGroup){
					if(surveyType.getSpaceType().equals(InquirySurveytype.Space_Type_Group)){
						result = true;
						break;
					}
				}else{
					if(surveyType.getSpaceType().equals(InquirySurveytype.Space_Type_Account)){
						result = true;
						break;
					}
				}
			}
		}
		
		return result;
		
	}
	
	/**
	 * 判断某板块下是否含有子调查
	 * @param typeId
	 * @return
	 */
	public boolean hasInquiryByTypeId(Long typeId){
		
		int count = inquiryAuthDao.getInquiryCountOfType(typeId);
		return count>0 ? true : false ;
		
	}
	
	public int getCountByType(Long typeId,String authIDs){
		return  inquiryAuthDao.getCountByType(typeId,authIDs);
	}
	
	public int getCountByType(Long typeId){
		return  inquiryAuthDao.getCountByType(typeId);
	}
	
	/**
	 * 人员删除时判断该人员是否有未审核的调查
	 * @param memberId
	 * @return
	 * @throws Exception
	 */
	public boolean hasInquiryNoCheck(Long memberId) throws Exception{
		int count = inquiryAuthDao.getInquiryNoCheckCountByMember(memberId);
		return count>0 ? true : false ;
	}
	
	/**
	 * 修改某板块审核员时判断该人员是否有未审核的调查
	 * @param memberId
	 * @return
	 * @throws Exception
	 */
	public boolean hasInquiryNoCheckByType(Long typeId) throws Exception{
		int count = inquiryAuthDao.getInquiryNoCheckCountByType(typeId);
		return count>0 ? true : false ;
	}
	
	public boolean isInquiryCheckerEnabled(Long typeId) throws Exception {
		List<InquirySurveytypeextend> listIds = this.getSerById(typeId,InquirySurveytypeextend.MANAGER_CHECK);
		if(listIds!=null&&listIds.size()>0){
			V3xOrgMember checker = orgManager.getMemberById(listIds.get(0).getManagerId());
			if(!checker.getEnabled()||checker.getIsDeleted()){
				return false;
			}
		}
		return true;
	}
	
	
//	获取审核员未审核调查数
	public int countCheckInquiryByMember(Long checkerId) throws Exception {
		return inquiryAuthDao.getInquiryNoCheckCountByMember(checkerId);
	}
	
	/**
	 * 综合查询
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public List<InquirySurveybasic> iSearch(ConditionModel cModel) throws Exception{
		String title = cModel.getTitle();
		final Date beginDate = cModel.getBeginDate();
		final Date endDate = cModel.getEndDate();
		final Long fromUserId = cModel.getFromUserId();
		final Long toUserId = cModel.getToUserId();
		
		User member = CurrentUser.get();
		String authID = "";
		
		if(member.isInternal()){
			authID = orgManager.getUserIDDomain(member.getId() , V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Account" , "Department", "Team", "Member", "Post", "Level");
		}else{
			authID = orgManager.getUserIDDomain(member.getId() , "Department", "Team", "Member", "Post", "Level");
		}
		
		List<Long> authIDList = CommonTools.parseStr2Ids(authID);
		
		Map<String, Object> namedParameterMap = new HashMap<String, Object>();
		
		StringBuffer hql = new StringBuffer("select DISTINCT b from " + InquirySurveybasic.class.getName() + " b, " + InquiryScope.class.getName() + " s, " + InquirySurveytype.class.getName() + " t "
				+ " WHERE ( s.inquirySurveybasic.id = b.id ) ");

		if (fromUserId != null) {
			if (toUserId != null) {//指定人发给我的
				hql.append(" AND b.createrId = :createrId AND s.scopeId in (:scopeIds) ");
				namedParameterMap.put("createrId", fromUserId);
				namedParameterMap.put("scopeIds", authIDList);
			} else {//我发出的
				hql.append(" AND b.createrId = :createrId ");
				namedParameterMap.put("createrId", fromUserId);
			}
		} else {//发给我的
			hql.append(" AND s.scopeId in (:scopeIds) ");
			namedParameterMap.put("scopeIds", authIDList);
		}

		hql.append(" AND b.flag=:bFlag AND b.censor=:bCensor AND b.surveyTypeId = t.id and t.flag=:tFlag ");

		namedParameterMap.put("bFlag", InquirySurveybasic.FLAG_NORMAL.intValue());
		namedParameterMap.put("bCensor", InquirySurveybasic.CENSOR_PASS.intValue());
		namedParameterMap.put("tFlag", InquirySurveytype.FLAG_NORMAL.intValue());

		if(Strings.isNotBlank(title)){
			hql.append(" and b.surveyName like :title");
			namedParameterMap.put("title", "%" + SQLWildcardUtil.escape(title) + "%");
		}
		if(beginDate != null){
			hql.append(" AND b.sendDate > :startDate");
			namedParameterMap.put("startDate", beginDate);
		}
		if(endDate != null){
			hql.append(" AND b.sendDate < :endDate");
			namedParameterMap.put("endDate", endDate);
		}
        hql.append(" order by b.sendDate desc");
        
		return (List<InquirySurveybasic>)inquiryBasicDao.find(hql.toString(), "b.id", true, namedParameterMap);
	}
	
	
	/**
	 * 查找是否存在重名调查模板
	 * @param tempName 模板名称
	 * @param typeId 模板ID
	 */
	@SuppressWarnings("unchecked")
	public boolean isInquiryUnique(String tempName,Long typeId){
		//List<InquirySurveybasic> basiclist = inquiryDao.find("from InquirySurveybasic inquiry where inquiry.surveyName='"+tempName+"' and inquiry.id!="+typeId+" and inquiry.createrId="+CurrentUser.get().getId() + " and inquiry.flag =" +InquirySurveybasic.FLAG_TEM);
		//HQL语句清理 modified by Meng Yang 2009-05-31
		String hql = "from InquirySurveybasic inquiry where inquiry.surveyName=? and inquiry.id<>? and inquiry.createrId=? and inquiry.flag=?";
		Object[] paramValues = new Object[] {tempName, typeId, CurrentUser.get().getId(), InquirySurveybasic.FLAG_TEM};
		List<InquirySurveybasic> basiclist = inquiryDao.find(hql, paramValues);
		return basiclist==null || basiclist.size() == 0 ? true : false;
	}
	
	/**
	 * 用于新建单位时初始化调查板块
	 * @param accountId
	 */
	public void initInquiryType(long accountId) {
		List<InquirySurveytype> types = this.inquiryTypeDao.getInquiryTypeByAccountId(V3xOrgEntity.VIRTUAL_ACCOUNT_ID);
		if(types != null) {
			for(InquirySurveytype type:types) {
				InquirySurveytype newType = new InquirySurveytype();
				newType.setIdIfNew();
				newType.setCensorDesc(type.getCensorDesc());
				newType.setFlag(type.getFlag());
				newType.setSurveyDesc(type.getSurveyDesc());
				newType.setTypeName(type.getTypeName());
				newType.setSpaceType(type.getSpaceType());
				newType.setAccountId(accountId);
				newType.setSort(type.getSort());
				this.inquiryTypeDao.save(newType);
			}
		}
		initAllSurveyType();
	}
	
	/**
	 * 用于更新调查版块的排序顺序
	 */
	public void updateSurveyTypeOrder(String[] surveyTypeIds) {
		if (surveyTypeIds == null) {
			return;
		}
		int i = 0;
		for (String surveyTypeId : surveyTypeIds) {
			i++;
			InquirySurveytype type = null;
			try {
				type = this.getSurveyTypeById(Long.valueOf(surveyTypeId));
			} catch (Exception e) {
				log.error("获取调查版块出现异常", e);
			}
			if (type != null) {
				type.setSort(i);
				inquiryTypeDao.update(type);
			}
		}
		// 重新加载版块列表, 保证集群环境下板块顺序一致
		initAllSurveyType();
	}
	
	/**
	 * 加载所有调查版块
	 *
	 */
	public void initAllSurveyType(){
		
		long startTime = System.currentTimeMillis();

		allSurveyTypeList = this.inquiryTypeDao.getAll();
		allSurveyTypeMap = new HashMap<Long, InquirySurveytype>();
		canIssueSurveyTypeMap.clear();
		canManageSurveyTypeMap.clear();
		canAuditSurveyTypeMap.clear();
		
		if(allSurveyTypeList.isEmpty()){
			return;
		}
		
		for (InquirySurveytype type : allSurveyTypeList) {
			
//				未删除的添加到map里
				if(type.getFlag().intValue()!=InquirySurveytype.FLAG_DELETE.intValue()){
					
				allSurveyTypeMap.put(type.getId(), type);
				
				
//				有权限发布的人的集合
				Set<InquiryAuthority> authIssues = type.getInquiryAuthorities();
				if(authIssues!=null && authIssues.size()>0){
					for(InquiryAuthority issuer : authIssues){
//						获取此人有发布权限的调查类型
						Set<Long> canIssueSurveyTypes = canIssueSurveyTypeMap.get(issuer.getAuthId());
//						如果不包括当前这个调查版块  加入
						if(canIssueSurveyTypes!=null){
							if(!canIssueSurveyTypes.contains(issuer.getInquirySurveytype().getId())){
								canIssueSurveyTypes.add(issuer.getInquirySurveytype().getId());
							}
						}else{
							canIssueSurveyTypes = new HashSet<Long>();
							canIssueSurveyTypes.add(issuer.getInquirySurveytype().getId());
						}
//						重新赋值
						canIssueSurveyTypeMap.put(issuer.getAuthId(), canIssueSurveyTypes);
					}
				}
				
//				管理员集合(审核、管理员)
				Set<InquirySurveytypeextend> surveyTypeManagers = type.getInquirySurveytypeextends();
				
				if(surveyTypeManagers!=null && surveyTypeManagers.size()>0){
					for(InquirySurveytypeextend manager : surveyTypeManagers){
	//					调查管理员
						if(manager.getManagerDesc().intValue()==InquirySurveytypeextend.MANAGER_SYSTEM.intValue()){
	//						获取此人有管理权限的调查类型
							Set<Long> canManageSurveyTypes = canManageSurveyTypeMap.get(manager.getManagerId());
							if(canManageSurveyTypes!=null && canManageSurveyTypes.size()>0){
								if(!canManageSurveyTypes.contains(type.getId())){
									canManageSurveyTypes.add(type.getId());
								}
							}else{
								canManageSurveyTypes = new HashSet<Long>();
								canManageSurveyTypes.add(type.getId());
							}
							canManageSurveyTypeMap.put(manager.getManagerId(), canManageSurveyTypes);
						}else{
	//						获取此人有审核权限的调查类型
							Set<Long> canAuditSurveyTypes = canAuditSurveyTypeMap.get(manager.getManagerId());
							if(canAuditSurveyTypes!=null && canAuditSurveyTypes.size()>0){
								if(!canAuditSurveyTypes.contains(type.getId())){
									canAuditSurveyTypes.add(type.getId());
								}
							}else{
								canAuditSurveyTypes = new HashSet<Long>();
								canAuditSurveyTypes.add(type.getId());
							}
							canAuditSurveyTypeMap.put(manager.getManagerId(), canAuditSurveyTypes);
						}
					}
				}
				
			}
			
		}
		
		log.info("加载所有调查版块信息. 耗时：" + (System.currentTimeMillis() - startTime) + " MS");
		
//		 附件标记升级数据
		String hql2 = "from InquirySurveybasic where attachmentsFlag is null";
		int total2 = inquiryBasicDao.getQueryCount(hql2, null, null);
		if(total2 == 0){
			log.debug("调查数据表不用初始化附件标记，没有 null 数据。");
		}else{			
			List<InquirySurveybasic> allData = inquiryBasicDao.getAll();
			if(allData == null || allData.size() == 0){
				log.debug("调查数据表不用初始化附件标记，没有公告数据。");
			}else{
				for(InquirySurveybasic data : allData){
					data.setAttachmentsFlag(attachmentManager.hasAttachments(data.getId(),data.getId()));	
					inquiryBasicDao.update(data);
				}

				log.info("调查数据表初始化附件标记完成，共 " + allData.size() + " 条。");
			}
		}
		
		// 发送通知
		NotificationManager.getInstance().send(NotificationType.InquiryLoadAllTypes, null);
		
	}
	
	/**
	 * 按板块抽取我能看到的调查   首页用到
	 */
	public List<SurveyBasicCompose> getSurveyByType(Long typeId) throws Exception {
		
			User member = CurrentUser.get();
			long memberid = member.getId();			 // 获取当前用户ID
			boolean isInternal = member.isInternal();
			
			String authID = "";
			
			if(isInternal){
				authID = orgManager.getUserIDDomain(memberid , V3xOrgEntity.VIRTUAL_ACCOUNT_ID,"Account","Department", "Team","Member", "Post", "Level", "Role");
			}else{
				authID = orgManager.getUserIDDomain(memberid , V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Department", "Team",
						"Member", "Post", "Level", "Role");
			}
			
			List<InquirySurveybasic> scopelist = inquiryBasicDao.getALLBasicListByTypeId( authID , typeId ,memberid);
			
//			for(InquirySurveybasic basic : scopelist ){
//				basic.setInquirySurveytype(allSurveyTypeMap.get(basic.getSurveyTypeId()));
//			}
			
			List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
			
			for (InquirySurveybasic ibasic : scopelist) {
				bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
			}
			
			return bscomposelist;
		}
	
	public InquiryLock lock(Long inquiryId, String action) {
		return this.lock(inquiryId, CurrentUser.get().getId(), action);
	}
	
	public InquiryLock lock(Long inquiryId, Long currentUserId, String action) {
		//进行文件锁的检查,方件锁是接口中的一个对象是不会抛空指针的
		InquiryLock inqlock=null;
		if(this.inquiryLockMap==null)
			this.inquiryLockMap=new HashMap<Long, InquiryLock>();

		if(this.inquiryLockMap.containsKey(inquiryId)) {
			//文件已加锁
			inqlock=this.inquiryLockMap.get(inquiryId);
			/**
			 * 如果操作类型相同，且锁的对象与当前用户相同，也允许用户继续进行同一操作
			 * 仅当两种不同操作同时在进行时，锁才确定生效，比如同一人进行编辑和审核操作，或者两人分别进行编辑或审核操作
			 */
			if(inqlock.getUserid()==currentUserId && action.equals(inqlock.getAction()))
				return null;
			return inqlock;
		} else {
			//文件没有加锁,对其加锁,继续进行相关的操作
			inqlock=new InquiryLock();
			inqlock.setInquiryid(inquiryId);
			inqlock.setUserid(currentUserId);
			inqlock.setAction(action);
			this.inquiryLockMap.put(inquiryId, inqlock);
			// 发送通知
			NotificationManager.getInstance().send(NotificationType.InquiryLock, inqlock);
			return null;
		}
	}

	public String[] lockStr(Long inquiryId, String action) throws BusinessException
	{
		InquiryLock inlock=null;
		String[] str=new String[1];
		inlock=this.lock(inquiryId, action);
		if(inlock!=null)
		{
			str[0]=orgManager.getMemberById(inlock.getUserid()).getName();
		}
		return str;
	}
	
	public Map<Long, InquiryLock> getLockInfo4Dump() {
		return this.inquiryLockMap;
	}
	
	public void unlock(Long inquiryId)
	{
		if(this.inquiryLockMap==null)
		{
			this.inquiryLockMap=new HashMap<Long, InquiryLock>();
		}
		if(this.inquiryLockMap.containsKey(inquiryId))
		{
			this.inquiryLockMap.remove(inquiryId);
			// 发送通知
			NotificationManager.getInstance().send(NotificationType.InquiryUnLock, inquiryId);
		}
	}

	public InquiryScopeDAO getInquiryScopeDAO()
	{
		return inquiryScopeDAO;
	}

	public void setInquiryScopeDAO(InquiryScopeDAO inquiryScopeDAO)
	{
		this.inquiryScopeDAO = inquiryScopeDAO;
	}
	/**
	 * 确定一下发布范围内有自己吗,如果没有自己就不显示提交和重置两个按钮
	 * 确定发布部门的类型sbcompose.getInquirySurveybasic().getId();调查的ID
	 */
	public boolean getInquiryScope(long inquiryId)
	{
		boolean scopeFlag=false;
		List<InquiryScope> list = this.inquiryScopeDAO.getInquiryScopeListDAO(inquiryId);
		if(list!=null && list.size()>0)
		{
			for (InquiryScope scope : list)
			{
				if("Account".equalsIgnoreCase(scope.getScopeDesc()))
				{
					if(scope.getScopeId().longValue()==CurrentUser.get().getAccountId())
					{
						scopeFlag=true;
						break;
					}
				}else if("Department".equalsIgnoreCase(scope.getScopeDesc()))
				{
					if(scope.getScopeId().longValue()==CurrentUser.get().getDepartmentId())
					{
						scopeFlag=true;
						break;
					}
				}else if("Member".equalsIgnoreCase(scope.getScopeDesc()))
				{
					if(scope.getScopeId().longValue()==CurrentUser.get().getId())
					{
						scopeFlag=true;
						break;
					}
				}
			}
		}
		return scopeFlag;
	}
	
	/**
	 * 判断用户是否在某一调查发布范围之内
	 */
	public boolean isInInquiryScope(User user, long inquiryId) throws Exception {
		List<Long> domainIds = this.getDomainIds(user);
		String hql = "select count(insco.id) from InquiryScope as insco where insco.inquirySurveybasic.id=:id and  insco.scopeId in (:domainIds)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", inquiryId);
		params.put("domainIds", domainIds);
		int count = ((Integer)this.inquiryScopeDAO.findUnique(hql, params)).intValue();
		return count>0;
	}
	
	/**
	 * 根据当前用户的ID和调查项的ID查出用户所选择的项数
	 *第一个参数传当前用户的ID,第二个参数传调查项的ID
	 */
	public List<Long> findByCurUser(long userid,long surbasicID)
	{
		List<Long> suritemid =new ArrayList<Long>();
		List<InquirySubsurveyitem> list=inquiryitemDao.findByCurUser(userid,surbasicID);
		for (InquirySubsurveyitem subsurveyitem : list)
		{
			suritemid.add(subsurveyitem.getId());
		}
		return suritemid;
	}
	
	/**
	 * 根據調查模塊名稱查詢
	 */
	public List<SurveyTypeCompose> getInquiryTypeList(String typename , String group) {
		List<SurveyTypeCompose> list = new ArrayList<SurveyTypeCompose>() ;
		List<InquirySurveytype> managerIns = new ArrayList<InquirySurveytype>() ;  //取得用户能管理的调查模块列表
		try{
			int spacetype = 0  ;
			if(group.equals("true")) {
				spacetype = InquirySurveytype.Space_Type_Group ;
			}else if(group.equals("false")){
				spacetype = InquirySurveytype.Space_Type_Account ;
			}else{
				spacetype = InquirySurveytype.Space_Type_Department ;
			}
			Long memberId = CurrentUser.get().getId() ;
			//得到所有的与该名字相近的模块列表
			List<InquirySurveytype> lnquirySurveytypeList = this.inquiryTypeDao.getInquiryTypeListByTitle(typename) ;
            //判断当前用户是不是改模块的管理员
			for(InquirySurveytype inquirySurveytype : lnquirySurveytypeList){
				if(inquirySurveytype.getSpaceType().intValue() == spacetype ){
			   
					Set<InquirySurveytypeextend> iSet = inquirySurveytype.getInquirySurveytypeextends() ;
					for(InquirySurveytypeextend ints :  iSet){
						if(ints.getManagerId().intValue() == memberId.intValue() 
								&& ints.getManagerDesc().intValue()==InquirySurveytypeextend.MANAGER_SYSTEM.intValue()) {
							managerIns.add(inquirySurveytype) ;	
						}
					} 	
				}
			}		
			list = this.getSurveyTypeCompose(list, managerIns, true) ;
			Pagination.setRowCount(list.size()) ;
		}catch(Exception e){
			log.error("", e) ;
		}	
		return list ;
	}
	/**
	 * 根据总数查找的实现
	 */
	public List<SurveyTypeCompose> getInquiryTypeList(String num,String match,String group){
		List<SurveyTypeCompose> list = null;
		List<SurveyTypeCompose> list1 = new ArrayList<SurveyTypeCompose>() ;
		try{
			//得到所有此人能管理的调查列表
			if(group!=null && !group.equals("") && group.equals("true")){
				list = this.getGroupInquiryTypeList() ;
			}else {	
				list = this.getAuthoritiesTypeList() ;
			}
            //判断公告总数与用户输入数字的关系
			if(list!=null && list.size()>0) {
				if(num != null && !num.equals("")) {  //不为空
					int total = Integer.parseInt(num) ;
					if(match != null &&!match.equals("") && match.equals("more")){  //大于
						for(SurveyTypeCompose stc : list ){
							if(stc.getCount().intValue() > total){
								list1.add(stc) ;
							}
						}
					}else if(match != null &&!match.equals("") && match.equals("less")){ //小于
						for(SurveyTypeCompose stc : list ){
							if(stc.getCount().intValue() < total){
								list1.add(stc) ;
							}
						}
					}else if(match != null &&!match.equals("") && match.equals("equal")){ //等于
						for(SurveyTypeCompose stc : list ){
							if(stc.getCount().intValue() == total){
								list1.add(stc) ;
							}
						}
					}else{
						list1.addAll(list) ;
					}
				}else{
	//				用户输入的数字为空的处理
					list1.addAll(list) ;
				}
			}
		}catch(Exception e){
			log.error("" ,e) ;
		}
		return list1 ;
	}
    /**
     * 根据是否需要审核实现
     */
	public List<SurveyTypeCompose> getInqTypeListByauditFlag(String flag ,String group) {
		List<InquirySurveytype> allList = null;
		List<SurveyTypeCompose> resultList = new ArrayList<SurveyTypeCompose>() ;
		List<InquirySurveytype> list2 = new ArrayList<InquirySurveytype>() ;  //满足条件的InquirySurveytype集合
		try{
			//得到所有此人能管理的调查列表
			if(group!=null && !group.equals("") && group.equals("true")){
				allList = this.getGroupInquiryTypeListByUserAuth() ;
			}else {			
				allList = this.getInquiryTypeListByUserAuth() ;
			}
            //判断公告中是否需要审核与用户输入的字符的关系
			if(allList!=null && allList.size()>0) {
				if(flag != null && !flag.equals("") && flag.equals("true") ){        //需要审核
					for(InquirySurveytype isc : allList){
						if(isc.getCensorDesc().intValue() == InquirySurveytype.CENSOR_NO_PASS.intValue())
							list2.add(isc) ;
					}			
				}else if(flag != null && !flag.equals("") && flag.equals("false")){  //不需要审核
					for(InquirySurveytype isc : allList){
						if(isc.getCensorDesc().intValue() == InquirySurveytype.CENSOR_PASS.intValue())
							list2.add(isc) ;
					}			
				}else{
					list2.addAll(allList) ;
				}
			}
			resultList =  this.getSurveyTypeCompose(resultList, list2, true) ;
			Pagination.setRowCount(resultList.size()) ;		
		}catch(Exception e){
			log.error("" ,e) ;
		}
		return resultList ;	
	}
    /**
     * 用户根据审核员名字查询
     */
	public List<SurveyTypeCompose> getInqTypeListByauditManager(String auditUserName ,String group)  {
		List<SurveyTypeCompose> resultList = new ArrayList<SurveyTypeCompose>() ;	
		
		List<InquirySurveytype> inqTypeListAcess = new ArrayList<InquirySurveytype>() ; 
		try{
			int spacetype = 0  ;
			if(group.equals("true")) {
				spacetype = InquirySurveytype.Space_Type_Group ;
			}else if(group.equals("false")){
				spacetype = InquirySurveytype.Space_Type_Account ;
			}else{
				spacetype = InquirySurveytype.Space_Type_Department ;
			}
			List<InquirySurveytype> inqTypeList = null;
			if(auditUserName != null && !auditUserName.equals("")){
				inqTypeList = this.inquiryTypeDao.getAllInquirySurveytype(auditUserName) ;
				Long memberId = CurrentUser.get().getId() ;
				//判断当前用户是不是改模块的管理员
				if(inqTypeList!=null && inqTypeList.size()>0) {
					for(InquirySurveytype inquirySurveytype : inqTypeList){
						if(inquirySurveytype.getSpaceType().intValue() == spacetype ){		   
							Set<InquirySurveytypeextend> iSet = inquirySurveytype.getInquirySurveytypeextends() ;
							for(InquirySurveytypeextend ints :  iSet){
								if(ints.getManagerId().intValue() == memberId.intValue() 
										&& ints.getManagerDesc().intValue()==InquirySurveytypeextend.MANAGER_SYSTEM.intValue()) {
									inqTypeListAcess.add(inquirySurveytype) ;	
								}
							} 	
						}
					}
				}
				resultList = this.getSurveyTypeCompose(resultList, inqTypeListAcess, true) ;
				Pagination.setRowCount(resultList.size()) ;						
			}else{
				if(spacetype == InquirySurveytype.Space_Type_Group)
					resultList = this.getGroupInquiryTypeList() ;
				else
					resultList = this.getAuthoritiesTypeList() ;
			}	
		}catch(Exception e){
			log.error("" ,e) ;
		}	
		return resultList ;
	}

	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
	 public List<SurveyBasicCompose> getInquiryBasicListByUserIDByRecent(long accountId,
				long personId, int firstNum, int size) throws Exception
		{
			String authID = orgManager.getUserIDDomain(personId, accountId, "Account", "Department",
					"Team", "Member", "Post", "Level", "Role");
			List<InquirySurveybasic> scopelist = inquiryBasicDao
					.getInquiryBasicListByUserScopeByRecent(authID, accountId, personId, firstNum, size);

			for (InquirySurveybasic basic : scopelist) {
				basic.setInquirySurveytype(allSurveyTypeMap.get(basic.getSurveyTypeId()));
			}

			List<SurveyBasicCompose> bscomposelist = new ArrayList<SurveyBasicCompose>();
			for (InquirySurveybasic ibasic : scopelist) {
				bscomposelist = this.getSurveyBasicInterior(bscomposelist, ibasic);
			}
			return bscomposelist;
		}
	 
	public void transfer2NewChecker(Long typeId, Long oldCheckerId, Long newCheckerId) {
		this.inquiryBasicDao.transfer2NewAuditor(typeId, oldCheckerId, newCheckerId);
		this.inquiryBasicDao.updateInquiryChecker(typeId, newCheckerId);
	}
	
	/**
	 * 配合管理员进行归档时，前端进行AJAX校验，返回选中调查中已结束的调查ID字符串以便进行下一步操作
	 * @param ids  选中的调查IDs
	 * @return String[] [0] - 已结束的调查ID拼接字符串结果,  [1] - 选中的调查是否包括了未结束的调查
	 */
	public String[] filterWhenPigeonhole(String ids) {
		List<Long> src = CommonTools.parseStr2Ids(ids);
		String[] result = {null, null};
		List<Long> ended = this.inquiryBasicDao.getEndedInquiryIds(src);
		if(CollectionUtils.isNotEmpty(ended)) {
			result[0] = StringUtils.join(ended, ",");
			result[1] = String.valueOf(ended.size() != src.size());
		} else {
			result[0] = "";
			result[1] = "true";
		}
		return result;
	}
	
	public boolean isAuditorOfInquiry(Long memberId) {
		Set<Long> auditTypes = canAuditSurveyTypeMap.get(memberId);
		if (auditTypes != null && auditTypes.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void delMember(Long id) throws Exception {
		Set<Long> manageTypes = canManageSurveyTypeMap.get(id);
		Set<Long> auditTypes = canAuditSurveyTypeMap.get(id);
		List<Long> types = CommonTools.getSumCollection(manageTypes, auditTypes);
		boolean isReloadAllType=false;
		int i = 0;
		if(types != null){
			for(Long typeId : types){
				InquirySurveytype surveyType = allSurveyTypeMap.get(typeId);
				Set<InquirySurveytypeextend> managerSet = surveyType.getInquirySurveytypeextends();
				Set<InquirySurveytypeextend> oldSet=new HashSet<InquirySurveytypeextend>();
				oldSet.addAll(managerSet);
				Set<InquirySurveytypeextend> temp = new HashSet<InquirySurveytypeextend>();
				for(InquirySurveytypeextend manager:oldSet){
					if(id.equals(manager.getManagerId()) && manager.getManagerDesc() != 1){
						temp.add(manager);
					}
				}
				oldSet.removeAll(temp);
				if(i == types.size() - 1){
					isReloadAllType = true;
				}
				this.updateInquiryType(surveyType, oldSet,isReloadAllType);
				i ++;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<InquirySurveytype> getInquiryTypeListByUserId(String userid,int type) {
		StringBuffer hql = new StringBuffer("SELECT t From ");
		hql.append(InquirySurveytype.class.getName()).append(" t,");
		hql.append(InquirySurveytypeextend.class.getName()).append(" e");
		hql.append(" Where  e.inquirySurveytype.id = t.id AND t.flag=");
		hql.append(InquirySurveytype.FLAG_NORMAL);
		hql.append(" AND e.managerId =? AND e.managerDesc =? ");
		List<InquirySurveytype> list= inquiryTypeDao.find(hql.toString(), new Long(userid),type);
		return list;
	}
	
	public InquirySurveytype saveCustomInquirySurveytype(Long spaceId, String spaceName) {
		InquirySurveytype type = new InquirySurveytype();
		type.setId(spaceId);
		type.setTypeName(spaceName);
		type.setSurveyDesc(null);
		type.setSpaceType(InquirySurveytype.Space_Type_Custom);
		type.setAccountId(spaceId);
		type.setCensorDesc(InquirySurveytype.CENSOR_PASS);
		type.setFlag(InquirySurveytype.FLAG_NORMAL);
		try {
			this.saveInquiryType(type);
		} catch (Exception e) {
			log.error("", e);
		}
		return type;
	}
	
	public boolean isEffective(String bid) throws Exception {
		InquirySurveybasic basic = inquiryBasicDao.getInquirySurveybasicByID(Long.parseLong(bid));
		if (basic == null) {
			return false;
		} else if (basic.getFlag() == 1) {
			//直接清除待办事项
			affairManager.deleteByObject(ApplicationCategoryEnum.inquiry, basic.getId());
			return false;
		} else {
			return true;
		}
	}

	@Override
	public List<InquirySurveytype> getAllCustomSurveyTypeList()
			throws BusinessException {
		List<InquirySurveytype> types = new ArrayList<InquirySurveytype>();
		List<Long> customSpaceIds = spaceManager.getAllCustomSpace();
		for(InquirySurveytype type : allSurveyTypeList){
			if(type.getFlag()!=1 && customSpaceIds.contains(type.getAccountId())){
				types.add(type);
			}
		}
		
//		排序
		Comparator<InquirySurveytype> comp = new InquirySurveytype();
		
		Collections.sort(types,comp);
		
		return types;
	}

	@Override
	public List<SurveyTypeCompose> getAllCustomInquiryList()
			throws BusinessException {
		User user = CurrentUser.get();
//		外部人员查看权限过滤
		boolean isInternal = user.isInternal();
		String authID = "";
		
		if(isInternal){
			authID = orgManager.getUserIDDomain(user.getId() , V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Account","Department", "Team","Member", "Post", "Level", "Role");
		}else{
			authID = orgManager.getUserIDDomain(user.getId() , V3xOrgEntity.VIRTUAL_ACCOUNT_ID, "Department", "Team","Member", "Post", "Level", "Role");
		}
		
		List<InquirySurveytype> surveytypes = getAllCustomSurveyTypeList();
		List<SurveyTypeCompose> comlist = new ArrayList<SurveyTypeCompose>();
		if(surveytypes!=null && surveytypes.size()>0) {
			for (InquirySurveytype surveytype : surveytypes) {
				SurveyTypeCompose stCompose = new SurveyTypeCompose();
				stCompose.setInquirySurveytype(surveytype);
				
				ArrayList<V3xOrgMember> manager = new ArrayList<V3xOrgMember>();
				
				Set<InquirySurveytypeextend> surveytypeextends = surveytype.getInquirySurveytypeextends();
				
				//保持用户设定管理员的顺序 added by Meng Yang at 2009-06-30
				List<InquirySurveytypeextend> sortedList = new ArrayList<InquirySurveytypeextend>();
				if(surveytypeextends!=null && surveytypeextends.size()>0) {
					sortedList.addAll(surveytypeextends);
					Collections.sort(sortedList);
				}
				
				if(sortedList!=null && sortedList.size()>0) {
					for (InquirySurveytypeextend surveytypeextend : sortedList) {
						V3xOrgMember member = this.orgManager.getEntityById(V3xOrgMember.class, surveytypeextend.getManagerId());// 获取当前用户对象
	    				if (surveytypeextend.getManagerDesc().intValue() == InquirySurveytypeextend.MANAGER_SYSTEM.intValue()) {// 管理员
	    					manager.add(member);
	    				} else if (surveytypeextend.getManagerDesc().intValue() == InquirySurveytypeextend.MANAGER_CHECK.intValue()) {// 审核人员
	    					stCompose.setChecker(member);
	    				}
					}
				}   
				
				stCompose.setManagers(manager);
				comlist.add(stCompose);
			}
		}
		return comlist;
	}


}