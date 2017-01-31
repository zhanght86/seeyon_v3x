package com.seeyon.v3x.resource.domain;

import java.io.Serializable;
import java.util.List;

import com.seeyon.v3x.bbs.domain.V3xBbsArticle;
import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the notepage database table.
 * 
 * @author BEA Workshop Studio
 */
public class Resource extends BaseModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 类型
	 */	
	private String type;
	/**
	 * 描述
	 */
	private String description;
	
    private List resourceIpp;
    
    private Long accountId;//单位ID
	
    private java.util.Set<Resource> resource;
    
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public List getResourceIpp() {
		return resourceIpp;
	}
	public void setResourceIpp(List resourceIpp) {
		this.resourceIpp = resourceIpp;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public java.util.Set<Resource> getResource() {
		return resource;
	}
	public void setResource(java.util.Set<Resource> resource) {
		this.resource = resource;
	}
}