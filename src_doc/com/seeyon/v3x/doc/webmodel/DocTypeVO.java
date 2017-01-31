package com.seeyon.v3x.doc.webmodel;

import com.seeyon.v3x.doc.domain.DocType;

/**
 * 内容类型vo
 */
public class DocTypeVO {
	private DocType docType;
	// 系统，自定义标记
	private String theDocType;
	// 是否当前登录用户的单位创建
	private boolean createdByCurrentAccount;
	// 是否已经使用
	private boolean used;
//	创建单位
	private String orgName;
	public boolean getUsed() {
		return used;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
	public String getOrgName(){
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public DocType getDocType() {
		return docType;
	}
	public void setDocType(DocType docType) {
		this.docType = docType;
	}
	public String getTheDocType() {
		return theDocType;
	}
	public void setTheDocType(String theDocType) {
		this.theDocType = theDocType;
	}
	public boolean getCreatedByCurrentAccount() {
		return createdByCurrentAccount;
	}
	public void setCreatedByCurrentAccount(boolean createdByCurrentAccount) {
		this.createdByCurrentAccount = createdByCurrentAccount;
	}
	
}
