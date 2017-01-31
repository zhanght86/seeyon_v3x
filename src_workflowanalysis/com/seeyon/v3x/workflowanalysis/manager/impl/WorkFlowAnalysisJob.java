package com.seeyon.v3x.workflowanalysis.manager.impl;
import java.util.Map;

import org.jfree.util.Log;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.common.quartz.QuartzJob;
import com.seeyon.v3x.workflowanalysis.manager.WorkFlowAnalysisManager;

public class WorkFlowAnalysisJob implements QuartzJob {

	private WorkFlowAnalysisManager workFlowAnalysisManager;
	
	public void setWorkFlowAnalysisManager(
			WorkFlowAnalysisManager workFlowAnalysisManager) {
		this.workFlowAnalysisManager = workFlowAnalysisManager;
	}
	@Override
	public void execute(Map<String, String> parameters) {
		Log.info("开始执行流程和效率分析定时调度任务.");
		workFlowAnalysisManager.doWorkFlowAnalysis();
	}

}
