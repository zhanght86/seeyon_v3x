package com.seeyon.v3x.collaboration.webmodel;

import java.io.Serializable;

import com.seeyon.v3x.collaboration.manager.impl.ColLock;
import com.seeyon.v3x.collaboration.manager.impl.ColLock.COL_ACTION;

public class ColLockModel implements Serializable{
	private static final long serialVersionUID = 4180484447383226236L;
	
	private long summaryId = -1L; //扩展该字段，支持集群传输类型
	
	private long memberId;

	private ColLock.COL_ACTION action;
	
	private String memberName;
	
	public ColLockModel(long summaryId, long memberId, COL_ACTION action, String memberName) {
		super();
		this.summaryId = summaryId;
		this.memberId = memberId;
		this.memberName = memberName;
		this.action = action;
	}

	public ColLock.COL_ACTION getAction() {
		return action;
	}

	public void setAction(ColLock.COL_ACTION action) {
		this.action = action;
	}

	public long getMemberId() {
		return memberId;
	}

	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public long getSummaryId() {
		return summaryId;
	}

	public void setSummaryId(long summaryId) {
		this.summaryId = summaryId;
	}
}