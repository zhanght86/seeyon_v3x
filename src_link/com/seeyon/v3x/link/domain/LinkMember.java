package com.seeyon.v3x.link.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;


public class LinkMember extends BaseModel implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private long memberid;

	private long linkSystemId;
	
	private int userLinkSort = 0;
	
	public LinkMember() {
	}

	public long getMemberid() {
		return this.memberid;
	}

	public void setMemberid(long memberid) {
		this.memberid = memberid;
	}

	public long getLinkSystemId() {
		return linkSystemId;
	}

	public void setLinkSystemId(long linkSystemId) {
		this.linkSystemId = linkSystemId;
	}

	public int getUserLinkSort() {
		return userLinkSort;
	}

	public void setUserLinkSort(int userLinkSort) {
		this.userLinkSort = userLinkSort;
	}

}