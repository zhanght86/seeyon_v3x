/**
 * 
 */
package com.seeyon.v3x.project.webmodel;

import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.project.domain.ProjectEvolution;

/**
 * @author Administrator
 * 
 */
public class EvolutionUserCompose {
	private ProjectEvolution projectEvolution;

	private V3xOrgMember member;

	public V3xOrgMember getMember() {
		return member;
	}

	public void setMember(V3xOrgMember member) {
		this.member = member;
	}

	public ProjectEvolution getProjectEvolution() {
		return projectEvolution;
	}

	public void setProjectEvolution(ProjectEvolution projectEvolution) {
		this.projectEvolution = projectEvolution;
	}

}
