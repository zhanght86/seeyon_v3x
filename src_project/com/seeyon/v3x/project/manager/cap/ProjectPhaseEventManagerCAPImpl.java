package com.seeyon.v3x.project.manager.cap;

import com.seeyon.cap.project.domain.ProjectPhaseEventCAP;
import com.seeyon.cap.project.manager.ProjectPhaseEventManagerCAP;
import com.seeyon.v3x.common.utils.BeanUtils;
import com.seeyon.v3x.project.domain.ProjectPhaseEvent;
import com.seeyon.v3x.project.manager.ProjectPhaseEventManager;

public class ProjectPhaseEventManagerCAPImpl implements ProjectPhaseEventManagerCAP {

	private ProjectPhaseEventManager projectPhaseEventManager;

	public void setProjectPhaseEventManager(ProjectPhaseEventManager projectPhaseEventManager) {
		this.projectPhaseEventManager = projectPhaseEventManager;
	}

	@Override
	public void save(ProjectPhaseEventCAP projectPhaseEvent) throws Exception {
		ProjectPhaseEvent phaseEvent = new ProjectPhaseEvent();
		BeanUtils.convert(phaseEvent, projectPhaseEvent);
		projectPhaseEventManager.save(phaseEvent);
	}

}