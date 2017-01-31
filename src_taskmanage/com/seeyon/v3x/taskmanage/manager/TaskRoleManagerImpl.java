package com.seeyon.v3x.taskmanage.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.seeyon.v3x.taskmanage.dao.TaskFeedbackDao;
import com.seeyon.v3x.taskmanage.dao.TaskRoleDao;
import com.seeyon.v3x.taskmanage.domain.TaskFeedback;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.taskmanage.domain.TaskRole;
import com.seeyon.v3x.taskmanage.utils.TaskUtils;

/**
 * 任务角色业务逻辑实现
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-27
 */
public class TaskRoleManagerImpl implements TaskRoleManager {
	private TaskRoleDao taskRoleDao;
	private TaskFeedbackDao taskFeedbackDao;
	public void setTaskRoleDao(TaskRoleDao taskRoleDao) {
		this.taskRoleDao = taskRoleDao;
	}
	
    public void setTaskFeedbackDao(TaskFeedbackDao taskFeedbackDao) {
        this.taskFeedbackDao = taskFeedbackDao;
    }

    public void save(TaskFeedback taskFeedback) {
        this.taskFeedbackDao.save(taskFeedback);
    }
	
	public void save(TaskRole taskRole) {
		this.taskRoleDao.save(taskRole);
	}

	public void saveRoles(Long taskId, Integer key, List<Long> roleIds) {
		if(CollectionUtils.isNotEmpty(roleIds)) {
			List<TaskRole> roles = new ArrayList<TaskRole>(roleIds.size());
			for(Long memberId : roleIds) {
				TaskRole manager = new TaskRole(memberId, key, taskId);
				roles.add(manager);
			}
			this.taskRoleDao.savePatchAll(roles);
		}
	}
	
	public void saveTaskRoles(TaskInfo task) {
		Long taskId = task.getId();
		for(TaskRole.RoleType roleType : TaskRole.RoleType.values()) {
			List<Long> roleIds = TaskUtils.getRoleIds(task, roleType);
			this.saveRoles(taskId, roleType.key(), roleIds);
		}
	}
	
	public void updateTaskRoles(TaskInfo task) {
		this.taskRoleDao.delete(new Object[][]{{"taskId", task.getId()}});
		this.saveTaskRoles(task);
	}
	
}
