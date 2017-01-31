package com.seeyon.v3x.meeting.domain;

import com.seeyon.v3x.organization.domain.V3xOrgEntity;

public class MtReplyWithAgentInfo {
	private MtReply mtReply;

	private Long replyUserId;
	
	private String replyUserName;

	private Long agentId;
	
	private String agentName;
	
	private java.lang.Integer feedbackFlag;
	
	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public MtReply getMtReply() {
		return mtReply;
	}

	public void setMtReply(MtReply mtReply) {
		this.mtReply = mtReply;
	}

	public Long getReplyUserId() {
		return replyUserId;
	}

	public void setReplyUserId(Long replyUserId) {
		this.replyUserId = replyUserId;
	}

	public String getReplyUserName() {
		return replyUserName;
	}

	public void setReplyUserName(String replyUserName) {
		this.replyUserName = replyUserName;
	}

	public java.lang.Integer getFeedbackFlag() {
		return feedbackFlag;
	}

	public void setFeedbackFlag(java.lang.Integer feedbackFlag) {
		this.feedbackFlag = feedbackFlag;
	}
	
	public String getEntityType() {
		return V3xOrgEntity.ORGENT_TYPE_MEMBER;
	}
	
}
