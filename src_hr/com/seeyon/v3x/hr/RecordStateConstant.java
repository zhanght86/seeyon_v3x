package com.seeyon.v3x.hr;

public class RecordStateConstant {


	/**
	 * 上班未打卡
	 */
	public final static int NO_BEGIN_CARD = 1;
	
	/**
	 * 下班未打卡
	 */
	public final static int NO_END_CARD = 2;
	
	/**
	 * 上下班均未打卡
	 */
	public final static int NO_CARD = 3;
	
	/**
	 * 上班迟到
	 */
	public final static int COME_LATE = 4;
	
	/**
	 * 下班早退
	 */
	public final static int LEVEAEARLY = 5;
	
	/**
	 * 上班迟到并且下班早退
	 */
	public final static int BOTH = 6;
	
	/**
	 *正常 
	 */
	public final static int NORMAL = 7;
	
	/**
	 * 上班未打卡并早退
	 */
	public final static int NO_BEGIN_CARD_LEAVE_EARLY = 8;
	
	/**
	 * 迟到并下班未打卡
	 */
	public final static int COME_LATE_NO_END_CARD = 9;
}
