package com.seeyon.v3x.indexresume.manager;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import com.seeyon.v3x.common.quartz.QuartzListener;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.domain.ConfigItem;
import com.seeyon.v3x.config.manager.ConfigManager;
import com.seeyon.v3x.indexresume.domain.IndexResumeInfo;
import com.seeyon.v3x.indexresume.util.IndexResumeConstants;

public class IndexResumeTaskManagerImpl implements IndexResumeTaskManager {
	private final static Log logger = LogFactory.getLog(IndexResumeTaskManagerImpl.class);
	
	private ConfigManager configManager;
	private static final String SPLIT_TIME="|";
	private static final String _SPLIT_TIME="[|]";
	private static final String SPLIT_APP_TYPE=",";
	private static final String _SPLIT_APP_TYPE="[,]";
	public void init() {
		
		try {
			registryTask(getResumeInfo());
		} catch (Exception e) {
			logger.error("",e);
		}
		
	}

	public void registryTask(IndexResumeInfo info) {
		if(!info.isEnableState())
		{
			logger.info("全文检索恢复任务未启用!");
			return;
		}
		if(info.getAppType()==null)
		{
			logger.info("没有要恢复的模块!");
			return;
		}
		starquartz(info.getStarHour(),info.getStarMin(),IndexResumeTaskJob.class);
		starquartz(info.getEndHour(),info.getEndMin(),IndexResumeStopTaskJob.class);
		logger.info("注册全文检索恢复任务完成: "+info.getStarHour()+":"+info.getStarMin());
	}

	private void starquartz(String arg0,String arg1,Class<?> arg3) {
		Scheduler sched;
		try {
			sched = QuartzListener.getScheduler();
			if( sched == null)
				return;
			 Trigger trigger = null;
			 trigger = org.quartz.TriggerUtils.makeDailyTrigger(arg3.getSimpleName(),Integer.parseInt(arg0), Integer.parseInt(arg1));
			
			 Calendar calendar=Calendar.getInstance();   
             calendar.setTime(new Date());
             calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND)+5);
             trigger.setStartTime(calendar.getTime());
             
     		JobDetail job = new JobDetail(arg3.getSimpleName(),
					null, arg3);

			Trigger tg = sched.getTrigger(arg3.getSimpleName(), null);
			if( tg != null)
				sched.deleteJob(arg3.getSimpleName(),null);
			
			sched.scheduleJob(job, trigger);
			
			
		} catch (Exception e) {
			logger.error("注册全文检索恢复任务失败",e);
		}
	}
	public IndexResumeInfo getResumeInfo()
	{
		IndexResumeInfo info=new IndexResumeInfo();
		
//		info.setShowPageOnly(showPage);
		ConfigItem config=configManager.getConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_STATE);
		if(config!=null)
		{
			info.setEnableState(Boolean.parseBoolean(config.getConfigValue()));
		}
	    config=configManager.getConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_DATE_SCOPE);
		if(config!=null)
		{
				String[] split=config.getConfigValue().split(_SPLIT_TIME);
				if(split!=null&&split.length==2)
				{
					info.setResumeStarDate(split[0]);
					info.setResumeEndDate(split[1]);
				}
		}
		config=configManager.getConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_APP_TYPE);
		if(config!=null)
		{
			//处理各应用恢复Bean
			info.setAppType(splitAppType(config.getConfigValue(),info));
		}
		 config=configManager.getConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_START_TIME);
			if(config!=null)
			{
				String[] startTime= config.getConfigValue().split("[:]");
				if(startTime!=null&&startTime.length==2)
				{
					info.setStarHour(startTime[0]);
					info.setStarMin(startTime[1]);
				}
			}
			
		 config=configManager.getConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_END_TIME);
			if(config!=null)
			{
				String[] endTime= config.getConfigValue().split("[:]");
				if(endTime!=null&&endTime.length==2)
				{
					info.setEndHour(endTime[0]);
					info.setEndMin(endTime[1]);
				}
			}
	
		return info;
	}
	public void saveConfig(IndexResumeInfo resumeInfo) {
		ConfigItem config=configManager.getConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_STATE);
		if(config!=null)
		{
			updateConfigItem(resumeInfo, config);
		}else{
			addConfigItem(resumeInfo);
		}
		this.registryTask(resumeInfo);
		isStartTaskNow(resumeInfo);
	}

	private void updateConfigItem(IndexResumeInfo resumeInfo, ConfigItem config) {
		config.setConfigValue(resumeInfo.isEnableState()+"");
		configManager.updateConfigItem(config);
		if(!resumeInfo.isEnableState())
		{
			logger.info("全文检索恢复任务未启用!");
			return;
		}
		config=configManager.getConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_START_TIME);
		config.setConfigValue(resumeInfo.getStarHour() + ":" + resumeInfo.getStarMin());
		configManager.updateConfigItem(config);
		config=configManager.getConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_END_TIME);
		config.setConfigValue(resumeInfo.getEndHour() + ":" + resumeInfo.getEndMin());
		configManager.updateConfigItem(config);
		config=configManager.getConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_APP_TYPE);
		config.setConfigValue(compoundAppType(resumeInfo));
		configManager.updateConfigItem(config);
		config=configManager.getConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_DATE_SCOPE);
		config.setConfigValue(getDateScope(resumeInfo.getResumeStarDate(),resumeInfo.getResumeEndDate()));
		configManager.updateConfigItem(config);
	}

	private void addConfigItem(IndexResumeInfo resumeInfo) {
		configManager.addConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_START_TIME, getTime(resumeInfo.getStarHour(),resumeInfo.getStarMin()));
		configManager.addConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_END_TIME, getTime(resumeInfo.getEndHour(),resumeInfo.getEndMin()));
		configManager.addConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_APP_TYPE, compoundAppType(resumeInfo));
		configManager.addConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_STATE, resumeInfo.isEnableState()+"");
		configManager.addConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_DATE_SCOPE, getDateScope(resumeInfo.getResumeStarDate(),resumeInfo.getResumeEndDate()));
	}
	private String getTime(String hour,String min)
	{
		if(StringUtils.isBlank(hour)||StringUtils.isBlank(min))
		{
			return "";
		}
		return hour + ":" + min;
	}
	private String getDateScope(String starDate,String endDate)
	{
		if(StringUtils.isBlank(starDate)||StringUtils.isBlank(endDate))
		{
			return "";
		}
		return starDate + SPLIT_TIME + endDate;
	}
	private String compoundAppType(IndexResumeInfo resumeInfo)
	{
		StringBuilder sb=new StringBuilder(); 
		String[] split=resumeInfo.getAppType();
		if(split!=null)
		{
			for (int i = 0; i < split.length; i++) {
				if(StringUtils.isBlank(resumeInfo.getResumeEndDate())){continue;}
				sb.append(split[i]+SPLIT_TIME+resumeInfo.getResumeEndDate()+SPLIT_APP_TYPE);
			}
		}
		return sb.toString();
	}
	private String[] splitAppType(String str,IndexResumeInfo info)
	{
		String[] sb = splitApp(str); 
		if(sb==null){return null;}
		String[] splited=new String[sb.length];
		for (int i = 0; i < sb.length; i++) {
			if(StringUtils.isBlank(sb[i])){continue;}
			String[] arrays=sb[i].split(_SPLIT_TIME);
			if(StringUtils.isBlank(arrays[0])){continue;}
			splited[i]=arrays[0];
//			if(!info.isShowPageOnly())
//			{
//				if(!arrays[1].equalsIgnoreCase(IndexResumeConstants.RESUME_OVER_FLAG))
//				{
			if(StringUtils.isBlank(info.getResumeStarDate()))
			{
				continue;
			}
					info.add(arrays[0], info.getResumeStarDate(), arrays[1]);
//				}
//			}
		}
		return splited;
	}

	private String[] splitApp(String str) {
		if(StringUtils.isBlank(str)){return null;}
		String[] sb=str.split(_SPLIT_APP_TYPE);
		return sb;
	}
	public void setConfigManager(ConfigManager configManager) {
		this.configManager = configManager;
	}

	public void taskEndWork(String createDate,int appType) {
		if(createDate==null){return;}
//		String updateDate=Datetimes.formatDate(createDate);
		ConfigItem config=configManager.getConfigItem(IndexResumeConstants.INDEX_RESUME_CONFIGURATION, IndexResumeConstants.RESUME_APP_TYPE);
		if(config!=null)
		{
			StringBuilder sb=new StringBuilder();
			String[] appTypeAndDate=splitApp(config.getConfigValue());
			for (int i = 0; i < appTypeAndDate.length; i++) {
				String[] temp=appTypeAndDate[i].split(_SPLIT_TIME);
				if(temp!=null)
				{
					if(temp[0].equalsIgnoreCase(appType+""))
					{
						sb.append(temp[0]+SPLIT_TIME+createDate+SPLIT_APP_TYPE);
					}
					else{
						sb.append(appTypeAndDate[i]+SPLIT_APP_TYPE);
					}
				}
			}
			config.setConfigValue(sb.toString());
			configManager.updateConfigItem(config);
		}
	}
	
	private void isStartTaskNow(IndexResumeInfo info)
	{
		if(!info.isEnableState())
		{
			logger.info("全文检索恢复任务未启用!");
			return;
		}
		if(info.getAppType()==null)
		{
			logger.info("没有要恢复的模块!");
			return;
		}
		 Calendar calendarB=Calendar.getInstance();
		 calendarB.set(Calendar.HOUR_OF_DAY, Integer.parseInt(info.getStarHour()));
		 calendarB.set(Calendar.MINUTE, Integer.parseInt(info.getStarMin()));
		 calendarB.set(Calendar.SECOND, 0);
		 
		 Calendar calendarA=Calendar.getInstance();
		 calendarA.set(Calendar.HOUR_OF_DAY, Integer.parseInt(info.getEndHour()));
		 calendarA.set(Calendar.MINUTE, Integer.parseInt(info.getEndMin()));
		 calendarA.set(Calendar.SECOND, 59);
		 
		 Calendar calendar=Calendar.getInstance();   
         calendar.setTime(new Date());
         
		if(calendarB.before(calendar)&&calendarA.after(calendar))
		{
			logger.info("立即运行时刻: "+calendar.getTime().toLocaleString());
			new thread1().start();
		}
	}
	class thread1 extends Thread
	{
		@Override
		public void run() {
			IndexResumeTaskManager taskResume = (IndexResumeTaskManager)ApplicationContextHolder.getBean("indexResumeTaskManager");
			try {
				IndexResumeManager resume=(IndexResumeManager)ApplicationContextHolder.getBean("indexResumeManager");
				resume.resumeStar(taskResume.getResumeInfo());
			} catch (Exception e) {
				logger.error("",e);
			}
		}
		
	}
}
