package com.seeyon.v3x.plan.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * <p/> Title:计划正文
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Feb 10, 2007 8:46:35 PM
 * </p>
 * 
 * @author T3
 * @version 1.0
 */
public class PlanBody extends BaseModel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8903127753501086638L;

	/**
	 * 计划
	 */
	private Plan plan;

	/**
	 * 正文类型
	 */
	private String bodyType;

	/**
	 * 正文
	 */
	private String content;

	/**
	 * 创建时间
	 */
	private Date createDate;

	// Constructors

	/** default constructor */
	public PlanBody() {
	}

	// Property accessors
	public String getBodyType() {
		return bodyType;
	}

	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Plan getPlan() {
		return plan;
	}

	public void setPlan(Plan plan) {
		this.plan = plan;
	}

}