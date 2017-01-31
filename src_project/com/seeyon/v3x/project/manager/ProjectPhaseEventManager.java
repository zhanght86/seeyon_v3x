/**
 * 
 */
package com.seeyon.v3x.project.manager;

import java.util.List;

import com.seeyon.v3x.project.domain.ProjectPhaseEvent;

public interface ProjectPhaseEventManager {
	
	/**
	 * 保存项目阶段事件
	 * @param projectPhaseEvent
	 * @throws Exception
	 */
	public void save(ProjectPhaseEvent projectPhaseEvent) throws Exception;

	/**
	 * 查询项目阶段下所有事件记录列表
	 * @param phaseId 项目阶段ID
	 * @return
	 * @throws Exception
	 */
	public List<ProjectPhaseEvent> getAllPhaseEventList(Long phaseId) throws Exception;

}
