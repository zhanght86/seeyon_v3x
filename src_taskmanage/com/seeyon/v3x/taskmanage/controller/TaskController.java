package com.seeyon.v3x.taskmanage.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.seeyon.v3x.collaboration.manager.WorkStatManager;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.filemanager.Attachment;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.metadata.Metadata;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.security.AccessControlBean;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.BaseController;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.FileToExcelManager;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.project.domain.ProjectPhase;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.project.util.ProjectUtils;
import com.seeyon.v3x.project.webmodel.ProjectCompose;
import com.seeyon.v3x.taskmanage.domain.TaskFeedback;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.taskmanage.domain.TaskInfoBody;
import com.seeyon.v3x.taskmanage.domain.TaskReply;
import com.seeyon.v3x.taskmanage.manager.TaskInfoManager;
import com.seeyon.v3x.taskmanage.manager.TaskRoleManager;
import com.seeyon.v3x.taskmanage.utils.GanttItem;
import com.seeyon.v3x.taskmanage.utils.GanttUtils;
import com.seeyon.v3x.taskmanage.utils.ProjectTree;
import com.seeyon.v3x.taskmanage.utils.StatisticCondition;
import com.seeyon.v3x.taskmanage.utils.TaskConstants;
import com.seeyon.v3x.taskmanage.utils.TaskMsgUtils;
import com.seeyon.v3x.taskmanage.utils.TaskQueryModel;
import com.seeyon.v3x.taskmanage.utils.TaskUtils;
import com.seeyon.v3x.taskmanage.utils.UserRoles;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.ListType;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.TaskAclEnum;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 任务管理Controller
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-25
 */
public class TaskController extends BaseController {
	private static final Log logger = LogFactory.getLog(TaskController.class);
	
	private UserMessageManager userMessageManager;	
	private TaskInfoManager taskInfoManager;
	private TaskRoleManager taskRoleManager;
	private MetadataManager metadataManager;
	private AttachmentManager attachmentManager;
	private FileToExcelManager fileToExcelManager;
	private IndexManager indexManager;
	private UpdateIndexManager updateIndexManager;
	private ProjectManager projectManager;
	private WorkStatManager workStatManager;
	private AppLogManager appLogManager;
	
	@Override
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}
	
	/**
	 * 分解、新建任务框架页面
	 */
	public ModelAndView addTaskPageFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("taskmanage/taskInfoIframe");
	}

	/**
	 * 进入新建任务页面。操作入口包括：点击"新建任务"菜单、对某一任务进行"分解"(产生新的子任务)
	 */
	public ModelAndView addTaskPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/taskInfo");
		TaskUtils.renderMetadatas4Task(mav, metadataManager);
		
		String from = request.getParameter("from");
		if(TaskConstants.FROM_DECOMPOSE.equals(from)) {
			Long userId = CurrentUser.get().getId();
			Long parentTaskId = NumberUtils.toLong(request.getParameter("parentTaskId"));
			TaskInfo parentTask = this.taskInfoManager.get(parentTaskId);
			UserRoles roles = TaskUtils.getTaskAcl(parentTask, userId);
			boolean noDecomposeAuth = !roles.isManager();
			if(parentTask == null)
				return mav.addObject("parentTaskIsNull", true);
			else if(noDecomposeAuth)
				return mav.addObject("noDecomposeAuth", noDecomposeAuth);
			
			TaskInfo task = TaskUtils.parse2ChildTask(parentTask);
			mav.addObject("task", task);
		}
		
		return mav;
	}
	
	/**
	 * 选择上级任务框架页面
	 */
	public ModelAndView showParentTasksFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("taskmanage/parentTasksFrame");
	}
	
	/**
	 * 显示用户可以选择的上级任务列表，用于新建任务时选择上级任务
	 */
	public ModelAndView showParentTasks(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long userId = CurrentUser.get().getId();
		TaskQueryModel tqm = TaskQueryModel.parse(request);
		List<TaskInfo> tasks = taskInfoManager.getTasks(ListType.Parent, userId, tqm);
		if(CollectionUtils.isEmpty(tasks) && Strings.isBlank(request.getParameter("condition"))) {
			super.rendJavaScript(response, "parent.handleNoParentTasks()");
			return null;
		}
		Metadata taskStatusMetadata = metadataManager.getMetadata(MetadataNameEnum.task_status);
		return new ModelAndView("taskmanage/parentTasks", "tasks", tasks).addObject("taskStatusMetadata", taskStatusMetadata);
	}
	
	/**
	 * 显示高级属性设置页面
	 */
	public ModelAndView advancedSettings(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("taskmanage/advancedSettings");
	}
	
	/**
	 * 保存新建或分解的任务，随后跳转到"任务管理"列表页面、项目信息首页或连续添加任务页面
	 */
	public ModelAndView addTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = CurrentUser.get();
		Long userId = user.getId();
		String from = request.getParameter("from");
		boolean fromDecompose = TaskConstants.FROM_DECOMPOSE.equals(from);
		
		TaskInfo task = new TaskInfo();
		super.bind(request, task);
		if (task.isFullTime()) {
			task.setPlannedEndTime(Datetimes.getTodayLastTime(task.getPlannedEndTime()));
		}
		Date now = new Date(System.currentTimeMillis());
		task.setCreateUser(userId);
		task.setCreateTime(now);
		task.setUpdateUser(userId);
		task.setUpdateTime(now);
		task.setNewId();
		String parentLogicalPath = request.getParameter("parentLogicalPath");
		if(Strings.isNotBlank(parentLogicalPath)) {
			// 分解任务场景下，在<b>保存过程中</b>，暂不对父任务的有效性进行校验
			// 原因有二：这种情况实际应用中较少见；父任务有效性在<b>分解操作之前</b>已进行校验，此处再重复校验对性能有一定影响。
			// 如果确实出现分解任务保存过程中，父任务被删除或当前用户分解权限被取消，保存操作仍能正确进行，但树状显示时外观略有影响
			task.setLogicalPath(parentLogicalPath + '.' + task.getId());
		} else {
			task.setLogicalPath(task.getId().toString());
		}
		task.setLogicalDepth(task.getLogicalPath().split("[.]").length);
		
		String attFlag = attachmentManager.create(ApplicationCategoryEnum.taskManage, task.getId(), task.getId(), request);
		task.setHasAttachments(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attFlag));
		
		this.taskInfoManager.save(task);
		this.taskRoleManager.saveTaskRoles(task);
		
		TaskUtils.remind4Create(task);
		TaskMsgUtils.sendMsg4Create(task, userMessageManager);
		try {
			IndexInfo indexInfo = taskInfoManager.getIndexInfo(task);
			indexManager.index(indexInfo);
		} 
		catch(Exception e) {
			logger.error("创建任务[id=" + task.getId() + "]时，全文检索入库出现异常:", e);
		}
		
		appLogManager.insertLog(user, AppLogAction.Task_Create, user.getName(), task.getSubject());
		
		boolean continuation = Strings.isNotBlank(request.getParameter("continuation"));
		boolean justCreator = TaskUtils.justCreator(task, userId);
		super.rendJavaScript(response, "parent.afterSave(" + fromDecompose + "," + continuation + "," + justCreator + ",'" + from + "');");
		return null;
	}
	
	/**
	 * 进入任务详情查看页面
	 */
	public ModelAndView viewTaskDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("taskmanage/taskDetailIframe");
	}
	
	/**
	 * 进入查看或修改任务框架页面，包含对功能菜单的权限控制
	 */
	public ModelAndView taskInfoIframe(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("taskmanage/taskInfoIframe");
	}
	
	/**
	 * 查看任务、任务反馈列表时，需先校验任务的有效性、用户是否具备查看权限<br>
	 * 如校验通过，则将任务信息、权限信息设入ModelAndView中以便后用<br>
	 * @return 校验是否通过，以便Controller后续据此进行动作
	 */
	private boolean validateTask(ModelAndView mav, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long taskId = NumberUtils.toLong(request.getParameter("taskId"), NumberUtils.toLong(request.getParameter("id")));
		TaskInfo task = this.taskInfoManager.get(taskId);
		Long userId = CurrentUser.get().getId();
		UserRoles roles = TaskUtils.getTaskAcl(task, userId);
		boolean manageMode = BooleanUtils.toBoolean(request.getParameter("manageMode"));
		// 任务信息为空，用户不具备任何权限且用户不在管理模式下或不具备项目管理权限，校验不通过
		if(task == null || (roles.isJamesWong() && !manageMode && !TaskUtils.isUserPM(task.getProjectId(), userId, projectManager))) {
			super.rendJavaScript(response, "parent.handleInvalidNoAuth(" + (task == null) + "," + Strings.isNotBlank(request.getParameter("msgFlag")) + ");");
			return false;
		}
		
		mav.addObject("task", task).addObject("roles", roles);
		return true;
	}
	
	/**
	 * 进入查看或修改任务页面
	 */
	public ModelAndView taskInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/taskInfo");
		if(!this.validateTask(mav, request, response)) {
			return null;
		}
		
		TaskInfo task = (TaskInfo)mav.getModel().get("task");
		Long taskId = task.getId();
		Long userId = CurrentUser.get().getId();
    	if(TaskAclEnum.View.name().equals(request.getParameter("flag"))) {
    		List<TaskReply> replys = this.taskInfoManager.getReplys(taskId);
    		mav.addObject("replys", replys);
    	}
    	else if(TaskAclEnum.Edit.name().equals(request.getParameter("flag"))) {
    		UserRoles roles = (UserRoles)mav.getModel().get("roles");
    		if(!roles.canEdit()) {
    			super.rendJavaScript(response, "alert(parent.v3x.getMessage('TaskManage.no_edit_auth'));" +
    										   "parent.location.href = parent.location;");
    			return null;
    		}
    		
    		Long lockEditorId = this.taskInfoManager.getLockEditorId(taskId);
    		if(lockEditorId != null && !lockEditorId.equals(userId)) {
    			String lockEditorName = Functions.showMemberName(lockEditorId);
    			super.rendJavaScript(response, "alert(parent.v3x.getMessage('TaskManage.edit_locked', '" + lockEditorName + "'));" +
    										   "parent.location.href = parent.location;");
    			return null;
    		}
    	}
    	
    	// 附件权限控制，比如将某一协同作为附件关联，点击查看时需进行权限校验，先在此处进行依附主体的权限设置
		if(task.isHasAttachments()) {
			AccessControlBean.getInstance().addAccessControl(ApplicationCategoryEnum.taskManage, taskId.toString(), userId);
		}
    	
    	TaskInfoBody body = this.taskInfoManager.getTaskBody(taskId);
		task.setContent(body.getContent());
		mav.addObject("task", task);
		
    	List<Attachment> attachments = this.attachmentManager.getByReference(taskId, taskId);
    	mav.addObject("attachments", attachments);
    	
    	TaskUtils.renderMetadatas4Task(mav, this.metadataManager);
		return mav;
	}
	
	/**
	 * 查看全部任务回复
	 */
	public ModelAndView viewAllReplys(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/allreplys");
		Long taskId = NumberUtils.toLong(request.getParameter("taskId"));
		Long userId = CurrentUser.get().getId();
		TaskInfo task = this.taskInfoManager.get(taskId);
		if(task == null || (!TaskUtils.authCheck(task, userId, TaskAclEnum.Reply) && !TaskUtils.isUserPM(task.getProjectId(), userId, projectManager))) {
			super.rendJavaScript(response, "window.dialogArguments.handleInvalid4Replys(" + (task == null) + ");" +
										   "window.close();");
			return null;
		}
		
		List<TaskReply> replys = this.taskInfoManager.getReplys(taskId);
		return mav.addObject("replys", replys);
	}
	
	/**
	 * 完成对任务的修改
	 */
	public ModelAndView modifyTask(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = NumberUtils.toLong(request.getParameter("id"));
		User user = CurrentUser.get();
		Long userId = user.getId();
		TaskInfo task = this.taskInfoManager.get(id);
		if(task == null || !TaskUtils.authCheck(task, userId, TaskAclEnum.Edit)) {
			super.rendJavaScript(response, "parent.handleInvalid4Modify(" + (task == null) + ");");
			return null;
		}
		boolean oldImport2Cal = task.isImportToCalendar();
		
		super.bind(request, task);
		if(!"1".equals(request.getParameter("fullTime"))){
			task.setFullTime(false);
		}
		if(request.getParameter("importToCalendar")==null)
			task.setImportToCalendar(false);
		if (task.isFullTime()) {
			task.setPlannedEndTime(Datetimes.getTodayLastTime(task.getPlannedEndTime()));
		}
		task.setUpdateTime(new Date(System.currentTimeMillis()));
		task.setUpdateUser(userId);
		if(task.isHasAttachments()) {
			this.attachmentManager.deleteByReference(id, id);
		}
		String attFlag = attachmentManager.create(ApplicationCategoryEnum.taskManage, id, id, request);
		task.setHasAttachments(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attFlag));
		
		this.taskInfoManager.update(task, task.getCalEventSyncAction(oldImport2Cal));
		this.taskRoleManager.updateTaskRoles(task);
		
		TaskUtils.remind4Update(task);
		TaskMsgUtils.sendMsg4Update(task, userMessageManager);
		TaskUtils.updateIndex(id, updateIndexManager);
		
		appLogManager.insertLog(user, AppLogAction.Task_Update, user.getName(), task.getSubject());
		
		super.rendJavaScript(response, "parent.endModifyTask();");
		return null;
	}
	
	/**
	 * 任务列表最外层框架页
	 */
	public ModelAndView listTasksIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/listTaskInfo_index");
		if(TaskConstants.FROM_PROJECT.equals(request.getParameter("from")) && !validateProject(mav, request, response))
			return null;
		
		return mav;
	}
	
	/**
	 * 左侧导航栏页面，包含按照人员导航和按照项目导航两种情况
	 */
	public ModelAndView navigation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/navigation");
		User user = CurrentUser.get();
		Long userId = user.getId();
		String navigationType = request.getParameter("type");
		if(Strings.isBlank(navigationType) || TaskConstants.NAVIGATION_BY_MEMBER.equals(navigationType)) {
			List<Long> members = workStatManager.getMembersByGrantorIdAndType(user.getLoginAccount(), userId, 
					ApplicationCategoryEnum.taskManage.key());
			if(members == null) {
				members = Arrays.asList(userId);
			}
			else if(!members.contains(userId)) {
				members.add(userId);
			}
			mav.addObject("members", members);
		}
		
		if(TaskConstants.NAVIGATION_BY_PROJECT.equals(navigationType)) {
			ProjectTree projectTree = this.taskInfoManager.getProjectTree(userId, user.getLoginAccount());
			mav.addObject("projectTree", projectTree);
		}
		
		return mav;
	}
	
	/**
	 * 任务列表上列表下详图页面框架
	 */
	public ModelAndView listTasksFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("taskmanage/listTaskInfo_frame");
	}
	
	/**
	 * 我的任务 - 个人任务
	 */
	public ModelAndView listPersonalTasks(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/listTaskInfo");
		this.listTasks(mav, request, response, ListType.Personal);
		return mav;
	}
	
	public ModelAndView listPersonalTasks4FrontPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/listTaskInfoFrontPage");
		this.listTasks(mav, request, response, ListType.Personal);
		return mav;
	}
	
	/**
	 * 我的任务 - 已分派任务
	 */
	public ModelAndView listSentTasks(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/listTaskInfo");
		this.listTasks(mav, request, response, ListType.Sent);
		return mav;
	}
	
	/**
	 * 任务管理
	 */
	public ModelAndView listManageTasks(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/listTaskInfo");
		if(!BooleanUtils.toBoolean(request.getParameter("showEmpty"))) {
			ListType listType = ListType.Manage;
			if(Strings.isNotBlank(request.getParameter("projectId"))) {
				if(!this.validateProject(mav, request, response)) {
					return null;
				}
				listType = (ListType)mav.getModel().get("listType");
			}
			mav.addObject("listTypeName", listType.name());
			this.listTasks(mav, request, response, listType);
		}
		return mav;
	}
	
	/**
	 * 项目任务列表
	 */
	public ModelAndView listProjectTasks(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/listTaskInfo");
		ListType listType = ListType.valueOf(request.getParameter("listTypeName"));
		this.listTasks(mav, request, response, listType);
		return mav;
	}
	
	/**
	 * 列表展现辅助方法
	 */
	private void listTasks(ModelAndView mav, HttpServletRequest request, HttpServletResponse response, ListType listType) throws Exception {
		Long userId = NumberUtils.toLong(request.getParameter("userId"), CurrentUser.get().getId());
		TaskQueryModel tqm = TaskQueryModel.parse(request);
		List<TaskInfo> tasks = taskInfoManager.getTasks(listType, userId, tqm);
		TaskUtils.renderMetadatas4Task(mav, this.metadataManager);
		mav.addObject("tasks", tasks);
	}
	
	/**
	 * 项目任务情况下，需要对项目或项目阶段有效性进行校验：存在，或用户持有对其的访问权限<br>
	 * 在校验通过的情况下，将项目信息、项目阶段信息、用户权限对应的列表类型设入ModelMap
	 */
	private boolean validateProject(ModelAndView mav, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long projectId = NumberUtils.toLong(request.getParameter("projectId"));
		Long userId = CurrentUser.get().getId();
		
		ProjectSummary project = this.projectManager.getProject(projectId);
		boolean manageMode = ListType.Manage.name().equals(request.getParameter("from"));
		String js = manageMode ? "parent.getA8Top().reFlesh();" : "parent.getA8Top().contentFrame.topFrame.backToPersonalSpace();";
		if(project == null || !project.isValid()) {
			super.rendJavaScript(response, "alert('" + TaskUtils.getI18n("task.projectdeleted") + "');" + js);
			return false;
		}
		
		ProjectCompose projectCompose = this.projectManager.wrapProject(project);
		mav.addObject("projectCompose", projectCompose);
		
		// 添加项目进度
		ProjectUtils.addProjectProcess(projectCompose, mav);

		List<Byte> roles = this.projectManager.getProjectRoles(userId, projectId);
		if(CollectionUtils.isEmpty(roles)) {
			super.rendJavaScript(response, "alert('" + TaskUtils.getI18n("task.projectnoauth") + "');" + js);
			return false;
		}
		ListType listType = TaskUtils.showAll(roles) ? ListType.ProjectAll : ListType.ProjectMember;
		mav.addObject("listType", listType).addObject("listTypeName", listType.name());
		
		Long projectPhaseId = NumberUtils.toLong(request.getParameter("projectPhaseId"), project.getPhaseId());
		if(projectPhaseId != TaskConstants.PROJECT_PHASE_ALL) {
			ProjectPhase projectPhase = this.projectManager.getProjectPhase(projectPhaseId);
			if(projectPhase == null) {
				super.rendJavaScript(response, "alert('" + TaskUtils.getI18n("task.projectphasedeleted") + "');" +
						(manageMode ? "parent.getA8Top().reFlesh();" : 
							"parent.getA8Top().contentFrame.mainFrame.location.href = '/seeyon/project.do?method=projectInfo&projectId='" + projectId +"';"
						 ));
			}
			mav.addObject("phaseId", projectPhaseId);
		}
		
		return true;
	}
	
	/**
	 * 项目任务：根据角色获取listType
	 */
	private ListType getProjectListType(Long userId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ListType listType = ListType.parseName(request.getParameter("from"));
		
		if (Strings.isNotBlank(request.getParameter("projectId"))) {
			List<Byte> roles = this.projectManager.getProjectRoles(userId, NumberUtils.toLong(request.getParameter("projectId")));
			
			if (CollectionUtils.isNotEmpty(roles)) {
				listType = TaskUtils.showAll(roles) ? ListType.ProjectAll : ListType.ProjectMember;
			}
		}

		return listType;
	}
	
	/**
	 * 以甘特图表的样式展现任务
	 */
	public ModelAndView ganttChartTasks(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ListType listType = ListType.valueOf(request.getParameter("listTypeName"));
		Long userId = CurrentUser.get().getId();
		TaskQueryModel tqm = TaskQueryModel.parse(request);
		tqm.setPagination(false);
		
		List<TaskInfo> tasks = this.taskInfoManager.getTasks(listType, userId, tqm);
		List<GanttItem> items = GanttUtils.parse2GanttItems(tasks, tqm.getProjectPhaseId(), projectManager);
		
		ModelAndView mav = new ModelAndView("taskmanage/ganttTaskInfo", "items", items);
		TaskUtils.renderMetadatas4Task(mav, metadataManager);
		return mav;
	}
	
	/**
	 * 项目任务统计框架页面
	 */
	public ModelAndView statisticFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long projectId = NumberUtils.toLong(request.getParameter("projectId"));
		List<Long> memberIds = this.projectManager.getAllProjectMembers(projectId);
		if(CollectionUtils.isEmpty(memberIds)) {
			super.rendJavaScript(response, "alert('" + TaskUtils.getI18n("task.projectdeleted") + "');" +
			   							   "parent.getA8Top().contentFrame.topFrame.backToPersonalSpace();");
			return null;
		}
		ModelAndView mav = new ModelAndView("taskmanage/statisticFrame", "memberIds", memberIds);
		
		String memberIdsStr = StringUtils.join(memberIds.iterator(), ',');
		Metadata taskStatusMetadata = metadataManager.getMetadata(MetadataNameEnum.task_status);
		
		return mav.addObject("memberIdsStr", memberIdsStr).addObject("taskStatusMetadata", taskStatusMetadata);
	}
	
	/**
	 * 项目任务统计结果展现
	 */
	public ModelAndView statisticResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/statisticResult");
		StatisticCondition sc = StatisticCondition.parse(request);
		Map<Long, Object[]> ststMap = this.taskInfoManager.getProjectStatisticResult(sc);
		return mav.addObject("ststMap", ststMap).addObject("status", sc.getStatus()).addObject("memberIds", sc.getMemberIds())
				  .addObject("memberIdsStr", StringUtils.join(sc.getMemberIds().iterator(), ','));
	}
	
	/**
	 * 项目任务统计结果穿透查看：统计对应的任务列表
	 */
	public ModelAndView showProjectStatList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/statList");
		StatisticCondition sc = StatisticCondition.parse(request);
        List<TaskInfo> tasks = this.taskInfoManager.getTasks(sc);
        TaskUtils.renderMetadatas4Task(mav, metadataManager);
        return mav.addObject("tasks", tasks);
	}
	
	/**
	 * 删除(也即取消)选中的任务
	 */
	public ModelAndView deleteTasks(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Long> ids = FormBizConfigUtils.parseStr2Ids(request.getParameter("ids"));
		this.taskInfoManager.delete(ids);
		super.rendJavaScript(response, "parent.location.href = parent.location.href;");
		return null;
	}
	
	/**
	 * 将当前列表中的任务信息导出为Excel表格
	 */
	public ModelAndView exportToExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long userId = NumberUtils.toLong(request.getParameter("userId"), CurrentUser.get().getId());
		ListType listType = this.getProjectListType(userId, request, response);
		TaskQueryModel tqm = TaskQueryModel.parse(request);
		try {
			DataRecord dataRecord = this.taskInfoManager.getDataRecord(listType, userId, tqm);
			fileToExcelManager.save(request, response, dataRecord.getTitle(), dataRecord);
		} catch (Exception e) {
			logger.error("为用户[id=" + userId + "]导出任务列表为Excel时出现异常:", e);
			super.rendJavaScript(response, "alert(parent.v3x.getMessage('TaskManage.exception_export_excel'));");
		}
		return null;
	}
	
	/**
	 * 以树状结构展现当前列表中的所有任务信息，以层次分明的方式展现所有任务的进度、状态、耗时、负责人等信息
	 */
	public ModelAndView taskTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/taskTree");
		Long taskId = NumberUtils.toLong(request.getParameter("taskId"));
		Long userId = CurrentUser.get().getId();
		TaskInfo selectedTask = this.taskInfoManager.get(taskId);
		if(selectedTask == null) {
			return mav.addObject("taskIsNull", true);
		} else {
			UserRoles roles = TaskUtils.getTaskAcl(selectedTask, userId);
			boolean manageMode = BooleanUtils.toBoolean(request.getParameter("manageMode"));
			if(roles.isJamesWong() && !manageMode && !TaskUtils.isUserPM(selectedTask.getProjectId(), userId, projectManager)) {
				return mav.addObject("noAuth", true);
			}
		}
		
		List<TaskInfo> tasks = this.taskInfoManager.getTaskTree(selectedTask);
		int count = tasks.size();
		String taskTreeHTML = TaskUtils.getTaskTreeHTML(tasks, selectedTask);
		return mav.addObject("taskTreeHTML", taskTreeHTML).addObject("selectedTask", selectedTask).addObject("count", count);
	}
	
	/**
	 * 任务汇报列表框架页面
	 */
	public ModelAndView listTaskFeedbacksFrame(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("taskmanage/listTaskFeedback_frame");
	}
	
	/**
	 * 列出(按照任务汇报属性查询)当前所查看、修改任务的汇报、反馈列表
	 */
	public ModelAndView listTaskFeedbacks(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/listTaskFeedback");
		if(!this.validateTask(mav, request, response))
			return null;
		
		Long taskId = NumberUtils.toLong(request.getParameter("taskId"));
		SearchModel sm = SearchModel.getSearchModel(request);
		List<TaskFeedback> feedbacks = this.taskInfoManager.getFeedbacks(taskId, sm);
		
		return mav.addObject("feedbacks", feedbacks);
	}
	
	/**
	 * 进入新增任务汇报页面
	 */
	public ModelAndView addTaskFeedbackPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("taskmanage/taskFeedback");
	}
	
	/**
	 * 完成任务汇报
	 */
	public ModelAndView addTaskFeedback(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long taskId = NumberUtils.toLong(request.getParameter("taskId"));
		Long userId = CurrentUser.get().getId();
		TaskInfo task = this.taskInfoManager.get(taskId);
		if(task == null || !TaskUtils.authCheck(task, userId, TaskAclEnum.Feedback)) {
			super.rendJavaScript(response, "parent.handleInvalid4Feedback(" + (task == null) + ");");
			return null;
		}
		
		TaskFeedback feedback = new TaskFeedback();
		super.bind(request, feedback);
		feedback.setNewId();
		feedback.setCreateUser(userId);
		feedback.setCreateTime(new Timestamp(System.currentTimeMillis()));
		feedback.setUpdateUser(userId);
		feedback.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		feedback.setTaskId(taskId);
		String attFlag = attachmentManager.create(ApplicationCategoryEnum.taskManage, taskId, feedback.getId(), request);
		feedback.setHasAttachments(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attFlag));
//		//把任务汇报时间加入任务实际用时 5.0考虑
//		task.setActualTaskTime(task.getActualTaskTime()+feedback.getElapsedTime());
		this.taskInfoManager.saveFeedback(feedback);
		TaskMsgUtils.sendMsg4Feedback(task, userMessageManager);
		TaskUtils.updateIndex(taskId, updateIndexManager);
		
		super.rendJavaScript(response, "parent.location.href = parent.location;");
		return null;
	}
	
	/**
	 * 进入查看或修改任务汇报页面
	 */
	public ModelAndView viewOrModifyTaskFeedbackPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("taskmanage/taskFeedback");
		Long id = NumberUtils.toLong(request.getParameter("id"));
		TaskFeedback feedback = this.taskInfoManager.getFeedback(id);
		if(feedback == null) {
			super.rendJavaScript(response, "alert(parent.v3x.getMessage('TaskManage.task_feedback_deleted'));" +
			   							   "parent.location.href = parent.location;");
			return null;
		}
		
		Long taskId = feedback.getTaskId();
		Long userId = CurrentUser.get().getId();
		TaskInfo task = this.taskInfoManager.get(taskId);
		if(task == null) {
			super.rendJavaScript(response, "parent.handleInvalid4Feedback(true);");
			return null;
		}
		
		boolean auth = false;
		if(TaskAclEnum.Edit.name().equals(request.getParameter("flag"))) {
			auth = TaskUtils.authCheck(task, userId, TaskAclEnum.Feedback) && feedback.getCreateUser().longValue() == userId;
		}
		else {
			boolean manageMode = BooleanUtils.toBoolean(request.getParameter("manageMode"));
			auth = manageMode || TaskUtils.authCheck(task, userId, TaskAclEnum.View) 
							  || TaskUtils.isUserPM(task.getProjectId(), userId, projectManager);
		}
		
		if(auth == false) {
			super.rendJavaScript(response, "parent.handleInvalid4Feedback(false);");
			return null;
		}
		
		// 附件权限控制，比如将某一协同作为附件关联，点击查看时需进行权限校验，先在此处进行依附主体的权限设置
		if(feedback.isHasAttachments()) {
			AccessControlBean.getInstance().addAccessControl(ApplicationCategoryEnum.taskManage, feedback.getTaskId().toString(), userId);
		}
		
		List<Attachment> attachments = this.attachmentManager.getByReference(feedback.getTaskId(), id);
		return mav.addObject("feedback", feedback).addObject("attachments", attachments);
	}
	
	/**
	 * 完成对任务汇报的修改
	 */
	public ModelAndView modifyTaskFeedback(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long id = NumberUtils.toLong(request.getParameter("id"));
		TaskFeedback feedback = this.taskInfoManager.getFeedback(id);
		if(feedback == null) {
			super.rendJavaScript(response, "alert(parent.parent.v3x.getMessage('TaskManage.task_feedback_deleted'));" +
			   							   "parent.parent.location.href = parent.parent.location;");
			return null;
		}
		
		Long taskId = feedback.getTaskId();
		Long userId = CurrentUser.get().getId();
		TaskInfo task = this.taskInfoManager.get(taskId);
		if(task == null || !TaskUtils.authCheck(task, userId, TaskAclEnum.Feedback) || feedback.getCreateUser().longValue() != userId) {
			super.rendJavaScript(response, "parent.parent.handleInvalid4Feedback(" + (task == null) + ");");
			return null;
		}
		
		super.bind(request, feedback);
		feedback.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		feedback.setUpdateUser(userId);
		
		if(feedback.isHasAttachments()) {
			this.attachmentManager.deleteByReference(feedback.getTaskId(), id);
		}
		String attFlag = attachmentManager.create(ApplicationCategoryEnum.taskManage, feedback.getTaskId(), feedback.getId(), request);
		feedback.setHasAttachments(com.seeyon.v3x.common.filemanager.Constants.isUploadLocaleFile(attFlag));
		
		this.taskRoleManager.save(feedback);
		TaskMsgUtils.sendMsg4EditFeedback(task, userMessageManager);
		TaskUtils.updateIndex(taskId, updateIndexManager);
		
		super.rendJavaScript(response, "parent.location.href = parent.location;");
		return null;
	}
	
	/**
	 * 删除选中的任务汇报
	 */
	public ModelAndView deleteTaskFeedbacks(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Long taskId = NumberUtils.toLong(request.getParameter("taskId"));
		this.taskInfoManager.deleteFeedbacks(request.getParameter("ids"));
		TaskUtils.updateIndex(taskId, updateIndexManager);
		
		super.rendJavaScript(response, "parent.window.location.href = parent.window.location;");
		return null;
	}
	
	/*-----------------------------------setter--------------------------------*/
	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
	public void setFileToExcelManager(FileToExcelManager fileToExcelManager) {
		this.fileToExcelManager = fileToExcelManager;
	}
	public void setTaskRoleManager(TaskRoleManager taskRoleManager) {
		this.taskRoleManager = taskRoleManager;
	}
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
	public void setTaskInfoManager(TaskInfoManager taskInfoManager) {
		this.taskInfoManager = taskInfoManager;
	}
	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}
	public void setWorkStatManager(WorkStatManager workStatManager) {
		this.workStatManager = workStatManager;
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
}
/* 无论帅哥还是美女，您竟能够访问此处，贫僧真个佩服，您果然是新一代的开山怪，自然新帅派:) */