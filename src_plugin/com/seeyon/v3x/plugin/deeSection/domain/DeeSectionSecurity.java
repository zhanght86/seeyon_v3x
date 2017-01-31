package com.seeyon.v3x.plugin.deeSection.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

public class DeeSectionSecurity extends BaseModel implements Serializable{

	private static final long serialVersionUID = -7911048704551968765L;

	private long deeSectionId;

	private long entityId;

	private String entityType;

	private int sort;
	
	public DeeSectionSecurity(){
		
	}
	
	public DeeSectionSecurity(long deeSectionId,long entityId,String entityType,Integer sort){
		super();
		this.deeSectionId = deeSectionId;
		this.entityId = entityId;
		this.entityType = entityType;
		this.sort = sort;
	}
	
	public long getDeeSectionId() {
		return deeSectionId;
	}

	public void setDeeSectionId(long deeSectionId) {
		this.deeSectionId = deeSectionId;
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
}
