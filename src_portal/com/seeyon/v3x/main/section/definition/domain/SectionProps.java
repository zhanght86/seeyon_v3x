/**
 * 
 */
package com.seeyon.v3x.main.section.definition.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 * 
 */
public class SectionProps extends BaseModel implements Serializable{

	private static final long serialVersionUID = 2120387494886271189L;

	private long sectionDefinitionId;

	private String propName;

	private String propValue;

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public String getPropValue() {
		return propValue;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}

	public long getSectionDefinitionId() {
		return sectionDefinitionId;
	}

	public void setSectionDefinitionId(long sectionDefinitionId) {
		this.sectionDefinitionId = sectionDefinitionId;
	}

}
