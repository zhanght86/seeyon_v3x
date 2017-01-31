package com.seeyon.v3x.link.webmodel;

import java.util.List;
import java.util.Set;

import com.seeyon.v3x.link.domain.LinkCategory;
import com.seeyon.v3x.link.domain.LinkOption;
import com.seeyon.v3x.link.domain.LinkSystem;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;

public class LinkModifyVo {
	private LinkSystem linkSystem;
	private List<LinkOption> linkOption;
	private List<V3xOrgEntity> entity;
	private LinkCategory category;

	public LinkCategory getCategory() {
		return category;
	}
	public void setCategory(LinkCategory category) {
		this.category = category;
	}
	public List<V3xOrgEntity> getEntity() {
		return entity;
	}
	public void setEntity(List<V3xOrgEntity> entity) {
		this.entity = entity;
	}

	public LinkSystem getLinkSystem() {
		return linkSystem;
	}
	public void setLinkSystem(LinkSystem linkSystem) {
		this.linkSystem = linkSystem;
	}
	public List<LinkOption> getLinkOption() {
		return linkOption;
	}
	public void setLinkOption(List<LinkOption> linkOption) {
		this.linkOption = linkOption;
	}
	
}
