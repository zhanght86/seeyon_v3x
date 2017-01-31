package com.seeyon.v3x.office.admin.domain;

import java.util.*;

public class MAdminInfo {
	private long admin;
	private String admin_model;
	private List depArr;
	private String depStr;
	private String depIdArr;
	private String accountIds;
	private Date createDate;
	private Date modifyDate;
	
	private String adminName;
	private String modelName;
	
	private Long domainId;
    
 	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}	
	
	public long getAdmin() {
		return admin;
	}
	public void setAdmin(long admin) {
		this.admin = admin;
	}
	public String getAdmin_model() {
		return admin_model;
	}
	public void setAdmin_model(String admin_model) {
		this.admin_model = admin_model;
	}
	public List getDepArr() {
		return depArr;
	}
	public void setDepArr(List depArr) {
		this.depArr = depArr;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getDepStr() {
		return depStr;
	}
	public void setDepStr(String depStr) {
		this.depStr = depStr;
	}
	public String getDepIdArr() {
		return depIdArr;
	}
	public void setDepIdArr(String depIdArr) {
		this.depIdArr = depIdArr;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	public String getAccountIds() {
		return accountIds;
	}
	public void setAccountIds(String accountIds) {
		this.accountIds = accountIds;
	}
}
