package com.seeyon.v3x.link.webmodel;

import com.seeyon.v3x.link.domain.LinkSystem;

public class LinkMenuVo {
	private LinkSystem linkSystem;
	private String 	   linkCategory;
	private String     isSystem;
	private String     creatorName;
	private String 	   url;
	// 有参数设置
	private boolean    hasOptions;
	
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public String getIsSystem() {
		return isSystem;
	}
	public void setIsSystem(String isSystem) {
		this.isSystem = isSystem;
	}
	public String getLinkCategory() {
		return linkCategory;
	}
	public void setLinkCategory(String linkCategory) {
		this.linkCategory = linkCategory;
	}
	public LinkSystem getLinkSystem() {
		return linkSystem;
	}
	public void setLinkSystem(LinkSystem linkSystem) {
		this.linkSystem = linkSystem;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean getHasOptions() {
		return hasOptions;
	}
	public void setHasOptions(boolean hasOptions) {
		this.hasOptions = hasOptions;
	}
}
