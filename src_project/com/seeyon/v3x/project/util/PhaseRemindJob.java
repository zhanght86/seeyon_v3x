package com.seeyon.v3x.project.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.project.domain.ProjectPhase;
import com.seeyon.v3x.project.manager.ProjectPhaseManager;
import com.seeyon.v3x.project.util.ProjectUtils.PhaseRemindType;


/**
 * 项目阶段提前、结束提醒(目前只对项目负责人、项目助理提醒)
 */
public class PhaseRemindJob implements Job {

	private static final Log logger = LogFactory.getLog(PhaseRemindJob.class);

	@Override
	public void execute(JobExecutionContext datamap) throws JobExecutionException {
		JobDetail jobDetail = datamap.getJobDetail();
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		Long phaseId = jobDataMap.getLongFromString(ProjectUtils.DATA_ID_PREFIX);
		Integer remindTypeKey = jobDataMap .getIntFromString(ProjectUtils.REMIND_TYPE);
		PhaseRemindType phaseRemindType = ProjectUtils.PhaseRemindType.valueOf(remindTypeKey);

		try {
			ProjectPhaseManager projectPhaseManager = (ProjectPhaseManager) ApplicationContextHolder.getBean("projectPhaseManager");
			UserMessageManager userMessageManager = (UserMessageManager) ApplicationContextHolder.getBean("UserMessageManager");
			ProjectPhase phase = projectPhaseManager.getById(phaseId);
			if (phase != null) {
				switch (phaseRemindType) {
					case PhaseBeforeStart:
						if (phase.remindBeforeStart()) {
							ProjectUtils.sendMsg(PhaseRemindType.PhaseBeforeStart, phase, userMessageManager);
						}
						break;
					case PhaseBeforeEnd:
						if (phase.remindBeforeEnd()) {
							ProjectUtils.sendMsg(PhaseRemindType.PhaseBeforeEnd, phase, userMessageManager);
						}
						break;
				}
			}
		} catch (Exception e) {
			logger.error("为项目阶段[id=" + phaseId + "]执行[" + phaseRemindType.name() + "]定时任务调度时出现异常：", e);
		}

	}

}
