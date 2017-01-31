package com.seeyon.v3x.hr.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * <p/> Title:家庭成员以及社会关系
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Jul 3, 2007
 * </p>
 * 
 * @author Dongjw
 * @version 1.0
 */
public class Relationship extends BaseModel implements java.io.Serializable {
		
	private static final long serialVersionUID = 7763903921325236630L;

	/**
	 * 与本人的关系
	 */	
    private String relationship;
    
    /**
	 * 姓名
	 */	
    private String name;
    
    /**
	 * 工作单位
	 */	
    private String organization;
    
    /**
	 * 职务
	 */	
    private String post;
    
	/**
	 * 出生日期
	 */
	private Date birthday;
	
	
	/**
	 * 政治面貌
	 */
	private int political_position;
	
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

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public int getPolitical_position() {
		return political_position;
	}

	public void setPolitical_position(int political_position) {
		this.political_position = political_position;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}



}
