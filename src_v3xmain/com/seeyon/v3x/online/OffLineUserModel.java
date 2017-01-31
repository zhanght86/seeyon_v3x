package com.seeyon.v3x.online;

import java.io.Serializable;

import com.seeyon.v3x.common.domain.BaseModel;
import com.seeyon.v3x.organization.domain.V3xOrgDepartment;
import com.seeyon.v3x.organization.domain.V3xOrgMember;
import com.seeyon.v3x.organization.domain.V3xOrgPost;

public class OffLineUserModel extends BaseModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1865680433155881293L;

	private Long id;
    private String name;
    private String departmentName;
    private String postName;
    private Long loginAccountId ;
    
    public OffLineUserModel() {
		// TODO Auto-generated constructor stub
	}
    
    public OffLineUserModel(V3xOrgMember member,V3xOrgPost post,V3xOrgDepartment dept){
    	this.id = member.getId();
    	this.name = member.getName();
    	this.postName = post.getName();
    	this.departmentName = dept.getName();
    	this.loginAccountId = dept.getOrgAccountId();
    }
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getPostName() {
		return postName;
	}
	public void setPostName(String postName) {
		this.postName = postName;
	}
	public Long getLoginAccountId() {
		return loginAccountId;
	}
	public void setLoginAccountId(Long loginAccountId) {
		this.loginAccountId = loginAccountId;
	}
}
