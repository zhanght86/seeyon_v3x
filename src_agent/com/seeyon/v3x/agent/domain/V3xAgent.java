package com.seeyon.v3x.agent.domain;
import java.io.Serializable;

import java.util.List;

import com.seeyon.v3x.common.constants.Constants;
import com.seeyon.v3x.common.utils.UUIDLong;

/**
 * The persistent class for the v3x_agent database table.
 * 
 * @author BEA Workshop Studio
 */
public class V3xAgent  implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 108026031096089368L;
	//代理人Id
	private Long agentId;
	//代理选项
	private String agentOption;
	//被代理人Id
	private Long agentToId;
	//取消时间
	private java.sql.Timestamp cancelDate;
	//是否取消标记
	private Boolean cancelFlag;
	//代理创建时间
	private java.sql.Timestamp createDate;
	//代理结束时间
	private java.sql.Timestamp endDate;
	//代理开始时间
	private java.sql.Timestamp startDate;
	//代理选项名称，内存中使用，不持久化化到数据库
	private String agentOptionName;
	//代理明细，自己持久化
	private List<V3xAgentDetail> agentDetails;
	//是否需要提醒（代理人）
	private Boolean agentRemind;
	//是否需要提醒（被代理人）
	private Boolean agentToRemind;
   
	public Boolean getAgentRemind() {
		return agentRemind;
	}

	public void setAgentRemind(Boolean agentRemind) {
		this.agentRemind = agentRemind;
	}

	public Boolean getAgentToRemind() {
		return agentToRemind;
	}

	public void setAgentToRemind(Boolean agentToRemind) {
		this.agentToRemind = agentToRemind;
	}

	public List<V3xAgentDetail> getAgentDetails() {
		return agentDetails;
	}

	public void setAgentDetails(List<V3xAgentDetail> agentDetails) {
		this.agentDetails = agentDetails;
	}

	public V3xAgent() {
    }

    private Long id = null;//Constants.GLOBAL_NULL_ID;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isNew(){
        if(id == null || id == Constants.GLOBAL_NULL_ID) return true;
        return false;
    }

    public void setIdIfNew(){
        if(isNew()){
            id = UUIDLong.longUUID();
        }
    }

	public Long getAgentId() {
		return this.agentId;
	}
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getAgentOption() {
		return this.agentOption;
	}
	public void setAgentOption(String agentOption) {
		this.agentOption = agentOption;
	}

	public Long getAgentToId() {
		return this.agentToId;
	}
	public void setAgentToId(Long agentToId) {
		this.agentToId = agentToId;
	}

	public java.sql.Timestamp getCancelDate() {
		return this.cancelDate;
	}
	public void setCancelDate(java.sql.Timestamp cancelDate) {
		this.cancelDate = cancelDate;
	}

	public Boolean getCancelFlag() {
		return this.cancelFlag;
	}
	public void setCancelFlag(Boolean cancelFlag) {
		this.cancelFlag = cancelFlag;
	}

	public java.sql.Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(java.sql.Timestamp createDate) {
		this.createDate = createDate;
	}

	public java.sql.Timestamp getEndDate() {
		return this.endDate;
	}
	public void setEndDate(java.sql.Timestamp endDate) {
		this.endDate = endDate;
	}

	public java.sql.Timestamp getStartDate() {
		return this.startDate;
	}
	public void setStartDate(java.sql.Timestamp startDate) {
		this.startDate = startDate;
	}

	public String getAgentOptionName() {
		return agentOptionName;
	}

	public void setAgentOptionName(String agentOptionName) {
		this.agentOptionName = agentOptionName;
	}
	
}