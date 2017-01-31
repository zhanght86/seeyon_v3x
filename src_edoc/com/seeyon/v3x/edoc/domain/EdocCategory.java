package com.seeyon.v3x.edoc.domain;

import com.seeyon.v3x.common.domain.BaseModel;

public class EdocCategory extends BaseModel {
	private static final long serialVersionUID = 7709614283781066137L;
	
	private Long id;
	private String name;
	private Long rootCategory;
	private Integer state;
	private Integer storeType;
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	private Long accountId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getRootCategory() {
		return rootCategory;
	}
	public void setRootCategory(Long rootCategory) {
		this.rootCategory = rootCategory;
	}
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public Integer getStoreType() {
		return storeType;
	}
	public void setStoreType(Integer storeType) {
		this.storeType = storeType;
	}
}
