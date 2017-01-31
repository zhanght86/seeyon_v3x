package com.seeyon.v3x.mobile.adapter;

import java.util.Collection;

/**
 * 短信收发通用接口
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-10-23
 */
public interface AdapterMobileWapPushManager {
	
	/**
	 * 检测Wappush适配器是否可用
	 * 
	 * @return
	 */
	public boolean isAvailability();

	/**
	 * adapter的名称
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 发送Wappush消息
	 * 
	 * @param messageId
	 *            消息的唯一标示
	 * @param srcPhone
	 *            短信的发送方手机号码
	 * @param destPhone
	 *            短信的接收方手机号码
	 * @param content
	 *            短信内容
	 * @param wappushURL
	 *            wap push的url，如果为<code>null</code>，说明不发送wappush，采用普通短信
	 * @return true 发送成功
	 */
	public boolean sendMessage(int messageId, String srcPhone, String destPhone, String content,
			String wappushURL);

	/**
	 * 发送Wappush消息
	 * 
	 * @param messageId
	 *            消息的唯一标示
	 * @param srcPhone
	 *            短信的发送方手机号码
	 * @param destPhone
	 *            短信的接收方手机号码
	 * @param content
	 *            短信内容
	 * @param wappushURL
	 *            wap push的url，如果为<code>null</code>，说明不发送wappush，采用普通短信
	 * @return true 发送成功
	 */
	public boolean sendMessage(int messageId, String srcPhone, Collection<String> destPhone,
			String content, String wappushURL);

	/**
	 * 是否支持群发
	 * 
	 * @return true 支持
	 */
	public boolean isSupportQueueSend();
	
	//public boolean isSupportSplit();

	
}
