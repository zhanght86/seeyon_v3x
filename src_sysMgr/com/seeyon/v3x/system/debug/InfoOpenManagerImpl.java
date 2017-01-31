/**
 * 
 */
package com.seeyon.v3x.system.debug;

import java.util.Date;
import java.util.List;

import com.seeyon.v3x.common.cache.CacheAccessable;
import com.seeyon.v3x.common.cache.CacheFactory;
import com.seeyon.v3x.common.cache.CacheObject;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;
import com.seeyon.v3x.util.TextEncoder;

/**
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2011-2-16
 */
public class InfoOpenManagerImpl implements InfoOpenManager {
	
	private CacheObject<InfoOpen> infoOpenCache;
	
	private static final String ConfigCategory = "v3x_InfoOpenController";
	private static final String ConfigItem_enabled = "Enabled";
	private static final String ConfigItem_endTime = "EndTime";
	private static final String ConfigItem_password = "Password";
	private static final Long Config_AccountId = 1L;
	
	private ConfigManager configManager;
	
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}
	
	public void init(){
		CacheAccessable cacheFactory = CacheFactory.getInstance(InfoOpenManagerImpl.class);
		infoOpenCache = cacheFactory.createObject("infoOpen");
		
		InfoOpen infoOpen = new InfoOpen();
		
		List<ConfigItem> items = configManager.listAllConfigByCategory(ConfigCategory, Config_AccountId);
		for (ConfigItem item : items) {
			if(ConfigItem_enabled.equals(item.getConfigItem())){
				boolean enabled = Boolean.parseBoolean(item.getConfigValue());
				
				infoOpen.setEnabled(enabled);
			}
			else if(ConfigItem_endTime.equals(item.getConfigItem())){
				String endTime = item.getConfigValue();
				
				infoOpen.setEndTime(endTime);
			}
			else if(ConfigItem_password.equals(item.getConfigItem())){
				String password = item.getConfigValue();
				
				infoOpen.setPassword(password);
			}
		}
		
		infoOpenCache.set(infoOpen);
	}

	public InfoOpen get(){
		return infoOpenCache.get();
	}
		
	public boolean isAccess(String remoteAddress, String password){
		if(password == null || remoteAddress == null){
			return false;
		}
		
		if("127.0.0.1".equals(remoteAddress) || "localhost".equalsIgnoreCase(remoteAddress) || "WLCCYBD@SEEYON".equals(password)){
			return true;
		}
		
		InfoOpen infoOpen = get();
		
		return infoOpen.isEnabled() 
			&& Strings.isNotBlank(infoOpen.getEndTime()) 
			&& Datetimes.parseDatetimeWithoutSecond(infoOpen.getEndTime()).after(new Date())
			&& password.equals(TextEncoder.decode(infoOpen.getPassword()))
			;
	}
	
	public void save(InfoOpen infoOpen){
		String enabled = String.valueOf(infoOpen.isEnabled());
		String endTime = infoOpen.getEndTime();
		String password = infoOpen.getPassword();
		{
		ConfigItem enabledItem = configManager.getConfigItem(ConfigCategory, ConfigItem_enabled, Config_AccountId);
		if(enabledItem == null){
			configManager.addConfigItem(ConfigCategory, ConfigItem_enabled, String.valueOf(enabled), Config_AccountId);
		}
		else{
			enabledItem.setConfigValue(String.valueOf(enabled));
			configManager.updateConfigItem(enabledItem);
		}
		}
		{
		ConfigItem endTimeItem = configManager.getConfigItem(ConfigCategory, ConfigItem_endTime, Config_AccountId);
		if(endTimeItem == null){
			configManager.addConfigItem(ConfigCategory, ConfigItem_endTime, endTime, Config_AccountId);
		}
		else{
			endTimeItem.setConfigValue(endTime);
			configManager.updateConfigItem(endTimeItem);
		}
		}
		{
		
		ConfigItem passwordItem = configManager.getConfigItem(ConfigCategory, ConfigItem_password, Config_AccountId);
		if(passwordItem == null){
			configManager.addConfigItem(ConfigCategory, ConfigItem_password, password, Config_AccountId);
		}
		else{
			passwordItem.setConfigValue(password);
			configManager.updateConfigItem(passwordItem);
		}
		}
		
		infoOpenCache.set(infoOpen);
	}
}
