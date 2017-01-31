package com.seeyon.v3x.workflowanalysis.domain;
/**
 * 对比分析的对象
 * @author mujun
 */
public class CompareModel {
	private Long avgRunTime;
	private Long maxRunTime;
	private Long minRunTime;
	private Integer standarduaration;
	private Double efficiency;
	private Long templeteId;
	
	public Long getTempleteId() {
		return templeteId;
	}
	public void setTempleteId(Long templeteId) {
		this.templeteId = templeteId;
	}
	public Long getAvgRunTime() {
		return avgRunTime;
	}
	public void setAvgRunTime(Long avgRunTime) {
		this.avgRunTime = avgRunTime;
	}
	public Long getMaxRunTime() {
		return maxRunTime;
	}
	public void setMaxRunTime(Long maxRunTime) {
		this.maxRunTime = maxRunTime;
	}
	public Long getMinRunTime() {
		return minRunTime;
	}
	public void setMinRunTime(Long minRunTime) {
		this.minRunTime = minRunTime;
	}
	public Integer getStandarduaration() {
		return standarduaration;
	}
	public void setStandarduaration(Integer standarduaration) {
		this.standarduaration = standarduaration;
	}
	public Double getEfficiency() {
		return efficiency;
	}
	public void setEfficiency(Double efficiency) {
		this.efficiency = efficiency;
	}
}
