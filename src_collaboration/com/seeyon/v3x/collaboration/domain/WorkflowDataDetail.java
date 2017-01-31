package com.seeyon.v3x.collaboration.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

public class WorkflowDataDetail extends BaseModel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long entityId;
	//发起或处理者
	private String handler;
	//节点名称
	private String policyName;
	//发起/接收时间
	private java.sql.Timestamp createDate;
	//处理时间
	private java.sql.Timestamp finishDate;
	//处理期限
	private String deadline;
	//处理时长
	private String dealTime;
	//超期时长
	private String deadlineTime;
	//处理状态
	private String stateLabel;
	//是否超期
	private boolean timeOutFlag;
	//是否超期标签
	private String timeOutFlagLabel;
	//超时时长
	private Long overWorkTime;
	//运行时长(工作日)
	private Long runWorkTime;
	
	public WorkflowDataDetail(){
	}

	public Long getRunWorkTime() {
		return runWorkTime;
	}

	public void setRunWorkTime(Long runWorkTime) {
		this.runWorkTime = runWorkTime;
	}

	public Long getOverWorkTime() {
		return overWorkTime;
	}

	public void setOverWorkTime(Long overWorkTime) {
		this.overWorkTime = overWorkTime;
	}

	public java.sql.Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(java.sql.Timestamp createDate) {
		this.createDate = createDate;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getDeadlineTime() {
		return deadlineTime;
	}

	public void setDeadlineTime(String deadlineTime) {
		this.deadlineTime = deadlineTime;
	}

	public String getDealTime() {
		return dealTime;
	}

	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public java.sql.Timestamp getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(java.sql.Timestamp finishDate) {
		this.finishDate = finishDate;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public String getStateLabel() {
		return stateLabel;
	}

	public void setStateLabel(String stateLabel) {
		this.stateLabel = stateLabel;
	}

	public boolean isTimeOutFlag() {
		return timeOutFlag;
	}

	public void setTimeOutFlag(boolean timeOutFlag) {
		this.timeOutFlag = timeOutFlag;
	}

	public String getTimeOutFlagLabel() {
		return timeOutFlagLabel;
	}

	public void setTimeOutFlagLabel(String timeOutFlagLabel) {
		this.timeOutFlagLabel = timeOutFlagLabel;
	}

}
