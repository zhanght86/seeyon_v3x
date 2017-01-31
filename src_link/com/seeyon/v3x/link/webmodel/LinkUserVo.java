package com.seeyon.v3x.link.webmodel;

import com.seeyon.v3x.link.domain.LinkOption;
import com.seeyon.v3x.link.domain.LinkOptionValue;

public class LinkUserVo {
	private LinkOption linkOption;
	private LinkOptionValue linkOptionValue;
	private boolean password;
	private boolean defaultValue;
	
	public boolean isPassword() {
		return password;
	}
	public void setPassword(boolean password) {
		this.password = password;
	}
	public LinkOption getLinkOption() {
		return linkOption;
	}
	public void setLinkOption(LinkOption linkOption) {
		this.linkOption = linkOption;
	}
	public LinkOptionValue getLinkOptionValue() {
		return linkOptionValue;
	}
	public void setLinkOptionValue(LinkOptionValue linkOptionValue) {
		this.linkOptionValue = linkOptionValue;
	}
	public boolean isDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}
}
