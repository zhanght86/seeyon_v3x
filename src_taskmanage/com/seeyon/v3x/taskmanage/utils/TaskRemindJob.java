package com.seeyon.v3x.taskmanage.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.taskmanage.manager.TaskInfoManager;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.TaskStatus;
import com.seeyon.v3x.taskmanage.utils.TaskUtils.RemindType;

/**
 * 任务提醒
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-2-1
 */
public class TaskRemindJob implements Job {

	private static final Log logger = LogFactory.getLog(TaskRemindJob.class);
	
	@Override
	public void execute(JobExecutionContext datamap) throws JobExecutionException {
		JobDetail jobDetail = datamap.getJobDetail();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		Long taskId = jobDataMap.getLongFromString(TaskUtils.DATA_ID_PREFIX);
		Integer remindTypeKey = jobDataMap.getIntFromString(TaskUtils.REMIND_TYPE);
		RemindType remindType = RemindType.valueOf(remindTypeKey);
		
		try {
			TaskInfoManager taskInfoManager = (TaskInfoManager)ApplicationContextHolder.getBean("taskInfoManager");
			UserMessageManager userMessageManager = (UserMessageManager) ApplicationContextHolder.getBean("UserMessageManager");
			if(taskInfoManager == null || userMessageManager == null)
				return;
			
			TaskInfo task = taskInfoManager.get(taskId);
			if(task != null && task.getStatus() != TaskStatus.Canceled.key() && task.getStatus() != TaskStatus.Finished.key()) {
				switch(remindType) {
				case BeforeStart :
					if(task.remindBeforeStart()) {
						TaskMsgUtils.sendMsg4RemindBeforeStart(task, userMessageManager);
					}
					break;
				case BeforeEnd :
					if(task.remindBeforeEnd()) {
						TaskMsgUtils.sendMsg4RemindBeforeEnd(task, userMessageManager);
					}
					break;
				}
			}
		}
		catch(Exception e) {
			logger.error("为任务[id=" + taskId + "]执行[" + remindType.name() + "]定时任务调度时出现异常:", e);
		}
		
	}

}
