package com.seeyon.v3x.collaboration.domain;

import java.io.Serializable;

import www.seeyon.com.v3x.form.base.SelectPersonOperation;

import com.seeyon.v3x.common.domain.BaseModel;

public class ColRelationAuthority extends BaseModel implements Serializable{
	private static final long serialVersionUID = 1L;

	private Long summaryId;
	private int usertype;
	private Long userid;
	
	public Long getSummaryId() {
		return summaryId;
	}

	public void setSummaryId(Long summaryId) {
		this.summaryId = summaryId;
	}

	public Long getUserid() {
		return userid;
	}
	
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	
	public int getUsertype() {
		return usertype;
	}
	
	public void setUsertype(int usertype) {
		this.usertype = usertype;
	}
	
	public String getUserType(){
		SelectPersonOperation selectPersonOperation = new SelectPersonOperation() ;
		return selectPersonOperation.getTypeByTypeId(usertype) ;
	}
	
}
