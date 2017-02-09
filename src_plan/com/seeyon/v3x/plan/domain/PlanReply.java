package com.seeyon.v3x.plan.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * PlanReply generated by MyEclipse - Hibernate Tools
 */

public class PlanReply extends BaseModel implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -8034842456414302388L;

	private Plan plan;

	private Long refUserId;

	private String text;

	private Date createTime;

	private Long refPlanReplyId;

	/**
	 * 相关用户 用户名
	 */
	private String refUserName;
	
	private Boolean isHidden = false;

	// Constructors

	public String getRefUserName() {
		return refUserName;
	}

	public void setRefUserName(String refUserName) {
		this.refUserName = refUserName;
	}

	/** default constructor */
	public PlanReply() {
	}

	/** minimal constructor */
	public PlanReply(Date createTime) {
		this.createTime = createTime;
	}

	// /** full constructor */
	public PlanReply(Plan plan, Long refUserId, String text, Date createTime,
			Long refPlanReplyId, String refUserName, Boolean isHidden) {
		this.plan = plan;
		this.refUserId = refUserId;
		this.text = text;
		this.createTime = createTime;
		this.refPlanReplyId = refPlanReplyId;
		this.refUserName = refUserName;
		this.isHidden = isHidden;
	}

	// Property accessors
	public Long getRefUserId() {
		return this.refUserId;
	}

	public void setRefUserId(Long refUserId) {
		this.refUserId = refUserId;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

	public Long getRefPlanReplyId() {
		return refPlanReplyId;
	}

	public void setRefPlanReplyId(Long refPlanReplyId) {
		this.refPlanReplyId = refPlanReplyId;
	}

	public Boolean getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(Boolean isHidden) {
		this.isHidden = isHidden;
	}

}