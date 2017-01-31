package com.seeyon.v3x.mobile.message.dialect;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.i18n.LocaleContext;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.mobile.MobileException;
import com.seeyon.v3x.mobile.manager.OAManagerInterface;
import com.seeyon.v3x.mobile.util.Constants;
import com.seeyon.v3x.util.Strings;

public class MeetingDialectImpl implements MobileAppDialect {
	
	private static final Log log = LogFactory.getLog(MeetingDialectImpl.class);
	
	private static final String key = "com.seeyon.v3x.mobile.mettingbasets";
	
	private OAManagerInterface oaManagerInterface;
	
	public void setOaManagerInterface(OAManagerInterface oaManagerInterface) {
		this.oaManagerInterface = oaManagerInterface;
	}

	public String getAppDialect(Locale locale, String id){
		String resouce = Constants.DEFAULT_MOBILE_RESOURCE;
		
		locale = LocaleContext.merge(locale);
		
		ResourceBundle rb = ResourceBundleUtil.getResourceBundle(resouce, locale);
		return ResourceBundleUtil.getString(rb, key, id);
	}
		
	public boolean parseRecieve(String content,Long objectId,Long senderId,Long srcId){
		if(Strings.isNotBlank(content)){
			try {
				if(content.charAt(0) == '+'){
					content = content.substring(1);
					
					if(Strings.isBlank(content)){
						return false;
					}
				}
				
				char flag = content.charAt(0);
				int process = 0;
				if(flag == 'y' || flag == 'Y'){
					process = 1;
				}
				else if(flag == 'n' || flag == 'N'){
					process = 0;
				}
				else{
					return false;
				}
				
				content = content.substring(1);
			if(content!=null&&content.length()!=0&&content.charAt(0) == '+'){
					content = content.substring(1);
				}
				
				oaManagerInterface.processMeeting(objectId, srcId, process, content,false);
			} 
			catch (MobileException e) {
				log.error("手机回复的会议内容有错！", e);
			}
		}
		
		return false;
	}
	
}
