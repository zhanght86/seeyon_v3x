/**
 * 
 */
package com.seeyon.v3x.plugin.deeSection.domain;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 * 
 */
public class DeeSectionProps extends BaseModel implements Serializable{

	private static final long serialVersionUID = 2120387494886271189L;

	private long deeSectionId;

	private String propName;

	private String propValue;
	
	private String propMeta;
	
	private int isShow;
	
	private int sort;

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getPropMeta() {
		return propMeta;
	}

	public void setPropMeta(String propMeta) {
		this.propMeta = propMeta;
	}

	public int getIsShow() {
		return isShow;
	}

	public void setIsShow(int isShow) {
		this.isShow = isShow;
	}

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

	public long getDeeSectionId() {
		return deeSectionId;
	}

	public void setDeeSectionId(long deeSectionId) {
		this.deeSectionId = deeSectionId;
	}

}
