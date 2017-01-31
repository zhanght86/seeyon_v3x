package com.seeyon.v3x.plan.manager;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MultiMap;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.plan.dao.PlanDao;
import com.seeyon.v3x.plan.domain.Plan;
import com.seeyon.v3x.plan.domain.PlanBody;
import com.seeyon.v3x.plan.domain.PlanReply;
import com.seeyon.v3x.plan.domain.PlanSummary;

public interface PlanManager {
	
	/**
	 * 获取当前登录用户对查看计划的角色：主、抄、告或其他
	 * @param planId	计划
	 * @param userId	当前登录用户ID
	 * @return 角色类型
	 */
	public  String getUserRole(Long planId, Long userId);
	
	/**
	 * 根据用户ID得到首页“我的计划”总数count
	 * 
	 * @param id 用户Id
	 * @return 首页“我的计划”总数count
	 */
	public abstract Integer getHomeMyPlanCountByUserId(Long id);
	
	/**
	 * 根据用户ID得到首页“计划管理”总数count
	 * 
	 * @param id 用户Id
	 * @return 首页“计划管理”总数count
	 */
	public abstract Integer getHomePlanManageCountByUserId(Long id);
	
	/**
	 * 根据用户ID取得首页“我的计划”计划List
	 * 
	 * @param id 用户Id
	 * @return 首页“我的计划”计划List
	 */
	public abstract List<Plan> getHomeMyPlanPlanList(Long id);
	
	/**
	 * 根据用户ID取得首页“计划管理”计划List
	 * 
	 * @param id 用户Id
	 * @param pruType 计划人员类型
	 * @return 首页“计划管理”计划List
	 */
	public abstract List<Plan> getHomePlanManagePlanList(Long id,String... preType);
	
	/**
	 * 根据计划ID得到计划的发布状态<br>（1:草稿   2:已发布   3:已总结  -1:对应计划不存在）
	 * 
	 * @param id 计划ID
	 * @return 1，草稿   2，已发布   3，已总结  -1，对应计划不存在
	 */
	public  abstract String getPublishStatusByPlanId(Long id);
	
	/**
	 * 添加计划
	 * 
	 * @param template
	 *            计划对象
	 */
	public abstract void addPlan(Plan plan);

	/**
	 * 取回计划列表
	 * 
	 * @return 计划列表
	 */
	public abstract List listPlan();

	/**
	 * 修改计划
	 * 
	 * @param template
	 *            计划对象
	 */
	public abstract void updatePlan(Plan plan);

	/**
	 * 通过主键取计划
	 * 
	 * @param id
	 *            主键
	 * @return 计划
	 */
	public abstract Plan getPlanByPk(Long id);

	/**
	 * 通过主键删除计划
	 * 
	 * @param id
	 *            主键
	 */
	public abstract void deletePlan(Long id);

	/**
	 * 通过主键数组删除计划
	 * 
	 * @param ids
	 *            主键数组
	 */
	public abstract void deletePlans(Long[] ids);

	public abstract void setPlanDao(PlanDao planDao);

	/**
	 * 根据用户、类型、计划类型、日期取得计划
	 * 
	 * @param userId
	 * @param planType
	 * @param startTime1
	 * @param startTime1
	 * @return
	 */
	public abstract List getPlan(Long userId, String relevantUserType,
			String planType, Date startTime1, Date startTime2);
	
	/**
	 * 根据用户、类型、计划类型、日期取得计划分页
	 * 
	 * @param userId
	 * @param planType
	 * @param startTime1
	 * @param startTime1
	 * @return
	 */
	public abstract List getPlanForPage(Long userId, String relevantUserType,
			String planType, Date startTime1, Date startTime2);
	
	/**
	 * 根据用户、类型、计划类型取得计划
	 * 
	 * @param userId
	 * @param planType
	 * @param startTime1
	 * @param startTime1
	 * @return
	 */
	public abstract List getPlan(Long userId, String relevantUserType,
			String planType);

	// /**
	// * 根据用户时间
	// *
	// * @param userId
	// * @param planType
	// * @param startTime1
	// * @param startTime2
	// * @return
	// */
	// public abstract List getPlan(Long userId, String planType, Date
	// startTime1,
	// Date startTime2);

	/**
	 * 得到计划发起人的相关计划，“我的计划”使用
	 * 
	 * @param userId
	 * @param planType
	 * @param startTime1
	 * @param startTime2
	 * @return
	 */
	public abstract List getIsDraftsmanPlan(Long userId, String planType,
			Date startTime1, Date startTime2);
	
	/**
	 * 分页显示计划发起人的相关计划，“我的计划”使用
	 * 
	 * @param userId
	 * @param planType
	 * @param startTime1
	 * @param startTime2
	 * @return
	 */
	public abstract List getIsDraftsmanPlanForPage(Long userId, String planType,
			Date startTime1, Date startTime2);
	
	/**
	 * 分页显示计划发起人的相关计划，“我的计划”使用    xut   结构调整后方法--------12-12
	 * @param userId
	 * @param planType
	 * @param startTime1
	 * @param startTime2
	 * @return
	 */
	public abstract List getMyPlanByTypeAndDateForPage(Long userId, String planType, Date startTime1, Date startTime2);

	/**
	 * 得到计划发起人的相关计划
	 * 
	 * @param userId
	 * @param startTime1
	 * @param startTime2
	 * @return
	 */
	public abstract List getIsDraftsmanPlan(Long userId, Date startTime1,
			Date startTime2);

	/**
	 * 得到非计划发起人的相关计划，“计划管理”使用
	 * 
	 * @param userId
	 * @param planType
	 * @param startTime1
	 * @param startTime2
	 * @return
	 */
	public abstract List getNotDraftsmanPlan(Long userId, String planType,
			Date startTime1, Date startTime2);
	
	/**
	 * 得到非计划发起人的相关计划分页，“计划管理”使用
	 * 
	 * @param userId
	 * @param planType
	 * @param startTime1
	 * @param startTime2
	 * @return
	 */
	public abstract List getNotDraftsmanPlanForPage(Long userId, String planType,
			Date startTime1, Date startTime2);
//~~~~~~~~~~~~~~~~~~~~~~~
	/**
	 * 得到计划发起人的相关计划分页，部门空间使用
	 * 
	 * @param userId
     * @param departmentId 部门Id
	 * @param planType
	 * @param startTime1
	 * @param startTime2
	 * @return
	 */
	public abstract List getDeptNotDraftsmanPlanForPage(int pageSize, Long departmentId, Long userId, Date startTime1);
	
	/**
	 * 部门空间更多页面显示
	 * 
	 * @param userId
     * @param departmentId 部门Id
	 * @param planType
	 * @param startTime1
	 * @param startTime2
	 * @return
	 */
	public abstract List getDeptMorePlanList( Long departmentId, Long userId, Date startTime1 , boolean isFromDeptSpaceManage);

	/**
	 * 部门计划取消发布
	 * 
	 * @param planIds
	 * @return
	 */
	public abstract int cancelPublishPlan(Set<Long> planIds);



//~~~~~~~~~~~~~~~~~~~~~~`	
	
	/**
	 * 添加计划总结
	 * 
	 * @param template
	 *            计划对象
	 */
	public abstract void addPlanSummary(PlanSummary planSummary);

	/**
	 * 添加计划回复
	 * 
	 * @param template
	 *            计划对象
	 */
	public abstract void addPlanReply(PlanReply planReply);

	/**
	 * 根据发起人统计计划信息
	 * 
	 * @param userId
	 *            计划发起人id
	 * @param startTime1
	 *            时间段1
	 * @param startTime2
	 *            时间段2
	 */
	public abstract Map countDraftsmanPlan(Long userId, Date startTime1,
			Date startTime2);
	
	
	/**
	 * 根据用户Id得到用户查看范围内的所有计划信息
	 * 
	 * @param userId
	 *            计划发起人id
	 */
	public abstract Map countUserScopePlan(Long userId,Date date1,Date date2);

//	/**
//	 * 根据用户列表
//	 * 
//	 * @param userIdList
//	 * @param startTime1
//	 * @param startTime2
//	 * @return
//	 */
//	public abstract Map countPlanByUserList(List userIdList, Date startTime1,
//			Date startTime2);

	/**
	 * 根据用户id，进行统计
	 * 
	 * @param userId
	 * @param startTime1
	 * @param startTime2
	 * @return
	 */
	public abstract Map countUserPlan(Long userId, Date startTime1,
			Date startTime2);

	/**
	 * 根据用户id、时间、用户名列表
	 * @param userId
	 * @param memberList 根据用户名查询出的列表
	 * @param startTime1
	 * @param startTime2
	 * @return
	 */
	public abstract Map countUserPlan(Long userId,
			List<V3xOrgMember> memberList, Date startTime1, Date startTime2);

	/**
	 * 添加计划正文实体
	 * 
	 * @param planBody
	 */
	public abstract void addPlanBody(PlanBody planBody);

	/**
	 * 更新计划正文实体
	 * 
	 * @param planBody
	 */
	public void updatePlanBody(PlanBody planBody);

	/**
	 * 删除计划相关用户
	 * 
	 * @param planId
	 */
	public void deletePlanRelevantUserByPlanId(Long planId);

	/**
	 * 
	 * 根据ID得到计划，加载正文、回复、总结
	 * 
	 * @param planId
	 *            计划ID
	 * @param plan
	 *            out参数，计划实体
	 * @param planReply
	 *            out参数，计划回复
	 * @param planSummary
	 *            out参数，计划总结
	 */
	public Plan getPlanLoadBodyAndReplyAndSummary(Long planId,
			MultiMap planReply);

	/**
	 * 判断是否拥有回复权限
	 * 
	 * @param planId
	 * @param userid
	 * @return
	 */
	public boolean isAllowReply(Long planId, Long userid);

    public int[] countPlanUser(Long planId, Long userid);
    
	/**
	 * 根据计划id删除计划正文
	 * 
	 * @param planId
	 */
	public abstract void deletePlanBodyByPlanId(Long planId);

	/**
	 * 根据计划id删除计划回复
	 * 
	 * @param planId
	 */
	public abstract void deletePlanReplyByPlanId(Long planId);

	/**
	 * 根据计划id删除计划总结
	 * 
	 * @param planId
	 */
	public abstract void deletePlanSummaryByPlanId(Long planId);

	/**
	 * 根据登录用户id、用户查看范围列表，开始时间、结束时间查询
	 * 
	 * @param loginUserId
	 *            登录用户id
	 * @param userIds
	 *            查看范围id
	 * @param startTime1
	 *            开始时间
	 * @param startTime2
	 *            结束时间
	 * @return
	 */
	public abstract List<Plan> getPlan(Long loginUserId,
			List<V3xOrgMember> userList, String planType, Date startTime1,
			Date startTime2);

	/**
	 * 根据用户id集合，取得用户名
	 * 
	 * @param userIds
	 * @return
	 */
	public Map<Long, String> getUserName(Collection<Long> userIds);

	// /**
	// * 根据用户、用户状态（主送、抄送、告知）和时间段得到计划相关集合
	// *
	// * @param userId
	// * @param userType
	// * @param
	// * @param startTime1
	// * @param startTime2
	// * @return
	// */
	// public abstract List getPlan(Long userId,
	// String userType,String planType, Date startTime1, Date startTime2);

	/**
	 * 归档接口使用
	 * 
	 * @see com.seeyon.v3x.plan.manager.PlanManager#getPlanByPk(Long)
	 * @param id
	 * @return
	 */
	public abstract Plan getPlanById(Long id);
	
	
	/**
	 * 根据用户Id和计划类型查出开始日期最晚的计划
	 * added by paul 5/15/07
	 * @param userId
	 * @param planType
	 * @return
	 */
	public abstract Plan getPersonalLastPlan(Long userId, String planType);
	
	public abstract Plan getPersonalLastDatePlan(Long userId, String planType);
	
	/**
	 * 通过主键取计划回复
	 * added by paul 5/15/07
	 * @param planReplyId
	 * @return
	 */
	public abstract PlanReply getPlanReplyByPk(Long planReplyId);
	
	/**
	 * 根据用户id及计划类型得到计划总条数 “计划归档”使用
	 * 
	 * @param userId
	 * @param planType
	 * @return
	 */
	public abstract int countDraftsmanPlan(Long userId, String planType);
	
	/**
	 * 根据用户id及计划类型查询当前页的所有计划列表 “计划归档”使用
	 * 
	 * @param userId
	 * @param planType
	 * @return
	 */	
	public abstract List getDraftsmanPlan(Long userId, String planType);
	
	/**
	 * 根据用户id得到用户查看范围内的所有用户
	 * 
	 * @param userId
	 * @return
	 */	
	public abstract Map getUserScope(Long userId);
	
	/**
	 * 判断计划是否重复
	 * 
	 * @param planId
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @param depId
	 * @param projectId 
	 * @return
	 */	
	public boolean isPlanRepeated(Long planId,Long userId,Date startTime,Date endTime, Long depId, Long projectId);

	/**
	 * 获取关联项目计划
	 * @param projectId
	 * @throws BusinessException 
	 */
	public List<Plan> getProjectPlan(Long projectId, Long phaseId) throws BusinessException;
	
	/**
	 * 条件查询关联项目计划
	 * @param projectId
	 * @throws BusinessException 
	 */
	public List<Plan> getProjectPlanByCondition(String condition,Long projectId, Long phaseId,Map<String,Object> paramMap) throws BusinessException;
	
	/**
	 * 统计用户被授权查看的用户的"我的计划"里的计划
	 * 
	 * @param userId 授权查看用户
	 * @param date1 开始日期
	 * @param date1 结束日期
	 * @return
	 */
	public abstract Map countUserScoupeIsDraftPlan(Long userId, Date date1, Date date2) throws BusinessException;
	
	/**
	 * 
	 * @param memberId	人员id
	 * @param senderId	发送者id
	 * @return
	 * @throws BusinessException
	 */
	public List<Plan> getSenderOrMemberPlan(Long memberId,Long senderId) throws BusinessException;
	
	public List<Plan> getSenderOrMemberPlan(Long memberId,Long senderId,int size) throws BusinessException;
	
    /**
	 * 综合查询
	 * @author jincm 2008-3-19
	 * @param cModel
     * @return List
	 */
    public List<Plan> iSearch(ConditionModel cModel);
    
    
    /**
     * 判断计划是否存在
     * @author xut 2008-3-27
     * @param planId
     * @return
     */
    public boolean isPlanExist(Long planId);
    
    public List getReplyByPlanid(Long planReply);
    
    public boolean  isAllowView(Long planId, Long userid) throws Exception;
    
        
	/**
	 * 取得用户列表的计划管理信息（为工作管理提供接口）
	 * 
	 * @param userIds	用户Id列表
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @return
	 */
    public HashMap<Long, int[]> getUsersPlanManagerList(List<Long> userIds, Date startTime, Date endTime);
 
	/**
	 * 取得用户在某一时间段内的计划管理信息（为工作管理提供提供ajax调用方法）
	 * 
	 * @param userIdsArray	用户Id数组
	 * @param beginDateStr	开始时间字符串
	 * @param endDateStr	结束时间字符串
	 * @return
	 */
    public String[][] getUsersPlanManagerListByTime(String[] userIdsArray, String beginDateStr, String endDateStr);
    
	/**
	 * 根据用户Id及计划管理类型、时间取得计划列表（为工作管理提供接口）
	 * 
	 * @param userId	用户Id
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @return
	 */   
    public List<Plan> getUserPlanByManagerType(Long userId, int type, Date startTime, Date endTime);
    
    
    /**
     * 获取某用户某时间段内发送的非草稿状态的计划
     * 
     * @param userId
     * @param startTime
     * @param endTime
     * @param isPaginate
     * @return
     * @throws Exception
     */
	public List<Plan> findSendPlanForPage(Long userId, Date startTime, Date endTime, boolean isPaginate) throws Exception;
    
}