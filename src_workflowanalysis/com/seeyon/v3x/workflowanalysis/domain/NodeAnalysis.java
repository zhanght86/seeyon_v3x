package com.seeyon.v3x.workflowanalysis.domain;

public class NodeAnalysis {
	//节点权限ID
	private String policyId ;
	//节点权限名字
	private String policyName ;
	//处理角色名称
	private String name;
	//节点ID
	private String Id;
	//平均处理时长
	private Long avgRunWorkTime ;
	//超时率
	private Double overRadio;
	
	public String getPolicyId() {
		return policyId;
	}
	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}
	public String getPolicyName() {
		return policyName;
	}
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public Long getAvgRunWorkTime() {
		return avgRunWorkTime;
	}
	public void setAvgRunWorkTime(Long avgRunWorkTime) {
		this.avgRunWorkTime = avgRunWorkTime;
	}
	public Double getOverRadio() {
		return overRadio;
	}
	public void setOverRadio(Double overRadio) {
		this.overRadio = overRadio;
	}
}
