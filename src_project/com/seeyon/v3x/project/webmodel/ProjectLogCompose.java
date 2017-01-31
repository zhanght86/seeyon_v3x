/**
 * 
 */
package com.seeyon.v3x.project.webmodel;

import java.util.List;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.project.domain.ProjectLog;


/**
 * @author tian lin
 * 
 */
public class ProjectLogCompose {
	private ProjectLog projectLog;
	
	private V3xOrgMember optionUser;//操作人
	
	//private ProjectSummary projectSummary;//

	private V3xOrgMember addManager;// 增加项目负责人

	private V3xOrgMember deleteManager;// 删除项目负责人

	private List<V3xOrgMember> addCharge;// 增加项目领导

	private List<V3xOrgMember> deleteCharge;// 删除项目领导

	private List<V3xOrgMember> addMember;// 增加项目成员

	private List<V3xOrgMember> deleteMember;// 删除项目成员

	private List<V3xOrgMember> addInterfix;// 增加项目相关人员

	private List<V3xOrgMember> deleteInterfix;// 删除项目相关人员

	public List<V3xOrgMember> getAddCharge() {
		return addCharge;
	}

	public void setAddCharge(List<V3xOrgMember> addCharge) {
		this.addCharge = addCharge;
	}

	public List<V3xOrgMember> getAddInterfix() {
		return addInterfix;
	}

	public void setAddInterfix(List<V3xOrgMember> addInterfix) {
		this.addInterfix = addInterfix;
	}

	public V3xOrgMember getAddManager() {
		return addManager;
	}

	public void setAddManager(V3xOrgMember addManager) {
		this.addManager = addManager;
	}

	public List<V3xOrgMember> getAddMember() {
		return addMember;
	}

	public void setAddMember(List<V3xOrgMember> addMember) {
		this.addMember = addMember;
	}

	public List<V3xOrgMember> getDeleteCharge() {
		return deleteCharge;
	}

	public void setDeleteCharge(List<V3xOrgMember> deleteCharge) {
		this.deleteCharge = deleteCharge;
	}

	public List<V3xOrgMember> getDeleteInterfix() {
		return deleteInterfix;
	}

	public void setDeleteInterfix(List<V3xOrgMember> deleteInterfix) {
		this.deleteInterfix = deleteInterfix;
	}

	public V3xOrgMember getDeleteManager() {
		return deleteManager;
	}

	public void setDeleteManager(V3xOrgMember deleteManager) {
		this.deleteManager = deleteManager;
	}

	public List<V3xOrgMember> getDeleteMember() {
		return deleteMember;
	}

	public void setDeleteMember(List<V3xOrgMember> deleteMember) {
		this.deleteMember = deleteMember;
	}

	public ProjectLog getProjectLog() {
		return projectLog;
	}

	public void setProjectLog(ProjectLog projectLog) {
		this.projectLog = projectLog;
	}

	public V3xOrgMember getOptionUser() {
		return optionUser;
	}

	public void setOptionUser(V3xOrgMember optionUser) {
		this.optionUser = optionUser;
	}

//	public ProjectSummary getProjectSummary() {
//		return projectSummary;
//	}
//
//	public void setProjectSummary(ProjectSummary projectSummary) {
//		this.projectSummary = projectSummary;
//	}	
}
