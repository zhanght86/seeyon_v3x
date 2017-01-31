package com.seeyon.v3x.meeting.util;

import java.util.List;
/**
 * 会议消息发送对象封装类，包含对象集合、消息类型、是否代理等信息
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-4-29
 */
public class MeetingMsgTarget {
	/**
	 * 消息类型，包括：会议发起、会议变更、会议取消、会前提醒等类型
	 */
	public static enum MessageType {
		send("mt.send"),
		edit("mt.edit"),
		cancel("mt.cancel"),
		remind("mt.remind"),
		sendvideo("mt.send.video"),
		editvideo("mt.edit.video"),
		cancelvideo("mt.cancel.video"),
		remindvideo("mt.remind.video");
		
		private String msgKey;
		
		MessageType(String msgKey) {
			this.msgKey = msgKey;
		}
		
		public String getMsgKey() {
			return this.msgKey;
		}
	}
	
	/** 接收消息的人员ID集合 */
	private List<Long> msgReceivers;

	/** 消息接受人员是否代理人员身份 */
	private boolean agent;
	
	/** 代理人模型 */
	private List<MeetingAgent> meetingAgents;
	
	/** 消息类型 */
	private MessageType messageType;
	
	/**
	 * 会议消息发送对象封装类构造方法
	 * @param msgReceivers	接收消息的人员ID集合
	 * @param agent			消息接受人员是否代理人员身份
	 * @param messageType	消息类型
	 * @param meetingAgents	代理模型集合
	 */
	public MeetingMsgTarget(List<Long> msgReceivers, boolean agent, MessageType messageType, List<MeetingAgent> meetingAgents) {
		super();
		this.msgReceivers = msgReceivers;
		this.agent = agent;
		this.messageType = messageType;
		this.meetingAgents = meetingAgents;
	}
	
	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}
	
	public List<Long> getMsgReceivers() {
		return msgReceivers;
	}

	public void setMsgReceivers(List<Long> msgReceivers) {
		this.msgReceivers = msgReceivers;
	}

	public boolean isAgent() {
		return agent;
	}

	public void setAgent(boolean agent) {
		this.agent = agent;
	}
	
	public List<MeetingAgent> getMeetingAgents() {
		return meetingAgents;
	}
	
	public void setMeetingAgents(List<MeetingAgent> meetingAgents) {
		this.meetingAgents = meetingAgents;
	}

}
