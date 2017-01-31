package com.seeyon.v3x.collaboration.domain;



/**
 * User: jincm Date: 2008-6-6 Time: 10:35:19
 */
public class WorkflowData {
	//协同摘要ID
	public String summaryId;
	
	//应用类型
	public String appType;

	//标题
	public String subject;

	//发起者
	public String initiator;
	
	//所属部门名称
	public String depName;
	
	//发起时间
	public java.sql.Timestamp sendTime;
	
	//流程定义Id
	public String processId;
	
	//流程实例Id
	public long caseId;
	
	//是否结束标记
	public int endFlag = 1;
	
	//处理期限
	public Long deadLine;
	
	//提前提醒
	public Long advanceRemind;
	
	//应用类型
	public String appEnumStr;

    //单位ID
    public Long accountId; 
    
    public String forwardMember;
    
    public Integer resentTime;
    
    public Integer newflowType = -1; //新流程类型，用于标记新流程
    
    public Boolean  isFromTemplete = false ;
    
    private Long templeteId;
    
	public Long getTempleteId() {
		return templeteId;
	}

	public void setTempleteId(Long templeteId) {
		this.templeteId = templeteId;
	}

	public WorkflowData() {
	}

	public String getSummaryId() {
		return summaryId;
	}

	public void setSummaryId(String summaryId) {
		this.summaryId = summaryId;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getDepName() {
		return depName;
	}

	public void setDepName(String depName) {
		this.depName = depName;
	}

	public String getInitiator() {
		return initiator;
	}

	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}

	public java.sql.Timestamp getSendTime() {
		return sendTime;
	}

	public void setSendTime(java.sql.Timestamp sendTime) {
		this.sendTime = sendTime;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public long getCaseId() {
		return caseId;
	}

	public void setCaseId(long caseId) {
		this.caseId = caseId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public Long getAdvanceRemind() {
		return advanceRemind;
	}

	public void setAdvanceRemind(Long advanceRemind) {
		this.advanceRemind = advanceRemind;
	}

	public Long getDeadLine() {
		return deadLine;
	}

	public void setDeadLine(Long deadLine) {
		this.deadLine = deadLine;
	}

	public String getAppEnumStr() {
		return appEnumStr;
	}

	public void setAppEnumStr(String appEnumStr) {
		this.appEnumStr = appEnumStr;
	}
    
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Integer getNewflowType() {
        return newflowType;
    }

    public void setNewflowType(Integer newflowType) {
        this.newflowType = newflowType;
    }

	public int getEndFlag() {
		return endFlag;
	}

	public void setEndFlag(int endFlag) {
		this.endFlag = endFlag;
	}

	public String getForwardMember() {
		return forwardMember;
	}

	public void setForwardMember(String forwardMember) {
		this.forwardMember = forwardMember;
	}

	public Integer getResentTime() {
		return resentTime;
	}

	public void setResentTime(Integer resentTime) {
		this.resentTime = resentTime;
	}

	public Boolean getIsFromTemplete() {
		return isFromTemplete;
	}

	public void setIsFromTemplete(Boolean isFromTemplete) {
		this.isFromTemplete = isFromTemplete;
	}
	
}
