package com.seeyon.v3x.system.signet.domain;

import java.util.Date;

import com.seeyon.v3x.common.appLog.domain.AppLog;

public class WebAppLog {
      private Long Id ;
      private String user ;
      private String account ;
      private String depment ;
      private String actionType ;
      private String actionDesc ;
  	  private String ipAddress;
  	  private String modelName ;
  	  private Date actionTime ;
  	  private AppLog appLog;
  	  
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getDepment() {
		return depment;
	}
	public void setDepment(String depment) {
		this.depment = depment;
	}
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getActionDesc() {
		return actionDesc;
	}
	public void setActionDesc(String actionDesc) {
		this.actionDesc = actionDesc;
	}
	public Date getActionTime() {
		return actionTime;
	}
	public void setActionTime(Date actionTime) {
		this.actionTime = actionTime;
	}
	public AppLog getAppLog() {
		return appLog;
	}
	public void setAppLog(AppLog appLog) {
		this.appLog = appLog;
	}


}
