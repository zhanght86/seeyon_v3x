package com.seeyon.v3x.collaboration.webmodel;

import java.util.Date;

public class ColSuperviseModel {
	private Long id;
	private String title;
	private long sender;
	private Date sendDate;
	private String caseLogXML;
	private String caseProcessXML;
	private String caseWorkItemLogXML;
	private String processDescBy;
	private String supervisor;
	private Date awakeDate;
	private String description;
	private Integer count;
	private Integer remindModel; // ?
	private Boolean hasWorkflow;
	private Long caseId;
	private String content;
	private Long summaryId;
	private Integer status;
	private Long deadline = 0L;
	private boolean canModify;
	private Integer entityType;
	private Boolean workflowTimeout = false;//流程超期
	private Boolean isRed = false;//督办日期是否超期	
	private Integer importantLevel = 1;      //重要程度
	private Integer appType;
    private Integer newflowType = -1; //新流程类型
    private Integer resendTime ;//重新发起次数
    private String forwardMember;
    private String bodyType;
    private Boolean hasAttachment;
    
	public Boolean getHasAttachment() {
		return hasAttachment;
	}
	public void setHasAttachment(Boolean hasAttachment) {
		this.hasAttachment = hasAttachment;
	}
	public String getBodyType() {
		return bodyType;
	}
	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}
	public Integer getImportantLevel() {
		return importantLevel;
	}
	/**
	 * @return the isRed
	 */
	public Boolean getIsRed() {
		return isRed;
	}
	/**
	 * @param isRed the isRed to set
	 */
	public void setIsRed(Boolean isRed) {
		this.isRed = isRed;
	}
	public void setImportantLevel(Integer importantLevel) {
		this.importantLevel = importantLevel;
	}
	public Integer getEntityType() {
		return entityType;
	}
	public void setEntityType(Integer entityType) {
		this.entityType = entityType;
	}
	public boolean isCanModify() {
		return canModify;
	}
	public void setCanModify(boolean canModify) {
		this.canModify = canModify;
	}
	public Date getAwakeDate() {
		return awakeDate;
	}
	public void setAwakeDate(Date awakeDate) {
		this.awakeDate = awakeDate;
	}
	public Long getCaseId() {
		return caseId;
	}
	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}
	public String getCaseLogXML() {
		return caseLogXML;
	}
	public void setCaseLogXML(String caseLogXML) {
		this.caseLogXML = caseLogXML;
	}
	public String getCaseProcessXML() {
		return caseProcessXML;
	}
	public void setCaseProcessXML(String caseProcessXML) {
		this.caseProcessXML = caseProcessXML;
	}
	public String getCaseWorkItemLogXML() {
		return caseWorkItemLogXML;
	}
	public void setCaseWorkItemLogXML(String caseWorkItemLogXML) {
		this.caseWorkItemLogXML = caseWorkItemLogXML;
	}
	public Long getSummaryId() {
		return summaryId;
	}
	public void setSummaryId(Long summaryId) {
		this.summaryId = summaryId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getHasWorkflow() {
		return hasWorkflow;
	}
	public void setHasWorkflow(Boolean hasWorkflow) {
		this.hasWorkflow = hasWorkflow;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getProcessDescBy() {
		return processDescBy;
	}
	public void setProcessDescBy(String processDescBy) {
		this.processDescBy = processDescBy;
	}
	public Integer getRemindModel() {
		return remindModel;
	}
	public void setRemindModel(Integer remindModel) {
		this.remindModel = remindModel;
	}
	public long getSender() {
		return sender;
	}
	public void setSender(long sender) {
		this.sender = sender;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getSupervisor() {
		return supervisor;
	}
	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getSendDate() {
		return sendDate;
	}
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	public Boolean getWorkflowTimeout() {
		return workflowTimeout;
	}
	public void setWorkflowTimeout(Boolean workflowTimeout) {
		this.workflowTimeout = workflowTimeout;
	}
	public Long getDeadline() {
		return deadline;
	}
	public void setDeadline(Long deadline) {
		this.deadline = deadline;
	}
	/**
	 * @return the appType
	 */
	public Integer getAppType() {
		return appType;
	}
	/**
	 * @param appType the appType to set
	 */
	public void setAppType(Integer appType) {
		this.appType = appType;
	}
    public Integer getNewflowType() {
        return newflowType;
    }
    public void setNewflowType(Integer newflowType) {
        this.newflowType = newflowType;
    }
	public Integer getResendTime() {
		return resendTime;
	}
	public void setResendTime(Integer resendTime) {
		this.resendTime = resendTime;
	}
	
	public String getForwardMember() {
		return forwardMember;
	}
	
	public void setForwardMember(String forwardMember) {
		this.forwardMember = forwardMember;
	}
	
}
