package com.seeyon.v3x.hr.webmodel;

public class RecordStatisticDTO {
	
	private Long userId;
	private String name;
	private String department;
	private int noBeginCard;
	private int noEndCard;
	private int noCard;
	private int comeLate;
	private int leaveEarly;
	private int both;
	private int normal;
	private int noBeginCardLeaveEarly;
	private int comeLateNoEndCard;
	
	public RecordStatisticDTO() {
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public int getNoBeginCard() {
		return noBeginCard;
	}
	public void setNoBeginCard(int noBeginCard) {
		this.noBeginCard = noBeginCard;
	}
	public int getNoEndCard() {
		return noEndCard;
	}
	public void setNoEndCard(int noEndCard) {
		this.noEndCard = noEndCard;
	}
	public int getNoCard() {
		return noCard;
	}
	public void setNoCard(int noCard) {
		this.noCard = noCard;
	}
	public int getComeLate() {
		return comeLate;
	}
	public void setComeLate(int comeLate) {
		this.comeLate = comeLate;
	}
	public int getLeaveEarly() {
		return leaveEarly;
	}
	public void setLeaveEarly(int leaveEarly) {
		this.leaveEarly = leaveEarly;
	}
	public int getBoth() {
		return both;
	}
	public void setBoth(int both) {
		this.both = both;
	}
	public int getNormal() {
		return normal;
	}
	public void setNormal(int normal) {
		this.normal = normal;
	}
	public int getNoBeginCardLeaveEarly() {
		return noBeginCardLeaveEarly;
	}
	public void setNoBeginCardLeaveEarly(int noBeginCardLeaveEarly) {
		this.noBeginCardLeaveEarly = noBeginCardLeaveEarly;
	}
	public int getComeLateNoEndCard() {
		return comeLateNoEndCard;
	}
	public void setComeLateNoEndCard(int comeLateNoEndCard) {
		this.comeLateNoEndCard = comeLateNoEndCard;
	}
	
}
