package com.seeyon.v3x.hr.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;


/**
 * 
 * <p/> Title:考勤记录
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Jun 13, 2007
 * </p>
 * 
 * @author Dongjw
 * @version 1.0
 */
public class Record  extends BaseModel implements java.io.Serializable {

	private static final long serialVersionUID = -4051323757814867124L;

	/**
	 * 人员id
	 */
	private Long staffer_id;
	
	/**
	 * 部门id
	 */
	private Long dep_id;
	
	/**
	 * 状态
	 */
	private RecordState state;
	
	/**
	 * 上班打卡时间
	 */
	private Date begin_work_time;
	
	/**
	 * 下班打卡时间
	 */
	private Date end_work_time;

	/**
	 * 所在年份
	 */
	private int year;
	
	/**
	 * 所在月份
	 */
	private int month;
	
	/**
	 * 所在天数
	 */
	private int day;
	
	/**
	 * 规定工作开始时间的小时数
	 */
	private String begin_hour;
	
	/**
	 * 规定工作开始时间的分钟数
	 */
	private String begin_minute;
	
	/**
	 * 规定工作结束时间的小时数
	 */
	private String end_hour;
	
	/**
	 * 规定工作结束时间的分钟数
	 */
	private String end_minute;
		
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 单位id
	 */
	private Long accountId;
	/**
	 * 签到IP
	 */
	private String signInIP;
	/**
	 * 签退IP
	 */
	private String signOutIP;
	/**
	 * 是否工作日
	 */
	private int isWorkDay;

	public String getSignInIP() {
		return signInIP;
	}

	public void setSignInIP(String signInIP) {
		this.signInIP = signInIP;
	}

	public String getSignOutIP() {
		return signOutIP;
	}

	public void setSignOutIP(String signOutIP) {
		this.signOutIP = signOutIP;
	}

	public Date getBegin_work_time() {
		return begin_work_time;
	}

	public void setBegin_work_time(Date begin_work_time) {
		this.begin_work_time = begin_work_time;
	}

	public Long getDep_id() {
		return dep_id;
	}

	public void setDep_id(Long dep_id) {
		this.dep_id = dep_id;
	}

	public Date getEnd_work_time() {
		return end_work_time;
	}

	public void setEnd_work_time(Date end_work_time) {
		this.end_work_time = end_work_time;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getStaffer_id() {
		return staffer_id;
	}

	public void setStaffer_id(Long staffer_id) {
		this.staffer_id = staffer_id;
	}

	public RecordState getState() {
		return state;
	}

	public void setState(RecordState state) {
		this.state = state;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getBegin_hour() {
		return begin_hour;
	}

	public void setBegin_hour(String begin_hour) {
		this.begin_hour = begin_hour;
	}

	public String getBegin_minute() {
		return begin_minute;
	}

	public void setBegin_minute(String begin_minute) {
		this.begin_minute = begin_minute;
	}

	public String getEnd_hour() {
		return end_hour;
	}

	public void setEnd_hour(String end_hour) {
		this.end_hour = end_hour;
	}

	public String getEnd_minute() {
		return end_minute;
	}

	public void setEnd_minute(String end_minute) {
		this.end_minute = end_minute;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public int getIsWorkDay() {
		return isWorkDay;
	}

	public void setIsWorkDay(int isWorkDay) {
		this.isWorkDay = isWorkDay;
	}
}
