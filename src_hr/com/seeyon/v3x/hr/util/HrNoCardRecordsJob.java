package com.seeyon.v3x.hr.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.hr.manager.RecordManager;

/**
 * <pre>
 * 定时任务，每天凌晨3：00执行，在系统开启考勤管理的前提下
 * 为未进行签到、签退操作的员工，插入一条未打卡的考勤记录
 * 便于HR管理员对各种考勤状态进行查询时，可以查询到"未打卡"状态的有效记录
 * </pre>
 * @author <a href="mailto:yangm@seeyon.com">多情的苦行僧</a> 2011-7-22
 */
public class HrNoCardRecordsJob implements Job {

	private static final Log log = LogFactory.getLog(HrNoCardRecordsJob.class);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			RecordManager recordManager = (RecordManager)ApplicationContextHolder.getBean("recordManager");
			if(recordManager == null) {
				log.warn("无法从Spring IOC容器中获取RecordManager，未打卡记录可能会因此无法插入!");
				return;
			}
			
			recordManager.addRecords4NoCard();
		}
		catch(Exception e) {
			log.error("为未进行签到、签退操作的员工，插入未打卡考勤记录过程中出现异常：", e);
		}
	}

}