package com.seeyon.v3x.collaboration.webmodel;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.affair.constants.SubStateEnum;
import com.seeyon.v3x.affair.domain.Affair;
import com.seeyon.v3x.collaboration.domain.ColSummary;

/**
 * User: lius Date: 2006-9-29 Time: 14:52:47
 */
public class ColSummaryModel {

	public static enum COLTYPE {
		WaitSend, Sent, Pending, Done
	}

	/**
	 * 协同类型：待办、待发、已办、已�?
	 */
	private String colType;

	private String workitemId;

	private String caseId;
	
	private String processId;

	private Long affairId;

	private boolean overtopTime;

	private ColSummary summary;

	private Date startDate;

    private Date receiveTime;

	private Long deadLine = 0L;

	private Long advanceRemindTime = 0L;

	private List<String> forwardMemberNames;
	
	private Date dealTime;

	/*
	 * 正文类型
	 */
	private String bodyType;

	/**
	 * 待办协同状态：未读、待办、暂存待办、已�?待发协同状态：草稿、回退、撤销
	 */
	private int state;

	/**
	 * 是否跟踪该事�?
	 */
	private boolean isTrack;

	/*
	 * 催办次数
	 */
	private int hastenTimes;
	
	//是否代理
	private boolean proxy = false;
	
	//代理人
	private String proxyName;
	
	//是否有附件
	private boolean hasAttsFlag = false;
	
	private String nodePolicy;
	
	private boolean isAgentDeal;  //是否代理人处理  
	
	private Affair affair;
	
	public Affair getAffair() {
		return affair;
	}

	public void setAffair(Affair affair) {
		this.affair = affair;
	}

	public boolean isAgentDeal() {
		return isAgentDeal;
	}

	public void setAgentDeal(boolean isAgentDeal) {
		this.isAgentDeal = isAgentDeal;
	}

	/**
	 * 流程是否结束
	 * 
	 * @return
	 */
	public boolean isFinshed() {
		if (summary == null) {
			return false;
		}

		return summary.isFinshed();
	}
	
	public int getFlowState(){
		Integer state = summary.getState();
		
		return state == null ? 0 : state.intValue();
	}

	public String getBodyType() {
		return bodyType;
	}

	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}

	public String getWorkitemId() {
		return workitemId;
	}

	public void setWorkitemId(String workitemId) {
		this.workitemId = workitemId;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public ColSummary getSummary() {
		return summary;
	}

	public void setSummary(ColSummary summary) {
		this.summary = summary;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Long getAffairId() {
		return affairId;
	}

	public void setAffairId(Long affairId) {
		this.affairId = affairId;
	}

	public int getHastenTimes() {
		return hastenTimes;
	}

	public void setHastenTimes(int hastenTimes) {
		this.hastenTimes = hastenTimes;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean getIsTrack() {
		return isTrack;
	}

	public void setIsTrack(boolean isTrack) {
		this.isTrack = isTrack;
	}

	public String getColType() {
		return colType;
	}

	public void setColType(String colType) {
		this.colType = colType;
	}

	public boolean getOvertopTime() {
		return overtopTime;
	}

	public void setOvertopTime(boolean overtopTime) {
		this.overtopTime = overtopTime;
	}

	public Long getDeadLine() {
		return deadLine;
	}

	public void setDeadLine(Long deadLine) {
		this.deadLine = deadLine;
	}

	public Long getAdvanceRemindTime() {
		return advanceRemindTime;
	}

	public void setAdvanceRemindTime(Long advanceRemindTime) {
		this.advanceRemindTime = advanceRemindTime;
	}

	public boolean isRead() {
		return state != SubStateEnum.col_pending_unRead.key();
	}

	public List<String> getForwardMemberNames() {
		return forwardMemberNames;
	}

	public void setForwardMemberNames(List<String> forwardMemberNames) {
		this.forwardMemberNames = forwardMemberNames;
	}

	public boolean isProxy() {
		return proxy;
	}

	public void setProxy(boolean proxy) {
		this.proxy = proxy;
	}

	public String getProxyName() {
		return proxyName;
	}

	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}

	public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Date getDealTime() {
		return dealTime;
	}

	public void setDealTime(Date dealTime) {
		this.dealTime = dealTime;
	}

	public boolean isHasAttsFlag() {
		return hasAttsFlag;
	}

	public void setHasAttsFlag(boolean hasAttsFlag) {
		this.hasAttsFlag = hasAttsFlag;
	}
	
	public String getNodePolicy() {
		return nodePolicy;
	}

	public void setNodePolicy(String nodePolicy) {
		this.nodePolicy = nodePolicy;
	}
}