package com.seeyon.v3x.hr.util.listener;

import java.text.ParseException;

import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import com.seeyon.v3x.common.SystemInitialitionInterface;
import com.seeyon.v3x.common.quartz.QuartzListener;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.config.IConfigPublicKey;
import com.seeyon.v3x.config.SystemConfig;
import com.seeyon.v3x.hr.util.Constants;
import com.seeyon.v3x.hr.util.HrNoCardRecordsJob;

/**
 * <pre>
 * 用于在系统启动时开启HR考勤部分定时任务调度：
 * 每天凌晨3：00执行，在系统开启考勤管理的前提下
 * 为未进行签到、签退操作的员工，插入一条未打卡的考勤记录
 * 便于HR管理员对各种考勤状态进行查询时，可以查询到"未打卡"状态的有效记录
 * </pre>
 * @author <a href="mailto:yangm@seeyon.com">多情的苦行僧</a> 2011-7-22
 */
public class HrNoCardRecordsInitListener implements SystemInitialitionInterface {
	private static Log logger = LogFactory.getLog(HrNoCardRecordsInitListener.class);
	
	private static final String TRIGGER_HRCARD = "trigger_hrcard";
	private static final String TRIGGER_GROUP_HRCARD = "trigger_group_hrcard";
	
	private static final String JOB_HRCARD = "job_hrcard";
	private static final String JOB_GROUP_HRCARD = "job_group_hrcard";
	
	public void destroyed(ServletContextEvent arg0) {
		
	}

	public void initialized(ServletContextEvent arg0) {
		try {
			SystemConfig systemConfig = (SystemConfig)ApplicationContextHolder.getBean("systemConfig");
			String ci = systemConfig.get(IConfigPublicKey.CARD_ENABLE);
			boolean cardEnabled = ci != null && Constants.CARD_ENABLED.equals(ci);
			if(!cardEnabled)
				return;
			
			Scheduler sched = QuartzListener.getScheduler();
			
			String[] triggerGroups = sched.getTriggerGroupNames();
            Boolean isRepeat = false;
            for(int i = 0; i < triggerGroups.length; i++) {
            	if(TRIGGER_GROUP_HRCARD.equals(triggerGroups[i])) {
            		String[] triggers = sched.getTriggerNames(triggerGroups[i]);
                	for (int j = 0; j < triggers.length; j++) {
                    	Trigger tg = sched.getTrigger(triggers[j], triggerGroups[i]);
                    	if (tg instanceof CronTrigger && tg.getFullName().equals(TRIGGER_GROUP_HRCARD + "." + TRIGGER_HRCARD)) {
                    		sched.rescheduleJob(triggers[j], triggerGroups[i], tg);
                    		isRepeat = true;
                    		continue;
                    	}
                    }
            	}
            }
            
            if(!isRepeat) {
				CronTrigger trigger = new CronTrigger(TRIGGER_HRCARD, TRIGGER_GROUP_HRCARD);
				// 设定在每周二-周六凌晨3点分运行(暂不考虑节假日等因素)，记录前一日未打卡的人员
				//每天都可以打卡
				CronExpression cexp = new CronExpression("0 0 3 ? * SUN-SAT");
				trigger.setCronExpression(cexp);
				
				JobDetail job = new JobDetail(JOB_HRCARD, JOB_GROUP_HRCARD, HrNoCardRecordsJob.class);
				job.setJobDataMap(new JobDataMap());
				sched.scheduleJob(job, trigger);
            }
			
			if(logger.isDebugEnabled()) {
				logger.debug("为未进行签到、签退操作的员工，插入未打卡考勤记录的定时任务已经启动...");
			}
		}
		catch (SchedulerException e) {
			logger.error("设置任务调度时出现异常：", e);
		} 
		catch (ParseException e) {
			logger.error("解析定时任务调度时间格式时出现异常：", e);
		}
		catch(Exception e) {
			logger.error("为未进行签到、签退操作的员工，插入未打卡考勤记录过程中出现异常：", e);
		}
	}

}