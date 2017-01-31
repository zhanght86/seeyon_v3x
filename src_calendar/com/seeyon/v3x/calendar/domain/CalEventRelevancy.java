package com.seeyon.v3x.calendar.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 日程事件和其他应用的关联表
 * 
 * @author javaKuang
 * 
 */
public class CalEventRelevancy extends BaseModel implements Serializable {

	private static final long serialVersionUID = 7938562254528358682L;

	private Long id;

	/**
	 * 日程事件ID
	 */
	private Long calId;

	/**
	 * 关联应用的ID
	 */
	private Long relevancyId;

	/**
	 * 关联应用的类型
	 */
	private Integer relevancyType;

	public Long getCalId() {
		return calId;
	}

	public void setCalId(Long calId) {
		this.calId = calId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRelevancyId() {
		return relevancyId;
	}

	public void setRelevancyId(Long relevancyId) {
		this.relevancyId = relevancyId;
	}

	public Integer getRelevancyType() {
		return relevancyType;
	}

	public void setRelevancyType(Integer relevancyType) {
		this.relevancyType = relevancyType;
	}

	@Override
	public boolean isNew() {
		if (id == null || id == Constants.GLOBAL_NULL_ID) {
			return true;
		}
		return false;
	}

}
