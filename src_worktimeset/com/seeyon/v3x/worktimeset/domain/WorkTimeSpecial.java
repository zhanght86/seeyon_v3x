package com.seeyon.v3x.worktimeset.domain;

import com.seeyon.v3x.common.domain.BaseModel;

public class WorkTimeSpecial extends BaseModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4987653652245675L;
	private long orgAcconutID;
	private String dateNum;
	private String isRest;
	private String restInfo;
	private String weekNum;
	private String year;
	private String month;
	private java.sql.Timestamp updateTime;
	
	public long getOrgAcconutID() {
		return orgAcconutID;
	}
	public void setOrgAcconutID(long orgAcconutID) {
		this.orgAcconutID = orgAcconutID;
	}
	public String getDateNum() {
		return dateNum;
	}
	public void setDateNum(String dateNum) {
		this.dateNum = dateNum;
	}
	public String getIsRest() {
		return isRest;
	}
	public void setIsRest(String isRest) {
		this.isRest = isRest;
	}
	public String getRestInfo() {
		return restInfo;
	}
	public void setRestInfo(String restInfo) {
		this.restInfo = restInfo;
	}
	public String getWeekNum() {
		return weekNum;
	}
	public void setWeekNum(String weekNum) {
		this.weekNum = weekNum;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public java.sql.Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(java.sql.Timestamp updateTime) {
		this.updateTime = updateTime;
	}
}
