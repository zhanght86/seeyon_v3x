/**
 * 
 */
package com.seeyon.v3x.mobile.message.domain;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.mobile.message.manager.MobileMessageManagerImpl;

/**
 * 应用发送方式规则
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-11-20
 */
public class AppMessageRule {
	
	/**
	 * 应用短信的首选方式定义
	 */
	public static enum AppMessagePreferred{
		SMS, //短信
		WAPPUSH //wap push
	}
	
	/**
	 * 应用
	 * 支持插件ID，将类型ApplicationCategoryEnum改为Integer.
	 */
	private int app;
	
	/**
	 * 首选方式
	 */
	private AppMessagePreferred preferred;
	
	/**
	 * 接收者在线时是否发送，默认不发送
	 */
	private boolean isSendOfOnline;
	
	/**
	 * 
	 * @param app
	 * @param preferred
	 * @param isSendOfOnline
	 */
	public AppMessageRule(int app, AppMessagePreferred preferred, boolean isSendOfOnline) {
		super();
		
		if(preferred == null){
			throw new java.lang.IllegalArgumentException("preferred不能为空");
		}
		this.app = app;
		
		//if(this.app == null){
		//	throw new java.lang.IllegalArgumentException("未知的App: " + app);
		//}
		
		this.preferred = preferred;
		this.isSendOfOnline = isSendOfOnline;
	}

	public int getApp() {
		return app;
	}

	public void setApp(int app) {
		this.app = app;
	}

	public boolean isSendOfOnline() {
		return isSendOfOnline;
	}

	public void setSendOfOnline(boolean isSendOfOnline) {
		this.isSendOfOnline = isSendOfOnline;
	}

	public AppMessagePreferred getPreferred() {
		return preferred;
	}

	public void setPreferred(AppMessagePreferred preferred) {
		this.preferred = preferred;
	}
	
	/**
	 * 判断应用是否采用wappush方式发送短信
	 * 
	 * @return true 是
	 */
	public boolean isSendWappush(){
		return AppMessagePreferred.WAPPUSH.equals(this.preferred);
	}
	
	/**
	 * 从配置管理组件中构造
	 * 
	 * @param configValue
	 */
	public AppMessageRule(String configItem, String configValue){
		String appkey = configItem.substring(MobileMessageManagerImpl.CONFIG_ITEM_MOBILE_AppMessageRules.length());
		this.app = Integer.parseInt(appkey);
		
		String[] values = configValue.split(",");
		
		this.preferred = AppMessagePreferred.valueOf(values[0]);
		this.isSendOfOnline = Boolean.parseBoolean(values[1]);
	}
	
	public String getConfigItem(){
		return MobileMessageManagerImpl.CONFIG_ITEM_MOBILE_AppMessageRules + this.app;
	}
	
	public String getConfigValue(){
		return this.preferred.name() + "," + String.valueOf(isSendOfOnline);
	}
}
