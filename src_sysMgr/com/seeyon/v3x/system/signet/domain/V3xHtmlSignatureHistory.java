package com.seeyon.v3x.system.signet.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.seeyon.v3x.common.domain.BaseModel;

public class V3xHtmlSignatureHistory extends BaseModel {

	
	private static final long serialVersionUID = 1L;
	private java.sql.Timestamp dateTime;
	private String fieldName;
	private String hostName;
	private String markGuid;
	private String markName;
	private Long summaryId;
	private String userName;
	
	private String dateTimeStr;
	
	public String getDateTimeStr()
	{
	  return this.dateTimeStr;
	}
	public void setDateTimeStr(String dateTimeStr)
	{
	    this.dateTimeStr=dateTimeStr;
	}

    public V3xHtmlSignatureHistory() {
    }	

	public java.sql.Timestamp getDateTime() {
		return this.dateTime;
	}
	public void setDateTime(java.sql.Timestamp dateTime) {
		this.dateTime = dateTime;
	}

	public String getFieldName() {
		return this.fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getHostName() {
		return this.hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getMarkGuid() {
		return this.markGuid;
	}
	public void setMarkGuid(String markGuid) {
		this.markGuid = markGuid;
	}

	public String getMarkName() {
		return this.markName;
	}
	public void setMarkName(String markName) {
		this.markName = markName;
	}

	public Long getSummaryId() {
		return this.summaryId;
	}
	public void setSummaryId(Long summaryId) {
		this.summaryId = summaryId;
	}

	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("id", getId())
			.toString();
	}
	
	
}
