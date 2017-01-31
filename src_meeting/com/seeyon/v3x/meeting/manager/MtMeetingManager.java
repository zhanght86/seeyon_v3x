package com.seeyon.v3x.meeting.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.common.authenticate.domain.AgentModel;
import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.meeting.MeetingException;
import com.seeyon.v3x.meeting.domain.MtMeeting;

/**
 * 会议的Manager接口
 * @author wolf
 *
 */
public interface MtMeetingManager {
    /**
     * 邀请人员参加会议时创建会议待办事项 Meixd 2010-11-16
     * @param mt
     * @param ownerList
     */
    public void createAffairs(MtMeeting mt, List<Long> ownerList);
    
	/**
	 * 保存会议，并为与会者生成个人待办事项，生成任务调度（会前或准时提醒、会议开始时将会议状态更新为已开始、会议结束后将资源清空），保存与会资源等
	 * @param meeting 待保存的会议
	 * @return 保存后的会议
	 */
	public MtMeeting save(MtMeeting meeting) throws BusinessException;
	
	/**
	 * 用于比较发送会议时，在修改前后与任务调度相关的属性是否发生了变化，辅助任务调度设置<br>
	 * 以便在任务启动时间没有发生变化时，不做删除任务再重新生成任务的无谓操作<br>
	 * 检查三项属性的变化情况，如果发生变化，向会议的备用字段中写入标识值"true"：<br>
	 * 1.<b>会前提醒时间</b>是否发生变化，对应任务：提醒与会人员，标识字段：ext1；<br>
	 * 2.<b>会议开始时间</b>是否发生变化，对应任务：将会议状态改为"已开始"，标识字段：ext2；<br>
	 * 3.<b>会议结束时间</b>是否发生变化，对应任务：清空与会资源，将会议状态改为"已结束"，标识字段：ext3；<br>
	 * @param oldMt  修改前的会议，传入时不会为空，状态为新建、暂存或已发起未召开
	 * @param newMt  修改后的会议，传入时不会为空，状态为已发起未召开
	 */
	public void checkIfFields4QuartzChanged(MtMeeting oldMt, MtMeeting newMt);
	
	/**
     * 发起人发起会议时，自动为其增加日程事件，与会人回执参加时，自动为其增加日程事件
     * @param bean		会议
     * @param userId	人员ID
     */
    public void createCalEvent(MtMeeting bean, Long userId);
    
    /**
     * 删除指定用户由会议自动转发的日程事件
     * @param meetingId 	会议ID
     * @param userId		对应的人员ID
     */
    public boolean deleteCalEvent(Long meetingId, Long userId);
	
	/**
	 * 更新会议
	 */
	public void update(MtMeeting template) throws BusinessException;
	
	/**
	 * 更新会议的状态，用于将已发但未召开的会议删除时，将其状态更新为暂存待发，以便用户对其进行修改
	 * @param meetingId    已发未召开的会议ID
	 * @param state2Update 更新为的状态：暂存待发
	 */
	public void updateState(Long meetingId, int state2Update);
	
	/**
	 * 删除会议，包含了删除会议正文、会议附件、与会人员的待办事项记录、占用资源时间段清空、删除任务调度及删除会议等行为
	 */
	public void delete(Long meetingId) throws BusinessException;
	
	/**
	 * 批量删除会议
	 * @param ids
	 * @throws BusinessException 
	 */
	public void deletes(List<Long> ids) throws BusinessException;
	
	/**
	 * 查询所有会议，支持分页
	 * @return
	 */
	public List<MtMeeting> findAll();
	public List<MtMeeting> findAllforAgent();

	/**
	 * 查询符合条件的会议列表，支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<MtMeeting> findByProperty(String property,Object value);
	
	/**
	 * 查询符合条件的会议列表，不支持分页
	 * @param property
	 * @param value
	 * @return
	 */
	public List<MtMeeting> findByPropertyNoInit(String property, Object value) ;
	public List<MtMeeting> findByPropertyNoInitforAgent(String property, Object value) ;
	
	/**
	 * 根据版面Id获取会议
	 * @param id
	 * @return
	 */
	public MtMeeting getById(Long id);
	
	public MtMeeting getByMtId(Long id);
	/**
	 * 将选中的会议状态更新为已归档，实际的归档在Controller中调用知识管理的接口完成
	 */
	public void pigeonhole(List<Long> ids);
	
	/**
	 * 根据日期查找会议
	 * 
	 * @param startdate
	 * @param enddate
	 * @return
	 * @throws MeetingException
	 */
	public List<MtMeeting> findByDate(String startdate,String enddate) throws MeetingException;
	public List<MtMeeting> findByDateforAgent(String startdate,String enddate) throws MeetingException;
	/**
	 * 将会议添加到个人事项表中
	 * @param mt
	 * @throws BusinessException 
	 */
	public void assignedMeeting(MtMeeting mt,String oper) throws BusinessException;
	
	/**
	 * 获取当前用户可以查看的关联项目会议
	 */
	public List<MtMeeting> getProjectMeeting(Long projectId, Long phaseId, Long currentUserId); 
	
	/**
	 * 条件查询当前用户可以查看的关联项目会议
	 */
	public List<MtMeeting> getProjectMeetingByCondition(String condition,Long projectId, Long phaseId, Long currentUserId,Map<String,Object> paramMap); 
	
	/**
	 * 根据用户Id及计划管理类型、时间取得会议列表（为工作管理提供接口）
	 * 
	 * @param userId	用户Id
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @return
	 */
	public List<MtMeeting> getUserMeetingByManagerType(Long userId, int type, Date startTime, Date endTime);
	
	/**
	 * 取得用户在某一时间段内的会议管理信息（为工作管理提供提供ajax调用方法）
	 * 
	 * @param userIdsArray	用户Id数组
	 * @param beginDateStr	开始时间字符串
	 * @param endDateStr	结束时间字符串
	 * @return
	 */
	public String[][] getUsersMeetingManagerListByTime(String[] userIdsArray, String beginDateStr, String endDateStr);
	
	/**
	 * 取得用户列表的会议管理信息（为工作管理提供接口）
	 * 
	 * @param userIds	用户Id列表
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @return
	 */
	public HashMap<Long, int[]> getUsersMeetingManagerList(List<Long> userIds, Date startTime, Date endTime);
	
	/**
	 * 综合查询
	 */
	public List<MtMeeting> iSearch(ConditionModel cModel);
	
	/**
	 * 判断当前用户是否仍在与会人员中，如果当前用户为会议创建者、主持人或记录人，或在有效与会范围中，则返回true，否则返回false<br>
	 * 如果当前会议选择了所属项目，且用户为该项目成员，也表明在与会范围中可以看到
	 * @param userId
	 * @param bean
	 */
	public boolean isStillInConferees(Long userId, MtMeeting bean);
	
	/**
	 * 得到 所有会议被代理人的 会议列表
	 * @param ids
	 * @return
	 * @deprecated 未进行状态、搜索条件匹配
	 */
	public List<MtMeeting> getAgentedListMeeting(AgentModel ids);
	
	/**
	 * 获取用户想要查看的会议列表（未召开、已召开(包含了已结束和已总结的会议)、暂存待发）
	 * @param stateStr   用户想要查看的会议类型：未召开、已召开(包含了已结束和已总结的会议)、暂存待发
	 * @param userId     当前用户
	 * @param condition  查询条件类型：会议主题、召开时段
	 * @param textfield  查询条件值1
	 * @param textfield1 查询条件值2
	 * added by Meng Yang at 2009-11-14
	 */
	public List<MtMeeting> findMeetings4User(String stateStr, Long userId, String condition, String textfield, String textfield1);
	
	/**
	 * 保存与会对象记录
	 * @param conferees  与会对象选人界面返回数据，为Type|ID以","拼接起来的字符串
	 * @param meetingId  与会对象所要参加的会议ID
	 */
	public void saveMeetingConferees(Long meetingId, String conferees);
	
	/**
	 * 会议创建者删除会议记录时，将对应的与会对象记录也一起删除<br>
	 * 会议创建者修改并保存会议记录时，与会对象有变动时，将旧有与会对象先删除（之后再保存新的与会对象记录）<br>
	 * @param meetingId
	 */
	public void deleteConferees(Long meetingId);
	
	/**
	 * 获取会议创建者、主持人、记录人之外的全体与会人员ID集合，未对代理人进行处理
	 * @param confereesStr   与会人Type|Ids
	 * @param creatUser		 会议发起人
	 * @param emcee			 会议主持人
	 * @param recorder       会议记录人
	 * @see #getMsgReceivers(String, Long, Long, Long)  其中对代理人进行了处理
	 */
	public List<Long>  getConfereeIds(String confereesStr, Long creatUser, Long emcee, Long recorder);
	
	/**
	 * 判断会议是否还可进行修改（已发起未召开的会议如果已经到达开始时间，此时不再允许修改）
	 * 用于前端AJAX调用
	 * @param meetingId
	 */
	public boolean canEditMeeting(Long meetingId);
	
	/**
     * 获取会议消息发送对象集合：主持人、记录人、与会人员，其中的人员如果设定了代理人员，也将代理人员加入
     */
    public List<Long> getMsgReceivers(MtMeeting meeting) throws BusinessException;
    
    /**
     * 获取会议消息发送对象集合：主持人、记录人、与会人员，其中的人员如果设定了代理人员，也将代理人员加入
     */
	public List<Long> getMsgReceivers(String confereesStr, Long creatUser, Long emcee, Long recorder) throws BusinessException;
	
	/**
     * 获取会议消息发送对象Map：主持人、记录人、与会人员，其中的人员如果设定了代理人员，也将代理人员加入
     */
    public Map<String, List> getMsgReceiversWithAgentMap(String confereesStr, Long creatUser, Long emcee, Long recorder) throws BusinessException;
	
    /**
     * 获取会议消息发送对象Map：主持人、记录人、与会人员，其中的人员如果设定了代理人员，也将代理人员加入
     */
	public Map<String, List> getMsgReceiversWithAgentMap(MtMeeting bean) throws BusinessException;
    
    /**
     * 获取会议会前提醒消息发送对象集合，包括与会人员及其代理人<br>
     * 回执了不参加会议的与会人员不发送消息，回执为待定和参加或未回执的与会人员才发送消息<br>
     * @param meeting
     */
	public Map<String, List> getRemindMsgReceivers(MtMeeting meeting) throws BusinessException;
	
	/**
	 * 全文检索入库
	 */
	public IndexInfo getIndexInfo(long id) throws Exception;

	/***
	 * 根据红杉树传过来的视频会议ID。查询A8系统视频会议实体
	 * @param meetingId
	 * @return MtMeeting
	 * @author radishlee 2011-11-3
	 */
	public List getMeetingByInfowarelabMeetingId(String meetingId);
	
	/***
	 * 根据协同ID。查询A8系统视频会议实体
	 * @param summaryId
	 * @return MtMeeting
	 * @author radishlee 2012-2-14
	 */
	public List getMeetingBySummaryId(Long summaryId);
	
	public List<MtMeeting> findAllMeetings4User(Long userId, Date beginDate, Date endDate);
}