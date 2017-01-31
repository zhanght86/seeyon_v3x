package com.seeyon.v3x.collaboration.templete.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

public class ColBranch extends BaseModel implements Serializable{

	private static final long serialVersionUID = -4479709045130444658L;
	private Integer conditionType;
	private String formCondition;
	private String conditionTitle;
	private String conditionDesc;
	private Long templateId;
	private Long linkId;
	private Integer isForce;
	private String conditionBase;
	private int appType;
	public int getAppType() {
		return appType;
	}
	public void setAppType(int appType) {
		this.appType = appType;
	}
	public String getConditionBase() {
		return conditionBase;
	}
	public void setConditionBase(String conditionBase) {
		this.conditionBase = conditionBase;
	}
	public Integer getIsForce() {
		return isForce;
	}
	public void setIsForce(Integer isForce) {
		this.isForce = isForce;
	}
	public String getConditionDesc() {
		return conditionDesc;
	}
	public void setConditionDesc(String conditionDesc) {
		this.conditionDesc = conditionDesc;
	}
	public String getConditionTitle() {
		return conditionTitle;
	}
	public void setConditionTitle(String conditionTitle) {
		this.conditionTitle = conditionTitle;
	}
	public Integer getConditionType() {
		return conditionType;
	}
	public Long getLinkId() {
		return linkId;
	}
	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}
	public Long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}
	public void setConditionType(Integer conditionType) {
		this.conditionType = conditionType;
	}
	public String getFormCondition() {
		return formCondition;
	}
	public void setFormCondition(String formCondition) {
		this.formCondition = formCondition;
	}
	
	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}
}
