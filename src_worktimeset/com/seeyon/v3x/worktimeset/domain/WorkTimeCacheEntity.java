package com.seeyon.v3x.worktimeset.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WorkTimeCacheEntity implements Comparable<WorkTimeCacheEntity>,
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5172593559543400578L;
	private Long accountId;
	private Integer year;
	private boolean isGroup;
	// 是否是继承自集团
	private boolean isExtendGroupCommon;

	public WorkTimeCacheEntity() {
		this.currencyRestDaysCacheMap = new HashMap<Integer, WorkTimeCurrency>();
		this.currencyWorkDaysCacheMap = new HashMap<Integer, WorkTimeCurrency>();
		this.specialWorkDaysCacheMap = new HashMap<String, WorkTimeSpecial>();
	}

	public int compareTo(WorkTimeCacheEntity o) {
		return o.getYear().compareTo(this.getYear());
	}

	public boolean isExtendGroupCommon() {
		return isExtendGroupCommon;
	}

	public void setExtendGroupCommon(boolean isExtendGroupCommon) {
		this.isExtendGroupCommon = isExtendGroupCommon;
	}

	//key:周几
	// 工作日
	private Map<Integer, WorkTimeCurrency> currencyWorkDaysCacheMap;
	// 一般休息日
	private Map<Integer, WorkTimeCurrency> currencyRestDaysCacheMap;
	//key:2010/10/11 设定休息日
	private Map<String, WorkTimeSpecial> specialWorkDaysCacheMap;
	
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public boolean isGroup() {
		return isGroup;
	}
	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}
	public Map<Integer, WorkTimeCurrency> getCurrencyWorkDaysCacheMap() {
		return currencyWorkDaysCacheMap;
	}
	public void setCurrencyWorkDaysCacheMap(
			Map<Integer, WorkTimeCurrency> currencyWorkDaysCacheMap) {
		this.currencyWorkDaysCacheMap = currencyWorkDaysCacheMap;
	}
	public Map<Integer, WorkTimeCurrency> getCurrencyRestDaysCacheMap() {
		return currencyRestDaysCacheMap;
	}
	public void setCurrencyRestDaysCacheMap(
			Map<Integer, WorkTimeCurrency> currencyRestDaysCacheMap) {
		this.currencyRestDaysCacheMap = currencyRestDaysCacheMap;
	}

	public Map<String, WorkTimeSpecial> getSpecialWorkDaysCacheMap() {
		return specialWorkDaysCacheMap;
	}

	public void setSpecialWorkDaysCacheMap(
			Map<String, WorkTimeSpecial> specialWorkDaysCacheMap) {
		this.specialWorkDaysCacheMap = specialWorkDaysCacheMap;
	}

}
