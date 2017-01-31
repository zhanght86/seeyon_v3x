package com.seeyon.v3x.log.tools;

import com.seeyon.v3x.common.operationlog.domain.OperationLog;

public class WebLogModel {

	private OperationLog operationLog;
	private String personnel;
	private String contentValue;
	private String category;
	private String ipAddress;
	private String accountName;
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getContentValue() {
		return contentValue;
	}
	public void setContentValue(String contentValue) {
		this.contentValue = contentValue;
	}
	public OperationLog getOperationLog() {
		return operationLog;
	}
	public void setOperationLog(OperationLog operationLog) {
		this.operationLog = operationLog;
	}
	public String getPersonnel() {
		return personnel;
	}
	public void setPersonnel(String personnel) {
		this.personnel = personnel;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
}
