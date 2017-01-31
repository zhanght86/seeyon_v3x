package com.seeyon.v3x.mobile.adapter.focuswire;

import java.net.URL;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.client.Client;

import com.seeyon.v3x.mobile.adapter.AdapterMobileWapPushManager;
import com.seeyon.v3x.util.Strings;


public class AdaptMobileWapPushManagerFocusWireImpl implements
		AdapterMobileWapPushManager {
	private static final Log log = LogFactory.getLog(AdaptMobileWapPushManagerFocusWireImpl.class);
	

	private String focusWireWsdl;
	private String focusWireName;
	private String focusWirePassword;
	private String focusWirePrdid;
	public String getFocusWireName() {
		return focusWireName;
	}

	public String getFocusWirePassword() {
		return focusWirePassword;
	}

	public String getFocusWirePrdid() {
		return focusWirePrdid;
	}

	public String getFocusWireWsdl() {
		return focusWireWsdl;
	}

	public void setFocusWireName(String focusWireName) {
		this.focusWireName = focusWireName;
	}

	public void setFocusWirePassword(String focusWirePassword) {
		this.focusWirePassword = focusWirePassword;
	}

	public void setFocusWirePrdid(String focusWirePrdid) {
		this.focusWirePrdid = focusWirePrdid;
	}

	public void setFocusWireWsdl(String focusWireWsdl) {
		this.focusWireWsdl = focusWireWsdl;
	}
	
	public boolean isAvailability(){
		return Strings.isNotBlank(focusWireName) 
		&& Strings.isNotBlank(focusWirePrdid) 
		&& Strings.isNotBlank(focusWirePassword) 
		&& Strings.isNotBlank(focusWireWsdl)
		;
	}

	public String getName() {
		return "分众wappush";
	}

	public boolean isSupportQueueSend() {
		return false;
	}

	public boolean sendMessage(int messageId, String srcPhone,
			String destPhone, String content, String wappushURL) {
		try {
			Client client = new Client(new URL(focusWireWsdl));

			Object[] result = client.invoke("SubmitSingle", new Object[]{focusWireName,focusWirePassword,focusWirePrdid,destPhone,wappushURL,content});	
			String str = (String) result[0];
			String state = str.substring(0, 1);
			return state.equals("0");
		}
		catch (Exception e) {
			log.error("通过分众单个发送WapPush消息，错误！！！",e);
			return false;
		}
	}

	public boolean sendMessage(int messageId, String srcPhone,
			Collection<String> destPhone, String content, String wappushURL) {
		return false;
	}

	public boolean isSupportSplit(){
    	return false;
    }
}
