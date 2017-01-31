package com.seeyon.v3x.plan.domain;


/**
 * 
 * <p/> Title:计划统计
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Feb 10, 2007 8:46:35 PM
 * </p>
 * 
 * @author T3
 * @version 1.0
 */
public class PlanCountModel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2731463405414432193L;
	
	/**
	 * 用户ID
	 */
	private int userId;
	
	/**
	 * 计划类型
	 */
	private String planType;

	/**
	 * 已发布数量
	 */
	private int issuedCount;
	
	/**
	 * 已归档
	 */
	private int pigeonholeCount;
	
	/**
	 * 已回复
	 */
	private int replyCount;
	
	/**
	 * 未回复
	 */
	private int notReplyCount;
	
	/**
	 * 已总结
	 */
	private int summaryCount;
	
	/**
	 * 未总结
	 */
	private int notSummaryCount;

	public int getIssuedCount() {
		return issuedCount;
	}

	public void setIssuedCount(int issuedCount) {
		this.issuedCount = issuedCount;
	}

	public int getNotReplyCount() {
		return notReplyCount;
	}

	public void setNotReplyCount(int notReplyCount) {
		this.notReplyCount = notReplyCount;
	}

	public int getNotSummaryCount() {
		return notSummaryCount;
	}

	public void setNotSummaryCount(int notSummaryCount) {
		this.notSummaryCount = notSummaryCount;
	}

	public int getPigeonholeCount() {
		return pigeonholeCount;
	}

	public void setPigeonholeCount(int pigeonholeCount) {
		this.pigeonholeCount = pigeonholeCount;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public int getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}

	public int getSummaryCount() {
		return summaryCount;
	}

	public void setSummaryCount(int summaryCount) {
		this.summaryCount = summaryCount;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}