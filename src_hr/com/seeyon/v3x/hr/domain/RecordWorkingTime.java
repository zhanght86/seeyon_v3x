package com.seeyon.v3x.hr.domain;

import com.seeyon.v3x.common.domain.BaseModel;



/**
 * 
 * <p/> Title:工作时间
 * </p>
 * <p/> Description:
 * </p>
 * <p/> Date:Jun 14, 2007
 * </p>
 * 
 * @author Dongjw
 * @version 1.0
 */
public class RecordWorkingTime extends BaseModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5131393458214331156L;

	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 工作开始时间的小时数
	 */
	private int begin_hour;
	
	/**
	 * 工作开始时间的分钟数
	 */
	private int begin_minute;
	
	/**
	 * 工作结束时间的小时数
	 */
	private int end_hour;
	
	/**
	 * 工作结束时间的分钟数
	 */
	private int end_minute;
	
	/**
	 * 单位Id
	 */
	private Long accountId;

	public int getBegin_hour() {
		return begin_hour;
	}

	public void setBegin_hour(int begin_hour) {
		this.begin_hour = begin_hour;
	}

	public int getBegin_minute() {
		return begin_minute;
	}

	public void setBegin_minute(int begin_minute) {
		this.begin_minute = begin_minute;
	}

	public int getEnd_hour() {
		return end_hour;
	}

	public void setEnd_hour(int end_hour) {
		this.end_hour = end_hour;
	}

	public int getEnd_minute() {
		return end_minute;
	}

	public void setEnd_minute(int end_minute) {
		this.end_minute = end_minute;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
}
