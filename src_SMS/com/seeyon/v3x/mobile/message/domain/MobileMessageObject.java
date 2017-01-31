package com.seeyon.v3x.mobile.message.domain;

import java.util.Locale;


/**
 * 
 * 
 */
public class MobileMessageObject {
	
	private Long messageId;//Mobile_Message 表中的Id
	
	private Long id;// 对象id（如：协同id、公告id等）

	private int type;// 对象类型（参照ApplicationCategoryEnum）

	private MessageReciver reciever;

	private Long sid;// 发送人的ID

	private String content;
	
	private java.sql.Timestamp affaircreatetime;
	
	private Locale locale;
	
	private String featureCode;
	
	/**
	 * 消息类型：系统: 0, 个人: 1;
	 * 
	 * @see com.seeyon.v3x.common.usermessage.Constants.UserMessage_TYPE
	 */
	private int messageType;// 系统：0， 个人：1 
	

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public MessageReciver getReciever() {
		return reciever;
	}

	public void setReciever(MessageReciver reciever) {
		this.reciever = reciever;
	}
	
	public Long getSid() {
		return sid;
	}

	public void setSid(Long sid) {
		this.sid = sid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public java.sql.Timestamp getAffaircreatetime() {
		return affaircreatetime;
	}

	public void setAffaircreatetime(java.sql.Timestamp affaircreatetime) {
		this.affaircreatetime = affaircreatetime;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getFeatureCode() {
		return featureCode;
	}

	public void setFeatureCode(String featureCode) {
		this.featureCode = featureCode;
	}
	
}
