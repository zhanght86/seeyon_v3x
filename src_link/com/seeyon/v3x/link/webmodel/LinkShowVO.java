package com.seeyon.v3x.link.webmodel;

import com.seeyon.v3x.link.domain.LinkSystem;

public class LinkShowVO {
	private LinkSystem linkSystem;
	
	private String icon;
	private String name;
	private String link;
	
	public LinkShowVO(LinkSystem linkSystem){
		this.linkSystem = linkSystem;
		this.name = linkSystem.getName();
	}
	
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public LinkSystem getLinkSystem() {
		return linkSystem;
	}
	public void setLinkSystem(LinkSystem linkSystem) {
		this.linkSystem = linkSystem;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
