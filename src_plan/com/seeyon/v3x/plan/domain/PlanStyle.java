package com.seeyon.v3x.plan.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * <p/> Title:计划样式
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:
 * </p>
 * 
 * @author T3
 * @version 1.0
 */
public class PlanStyle extends BaseModel implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 3037194645221790549L;

	private String title;

	/**
	 * 1 html 2 ms word(.doc) 3 ms excel(.xls)
	 */
	private String textType;

	private PlanStyleBody planStyleBody;

	/**
	 * 1 day plan 2 week plan 3 month plan 4 any scope plan
	 */
	private String type;

	private Date createTime;
	private Long accountId; //单位id
	// Constructors

	/** default constructor */
	public PlanStyle() {
	}

	/** full constructor */
	public PlanStyle(String title, String textType, String text, String type,
			Date createTime,Long accountId) {
		this.title = title;
		this.textType = textType;
		// this.text = text;
		this.type = type;
		this.createTime = createTime;
		this.accountId = accountId;
	}

	// Property accessors

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTextType() {
		return this.textType;
	}

	public void setTextType(String textType) {
		this.textType = textType;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public PlanStyleBody getPlanStyleBody() {
		return planStyleBody;
	}

	public void setPlanStyleBody(PlanStyleBody planStyleBody) {
		this.planStyleBody = planStyleBody;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

}