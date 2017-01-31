package com.seeyon.v3x.space.domain;

import java.io.Serializable;

/**
 * The persistent class for the v3x_space_security database table.
 * 
 * @author BEA Workshop Studio
 */
public class SpaceSecurity extends com.seeyon.v3x.common.domain.BaseModel
		implements Serializable {

	private static final long serialVersionUID = -8770453780631491891L;
	
	public static enum SecurityType {
		used, //使用权限
		manager, //管理权限
		vistor,//访问权限
	}

	private int securityType;
	
	private Long entityId;

	private String entityType;

	private Integer sort;

	private Long spaceFixId;

	public SpaceSecurity() {
	}
	
	public SpaceSecurity( Long spaceFixId, SpaceSecurity.SecurityType securityType, String entityType, Long entityId, Integer sortId) {
		this.setIdIfNew();
		this.spaceFixId = spaceFixId;
		this.securityType = securityType.ordinal();
		this.entityType = entityType;
		this.entityId = entityId;
		this.sort = sortId;
	}

	public Long getEntityId() {
		return this.entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return this.entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Integer getSort() {
		return this.sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Long getSpaceFixId() {
		return spaceFixId;
	}

	public void setSpaceFixId(Long spaceFixId) {
		this.spaceFixId = spaceFixId;
	}

	public int getSecurityType() {
		return securityType;
	}

	public void setSecurityType(int securityType) {
		this.securityType = securityType;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 0;
		result = prime * result + ((entityId == null) ? 0 : entityId.hashCode());
		result = prime * result + ((entityType == null) ? 0 : entityType.hashCode());
		result = prime * result + securityType;
		result = prime * result + ((spaceFixId == null) ? 0 : spaceFixId.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		SpaceSecurity other = (SpaceSecurity) obj;
		if (entityId == null) {
			if (other.entityId != null)
				return false;
		}
		else if (!entityId.equals(other.entityId))
			return false;
		if (entityType == null) {
			if (other.entityType != null)
				return false;
		}
		else if (!entityType.equals(other.entityType))
			return false;
		if (securityType != other.securityType)
			return false;
		if (spaceFixId == null) {
			if (other.spaceFixId != null)
				return false;
		}
		else if (!spaceFixId.equals(other.spaceFixId))
			return false;
		return true;
	}
	
}