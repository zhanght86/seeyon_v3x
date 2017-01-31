package com.seeyon.v3x.calendar.domain;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

/**
 * 周期性事件的周期性信息
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-6-11
 */
public class CalEventPeriodicalInfo extends BaseModel {

	public int getPeriodicalType() {
		return periodicalType;
	}
	public void setPeriodicalType(int periodicalType) {
		this.periodicalType = periodicalType;
	}
	public int getDayDate() {
		return dayDate;
	}
	public void setDayDate(int dayDate) {
		this.dayDate = dayDate;
	}
	public int getDayWeek() {
		return dayWeek;
	}
	public void setDayWeek(int dayWeek) {
		this.dayWeek = dayWeek;
	}
	public int getWeek() {
		return week;
	}
	public void setWeek(int week) {
		this.week = week;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	/**
	 *
	 */
	private static final long serialVersionUID = 3130359100006181537L;

	/** 定期重复类型：日、周、月、年，如每日重复  */
	private int periodicalType;
	/** 具体某一日 */
	private int dayDate;
	/** 一周中的某一日，如星期一 */
	private int dayWeek;
	/** 一月中的第几周，如第一周 */
	private int week;
	/** 一年中的某月，如一月 */
	private int month;
	/**
	 * 一周中的某几日，如星期一，星期三
	 */
	private String weeks;
	/**
	 * 周期的开始时间
	 */
	private Date beginTime;
	/**
	 * 周期的结束时间
	 */
	private Date endTime;

	private Long calEventId;

	/** 创建者*/
	private Long memberId;
	
	private PeriodicalCalEvent calEvent;
	
	public PeriodicalCalEvent getCalEvent() {
		return calEvent;
	}
	public void setCalEvent(PeriodicalCalEvent calEvent) {
		this.calEvent = calEvent;
	}
	public Long getCalEventId() {
		return calEventId;
	}
	public void setCalEventId(Long calEventId) {
		this.calEventId = calEventId;
	}
	public Long getMemberId() {
		return memberId;
	}
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}
	public String getWeeks() {
		return weeks;
	}
	public void setWeeks(String weeks) {
		this.weeks = weeks;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public void clearSet(){
		this.dayDate = 0;
		this.dayWeek = 0;
		this.week = 0;
		this.month = 0;
		this.weeks = "";
	}
}
