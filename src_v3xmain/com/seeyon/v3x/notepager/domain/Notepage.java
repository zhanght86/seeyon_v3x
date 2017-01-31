package com.seeyon.v3x.notepager.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the notepage database table.
 * 
 * @author BEA Workshop Studio
 */
public class Notepage extends BaseModel implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private String content;
	private java.sql.Timestamp createDate;
	private long memberId;
	private java.sql.Timestamp updateDate;

    public Notepage() {
    }

	public String getContent() {
		return this.content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public java.sql.Timestamp getCreateDate() {
		return this.createDate;
	}
	public void setCreateDate(java.sql.Timestamp createDate) {
		this.createDate = createDate;
	}

	public long getMemberId() {
		return this.memberId;
	}
	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}

	public java.sql.Timestamp getUpdateDate() {
		return this.updateDate;
	}
	public void setUpdateDate(java.sql.Timestamp updateDate) {
		this.updateDate = updateDate;
	}

	public String toString() { 
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}