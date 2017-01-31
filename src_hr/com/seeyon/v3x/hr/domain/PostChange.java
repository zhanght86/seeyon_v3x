package com.seeyon.v3x.hr.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * <p/> Title:职务变动
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Jul 3, 2007
 * </p>
 * 
 * @author Dongjw
 * @version 1.0
 */
public class PostChange extends BaseModel implements java.io.Serializable {
	
	private static final long serialVersionUID = 5388639072787156625L;

	/**
	 * 任职开始时间
	 */
	private Date start_time;
	
	/**
	 * 任职结束时间
	 */
	private Date end_time;
		
	/**
	 * 职务名称
	 */	
    private String post_name;
    
    /**
	 * 批准单位
	 */	
    private String organization;
    
    /**
	 * 任职文号
	 */	
    private String wordnumber;
    
    /**
	 * 人员id
	 */	
    private Long member_id;

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public Long getMember_id() {
		return member_id;
	}

	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}

	public Date getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}

	public String getPost_name() {
		return post_name;
	}

	public void setPost_name(String post_name) {
		this.post_name = post_name;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}

	public String getWordnumber() {
		return wordnumber;
	}

	public void setWordnumber(String wordnumber) {
		this.wordnumber = wordnumber;
	}

}
