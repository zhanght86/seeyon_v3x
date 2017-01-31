package com.seeyon.v3x.worktimeset.domain;

import com.seeyon.v3x.common.domain.BaseModel;

public class WorkTimeCurrency extends BaseModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7082437824578888377L;
	private long orgAcconutID;
	private String amWorkTimeBeginTime;
	private String amWorkTimeEndTime;
	private String pmWorkTimeBeginTime;
	private String pmWorkTimeEndTime;
	private String weekDayName;
	private String isWork;
	private String year;

	private java.sql.Timestamp updateTime;

	public java.sql.Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(java.sql.Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	public long getOrgAcconutID() {
		return orgAcconutID;
	}
	public void setOrgAcconutID(long orgAcconutID) {
		this.orgAcconutID = orgAcconutID;
	}
	public String getIsWork() {
		return isWork;
	}
	public void setIsWork(String isWork) {
		this.isWork = isWork;
	}

	public String getWeekDayName() {
		return weekDayName;
	}
	public void setWeekDayName(String weekDayName) {
		this.weekDayName = weekDayName;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getAmWorkTimeBeginTime() {
		return amWorkTimeBeginTime;
	}
	public void setAmWorkTimeBeginTime(String amWorkTimeBeginTime) {
		this.amWorkTimeBeginTime = amWorkTimeBeginTime;
	}
	public String getAmWorkTimeEndTime() {
		return amWorkTimeEndTime;
	}
	public void setAmWorkTimeEndTime(String amWorkTimeEndTime) {
		this.amWorkTimeEndTime = amWorkTimeEndTime;
	}
	public String getPmWorkTimeBeginTime() {
		return pmWorkTimeBeginTime;
	}
	public void setPmWorkTimeBeginTime(String pmWorkTimeBeginTime) {
		this.pmWorkTimeBeginTime = pmWorkTimeBeginTime;
	}
	public String getPmWorkTimeEndTime() {
		return pmWorkTimeEndTime;
	}
	public void setPmWorkTimeEndTime(String pmWorkTimeEndTime) {
		this.pmWorkTimeEndTime = pmWorkTimeEndTime;
	}
	
}
