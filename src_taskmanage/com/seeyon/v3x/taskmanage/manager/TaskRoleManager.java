package com.seeyon.v3x.taskmanage.manager;

import java.util.List;

import com.seeyon.v3x.taskmanage.domain.TaskFeedback;
import com.seeyon.v3x.taskmanage.domain.TaskInfo;
import com.seeyon.v3x.taskmanage.domain.TaskRole;

/**
 * 任务角色业务逻辑接口
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2011-1-27
 */
public interface TaskRoleManager {
	
	/**
	 * 保存任务对应的角色信息，包括创建人、负责人、参与人和查看人
	 * @param task	任务信息
	 */
	public void saveTaskRoles(TaskInfo task);

	/**
	 * 保存任务对应的角色信息，包括创建人、负责人、参与人和查看人
	 * @param taskId	任务ID
	 * @param key		角色类型
	 * @param roleIds	角色人员ID集合
	 */
	public void saveRoles(Long taskId, Integer key, List<Long> roleIds);
	
	/**
	 * 更新任务对应的角色信息，包括创建人、负责人、参与人和查看人
	 * @param task	任务信息
	 */
	public void updateTaskRoles(TaskInfo task);
	
	/**
	 * 保存任务角色
	 * @param role	任务角色
	 */
	public void save(TaskRole role);
	
	/**
     * 保存
     * @param taskFeedback  任务
     */
    public void save(TaskFeedback taskFeedback);

}
