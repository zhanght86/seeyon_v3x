package com.seeyon.v3x.hr.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 
 * <p/> Title:奖惩档案
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Jul 3, 2007
 * </p>
 * 
 * @author Dongjw
 * @version 1.0
 */
public class RewardsAndPunishment  extends BaseModel implements java.io.Serializable {

	private static final long serialVersionUID = -2650181549855388728L;

	/**
	 * 时间
	 */
	private Date time;
	
	/**
	 * 类型
	 */
	private int type;
	
	/**
	 * 奖惩事由
	 */	
    private String reason;
    
    /**
	 * 奖惩办法
	 */	
    private String content;
    
    /**
	 * 奖惩机构
	 */	
    private String organization;
    
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
    
}
