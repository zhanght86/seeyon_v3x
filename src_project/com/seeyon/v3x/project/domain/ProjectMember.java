package com.seeyon.v3x.project.domain;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The persistent class for the project_member database table.
 * 
 * @author BEA Workshop Studio
 */
public class ProjectMember extends com.seeyon.v3x.common.domain.BaseModel
		implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	public static final Byte memberType_manager = 0;// 负责人

	public static final Byte memberType_charge = 1;// 领导

	public static final Byte memberType_member = 2;// 成员

	public static final Byte memberType_interfix = 3;// 相关人员
	
	public static final Byte memberType_create = 4;// 项目发起人
	
	public static final Byte memberType_assistant = 5;//项目助理

	private Byte memberType;

	private long memberid;

	private ProjectSummary projectSummary;
	
	private int memberSort =0;
	
	private int userProjectSort=0;
	
	public int getUserProjectSort()
	{
		return this.userProjectSort; 
	}
	public void setUserProjectSort(int userProjectSort)
	{
		this.userProjectSort=userProjectSort;
	}

	public ProjectMember() {
	}



	public int getMemberSort() {
		return memberSort;
	}



	public void setMemberSort(int memberSort) {
		this.memberSort = memberSort;
	}



	public Byte getMemberType() {
		return this.memberType;
	}

	public void setMemberType(Byte memberType) {
		this.memberType = memberType;
	}

	public long getMemberid() {
		return this.memberid;
	}

	public void setMemberid(long memberid) {
		this.memberid = memberid;
	}

	public ProjectSummary getProjectSummary() {
		return projectSummary;
	}



	public void setProjectSummary(ProjectSummary projectSummary) {
		this.projectSummary = projectSummary;
	}



	public String toString() {
		return new ToStringBuilder(this).append("id", getId()).toString();
	}
}