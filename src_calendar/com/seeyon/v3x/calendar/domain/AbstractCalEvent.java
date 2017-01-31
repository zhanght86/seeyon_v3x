package com.seeyon.v3x.calendar.domain;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.util.Datetimes;

/**
 * The persistent class for the cal_event database table.
 * 
 * @author BEA Workshop Studio
 */
public class AbstractCalEvent extends BaseModel implements java.io.Serializable,
		Comparable<AbstractCalEvent> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6674580210009342525L;

	// default serial version id, required for serializable classes.

	private Long alarmDate;// 提醒时间
	
	private Long beforendAlarm;//结束提前提醒。

	private boolean alarmFlag;// 提醒的标识

	private java.util.Date beginDate; // 开始时间

	private float completeRate; // 完成率

	private java.util.Date createDate;// 创建时间

	private Long createUserId;// 创建者

	private java.util.Date endDate;// 结束时间

	private Integer eventType = 1;// 事件类型(1.自建 2.安排 3委托 )

	private Integer priorityType;// 优先级类型（1.低2.中3.高）

	private Float realEstimateTime;// 实际完成时间

	private Integer shareType;// 共享类型（1.私人事件2.公开事件3.共享给上级4.共享给下级5.共享给部门6.共享给项目7.秘书和助手）

	private Integer signifyType;// 重要程度（1.重要紧急2.不重要紧急3.重要不紧急4.不重要不紧急）

	private Integer states;// 事件完成类型（1.未安排 2.已安排3.进行中 4.已完成）

	private String subject;// 题目

	private String tranMemberIds;// 接受的对象id（人）（逗号分隔，只用于显示）

	private String shareTarget;// 共享对象name（部门）（逗号分隔，只用于显示）

	private java.util.Date updateDate;// 更新时间

	private Integer workType;// 工作类型（1.自办2.督办3.协办）

	private Integer eventflag = 0;// 事件当前类型标识（0.初试状态 1.已安排2.已委托）

	private Long projectId;// 关联项目ID

	// 新加的 接受者的id和名字 为了显示和存储
	private String receiveMemberId;// 所属人员的Id

	private String receiveMemberName;// 所属人员的name

	// -----others
	
	private int fromType;//关联的应用类型
	private long fromId;//关联应用的id
	
	private Integer calEventType;// 事件类型(0.业务1.管理 2.个人 3.其它 )
	/**
	 * 周期性提醒（0.无  1.按天提醒 2.按周提醒 3.按月提醒 4.按年提醒）
	 */
	private Integer periodicalStyle = 0;

	
	public Integer getPeriodicalStyle() {
		return periodicalStyle;
	}

	public void setPeriodicalStyle(Integer periodicalStyle) {
		this.periodicalStyle = periodicalStyle;
	}

	public Integer getCalEventType() {
		return calEventType;
	}

	public void setCalEventType(Integer calEventType) {
		this.calEventType = calEventType;
	}

	public String getReceiveMemberId() {
		return receiveMemberId;
	}

	public void setReceiveMemberId(String receiveMemberId) {
		this.receiveMemberId = receiveMemberId;
	}

	public String getReceiveMemberName() {
		return receiveMemberName;
	}

	public void setReceiveMemberName(String receiveMemberName) {
		this.receiveMemberName = receiveMemberName;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public AbstractCalEvent() {
	}

	public java.util.Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(java.util.Date beginDate) {
		this.beginDate = beginDate;
	}

	public java.util.Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(java.util.Date createDate) {
		this.createDate = createDate;
	}

	public java.util.Date getEndDate() {
		return endDate;
	}

	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}

	public java.util.Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(java.util.Date updateDate) {
		this.updateDate = updateDate;
	}

	public void setStates(Integer states) {
		this.states = states;
	}

	public Long getAlarmDate() {
		return this.alarmDate;
	}

	public void setAlarmDate(Long alarmDate) {
		this.alarmDate = alarmDate;
	}

	public boolean isAlarmFlag() {
		return alarmFlag;
	}

	public void setAlarmFlag(boolean alarmFlag) {
		this.alarmFlag = alarmFlag;
	}

	public float getCompleteRate() {
		return this.completeRate;
	}

	public void setCompleteRate(float completeRate) {
		this.completeRate = completeRate;
	}

	public Long getCreateUserId() {
		return this.createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public Integer getEventType() {
		return this.eventType;
	}

	public void setEventType(Integer eventType) {
		this.eventType = eventType;
	}

	public Integer getPriorityType() {
		return this.priorityType;
	}

	public void setPriorityType(Integer priorityType) {
		this.priorityType = priorityType;
	}

	public Float getRealEstimateTime() {
		return this.realEstimateTime;
	}

	public void setRealEstimateTime(Float realEstimateTime) {
		this.realEstimateTime = realEstimateTime;
	}

	public String getShareTarget() {
		return this.shareTarget;
	}

	public void setShareTarget(String shareTarget) {
		this.shareTarget = shareTarget;
	}

	public Integer getShareType() {
		return this.shareType;
	}

	public void setShareType(Integer shareType) {
		this.shareType = shareType;
	}

	public Integer getSignifyType() {
		return this.signifyType;
	}

	public void setSignifyType(Integer signifyType) {
		this.signifyType = signifyType;
	}

	public Integer getStates() {
		return this.states;
	}


	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTranMemberIds() {
		return this.tranMemberIds;
	}

	public void setTranMemberIds(String tranMemberIds) {
		this.tranMemberIds = tranMemberIds;
	}

	public Integer getWorkType() {
		return this.workType;
	}

	public void setWorkType(Integer workType) {
		this.workType = workType;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

	// -----------非数据库字段，表现值

	private Integer completeRateInt;// 完成率的整数显示

	private String createUserName;// 创建人的名字

	// lihf 08.06.12 持久化
	private Boolean attachmentsFlag = false;// 是否添加附件的标识

	private String timeFlag;// 显示事件执行时间的标识 1.today （当天内）2.oneday（不是当天的一天内） 3
							// days（跨天） 4 noend（没有结束的）

	private Integer beforeTime;// 提醒的时间标识

	private CalEventTran tran;// 共享事件额度类型

	private Long contentId;// 接受部门的名字

	private String tranMemberName;// 接受人的名字

	public Boolean getAttachmentsFlag() {
		return attachmentsFlag;
	}

	public void setAttachmentsFlag(Boolean attachmentsFlag) {
		this.attachmentsFlag = attachmentsFlag;
	}

	public Integer getCompleteRateInt() {
		return completeRateInt;
	}

	public void setCompleteRateInt(Integer completeRateInt) {
		this.completeRateInt = completeRateInt;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public CalEventTran getTran() {
		return tran;
	}

	public void setTran(CalEventTran tran) {
		this.tran = tran;
	}

	public Integer getEventflag() {
		return eventflag;
	}

	public void setEventflag(Integer eventflag) {
		this.eventflag = eventflag;
	}

	public Integer getBeforeTime() {
		return beforeTime;
	}

	public void setBeforeTime(Integer beforeTime) {
		this.beforeTime = beforeTime;
	}

	public String getTimeFlag() {
		return timeFlag;
	}

	public void setTimeFlag(String timeFlag) {
		this.timeFlag = timeFlag;
	}

	public String getTranMemberName() {
		return tranMemberName;
	}

	public void setTranMemberName(String tranMemberName) {
		this.tranMemberName = tranMemberName;
	}

	public boolean isNew() {
		if (id == null || id == Constants.GLOBAL_NULL_ID) {
			return true;
		}

		return false;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public int compareTo(AbstractCalEvent o) {
		Integer rangeSort1 = this.getRangeSort();
		if(rangeSort1 == null)
			rangeSort1 = com.seeyon.v3x.calendar.util.Constants.DateRangeType.today.ordinal();
		
		Integer rangeSort2 = o.getRangeSort();
		if(rangeSort2 == null)
			rangeSort2 = com.seeyon.v3x.calendar.util.Constants.DateRangeType.today.ordinal();
		
		if(rangeSort1.equals(rangeSort2)) {
			return this.getBeginDate().compareTo(o.getBeginDate());
		} else {
			return rangeSort1.compareTo(rangeSort2);
		}
	}

	public long getFromId() {
		return fromId;
	}

	public void setFromId(long fromId) {
		this.fromId = fromId;
	}

	public int getFromType() {
		return fromType;
	}

	public void setFromType(int fromType) {
		this.fromType = fromType;
	}
	
	/**
	 * 得到该事项的 第一个所属人
	 * @return
	 */
	public String getReceiverMember(){
		String rId = this.getReceiveMemberId();
		if(rId!=null&&rId.length()!=0){
			return this.getReceiveMemberName().split(",")[0];
		}else{
			return this.getCreateUserName();
		}
	}

	/**
	 * 得到事件自身的提醒时间(只在事件已经具备提醒时，才有必要调用此方法)
	 * @return
	 */
	public Date getRemindTime() {
		Date result;
		if (this.getAlarmDate() != null && this.getAlarmDate() != 0 && this.getAlarmDate() != -1) {
			result = Datetimes.addMinute(this.getBeginDate(), -this.getAlarmDate().intValue());
		} else {
			result = this.getBeginDate();
		}
		return result;
	}
	
	//结束前提醒时间
	public Date getBeforEndRemindTime(){
		Date result;
		if (this.getBeforendAlarm() != null && this.getBeforendAlarm() != 0 && this.getBeforendAlarm() != -1) {
			result = Datetimes.addMinute(this.getEndDate(), -this.getBeforendAlarm().intValue());
		} else {
			result = this.getEndDate();
		}
		return result;
	}
	
	/** 日程类型：今日、跨日、明日、更晚、更早 */
	private String dateRangeType;
	
	/** 日程类型排序号： 今日、跨日、明日、更晚、更早 */
	private Integer rangeSort;

	/** 
	 * 不同日程类型下面，最终显示的日期信息：
	 * 1.今日，显示结果(HH:mm)：11:00 ~ 14:00;
	 * 2.跨日，显示结果(MM:/dd)：05/10 ~ 05/12;
	 * 3.更晚(明日)、更早，显示结果(yy/MM/dd)：10/05/**
	 */
	private String dateInfo;
	
	
	public String getDateRangeType() {
		return dateRangeType;
	}

	public void setDateRangeType(String dateRangeType) {
		this.dateRangeType = dateRangeType;
	}

	public String getDateInfo() {
		return dateInfo;
	}

	public void setDateInfo(String dateInfo) {
		this.dateInfo = dateInfo;
	}
	
	public Integer getRangeSort() {
		return rangeSort;
	}

	public void setRangeSort(Integer rangeSort) {
		this.rangeSort = rangeSort;
	}

	public Long getBeforendAlarm() {
		return beforendAlarm;
	}

	public void setBeforendAlarm(Long beforendAlarm) {
		this.beforendAlarm = beforendAlarm;
	}
	
}