package com.seeyon.v3x.hr.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * <p/> Title:工作履历
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Jul 3, 2007
 * </p>
 * 
 * @author Dongjw
 * @version 1.0
 */
public class WorkRecord extends BaseModel implements java.io.Serializable {
    
	private static final long serialVersionUID = -2946593579770999575L;

	/**
	 * 开始时间
	 */
	private Date start_time;
    
	/**
	 * 结束时间
	 */
	private Date end_time;
	
	/**
	 * 所在单位
	 */	
    private String organization;
    
    /**
	 * 所在部门
	 */	
    private String department;    
    
    /**
	 * 职务层次
	 */	
    private String level;
    
    /**
	 * 岗位
	 */	
    private String post;
    
    /**
	 * 证明人
	 */	
    private String reference;
    
    
    /**
	 * 人员id
	 */	
    private Long member_id;

	public Long getMember_id() {
		return member_id;
	}

	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}


	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}


}
