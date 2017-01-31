package com.seeyon.v3x.project.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The persistent class for the project_evolution database table.
 * 
 * @author BEA Workshop Studio
 */
public class ProjectType extends com.seeyon.v3x.common.domain.BaseModel
		implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String name; //类型名称
	private String memo; //类型描述
	private Long accountId; // 单位ID
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private List<ProjectSummary> projects;
	public List<ProjectSummary> getProjects() {
		return projects;
	}
	public void setProjects(List<ProjectSummary> projects) {
		this.projects = projects;
	}
	
	public void addProject(ProjectSummary project) {
		if(this.projects == null)
			projects = new ArrayList<ProjectSummary>();
		
		if(!projects.contains(project)) {
			projects.add(project);
		}
	}
	
	
}