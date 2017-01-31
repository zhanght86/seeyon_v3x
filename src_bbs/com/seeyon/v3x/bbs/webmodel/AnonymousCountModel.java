package com.seeyon.v3x.bbs.webmodel;

/**
 * 按照帖子发起人进行统计时，对匿名发起的帖子单独进行统计，获取日、周、月和全部的统计数
 * @see com.seeyon.v3x.bbs.manager.BbsArticleManagerImpl#getAnonymousCount4Statistic
 */
public class AnonymousCountModel {
	/** 本日发帖数 */
	private long dayTotalNum;
	/** 本周发帖数 */
	private long weekTotalNum;
	/** 本月发帖数 */
	private long monthTotalNum;
	/** 全部发帖数 */
	private long allTotalNum;
	
	public AnonymousCountModel() {
			
	}
	
	/**
	 * 匿名发帖统计构造方法
	 * @param dayTotalNum    本日发帖数
	 * @param weekTotalNum	 本周发帖数
	 * @param monthTotalNum  本月发帖数
	 * @param allTotalNum    全部发帖数
	 */
	public AnonymousCountModel(long dayTotalNum, long weekTotalNum, long monthTotalNum, long allTotalNum) {
		super();
		this.dayTotalNum = dayTotalNum;
		this.weekTotalNum = weekTotalNum;
		this.monthTotalNum = monthTotalNum;
		this.allTotalNum = allTotalNum;
	}
	
	public long getAllTotalNum() {
		return allTotalNum;
	}
	public void setAllTotalNum(long allTotalNum) {
		this.allTotalNum = allTotalNum;
	}
	public long getDayTotalNum() {
		return dayTotalNum;
	}
	public void setDayTotalNum(long dayTotalNum) {
		this.dayTotalNum = dayTotalNum;
	}
	public long getMonthTotalNum() {
		return monthTotalNum;
	}
	public void setMonthTotalNum(long monthTotalNum) {
		this.monthTotalNum = monthTotalNum;
	}
	public long getWeekTotalNum() {
		return weekTotalNum;
	}
	public void setWeekTotalNum(long weekTotalNum) {
		this.weekTotalNum = weekTotalNum;
	}
	
}
