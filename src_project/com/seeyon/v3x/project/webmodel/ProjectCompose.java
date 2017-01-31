/**
 * 
 */
package com.seeyon.v3x.project.webmodel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.project.domain.ProjectSummary;
import com.seeyon.v3x.project.util.ProjectConstants;

/**
 * @author lin tian 2007-5-15
 */
public class ProjectCompose {

	public static final String assistant = "assistant";

	public static final String charge = "charge";

	public static final String member = "member";

	public static final String interfix = "interfix";

	private ProjectSummary projectSummary;

	private V3xOrgDepartment deparment;//所属部门

	private List<EvolutionUserCompose> composeList;//进展列表

	private List<V3xOrgMember> principalLists = new ArrayList<V3xOrgMember>();//负责人

	private List<V3xOrgMember> assistantLists = new ArrayList<V3xOrgMember>();//助理

	private List<V3xOrgMember> chargeLists = new ArrayList<V3xOrgMember>();//主管领导

	private List<V3xOrgMember> memberLists = new ArrayList<V3xOrgMember>();//成员列表

	private List<V3xOrgMember> interfixLists = new ArrayList<V3xOrgMember>();//相关人员列表
	
	public List<V3xOrgMember> getAllProjectMembers(String... memberTypes) {
		List<V3xOrgMember> memberList = new ArrayList<V3xOrgMember>();
		for(String memberType : memberTypes){
			if(ProjectConstants.MEMBERTYPE_PRINCIPAL.equals(memberType)){
				if(CollectionUtils.isNotEmpty(principalLists)){
					memberList.addAll(principalLists);
				}
				continue;
			}
			if(ProjectConstants.MEMBERTYPE_ASSISTANT.equals(memberType)){
				if(CollectionUtils.isNotEmpty(assistantLists)){
					memberList.addAll(assistantLists);
				}
				continue;
			}
			if(ProjectConstants.MEMBERTYPE_MEMBER.equals(memberType)){
				if(CollectionUtils.isNotEmpty(memberList)){
					memberList.addAll(memberLists);
				}
				continue;
			}
			if(ProjectConstants.MEMBERTYPE_CHARGE.equals(memberType)){
				if(CollectionUtils.isNotEmpty(chargeLists)){
					memberList.addAll(chargeLists);
				}
				continue;
			}
			if(ProjectConstants.MEMBERTYPE_INTERFIX.equals(memberType)){
				if(CollectionUtils.isNotEmpty(interfixLists)){
					memberList.addAll(interfixLists);
				}
				continue;
			}
		}
		return memberList;
	}

	public V3xOrgDepartment getDeparment() {
		return deparment;
	}

	public void setDeparment(V3xOrgDepartment deparment) {
		this.deparment = deparment;
	}

	public List<V3xOrgMember> getInterfixLists() {
		return interfixLists;
	}

	public void setInterfixLists(List<V3xOrgMember> interfixLists) {
		this.interfixLists = interfixLists;
	}

	public List<V3xOrgMember> getChargeLists() {
		return chargeLists;
	}

	public void setChargeLists(List<V3xOrgMember> chargeLists) {
		this.chargeLists = chargeLists;
	}

	public List<V3xOrgMember> getMemberLists() {
		return memberLists;
	}

	public void setMemberLists(List<V3xOrgMember> memberLists) {
		this.memberLists = memberLists;
	}

	public List<V3xOrgMember> getPrincipalLists() {
		return principalLists;
	}

	public void setPrincipalLists(List<V3xOrgMember> principalLists) {
		this.principalLists = principalLists;
	}

	public List<V3xOrgMember> getAssistantLists() {
		return assistantLists;
	}

	public void setAssistantLists(List<V3xOrgMember> assistantLists) {
		this.assistantLists = assistantLists;
	}

	public ProjectSummary getProjectSummary() {
		return projectSummary;
	}

	public void setProjectSummary(ProjectSummary projectSummary) {
		this.projectSummary = projectSummary;
	}

	public List<EvolutionUserCompose> getComposeList() {
		return composeList;
	}

	public void setComposeList(List<EvolutionUserCompose> composeList) {
		this.composeList = composeList;
	}

}
