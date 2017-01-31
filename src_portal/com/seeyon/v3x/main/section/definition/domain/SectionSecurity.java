/**
 * 
 */
package com.seeyon.v3x.main.section.definition.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 * 
 */
public class SectionSecurity extends BaseModel {
	private static final long serialVersionUID = 8888937831971269502L;

	private long sectionDefinitionId;

	private long entityId;

	private String entityType;

	private int sort;

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

	public long getSectionDefinitionId() {
		return sectionDefinitionId;
	}

	public void setSectionDefinitionId(long sectionDefinitionId) {
		this.sectionDefinitionId = sectionDefinitionId;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

}
