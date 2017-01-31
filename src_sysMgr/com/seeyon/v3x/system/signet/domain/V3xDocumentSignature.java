package com.seeyon.v3x.system.signet.domain;

public class V3xDocumentSignature extends com.seeyon.v3x.common.domain.BaseModel implements
java.io.Serializable  {
	
	private static final long serialVersionUID = 1L;
	private String hostname;
	private String markguid;
	private String markname;
	private String recordId;
	private java.sql.Timestamp signDate;
	private String username;

    public V3xDocumentSignature() {
    }

	public String getHostname() {
		return this.hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getMarkguid() {
		return this.markguid;
	}
	public void setMarkguid(String markguid) {
		this.markguid = markguid;
	}

	public String getMarkname() {
		return this.markname;
	}
	public void setMarkname(String markname) {
		this.markname = markname;
	}

	public String getRecordId() {
		return this.recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public java.sql.Timestamp getSignDate() {
		return this.signDate;
	}
	public void setSignDate(java.sql.Timestamp signDate) {
		this.signDate = signDate;
	}

	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
}
