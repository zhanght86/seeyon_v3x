package com.seeyon.v3x.organization.webmodel;

import com.seeyon.v3x.organization.domain.V3xOrgRelationship;

public class WebV3xOrgRelationship {
	
	private V3xOrgRelationship v3xOrgRelationship;
	
	private Long orgPostId;
	
	private Long orgAccountId;
	
	private String str;

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public Long getOrgAccountId() {
		return orgAccountId;
	}

	public void setOrgAccountId(Long orgAccountId) {
		this.orgAccountId = orgAccountId;
	}

	public Long getOrgPostId() {
		return orgPostId;
	}

	public void setOrgPostId(Long orgPostId) {
		this.orgPostId = orgPostId;
	}

	public V3xOrgRelationship getV3xOrgRelationship() {
		return v3xOrgRelationship;
	}

	public void setV3xOrgRelationship(V3xOrgRelationship orgRelationship) {
		v3xOrgRelationship = orgRelationship;
	}

}
