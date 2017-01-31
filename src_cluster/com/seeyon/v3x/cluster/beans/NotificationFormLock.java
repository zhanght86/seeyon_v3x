package com.seeyon.v3x.cluster.beans;

import java.io.Serializable;

public class NotificationFormLock implements Serializable {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4586990891889182969L;
	
	private Long summaryId ;
	private Long affairId ; 
	private Long memberId ;
	private String loginName ;
	private Long loginTimestamp;
	
	public NotificationFormLock(){} 
	
	public NotificationFormLock(Long summaryId ,Long affairId ,Long memberId ,String loginName,Long loginTimestamp){
		this.affairId = affairId;
		this.summaryId = summaryId ;
		this.memberId = memberId ;
		this.loginName = loginName ;
		this.loginTimestamp = loginTimestamp;
	} 
	
	public Long getAffairId() {
		return affairId;
	}
	public void setAffairId(Long affairId) {
		this.affairId = affairId;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public Long getSummaryId() {
		return summaryId;
	}
	public void setSummaryId(Long summaryId) {
		this.summaryId = summaryId;
	}
	
	@Override
	public String toString() {
		return "NotificationFormLock [affairId=" + affairId + ", summaryId=" + summaryId + 
		         ",memberId=" + memberId + ",loginName =" + loginName
				+ "]";
	}

	public Long getLoginTimestamp() {
		return loginTimestamp;
	}

	public void setLoginTimestamp(Long loginTimestamp) {
		this.loginTimestamp = loginTimestamp;
	}
	
}
