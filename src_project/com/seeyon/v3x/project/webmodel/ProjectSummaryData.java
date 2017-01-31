package com.seeyon.v3x.project.webmodel;

import java.util.Date;
import java.util.Set;

import com.seeyon.v3x.common.exceptions.BusinessException;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.project.domain.ProjectMember;
import com.seeyon.v3x.project.domain.ProjectSummary;

/**
 * 知识管理模块的项目文档夹管理使用
 * 因为配置文件的原因，需要将ProjectSummary转换成ProjectData以方便取值
 */
public class ProjectSummaryData {
	private String projectName;
	private String projectType;
	private Date beginDate;
	private Date endDate;
	// lihf: 2007.08.29 不存id串，存name串
//	// 项目负责人id串
//	private String managerIds;
//	// 项目成员id串
//	private String memberIds;
//	// 关联人员id串
//	private String interfixIds;
//	// 主管领导id串
//	private String chargeIds;
//	private String status;
	private Long projectId;
	
	private Long managerId;

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public ProjectSummaryData(ProjectSummary summary, OrgManager orgManager) {
		this.projectId = summary.getId();
		this.projectName = summary.getProjectName();
		this.projectType = summary.getProjectTypeName();
		this.beginDate = summary.getBegintime();
		this.endDate = summary.getClosetime();
//		this.status = "project.body.projectstate." + summary.getProjectState().byteValue();
//		String _managerIds = "";
//		String _memberIds = "";
//		String _interfixIds = "";
//		String _chargeIds = "";
		Set<ProjectMember> users = summary.getProjectMembers();
		for(ProjectMember u:users) {
			byte type = u.getMemberType();
//			String name = "";
//			try {
////				V3xOrgMember member = orgManager.getMemberById(u.getMemberid());			
////				if(member != null)
////					name = member.getName();
//			} catch (BusinessException e) {
////				e.printStackTrace();
//			}
			if(type == ProjectMember.memberType_manager){
				this.managerId = u.getMemberid();
				break;
			}
//				_managerIds = _managerIds + "," + name;
//			else if(type == ProjectMember.memberType_interfix)
//				_interfixIds = _interfixIds + "," + name;
//			else if(type == ProjectMember.memberType_charge)
//				_chargeIds = _chargeIds + "," + name;				
//			else if(type == ProjectMember.memberType_member)
//				_memberIds = _memberIds + "," + name;
		}
//		if(_managerIds.length() > 0)
//			this.managerIds = _managerIds.substring(1, _managerIds.length());
//		if(_memberIds.length() > 0)
//			this.memberIds = _memberIds.substring(1, _memberIds.length());
//		if(_interfixIds.length() > 0)
//			this.interfixIds = _interfixIds.substring(1, _interfixIds.length());
//		if(_chargeIds.length() > 0)
//			this.chargeIds = _chargeIds.substring(1, _chargeIds.length());
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

//	public String getChargeIds() {
//		return chargeIds;
//	}
//
//	public void setChargeIds(String chargeIds) {
//		this.chargeIds = chargeIds;
//	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

//	public String getInterfixIds() {
//		return interfixIds;
//	}
//
//	public void setInterfixIds(String interfixIds) {
//		this.interfixIds = interfixIds;
//	}
//
//	public String getManagerIds() {
//		return managerIds;
//	}
//
//	public void setManagerIds(String managerIds) {
//		this.managerIds = managerIds;
//	}
//
//	public String getMemberIds() {
//		return memberIds;
//	}
//
//	public void setMemberIds(String memberIds) {
//		this.memberIds = memberIds;
//	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

//	public String getStatus() {
//		return status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}

}
