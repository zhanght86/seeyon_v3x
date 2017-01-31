package com.seeyon.v3x.collaboration.domain;

import java.util.Date;

public class LockObject {
	private long owner;
	private Date lockTime;
	private String loginName;
	private Long loginTimestamp;

	public Long getLoginTimestamp() {
		return loginTimestamp;
	}

	public void setLoginTimestamp(Long loginTimestamp) {
		this.loginTimestamp = loginTimestamp;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public long getOwner(){
	    return this.owner;
	}

	public void setOwner(long owner){
	    this.owner = owner;
	}

	public Date getLockTime(){
	    return this.lockTime;
	}

	public void setLockTime(Date lockTime){
	    this.lockTime = lockTime;
	}
}
