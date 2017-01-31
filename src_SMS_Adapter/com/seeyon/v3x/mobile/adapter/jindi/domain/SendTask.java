package com.seeyon.v3x.mobile.adapter.jindi.domain;

public class SendTask {
	private Long taskId;
	private String destNumber;
	private String content;
	private String signName;
	private Long sendPriority;
	private java.sql.Timestamp sendTime;
	private String statusReport;
	private String englishFlag;
	private Long MsgTyep;
	private String pushUrl;
	private Long recAction;
	private Long validMinute;
	private Long sendFlag;
	private Long commPort;
	private Long splitCount;
	private String batchId;
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public Long getCommPort() {
		return commPort;
	}
	public void setCommPort(Long commPort) {
		this.commPort = commPort;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getDestNumber() {
		return destNumber;
	}
	public void setDestNumber(String destNumber) {
		this.destNumber = destNumber;
	}
	public String getEnglishFlag() {
		return englishFlag;
	}
	public void setEnglishFlag(String englishFlag) {
		this.englishFlag = englishFlag;
	}
	public Long getMsgTyep() {
		return MsgTyep;
	}
	public void setMsgTyep(Long msgTyep) {
		MsgTyep = msgTyep;
	}
	public String getPushUrl() {
		return pushUrl;
	}
	public void setPushUrl(String pushUrl) {
		this.pushUrl = pushUrl;
	}
	public Long getRecAction() {
		return recAction;
	}
	public void setRecAction(Long recAction) {
		this.recAction = recAction;
	}
	public Long getSendFlag() {
		return sendFlag;
	}
	public void setSendFlag(Long sendFlag) {
		this.sendFlag = sendFlag;
	}
	public Long getSendPriority() {
		return sendPriority;
	}
	public void setSendPriority(Long sendPriority) {
		this.sendPriority = sendPriority;
	}
	public java.sql.Timestamp getSendTime() {
		return sendTime;
	}
	public void setSendTime(java.sql.Timestamp sendTime) {
		this.sendTime = sendTime;
	}
	public String getSignName() {
		return signName;
	}
	public void setSignName(String signName) {
		this.signName = signName;
	}
	public Long getSplitCount() {
		return splitCount;
	}
	public void setSplitCount(Long splitCount) {
		this.splitCount = splitCount;
	}
	public String getStatusReport() {
		return statusReport;
	}
	public void setStatusReport(String statusReport) {
		this.statusReport = statusReport;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public Long getValidMinute() {
		return validMinute;
	}
	public void setValidMinute(Long validMinute) {
		this.validMinute = validMinute;
	}

	
}
