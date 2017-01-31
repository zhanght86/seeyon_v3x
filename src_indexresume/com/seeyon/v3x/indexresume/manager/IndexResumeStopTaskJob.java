package com.seeyon.v3x.indexresume.manager;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.index.IndexOptimize;

public class IndexResumeStopTaskJob implements Job {
	private static IndexResumeManager resume = (IndexResumeManager)ApplicationContextHolder.getBean("indexResumeManager");
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		if(resume!=null)
		{
			resume.setStopFlag(true);
			IndexOptimize.indexOptimize();
		}
	}

}
