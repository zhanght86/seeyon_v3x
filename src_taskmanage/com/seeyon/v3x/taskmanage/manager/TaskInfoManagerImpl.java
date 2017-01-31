package com.seeyon.v3x.taskmanage.manager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.bulletin.util.Constants;
import com.seeyon.v3x.calendar.manager.CalEventManager;
import com.seeyon.v3x.common.appLog.AppLogAction;
import com.seeyon.v3x.common.appLog.manager.AppLogManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.constants.ApplicationCategoryEnum;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.exceptions.MessageException;
import com.seeyon.v3x.common.filemanager.manager.AttachmentManager;
import com.seeyon.v3x.common.lock.domain.Lock;
import com.seeyon.v3x.common.lock.manager.LockManager;
import com.seeyon.v3x.common.metadata.MetadataNameEnum;
import com.seeyon.v3x.common.metadata.manager.MetadataManager;
import com.seeyon.v3x.common.online.OnlineRecorder;
import com.seeyon.v3x.common.online.OnlineUser;
import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.common.usermessage.UserMessageManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.excel.DataCell;
import com.seeyon.v3x.excel.DataRecord;
import com.seeyon.v3x.excel.DataRow;
import com.seeyon.v3x.formbizconfig.utils.FormBizConfigUtils;
import com.seeyon.v3x.formbizconfig.utils.SearchModel;
import com.seeyon.v3x.index.share.datamodel.AuthorizationInfo;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;
import com.seeyon.v3x.index.share.interfaces.IndexEnable;
import com.seeyon.v3x.index.share.interfaces.IndexManager;
import com.seeyon.v3x.indexInterface.IndexUtil;
import com.seeyon.v3x.indexInterface.IndexManager.UpdateIndexManager;
import com.seeyon.v3x.organization.domain.V3xOrgEntity;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.domain.ProjectType;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.taskmanage.dao.TaskFeedbackDao;
import com.seeyon.v3x.taskmanage.dao.TaskInfoBodyDao;
import com.seeyon.v3x.taskmanage.dao.TaskInfoDao;
import com.seeyon.v3x.taskmanage.dao.TaskReplyDao;
import com.seeyon.v3x.taskmanage.dao.TaskRoleDao;
import com.seeyon.v3x.taskmanage.domain.TaskFeedback;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.taskmanage.domain.TaskInfoBody;
import com.seeyon.v3x.taskmanage.domain.TaskReply;
import com.seeyon.v3x.taskmanage.domain.TaskRole.RoleType;
import com.seeyon.v3x.taskmanage.utils.ProjectTree;
import com.seeyon.v3x.taskmanage.utils.StatisticCondition;
import com.seeyon.v3x.taskmanage.utils.TaskConstants;
import com.seeyon.v3x.taskmanage.utils.TaskMsgUtils;
import com.seeyon.v3x.taskmanage.utils.TaskQueryModel;
import com.seeyon.v3x.taskmanage.utils.TaskUtils;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.CalEventSyncType;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.ListType;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.StatisticPeriod;
import com.seeyon.v3x.taskmanage.utils.TaskConstants.TaskAclEnum;
import com.seeyon.v3x.taskmanage.utils.TaskMsgUtils.ExceptionMsg;
import com.seeyon.v3x.taskmanage.utils.TaskQueryModel.TaskQueryType;
import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.util.Strings;

/**
 * 任务管理主业务逻辑实现
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-25
 */
public class TaskInfoManagerImpl implements TaskInfoManager, IndexEnable {
	private static final Log logger = LogFactory.getLog(TaskInfoManagerImpl.class);
	
	private TaskInfoDao taskInfoDao;
	private TaskInfoBodyDao taskInfoBodyDao;
	private TaskRoleDao taskRoleDao;
	private TaskFeedbackDao taskFeedbackDao;
	private TaskReplyDao taskReplyDao;
	private CalEventManager calEventManager;
	private ProjectManager projectManager;
	private UserMessageManager userMessageManager;
	private MetadataManager metadataManager;
	private AttachmentManager attachmentManager;
	private IndexManager indexManager;
	private UpdateIndexManager updateIndexManager;
	private LockManager taskManageLock;
	private AppLogManager appLogManager;

	public void save(TaskInfo task) {
		TaskInfoBody content = new TaskInfoBody(task.getId(), task.getContent());
		this.taskInfoDao.save(task);
		this.taskInfoBodyDao.save(content);
		
		if(task.isImportToCalendar()) {
			this.calEventManager.saveCalEventFromTask(task);
		}
	}
	
	public TaskInfo get(Long taskId) {
		return this.taskInfoDao.get(taskId);
	}
	
	public void delete(List<Long> ids) {
		User user = CurrentUser.get();
		if(CollectionUtils.isNotEmpty(ids)) {
			for(Long id : ids) {
				this.deleteTask(id, user);
			}
		}
	}
	
	private void deleteTask(Long id, User user) {
		try {
			List<Long> childTaskIds = this.taskInfoDao.getChildTaskIds(id);
			if(CollectionUtils.isNotEmpty(childTaskIds)) {
				for(Long childTaskId : childTaskIds) {
					this.deleteTask(childTaskId, user);
				}
			}
			this.deleteSingleTask(id, user);
		}
		catch(Exception e) {
			logger.error("递归删除任务[id=" + id + "]时出现异常：", e);
		}
	}
	
	private void deleteSingleTask(Long id, User user) {
		try {
			this.taskRoleDao.delete(new Object[][]{{"taskId", id}});
			this.taskReplyDao.delete(new Object[][]{{"taskId", id}});
			this.taskFeedbackDao.delete(new Object[][]{{"taskId", id}});
			this.taskInfoBodyDao.delete(id);
			this.attachmentManager.deleteByReference(id);
			
			TaskInfo task = this.get(id);
			String subject = task.getSubject();
			if(task.isImportToCalendar()) {
				this.calEventManager.deleteCalEventFromTask(id);
			}
			
			TaskMsgUtils.sendMsg4Delete(task, userMessageManager);
			TaskUtils.cancelRemind(id);
			
			this.taskInfoDao.delete(task);
			TaskUtils.deleteIndex(id, indexManager);
			
			appLogManager.insertLog(user, AppLogAction.Task_Delete, user.getName(), subject);
		}
		catch(Exception e) {
			logger.error("单一删除任务[id=" + id + "]时出现异常：", e);
		}
	}
	
	public List<TaskFeedback> getFeedbacks(Long taskId, SearchModel sm) {
		return this.taskFeedbackDao.getFeedbacks(taskId, sm);
	}

	public void deleteFeedbacks(String feedbackIdsStr) {
		List<Long> feedbackIds = FormBizConfigUtils.parseStr2Ids(feedbackIdsStr);
		if(CollectionUtils.isNotEmpty(feedbackIds)) {
			this.taskFeedbackDao.deleteByIds(feedbackIds);
		}
	}
	
	public List<TaskInfo> getTasks(ListType listType, Long userId, TaskQueryModel tqm) {
		return this.taskInfoDao.getTasks(listType, userId, tqm);
	}

	public List<TaskInfo> getTasks(ListType listType, Long userId, Date startTime, Date endTime) {
		return this.taskInfoDao.getTasksForTiming(listType, userId,  startTime, endTime);
	}
	public int getCountMyPendingTask(Long userId){
		TaskQueryType queryType = TaskQueryType.value(TaskQueryType.status.name());
		TaskQueryModel tqm = new TaskQueryModel(queryType, "1,2,3", null);
		
		return this.taskInfoDao.getCountPendingTask(ListType.Personal, userId, tqm);
	}
	
	public List<TaskInfo> getProjectTasks(Long projectId, Long projectPhaseId, Long userId) {
		boolean isLeaderOrManager = this.projectManager.isProjectLeaderOrManager(userId, projectId);
		ListType listType =  isLeaderOrManager ? ListType.ProjectAll : ListType.ProjectMember;
		
		TaskQueryModel tqm = new TaskQueryModel();
		tqm.setProjectId(projectId);
		tqm.setProjectPhaseId(projectPhaseId);
		tqm.setPagination(false);
		
		return this.taskInfoDao.getTasks(listType, userId, tqm);
	}
	
	public List<TaskInfo> getProjectTasks4Section(Long projectId, Long projectPhaseId, Long userId, boolean leaderOrManager) {
		ListType listType =  leaderOrManager ? ListType.ProjectAll : ListType.ProjectMember;
		
		TaskQueryModel tqm = new TaskQueryModel();
		tqm.setProjectId(projectId);
		tqm.setProjectPhaseId(projectPhaseId);

		Pagination.setFirstResult(0);
		Pagination.setMaxResults(Constants.SECTION_TABLE_COLUMNS);
		Pagination.setNeedCount(false);
		
		return this.taskInfoDao.getTasks(listType, userId, tqm);
	}

	public DataRecord getDataRecord(ListType listType, Long userId, TaskQueryModel tqm) {
		tqm.setPagination(false);
		List<TaskInfo> tasks = this.getTasks(listType, userId, tqm);
		if(CollectionUtils.isEmpty(tasks)) 
			return null;
		
		DataRecord dataRecord = new DataRecord(); 
		String[] columnNames = {
				TaskUtils.getI18n("task.name"), TaskUtils.getCommonI18n("common.importance.label"),
				TaskUtils.getI18n("task.status"), TaskUtils.getI18n("task.risk"), 
				TaskUtils.getI18n("task.finishrate"), TaskUtils.getCommonI18n("common.date.begindate.label"), 
				TaskUtils.getCommonI18n("common.date.enddate.label"), TaskUtils.getI18n("task.manager")};
		short[] width = {50, 15, 15, 12, 15, 20, 20, 30};
		
		dataRecord.setColumnName(columnNames);
		dataRecord.setColumnWith(width);
		dataRecord.setTitle(TaskUtils.getI18n("task.list." + listType.name()));
		dataRecord.setSheetName("sheet1");
		
		DataRow[] rows = new DataRow[tasks.size()];
		int i = 0;
		for(TaskInfo task : tasks) {
			DataRow row = new DataRow();
			row.addDataCell(task.getSubject(), DataCell.DATA_TYPE_TEXT);
			
			String importance = this.metadataManager.getMetadataItemLabel(MetadataNameEnum.common_importance, task.getImportantLevel().toString());
			row.addDataCell(TaskUtils.getCommonI18n(importance), DataCell.DATA_TYPE_TEXT);
			
			String status = this.metadataManager.getMetadataItemLabel(MetadataNameEnum.task_status, task.getStatus().toString());
			row.addDataCell(TaskUtils.getI18n(status), DataCell.DATA_TYPE_TEXT);
			
			String risk = this.metadataManager.getMetadataItemLabel(MetadataNameEnum.task_risk_level, task.getRiskLevel().toString());
			row.addDataCell(TaskUtils.getI18n(risk), DataCell.DATA_TYPE_TEXT);
			
			row.addDataCell(Functions.showRate(task.getFinishRate(), true), DataCell.DATA_TYPE_TEXT);
			
			if(task.isFullTime()) {
				row.addDataCell(Datetimes.formatDate(task.getPlannedStartTime()), DataCell.DATA_TYPE_DATE);
				row.addDataCell(Datetimes.formatDate(task.getPlannedEndTime()), DataCell.DATA_TYPE_DATE);
			}
			else {
				row.addDataCell(Datetimes.formatDatetime(task.getPlannedStartTime()), DataCell.DATA_TYPE_DATETIME);
				row.addDataCell(Datetimes.formatDatetime(task.getPlannedEndTime()), DataCell.DATA_TYPE_DATETIME);
			}
			
			String managersName = Functions.showOrgEntities(task.getManagers(), V3xOrgEntity.ORGENT_TYPE_MEMBER, 
					TaskUtils.getCommonI18n("common.separator.label"));
			row.addDataCell(managersName, DataCell.DATA_TYPE_TEXT);
			rows[i] = row;
			i ++;
		}
		dataRecord.addDataRow(rows);
		
		return dataRecord;
	}

	public TaskFeedback getFeedback(Long feedbackId) {
		return this.taskFeedbackDao.get(feedbackId);
	}

	public void saveFeedback(TaskFeedback feedback) {
		this.taskFeedbackDao.save(feedback);
	}

	public List<TaskReply> getReplys(Long taskId) {
		List<TaskReply> replys = this.taskReplyDao.getAllReplysByTaskId(taskId);
		if(CollectionUtils.isNotEmpty(replys)) {
			Map<Long, TaskReply> relations = new HashMap<Long, TaskReply>();
			for(TaskReply r : replys) {
				relations.put(r.getId(), r);
			}
			
			for(TaskReply r2 : replys) {
				if(r2.getParentReplyId() != -1l) {
					relations.get(r2.getParentReplyId()).addChild(r2);
				}
			}
		}
		return replys;
	}

	public void updateFeedback(TaskFeedback feedback) {
		this.taskFeedbackDao.update(feedback);
	}

	public String[] saveTaskReply(Long replyerId, Long taskId, Long referenceReplyId, Long referenceReplyerId, String replyContent, boolean sendMsg) {
		String replyId = "-1";
		try {
			TaskInfo task = this.get(taskId);
			if(task == null) {
				return new String[]{String.valueOf(ExceptionMsg.TaskDeleted.ordinal()), replyId};
			}
			else if(!TaskUtils.authCheck(task, replyerId, TaskAclEnum.Reply) && !TaskUtils.isUserPM(task.getProjectId(), replyerId, projectManager)) {
				return new String[]{String.valueOf(ExceptionMsg.NoReplyAuth.ordinal()), replyId};
			}
			
			TaskReply reply = new TaskReply();
			reply.setNewId();
			reply.setCreateUser(replyerId);
			reply.setCreateTime(new Timestamp(System.currentTimeMillis()));
			reply.setParentReplyId(referenceReplyId);
			reply.setTaskId(taskId);
			reply.setContent(URLDecoder.decode(replyContent, "UTF-8"));
			
			this.taskReplyDao.save(reply);
			replyId = reply.getId().toString();
			
			if(sendMsg) {
				if(referenceReplyId != -1l) {
					TaskMsgUtils.sendMsg4ReferenceReply(task, referenceReplyerId, userMessageManager);
				}
				else {
					TaskMsgUtils.sendMsg4Reply(task, userMessageManager);
				}
			}
			
			TaskUtils.updateIndex(task.getId(), updateIndexManager);
		} 
		catch (UnsupportedEncodingException e) {
			return new String[]{String.valueOf(ExceptionMsg.SaveReplyFail.ordinal()), replyId};
		} 
		catch (MessageException e) {
			return new String[]{String.valueOf(ExceptionMsg.SendMsgFail.ordinal()), replyId};
		}
		catch(Exception e) {
			logger.error("保存任务[id=" + taskId + "]回复过程中出现异常:", e);
			return new String[]{String.valueOf(ExceptionMsg.OtherException.ordinal()), replyId};
		}
		
		return new String[]{String.valueOf(ExceptionMsg.None.ordinal()), replyId};
	}
	
	public IndexInfo getIndexInfo(TaskInfo task) throws Exception {
		if(task == null)
			return null;

		Long taskId = task.getId();
		
		IndexInfo info = new IndexInfo();
		info.setAppType(ApplicationCategoryEnum.taskManage);
		info.setEntityID(taskId);
		info.setTitle(task.getSubject());
		info.setContentType(IndexInfo.CONTENTTYPE_HTMLSTR);
		
		TaskInfoBody body = this.getTaskBody(taskId);
		if(body==null){
			info.setContent("");
		}else{
			info.setContent(body.getContent()==null?"":body.getContent());
			info.setContentCreateDate(body.getCreateDate());
		}
		
		info.setStartMemberId(task.getCreateUser());
		info.setAuthor(Functions.showMemberName(task.getCreateUser()));
		info.setCreateDate(task.getCreateTime());
		
		info.setImportantLevel(task.getImportantLevel());
		String status = this.metadataManager.getMetadataItemLabel(MetadataNameEnum.task_status, task.getStatus().toString());
		info.setState(status);
		
		info.setStartTime(task.getPlannedStartTime());
		info.setEndTime(task.getPlannedEndTime());
		info.setHasAttachment(task.isHasAttachments());
		
		List<TaskReply> replys = this.taskReplyDao.getAllReplysByTaskId(taskId);
		if(CollectionUtils.isNotEmpty(replys)) {
			StringBuilder comment = new StringBuilder();
			for(TaskReply reply : replys) {
				comment.append(reply.getContent() + " ");
			}
			info.setComment(comment.toString());
		}
		
		StringBuilder keyWord = new StringBuilder();
		SearchModel sm = new SearchModel();
		sm.setPagination(false);
		List<TaskFeedback> feedbacks = this.getFeedbacks(taskId, sm);
		if(CollectionUtils.isNotEmpty(feedbacks)) {
			for(TaskFeedback fb : feedbacks) {
				keyWord.append(fb.getContent() + " ");
			}
		}
		
		Set<Long> allMembers = TaskUtils.getRoleIds(task, RoleType.values());
		for(Long mId : allMembers) {
			keyWord.append(Functions.showMemberName(mId) + " ");
		}
		info.setKeyword(keyWord.toString());
		
		AuthorizationInfo authInfo = new AuthorizationInfo();
		authInfo.setOwner(task.getAllOwners());
		info.setAuthorizationInfo(authInfo);
		
		IndexUtil.convertToAccessory(info);

		return info;
	}

	/**
	 * 根据任务ID获取对应的全文检索信息，包括任务基本信息、附件和任务回复
	 */
	public IndexInfo getIndexInfo(long taskId) throws Exception {
		TaskInfo task = this.get(taskId);
		return this.getIndexInfo(task);
	}

	public void update(TaskInfo task, CalEventSyncType calEventSyncType) {
		if(CurrentUser.get().getId() == task.getCreateUser()) {
			Map<String, Object> columns = new HashMap<String, Object>();
			columns.put("content", task.getContent());
			columns.put("createDate", new Date(System.currentTimeMillis()));
			this.taskInfoBodyDao.update(task.getId(), columns);
		}
		
		switch(calEventSyncType) {
		case Delete :
			this.calEventManager.deleteCalEventFromTask(task.getId());
			break;
		case Update :
			this.calEventManager.deleteCalEventFromTask(task.getId());
			this.calEventManager.saveCalEventFromTask(task);
			break;
		case Save :
			this.calEventManager.saveCalEventFromTask(task);
			break;
		case None :
			break;
		}
	}
	
	public TaskInfoBody getTaskBody(Long taskId) {
		return this.taskInfoBodyDao.get(taskId);
	}
	
	public boolean checkIfChildExist(String taskIds) {
		List<Long> ids = FormBizConfigUtils.parseStr2Ids(taskIds);
		return this.taskInfoDao.checkIfChildExist(ids);
	}

	public Long getLockEditorId(Long taskId) {
		List<Lock> locks = this.taskManageLock.getLocks(taskId);
		if(CollectionUtils.isNotEmpty(locks)) {
			Lock lock = locks.get(0);
			Long lockerId = lock.getOwner();
			
			V3xOrgMember member = Functions.getMember(lockerId);
			if(member != null && member.isValid()) {
				OnlineUser onlineUser = OnlineRecorder.getOnlineUser(member.getLoginName());
				if(onlineUser != null && onlineUser.getLoginTime().getTime() == lock.getLoginTime())
					return lockerId;
			}
		}
		return null;
	}

	public boolean lockWhenEdit(Long userId, Long taskId) {
		return this.taskManageLock.lock(userId, taskId);
	}
	
	public void unLockAfterEdit(Long taskId) {
		this.taskManageLock.unlock(taskId);
	}

	public List<TaskInfo> getTaskTree(TaskInfo task) {
		return this.taskInfoDao.getTaskTree(task);
	}
	
	public List<TaskInfo> getTasks(StatisticCondition sc) {
		return this.taskInfoDao.getTasks(sc);
	}
	
	public Map<Long, int[]> getStatisticInfo(List<Long> membersList, Date beginDate, Date endDate) {
		return this.taskInfoDao.getStatisticInfo(membersList, beginDate, endDate);
	}
	
	public Map<Long, Object[]> getProjectStatisticResult(StatisticCondition sc) {
		List<Long> memberIds = sc.getMemberIds();
		Map<Long, Object[]> result = this.taskInfoDao.getProjectStatisticResult(sc.getProjectId(), sc.getProjectPhaseId(), 
				sc.getStatus(), memberIds, sc.getBeginDate(), sc.getEndDate());
		if(memberIds != null && memberIds.size() > 1) {
			Object[] sum = this.taskInfoDao.getProjectSumStatisticResult(sc.getProjectId(), sc.getProjectPhaseId(), 
				sc.getStatus(), memberIds, sc.getBeginDate(), sc.getEndDate());
			
			result.put(TaskConstants.STATISTIC_SUM_MEMBERS, sum);
		}
		
		return result;
	}
	
	public String[][] getStatisticInfo(String[] members, String beginDateStr, String endDateStr) {
		Date beginDate = Strings.isNotBlank(beginDateStr) ? Datetimes.parseDate(beginDateStr) : null;
		Date endDate = Strings.isNotBlank(endDateStr) ? Datetimes.parseDate(endDateStr) : null;
		List<Long> memberIds = FormBizConfigUtils.parseStrArr2Ids(members);
		Map<Long, int[]> map = this.taskInfoDao.getStatisticResult(StatisticPeriod.Custom, memberIds, beginDate, endDate);
		
		String[][] result = new String[members.length][4];
		int[] empty = {0, 0, 0};
		int i = 0;
		for(Long memberId : memberIds) {
			int[] count = map.get(memberId) == null ? empty : map.get(memberId);
			
			result[i][0] = memberId.toString();
			result[i][1] = String.valueOf(count[0]);
			result[i][2] = String.valueOf(count[1]);
			result[i][3] = String.valueOf(count[2]);
			
			i++;
		}
		return result;
	}
	
	public ProjectTree getProjectTree(Long userId, Long accountId) throws Exception {
		List<ProjectType> projectTypes = this.projectManager.getProjectTypes(accountId);
		
		Map<Long, ProjectType> map = new HashMap<Long, ProjectType>();
		List<ProjectType> pts = null;
		if(CollectionUtils.isNotEmpty(projectTypes)) {
			pts = new ArrayList<ProjectType>(projectTypes);
			for(ProjectType pt : pts) {
				pt.setProjects(null);
				map.put(pt.getId(), pt);
			}
		}
		
		List<ProjectSummary> projects = this.projectManager.getAllProjects4User(userId, accountId);
		Long selectedProject = -1l;
		Long selectedProjectType = -1l;
		if(CollectionUtils.isNotEmpty(projects)) {
			int i = 0;
			for(ProjectSummary ps : projects) {
				ProjectType pt = map.get(ps.getProjectTypeId());
				if(pt == null) {
					continue;
				}
				
				if( i == 0) {
					selectedProject = ps.getId();
					selectedProjectType = pt.getId();
				}
				
				map.get(ps.getProjectTypeId()).addProject(ps);
				i ++;
			}
		}
		
		return new ProjectTree(selectedProject, selectedProjectType, pts);
	}
	
	/*-----------------------------------setter------------------------------*/
	public void setTaskManageLock(LockManager taskManageLock) {
		this.taskManageLock = taskManageLock;
	}
	public void setTaskInfoBodyDao(TaskInfoBodyDao taskInfoBodyDao) {
		this.taskInfoBodyDao = taskInfoBodyDao;
	}
	public void setUpdateIndexManager(UpdateIndexManager updateIndexManager) {
		this.updateIndexManager = updateIndexManager;
	}
	public void setIndexManager(IndexManager indexManager) {
		this.indexManager = indexManager;
	}
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	public void setUserMessageManager(UserMessageManager userMessageManager) {
		this.userMessageManager = userMessageManager;
	}
	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}
	public void setCalEventManager(CalEventManager calEventManager) {
		this.calEventManager = calEventManager;
	}
	public void setTaskFeedbackDao(TaskFeedbackDao taskFeedbackDao) {
		this.taskFeedbackDao = taskFeedbackDao;
	}
	public void setTaskReplyDao(TaskReplyDao taskReplyDao) {
		this.taskReplyDao = taskReplyDao;
	}
	public void setTaskRoleDao(TaskRoleDao taskRoleDao) {
		this.taskRoleDao = taskRoleDao;
	}
	public void setTaskInfoDao(TaskInfoDao taskInfoDao) {
		this.taskInfoDao = taskInfoDao;
	}
	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}
	public void setAppLogManager(AppLogManager appLogManager) {
		this.appLogManager = appLogManager;
	}
}
