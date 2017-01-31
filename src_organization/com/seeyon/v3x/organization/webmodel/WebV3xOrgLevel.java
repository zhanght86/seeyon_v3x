package com.seeyon.v3x.organization.webmodel;

import com.seeyon.v3x.organization.domain.V3xOrgLevel;

public class WebV3xOrgLevel {

	private V3xOrgLevel v3xOrgLevel;
	private Integer groupLevelId;
	
	public Integer getGroupLevelId() {
		return groupLevelId;
	}
	public void setGroupLevelId(Integer groupLevelId) {
		this.groupLevelId = groupLevelId;
	}
	public V3xOrgLevel getV3xOrgLevel() {
		return v3xOrgLevel;
	}
	public void setV3xOrgLevel(V3xOrgLevel orgLevel) {
		v3xOrgLevel = orgLevel;
	}

}
