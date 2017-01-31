package com.seeyon.v3x.indexresume.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.common.web.util.ApplicationContextHolder;

public class IndexResumeTaskJob implements Job {
	private final static Log logger = LogFactory.getLog(IndexResumeTaskJob.class);
	private static IndexResumeManager resume = (IndexResumeManager)ApplicationContextHolder.getBean("indexResumeManager");
	private static IndexResumeTaskManager taskResume = (IndexResumeTaskManager)ApplicationContextHolder.getBean("indexResumeTaskManager");
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			if(resume!=null)
			{
				resume.setStopFlag(false);
				resume.resumeStar(taskResume.getResumeInfo());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
}
