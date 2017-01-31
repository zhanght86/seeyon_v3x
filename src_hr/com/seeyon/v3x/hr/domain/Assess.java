package com.seeyon.v3x.hr.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * <p/> Title:考核情况
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Jun 8, 2007
 * </p>
 * 
 * @author Dongjw
 * @version 1.0
 */
public class Assess extends BaseModel implements java.io.Serializable {
	private static final long serialVersionUID = -7785173971041325482L;

	/**
	 * 考核开始日期
	 */
	private Date begin_date;
	
	/**
	 * 考核结束日期
	 */
	private Date end_date;
	
	/**
	 * 考核名称
	 */	
    private String assess_name;
    
    /**
	 * 考核单位
	 */	
    private String organization;   
    
    /**
	 * 考核结果
	 */	
    private String assess_result; 
    
    /**
	 * 考核内容
	 */	
    private String assess_content; 
    
    /**
	 * 人员id
	 */	
    private Long member_id;

	public String getAssess_name() {
		return assess_name;
	}

	public void setAssess_name(String assess_name) {
		this.assess_name = assess_name;
	}

	public Long getMember_id() {
		return member_id;
	}

	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public Date getBegin_date() {
		return begin_date;
	}

	public void setBegin_date(Date begin_date) {
		this.begin_date = begin_date;
	}

	public Date getEnd_date() {
		return end_date;
	}

	public void setEnd_date(Date end_date) {
		this.end_date = end_date;
	}

	public String getAssess_content() {
		return assess_content;
	}

	public void setAssess_content(String assess_content) {
		this.assess_content = assess_content;
	}

	public String getAssess_result() {
		return assess_result;
	}

	public void setAssess_result(String assess_result) {
		this.assess_result = assess_result;
	}

}
