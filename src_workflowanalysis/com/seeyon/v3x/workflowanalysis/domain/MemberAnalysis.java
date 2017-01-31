package com.seeyon.v3x.workflowanalysis.domain;

public class MemberAnalysis {
	//人员ID
	private Long memberId;
	//处理次数
	private Integer count ;
	//超期率
	private Double overRadio;
	//平均处理时长
	private Long avgRunTime;
	
	
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public Double getOverRadio() {
		return overRadio;
	}
	public void setOverRadio(Double overRadio) {
		this.overRadio = overRadio;
	}
	public Long getAvgRunTime() {
		return avgRunTime;
	}
	public void setAvgRunTime(Long avgRunTime) {
		this.avgRunTime = avgRunTime;
	}
}
