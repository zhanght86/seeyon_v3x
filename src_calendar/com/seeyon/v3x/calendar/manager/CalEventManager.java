package com.seeyon.v3x.calendar.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.seeyon.cap.isearch.model.ConditionModel;
import com.seeyon.v3x.calendar.domain.CalContent;
import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.domain.CalEventPeriodicalInfo;
import com.seeyon.v3x.calendar.domain.CalEventPeriodicalRelation;
import com.seeyon.v3x.calendar.domain.CalEventStatistics;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.peoplerelate.manager.PeopleRelateManager;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;

public interface CalEventManager {

	/**
	 * 保存个人事件和委托，安排事件
	 *
	 * @param event
	 * @return
	 */
	public Long save(CalEvent event);

	public Long save(CalEvent event, boolean isNew);

	/**
	 * 根据主键获取事件
	 *
	 * @param id
	 * @return
	 */
	public CalEvent getEventById(Long id);

	public CalEvent getEventByIdNoInit(Long id);

	/**
	 * 根据主键删除事件
	 *
	 * @param id
	 */
	public void deleteById(Long id);

	/**
	 * -----首页---个人空间---个人事件（根据用户ID）
	 *
	 */
	public List<CalEvent> getEventListByUserIdForFirst(Long userId,Boolean personl,Boolean relete);

	/**
	 * 获取首页日程事件栏目中所要显示的日程事件内容，相关约束条件如下：<br>
	 * 1.日程事件状态不能为"已完成"，可以是"待安排"、"已安排"、"处理中"三者之一;<br>
	 * 2.根据用户对日程事件栏目的配置，显示个人创建的日程事件、他人安排/委托的日程事件、或以上二者兼备；<br>
	 * 3.依次显示今日（含跨日）、更晚（含明日）、更早事件；<br>
	 * 5.更晚事件不能晚于今日一周之后，更早事件不能早于今日一周之前。<br>
	 * 6.事件分配原则：<br>
	 *		1)今日事件大于等于viewCount，全部显示今日事件。<br>
	 * 		2)今日事件小于viewCount，今日事件全显示,更早或更晚事件按以下策略显示：<br>
	 * 			1>如果更早或更晚事件都大于剩余条数的一半，则平均分配；
	 * 			2>如果更早或更晚事件小于等于剩余条数的一半，则本身显示全部,其余的显示另一个。<br>
	 * @param viewCount				栏目配置的显示条数
	 * @param userId				当前用户ID
	 * @param self					是否包含自己创建的事件
	 * @param arrangedOrConsigned	是否包含他人安排或委托给自己的事件
	 */
	public List<CalEvent> getEventList4Section(Long userId, Boolean self, Boolean arrangedOrConsigned, int viewCount);
	
	public List<CalEvent> getEventList4Section(Long userId, Boolean self, Boolean arrangedOrConsigned, int viewCount, Date beginDate, Date endDate);
	
	/**
	 * 
	 * 获取首页-日程事件栏目日历样式下所要显示的日程事件内容
	 * 
	 * @param userId
	 * @param self 是否包含自己创建的事件
	 * @param arrangedOrConsigned 是否包含他人安排或委托给自己的事件
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public List<CalEvent> getEventList4Section(Long userId, Boolean self, Boolean arrangedOrConsigned, Date beginDate, Date endDate);

	/**
	 * -----首页---个人空间---个人事件---总数
	 *
	 * @param userId
	 * @return
	 */
	public int getEventListCountByUserIdForFirst(Long userId);

	/**
	 * -----首页---个人空间---他人事件--+--模块主页---共享事件---他人事件
	 *
	 */
	public List<CalEvent> getOtherEventListByUserIdForFirst(User user,
			PeopleRelateManager peopleRelateManager, String condition, String value);

	/**
	 * -----首页---部门空间---部门事件--+--模块主页---共享事件---部门事件
	 *
	 */
	
	public List<CalEvent> getEventListByDeptIds4Section(List<Long> deptId);
	
	public List<CalEvent> getEventListByDeptId(Long deptId);
	
	public List<CalEvent> getEventListByDeptIds(List<Long> deptId, String condition, String value);

	/**
	 * -----模块主页---个人事件（根据用户ID）
	 *
	 */
	public List<CalEvent> getEventListByUserId(Long userId,boolean personal,boolean relete);

	/**
	 * -----模块主页---个人事件----按条件查询（根据用户ID + 某一个参数）
	 *
	 * @param userId
	 * @param type        条件类型 例如：标题 重要程度
	 * @param value       相对的值
	 * @return
	 */

	public List<CalEvent> getEventListByUserId(Long userId, String type,
			String value);

	/**
	 * -----模块主页------共享事件---主列表
	 *
	 */
	public List<CalEvent> getOtherEventListByUserIdForMain(User user,
			PeopleRelateManager peopleRelateManager, String condition, String value);

	/**
	 * -----模块主页------共享事件---项目事件
	 *
	 */
	public List<CalEvent> getItemEventListByUserId(User user,String condition, String value);

	/**
	 * -----模块主页------共享事件---他人事件---左侧选人界面点击后查询列表
	 *
	 */
	public List<CalEvent> getEventListByOtherId(Long otherId,
			PeopleRelateManager p);

	/**
	 *
	 * -----日程视图------日程视图---日，周，月 视图 ---获取除了自己的其他所有人公开给我的事件
	 *
	 */
	public List<CalEvent> getOpenEventListByUserId(Long userId);
	
	/**
	 * 获取我的所有事件或关联人员的我能看到的事件
	 */
	public List<CalEvent> getAllEventListByUserId(Long memberId, Date beginDate, Date endDate) throws Exception;

	/**
	 * 综合查询
	 *
	 * @param model
	 * @return
	 */
	public List<CalEvent> iSearch(ConditionModel cmodel);

	/**
	 * 全文检索
	 *
	 * @param model
	 * @return
	 */

	public IndexInfo getIndexInfo(long id);

	/**
	 * 对关联项目的接口 --------取得所有项目事件
	 *
	 * @param model
	 * @return
	 */
	public List<CalEvent> getItemEventListByUserId(User user, Long projectId, Long phaseId);
	
	/**
	 * 对关联项目的接口 --------取得所有项目事件 (条件查询)
	 *
	 * @param model
	 * @return
	 */
	public List<CalEvent> getItemEventListByCondition(String condition,User user, Long projectId, Long phaseId,Map<String,Object> paramMap);

	/**
	 * 从其他应用增加的日程事件（如回复会议时勾选加入日程事件）
	 *
	 * @param calEvent		设置好的日程事件对象
	 * @param calContent	设置好的日程时间内容对象
	 * @param createUserId	新建日程用户的ID
	 * @return 新建或更新日程事件, 返回新建的或是更新的日程事件的ID
	 */
	public Long saveOrUpdateCalEventFromOtherApp(CalEvent calEvent,CalContent calContent,Long createUserId);

	/**
	 * 根据其他应用的ID删除从其他应用关联的日程事件（如回复会议时勾选加入日程事件）
	 * @param appId			其他应用的ID（如会议的ID）
	 * @param type				应用类型的枚举值（如标识会议的枚举值）
	 * @param createUserId	新建日程用户的ID
	 */
	public void deleteCalEventFromOtherAppId(Long appId,Integer type,Long createUserId);

	/**
	 * 根据其他应用的ID查找对应的日程事件
	 * @param appId			其他应用的ID（如会议的ID）
	 * @param type				应用类型的枚举值（如标识会议的枚举值）
	 * @param createUserId	新建日程用户的ID
	 * @return						对应的日程事件对象
	 */
	public CalEvent isHasCalEventByAppId(Long appId,Integer type,Long createUserId);
	/**
	 * 根据其他应用的ID查找对应的所有日程事件
	 * @param appId          其他应用的ID（如会议的ID
	 * @param type           应用类型的枚举值（如标识会议的枚举值）
	 * @param createUserId   新建日程用户的ID
	 * @return
	 */
	public List<CalEvent> getAllCalEventByAppId(Long appId,Integer type);

	/**
	 * 事件提醒
	 * @param calEvent
	 */
	public void eventRemind(CalEvent calEvent);
	
	/**
	 * 结束前提醒
	 * @param calEvent
	 */
	public void beforEndRemind(CalEvent calEvent);
	
	/**
	 * 事件提醒
	 * @param calEvent
	 * @param isNew
	 */
	public void eventRemind(CalEvent event, boolean isNew);

	/**
	 * 事件取消提醒时，删除对应任务调度
	 * @param eventId
	 */
	public void cancelRemind(Long eventId);

	/**
	 * 根据ID集合获取事件列表
	 * @param eventIds
	 * @return
	 */
	public List<CalEvent> getEventByIds(List<Long> eventIds);

	public void handlePeriodicalEvents();
	
	/**
	 * 查询用户所创建的周期性事件，支持查询
	 * @param userId
	 * @return
	 */
	public List<CalEventPeriodicalInfo> findPeriodical4User(Long userId,String condtion,String textfield1);
	
	/**
	 * 保存周期性事件(生成本次事件)
	 * @param periodicalInfo
	 */
	public void saveOrUpdate(CalEventPeriodicalInfo periodicalInfo,Boolean isNew);
	
	/**
	 * 按照id删除周期性事件，同时删除关联的calevent
	 * @param ids
	 */
	public void deletePeriodicalByIds(List<Long> ids);
	
	public CalEventPeriodicalInfo getPeriodicalInfo(Long id);
	
	//查询 startDate endDate之间，周期性事件可以生成的事件
	public List<CalEvent> preCreateEvent(Long userId,Date startDate,Date endDate,int view);
	
	public void savePeriodicalEventRelition(CalEventPeriodicalRelation relation);
	
	/**
	 * 得到某个时间段、某个完成状态下所有个人事件
	 * 
	 * @param userId
	 * @param beginDate
	 * @param endDate
	 * @param states
	 * @param statistics
	 * @return
	 */
	public List<CalEventStatistics> getEventListByUserId(Long userId,
			Date beginDate, Date endDate, Integer states, Integer statistics);
	
	/**
	 * 得到某个时间段、某个完成状态下、根据统计类型查出的个人事件
	 * 
	 * @param userId
	 * @param beginDate
	 * @param endDate
	 * @param states
	 * @param statistics
	 * @param value
	 * @return
	 */
	public List<CalEvent> getEventListByUserId(Long userId,
			Date beginDate, Date endDate, Integer states, Integer statistics,
			Integer value);
	/**
	 * 根据日程事件id获得周期性信息
	 * @param id
	 * @return
	 */
	public CalEventPeriodicalInfo getPeriodicalInfoByCalEventId(Long id);
	/**
	 * 根据日程事件ID，获得周期性信息和事件的关系
	 * @param id
	 */
	public CalEventPeriodicalRelation getCalEventPeriodicalRelation(Long id);
	/**
	 * 根据一个周期性信息ID，获得所有该周期性生成的事件
	 * @param id
	 * @return
	 */
	public List<CalEvent> getAllPeriodicalEventByPeriodicalInfoId(Long id);
	/**
	 * 根据起止日期、状态和用户ID获得该用户的符合条件的日程事件
	 * @param userId
	 * @param firstResult
	 * 					-1时取所有
	 * @param maxResults
	 * 					-1时取所有
	 * @param beginDate
	 * @param endDate
	 * @param states
	 * @return
	 */
	public List<CalEvent> getEventListByArgu(Long userId,int firstResult,int maxResults, 
			Date beginDate, Date endDate, Integer states);
	
	/**
	 * 根据起止日期、状态和用户ID获得该用户的符合条件的日程事件总数
	 * @param userId
	 * @param beginDate
	 * @param endDate
	 * @param states
	 * @return
	 */
	public int getEventCountByArgu(Long userId,Date beginDate, Date endDate, Integer states);
	
	/**
	 * 将工作任务导入到日程事件视图中
	 * @param task	工作任务
	 */
	public void saveCalEventFromTask(TaskInfo task);
	
	/**
	 * 删除工作任务时，将对应导入的日程事件也一并删除
	 * @param taskId	任务ID
	 */
	public void deleteCalEventFromTask(Long taskId);
	
	/**
	 * 根据eventId获取周期性事件Id
	 * @param eventId
	 */
	public Long getPeriodicalId(Long eventId);
	
}