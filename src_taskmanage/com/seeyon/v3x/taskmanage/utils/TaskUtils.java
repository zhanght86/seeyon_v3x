package com.seeyon.v3x.taskmanage.utils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.common.SystemEnvironment;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.i18n.ResourceBundleUtil;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.quartz.QuartzListener;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.utils.UUIDLong;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigConstants;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.project.domain.ProjectMember;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.taskmanage.domain.TaskRole;
import com.seeyon.v3x.taskmanage.domain.TaskInfo.TaskDateEnum;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.StatisticPeriod;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.TaskAclEnum;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 任务管理工具类
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-25
 */
public abstract class TaskUtils {
	private static final Log logger = LogFactory.getLog(TaskUtils.class);

	/**
	 * 获取任务管理国际化结果值
	 * @param key		国际化key
	 * @param params	参数
	 * @return	国际化结果值
	 */
	public static String getI18n(String key, Object... params) {
		return ResourceBundleUtil.getString(TaskConstants.TASK_I18N_RES, key, params);
	}
	
	/**
	 * 获取Common资源文件中的国际化结果值
	 * @param key		国际化key
	 * @param params	参数
	 * @return	国际化结果值
	 */
	public static String getCommonI18n(String key, Object... params) {
		return ResourceBundleUtil.getString(FormBizConfigConstants.COMMON_RESOURCE, key, params);
	}
	
	/**
	 * 根据指定的角色类型枚举获取对应的角色人员ID集合
	 * @param rask		任务
	 * @param roleType	角色类型枚举
	 * @return	角色ID集合，可能为空
	 */
	public static List<Long> getRoleIds(TaskInfo task, TaskRole.RoleType roleType) {
		List<Long> result = null;
		switch(roleType) {
		case Creator :
			result = Arrays.asList(task.getCreateUser());
			break;
		case Manager :
			result = FormBizConfigUtils.parseStr2Ids(task.getManagers());
			break;
		case Participator :
			result = FormBizConfigUtils.parseStr2Ids(task.getParticipators());
			break;
		}
		return result;
	}
	
	/**
	 * 根据指定的多种角色类型枚举获取对应的所有角色人员ID集合
	 * @param task		任务
	 * @param roleTypes	角色类型数组
	 * @return	多种类型的角色ID集合，可能为空
	 */
	public static Set<Long> getRoleIds(TaskInfo task, TaskRole.RoleType...roleTypes) {
		if(roleTypes != null && roleTypes.length > 0) {
			Set<Long> result = new HashSet<Long>();
			for(TaskRole.RoleType roleType : roleTypes) {
				List<Long> ids = getRoleIds(task, roleType);
				FormBizConfigUtils.addAllIgnoreEmpty(result, ids);
			}
			return result;
		}
		return null;
	}
	
	/**
	 * <pre>
	 * 校验某一用户是否具有对某一任务的某种权限
	 * 任务权限定义如下：
	 * 新建 - 创建人、负责人、参与人、查看人
	 * 分解任务 - 负责人
	 * 修改 - 创建人、负责人、参与人
	 * 查看 - 创建人、负责人、参与人、查看人
	 * 回复 - 创建人、负责人、参与人、查看人
	 * 汇报 - 负责人、参与人
	 * 我的任务列表 - 负责人、参与人
	 * 任务管理列表 - 创建人、查看人
	 * </pre>
	 * @param task		任务
	 * @param userId	用户ID
	 * @param aclType	权限类型
	 * @return	是否具备该种权限
	 */
	public static boolean authCheck(TaskInfo task, Long userId, TaskAclEnum aclType) {
		UserRoles roles = getTaskAcl(task, userId);
		switch(aclType) {
		case Add :
		case View : 
		case Reply : return !roles.isJamesWong();
		case Decompose : return roles.isManager();
		case Edit :	return roles.canEdit();
		case Feedback : return roles.isManager() || roles.isParticipator();		
		default :
			return false;
		}
	}
	
	/**
	 * 获取当前用户在任务中的全部角色
	 * @param task		任务
	 * @param userId	用户ID
	 * @return	用户在任务中的角色权限
	 */
	public static UserRoles getTaskAcl(TaskInfo task, Long userId) {
		UserRoles roles = new UserRoles();
		if(task != null) {
			roles.setCreator(task.getCreateUser().equals(userId));
			roles.setManager(task.getManagers().indexOf(userId.toString()) != -1);
			roles.setParticipator(Strings.isNotBlank(task.getParticipators()) && task.getParticipators().indexOf(userId.toString()) != -1);
		}
		return roles;
	}
	
	/**
	 * 进行任务分解时，将父级任务的若干属性转换为分解子任务，以便页面信息展现
	 * @param parentTask	父级任务
	 * @return 子任务(用于页面展现的信息)
	 */
	public static TaskInfo parse2ChildTask(TaskInfo parentTask) throws CloneNotSupportedException {
		TaskInfo child = (TaskInfo)parentTask.clone();
		
		child.setSubject(null);
		child.setFinishRate(0);
		child.setPlannedTaskTime(0);
		child.setActualTaskTime(0);
		child.setHasAttachments(false);
		child.setParentTaskId(parentTask.getId());
		child.setParentTaskSubject(parentTask.getSubject());
		
		return child;
	}
	
	/*-----------------------------------任务调度Begin------------------------------------*/
	public static final String JOB_TASK_PREFIX = "job_task_";
	public static final String GROUP_TASK_PREFIX = "group_task_";
	public static final String DATA_ID_PREFIX = "task_id";
	public static final String REMIND_TYPE = "remind_type";
	
	/**
	 * 提醒类型：任务开始前提醒提醒、任务结束前提前提醒
	 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-2-28
	 */
	public static enum RemindType {
		/**
		 * 任务开始前提醒提醒
		 */
		BeforeStart,
		/**
		 * 任务结束前提前提醒
		 */
		BeforeEnd;
		
		public static RemindType valueOf(int key) {
			RemindType[] types = RemindType.values();
			for(RemindType type : types) {
				if(type.ordinal() == key) {
					return type;
				}
			}
			throw new IllegalArgumentException("不合法[key=" + key + "]的提前提醒类型");
		}
	}
	
	/**
	 * 新建任务时，处理对应的提前提醒任务调度
	 * @param task	新建的任务
	 */
	public static void remind4Create(TaskInfo task) {
		if(task.getRemindStartTime() != TaskConstants.NO_REMIND) {
			remind(task, RemindType.BeforeStart);
		}
		
		if(task.getRemindEndTime() != TaskConstants.NO_REMIND) {
			remind(task, RemindType.BeforeEnd);
		}
	}
	
	/**
	 * 修改任务时，处理对应的提前提醒任务调度
	 * @param task	修改后的任务
	 */
	public static void remind4Update(TaskInfo task) {
		cancelRemind(task.getId());
		remind4Create(task);
	}
	
	/**
	 * 任务调度处理
	 * @param task			任务信息
	 * @param remindType	提前提醒类型
	 */
	private static void remind(TaskInfo task, RemindType remindType) {
		try {
			Scheduler sched = QuartzListener.getScheduler();
			Long taskId = task.getId();
			String jobName = JOB_TASK_PREFIX + remindType.ordinal() + taskId;
			String groupName = GROUP_TASK_PREFIX + remindType.ordinal() + taskId;
			
			Date runTime = task.getRemindTime(remindType);
			String triggerName = String.valueOf(UUIDLong.longUUID());
			SimpleTrigger trigger = new SimpleTrigger(triggerName, groupName, task.getRemindTime(remindType));
			JobDataMap datamap = new JobDataMap();
			datamap.putAsString(DATA_ID_PREFIX, taskId.longValue());
			datamap.putAsString(REMIND_TYPE, remindType.ordinal());

			JobDetail job = new JobDetail(jobName, groupName, TaskRemindJob.class);
			job.setJobDataMap(datamap);
			sched.scheduleJob(job, trigger);
			
			if(logger.isDebugEnabled()) {
				logger.debug("为任务[id=" + taskId + ", subject=" + task.getSubject() + "]启动任务调度[类型：" + remindType.name() + "]" +
							 "启动时间为：" + Datetimes.formatDatetime(runTime));
			}
		} 
		catch (SchedulerException e) {
			logger.error("为任务[id=" + task.getId() + ", subject=" + task.getSubject() + "]设置提醒任务调度过程中出现异常：", e);
		}
	}
	
	/**
	 * 取消任务提前提醒任务调度
	 * @param taskId	任务ID
	 */
	public static void cancelRemind(Long taskId) {
		try {
			Scheduler sched = QuartzListener.getScheduler();
			sched.deleteJob(JOB_TASK_PREFIX + RemindType.BeforeStart.ordinal() + taskId, GROUP_TASK_PREFIX + RemindType.BeforeStart.ordinal() + taskId);
			sched.deleteJob(JOB_TASK_PREFIX + RemindType.BeforeEnd.ordinal() + taskId, GROUP_TASK_PREFIX + RemindType.BeforeEnd.ordinal() + taskId);
			
			if(logger.isDebugEnabled()) {
				logger.debug("为任务[id=" + taskId + "]取消提醒任务调度于" + Datetimes.formatDatetime(new Date(System.currentTimeMillis())));
			}
		} 
		catch (SchedulerException e) {
			logger.error("删除工作任务[id=" + taskId + "]提醒任务调度过程中出现异常：", e);
		}
	}
	/*-------------------------------------任务调度End--------------------------------------*/
	
	/*---------------------------------任务树相关Begin--------------------------------------*/
	/**
	 * 对需要展现成为任务树的数据进行处理，设置好父子级别关系
	 */
	public static void tree(List<TaskInfo> tasks) {
		if(CollectionUtils.isNotEmpty(tasks)) {
			Map<Long, TaskInfo> map = new HashMap<Long, TaskInfo>();
			for(TaskInfo t : tasks) {
				map.put(t.getId(), t);
			}
			
			for(TaskInfo t2 : tasks) {
				Long parentId = t2.getParentTaskId();
				if(parentId != -1l && map.get(parentId) != null) {
					map.get(parentId).addChild(t2);
				}
			}
		}
	}
	
	/**
	 * 获取任务树对应的HTML代码
	 * @param tasks 任务树中的全部节点集合，按照任务的逻辑层级深度升序排列(也即第一个元素为根节点)
	 */
	public static String getTaskTreeHTML(List<TaskInfo> tasks, TaskInfo selectedTask) {
		StringBuilder sb = new StringBuilder();
		TaskInfo root = tasks.get(0);
		taskInfo2HTML(root, selectedTask, sb);
		return sb.toString();
	}
	
	/**
	 * 将任务解析为任务树列表中的一行所对应的HTML代码
	 * @param task	任务节点
	 * @param selectedTask	当前任务(默认处于选中状态，不可点击)
	 */
	private static void taskInfo2HTML(TaskInfo task, TaskInfo selectedTask, StringBuilder sb) {
		List<TaskInfo> children = task.getChildren();
		boolean hasChild = CollectionUtils.isNotEmpty(children);
		sb.append(TaskUtils.getTreeNodeHTML(task, selectedTask));
		if(hasChild) {
			for(TaskInfo child : children) {
				taskInfo2HTML(child, selectedTask, sb);
			}
		}
	}
	
	/**
	 * 获取单个任务所在任务树节点中对应的HTML代码
	 * @param task		任务节点
	 * @param selectedTask	当前任务(默认处于选中状态，不可点击)
	 * @return	对应树(Table Tree)节点的HTML代码
	 */
	private static String getTreeNodeHTML(TaskInfo task, TaskInfo selectedTask) {
		StringBuilder ret = new StringBuilder();
		String pId = task.getParent() == null ? task.getParentTaskId().toString() : task.getParent().getLogicalPath();
		ret.append("<tr id='TR_" + task.getLogicalPath() + "' pid='TR_" +  pId  + "' style='display:block;'>\n");
		ret.append(getSubjectHTML(task, selectedTask));
		ret.append(getFinishRateHTML(task));
		ret.append(getTaskCycleHTML(task));
		ret.append(getManagersHTML(task));
		ret.append("</tr>\n");
		return ret.toString();
	}
	
	private static final String ANCHOR = "a";
	private static final String SPAN = "span";
	
	/**
	 * 获取<b>标题</b>列的HTML代码
	 * @param task		任务节点
	 * @param selectedTask	当前任务(默认处于选中状态，不可点击)
	 */
	private static String getSubjectHTML(TaskInfo task, TaskInfo selectedTask) {
		StringBuilder ret = new StringBuilder();
		ret.append("<td style=\"text-align:left;text-indent:" + (task.getLogicalDepth() - 1) * 20 + "pt;\">\n");
		boolean hasChild = task.hasChild();
		if(hasChild) {
			ret.append("<img src='" + SystemEnvironment.getA8ContextPath() + "/apps_res/taskmanage/images/nolines_minus.gif' " +
					   "id='IMG_" + task.getLogicalPath() + "' border='0' style='cursor:hand;' align='absmiddle' " +
					   "onclick=\"javascript:showHiddenNode(this, 'TR_" + task.getLogicalPath() + "')\" >\n");
		}
		else {
			ret.append("<img src='" + SystemEnvironment.getA8ContextPath() + "/apps_res/taskmanage/images/nolines_null.gif' " +
					   "border='0' align='absmiddle' >\n");
		}
		
		Long taskId = task.getId();
		boolean anchor = anchor(task, selectedTask);
		String label = anchor ? ANCHOR : SPAN;
		//String link = anchor ? " href=\"javascript:viewTaskInfoInTree('" + taskId + "')\" " : "";
		String click = anchor ? "viewTaskInfoInTree(\"" + taskId + "\");" : "";
		ret.append("<" + label + " id='anchor-" + taskId + "' href='javascript:void(0);' onclick='select(this);" + click + "return false;' title='" + Strings.toHTML(task.getSubject()) + "'>\n" + 
				   		Strings.toHTML(Strings.getLimitLengthString(task.getSubject(), 50, "...")) + "\n" + 
				   "</" + label + ">\n");
		ret.append("</td>\n");
		return ret.toString();
	}
	
	/**
	 * 绘制任务树DOM节点时，确定该节点是否允许点击查看详情
	 * @param task		要绘制的任务树节点
	 * @param selectedTask	当前任务(默认处于选中状态)
	 */
	private static boolean anchor(TaskInfo task, TaskInfo selectedTask) {
		// 当前任务无需再打开嵌套页面循环查看
		if(task.getId().equals(selectedTask.getId()))
			return false;
		
		// 任务树中不允许查看上级任务详情
		if(task.getLogicalDepth() < selectedTask.getLogicalDepth())
			return false;
		
		// 同级(逻辑层级深度相等)任务，上级任务不同的，不允许查看
		if(task.getLogicalDepth() == selectedTask.getLogicalDepth() &&
		   task.getParentTaskId() != selectedTask.getParentTaskId().longValue())
			return false;
		
		// 下级(逻辑层级更深)任务，非当前选中任务下级任务的，不允许查看
		if(task.getLogicalDepth() > selectedTask.getLogicalDepth() &&
		   task.getLogicalPath().indexOf(selectedTask.getId().toString()) == -1)
			return false;
		
		return true;
	}
	
	/**
	 * 获取<b>完成率</b>列的HTML代码
	 * @param task		任务
	 */
	private static String getFinishRateHTML(TaskInfo task) {
		StringBuilder ret = new StringBuilder();
		ret.append("<td>\n");
		ret.append("<div style='border:1px solid #A4A4A4;height:16px;overflow:hidden;'>\n");
		ret.append("<img src='" + SystemEnvironment.getA8ContextPath() + "/apps_res/project/images/pro_g.gif' width='" + task.getFinishRate() + "%' height='15' >\n");
		ret.append("</div>\n");
		ret.append("</td>\n");
		ret.append("<td align='left'>\n");
		ret.append("&nbsp;&nbsp;" + task.getFinishRate() + "%&nbsp;&nbsp;\n");
		ret.append("</td>\n");
		return ret.toString();
	}
	
	/**
	 * 获取<b>负责人</b>列的HTML代码
	 * @param task		任务
	 */
	private static String getManagersHTML(TaskInfo task) {
		StringBuilder ret = new StringBuilder();
		String managersName = Functions.showOrgEntities(task.getManagers(), V3xOrgEntity.ORGENT_TYPE_MEMBER, 
				TaskUtils.getCommonI18n("common.separator.label"));
		String managersNameHTML = Strings.toHTML(managersName);
		ret.append("<td alt='" + managersNameHTML + "' title='" + managersNameHTML + "'>\n");
		ret.append(Strings.toHTML(Strings.getLimitLengthString(managersName, 16, "...")) + "\n");
		ret.append("</td>" + "\n");
		return ret.toString();
	}
	
	/**
	 * 获取<b>任务周期</b>列的HTML代码
	 * @param task		任务
	 */
	private static String getTaskCycleHTML(TaskInfo task) {
		StringBuilder ret = new StringBuilder();
		ret.append("<td>\n");
		ret.append(task.getFormatDate(TaskDateEnum.PlannedStart) + 
				   "&nbsp;&nbsp;-&nbsp;&nbsp;" + 
				   task.getFormatDate(TaskDateEnum.PlannedEnd) + "\n");
		ret.append("</td>\n");
		return ret.toString();
	}
	/*---------------------------------任务树相关End--------------------------------------*/
	
	/**
	 * 更新工作任务的全文检索信息
	 */
	public static void updateIndex(Long taskId, UpdateIndexManager updateIndexManager) {
		try {
			updateIndexManager.update(taskId, ApplicationCategoryEnum.taskManage.key());
		} 
		catch(Exception e) {
			logger.error("更新任务[id=" + taskId + "]全文检索信息时出现异常:", e);
		}
	}
	
	/**
	 * 删除工作任务的全文检索信息
	 */
	public static void deleteIndex(Long taskId, IndexManager indexManager) {
		try {
			indexManager.deleteFromIndex(ApplicationCategoryEnum.taskManage, taskId);
		} 
		catch(Exception e) {
			logger.error("删除任务[id=" + taskId + "]全文检索信息时出现异常:", e);
		}
	}
	
	/**
	 * 设置任务信息页面所需的元数据信息，用于新建、查看、修改任务和工作任务管理统计等场景
	 */
	public static void renderMetadatas4Task(ModelAndView mav, MetadataManager metadataManager) {
		Metadata remindTimeMetaData = metadataManager.getMetadata(MetadataNameEnum.common_remind_time);
		mav.addObject("remindTimeMetaData", remindTimeMetaData);
		
		Metadata comImportanceMetadata = metadataManager.getMetadata(MetadataNameEnum.common_importance);	
        mav.addObject("comImportanceMetadata", comImportanceMetadata);
        
        Metadata taskStatusMetadata = metadataManager.getMetadata(MetadataNameEnum.task_status);
        mav.addObject("taskStatusMetadata", taskStatusMetadata);
        
        Metadata riskMetadata = metadataManager.getMetadata(MetadataNameEnum.task_risk_level);	
        mav.addObject("riskMetadata", riskMetadata);
	}
	
	/**
	 * 获取任务统计中指定统计类型的开始统计时间
	 * @param sp	统计时间段类型
	 */
	public static Date getBeginDate(StatisticPeriod sp) {
		switch(sp) {
		case Day : return Datetimes.getTodayFirstTime();
		case Week : return Datetimes.getFirstDayInWeek(new Date());
		case Month : return Datetimes.getFirstDayInMonth(new Date());
		}
		return null;
	}

	/**
	 * 获取任务统计中指定统计类型的结束统计时间
	 * @param sp	统计时间段类型
	 */
	public static Date getEndDate(StatisticPeriod sp) {
		switch(sp) {
		case Day : return Datetimes.getTodayLastTime();
		case Week : return Datetimes.getLastDayInWeek(new Date());
		case Month : return Datetimes.getLastDayInMonth(new Date());
		}
		return null;
	}
	
	/**
	 * 查看项目任务时，根据当前用户所在任务中的角色类型，判断用户是否有权查看所有项目任务
	 * @param roles		用户所在任务中的全部角色类型
	 * @return	能否查看所有项目任务
	 */
	public static boolean showAll(List<Byte> roles) {
		for(Byte role : roles) {
			if(role.byteValue() == ProjectMember.memberType_charge || role.byteValue() == ProjectMember.memberType_manager
					|| role.byteValue() == ProjectMember.memberType_assistant) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 项目任务查看时权限校验有一特别之处：即便与任务无关，但项目领导、负责人或助理，也能无视任务的权限校验而能直接查看任务信息<br>
	 * @param projectId		项目ID
	 * @param userId		当前用户ID
	 * @return		当前用户是否为项目的领导、负责人或助理
	 */
	public static boolean isUserPM(Long projectId, Long userId, ProjectManager projectManager) {
		if(projectId == null || projectId == TaskConstants.PROJECT_NONE)
			return false;
		
		List<Byte> roles = projectManager.getProjectRoles(userId, projectId);
		return showAll(roles);
	}
	
	public static String getViewUrl(Long taskId) {
		return SystemEnvironment.getA8ContextPath() + "/taskManage.do?method=viewTaskDetail&id=" + taskId + "&random=" + UUIDLong.longUUID();
	}
	
	public static boolean justCreator(TaskInfo task, Long userId) {
		return task.getManagers().indexOf(userId.toString()) == -1 && 
			   (Strings.isBlank(task.getParticipators()) || task.getParticipators().indexOf(userId.toString()) == -1);
	}
	
}
