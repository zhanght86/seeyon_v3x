package com.seeyon.v3x.online;

import java.io.Serializable;

import com.seeyon.v3x.organization.domain.V3xOrgAccount;

public class OnlineAccountModel extends com.seeyon.v3x.common.domain.BaseModel implements Serializable {

	private static final long serialVersionUID = 3708627939824649560L;

	private V3xOrgAccount account;
	private Long id;
	private Long superior;
	private String name;

	public V3xOrgAccount getAccount() {
		return account;
	}

	public void setAccount(V3xOrgAccount account) {
		this.account = account;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSuperior() {
		return superior;
	}

	public void setSuperior(Long superior) {
		this.superior = superior;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
