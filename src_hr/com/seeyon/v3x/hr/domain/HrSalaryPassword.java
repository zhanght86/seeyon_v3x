package com.seeyon.v3x.hr.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

public class HrSalaryPassword extends BaseModel implements java.io.Serializable{
	private static final long serialVersionUID = -1370801708654933773L;
	
	private String salaryPassword ;
	
	private Date updateDate ;
	private Date createDate ;
	private Long userId ;
	
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getSalaryPassword() {
		return salaryPassword;
	}
	public void setSalaryPassword(String salaryPassword) {
		this.salaryPassword = salaryPassword;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
