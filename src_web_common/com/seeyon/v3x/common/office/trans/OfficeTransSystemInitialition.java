package com.seeyon.v3x.common.office.trans;

import java.util.Date;

import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.common.SystemInitialitionInterface;
import com.seeyon.v3x.common.quartz.QuartzHolder;
import com.seeyon.v3x.util.Datetimes;
/**
 * Office转换启动初始化。
 * @author wangwenyou
 *
 */
public class OfficeTransSystemInitialition implements
		SystemInitialitionInterface {
	private final static Log log = LogFactory
			.getLog(OfficeTransSystemInitialition.class);

	public void destroyed(ServletContextEvent arg0) {

	}

	public void initialized(ServletContextEvent arg0) {
		initQuartzJob();
	}

	/**
	 * 设置定时清理任务。
	 */
	public void initQuartzJob() {
		try {
			String name = OfficeTransCleanTimeJob.class.getSimpleName();
			// 如果存在相同的任务
			if (QuartzHolder.hasQuartzJob(name)) {
				QuartzHolder.deleteQuartzJob(name);
			}
			// 每天2：00执行
			Date beginDate = Datetimes
					.addHour(Datetimes.getTodayFirstTime(), 2);

			QuartzHolder.newQuartzJob(name, beginDate, 24 * 60 * 60 * 1000,
					"officeTransCleanTimeJob", null);

			log.info("注册Office转换清理调度任务" + name + ",成功");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
