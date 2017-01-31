package com.seeyon.v3x.addressbook.domain;

public class Csv {
	private String name;//全名
	private String compayName;//单位名称
	private String departmentName;//部门
	private String levelName;//职务级别
	private String familyAddress;//住宅地址
	private String familyPost;//住宅地址的邮政编码
	private String officePhone;//固定电话
	private String mobilePhone;//移动电话
	private String email;//电邮
	private String emailType;//电邮类型
	private String emailDisplay;//电邮表示名
	private String blogUrl;//博客
	
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLevelName() {
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	
	
	public String getCompayName() {
		return compayName;
	}
	public void setCompayName(String compayName) {
		this.compayName = compayName;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getFamilyAddress() {
		return familyAddress;
	}
	public void setFamilyAddress(String familyAddress) {
		this.familyAddress = familyAddress;
	}
	public String getFamilyPost() {
		return familyPost;
	}
	public void setFamilyPost(String familyPost) {
		this.familyPost = familyPost;
	}
	public String getOfficePhone() {
		return officePhone;
	}
	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}
	
	public String getBlogUrl() {
		return blogUrl;
	}
	public void setBlogUrl(String blogUrl) {
		this.blogUrl = blogUrl;
	}
	
	public String getEmailDisplay() {
		return emailDisplay;
	}
	public void setEmailDisplay(String emailDisplay) {
		this.emailDisplay = emailDisplay;
	}
	public String getEmailType() {
		return emailType;
	}
	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}
	
	private String getLastName(){
		 return this.name;
	}
	
	public String getCsv(boolean isEnableLevel) {
		StringBuilder str = new StringBuilder();
		str.append("\"");
		str.append(this.getLastName());
		str.append("\"");
		str.append(",");
		
		str.append("\"");
		str.append("");
		str.append("\"");
		str.append(",");
		
		str.append("\"");
		str.append(this.name!=null?"":"");
		str.append("\"");
		str.append(",");
		
		str.append("\"");
		str.append(this.compayName!=null?this.compayName:"");
		str.append("\"");
		str.append(",");
		
		str.append("\"");
		str.append(this.departmentName!=null?this.departmentName:"");
		str.append("\"");
		str.append(",");
		
		if (isEnableLevel) {
			str.append("\"");
			str.append(this.levelName != null ? this.levelName : "");
			str.append("\"");
			str.append(",");
		}
		
		str.append("\"");
		str.append(this.familyAddress!=null?this.familyAddress:"");
		str.append("\"");
		str.append(",");
		
		str.append("\"");
		str.append(this.familyPost!=null?this.familyPost:"");
		str.append("\"");
		str.append(",");
		
		str.append("\"");
		str.append(this.officePhone!=null?this.officePhone:"");
		str.append("\"");
		str.append(",");
		
		str.append("\"");
		str.append(this.mobilePhone!=null?this.mobilePhone:"");
		str.append("\"");
		str.append(",");
		
		str.append("\"");
		str.append(this.email!=null?this.email:"");
		str.append("\"");
		str.append(",");
		
		str.append("\"");
		str.append("SMTP");
		str.append("\"");
		str.append(",");
		
		str.append("\"");
		str.append(this.name!=null?this.name:""+"("+this.email!=null?this.email:""+")");
		str.append("\"");
		str.append(",");
		
		str.append("\"");
		str.append(this.blogUrl!=null?this.blogUrl:"");
		str.append("\"");
		str.append(",");
		return str+"\r\n";
	}
	
}
