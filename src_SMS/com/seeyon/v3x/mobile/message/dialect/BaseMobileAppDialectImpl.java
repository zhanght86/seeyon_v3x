package com.seeyon.v3x.mobile.message.dialect;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.mobile.util.Constants;
import com.seeyon.v3x.util.Strings;

public class BaseMobileAppDialectImpl implements MobileAppDialect {
	
	private static final Log log = LogFactory.getLog(BaseMobileAppDialectImpl.class);
	
	private static final String key="SMS.postfix";
	
	private UserMessageManager userMessageManager;
	
	public String getAppDialect(Locale locale, String featureCode){
		String resouce = Constants.DEFAULT_MOBILE_RESOURCE;
		
		locale = LocaleContext.merge(locale);
		
		ResourceBundle rb = ResourceBundleUtil.getResourceBundle(resouce, locale);
		
		return ResourceBundleUtil.getString(rb, key, featureCode);
	}
	
    /**
     * 解析接收到的回复信息
     */
	public boolean parseRecieve(String content, Long objectId, Long senderId, Long srcId) {
		if(senderId==null || Strings.isBlank(content)){
			return false;
		}
		if(content.charAt(0)=='+'){
		    content = content.substring(1);
		}
		try {
		    userMessageManager.sendPersonMessage(content, senderId, srcId);
		} catch (Exception e) {
		    log.error("baseDialect处理接收到的回复信息转发在线消息报错！", e);
		}
        return true;
	}

	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}

}
