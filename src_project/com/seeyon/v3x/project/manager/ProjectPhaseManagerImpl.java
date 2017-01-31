/**
 * 
 */
package com.seeyon.v3x.project.manager;

import com.seeyon.v3x.project.dao.ProjectPhaseDao;
import com.seeyon.v3x.project.domain.ProjectPhase;

public class ProjectPhaseManagerImpl implements ProjectPhaseManager {

	private ProjectPhaseDao projectPhaseDao;

	public ProjectPhaseDao getProjectPhaseDao() {
		return projectPhaseDao;
	}

	public void setProjectPhaseDao(ProjectPhaseDao projectPhaseDao) {
		this.projectPhaseDao = projectPhaseDao;
	}

	public ProjectPhase getById(Long phaseId) throws Exception {
		return this.projectPhaseDao.get(phaseId);
	}

}
