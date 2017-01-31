package com.seeyon.v3x.messageManager.domain;

import java.io.Serializable;
import com.seeyon.v3x.common.domain.BaseModel;

/**
 * The persistent class for the notepage database table.
 * 
 * @author BEA Workshop Studio
 */
public class MessageDelset extends BaseModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/**
	 * 消息数量
	 */
	private long messageCount;
	/**
	 * 消息天数
	 */	
	private long messageDay;
	/**
	 * 状态　０两者都不生效１消息天数生效２消息数量生效３两者都生效
	 */
	private int status;
	
	public long getMessageCount() {
		return messageCount;
	}
	public void setMessageCount(long messageCount) {
		this.messageCount = messageCount;
	}
	public long getMessageDay() {
		return messageDay;
	}
	public void setMessageDay(long messageDay) {
		this.messageDay = messageDay;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}