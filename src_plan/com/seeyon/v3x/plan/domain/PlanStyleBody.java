package com.seeyon.v3x.plan.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * <p/> Title:计划样式正文
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:
 * </p>
 * 
 * @author T3
 * @version 1.0
 */
public class PlanStyleBody extends BaseModel implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8903127753501086638L;

	/**
	 * 计划
	 */
	private PlanStyle planStyle;

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
	public PlanStyleBody() {
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

	public PlanStyle getPlanStyle() {
		return planStyle;
	}

	public void setPlanStyle(PlanStyle planStyle) {
		this.planStyle = planStyle;
	}

}