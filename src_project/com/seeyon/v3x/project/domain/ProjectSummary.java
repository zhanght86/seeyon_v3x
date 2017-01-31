package com.seeyon.v3x.project.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The persistent class for the project_summary database table.
 * 
 * @author BEA Workshop Studio
 */
public class ProjectSummary extends com.seeyon.v3x.common.domain.BaseModel
		implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	public static final Byte state_begin = 0; //状态：开始

	public static final Byte state_option = 1;//状态：进行中

	public static final Byte state_close = 2;//状态：结束

	public static final Byte state_stop = 3;//状态：终止
	
	public static final Byte state_delete = 4;//状态：删除
	
	public static final Byte state_create = -1;//状态：创建阶段   原值是5  后改为-1  优化查询
	
	public static final Byte state_poublic = 0;//公开
	
	public static final Byte state_closed  = 1;//不公开
	
	private Date begintime;

	private Date closetime;

	private long projectCreator;
	
	private long projectManager = 0;//废弃,扩展为可以有多个负责人

	private String projectDesc;

	private String projectName;

	private Byte projectState;

	private String projectTypeName;//项目类型名称
	
	private long projectTypeId;

	private long  department;//发布部门
	
	private Byte publicState ; //公开状态 ：0：公开 1：不公开
	
	public long domainId; //项目所属单位id
	
	public long phaseId;//项目阶段id
	
	private Float projectProcess = 0f;//项目进度

	private java.util.Set<ProjectMember> projectMembers;

	private java.util.Set<ProjectPhase> projectPhases;
	
	private String templates;//项目模板
	
	/**
	 * 校验当前项目是否有效、允许查看，当前只允许查看仍存在、未结束的项目
	 */
	public boolean isValid() {
		return projectState < ProjectSummary.state_close;
	}
	

	public ProjectSummary() {
	}
    
	public long getDepartment() {
		return department;
	}

	public void setDepartment(long department) {
		this.department = department;
	}

	public Byte getPublicState() {
		return publicState;
	}

	public void setPublicState(Byte publicState) {
		this.publicState = publicState;
	}

	/**
	 * @return the begintime
	 */
	public Date getBegintime() {
		return begintime;
	}

	/**
	 * @param begintime
	 *            the begintime to set
	 */
	public void setBegintime(Date begintime) {
		this.begintime = begintime;
	}

	/**
	 * @return the closetime
	 */
	public Date getClosetime() {
		return closetime;
	}

	/**
	 * @param closetime
	 *            the closetime to set
	 */
	public void setClosetime(Date closetime) {
		this.closetime = closetime;
	}

	public long getProjectCreator() {
		return this.projectCreator;
	}

	public void setProjectCreator(long projectCreator) {
		this.projectCreator = projectCreator;
	}

	public String getProjectDesc() {
		return this.projectDesc;
	}

	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}

	public String getProjectName() {
		return this.projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Byte getProjectState() {
		return this.projectState;
	}

	public void setProjectState(Byte projectState) {
		this.projectState = projectState;
	}


	public long getDomainId() {
		return domainId;
	}
	
	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}
    
	/**
	 * @return the projectManager
	 */
	public long getProjectManager() {
		return projectManager;
	}

	/**
	 * @param projectManager the projectManager to set
	 */
	public void setProjectManager(long projectManager) {
		this.projectManager = projectManager;
	}

	// uni-directional many-to-one association to ProjectMember
	public java.util.Set<ProjectMember> getProjectMembers() {
		return this.projectMembers;
	}

	public void setProjectMembers(java.util.Set<ProjectMember> projectMembers) {
		this.projectMembers = projectMembers;
	}

	// uni-directional many-to-one association to ProjectPhase
	public java.util.Set<ProjectPhase> getProjectPhases() {
		return this.projectPhases;
	}

	public void setProjectPhases(java.util.Set<ProjectPhase> projectPhases) {
		this.projectPhases = projectPhases;
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}

	public long getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(long phaseId) {
		this.phaseId = phaseId;
	}


    public String getProjectTypeName()
    {
        return projectTypeName;
    }

    public void setProjectTypeName(String projectTypeName)
    {
        this.projectTypeName = projectTypeName;
    }
	public float getProjectProcess() {
		return projectProcess;
	}
	public void setProjectProcess(Float projectProcess) {
		this.projectProcess = projectProcess == null ? 0f : projectProcess;
	}
	public String getTemplates() {
		return templates;
	}
	public void setTemplates(String templates) {
		this.templates = templates;
	}
	public long getProjectTypeId() {
		return projectTypeId;
	}
	public void setProjectTypeId(long projectTypeId) {
		this.projectTypeId = projectTypeId;
	}
	
	private List<Long> oldMemberIds;
	public List<Long> getOldMemberIds() {
		return oldMemberIds;
	}
	public void setOldMemberIds(List<Long> oldMemberIds) {
		this.oldMemberIds = oldMemberIds;
	}
	
}