package com.seeyon.v3x.project.manager.cap;

import java.util.List;

import com.seeyon.cap.project.domain.ProjectSummaryCAP;
import com.seeyon.cap.project.manager.ProjectManagerCAP;
import com.seeyon.cap.project.webmodel.ProjectComposeCAP;
import com.seeyon.v3x.common.utils.BeanUtils;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.manager.ProjectManager;
import com.seeyon.v3x.project.webmodel.ProjectCompose;

public class ProjectManagerCAPImpl implements ProjectManagerCAP {

	private ProjectManager projectManager;

	public void setProjectManager(ProjectManager projectManager) {
		this.projectManager = projectManager;
	}

	@Override
	public ProjectSummaryCAP getProject(long projectId) throws Exception {
		ProjectSummary projectSummary = projectManager.getProject(projectId);
		if (projectSummary == null) {
			return null;
		}
		ProjectSummaryCAP projectSummaryCAP = new ProjectSummaryCAP();
		BeanUtils.convert(projectSummaryCAP, projectSummary);
		return projectSummaryCAP;
	}

	@Override
	public ProjectComposeCAP getProjectComposeByID(long projectId, boolean b) throws Exception {
		ProjectCompose projectCompose = projectManager.getProjectComposeByID(projectId, b);
		if (projectCompose == null) {
			return null;
		}
		ProjectComposeCAP projectComposeCAP = new ProjectComposeCAP();
		BeanUtils.convert(projectComposeCAP, projectCompose);
		return projectComposeCAP;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectSummaryCAP> getProjectList() throws Exception {
		List<ProjectSummary> list = projectManager.getProjectList();
		if (list == null) {
			return null;
		}
		return (List<ProjectSummaryCAP>) BeanUtils.converts(ProjectSummaryCAP.class, list);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectSummaryCAP> getProjects(long domainId) throws Exception {
		List<ProjectSummary> list = projectManager.getProjects(domainId);
		if (list == null) {
			return null;
		}
		return (List<ProjectSummaryCAP>) BeanUtils.converts(ProjectSummaryCAP.class, list);
	}
	
}