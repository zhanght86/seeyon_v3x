package com.seeyon.v3x.mobile.adapter;

import java.util.Collection;
import java.util.List;

import com.seeyon.v3x.mobile.message.domain.MobileReciver;

public interface AdapterMobileMessageManger {
	
	/**
	 * 检测短信适配器是否可用
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
	 * 发送短消息
	 * 
	 * @param messageId
	 *            消息的唯一标示
	 * @param srcPhone
	 *            短信的发送方手机号码
	 * @param destPhone
	 *            短信的接收方手机号码
	 * @param content
	 *            短信内容
	 * @return true 发送成功
	 */
	public boolean sendMessage(Long messageId, String srcPhone, String destPhone, String content);
	
	
	/**
	 * 发送短消息
	 * 
	 * @param messageId
	 *            消息的唯一标示
	 * @param srcPhone
	 *            短信的发送方手机号码
	 * @param destPhone
	 *            短信的接收方手机号码
	 * @param content
	 *            短信内容
	 * @return true 发送成功
	 */
	public boolean sendMessage(Long messageId, String srcPhone, Collection<String> destPhone,
			String content);
	
	
	/**
	 * 是否支持群发
	 * 
	 * @return true 支持
	 */
	public boolean isSupportQueueSend();
	
	/**
	 * 是否支持
	 * 
	 * @return
	 */
	public boolean isSupportRecive();
	
	/**
	 * 从手机端返回到A8系统
	 * 
	 * @return
	 */
	public List<MobileReciver> recive();
	
	//public boolean isSupportSplit();

}
