package com.seeyon.v3x.office.common.domain;

public class OfficeLossInfoBean extends OfficeLossInfo {

	private String resourceName;		//资源名
	private String createUserName;		//登记人
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	
	
}
