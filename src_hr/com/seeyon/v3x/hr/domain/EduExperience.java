package com.seeyon.v3x.hr.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;


/**
 * 
 * <p/> Title:教育培训经历
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Jul 3, 2007
 * </p>
 * 
 * @author Dongjw
 * @version 1.0
 */
public class EduExperience extends BaseModel implements java.io.Serializable {

	private static final long serialVersionUID = -1333370780089113072L;

	/**
	 * 开始时间
	 */
	private Date start_time;
	
	/**
	 * 结束时间
	 */
	private Date end_time;
	
	/**
	 * 所在教育培训单位
	 */	
    private String organization;
    
    /**
	 * 证书名称
	 */	
    private String certificate_name;
    
    /**
	 * 人员id
	 */	
    private Long member_id;


	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public Long getMember_id() {
		return member_id;
	}

	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}

	public String getCertificate_name() {
		return certificate_name;
	}

	public void setCertificate_name(String certificate_name) {
		this.certificate_name = certificate_name;
	}
}
