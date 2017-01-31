/**
 * 
 */
package com.seeyon.v3x.project.manager;

import com.seeyon.v3x.project.domain.ProjectPhase;

public interface ProjectPhaseManager {

	/**
	 * 根据id获取项目阶段信息
	 */
	public ProjectPhase getById(Long phaseId) throws Exception;

}
