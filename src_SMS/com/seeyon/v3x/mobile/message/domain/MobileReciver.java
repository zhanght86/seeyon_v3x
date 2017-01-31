package com.seeyon.v3x.mobile.message.domain;

/**
 * 从手机返回到A8系统
 * 
 * @author hubing
 * 
 */
public class MobileReciver {
	
	/**
	 * 短信发送者（回复者）的手机号
	 */
	private String srcPhone;

	/**
	 * 短信内容
	 */
	private String content;
	
	public MobileReciver(){}
	
	public MobileReciver(String srcPhone, String content) {
		super();
		this.srcPhone = srcPhone;
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSrcPhone() {
		return srcPhone;
	}

	public void setSrcPhone(String src) {
		this.srcPhone = src;
	}
	
}
