package com.seeyon.v3x.workflowanalysis.domain;

public class NodeAnalysisDetailModel {
	
	private String subject;
	private String memberNames;
	private Long runWorkTime;
	private Long deadLine;
	private Long overWorkTime;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMemberNames() {
		return memberNames;
	}
	public void setMemberNames(String memberNames) {
		this.memberNames = memberNames;
	}
	public Long getRunWorkTime() {
		return runWorkTime;
	}
	public void setRunWorkTime(Long runWorkTime) {
		this.runWorkTime = runWorkTime;
	}
	public Long getDeadLine() {
		return deadLine;
	}
	public void setDeadLine(Long deadLine) {
		this.deadLine = deadLine;
	}
	public Long getOverWorkTime() {
		return overWorkTime;
	}
	public void setOverWorkTime(Long overWorkTime) {
		this.overWorkTime = overWorkTime;
	}
	
}
