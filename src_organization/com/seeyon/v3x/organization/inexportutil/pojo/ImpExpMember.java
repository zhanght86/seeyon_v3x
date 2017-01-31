package com.seeyon.v3x.organization.inexportutil.pojo;

import com.seeyon.v3x.organization.domain.V3xOrgEntity;

public class ImpExpMember extends ImpExpPojo {
	String code;
	
	String accountName;
	
	String loginName;
	
	String dept;
	
	String ppost;
	
	String level;
	
	String eMail=V3xOrgEntity.DEFAULT_EMPTY_STRING;
	
	String telNumber=V3xOrgEntity.DEFAULT_EMPTY_STRING;

	String gender=V3xOrgEntity.DEFAULT_EMPTY_STRING;
	
	String birthday=V3xOrgEntity.DEFAULT_EMPTY_STRING;
	
	String officeNumber=V3xOrgEntity.DEFAULT_EMPTY_STRING;
	
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getOfficeNumber() {
		return officeNumber;
	}

	public void setOfficeNumber(String officeNumber) {
		this.officeNumber = officeNumber;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPpost() {
		return ppost;
	}

	public void setPpost(String ppost) {
		this.ppost = ppost;
	}

	public String getEMail() {
		return eMail;
	}

	public void setEMail(String mail) {
		eMail = mail;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public void setTelNumber(String telNumber) {
		this.telNumber = telNumber;
	}
}//end class
