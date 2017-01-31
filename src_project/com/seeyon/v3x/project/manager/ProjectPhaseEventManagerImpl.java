/**
 * 
 */
package com.seeyon.v3x.project.manager;

import java.util.List;

import com.seeyon.v3x.project.dao.ProjectPhaseEventDao;
import com.seeyon.v3x.project.domain.ProjectPhaseEvent;

public class ProjectPhaseEventManagerImpl implements ProjectPhaseEventManager {
	
	private ProjectPhaseEventDao projectPhaseEventDao;
	
	public ProjectPhaseEventDao getProjectPhaseEventDao() {
		return projectPhaseEventDao;
	}

	public void setProjectPhaseEventDao(ProjectPhaseEventDao projectPhaseEventDao) {
		this.projectPhaseEventDao = projectPhaseEventDao;
	}

	public void save(ProjectPhaseEvent projectPhaseEvent) throws Exception {
		this.projectPhaseEventDao.save(projectPhaseEvent);
	}

	public List<ProjectPhaseEvent> getAllPhaseEventList(Long phaseId) throws Exception {
		return this.projectPhaseEventDao.getAllPhaseEventList(phaseId);
	}

}
