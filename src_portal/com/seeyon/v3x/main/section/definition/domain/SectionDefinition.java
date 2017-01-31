/**
 * 
 */
package com.seeyon.v3x.main.section.definition.domain;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * @author <a href="tanmf@seeyon.com">Tanmf</a>
 * 
 */
public class SectionDefinition extends BaseModel implements Comparable<SectionDefinition>, Serializable{

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SectionDefinition other = (SectionDefinition) obj;
		if (createDate == null) {
			if (other.createDate != null)
				return false;
		} else if (!createDate.equals(other.createDate))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sort != other.sort)
			return false;
		if (state != other.state)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	private static final long serialVersionUID = 9128444281883580562L;

	public static enum State {
		normal, invalidation
	}

	public static enum Type {
		SSOWebcontent, SSOIframe, Iframe,
	}

	private int type;

	private String name;

	private int state = State.normal.ordinal();

	private Date createDate;
	
	private int sort;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int compareTo(SectionDefinition o) {
		if(this.getSort() == o.getSort()){
			return this.getCreateDate().compareTo(o.getCreateDate());
		}
		else{
			return this.getSort() < o.getSort() ? -1 : 1;
		}
	}



}
