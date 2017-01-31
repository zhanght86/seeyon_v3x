package com.seeyon.v3x.worktimeset.manager;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.worktimeset.exception.WorkTimeSetExecption;

public class WorkTimeCurrencySaveHelp implements Job {
	static Log log = LogFactory.getLog(WorkTimeCurrencySaveHelp.class);
	public void execute(JobExecutionContext arg) throws JobExecutionException {
		WorkTimeSetManager workTimeSetManager = (WorkTimeSetManager) ApplicationContextHolder
				.getBean("workTimeSetManager");
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		try {
			workTimeSetManager.insertWorkTimeCurrencySetByYear(year);
		} catch (WorkTimeSetExecption e) {
			log.error("年底12月31日执行工作时间设置保存出错", e);
		}
	}
}
