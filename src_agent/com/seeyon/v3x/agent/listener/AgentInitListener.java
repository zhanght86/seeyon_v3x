package com.seeyon.v3x.agent.listener;

import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import com.seeyon.v3x.agent.manager.AgentIntercalateHelper;
import com.seeyon.v3x.common.SystemInitialitionInterface;
import com.seeyon.v3x.common.quartz.QuartzListener;

public class AgentInitListener  implements SystemInitialitionInterface {
	static Log log = LogFactory.getLog(AgentInitListener.class);

	private static String triggerName = "agentTriggerName";
	private static String triggerGroupName = "agentTriggerGroupName";
	private static String jobName = "agentJobName";
	private static String jobGroupName = "agentJobGroupName";
	public void destroyed(ServletContextEvent arg0) {

	}

	public void initialized(ServletContextEvent arg0) {
		try {
			//生成新的任务之前，确认一下数据表中是否已经存在该记录
            Scheduler sched = QuartzListener.getScheduler();
            String[] triggerGroups = sched.getTriggerGroupNames();
            Boolean isRepeat = false;
            for(int i=0; i<triggerGroups.length; i++){
            	if(triggerGroupName.equals(triggerGroups[i])){
            		String[] triggers = sched.getTriggerNames(triggerGroups[i]);
                	for (int j = 0; j < triggers.length; j++) {
                    	Trigger tg = sched.getTrigger(triggers[j], triggerGroups[i]);
                    	if (tg instanceof CronTrigger && tg.getFullName().equals(triggerGroupName + "." + triggerName)) {
                    		sched.rescheduleJob(triggers[j], triggerGroups[i], tg);
                    		isRepeat = true;
                    		continue;
                    	}
                    }
            	}
            }
            if(!isRepeat){
            	CronTrigger cronTrigger = new CronTrigger(triggerName, triggerGroupName);
                try {
                    CronExpression cexp = new CronExpression("0 0 0 ? * SUN-SAT");
                    cronTrigger.setCronExpression(cexp);
                } catch (Exception e) {
                   log.error("", e);
                }
                JobDetail job = new JobDetail(jobName, jobGroupName, AgentIntercalateHelper.class);
                job.setJobDataMap(new JobDataMap());
                sched.scheduleJob(job, cronTrigger);
            }
		} catch (Exception e) {
			log.error("", e);
		}
	}

}
