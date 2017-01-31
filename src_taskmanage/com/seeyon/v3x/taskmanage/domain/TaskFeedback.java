package com.seeyon.v3x.taskmanage.domain;

import java.sql.Timestamp;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 任务汇报、反馈
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-25
 */
public class TaskFeedback extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5490065434753679789L;
	
	/**
	 * 汇报人
	 */
	private Long createUser;
	/**
	 * 汇报创建时间
	 */
	private Timestamp createTime;
	/**
	 * 汇报人
	 */
	private Long updateUser;
	/**
	 * 汇报修改时间
	 */
	private Timestamp updateTime;
	/**
	 * 汇报项：实际耗时
	 */
	private float elapsedTime;
	/**
	 * 汇报项：完成率
	 */
	private float finishRate;
	/**
	 * 汇报项：主体内容
	 */
	private String content;
	/**
	 * 是否含有附件
	 */
	private boolean hasAttachments;
	/**
	 * 汇报任务ID
	 */
	private Long taskId;
	
	public Long getCreateUser() {
		return createUser;
	}
	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Long getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(Long updateUser) {
		this.updateUser = updateUser;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	public float getElapsedTime() {
		return elapsedTime;
	}
	public void setElapsedTime(float elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	public float getFinishRate() {
		return finishRate;
	}
	public void setFinishRate(float finishRate) {
		this.finishRate = finishRate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isHasAttachments() {
		return hasAttachments;
	}
	public void setHasAttachments(boolean hasAttachments) {
		this.hasAttachments = hasAttachments;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	
}
