package com.seeyon.v3x.office.book.domain;

public class TongjiInfo {
	private long applyUserNameId;
	private String name;
	private int week;
	private int month;
	private int total;
	private int totalNoBack;
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getTotalNoBack() {
		return totalNoBack;
	}
	public void setTotalNoBack(int totalNoBack) {
		this.totalNoBack = totalNoBack;
	}
	public int getWeek() {
		return week;
	}
	public void setWeek(int week) {
		this.week = week;
	}
	public long getApplyUserNameId() {
		return applyUserNameId;
	}
	public void setApplyUserNameId(long applyUserNameId) {
		this.applyUserNameId = applyUserNameId;
	}
}
