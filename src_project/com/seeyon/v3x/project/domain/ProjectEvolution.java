package com.seeyon.v3x.project.domain;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The persistent class for the project_evolution database table.
 * 
 * @author BEA Workshop Studio
 */
public class ProjectEvolution extends com.seeyon.v3x.common.domain.BaseModel
		implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private Float evolutionPercent;

	private Byte evolutionState;

	private Date evolutionDate;

	private String evolutionDesc;
	
	private ProjectPhase projectPhase;
	
	private long userId;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getEvolutionDesc() {
		return evolutionDesc;
	}

	public void setEvolutionDesc(String evolutionDesc) {
		this.evolutionDesc = evolutionDesc;
	}

	public Date getEvolutionDate() {
		return evolutionDate;
	}

	public void setEvolutionDate(Date evolutionDate) {
		this.evolutionDate = evolutionDate;
	}

	public ProjectEvolution() {
	}

	public Float getEvolutionPercent() {
		return this.evolutionPercent;
	}

	public void setEvolutionPercent(Float evolutionPercent) {
		this.evolutionPercent = evolutionPercent;
	}

	public Byte getEvolutionState() {
		return evolutionState;
	}

	public void setEvolutionState(Byte evolutionState) {
		this.evolutionState = evolutionState;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

	public ProjectPhase getProjectPhase() {
		return projectPhase;
	}

	public void setProjectPhase(ProjectPhase projectPhase) {
		this.projectPhase = projectPhase;
	}
	
	
}