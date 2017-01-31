package com.seeyon.v3x.cluster.listener;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.cluster.notification.NotificationType;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.main.AccountSymbol;
import com.seeyon.v3x.main.MainDataLoader;
import com.seeyon.v3x.system.Constants;
import com.seeyon.v3x.util.annotation.HandleNotification;

/**
 * 单位标识 - 通知监听处理类
 * @author Mazc
 *
 */
public class AccountSymbolNotificationHandler {
	protected static final Log log = LogFactory.getLog(AccountSymbolNotificationHandler.class);
	private ConfigManager configManager;
	@HandleNotification(type = NotificationType.AccountSymbol_Update)
	public void updateAccountSymbol(Object o){
		if(o instanceof AccountSymbol){
			try{
				final AccountSymbol symbol = (AccountSymbol)o;
				final Long accountId = symbol.getAccountId();
//				MainDataLoader.getInstance().updateAccountSymbol(accountId, symbol);
	            List<ConfigItem> configItemList = getConfigManager().listAllConfigByCategory(Constants.CONFIG_CATRGORY_ACCOUNT_SYMBOL, accountId);
	            for(ConfigItem configItem : configItemList){
	                String configItemStr = configItem.getConfigItem();
	                if(Constants.CONFIG_ITEM_LOGO.equals(configItemStr)){
	    				MainDataLoader.getInstance().refreshLocalImage(configItem);
	                }
	                else if(Constants.CONFIG_ITEM_BANNER.equals(configItemStr)){
	    				MainDataLoader.getInstance().refreshLocalImage(configItem);
	                }
		        }			
			
			}
			catch(Exception e){
				log.error("集群-更新单位标识异常", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-更新单位标识]");
		}
	}
	
	@HandleNotification(type = NotificationType.AccountSymbol_Delete)
	public void deleteAccountSymbol(Object o){
		if(o instanceof Long){
			try{
				MainDataLoader.getInstance().deleteAccountSymbol((Long)o);
			}
			catch(Exception e){
				log.error("集群-删除单位标识异常", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-删除单位标识]");
		}
	}
	
	@HandleNotification(type = NotificationType.AccountSymbol_UpdateLoginImg)
	public void updateLoginBgPath(Object o){
		if(o instanceof String){
			try{
				MainDataLoader.getInstance().setLoginImagePath((String)o);
				ConfigItem configItem_login = getConfigManager().getConfigItem(Constants.CONFIG_CATRGORY_LOGIN_IMAGE, Constants.CONFIG_ITEM_LOGIN_IMAGE);
				MainDataLoader.getInstance().refreshLocalImage(configItem_login);
				log.info("更新背景图片。");
			}
			catch(Exception e){
				log.error("集群-更新登录背景图片异常", e);
			}
		}
		else{
			log.error("参数传递类型错误[集群-更新登录背景图片]");
		}
	}

	private ConfigManager getConfigManager() {
		if(configManager==null){
			configManager = (ConfigManager) ApplicationContextHolder.getBean("configManager");
		}
		return configManager;
	}
}
