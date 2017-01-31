package com.seeyon.v3x.doc.webmodel;

/**
 * 个人共享权限记录的vo
 *
 */
public class DocPersonalShareVO {
	// 共享给谁
	private Long userId;
	private String userType;
	private String userName;
	// 是否继承产生
	private boolean inherit;
	// 是否订阅
	private boolean alert = false;
	private Long alertId;
	// 对应的docAcl的id
	private Long aclId;
	
	public Long getAclId() {
		return aclId;
	}
	public void setAclId(Long aclId) {
		this.aclId = aclId;
	}
	public Long getAlertId() {
		return alertId;
	}
	public void setAlertId(Long alertId) {
		this.alertId = alertId;
	}
	public boolean isAlert() {
		return alert;
	}
	public void setAlert(boolean alert) {
		this.alert = alert;
	}
	public boolean isInherit() {
		return inherit;
	}
	public void setInherit(boolean inherit) {
		this.inherit = inherit;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	

}
