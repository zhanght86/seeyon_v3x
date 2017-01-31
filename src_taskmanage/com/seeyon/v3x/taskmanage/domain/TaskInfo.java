package com.seeyon.v3x.taskmanage.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.seeyon.v3x.calendar.util.Constants;
import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.project.domain.ProjectPhase;
import com.seeyon.v3x.taskmanage.utils.TaskConstants;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.CalEventSyncType;
import com.seeyon.v3x.taskmanage.utils.TaskUtils.RemindType;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 任务信息(命名为TaskInfo，避免与系统中已有的、成打的Task混淆，君莫悲...)
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-25
 */
public class TaskInfo extends BaseModel implements Comparable<TaskInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1620667030207809176L;
	
	/*----------------------persistent fields-----------------------*/
	/**
	 * 任务名称
	 */
	private String subject;
	/**
	 * 计划开始时间
	 */
	private Date plannedStartTime;
	/**
	 * 计划结束时间
	 */
	private Date plannedEndTime;
	/**
	 * 实际开始时间
	 */
	private Date actualStartTime;
	/**
	 * 是否为全日制事件
	 */
	private boolean fullTime;
	/**
	 * 实际结束时间
	 */
	private Date actualEndTime;
	/**
	 * 任务开始前提醒时间差
	 */
	private Integer remindStartTime;
	/**
	 * 任务结束前提醒时间差
	 */
	private Integer remindEndTime;
	/**
	 * 任务创建人
	 */
	private Long createUser;
	/**
	 * 任务创建时间
	 */
	private Date createTime;
	/**
	 * 任务负责人(可以设为多个)
	 */
	private String managers;
	/**
	 * 任务最后修改人
	 */
	private Long updateUser;
	/**
	 * 任务最后修改时间
	 */
	private Date updateTime;
	/**
	 * 重要程度
	 */
	private Integer importantLevel;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 计划工时
	 */
	private float plannedTaskTime;
	/**
	 * 实际耗时
	 */
	private float actualTaskTime;
	/**
	 * 完成率
	 */
	private float finishRate;
	/**
	 * 风险级别
	 */
	private Integer riskLevel;
	/**
	 * 是否有附件
	 */
	private boolean hasAttachments;
	/**
	 * 是否导入日程视图
	 */
	private boolean importToCalendar;
	/**
	 * 上级任务ID
	 */
	private Long parentTaskId = -1l;
	/**
	 * 上级任务名称
	 */
	private String parentTaskSubject;
	/**
	 * 任务树中的逻辑层级地址
	 */
	private String logicalPath;
	/**
	 * 任务树中的逻辑层级深度(限制最高10级)
	 */
	private int logicalDepth;
	/**
	 * 任务参与人
	 */
	private String participators;
	/**
	 * 关联项目ID
	 */
	private Long projectId = TaskConstants.PROJECT_NONE;
	/**
	 * 关联项目阶段ID
	 */
	private Long projectPhaseId = TaskConstants.PROJECT_PHASE_ALL;
	
	public TaskInfo() {}
	
	/*--------------------------setter/getter-------------------------*/
	public Long getProjectPhaseId() {
		return projectPhaseId;
	}
	public void setProjectPhaseId(Long projectPhaseId) {
		this.projectPhaseId = projectPhaseId;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public String getLogicalPath() {
		return logicalPath;
	}
	public void setLogicalDepth(int logicalDepth) {
		this.logicalDepth = logicalDepth;
	}
	public int getLogicalDepth() {
		return this.logicalDepth;
	}
	public void setLogicalPath(String logicalPath) {
		this.logicalPath = logicalPath;
	}
	public String getParentTaskSubject() {
		return parentTaskSubject;
	}
	public void setParentTaskSubject(String parentTaskSubject) {
		this.parentTaskSubject = parentTaskSubject;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Date getPlannedStartTime() {
		return plannedStartTime;
	}
	public void setPlannedStartTime(Date plannedStartTime) {
		this.plannedStartTime = plannedStartTime;
	}
	public Date getPlannedEndTime() {
		return plannedEndTime;
	}
	public void setPlannedEndTime(Date plannedEndTime) {
		this.plannedEndTime = plannedEndTime;
	}
	public boolean isFullTime() {
		return fullTime;
	}
	public void setFullTime(boolean fullTime) {
		this.fullTime = fullTime;
	}
	public Date getActualStartTime() {
		return actualStartTime;
	}
	public void setActualStartTime(Date actualStartTime) {
		this.actualStartTime = actualStartTime;
	}
	public Date getActualEndTime() {
		return actualEndTime;
	}
	public void setActualEndTime(Date actualEndTime) {
		this.actualEndTime = actualEndTime;
	}
	public Integer getRemindStartTime() {
		return remindStartTime;
	}
	public void setRemindStartTime(Integer remindStartTime) {
		this.remindStartTime = remindStartTime;
	}
	public Integer getRemindEndTime() {
		return remindEndTime;
	}
	public void setRemindEndTime(Integer remindEndTime) {
		this.remindEndTime = remindEndTime;
	}
	public Long getCreateUser() {
		return createUser;
	}
	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getManagers() {
		return managers;
	}
	public void setManagers(String managers) {
		this.managers = managers;
	}
	public Long getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(Long updateUser) {
		this.updateUser = updateUser;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getImportantLevel() {
		return importantLevel;
	}
	public void setImportantLevel(Integer importantLevel) {
		this.importantLevel = importantLevel;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public float getPlannedTaskTime() {
		return plannedTaskTime;
	}
	public void setPlannedTaskTime(float plannedTaskTime) {
		this.plannedTaskTime = plannedTaskTime;
	}
	public float getActualTaskTime() {
		return actualTaskTime;
	}
	public void setActualTaskTime(float actualTaskTime) {
		this.actualTaskTime = actualTaskTime;
	}
	public float getFinishRate() {
		return finishRate;
	}
	public void setFinishRate(float finishRate) {
		this.finishRate = finishRate;
	}
	public Integer getRiskLevel() {
		return riskLevel;
	}
	public void setRiskLevel(Integer riskLevel) {
		this.riskLevel = riskLevel;
	}
	public boolean isHasAttachments() {
		return hasAttachments;
	}
	public void setHasAttachments(boolean hasAttachments) {
		this.hasAttachments = hasAttachments;
	}
	public boolean isImportToCalendar() {
		return importToCalendar;
	}
	public void setImportToCalendar(boolean importToCalendar) {
		this.importToCalendar = importToCalendar;
	}
	public Long getParentTaskId() {
		return parentTaskId;
	}
	public void setParentTaskId(Long parentTaskId) {
		this.parentTaskId = parentTaskId;
	}
	public String getParticipators() {
		return participators;
	}
	public void setParticipators(String participators) {
		this.participators = participators;
	}

	/*-----------------transient fields/methods-----------------*/
	/**
	 * 任务内容(实际存储在TaskInfoContent中)
	 */
	private String content;
	
	/**
	 * 判断当前任务是否具备开始前提醒设置
	 */
	public boolean remindBeforeStart() {
		return this.remindStartTime != TaskConstants.NO_REMIND;
	}

	/**
	 * 判断当前任务是否具备结束前提醒设置
	 */
	public boolean remindBeforeEnd() {
		return this.remindEndTime != TaskConstants.NO_REMIND;
	}
	
	/**
	 * 根据提醒类型获取对应的提醒时间
	 * @param remindType	提醒类型：任务开始前提醒 or 任务结束前提醒
	 * @return
	 */
	public Date getRemindTime(RemindType remindType) {
		switch(remindType) {
		case BeforeStart :
			return Datetimes.addMinute(this.plannedStartTime, -this.remindStartTime);
		case BeforeEnd :
			return Datetimes.addMinute(this.plannedEndTime, -this.remindEndTime);
		}
		throw new IllegalArgumentException("非法提醒类型[RemindType=" + remindType.name() + "]");
	}
	
	/**
	 * 任务中的日期类型枚举：计划开始、计划结束、实际开始、实际结束
	 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-2-24
	 */
	public static enum TaskDateEnum {
		PlannedStart,
		PlannedEnd,
		ActualStart,
		ActualEnd
	}
	
	/**
	 * 根据当前任务是否全天事件获取格式化后的计划开始或结束日期
	 * @param dateEnum	日期类型
	 */
	public String getFormatDate(TaskDateEnum dateEnum) {
		String pattern = this.fullTime ? Datetimes.dateStyle : Datetimes.datetimeStyle;
		Date date = null;
		switch(dateEnum) {
		case PlannedStart : date = this.plannedStartTime; break;
		case PlannedEnd : date = this.plannedEndTime; break;
		case ActualStart : date = this.actualStartTime; break;
		case ActualEnd : date = this.actualEndTime; break;
		}
		return Datetimes.format(date, pattern);
	}
	
	/**
	 * 获取在修改任务前后，对应日程事件的同步操作类型
	 * @param oldImport2Cal		修改前：是否导入日程事件视图
	 * @return	日程事件同步操作类型
	 * @see CalEventSyncType
	 */
	public CalEventSyncType getCalEventSyncAction(boolean oldImport2Cal) {
		if(oldImport2Cal && this.importToCalendar)
			return CalEventSyncType.Update;
		
		if(!oldImport2Cal && this.importToCalendar)
			return CalEventSyncType.Save;
		
		if(oldImport2Cal && !this.importToCalendar)
			return CalEventSyncType.Delete;
		
		return CalEventSyncType.None;
	}
	
	/**
	 * 导入日程事件视图时，将任务的重要程度转换为日程事件的重要紧急程度
	 * @return	日程事件的重要紧急程度
	 */
	public Integer parse2CalEventSignifyType() {
		switch(this.importantLevel) {
		case TaskConstants.IMPORTANCE_COMMON : return Constants.SIGNIFY_TYPE_4;
		case TaskConstants.IMPORTANCE_IMPORTANT : return Constants.SIGNIFY_TYPE_3;
		case TaskConstants.IMPORTANCE_VERY_IMPORTANT : return Constants.SIGNIFY_TYPE_1;
		}
		
		return Constants.SIGNIFY_TYPE_4;
	}
	
	/**
	 * 获取创建人、负责人和参与人的ID字符串集合，供全文检索使用
	 */
	public List<String> getAllOwners() {
		StringBuilder sb = new StringBuilder(this.getManagers());
		if(Strings.isNotBlank(this.getParticipators()))
			sb.append(',' + this.getParticipators());
        
        sb.append(',' + this.getCreateUser().toString());
        
        return Arrays.asList(sb.toString().split(","));
	}
	
	/**
	 * 子(分解出的下级)任务
	 */
	private List<TaskInfo> children;
	/**
	 * 父(上级)任务
	 */
	private TaskInfo parent;
	/**
	 * 甘特图ID
	 */
	private int ganttId;
	
	/**
	 * 增加一个子任务
	 * @param child	子任务
	 */
	public void addChild(TaskInfo child) {
		if(this.children == null)
			this.children = new ArrayList<TaskInfo>();
		this.children.add(child);
		child.setParent(this);
	}
	
	/**
	 * 判断当前任务是否有子任务
	 * @return	是否有子任务
	 */
	public boolean hasChild() {
		return CollectionUtils.isNotEmpty(this.children);
	}

	public List<TaskInfo> getChildren() {
		return children;
	}
	public void setChildren(List<TaskInfo> children) {
		this.children = children;
	}
	public TaskInfo getParent() {
		return parent;
	}
	public void setParent(TaskInfo parent) {
		this.parent = parent;
	}
	public int getGanttId() {
		return ganttId;
	}
	public void setGanttId(int ganttId) {
		this.ganttId = ganttId;
	}
	
	/**
	 * 排序：逻辑层级升序、开始日期倒序
	 */
	public int compareTo(TaskInfo o) {
		Integer depth = this.getLogicalDepth();
		Integer depth_o = o.getLogicalDepth();
		
		if(depth.compareTo(depth_o) == 0) {
			return -this.getPlannedStartTime().compareTo(o.getPlannedStartTime());
		}
		
		return depth.compareTo(depth_o);
	}
	
	/**
	 * 当前任务是否由项目阶段转换而来用于甘特图显示(部分节点数据显示需区别于普通任务节点)
	 */
	private boolean fromProjectPhase;

	public boolean isFromProjectPhase() {
		return fromProjectPhase;
	}
	/**
	 * 当前任务是否由项目阶段转换而来用于甘特图显示(部分节点数据显示需区别于普通任务节点)
	 */
	public void setFromProjectPhase(boolean fromProjectPhase) {
		this.fromProjectPhase = fromProjectPhase;
	}
	
	/**
	 * 用于甘特图展现(项目阶段需要作为一个根节点)
	 * @param phase		项目阶段
	 */
	public TaskInfo(ProjectPhase phase) {
		this.setId(phase.getId());
		this.setSubject(phase.getPhaseName());
		this.setFullTime(true);
		this.setPlannedStartTime(phase.getPhaseBegintime());
		this.setPlannedEndTime(phase.getPhaseClosetime());
		this.setFinishRate(0f);
		this.setFromProjectPhase(true);
		this.setProjectPhaseId(phase.getId());
		this.setRiskLevel(0);
	}

}