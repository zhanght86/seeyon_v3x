package com.seeyon.v3x.collaboration.templete.domain;

import java.io.Serializable;

/**
 * The persistent class for the v3x_templete_auth database table.
 * 
 * @author BEA Workshop Studio
 */
public class TempleteAuth extends com.seeyon.v3x.common.domain.BaseModel
		implements Serializable {

	private static final long serialVersionUID = -6565491137991889357L;
	
	public static final String ENTITY_NAME = TempleteAuth.class.getName();

	public static final String PROP_authId = "authId";

	public static final String PROP_authType = "authType";

	public static final String PROP_sort = "sort";

	public static final String PROP_objectId = "objectId";
	
	private Long authId;

	private String authType;

	private Integer sort;

	private long objectId;
	
	public TempleteAuth() {
	}

	public Long getAuthId() {
		return this.authId;
	}

	public void setAuthId(Long authId) {
		this.authId = authId;
	}

	public String getAuthType() {
		return this.authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public Integer getSort() {
		return this.sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

}