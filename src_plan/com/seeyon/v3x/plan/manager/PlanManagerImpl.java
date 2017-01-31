package com.seeyon.v3x.plan.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.common.filemanager.Partition;
import com.seeyon.v3x.common.filemanager.manager.FileManager;
import com.seeyon.v3x.common.filemanager.manager.PartitionManager;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.index.share.datamodel.AuthorizationInfo;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.indexInterface.IndexUtil;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.plan.Constant;
import com.seeyon.v3x.plan.PlanRelevantUserType;
import com.seeyon.v3x.plan.PlanType;
import com.seeyon.v3x.plan.PublishStatus;
import com.seeyon.v3x.plan.dao.PlanBodyDao;
import com.seeyon.v3x.plan.dao.PlanDao;
import com.seeyon.v3x.plan.dao.PlanRelevantUserDao;
import com.seeyon.v3x.plan.dao.PlanReplyDao;
import com.seeyon.v3x.plan.dao.PlanSummaryDao;
import com.seeyon.v3x.plan.dao.PlanUserScopeDao;
import com.seeyon.v3x.plan.domain.Plan;
import com.seeyon.v3x.plan.domain.PlanBody;
import com.seeyon.v3x.plan.domain.PlanCount;
import com.seeyon.v3x.plan.domain.PlanCountModel;
import com.seeyon.v3x.plan.domain.PlanRelevantUser;
import com.seeyon.v3x.plan.domain.PlanReply;
import com.seeyon.v3x.plan.domain.PlanSummary;
import com.seeyon.v3x.util.Datetimes;

public class PlanManagerImpl implements PlanManager ,IndexEnable {

	private OrgManager orgManager;

	private PlanBodyDao planBodyDao;

	private PlanDao planDao;

	private PlanRelevantUserDao planRelevantUserDao;

	private PlanReplyDao planReplyDao;

	private PlanSummaryDao planSummaryDao;

	private PlanUserScopeDao planUserScopeDao;
	
	private FileManager fileManager;
	
	private PartitionManager partitionManager;
	
	private static final Log log = LogFactory.getLog(PlanManagerImpl.class);
	
	/**
	 * 获取当前登录用户对查看计划的角色：主、抄、告或其他
	 * @param planId	计划
	 * @param userId	当前登录用户ID
	 * @return 角色类型
	 */
	public  String getUserRole(Long planId, Long userId) {
		String hql = "from " + PlanRelevantUser.class.getName() + " as r where r.plan.id=? and r.refUserId=? and r.type!=?";
		PlanRelevantUser result = (PlanRelevantUser)this.planRelevantUserDao.findUnique(hql, null, planId, userId, PlanRelevantUser.UserType.Creator.getValue());
		return result == null ? "0" : result.getType();
	}
	 
	

	public void addPlan(Plan plan) {
		plan.setIdIfNew();
		getPlanDao().save(plan);
	}

	public void addPlanBody(PlanBody planBody) {
		planBody.setIdIfNew();
		getPlanBodyDao().save(planBody);
	}

	public void addPlanReply(PlanReply planReply) {
		planReply.setIdIfNew();
		planReplyDao.save(planReply);
	}

	public void addPlanSummary(PlanSummary planSummary) {
		planSummary.setIdIfNew();
		planSummaryDao.save(planSummary);
	}

	/**
	 * 分解计划统计集合
	 * 
	 * @param planCountList
	 * @return map key:计划类型（日计划、周计划等） value:计划统计Model（具体数字）
	 */
	private Map analyseList(Collection planCountList) {
		int iTemp = 0;
		PlanCountModel pcmDay = new PlanCountModel();// 日计划统计
		PlanCountModel pcmWeek = new PlanCountModel();// 周计划统计
		PlanCountModel pcmMonth = new PlanCountModel();// 月计划统计
		PlanCountModel pcmAnyScope = new PlanCountModel();// 任意期计划统计
		for (Iterator itr = planCountList.iterator(); itr.hasNext();) {
			PlanCount plan = (PlanCount) itr.next();

			if (plan.getType().equals(PlanType.DAY_PLAN.getValue())) {
				if (!plan.getPublishStatus().equals(PublishStatus.DRAFT.getValue())) {
					iTemp = pcmDay.getIssuedCount();
					pcmDay.setIssuedCount(++iTemp);
				}
				if (plan.getSummaryCount() != 0) {
					iTemp = pcmDay.getSummaryCount();
					pcmDay.setSummaryCount(++iTemp);
				} else {
					iTemp = pcmDay.getNotSummaryCount();
					pcmDay.setNotSummaryCount(++iTemp);
				}
				if (plan.getReplyCount() != 0) {
					iTemp = pcmDay.getReplyCount();
					pcmDay.setReplyCount(++iTemp);
				} else {
					iTemp = pcmDay.getNotReplyCount();
					pcmDay.setNotReplyCount(++iTemp);
				}
			}
			if (plan.getType().equals(PlanType.WEEK_PLAN.getValue())) {
				if (!plan.getPublishStatus().equals(PublishStatus.DRAFT.getValue())) {
					iTemp = pcmWeek.getIssuedCount();
					pcmWeek.setIssuedCount(++iTemp);
				}
				if (plan.getSummaryCount() != 0) {
					iTemp = pcmWeek.getSummaryCount();
					pcmWeek.setSummaryCount(++iTemp);
				} else {
					iTemp = pcmWeek.getNotSummaryCount();
					pcmWeek.setNotSummaryCount(++iTemp);
				}
				if (plan.getReplyCount() != 0) {
					iTemp = pcmWeek.getReplyCount();
					pcmWeek.setReplyCount(++iTemp);
				} else {
					iTemp = pcmWeek.getNotReplyCount();
					pcmWeek.setNotReplyCount(++iTemp);
				}
			}
			if (plan.getType().equals(PlanType.MONTH_PLAN.getValue())) {
				if (!plan.getPublishStatus().equals(PublishStatus.DRAFT.getValue())) {
					iTemp = pcmMonth.getIssuedCount();
					pcmMonth.setIssuedCount(++iTemp);
				}
				if (plan.getSummaryCount() != 0) {
					iTemp = pcmMonth.getSummaryCount();
					pcmMonth.setSummaryCount(++iTemp);
				} else {
					iTemp = pcmMonth.getNotSummaryCount();
					pcmMonth.setNotSummaryCount(++iTemp);
				}
				if (plan.getReplyCount() != 0) {
					iTemp = pcmMonth.getReplyCount();
					pcmMonth.setReplyCount(++iTemp);
				} else {
					iTemp = pcmMonth.getNotReplyCount();
					pcmMonth.setNotReplyCount(++iTemp);
				}
			}
			if (plan.getType().equals(PlanType.ANY_SCOPE_PLAN.getValue())) {
				if (!plan.getPublishStatus().equals(PublishStatus.DRAFT.getValue())) {
					iTemp = pcmAnyScope.getIssuedCount();
					pcmAnyScope.setIssuedCount(++iTemp);
				}
				if (plan.getSummaryCount() != 0) {
					iTemp = pcmAnyScope.getSummaryCount();
					pcmAnyScope.setSummaryCount(++iTemp);
				} else {
					iTemp = pcmAnyScope.getNotSummaryCount();
					pcmAnyScope.setNotSummaryCount(++iTemp);
				}
				if (plan.getReplyCount() != 0) {
					iTemp = pcmAnyScope.getReplyCount();
					pcmAnyScope.setReplyCount(++iTemp);
				} else {
					iTemp = pcmAnyScope.getNotReplyCount();
					pcmAnyScope.setNotReplyCount(++iTemp);
				}
			}
		}
		Map<String, PlanCountModel> map = new HashMap<String, PlanCountModel>();
		map.put(PlanType.DAY_PLAN.getValue(), pcmDay);
		map.put(PlanType.WEEK_PLAN.getValue(), pcmWeek);
		map.put(PlanType.MONTH_PLAN.getValue(), pcmMonth);
		map.put(PlanType.ANY_SCOPE_PLAN.getValue(), pcmAnyScope);
		return map;
	}

	public Map countDraftsmanPlan(Long userId, Date startTime1, Date startTime2) {
		List planList = getPlanDao().findByUserAndUserTypeAndTime(userId,
				PlanRelevantUserType.DRAFTSMAN.getValue(), startTime1,
				startTime2);

		return analyseList(planList);

	}
	
	
	@SuppressWarnings("unchecked")
	public Map countUserScopePlan(Long userId, Date date1, Date date2){
		// 通过赋权得到的用户列表
		List userList = getPlanUserScopeDao().getPlanUserIdListByRefUserId(userId);
		Iterator userListIt = userList.iterator();
		Map resultMap = new HashMap();
		while(userListIt.hasNext()){
			Long userScopeId = Long.valueOf(String.valueOf(userListIt.next()));
			PlanDao planDao = getPlanDao();
			List<PlanCount> planCount =  planDao.findByUserAndUserTypeAndTime(userScopeId, PlanRelevantUserType.TO_LEADER.getValue(),date1,date2);
			planCount.addAll(planDao.findByUserAndUserTypeAndTime(userScopeId,PlanRelevantUserType.CC_LEADER.getValue(),date1,date2));
			planCount.addAll(planDao.findByUserAndUserTypeAndTime(userScopeId,PlanRelevantUserType.APPRIZE_USER.getValue(),date1,date2));		
			Map modelMap = analyseList(planCount);
			resultMap.put(userScopeId,modelMap);
		}
		return resultMap;
	}

	// public List getPlan(Long userId, String planType, Date createDate1,
	// Date createDate2) {
	// return null;
	// // return getPlanDao().findByUserAndTypeAndTime(userId, planType,
	// // createDate1, createDate2);
	// }

	// public Map countPlanByUserList(List userIdList, Date startTime1,
	// Date startTime2) {
	// // 自动生成方法存根
	// return null;
	// }

	public Map countUserPlan(Long userId, Date startTime1, Date startTime2) {
		// 主送来的用户列表
//		List userList1 = getPlanDao().findDraftsmanUserIdListByToLeaderUserId(
//				userId, startTime1, startTime2);
		// 通过赋权得到的用户列表
		List userList2 = getPlanUserScopeDao().getPlanUserIdListByRefUserId(
				userId);
//		List userList = new ArrayList();
//		userList.addAll(userList1);
//		userList.addAll(userList2);

//		List planCountList = getPlanDao().findByUserAndUserTypeAndTime(
//				userList, PlanRelevantUserType.DRAFTSMAN.getValue(),
//				startTime1, startTime2);
		//modified by paul at 5/15/07 计划统计根据主送用户和授权用户列表
		List planCountList = getPlanDao().findPlanCountsByToLeaderAndUserScopeList(
				userId, PlanRelevantUserType.TO_LEADER.getValue(), 
				userList2, PlanRelevantUserType.DRAFTSMAN.getValue(), 
				startTime1, startTime2);
		Map resultMap = new HashMap();
		MultiMap tempMap = new MultiHashMap();
		for (Iterator itr = planCountList.iterator(); itr.hasNext();) {
			PlanCount planCount = (PlanCount) itr.next();
			tempMap.put(planCount.getUserId(), planCount);
		}
		for (Iterator itrUserMap = tempMap.keySet().iterator(); itrUserMap
				.hasNext();) {
			Long tempUserId = (Long) itrUserMap.next();
			Map map = analyseList((Collection) tempMap.get(tempUserId));
			resultMap.put(tempUserId, map);
		}
		return resultMap;
	}
	
	/**
	 * @deprecated modified by paul at 5/17/07
	 */
	public Map countUserPlan(Long userId, List<V3xOrgMember> memberList,
			Date startTime1, Date startTime2) {
		List userList1 = getPlanDao().findDraftsmanUserIdListByToLeaderUserId(
				userId, startTime1, startTime2);
		// 通过赋权得到的用户列表
		List userList2 = getPlanUserScopeDao().getPlanUserIdListByRefUserId(
				userId);
		List userList = new ArrayList();
		userList.addAll(userList1);
		userList.addAll(userList2);
		
		List<Long> userIdList = new ArrayList<Long>();
		if (memberList!=null && !memberList.isEmpty()){
			for(V3xOrgMember member:memberList){
				for(Long tempUserId : (List<Long>)userList){
					if(member.getId().equals(tempUserId)){
						userIdList.add(tempUserId);
					}
				}
			}
		}
		
		List planCountList = getPlanDao().findByUserAndUserTypeAndTime(
				userIdList, PlanRelevantUserType.DRAFTSMAN.getValue(),
				startTime1, startTime2);
		Map resultMap = new HashMap();
		MultiMap tempMap = new MultiHashMap();
		for (Iterator itr = planCountList.iterator(); itr.hasNext();) {
			PlanCount planCount = (PlanCount) itr.next();
			tempMap.put(planCount.getUserId(), planCount);
		}
		for (Iterator itrUserMap = tempMap.keySet().iterator(); itrUserMap
				.hasNext();) {
			Long tempUserId = (Long) itrUserMap.next();
			Map map = analyseList((Collection) tempMap.get(tempUserId));
			resultMap.put(tempUserId, map);
		}
		return resultMap;
	}

	public void deletePlan(Long id) {
		getPlanDao().delete(id);
	}

	public void deletePlanBodyByPlanId(Long planId) {
		getPlanBodyDao().deleteByPlanId(planId);
	}

	public void deletePlanRelevantUserByPlanId(Long planId) {
		this.planRelevantUserDao.deleteByPlanId(planId);
	}

	public void deletePlanReplyByPlanId(Long planId) {
		getPlanReplyDao().deleteByPlanId(planId);
	}

	public void deletePlans(Long[] ids) {
		getPlanDao().delete(ids);
	}

	public void deletePlanSummaryByPlanId(Long planId) {
		getPlanSummaryDao().deleteByPlanId(planId);
	}

	public List getIsDraftsmanPlan(Long userId, Date startTime1, Date startTime2) {
		return getPlanDao().findByUserAndTypeAndTime(userId, startTime1,
				startTime2, true);
	}

	public List getIsDraftsmanPlan(Long userId, String planType,
			Date startTime1, Date startTime2) {
		return getPlanDao().findByUserAndTypeAndTime(userId, planType,
				startTime1, startTime2, true);
	}

	public List getNotDraftsmanPlan(Long userId, String planType,
			Date startTime1, Date startTime2) {
		return getPlanDao().findByUserAndTypeAndTime(userId, planType,
				startTime1, startTime2, false);
	}
	
	public List getNotDraftsmanPlanForPage(Long userId, String planType,
			Date startTime1, Date startTime2) {
		return getPlanDao().findByUserAndTypeAndTimeForPage(userId, planType,
				startTime1, startTime2, false);
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		//部门的最新几条显示
	public List getDeptNotDraftsmanPlanForPage(int pageSize ,Long departmentId, Long userId, Date startTime1) {
		return getPlanDao().deptfindByUserAndTypeAndTimeForPage(pageSize, departmentId, userId, startTime1, false);
	}
	//部门更多显示
	public List getDeptMorePlanList(Long departmentId, Long userId, Date startTime1 , boolean isFromDeptSpaceManage) {
		return getPlanDao().deptMorePlanList( departmentId, userId, startTime1, isFromDeptSpaceManage );
	}

	public int cancelPublishPlan( Set<Long> planIds){
		return getPlanDao().cancelPublishPlan(planIds);
	}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public OrgManager getOrgManager() {
		return orgManager;
	}

	public List<Plan> getPlan(Long loginUserId, List<V3xOrgMember> userList,
			String planType, Date startTime1, Date startTime2) {
		List<Long> ids = new ArrayList<Long>();
		for (Iterator itr = userList.iterator(); itr.hasNext();) {
			V3xOrgMember member = (V3xOrgMember) itr.next();
			Long id = member.getId();
			ids.add(id);
		}
		return getPlanDao().findByUserAndTypeAndTime(loginUserId, ids,
				planType, startTime1, startTime2);
	}

	public List getPlan(Long userId, String relevantUserType, String planType,
			Date createDate1, Date createDate2) {
		return getPlanDao().findByUserAndTypeAndTime(userId, relevantUserType,
				planType, createDate1, createDate2);
	}
	
	public List getPlanForPage(Long userId, String relevantUserType, String planType,
			Date createDate1, Date createDate2) {
		return 	getPlanDao().findByUserAndTypeAndTimeForPage(userId, relevantUserType, planType, createDate1, createDate2);
	}

	public PlanBodyDao getPlanBodyDao() {
		return planBodyDao;
	}

	public Plan getPlanById(Long id) {
		return this.getPlanByPk(id);
	}

	public Plan getPlanByPk(Long id) {
		return getPlanDao().findByPrimaryKey(id);
	}

	public PlanDao getPlanDao() {
		return planDao;
	}

	public Plan getPlanLoadBodyAndReplyAndSummary(Long planId,
			MultiMap planReplyMap) {
		Plan plan = this.getPlanByPk(planId);
		Hibernate.initialize(plan.getPlanBody());
		Hibernate.initialize(plan.getPlanReply());
		Hibernate.initialize(plan.getPlanSummary());
		Hibernate.initialize(plan.getAllPlanRefUser());

		List replyList = plan.getPlanReply();
		for (Iterator itr = replyList.iterator(); itr.hasNext();) {
			PlanReply pr = (PlanReply) itr.next();
			if (pr.getRefPlanReplyId() != null) {
				if (pr.getRefPlanReplyId().equals(pr.getId())) {
					planReplyMap.put(Constant.FIRST_LEVEL_PLANREPLY, pr);
				} else {
					planReplyMap.put(pr.getRefPlanReplyId(), pr);
				}
			}
		}
		return plan;
	}

	public PlanRelevantUserDao getPlanRelevantUserDao() {
		return planRelevantUserDao;
	}

	public PlanReplyDao getPlanReplyDao() {
		return planReplyDao;
	}

	public PlanSummaryDao getPlanSummaryDao() {
		return planSummaryDao;
	}

	public PlanUserScopeDao getPlanUserScopeDao() {
		return planUserScopeDao;
	}

	public Map<Long, String> getUserName(Collection<Long> userIds) {
		Map<Long, String> result = new HashMap<Long, String>();
		for (Long userId : userIds) {
			result.put(userId, "用户名 请取消代码中PlanManagerImpl第382行开始的注释");
			// TODO 本地没有数据
			 try {
				 V3xOrgMember member =
			 getOrgManager().getMemberById(userId);
			 result.put(userId, member.getName());
			 } catch (BusinessException e) {
			 }
		}
		return result;
	}

    public int[] countPlanUser(Long planId, Long userid) {
	    int[] count = getPlanDao().countPlanUser(planId, userid);
	    return count;
	}

	public List listPlan() {
		return getPlanDao().list();
	}

	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}

	public void setPlanBodyDao(PlanBodyDao planBodyDao) {
		this.planBodyDao = planBodyDao;
	}

	public void setPlanDao(PlanDao planDao) {
		this.planDao = planDao;
	}

	public void setPlanRelevantUserDao(PlanRelevantUserDao planRelevantUserDao) {
		this.planRelevantUserDao = planRelevantUserDao;
	}

	public void setPlanReplyDao(PlanReplyDao planReplyDao) {
		this.planReplyDao = planReplyDao;
	}

	public void setPlanSummaryDao(PlanSummaryDao planSummaryDao) {
		this.planSummaryDao = planSummaryDao;
	}

	public void setPlanUserScopeDao(PlanUserScopeDao planUserScopeDao) {
		this.planUserScopeDao = planUserScopeDao;
	}

	public void updatePlan(Plan plan) {
        planDao.update(plan);
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setPartitionManager(PartitionManager partitionManager) {
		this.partitionManager = partitionManager;
	}

	public void updatePlanBody(PlanBody planBody) {
		planBody.setIdIfNew();
		getPlanBodyDao().saveOrUpdate(planBody);
	}

	public Plan getPersonalLastPlan(Long userId, String planType) {
		List plans = this.getPlanDao().findPersonalLastPlan(userId, planType);
		if(plans.iterator().hasNext()) return (Plan)plans.iterator().next();
		return new Plan();
	}
	
	public Plan getPersonalLastDatePlan(Long userId, String planType) {
		List plans = this.getPlanDao().findPersonalLastDatePlan(userId, planType);
		if(plans.iterator().hasNext()) return (Plan)plans.iterator().next();
		return new Plan();
	}

	public PlanReply getPlanReplyByPk(Long planReplyId) {
		// TODO Auto-generated method stub
		return getPlanReplyDao().findByPrimaryKey(planReplyId);
	}

	public List getIsDraftsmanPlan(Long userId, String planType) {
		// TODO Auto-generated method stub
		return getPlanDao().findByUserAndType(userId, planType, true);
	}

	public int countDraftsmanPlan(Long userId, String planType) {
		// TODO Auto-generated method stub
		return getPlanDao().countByUserAndType(userId, planType, true);
	}

	public List getDraftsmanPlan(Long userId, String planType) {
		// TODO Auto-generated method stub
		return getPlanDao().findByUserAndTypeForPage(userId, planType, true);
	}

	public List getPlan(Long userId, String relevantUserType, String planType) {
		// TODO Auto-generated method stub
		return getPlanDao().findByUserAndType(userId, relevantUserType,planType);
	}

	@SuppressWarnings("unchecked")
	public Map getUserScope(Long userId) {
		// TODO Auto-generated method stub
		List lst = getPlanUserScopeDao().getPlanUserIdListByRefUserId(userId);
		if(ListUtils.EMPTY_LIST.equals(lst)){
			return MapUtils.EMPTY_MAP;
		}else{
			return getUserName(lst);
		}
	}

	public List getIsDraftsmanPlanForPage(Long userId, String planType, Date startTime1, Date startTime2) {
		// TODO Auto-generated method stub
		return getPlanDao().findByUserAndTypeAndTimeForPage(userId, planType, startTime1, startTime2, true);
	}
	
	
	public List getMyPlanByTypeAndDateForPage(Long userId, String planType, Date startTime1, Date startTime2) {
		return getPlanDao().findByUserAndTypeAndTimeForPage(userId, planType, startTime1, startTime2);
	}
	
	public boolean isPlanRepeated(Long planId,Long userId,Date startTime,Date endTime, Long depId, Long projectId){
//		List planList = getIsDraftsmanPlan(userId,startTime,Datetimes.addDate(endTime, 1));
//		获取我在某时间段创建得计划
		List planList = getCreateUserByIdAndDatePlan(userId,startTime,Datetimes.addDate(endTime, 1));
		Iterator planIt = planList.iterator();
		while(planIt.hasNext()){
		  Plan plan =(Plan)planIt.next();
		  if(plan.getStartTime().getTime()==startTime.getTime()&&plan.getEndTime().getTime()==endTime.getTime()&&!planId.equals(plan.getId())){
			  if(depId==null&&plan.getRefDepartmentId()==null){
				  if(projectId==null&&plan.getRefProjectId()==null){
					  return true;
				  }else if(projectId.equals(plan.getRefProjectId())){
					  return true;
				  }
			  }else if(depId != null && depId.equals(plan.getRefDepartmentId())){
				  if(projectId==null&&plan.getRefProjectId()==null){
					  return true;
				  }else if(projectId.equals(plan.getRefProjectId())){
					  return true;
				  }
			  }		  
		  }
		}
		return false;
	}
	
//	获取我创建得计划
	public List getCreateUserByIdAndDatePlan(Long userId, Date startTime1, Date startTime2) {
		return getPlanDao().findByCreateUserAndTime(userId, startTime1,
				startTime2);
	}

	public List<Plan> getProjectPlan(Long projectId, Long phaseId) throws BusinessException{
		return getPlanDao().findByProjectId(projectId, phaseId);
	}
	
	public List<Plan> getProjectPlanByCondition(String condition,Long projectId, Long phaseId,Map<String,Object> paramMap) throws BusinessException {
		return getPlanDao().findByProjectCondition(condition, projectId, phaseId, paramMap);
	}


	public Map countUserScoupeIsDraftPlan(Long userId, Date date1, Date date2) throws BusinessException {
		// TODO Auto-generated method stub
		// 通过赋权得到的用户列表
		List userList = getPlanUserScopeDao().getPlanUserIdListByRefUserId(userId);
		Iterator userListIt = userList.iterator();
		Map resultMap = new HashMap();
		while(userListIt.hasNext()){
			Long userScopeId = Long.valueOf(String.valueOf(userListIt.next()));				
			resultMap.put(userScopeId,countDraftsmanPlan(userScopeId,date1,date2));
		}
		return resultMap;
	}
	
	public List<Plan> getSenderOrMemberPlan(Long memberId,Long senderId) throws BusinessException{
		return getPlanDao().findByMemberIdAndSenderId(memberId,senderId);
	}
	
	public List<Plan> getSenderOrMemberPlan(Long memberId,Long senderId,int size) throws BusinessException{
		return getPlanDao().findByMemberIdAndSenderId(memberId,senderId,size);
	}
	
    /**
	 * 综合查询
	 * @author jincm 2008-3-19
	 * @param cModel
     * @return List
	 */
    public List<Plan> iSearch(ConditionModel cModel){
    	List<Plan> list = getPlanDao().iSearch(cModel);
    	List<Plan> newList = new ArrayList<Plan>();
    	if(list!=null){
    		for(Plan p : list){
        		if(p!=null){
        			try {
						boolean isView = isAllowView(p.getId(),CurrentUser.get().getId());
						if(isView){
							newList.add(p);
						}
					} catch (Exception e) {
						log.error("", e);
					}
        		}
        	}
    	}
    	
    	return newList;
    }
    
    /**
     * 判断计划是否存在
     * @param planId
     * @return
     */
    public boolean isPlanExist(Long planId){
    	return getPlanDao().get(planId) != null ? true : false;
    }

	public String getPublishStatusByPlanId(Long id) {
		Plan plan  = getPlanDao().get(id);
		if(plan!=null){
			return plan.getPublishStatus();
		}else{
			return "-1";
		}
	}

	public Integer getHomeMyPlanCountByUserId(Long id) {
		// TODO 自动生成方法存根
		return null;
	}

	public List<Plan> getHomeMyPlanPlanList(Long userId) {
		return planDao.findByUserAndTime(userId, Datetimes.getTodayFirstTime(), true, null);
	}

	public Integer getHomePlanManageCountByUserId(Long id) {
		// TODO 自动生成方法存根
		return null;
	}

	public List<Plan> getHomePlanManagePlanList(Long userId, String... preType) {
		return planDao.findByUserAndTime(userId, Datetimes.getTodayFirstTime(), false, preType);
	}

    public List getReplyByPlanid(Long planReply)
    {
        return planReplyDao.findreplyByPlanid(planReply);
    }
    
    @SuppressWarnings("unchecked")
	public HashMap<Long, int[]> getUsersPlanManagerList(List<Long> userIds, Date startTime, Date endTime){
		HashMap<Long, int[]> planMangegerMap = new HashMap<Long, int[]>();
    	//计时器
    	java.util.Calendar calendar = new GregorianCalendar();
    	//当前时间
    	Date nowDate = new Date();
    	//如果用户列表为空，直接返回
    	if(userIds == null) return null;
        //循环得到所有用户的计划管理列表
    	for(Long userId : userIds){
    		int[] nums = new int[10];
    		Date fromDate = Datetimes.parseDate("2000-01-01");
            //首先得到所有主送给用户的计划条数
/*    		
             List<Plan> ccPlans = new ArrayList<Plan>();
    		//起始日期
    		
    		ccPlans.addAll(planDao.findByUserAndTypeAndTime(userId,PlanRelevantUserType.TO_LEADER.getValue(),PlanType.DAY_PLAN.getValue(),fromDate,nowDate));
    		ccPlans.addAll(planDao.findByUserAndTypeAndTime(userId,PlanRelevantUserType.TO_LEADER.getValue(),PlanType.WEEK_PLAN.getValue(),fromDate,nowDate));
    		ccPlans.addAll(planDao.findByUserAndTypeAndTime(userId,PlanRelevantUserType.TO_LEADER.getValue(),PlanType.MONTH_PLAN.getValue(),fromDate,nowDate));
    		ccPlans.addAll(planDao.findByUserAndTypeAndTime(userId,PlanRelevantUserType.TO_LEADER.getValue(),PlanType.ANY_SCOPE_PLAN.getValue(),fromDate,nowDate));*/
    		//下面优化等效如下:
    		List<Plan> ccPlans=planDao.findByUserAndTimeForAnyPlanType(userId, PlanRelevantUserType.TO_LEADER.getValue(), fromDate, nowDate);
    		//取得用户未回复的主送计划条数
    		nums[0] = ccPlans.size() -  planDao.findCcPlanRepliedCount(userId, fromDate, nowDate);		
    		//取得用户本日已回复的主送计划条数
    		nums[1] = planDao.findCcPlanRepliedCount(userId, Datetimes.getTodayFirstTime(), Datetimes.getTodayLastTime());
    		//取得用户本日已发送的计划条数
    		nums[2] = planDao.findSendPlanCount(userId, Datetimes.getTodayFirstTime(), Datetimes.getTodayLastTime());
            //取得用户本周已回复的主送计划条数
    		nums[3] = planDao.findCcPlanRepliedCount(userId, Datetimes.getFirstDayInWeek(calendar.getTime()),Datetimes.getLastDayInWeek(calendar.getTime()));
    		//取得用户本周已发送的计划条数
    		nums[4] = planDao.findSendPlanCount(userId, Datetimes.getFirstDayInWeek(calendar.getTime()),Datetimes.getLastDayInWeek(calendar.getTime()));
    		//取得用户本月已回复的主送计划条数
    		nums[5] = planDao.findCcPlanRepliedCount(userId, Datetimes.getFirstDayInMonth(calendar.getTime()),Datetimes.getLastDayInMonth(calendar.getTime()));
    		//取得用户本月已发送的计划条数
    		nums[6] = planDao.findSendPlanCount(userId, Datetimes.getFirstDayInMonth(calendar.getTime()),Datetimes.getLastDayInMonth(calendar.getTime()));
    		//取得用户时间段内已回复的主送计划条数
    		nums[7] = planDao.findCcPlanRepliedCount(userId, startTime, endTime);
    		//取得用户时间段内已发送的计划条数
    		nums[8] = planDao.findSendPlanCount(userId, startTime, endTime);
    		//取得用户已归档的计划
    		nums[9] = planDao.findByUserAndTypeForPageCount(userId, PlanType.DAY_PLAN.getValue(),true);
    		nums[9] += planDao.findByUserAndTypeForPageCount(userId, PlanType.WEEK_PLAN.getValue(),true);
    		nums[9] += planDao.findByUserAndTypeForPageCount(userId, PlanType.MONTH_PLAN.getValue(),true);
    		nums[9] += planDao.findByUserAndTypeForPageCount(userId, PlanType.ANY_SCOPE_PLAN.getValue(),true);
    		planMangegerMap.put(userId, nums);
    	}
    	return planMangegerMap;
    }
	
	public String[][] getUsersPlanManagerListByTime(String[] userIdsArray, String beginDateStr, String endDateStr){		
		if(userIdsArray == null)	return null;
		String[][] usersPlanManagerList = new String[userIdsArray.length][3];
		Date startTime = Datetimes.parseDatetime(beginDateStr);
		Date endTime = Datetimes.parseDatetime(endDateStr);
		if(startTime == null || endTime == null) return null;
		for(int i = 0;i<userIdsArray.length;i++){
			String userIdStr = userIdsArray[i];
			Long userId = Long.parseLong(userIdStr);
			usersPlanManagerList[i] = new String[3];
			if(userId!=null){
                usersPlanManagerList[i][0] = userIdStr;
	    		//取得用户时间段内已回复的主送计划条数
				List ccPlanReplieds = planDao.findCcPlanReplied(userId, startTime, endTime);
				if(ccPlanReplieds !=null){					
					usersPlanManagerList[i][1] = String.valueOf(ccPlanReplieds.size());
				}else{
					usersPlanManagerList[i][1] = "0";
				}	
	    		//取得用户时间段内已发送的计划条数
				List sendPlans = planDao.findSendPlan(userId, startTime, endTime);
				if(sendPlans != null){
					usersPlanManagerList[i][2] = String.valueOf(sendPlans.size());
				}else{
					usersPlanManagerList[i][2] = "0";
				}				
			}
		}	
		return usersPlanManagerList;
	}
	
	private List<Plan> paginate(List<Plan> list) {
		if (list == null || list.size() == 0)
			return new ArrayList<Plan>();
		Integer first = Pagination.getFirstResult();
		Integer pageSize = Pagination.getMaxResults();
		Pagination.setRowCount(list.size());
		List<Plan> subList = null;
		if (first + pageSize > list.size()) {
			subList = list.subList(first, list.size());
		} else {
			subList = list.subList(first, first + pageSize);
		}
		return subList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Plan> getUserPlanByManagerType(Long userId, int type, Date startTime, Date endTime){
		if(userId == null) return null;
    	//计时器
    	java.util.Calendar calendar = new GregorianCalendar();
    	//当前时间
    	Date nowDate = new Date();
		if(type == 0){
            //首先得到所有主送给用户的计划条数
    		List<Plan> ccPlans = new ArrayList<Plan>();
    		//起始日期
    		Date fromDate = Datetimes.parseDate("2000-01-01");
    		ccPlans.addAll(planDao.findByUserAndTypeAndTime(userId,PlanRelevantUserType.TO_LEADER.getValue(),PlanType.DAY_PLAN.getValue(),fromDate,nowDate));
    		ccPlans.addAll(planDao.findByUserAndTypeAndTime(userId,PlanRelevantUserType.TO_LEADER.getValue(),PlanType.WEEK_PLAN.getValue(),fromDate,nowDate));
    		ccPlans.addAll(planDao.findByUserAndTypeAndTime(userId,PlanRelevantUserType.TO_LEADER.getValue(),PlanType.MONTH_PLAN.getValue(),fromDate,nowDate));
    		ccPlans.addAll(planDao.findByUserAndTypeAndTime(userId,PlanRelevantUserType.TO_LEADER.getValue(),PlanType.ANY_SCOPE_PLAN.getValue(),fromDate,nowDate));
    		//取得用户未回复的主送计划条数
    		List ccPlanReplieds = planDao.findCcPlanReplied(userId, fromDate, nowDate);
    		ccPlans.removeAll(ccPlanReplieds);
    		return this.paginate(ccPlans);
		}else if(type == 1){
    		//取得用户本日已回复的主送计划
    		return planDao.findCcPlanRepliedForPage(userId, Datetimes.getTodayFirstTime(), Datetimes.getTodayLastTime());			
		}else if(type == 2){
    		//取得用户本日已发送的计划
    		return planDao.findSendPlanForPage(userId, Datetimes.getTodayFirstTime(), Datetimes.getTodayLastTime());			
		}else if(type == 3){
    		//取得用户本周已回复的主送计划
    		return planDao.findCcPlanRepliedForPage(userId, Datetimes.getFirstDayInWeek(calendar.getTime()),Datetimes.getLastDayInWeek(calendar.getTime()));			
		}else if(type == 4){
    		//取得用户本周已发送的计划
    		return planDao.findSendPlanForPage(userId, Datetimes.getFirstDayInWeek(calendar.getTime()),Datetimes.getLastDayInWeek(calendar.getTime()));			
		}else if(type == 5){
    		//取得用户本月已回复的主送计划
    		return planDao.findCcPlanRepliedForPage(userId, Datetimes.getFirstDayInMonth(calendar.getTime()),Datetimes.getLastDayInMonth(calendar.getTime()));			
		}else if(type == 6){
    		//取得用户本月已发送的计划
    		return planDao.findSendPlanForPage(userId, Datetimes.getFirstDayInMonth(calendar.getTime()),Datetimes.getLastDayInMonth(calendar.getTime()));			
		}else if(type == 7){
    		//取得用户时间段内已回复的主送计划
    		return planDao.findCcPlanRepliedForPage(userId, startTime, endTime);			
		}else if(type == 8){
    		//取得用户时间段内已发送的计划
    		return planDao.findSendPlanForPage(userId, startTime, endTime);			
		}else if(type == 9){
    		//取得用户已归档的计划
    		List planList = getDraftsmanPlan(userId, PlanType.DAY_PLAN.getValue());
    		planList.addAll(getDraftsmanPlan(userId, PlanType.WEEK_PLAN.getValue()));
    		planList.addAll(getDraftsmanPlan(userId, PlanType.MONTH_PLAN.getValue()));
    		planList.addAll(getDraftsmanPlan(userId, PlanType.ANY_SCOPE_PLAN.getValue()));
    		return this.paginate(planList);
		}
		return null;
	}

    public boolean isAllowView(Long planId, Long userid) throws Exception
    {
        int[] count = getPlanDao().countPlanUser(planId, userid);
        return (count[0] > 0 || count[1] > 0 || count[2] > 0 || count[3] > 0);
    }

    public boolean isAllowReply(Long planId, Long userid) {
        int[] count = getPlanDao().countPlanUser(planId, userid);
        return (count[0] > 0 || count[1] > 0 );
    }

	public IndexInfo getIndexInfo(long id) throws Exception {
		Plan plan = this.getPlanById(id);
		if(plan == null)return null;
		IndexInfo indexInfo=new IndexInfo();
		
		indexInfo.setAppType(ApplicationCategoryEnum.plan);
		indexInfo.setEntityID(id);
		indexInfo.setTitle(plan.getTitle());
		indexInfo.setStartMemberId(plan.getCreateUserId());
		indexInfo.setAuthor(Functions.showMemberName(plan.getCreateUserId()));
		indexInfo.setCreateDate(plan.getCreateTime());
		
		PlanBody planBody = planBodyDao.findUniqueBy("plan.id", id);
		List<PlanRelevantUser> relevantUsers = planRelevantUserDao.findBy("plan", plan);
		List<PlanSummary> planSummarys = planSummaryDao.findBy("plan", plan);
		
		String contentType = planBody.getBodyType();
		Partition partition = partitionManager.getPartition(planBody.getCreateDate(), true);
		String contentPath = this.fileManager.getFolder(planBody.getCreateDate(), false);
		StringBuilder content = new StringBuilder();
		content.append(planBody.getContent());
		for(PlanRelevantUser relevantUser : relevantUsers){
			content.append(Functions.showMemberName(relevantUser.getRefUserId())+" ");
		}
		indexInfo.setKeyword(content.toString());
		if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_HTML.equals(contentType)){
			indexInfo.setContentType(IndexInfo.CONTENTTYPE_HTMLSTR);
			indexInfo.setContent(content.toString());
		} else {
			if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_WORD.equals(contentType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_WORD);
			} else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_OFFICE_EXCEL.equals(contentType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_XLS);
			}
			else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_WORD.equals(contentType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_WPS_Word);
			}
			else if(com.seeyon.v3x.common.constants.Constants.EDITOR_TYPE_WPS_EXCEL.equals(contentType)){
				indexInfo.setContentType(IndexInfo.CONTENTTYPE_WPS_EXCEL);
			}
			indexInfo.setContentID(Long.parseLong(planBody.getContent()));
			indexInfo.setContentAreaId(partition.getId().toString());
			indexInfo.setContentPath(contentPath.substring(contentPath.length()-11)+System.getProperty("file.separator"));
		}
		indexInfo.setHasAttachment(plan.isHasAttachments());
		if(CollectionUtils.isNotEmpty(planSummarys)){
			StringBuilder opinion = new StringBuilder();
			for(PlanSummary planSummary : planSummarys){
				opinion.append(planSummary.getText()+" ");
			}
			indexInfo.setOpinion(opinion.toString());
		}
		List<PlanReply> planReplys = planReplyDao.findBy("plan", plan);
		if(CollectionUtils.isNotEmpty(planReplys)){
			StringBuilder comment = new StringBuilder();
			for(PlanReply planReply : planReplys){
				comment.append(planReply.getRefUserName()+" ");
				comment.append(planReply.getText());
			}
			indexInfo.setComment(comment.toString());
		}
		//权限处理
		List<String> ownerList=new ArrayList<String>();
		if(CollectionUtils.isNotEmpty(relevantUsers)){
			for(PlanRelevantUser relevantUser : relevantUsers){
				ownerList.add(relevantUser.getRefUserId().toString());
			}
		}
		AuthorizationInfo authorizationInfo=new AuthorizationInfo();
		if(ownerList.size() > 0){
			authorizationInfo.setOwner(ownerList);
		}
		indexInfo.setAuthorizationInfo(authorizationInfo);
		
		indexInfo.setStartTime(plan.getStartTime());
		indexInfo.setEndTime(plan.getEndTime());
		IndexUtil.convertToAccessory(indexInfo);
		
		return indexInfo;
	}
	
	public List<Plan> findSendPlanForPage(Long userId, Date startTime, Date endTime, boolean isPaginate) throws Exception {
		return planDao.findSendPlanForPage(userId, startTime, endTime, isPaginate);
	}
	
}