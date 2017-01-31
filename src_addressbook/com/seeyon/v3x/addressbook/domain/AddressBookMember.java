/**
 * $Id: AddressBookMember.java,v 1.4 2008/08/22 02:05:32 lucx Exp $
`* 
 * Licensed to the UFIDA
 */
package com.seeyon.v3x.addressbook.domain;

import java.io.Serializable;
import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 *
 * <p/> Title: 外部联系人<实体>
 * </p>
 * <p/> Description: 外部联系人<实体> 
 * </p>
 * <p/> Date: 2007-5-24
 * </p>
 * @author paul
 */
public class AddressBookMember extends BaseModel implements Serializable {
	private static final long serialVersionUID = 2222319755535223522L;
	
	private String name; //外部联系人：用户名称
//	private AddressBookTeam category; //外部联系人：类别
	private Long category; //外部联系人：类别	
	private String companyName; //外部联系人：单位名称
	private String companyDept;//外部联系人：级别
	private String companyLevel;//外部联系人：级别
	private String companyPost;//外部联系人：岗位
	private String companyPhone;//外部联系人：单位电话
	private String familyPhone;//外部联系人：家庭电话
	private String mobilePhone;//外部联系人：手机
	private String fax;//外部联系人：传真
	private String address;//外部联系人：地址
	private String postcode;//外部联系人：邮编
	private String email;//外部联系人：电子邮件
	private String website;//外部联系人：电子邮件
	private String blog;//外部联系人：博客
	private String msn;//外部联系人：msn
	private String qq;//外部联系人：qq
	private Long creatorId;//外部联系人：创建人Id
	private String creatorName;//外部联系人：创建人名称
	private Date createdTime;//外部联系人：创建时间
	private Date modifiedTime;//外部联系人：修改时间
	private String memo;//外部联系人：备注
	
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getCompanyDept() {
		return companyDept;
	}
	public void setCompanyDept(String companyDept) {
		this.companyDept = companyDept;
	}
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
	public String getCompanyLevel() {
		return companyLevel;
	}
	public void setCompanyLevel(String companyLevel) {
		this.companyLevel = companyLevel;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyPhone() {
		return companyPhone;
	}
	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}
	public String getCompanyPost() {
		return companyPost;
	}
	public void setCompanyPost(String companyPost) {
		this.companyPost = companyPost;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFamilyPhone() {
		return familyPhone;
	}
	public void setFamilyPhone(String familyPhone) {
		this.familyPhone = familyPhone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public Date getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	public String getMsn() {
		return msn;
	}
	public void setMsn(String msn) {
		this.msn = msn;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	/*public AddressBookTeam getCategory() {
		return category;
	}
	public void setCategory(AddressBookTeam category) {
		this.category = category;
	}*/
	public Long getCategory() {
		return category;
	}
	public void setCategory(Long category) {
		this.category = category;
	}
	
}
