package com.seeyon.v3x.workflowanalysis.domain;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

public class WorkFlowAnalysisAcl extends BaseModel implements Serializable{
	private static final long serialVersionUID = 4056223909454283756L;
	private String memberIds ;
	private String memberNames;
	private String templeteIds;
	private String templeteNames;
	private Long   orgAccountId;
	private Date   createDate;
	private Date   updateDate;
	
	public String getMemberIds() {
		return memberIds;
	}
	public void setMemberIds(String memberIds) {
		this.memberIds = memberIds;
	}
	public String getTempleteIds() {
		return templeteIds;
	}
	public void setTempleteIds(String templeteIds) {
		this.templeteIds = templeteIds;
	}
	public String getTempleteNames() {
		return templeteNames;
	}
	public void setTempleteNames(String templeteNames) {
		this.templeteNames = templeteNames;
	}
	public Long getOrgAccountId() {
		return orgAccountId;
	}
	public void setOrgAccountId(Long orgAccountId) {
		this.orgAccountId = orgAccountId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getMemberNames() {
		return memberNames;
	}
	public void setMemberNames(String memberNames) {
		this.memberNames = memberNames;
	}
}
