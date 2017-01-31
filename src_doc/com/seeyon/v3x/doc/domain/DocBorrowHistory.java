package com.seeyon.v3x.doc.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 借阅历史，暂时未使用
 */
@Deprecated
public class DocBorrowHistory extends BaseModel {
//	default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private long userId;
	private String userType;
	private Byte borrowType;
	private java.sql.Timestamp sdate;
	private java.sql.Timestamp edate;
	private long docResourceId;
	public Byte getBorrowType() {
		return borrowType;
	}
	public void setBorrowType(Byte borrowType) {
		this.borrowType = borrowType;
	}
	public long getDocResourceId() {
		return docResourceId;
	}
	public void setDocResourceId(long docResourceId) {
		this.docResourceId = docResourceId;
	}
	public java.sql.Timestamp getEdate() {
		return edate;
	}
	public void setEdate(java.sql.Timestamp edate) {
		this.edate = edate;
	}
	public java.sql.Timestamp getSdate() {
		return sdate;
	}
	public void setSdate(java.sql.Timestamp sdate) {
		this.sdate = sdate;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
}
