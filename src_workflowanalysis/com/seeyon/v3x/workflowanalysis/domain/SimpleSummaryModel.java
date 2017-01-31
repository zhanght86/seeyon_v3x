package com.seeyon.v3x.workflowanalysis.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * @author mujun
 */
public class SimpleSummaryModel implements Comparable<SimpleSummaryModel>{
	//ID,取COL_SUMMARY 或者 EDOC_SUMMARY的ID
	private Long id;
	//标题
	private String subject ;
	//运行时长
	private Long runTime ;
	//超时时长
	private Long overTime;
	//运行时长
	private Long runWorkTime ;
	//超时时长
	private Long overWorkTime;
	//效率
	private Double efficiency;
	//流程期限
	private Long deadline;

	private String appTypeName;
	
	public String getAppTypeName() {
		return appTypeName;
	}
	public void setAppTypeName(String appTypeName) {
		this.appTypeName = appTypeName;
	}
	public Long getDeadline() {
		return deadline;
	}
	public void setDeadline(Long deadline) {
		this.deadline = deadline;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Long getRunTime() {
		return runTime;
	}
	public void setRunTime(Long runTime) {
		this.runTime = runTime;
	}
	public Long getOverTime() {
		return overTime;
	}
	public void setOverTime(Long overTime) {
		this.overTime = overTime;
	}
	public Double getEfficiency() {
		return efficiency;
	}
	public void setEfficiency(Double efficiency) {
		this.efficiency = efficiency;
	}
	public Long getRunWorkTime() {
		return runWorkTime;
	}
	public void setRunWorkTime(Long runWorkTime) {
		this.runWorkTime = runWorkTime;
	}
	public Long getOverWorkTime() {
		return overWorkTime;
	}
	public void setOverWorkTime(Long overWorkTime) {
		this.overWorkTime = overWorkTime;
	}

	@Override
	public int compareTo(SimpleSummaryModel s) {
		
		if(efficiency == null) efficiency = 0.0;
		
		if(s.getEfficiency() == null ) s.setEfficiency(0.0);
		
		if(this.efficiency.doubleValue() > s.getEfficiency().doubleValue()){
			return -1;
		}else if( this.efficiency.doubleValue() < s.getEfficiency().doubleValue()){
			return 1;
		}
		return 0;
	}
}
