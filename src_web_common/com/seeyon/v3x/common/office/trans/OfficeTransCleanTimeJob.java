package com.seeyon.v3x.common.office.trans;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.common.office.trans.manager.OfficeTransManager;
import com.seeyon.v3x.common.quartz.QuartzJob;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
/**
 * Office转换文件定时清理任务。
 * @author wangwenyou
 *
 */
public class OfficeTransCleanTimeJob implements QuartzJob, Job {
	private static final Log log = LogFactory
			.getLog(OfficeTransCleanTimeJob.class);

	@Override
	public void execute(Map<String, String> parameters) {
		execute();
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		execute();
	}

	public void execute() {
		long startTime = System.currentTimeMillis();
		log.info("自动清理Office转换文件开始");
		OfficeTransManager officeTransManager = (OfficeTransManager) ApplicationContextHolder
				.getBean("officeTransManager");
		officeTransManager.clean();
		log.info("自动清理Office转换文件结束. 耗时："
				+ (System.currentTimeMillis() - startTime) + " MS");
	}

}
