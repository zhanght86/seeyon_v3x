package com.seeyon.v3x.taskmanage.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.taskmanage.domain.TaskFeedback;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.taskmanage.domain.TaskInfoBody;
import com.seeyon.v3x.taskmanage.domain.TaskReply;
import com.seeyon.v3x.taskmanage.utils.ProjectTree;
import com.seeyon.v3x.taskmanage.utils.StatisticCondition;
import com.seeyon.v3x.taskmanage.utils.TaskQueryModel;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.CalEventSyncType;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.ListType;

/**
 * 任务管理主业务逻辑接口
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-25
 */
public interface TaskInfoManager {

	/**
	 * 保存新建的任务信息
	 * @param task	任务信息
	 */
	public void save(TaskInfo task);

	/**
	 * 根据主键ID获取对应的任务信息
	 * @param taskId	任务ID
	 */
	public TaskInfo get(Long taskId);

	/**
	 * 根据主键ID集合删除对应的任务信息
	 * @param ids
	 */
	public void delete(List<Long> ids);

	/**
	 * 获取当前任务的全部汇报记录
	 * @param taskId	任务ID
	 * @param sm		查询模型
	 */
	public List<TaskFeedback> getFeedbacks(Long taskId, SearchModel sm);

	/**
	 * 删除选中的任务汇报记录
	 * @param feedbackIds	任务汇报ID字符串拼接
	 */
	public void deleteFeedbacks(String feedbackIds);

	/**
	 * 获取导出Excel表格所需的数据内容
	 * @param listType	列表类型：我的任务 or 任务管理
	 * @param userId	当前用户ID
	 * @param sqm		简单属性查询模型
	 * @return
	 */
	public DataRecord getDataRecord(ListType listType, Long userId, TaskQueryModel sqm);

	/**
	 * 根据ID获取任务汇报内容
	 * @param feedbackId	任务汇报ID
	 * @return	任务汇报
	 */
	public TaskFeedback getFeedback(Long feedbackId);

	/**
	 * 保存任务汇报内容
	 * @param feeback	任务汇报
	 */
	public void saveFeedback(TaskFeedback feeback);

	/**
	 * 修改任务汇报内容
	 * @param feedback	任务汇报
	 */
	public void updateFeedback(TaskFeedback feedback);

	/**
	 * 获取任务的全部评论回复内容，并按照父子关系设置好对应关系
	 * @param taskId	任务ID
	 * @return	任务回复列表
	 */
	public List<TaskReply> getReplys(Long taskId);
	
	/**
	 * 保存任务回复
	 * @param replyerId		回复者ID
	 * @param taskId	任务ID
	 * @param referenceReplyId	引用的任务回复ID
	 * @parma referenceReplyerId	引用的任务回复者ID
	 * @param replyContent	回复内容
	 * @param sendMsg	是否发送消息
	 * @return
	 */
	public String[] saveTaskReply(Long replyerId, Long taskId, Long referenceReplyId, Long referenceReplyerId, String replyContent, boolean sendMsg);

	/**
	 * 修改任务信息，操作顺序为：先更新任务信息正文内容，再更新任务信息内容，最后进行与日程事件的同步操作
	 * @param task	任务信息
	 * @param calEventSyncType	日程事件的同步操作类型 	
	 */
	public void update(TaskInfo task, CalEventSyncType calEventSyncType); 
	
	/**
	 * 获取任务信息正文内容
	 * @param taskId	任务ID(任务与任务正文为一对一关系，二者主键ID相同)
	 * @return
	 */
	public TaskInfoBody getTaskBody(Long taskId);
	
	/**
	 * 删除任务之前，判断选中的任务中是否包含有子任务，以便随后给出的提示信息不同
	 * @param taskIds	选中的任务ID字符串拼接
	 * @return	是否存在子任务
	 */
	public boolean checkIfChildExist(String taskIds);
	
	/**
	 * 获取任务当前锁定修改人的ID，用于并发编辑时进行校验<br>
	 * 注意：如果锁持有者当前登录时间与加锁时的登录时间不同，视为锁失效
	 * @param taskId	任务ID
	 */
	public Long getLockEditorId(Long taskId);
	
	/**
	 * 修改任务时，进行操作锁定
	 * @param userId	当前用户ID
	 * @param taskId	任务ID
	 */
	public boolean lockWhenEdit(Long userId, Long taskId);
	
	/**
	 * 完成任务修改之后，进行操作解锁
	 * @param taskId	任务ID
	 */
	public void unLockAfterEdit(Long taskId);

	/**
	 * 获取当前任务相关的全部上级、下级任务，用于展现任务树
	 * @param task		当前任务
	 * @return	当前任务的全部上级、下级任务(包括自己)<br>
	 * 			按照任务的逻辑层级深度升序排列(也即第一个元素为根节点)
	 */
	public List<TaskInfo> getTaskTree(TaskInfo task);

	/**
	 * 获取当前用户根据指定类型、属性查询模型等所能查看的任务列表
	 * @param listType		任务列表类型
	 * @param userId		用户ID
	 * @param tqm			简单属性查询模型
	 * @return	任务列表结果集
	 */
	public List<TaskInfo> getTasks(ListType listType, Long userId, TaskQueryModel tqm);

	/**
	 * 获取当前用户根据指定类型、属性查询模型等所能查看的任务列表   为时间管理 huangfj 2012-07-13
	 * @param listType		任务列表类型
	 * @param userId		用户ID
	 * @param startTime 开始时间
	 * @param endTime   结束时间
	 * @return	任务列表结果集
	 */
	public List<TaskInfo> getTasks(ListType listType, Long userId, Date startTime, Date endTime );
	
	/**
	 * 取得我的待办任务（待安排、未安排、处理中）的总数
	 * @param userId
	 * @return
	 */
	public int getCountMyPendingTask(Long userId);

	/**
	 * 根据统计条件获取对应的任务列表
	 * @param sc	统计条件
	 */
	public List<TaskInfo> getTasks(StatisticCondition sc);
	
	/**
	 * 获取项目任务列表
	 * @param projectId			项目ID
	 * @param projectPhaseId	项目阶段ID
	 * @param userId			当前用户ID
	 * @return	所能查看的对应项目任务列表
	 */
	public List<TaskInfo> getProjectTasks(Long projectId, Long projectPhaseId, Long userId);

	/**
	 * 获取工作任务的统计结果
	 * @param membersList	所进行统计所依据的人员ID列表
	 * @param beginDate		统计开始日期
	 * @param endDate		统计结束日期
	 * @return
	 */
	public Map<Long, int[]> getStatisticInfo(List<Long> membersList, Date beginDate, Date endDate);
	
	/**
	 * 获取项目任务统计结果
	 * @param sc	统计条件
	 * @return	项目任务统计结果：key - memberId, value - [指定状态：0-任务总数, 1-实际耗时总和] or [状态依次枚举：任务总数, 实际耗时总和]
	 */
	public Map<Long, Object[]> getProjectStatisticResult(StatisticCondition sc);
	
	/**
	 * 在工作统计界面变动统计日期时，实时变化对应的统计数据，提供给前端AJAX调用
	 * @param members		所进行统计所依据的人员ID列表
	 * @param beginDateStr	统计开始日期字符串
	 * @param endDateStr	统计结束日期字符串
	 * @return 二维数组：String[][]<br>	
	 * 		        第一维：[长度=人员总数]<br>
	 * 		        第二维：<b>[0=人员ID, 1=进行中任务总数, 2=已完成任务总数, 3=已延迟任务总数]</b><br>
	 */
	public String[][] getStatisticInfo(String[] members, String beginDateStr, String endDateStr);
	
	/**
	 * 在任务管理页面，按照项目导航时，获取对应的项目树数据
	 * @param userId	当前用户ID
	 * @param accountId	当前用户登录单位ID
	 */
	public ProjectTree getProjectTree(Long userId, Long accountId) throws Exception;

	/**
	 * 获取项目首页任务栏目所需的任务信息列表(最多8条，同一般栏目数据总数)
	 * @param projectId			任务ID
	 * @param projectPhaseId	当前任务阶段ID
	 * @param userId			当前用户ID
	 * @param leaderOrManager	当前用户是否为领导、负责人或项目助理，这种情况下用户有权查看所有任务，反之则需按照当前用户权限过滤
	 */
	public List<TaskInfo> getProjectTasks4Section(Long projectId, Long projectPhaseId, Long userId, boolean leaderOrManager);
	
	/**
	 * 获取任务信息的全文检索信息，此接口存在的理由是：减少一条sql
	 * @param task	任务信息
	 * @see com.seeyon.v3x.taskmanage.manager.TaskInfoManagerImpl#getIndexInfo(long)
	 */
	public IndexInfo getIndexInfo(TaskInfo task) throws Exception;

}
