package com.seeyon.v3x.cluster.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.mobile.message.domain.AppMessageRule;
import com.seeyon.v3x.mobile.message.manager.MobileMessageManager;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.annotation.HandleNotification;


/**
 * 移动应用 集群监听
 * @author dongyj
 *
 */
public class MobileNotificationHandler {
	protected static final Log logger = LogFactory.getLog(MobileNotificationHandler.class);
	
	private MobileMessageManager mobileMessageManager;
	private ConfigManager configManager;
	
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}
	public void setMobileMessageManager(MobileMessageManager mobileMessageManager) {
		this.mobileMessageManager = mobileMessageManager;
	}
	/**
	 * 移动应用:修改是否可以使用移动应用、可以发送短信、可以接收短信
	 * @param o
	 */
	@HandleNotification(type = NotificationType.MobileCanUseModify)
	public void mobileUseRight(Object o){
		try{
			if(o instanceof Object[]){
				Object[] useRight = (Object[]) o ;
				if(useRight.length ==3){
					mobileMessageManager.setCanUseWap(Boolean.valueOf(useRight[0].toString()));
			        mobileMessageManager.setCanUseSMS(Boolean.valueOf(useRight[1].toString()));
			        mobileMessageManager.setCanUseWappush(Boolean.valueOf(useRight[2].toString()));
			        if(logger.isDebugEnabled()){
						logger.debug("Modify Mobile used right:"+useRight[0].toString()+","+useRight[1].toString()+","+useRight[2].toString());
					}
				}
			}
		}catch(Exception e){
			logger.error(e);
		}
	}
	@HandleNotification(type = NotificationType.SuffixModify)
	public void suffixModify(Object o){
		try{
			ConfigItem mobileConfig = configManager.getConfigItem(MobileMessageManager.CONFIG_CATEGORY_MOBILE, MobileMessageManager.CONFIG_ITEM_MOBILE_SUFFIX);
			if(mobileConfig != null){
				mobileMessageManager.setSMSSuffix(mobileConfig.getConfigValue());
				if(logger.isDebugEnabled()){
					logger.debug("修改短信后缀为:"+mobileConfig.getConfigValue());
				}
			}
		}catch(Exception e){
			logger.error(e);
		}
	}
	
	@HandleNotification(type = NotificationType.AccountOfCanUseWapModify)
	public void wapAccoundUserModify(Object o){
		try{
			ConfigItem mobileConfig = configManager.getConfigItem(MobileMessageManager.CONFIG_CATEGORY_MOBILE, MobileMessageManager.CONFIG_ITEM_MOBILE_AccountOfCanUseWap);
			if(mobileConfig != null){
				mobileMessageManager.getAccountOfCanUseWap().clear();
				mobileMessageManager.getAccountOfCanUseWap().addAll(sp(mobileConfig.getExtConfigValue()));
				if(logger.isDebugEnabled()){
					logger.debug("modify the account user who can access a8 by wap:"+mobileConfig.getExtConfigValue());
				}
			}
		}catch(Exception e){
			logger.error(e);
		}
	}
	
	@HandleNotification(type = NotificationType.AccountOfCanUseSMSModify)
	public void smsAccoundUserModify(Object o){
		try{
			ConfigItem mobileConfig = configManager.getConfigItem(MobileMessageManager.CONFIG_CATEGORY_MOBILE, MobileMessageManager.CONFIG_ITEM_MOBILE_AccountOfCanUseSMS);
			if(mobileConfig != null){
				mobileMessageManager.getAccountOfCanUseSMS().clear();
				mobileMessageManager.getAccountOfCanUseSMS().addAll(sp(mobileConfig.getExtConfigValue()));
				if(logger.isDebugEnabled()){
					logger.debug("modify the account user who can use send sms:"+mobileConfig.getExtConfigValue());
				}
			}
		}catch(Exception e){
			logger.error(e);
		}
	}
	
	@HandleNotification(type = NotificationType.AccountOfCanUseWappushModify)
	public void wapPushAccoundUserModify(Object o){
		try{
			ConfigItem mobileConfig = configManager.getConfigItem(MobileMessageManager.CONFIG_CATEGORY_MOBILE, MobileMessageManager.CONFIG_ITEM_MOBILE_AccountOfCanUseWappush);
			if(mobileConfig != null){
				mobileMessageManager.getAccountOfCanUseWappush().clear();
				mobileMessageManager.getAccountOfCanUseWappush().addAll(sp(mobileConfig.getExtConfigValue()));
				if(logger.isDebugEnabled()){
					logger.debug("modify the account user who can use wap push:"+mobileConfig.getExtConfigValue());
				}
			}
		}catch(Exception e){
			logger.error(e);
		}
	}
	
	@HandleNotification(type = NotificationType.SMSCanSendOrReceiveMemberReload)
	public void reloadCanSendOrReceiveMember(Object o){
		if(o instanceof Long){
			try{
				ConfigItem mobileConfig = configManager.getConfigItem(MobileMessageManager.CONFIG_CATEGORY_MOBILE, MobileMessageManager.CONFIG_ITEM_MOBILE_CanSendAuth+o.toString());
				if(mobileConfig != null){
					mobileMessageManager.setCanSendMemberInMemory(mobileConfig.getExtConfigValue(),Long.parseLong(o.toString()));
					if(logger.isDebugEnabled()){
						logger.debug("relaod member who can send sms:"+mobileConfig.getExtConfigValue());
					}
				}
				mobileConfig = configManager.getConfigItem(MobileMessageManager.CONFIG_CATEGORY_MOBILE, MobileMessageManager.CONFIG_ITEM_MOBILE_CanRecieveAuth+o.toString());
				if(mobileConfig != null){
					mobileMessageManager.setCanReceiveMemberInMemory(mobileConfig.getExtConfigValue(),Long.parseLong(o.toString()));
					if(logger.isDebugEnabled()){
						logger.debug("reload member who can receive sms:"+mobileConfig.getExtConfigValue());
					}
				}
			}catch(Exception e){
				logger.error(e);
			}
		}
	}
	
	@HandleNotification(type = NotificationType.AppMessageRulesReload)
	public void appMessageRulsReload(Object o){
		try {
			Map<Integer, AppMessageRule> appMessageRules = mobileMessageManager.getAppMessageRules();
			List<ConfigItem> items = this.configManager.listAllConfigByCategory(MobileMessageManager.CONFIG_CATEGORY_MOBILE);
			if(mobileMessageManager.isValidateMobileMessage()){
				appMessageRules.clear();
				for(ConfigItem item : items){
					if(item.getConfigItem().startsWith("AppMessageRules")){
						AppMessageRule rule = new AppMessageRule(item.getConfigItem(), item.getConfigValue());
						//首选短信，但又没有短信插件
						if(rule.getPreferred().equals(AppMessageRule.AppMessagePreferred.SMS) && !mobileMessageManager.isValidateSMS()){
							continue;
						}
						//首选wappush，但又没有wappush插件
						if(rule.getPreferred().equals(AppMessageRule.AppMessagePreferred.WAPPUSH) && !mobileMessageManager.isValidateWappush()){
							continue;
						}
						appMessageRules.put(rule.getApp(), rule);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	private static List<Long> sp(String itemvalue){
		List<Long> result = new ArrayList<Long>();
		if(Strings.isNotBlank(itemvalue)){
			String[] itemvalues = itemvalue.split(",");
			for (String string : itemvalues) {
				result.add(new Long(string.trim()));
			}
		}
		return result;
	}
}
