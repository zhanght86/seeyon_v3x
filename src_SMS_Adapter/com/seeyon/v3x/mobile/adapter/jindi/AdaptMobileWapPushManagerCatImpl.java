package com.seeyon.v3x.mobile.adapter.jindi;

import java.util.Collection;

import com.seeyon.v3x.mobile.adapter.AdapterMobileWapPushManager;
import com.seeyon.v3x.mobile.utils.MobileConstants;
import com.seeyon.v3x.util.Strings;
import com.tidyinfo.utils.MessageSender;

public class AdaptMobileWapPushManagerCatImpl implements
		AdapterMobileWapPushManager {
	
	private MessageSender messageSender;

	public void setMessageSender(MessageSender messageSender) {
		this.messageSender = messageSender;
	}
	public void setSERVER_IP_WAPPUSH(String server_ip_wappush) {
		MobileConstants.SERVER_IP_WAPPUSH = server_ip_wappush;
	}

	public void setWAPPUSH_PORT(int wappush_port) {
		MobileConstants.WAPPUSH_PORT = wappush_port;
	}
	
	public boolean isAvailability(){
		return Strings.isNotBlank(MobileConstants.SERVER_IP_WAPPUSH) 
		&& MobileConstants.WAPPUSH_PORT != 0 
		;
	}

	public String getName() {
		return "金笛Wappush猫";
	}

	public boolean isSupportQueueSend() {
		return false;
	}

	public boolean sendMessage(int messageId, String srcPhone,
			String destPhone, String content, String wappushURL) {
		boolean result = false;
		if(wappushURL!=null){
			result = messageSender.sendMessage(messageId,srcPhone,destPhone,content,wappushURL);
		}
		
		return result;
	}

	public boolean sendMessage(int messageId, String srcPhone,
			Collection<String> destPhone, String content, String wappushURL) {
		return false;
	}
	
	public boolean isSupportSplit(){
    	return true;
    }
}
