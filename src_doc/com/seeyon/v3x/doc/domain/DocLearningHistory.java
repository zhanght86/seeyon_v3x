package com.seeyon.v3x.doc.domain;

import java.sql.Timestamp;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 学习记录
 */
public class DocLearningHistory extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6933152247161800641L;
	
	private long docResourceId;
	// 学习人
	private long accessMemberId;
	// 学习人所在部门
	private long departmentId;
	// 学习时间
	private Timestamp accessTime;
	public long getAccessMemberId() {
		return accessMemberId;
	}
	public void setAccessMemberId(long accessMemberId) {
		this.accessMemberId = accessMemberId;
	}
	public Timestamp getAccessTime() {
		return accessTime;
	}
	public void setAccessTime(Timestamp accessTime) {
		this.accessTime = accessTime;
	}
	public long getDocResourceId() {
		return docResourceId;
	}
	public void setDocResourceId(long docResourceId) {
		this.docResourceId = docResourceId;
	}
	public long getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(long departmentId) {
		this.departmentId = departmentId;
	}
}
