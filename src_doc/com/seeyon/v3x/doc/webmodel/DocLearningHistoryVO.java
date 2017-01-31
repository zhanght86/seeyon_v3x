package com.seeyon.v3x.doc.webmodel;

import java.sql.Timestamp;

import com.seeyon.v3x.doc.domain.DocLearningHistory;

/**
 * 文档学习记录vo
 */
public class DocLearningHistoryVO {
	private DocLearningHistory history;
	// 学习人
	private String memberName;
	// 学习人所在部门
	private String deptName;
	// 学习时间
	private Timestamp lastAccessTime;
	
	public DocLearningHistoryVO(DocLearningHistory history){
		this.history = history;
		this.lastAccessTime = history.getAccessTime();
	}
	
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public DocLearningHistory getHistory() {
		return history;
	}
	public void setHistory(DocLearningHistory history) {
		this.history = history;
	}
	public Timestamp getLastAccessTime() {
		return lastAccessTime;
	}
	public void setLastAccessTime(Timestamp lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}
	public String getMemberName() {
		return memberName;
	}
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
}
