/**
 * 
 */
package com.seeyon.v3x.main.section;

import java.io.Serializable;

import com.seeyon.v3x.common.ObjectToXMLBase;

/**
 * @author dongyj
 *
 */
public class SectionProperty extends ObjectToXMLBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6532970240546676599L;
	
	private SectionReference[] reference;

	private String sectionId;
	
	private boolean isReadOnly;
	
	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public SectionReference[] getReference() {
		return reference;
	}

	public void setReference(SectionReference[] reference) {
		this.reference = reference;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	
}
