package com.seeyon.v3x.calendar.listener;

import java.util.Date;

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
import org.quartz.TriggerUtils;

import com.seeyon.v3x.agent.manager.AgentIntercalateHelper;
import com.seeyon.v3x.calendar.util.PeriodicalEventJob;
import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.SystemInitialitionInterface;
import com.seeyon.v3x.common.quartz.QuartzListener;
import com.seeyon.v3x.util.Datetimes;

public class PeriodicalInitListener implements SystemInitialitionInterface {
	private static Log log = LogFactory.getLog(PeriodicalInitListener.class);

	private static String triggerName = "periodicalTriggerName";
	private static String triggerGroupName = "periodical_groupName";
	private static String jobName = "periodical_jobName";
	private static String jobGroupName = "periodicalJobGroupName";
	
	public void destroyed(ServletContextEvent arg0) {

	}
	
	public void initialized(ServletContextEvent arg0) {
		try {
				Scheduler sched = QuartzListener.getScheduler();
			String[] triggerGroups = sched.getTriggerGroupNames();
			Boolean isRepeat = false;
			for (int i = 0; i < triggerGroups.length; i++) {
				if (triggerGroupName.equals(triggerGroups[i])) {
					String[] triggers = sched.getTriggerNames(triggerGroups[i]);
					for (int j = 0; j < triggers.length; j++) {
						Trigger tg = sched.getTrigger(triggers[j],
								triggerGroups[i]);
						if (tg instanceof CronTrigger
								&& tg.getFullName().equals(
										triggerGroupName + "." + triggerName)) {
							sched.rescheduleJob(triggers[j], triggerGroups[i],
									tg);
							isRepeat = true;
						}
					}
				}
			}
			if (!isRepeat) {
				CronTrigger cronTrigger = new CronTrigger(triggerName,
						triggerGroupName);
				try {
					CronExpression cexp = new CronExpression(
							"0 0 1 ? * SUN-SAT");
					cronTrigger.setCronExpression(cexp);
				} catch (Exception e) {
					log.error("", e);
				}
				JobDetail job = new JobDetail(jobName, jobGroupName,
						PeriodicalEventJob.class);
				job.setJobDataMap(new JobDataMap());
				sched.scheduleJob(job, cronTrigger);
			}
			log.info("周期性日程事件扫描、生成任务调度于"
					+ Datetimes.format(new Date(), Datetimes.datetimeStyle) + "启动。");
		}
		catch (SchedulerException e) {
			log.error("周期性日程事件扫描、生成任务调度启动过程中出现异常：", e);
		}
	}

}
