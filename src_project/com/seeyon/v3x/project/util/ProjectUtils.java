/**
 * 
 */
package com.seeyon.v3x.project.util;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.quartz.QuartzListener;
import com.seeyon.v3x.common.usermessage.Constants;
import com.seeyon.v3x.common.usermessage.MessageContent;
import com.seeyon.v3x.common.usermessage.MessageReceiver;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.project.domain.ProjectPhase;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.project.webmodel.ProjectCompose;
import com.seeyon.v3x.util.Datetimes;

/**
 * @author tianlin
 * 
 * @date 2007-5-18
 */
public class ProjectUtils {
	
	private static final Log logger = LogFactory.getLog(ProjectUtils.class);
	
	private static ProjectUtils projectUtils = new ProjectUtils();

	private static int sort = 0;

	public final static String PROJECT_RESOUCE = "com.seeyon.v3x.project.resources.i18n.ProjectResources";

	private ProjectUtils() {
	}

	public static ProjectUtils getInstance() {
		return projectUtils;
	}

	public synchronized int getTheSort() {
		sort++;
		return sort;
	}
	
	/**
	 * 计算项目进度
	 */
	public static void addProjectProcess(ProjectCompose projectCompose, ModelAndView mav) {
		float proc = projectCompose.getProjectSummary().getProjectProcess();
		if(proc >= 0){
			Float f = new Float(proc);
			int index = f.toString().indexOf(".");
			String projectProcess = f.toString().substring(0, index);
			mav.addObject("projectProcess", projectProcess);
		}
	}
	
	/*-----------------------------------项目阶段提醒任务调度Begin------------------------------------*/
	
	public static final String JOB_PHASE_PREFIX = "job_task_";
	public static final String GROUP_PHASE_PREFIX = "group_task_";
	public static final String DATA_ID_PREFIX = "phase_id";
	public static final String REMIND_TYPE = "remind_type";

	/**
	 * 提醒类型：项目阶段开始前提醒、项目阶段结束前提醒
	 */
	public static enum PhaseRemindType {
		PhaseBeforeStart, // 项目阶段开始前提醒
		PhaseBeforeEnd;// 项目阶段结束前提醒

		public static PhaseRemindType valueOf(int key) {
			PhaseRemindType[] types = PhaseRemindType.values();
			for (PhaseRemindType type : types) {
				if (type.ordinal() == key) {
					return type;
				}
			}
			throw new IllegalArgumentException("不合法[key=" + key + "]的提前提醒类型");
		}
	}
	
	/**
	 * 保存项目时，处理项目阶段对应的提前提醒任务调度
	 */
	public static void remind4Create(ProjectPhase phase) {
		if(phase.getBeforeAlarmDate() != ProjectConstants.PHASE_NO_REMIND) {
			remind(phase, PhaseRemindType.PhaseBeforeStart);
		}
		
		if(phase.getEndAlarmDate() != ProjectConstants.PHASE_NO_REMIND) {
			remind(phase, PhaseRemindType.PhaseBeforeEnd);
		}
	}
	
	/**
	 * 修改项目时，处理项目阶段对应的提前提醒任务调度
	 */
	public static void remind4Update(ProjectPhase phase) {
		cancelRemind(phase.getId());
		remind4Create(phase);
	}
	
	/**
	 * 项目阶段任务调度处理
	 */
	private static void remind(ProjectPhase phase, PhaseRemindType phaseRemindType) {
		try {
			Scheduler sched = QuartzListener.getScheduler();
			Long phaseId = phase.getId();
			String jobName = JOB_PHASE_PREFIX + phaseRemindType.ordinal() + phaseId;
			String groupName = GROUP_PHASE_PREFIX + phaseRemindType.ordinal() + phaseId;
			String triggerName = String.valueOf(UUIDLong.longUUID());
			Date runTime = phase.getRemindTime(phaseRemindType);
			
			SimpleTrigger trigger = new SimpleTrigger(triggerName, groupName, runTime);
			
			JobDataMap datamap = new JobDataMap();
			datamap.putAsString(DATA_ID_PREFIX, phaseId.longValue());
			datamap.putAsString(REMIND_TYPE, phaseRemindType.ordinal());

			JobDetail job = new JobDetail(jobName, groupName, PhaseRemindJob.class);
			job.setJobDataMap(datamap);
			sched.scheduleJob(job, trigger);
			
			if(logger.isDebugEnabled()) {
				logger.debug("为项目阶段[id=" + phase.getId() + ", name=" + phase.getPhaseName() + "]启动任务调度[类型：" + phaseRemindType.name() + "]" +
							 "启动时间为：" + Datetimes.format(runTime, Datetimes.datetimeWithoutSecondStyle));
			}
		} catch (SchedulerException e) {
			logger.error("为项目阶段[id=" + phase.getId() + ", name=" + phase.getPhaseName() + "]设置提醒任务调度过程中出现异常：", e);
		}
	}
	
	/**
	 * 取消项目阶段提前提醒任务调度
	 */
	public static void cancelRemind(Long phaseId) {
		try {
			Scheduler sched = QuartzListener.getScheduler();
			sched.deleteJob(JOB_PHASE_PREFIX + PhaseRemindType.PhaseBeforeStart.ordinal() + phaseId, GROUP_PHASE_PREFIX + PhaseRemindType.PhaseBeforeStart.ordinal() + phaseId);
			sched.deleteJob(JOB_PHASE_PREFIX + PhaseRemindType.PhaseBeforeEnd.ordinal() + phaseId, GROUP_PHASE_PREFIX + PhaseRemindType.PhaseBeforeEnd.ordinal() + phaseId);
			
			if(logger.isDebugEnabled()) {
				logger.debug("为项目阶段[id=" + phaseId + "]取消提醒任务调度于" + new Date(System.currentTimeMillis()));
			}
		} 
		catch (SchedulerException e) {
			logger.error("删除项目阶段[id=" + phaseId + "]提醒任务调度过程中出现异常：", e);
		}
	}
	
	/*-----------------------------------项目阶段提醒任务调度End------------------------------------*/
	
	/**
	 * 项目阶段提醒发送消息，消息接受对象为项目负责人、项目助理
	 */
	public static void sendMsg(PhaseRemindType phaseRemindType, ProjectPhase phase, UserMessageManager userMessageManager) throws MessageException {
		Long phaseId = phase.getId();
		ProjectManager projectManager = (ProjectManager) ApplicationContextHolder.getBean("projectManager");
		
		Long projectId = null;
		String projectName = "";
		Long userId = null;
		
		List<Object[]> phases = projectManager.getProjectByPhase(phaseId);
		if(phases != null && phases.size() > 0){
			Object[] object = phases.get(0);
			projectId = ((BigInteger)object[0]).longValue();
			projectName = object[1].toString();
			userId = ((BigInteger)object[2]).longValue();
		}
		
		//只提醒项目负责人和项目助理
		Set<Long> msgReceiverIds = new HashSet<Long>();
		
		List<Object> members = projectManager.getProjectMembersByProject(projectId);
		if(members != null && members.size() > 0){
			for(Object object : members){
				msgReceiverIds.add(((BigInteger)object).longValue());
			}
		}
		
		MessageContent messageContent = null;
		
		String msgKey = "";
		String date = "";
		if(phaseRemindType == PhaseRemindType.PhaseBeforeStart){
			msgKey = "project.phase.RemindBeforeStart";
			date = Datetimes.formatDate(phase.getPhaseBegintime());
		}else if(phaseRemindType == PhaseRemindType.PhaseBeforeEnd){
			msgKey = "project.phase.RemindBeforeEnd";
			date = Datetimes.formatDate(phase.getPhaseClosetime());
		}
		
		messageContent = MessageContent.get(msgKey, projectName, phase.getPhaseName(), date);
		
		Collection<MessageReceiver> messageReceiver = MessageReceiver.get(phaseId, msgReceiverIds, "message.link.project.info", Constants.LinkOpenType.href, projectId);
			
		userMessageManager.sendSystemMessage(messageContent, ApplicationCategoryEnum.project, userId, messageReceiver, phaseId);
	}
	
}
