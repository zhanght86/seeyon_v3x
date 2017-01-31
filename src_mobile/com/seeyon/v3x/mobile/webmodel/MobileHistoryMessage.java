package com.seeyon.v3x.mobile.webmodel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.filemanager.Attachment;

public class MobileHistoryMessage implements Comparable<MobileHistoryMessage>{

	private Long senderId;//发送者
	private Long receiverId;//接受者
	private Date sendTime;//发起时间
	private String content;//信息内容
	private Integer type;//类型
	private Long id;
	List<Attachment> atts = null;
	public List<Attachment> getAtts() {
		return atts;
	}

	public void setAtts(List<Attachment> atts) {
		this.atts = atts;
	}

	public void addAttachments(List<Attachment> atts){
		if(this.atts == null){
			this.atts = new ArrayList<Attachment>();
		}
		this.atts.addAll(atts);
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public int compareTo(MobileHistoryMessage o) {
		return -this.sendTime.compareTo(o.sendTime);
	}

}
