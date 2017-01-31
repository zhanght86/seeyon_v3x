package com.seeyon.v3x.link.domain;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the link_category database table.
 * 
 * @author BEA Workshop Studio
 */
public class LinkCategory extends BaseModel implements Serializable, Comparable<LinkCategory> {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
//	private Long id;
	private java.sql.Timestamp createTime;
	private long createUserId;
	private String description;
	private Byte isSystem;
	private java.sql.Timestamp lastUpdate;
	private Long lastUserId;
	private String name;
	private int  orderNum;
	
	private Set linkAcl;
	private Set linkSystem;

    public Set getLinkSystem() {
		return linkSystem;
	}

	public void setLinkSystem(Set linkSystem) {
		this.linkSystem = linkSystem;
	}

	public Set getLinkAcl() {
		return linkAcl;
	}

	public void setLinkAcl(Set linkAcl) {
		this.linkAcl = linkAcl;
	}

	public LinkCategory() {
    }

	public java.sql.Timestamp getCreateTime() {
		return this.createTime;
	}
	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	public long getCreateUserId() {
		return this.createUserId;
	}
	public void setCreateUserId(long createUserId) {
		this.createUserId = createUserId;
	}

	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Byte getIsSystem() {
		return this.isSystem;
	}
	public void setIsSystem(Byte isSystem) {
		this.isSystem = isSystem;
	}

	public java.sql.Timestamp getLastUpdate() {
		return this.lastUpdate;
	}
	public void setLastUpdate(java.sql.Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Long getLastUserId() {
		return this.lastUserId;
	}
	public void setLastUserId(Long lastUserId) {
		this.lastUserId = lastUserId;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}

	public int getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public int compareTo(LinkCategory o) {
		if(this.orderNum <= o.orderNum)
			return -1;
		return 1;
	}
}