package com.seeyon.v3x.collaboration.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.domain.ColSummary;
import com.seeyon.v3x.collaboration.domain.ColSuperviseDetail;
import com.seeyon.v3x.collaboration.domain.ColSuperviseLog;
import com.seeyon.v3x.collaboration.domain.ColSupervisor;
import com.seeyon.v3x.collaboration.domain.SuperviseTemplateRole;
import com.seeyon.v3x.collaboration.webmodel.ColSuperviseDealModel;
import com.seeyon.v3x.collaboration.webmodel.ColSuperviseModel;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.organization.domain.V3xOrgMember;

public interface ColSuperviseManager {
	public ColSuperviseDetail getCurrentUserSupervise(int entityType,long entityId, long userId);
	public ColSuperviseDetail getSupervise(int entityType,long entityId);

	public long save(int importantLevel, String summarySubject,String title,long senderId,String senderName,String supervisorNames,long[] supervisorIds,Date awakeDate,int entityType,long entityId,int state,boolean sendMessage, String forwardMemberIdStr);
	public long saveForTemplate(int importantLevel, String summarySubject,String title,long senderId,String senderName,String supervisorNames,long[] supervisorIds,long awakeDate,int entityType,long entityId,boolean sendMessage);
	public void updateDetail(ColSuperviseDetail colSuperviseDetail);

	public void update(int importantLevel, String summarySubject,String title,long senderId,String senderName,String supervisorNames,long[] supervisorIds,Date awakeDate,int entityType,long entityId,int state,boolean sendMessage, String forwardMemberIdStr);
	public void updateForTemplate(int importantLevel, String summarySubject,String title,long senderId,String senderName,String supervisorNames,long[] supervisorIds,long awakeDate,int entityType,long entityId,boolean sendMessage);
	/**
	 * 取得用户督办列表 （默认只取协同督办）
	 * @param userId
	 * @param status
	 * @return
     * @deprecated 被getSuperviseCollListByCondition方法取代，暂未使用到
	 */
	public List<ColSuperviseModel> getMySupervise(long userId,int status);

    /**
     * 根据查询条件取得用户协同督办列表
     * @param condition 条件名称
     * @param field 值1
     * @param field1 值2
     * @param userId 用户ID
     * @param status 状况（未办结、已办结）
     * @return
     */
    public List<ColSuperviseModel> getSuperviseCollListByCondition(String condition, String field, String field1, long userId, int status, List<Long> templeteIds);
    //重写getSuperviseCollListByCondition
    public List<ColSuperviseModel> getSuperviseCollListByCondition(String condition, String field, String field1, long userId, int status, List<Long> templeteIds,Integer secretLevel);
	/**
	 * 取得用户全部督办列表
	 * @param userId
	 * @param status
	 * @return
	 */
	public List<ColSuperviseModel> getMyAllSupervise(long userId,int status);

	/**
	 * 取得用户督办列表 （根据superviseType来判断督办的类型）
	 * @param userId
	 * @param status
	 * @return
	 */
	public List<ColSuperviseModel> getMySupervise(long userId, int status, String condition, String textfield, String textfield1, Integer... superviseType);

	/**
	 * 取得用户督办总数
	 * @param userId
	 * @param status
	 * @return
	 */
	public Integer getMySuperviseCount(long userId,int status);

	/**
	 *
	 * 方法描述：取得督办总数
	 * ignore type
	 */
	public Integer getMySuperviseTotalCount(long userId, int status,Integer... types);

	/**
	 * 更新督办信息，不发送信息，用于协同模板中修改督办信息
	 * @param summarySubject
	 * @param userId
	 * @param userName
	 * @param entityType
	 * @param entityId
	 */
	public void updateOnlySendMessage(int importantLevel, String summarySubject,long userId,String userName,int entityType,long entityId, String forwardMemberIdStr);


	/**
     * 改变督办的流程
     * @param str
     * @param idArr
     * @param typeArr
     * @param nameArr
     * @param accountIdArr
     * @param accountShortNameArr
     * @return
     */

	public String[] changeProcess1(String[] flowProp, String[] policyStr, String summaryId,boolean iscol);
	/**
     * 改变督办的流程
     * @param str
     * @param idArr
     * @param typeArr
     * @param nameArr
     * @param accountIdArr
     * @param accountShortNameArr
     * @return
     */
	public String[] changeProcess(String str, String[] idArr, String[] typeArr, String[] nameArr,
			String[] accountIdArr, String[] accountShortNameArr, String[] selecteNodeIdArr, String[] _peopleArr, String summaryId,String[] condition,String[] nodes,boolean iscol, String[] userExcludeChildDepartmentArr);

	/**
	 * 保存日志
	 * @param superviseId
	 * @param userId
	 * @param receivers
	 * @param content
	 */
	public void saveLog(long superviseId,long userId,List<Long> receivers,String content);

	/**
     * 获取催办总次数
     * @param superviseId
     */
    public int getHastenTimes(long superviseId);

	/**
	 * 通过督办id取到日志列表
	 * @param superviseId
	 * @return
	 */
	public List<ColSuperviseLog> getLogByDetailId(long superviseId);

	/**
	 * 修改提醒时间
	 * @param superviseId
	 * @param userId
	 * @param awakeDate
	 * @param summarySubject
	 */
	public void changeAwakeDate(long superviseId,long userId,Date awakeDate,String summarySubject);

	public ColSuperviseDetail get(long superviseId);

	/**
	 * 更新摘要
	 * @param superviseId
	 * @param content
	 */
	public void updateContent(long superviseId,String content);

	/**
	 * 删除已办结督办
	 * @param userId
	 * @param superviseIds
	 */
	public void deleteSupervised(long userId,String superviseIds);

	/**
	 * 查看办理情况
	 * @param summaryId
	 * @return
	 */
	public List<ColSuperviseDealModel> getAffairModel(long summaryId);

	/**
	 * 通过summaryId更新status
	 * @param summaryId
	 */
	public void updateStatusBySummaryId(long summaryId);

	public List<SuperviseTemplateRole> findSuperviseRoleByTemplateId(long templateId);

	public void saveSuperviseTemplateRole(long templateId, String supervisors);

	public void updateSuperviseTemplateRole(long templateId, String supervisors);

	public long saveForTemplate(ColSuperviseDetail detail,List<SuperviseTemplateRole> roles);

	public void updateForTemplate(ColSuperviseDetail detail,List<SuperviseTemplateRole> roles);

	public void updateStatus(int importantLevel, String summarySubject,long userId,String userName,int entityType,long entityId,int status, String forwardMemberIdStr);

	/**
	 * 撤销流程时，更新督办状态并发送消息给督办人
	 * @param entityId
	 * @param entityType
	 * @param app
	 * @param summarySubject
	 * @param userId
	 * @param userName
	 * @param status
	 * @param repealComment 撤销附言
	 */
	public void updateStatusAndNoticeSupervisor(long entityId,int entityType,ApplicationCategoryEnum app,String summarySubject,long userId,String userName,int status, String messageKey, String repealComment, String forwardMemberIdStr);

	/**
	 * 撤销流程时，更新督办状态
	 * @param entityId
	 * @param entityType
	 * @param status
	 */
	public void updateStatusAndNoticeSupervisorWithoutMes(long entityId,int entityType,int status);

	public List<ColSuperviseModel> getMyAllSuperviseWithoutTemplate(long userId, int status,List<Integer> entityType,int maxCount);

	public int countMyAllSuperviseWithoutTemplate(Long userId,int status,List<Integer> entityType);

	/**
	 * 获取督办人员信息
	 * @param entityType
	 * @param entityId
	 * @return
	 */
	public List<ColSupervisor> getColSupervisorList(int entityType,long entityId);
	public void deleteSupervisorsBySupervisorIdAndDetailId(long detailId, long supervisorId);
	public List<ColSuperviseModel> getMyAllSuperviseForMorePending(long userId, int status);
	public int countMySupervise(long userId,int status ,Integer... superviseType);

    /**
     * 从模板直接拷贝督办信息并保存，不经过页面周转。<br>
     * 目前用于触发新流程
     * @return
     */
	public boolean copyAndSaveSuperviseFromTemplete(V3xOrgMember sender, ColSummary newSummary, Long templeteId) throws Exception;

    /**
     * 删除督办信息
     * @param superviseId
     */
    public void deleteSuperviseById(Long superviseId);

	public String checkColSupervisor(Long summaryId);
	public String checkColSupervisor(Long summaryId, Affair senderAffair);

	/**
	 * 某用户是否是某协同的督办人<br>
	 * 用于判断是否具有查看协同权限
	 * @return
	 */
	public boolean isSupervisor(Long userId, Long summaryId);
	/**
	 * 根据用户和督办状态获取用户所有的事项信息
	 * @param memberId 督办用户ID
	 * @param state 督办状态
	 *                    --5未办结
	 *                    --6已办结
	 * @param firstResult 分页起始值
	 * 					 --为-1时不限制
	 * @param maxResults  每页显示的数据条数
	 * 					 --为-1时不限制
	 * @param entityType 实体类型列表（模板-0、协同-1、公文-2）
	 * @return
	 */
	public List<Affair> getAffairByStatus(Long memberId, int state,int firstResult,int maxResults,List<Integer> entityType );

	/**
	 * 根据summaryId得到某条记录的催办记录
	 * @param summaryId
	 * @return
	 */
	public List<ColSuperviseLog> getColSuperviseLogBySummaryId(long summaryId);

	/**
	 * @author lilong 2012-01-17
	 *  督办栏目查询 */
	public List<ColSuperviseModel> getMyAllSupervise4SectionByImportLevel(long userId, int status,List<Integer> entityType,int maxCount, List<Integer> importantList);
	
	public List<ColSuperviseModel> getMyAllSupervise4SectionByCategory(long userId, int status,List<Integer> entityType,int maxCount, List<String> category) ;
	/**
	 * @author lilong 2012-01-17
	 * 栏目新增显示数字统计方法
	 */
	public Integer getMySuperviseTotalCountByCateOrImportant(long userId, int status,List<Integer> types, List<Integer> importantList);
	
	public ColSuperviseDetail getSuperviseDetailByEntityId(Long entityId);
	public Object getSuperviseModelList(long userId, int status, Map<String,List<String>> queryCondition, List<Integer> entityType, int maxCount,boolean isCount);
}