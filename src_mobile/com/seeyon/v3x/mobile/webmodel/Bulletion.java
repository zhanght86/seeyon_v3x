package com.seeyon.v3x.mobile.webmodel;

import java.util.List;

import com.seeyon.v3x.bulletin.domain.BulBody;
import com.seeyon.v3x.common.filemanager.Attachment;

public class Bulletion {
	private String title;

	private String content;

	private String type;

	private Long senderId;

	private java.util.Date sendTime;

	private String contentType;
	
	private String sign;
	
	private boolean deleteFlag;
	
	private int bulState;
	
	private BulBody bulBody;
	
	private List<Attachment> attachmentList;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public java.util.Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(java.util.Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Attachment> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<Attachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public boolean isDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public int getBulState() {
		return bulState;
	}

	public void setBulState(int bulState) {
		this.bulState = bulState;
	}

	public BulBody getBulBody() {
		return bulBody;
	}

	public void setBulBody(BulBody bulBody) {
		this.bulBody = bulBody;
	}

}
