package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * <p/> Title:联系信息
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Jul 28, 2007
 * </p>
 * 
 * @author Dongjw
 * @version 1.0
 */

public class ContactInfo extends BaseModel implements java.io.Serializable{
	
	private static final long serialVersionUID = 1330254930350008335L;

	/**
	 * 电话号码
	 */	
    private String telephone;
    
    /**
	 * 电子邮件地址
	 */	
    private String email;   
    
    /**
	 * 即时通讯地址
	 */	
    private String communication;
    
    /**
	 * 博客
	 */	
    private String blog;  
    
    /**
	 * 个人网页
	 */	
    private String website;
    
    /**
	 * 邮编
	 */	
    private String postalcode;  
    
    /**
	 * 家庭住址
	 */	
    private String address;
    
    /**
	 * 人员id
	 */	
    private Long member_id;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBlog() {
		return blog;
	}

	public void setBlog(String blog) {
		this.blog = blog;
	}

	public String getCommunication() {
		return communication;
	}

	public void setCommunication(String communication) {
		this.communication = communication;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getMember_id() {
		return member_id;
	}

	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}

	public String getPostalcode() {
		return postalcode;
	}

	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

}
