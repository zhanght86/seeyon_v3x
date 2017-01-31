package com.seeyon.v3x.messageManager.manager;

import com.seeyon.v3x.messageManager.domain.MessageDelset;

public interface MessageDelsetManager {

	/**
	 * 取出消息清除设置
	 * 
	 * @return 消息清除设置对象
	 */
	public MessageDelset getMessageDelset();

	/**
	 * 更新消息清除设置
	 * 
	 * @param messageDelset
	 *            消息清除设置对象
	 */
	public void updateMessageDelset(int count, int day);
}
