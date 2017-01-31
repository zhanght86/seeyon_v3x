package com.seeyon.v3x.project.listener;

import com.seeyon.v3x.organization.event.AddAccountEvent;
import com.seeyon.v3x.organization.event.DeleteMemberEvent;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.util.annotation.ListenEvent;

public class ProjectOrganizationEventListener {

	private ProjectManager projectManager;

	public ProjectManager getProjectManager() {
		return projectManager;
	}

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	@ListenEvent(event = DeleteMemberEvent.class)
	public void onDeleteMember(DeleteMemberEvent evt) throws Exception {
		projectManager.retakeClew(evt.getMember().getId());
	}

	@ListenEvent(event = AddAccountEvent.class)
	public void onAddAccount(AddAccountEvent evt) throws Exception {
		projectManager.initProjectType(evt.getAccount().getId());
	}

}