package com.seeyon.v3x.calendar.util;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.calendar.manager.CalEventManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 配合任务调度，生成周期性事件
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-6-8
 */
public class PeriodicalEventJob implements Job {
	private static final Log logger = LogFactory.getLog(PeriodicalEventJob.class);
	private static final String PERIODICAL_CONFIG = "periodical_config";
	private static final String PERIODICAL_CONFIG_ITEM = "periodical_config_item";
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		long start = System.currentTimeMillis();
		logger.info("执行扫描周期性事件");
		try {
			ConfigManager configManager = (ConfigManager)ApplicationContextHolder.getBean("configManager");
			CalEventManager calEventManager =(CalEventManager)ApplicationContextHolder.getBean("calEventManager");
			if(configManager==null || calEventManager==null){
				logger.warn("无法获取Manager，忽略。");
				return;
			}
			ConfigItem configItem = configManager.getConfigItem(PERIODICAL_CONFIG, PERIODICAL_CONFIG_ITEM);
			String todayStr = Datetimes.formatDate(Datetimes.getTodayFirstTime());
			if(!configItem.getConfigValue().equals(todayStr)){
				configItem.setConfigValue(todayStr);
				configManager.updateConfigItem(configItem);
				calEventManager.handlePeriodicalEvents();
			} else {
				logger.info("周期性事件已经扫描！");
			}
		}
		catch(Exception e) {
			logger.error("执行扫描周期性事件并生成重复事件任务过程中出现异常：", e);
		}
		logger.info("执行扫描周期性事件结束,耗时:"+(System.currentTimeMillis()-start)+"MS");
	}
	//周期性事件扫描日期--同一天只扫描一次
	private boolean doAuth(){
		ConfigManager configManager = (ConfigManager)ApplicationContextHolder.getBean("configManager");
		ConfigItem item = configManager.getConfigItem(PERIODICAL_CONFIG, PERIODICAL_CONFIG_ITEM);
		String todayStr = Datetimes.formatDate(Datetimes.getTodayFirstTime());
		if(item == null){
			//创建
			item = new ConfigItem();
			item.setIdIfNew();
			item.setConfigCategory(PERIODICAL_CONFIG);
			item.setConfigItem(PERIODICAL_CONFIG_ITEM);
			item.setExtConfigValue(todayStr);
			Date date=new Date();
			Timestamp stamp=new Timestamp(date.getTime());
			item.setCreateDate(stamp);
			item.setOrgAccountId(1L);
			configManager.addConfigItem(item);
		}else{
			if(todayStr.equals(item.getExtConfigValue())){
				return false;
			}
			item.setExtConfigValue(todayStr);
			configManager.updateConfigItem(item);
		}
		return true;
	}
	

}
