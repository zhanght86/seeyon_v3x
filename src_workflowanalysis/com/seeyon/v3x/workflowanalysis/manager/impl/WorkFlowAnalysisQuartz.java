package com.seeyon.v3x.workflowanalysis.manager.impl;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.quartz.QuartzHolder;
import com.seeyon.v3x.util.Datetimes;

public class WorkFlowAnalysisQuartz {
	/**定时调度任务*/
	private static final Log log = LogFactory.getLog(WorkFlowAnalysisQuartz.class);
	private static String jobName = "workFlowAnalysisJobName";
	public void init(){
		startJob();
	}
	private void startJob() {
		try {
				QuartzHolder.deleteQuartzJob(jobName);
				
				String s = Datetimes.formatDate(new Date());
				Date startDate =  Datetimes.parse(s+" 03:00:00");
				QuartzHolder.newQuartzJobPerDay(null,jobName , startDate, "workFlowAnalysisJob", null);
		} catch (Exception e) {
			log.error("启动流程和效率分析定时调度任务失败.", e);
		}
		log.info("启动流程和效率分析定时调度任务成功.");
	}

}
