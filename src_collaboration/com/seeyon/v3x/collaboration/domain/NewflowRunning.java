package com.seeyon.v3x.collaboration.domain;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 分段流程 - 新流程发起
 * @author <a href="mailto:fishsoul@126.com">Mazc</a>
 */
public class NewflowRunning extends BaseModel implements Serializable
{
    private static final long serialVersionUID = 3867276807526761215L;
    private String triggerCondition;
    private String conditionTitle;
    private String conditionBase;
    private Boolean isForce = false;
    private String sender;
    private int flowRelateType;
    private Boolean isCanViewByMainFlow = false;
    private Boolean isCanViewMainFlow = false;
    private Long mainSummaryId;
    private String mainNodeId;
    private Long mainCaseId;
    private String mainProcessId;
    private Long mainTempleteId;
    private Long mainFormId;
    private Long mainAffairId;
    private Long mainRecordId;//无流程表单主键值Id
	private int mainFormType;//来自类型：1：有流程表单;2：基础信息;3：信息管理
	private Long summaryId;
    private Long caseId;
    private String processId;
    private Long templeteId;
    private Long senderId;
    private Long affairId;
    private Integer affairState;
    private Boolean isActivate = false;
    private Boolean isDelete = false;
    private Date createTime;
    private Date updateTime;
    
    /**
	 * @return the mainFormType
	 */
	public int getMainFormType() {
		return mainFormType;
	}
	/**
	 * @param mainFormType the mainFormType to set
	 */
	public void setMainFormType(int mainFormType) {
		this.mainFormType = mainFormType;
	}
    /**
	 * @return the mainRecordId
	 */
	public Long getMainRecordId() {
		return mainRecordId;
	}
	/**
	 * @param mainRecordId the mainRecordId to set
	 */
	public void setMainRecordId(Long mainRecordId) {
		this.mainRecordId = mainRecordId;
	}
    
    public int getFlowRelateType() {
        return flowRelateType;
    }
    public void setFlowRelateType(int flowRelateType) {
        this.flowRelateType = flowRelateType;
    }
    public Boolean getIsForce() {
        return isForce;
    }
    public void setIsForce(Boolean isForce) {
        this.isForce = isForce;
    }
    public Boolean getIsActivate() {
        return isActivate;
    }
    public void setIsActivate(Boolean isActivate) {
        this.isActivate = isActivate;
    }
    public String getTriggerCondition() {
        return triggerCondition;
    }
    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }
    public Long getAffairId() {
        return affairId;
    }
    public void setAffairId(Long affairId) {
        this.affairId = affairId;
    }
    public java.util.Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }
    public Long getCaseId() {
        return caseId;
    }
    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }
    public Long getMainCaseId() {
        return mainCaseId;
    }
    public void setMainCaseId(Long mainCaseId) {
        this.mainCaseId = mainCaseId;
    }
    public Long getMainFormId() {
        return mainFormId;
    }
    public void setMainFormId(Long mainFormId) {
        this.mainFormId = mainFormId;
    }
    public String getMainNodeId() {
        return mainNodeId;
    }
    public void setMainNodeId(String mainNodeId) {
        this.mainNodeId = mainNodeId;
    }
    public Long getMainSummaryId() {
        return mainSummaryId;
    }
    public void setMainSummaryId(Long mainSummaryId) {
        this.mainSummaryId = mainSummaryId;
    }
    public Long getMainTempleteId() {
        return mainTempleteId;
    }
    public void setMainTempleteId(Long mainTempleteId) {
        this.mainTempleteId = mainTempleteId;
    }
    public Long getMainAffairId() {
        return mainAffairId;
    }
    public void setMainAffairId(Long mainAffairId) {
        this.mainAffairId = mainAffairId;
    }
    public Long getSenderId() {
        return senderId;
    }
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
    public String getMainProcessId() {
        return mainProcessId;
    }
    public void setMainProcessId(String mainProcessId) {
        this.mainProcessId = mainProcessId;
    }
    public String getProcessId() {
        return processId;
    }
    public void setProcessId(String processId) {
        this.processId = processId;
    }
    public Integer getAffairState() {
        return affairState;
    }
    public void setAffairState(Integer affairState) {
        this.affairState = affairState;
    }
    public Long getSummaryId() {
        return summaryId;
    }
    public void setSummaryId(Long summaryId) {
        this.summaryId = summaryId;
    }
    public Long getTempleteId() {
        return templeteId;
    }
    public void setTempleteId(Long templeteId) {
        this.templeteId = templeteId;
    }
    public java.util.Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(java.util.Date updateTime) {
        this.updateTime = updateTime;
    }
    public String getConditionBase() {
        return conditionBase;
    }
    public void setConditionBase(String conditionBase) {
        this.conditionBase = conditionBase;
    }
    public String getConditionTitle() {
        return conditionTitle;
    }
    public void setConditionTitle(String conditionTitle) {
        this.conditionTitle = conditionTitle;
    }
    public Boolean getIsCanViewByMainFlow() {
        return isCanViewByMainFlow;
    }
    public void setIsCanViewByMainFlow(Boolean isCanViewByMainFlow) {
        this.isCanViewByMainFlow = isCanViewByMainFlow;
    }
    public Boolean getIsCanViewMainFlow() {
        return isCanViewMainFlow;
    }
    public void setIsCanViewMainFlow(Boolean isCanViewMainFlow) {
        this.isCanViewMainFlow = isCanViewMainFlow;
    }
    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public Boolean getIsDelete() {
        return isDelete;
    }
    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }
}
