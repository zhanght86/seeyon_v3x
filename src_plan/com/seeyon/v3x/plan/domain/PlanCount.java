package com.seeyon.v3x.plan.domain;

/**
 * 
 * <p/> Title:计划实体
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Feb 10, 2007 8:46:35 PM
 * </p>
 * 
 * @author T3
 * @version 1.0
 */
public class PlanCount extends Plan {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1707067821438080646L;

	/**
	 * 计划总结统计数
	 */
	private int summaryCount;

	/**
	 * 计划回复统计数
	 */
	private int replyCount;

	/**
	 * 计划发起人用户id
	 */
	private Long userId;

	/**
	 * 计划发起人用户名
	 */
	private String userName;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public PlanCount(Long id, String planType, String publishStatus,
			int summaryCount, int replyCount) {
		setId(id);
		setType(planType);
		setPublishStatus(publishStatus);
		setSummaryCount(summaryCount);
		setReplyCount(replyCount);
	}

	public PlanCount(Long id, String planType, String publishStatus,
			int summaryCount, int replyCount, Long userId, String userName) {
		setId(id);
		setType(planType);
		setPublishStatus(publishStatus);
		setSummaryCount(summaryCount);
		setReplyCount(replyCount);
		setUserId(userId);
		setUserName(userName);
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
}