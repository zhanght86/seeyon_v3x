package com.seeyon.v3x.collaboration.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.seeyon.v3x.common.domain.BaseModel;

public class ColSuperviseLog extends BaseModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long superviseId;
	private Integer type;
	private Long sender;
	private Set<ColSuperviseReceiver> receivers;
	private String reveiverIds;
	private Date sendTime;
	private Integer mode;
	private String content;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getMode() {
		return mode;
	}
	public void setMode(Integer mode) {
		this.mode = mode;
	}
	public Set<ColSuperviseReceiver> getReceivers() {
		return receivers;
	}
	public void setReceivers(Set<ColSuperviseReceiver> receivers) {
		this.receivers = receivers;
	}
	public Long getSender() {
		return sender;
	}
	public void setSender(Long sender) {
		this.sender = sender;
	}
	public Long getSuperviseId() {
		return superviseId;
	}
	public void setSuperviseId(Long superviseId) {
		this.superviseId = superviseId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public String getReveiverIds() {
		return reveiverIds;
	}
	public void setReveiverIds(String reveiverIds) {
		this.reveiverIds = reveiverIds;
	}
	
	
}
