package com.seeyon.v3x.workflowanalysis.domain;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

public class WorkFlowAnalysis extends BaseModel  implements Serializable{
	private static final long serialVersionUID = 8116283345484660765L;
	//年
	private Integer year;
	//月
	private Integer month ;
	//时间
	private Date    statDate;
	//单位
	private Long orgAccountId;
	//模板ID
	private Long templeteId;
	// 应用名
	private Integer catagory;
	//本模板流程实例数
	private Integer caseCount ;
	//所有模板流程总数
	private Integer allCaseCount;
	//本模板超时流程实例数
	private Integer overCaseCount ;
	// 使用率
	private Double useRadio ;
	//平均运行时长
	private Integer avgRunTime;
	//基准时长
	private Integer standardTime;
	//效率
	private Double efficiency;
	//平均超时
	private Integer avgOverTime;
	//超时率
	private Double overTimeRatio;
	
	
	
	//不持久化
	public static final Long AllTemplete = 1L;
	public String templeteSubject ; 
	public Long templeteMemberId;
	
	
	public String getTempleteSubject() {
		return templeteSubject;
	}
	public void setTempleteSubject(String templeteSubject) {
		this.templeteSubject = templeteSubject;
	}
	public Long getTempleteMemberId() {
		return templeteMemberId;
	}
	public void setTempleteMemberId(Long templeteMemberId) {
		this.templeteMemberId = templeteMemberId;
	}
	public Integer getCatagory() {
		return catagory;
	}
	public void setCatagory(Integer catagory) {
		this.catagory = catagory;
	}
	public Integer getCaseCount() {
		return caseCount;
	}
	public void setCaseCount(Integer caseCount) {
		this.caseCount = caseCount;
	}
	public Double getUseRadio() {
		return useRadio;
	}
	public void setUseRadio(Double useRadio) {
		this.useRadio = useRadio;
	}
	public Integer getAvgRunTime() {
		return avgRunTime;
	}
	public void setAvgRunTime(Integer avgRunTime) {
		this.avgRunTime = avgRunTime;
	}
	public Integer getStandardTime() {
		return standardTime;
	}
	public void setStandardTime(Integer standardTime) {
		this.standardTime = standardTime;
	}
	
	public Integer getAvgOverTime() {
		return avgOverTime;
	}
	public void setAvgOverTime(Integer avgOverTime) {
		this.avgOverTime = avgOverTime;
	}
	public Double getOverTimeRatio() {
		return overTimeRatio;
	}
	public void setOverTimeRatio(Double overTimeRatio) {
		this.overTimeRatio = overTimeRatio;
	}
	public Double getEfficiency() {
		return efficiency;
	}
	public void setEfficiency(Double efficiency) {
		this.efficiency = efficiency;
	}
	public Long getTempleteId() {
		return templeteId;
	}
	public void setTempleteId(Long templeteId) {
		this.templeteId = templeteId;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public Long getOrgAccountId() {
		return orgAccountId;
	}
	public void setOrgAccountId(Long orgAccountId) {
		this.orgAccountId = orgAccountId;
	}
	public Integer getAllCaseCount() {
		return allCaseCount;
	}
	public void setAllCaseCount(Integer allCaseCount) {
		this.allCaseCount = allCaseCount;
	}
	public Integer getOverCaseCount() {
		return overCaseCount;
	}
	public void setOverCaseCount(Integer overCaseCount) {
		this.overCaseCount = overCaseCount;
	}
	public Date getStatDate() {
		return statDate;
	}
	public void setStatDate(Date statDate) {
		this.statDate = statDate;
	}
}