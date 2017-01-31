package com.seeyon.v3x.system.ipcontrol.domain;

import com.seeyon.v3x.common.domain.BaseModel;




public class V3xIpcontrol  extends BaseModel {

	private static final long serialVersionUID = 7928023112237506732L;

	// fields
	private java.lang.String address;
	private java.util.Date createTime;
	private java.util.Date modifyTime;
	private java.lang.Long createUser;
	private java.lang.String name;
	private java.lang.Integer type;
	private java.lang.String users;
	private java.lang.Long accountId;
	
	public java.lang.String getAddress() {
		return address;
	}
	public void setAddress(java.lang.String address) {
		this.address = address;
	}
	public java.util.Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}
	public java.util.Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(java.util.Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	public java.lang.String getName() {
		return name;
	}
	public void setName(java.lang.String name) {
		this.name = name;
	}
	public java.lang.Integer getType() {
		return type;
	}
	public void setType(java.lang.Integer type) {
		this.type = type;
	}
	public java.lang.String getUsers() {
		return users;
	}
	public void setUsers(java.lang.String users) {
		this.users = users;
	}
	public java.lang.Long getAccountId() {
		return accountId;
	}
	public void setAccountId(java.lang.Long accountId) {
		this.accountId = accountId;
	}
	public java.lang.Long getCreateUser() {
		return createUser;
	}
	public void setCreateUser(java.lang.Long createUser) {
		this.createUser = createUser;
	}
}