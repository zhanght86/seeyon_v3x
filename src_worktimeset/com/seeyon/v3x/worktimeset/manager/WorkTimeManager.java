package com.seeyon.v3x.worktimeset.manager;

import java.util.Date;

import com.seeyon.v3x.worktimeset.domain.WorkTimeCurrency;
import com.seeyon.v3x.worktimeset.exception.WorkTimeSetExecption;

/**
 * 工作时间对上层应用的接口
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2011-2-16
 */
public interface WorkTimeManager {

	/**
	 * deadline是按照工作时间计算好的
	 * @param beginTimeDate 起始时间点
	 * @param deadline 超期分钟数
	 * @param orgAcconutID 单位
	 * @return
	 * @throws WorkTimeSetExecption
	 */
	public Date getCompleteDate4Worktime(java.util.Date beginTimeDate, long deadline, Long orgAcconutID) throws WorkTimeSetExecption;
	
	/**
	 * deadline是按照自然时间
	 * @param beginTimeDate 起始时间点
	 * @param deadline 超期分钟数
	 * @param orgAcconutID单位
	 * @return
	 * @throws WorkTimeSetExecption
	 */
	public Date getCompleteDate4Nature(java.util.Date beginTimeDate, long deadline, Long orgAcconutID) throws WorkTimeSetExecption;

	/**
	 * 根据给定限定时间计算提前提醒时间。不按工作时间，简单地按自然时间计算。
	 * 
	 * @param completeDate
	 *            限定时间
	 * @param remindTime
	 *            提前提醒的分钟数
	 * @return 提前提醒的时间
	 */
	public Date getRemindDate(Date completeDate, long remindTime);

	/**
	 * 日期时间差。取得给定工作时间区间使用了的工作时间 单位 ms
	 * 
	 * @param beginDealTimeDate
	 *            开始工作时间
	 * @param endDealTimeDate
	 *            结束工作时间
	 * @param orgAcconutID
	 *            单位ID
	 * @return 在开始工作时间和结束工作时间之间，消耗了多少工作时间，单位ms
	 * @throws WorkTimeSetExecption
	 */
	public long getDealWithTimeValue(java.util.Date beginDealTimeDate, java.util.Date endDealTimeDate, Long orgAcconutID) throws WorkTimeSetExecption;

	/**
	 * 日期时间差。取得给定工作时间区间使用了的工作时间， 单位 天
	 * 
	 * @param beginDealTimeDate
	 *            开始工作时间
	 * @param endDealTimeDate
	 *            结束工作时间
	 * @param orgAcconutID
	 *            单位ID
	 * @return 在开始工作时间和结束工作时间之间，消耗了多少工作时间，单位 天
	 * @throws WorkTimeSetExecption
	 */
	public float differDateTime(java.util.Date beginDealTimeDate, java.util.Date endDealTimeDate, Long orgAcconutID) throws WorkTimeSetExecption;

	/**
	 * 日期差。根据传入的日期字符串，计算时间间隔。
	 * 
	 * @param beginDealDateStr
	 *            开始日期字符串 2010-12-08
	 * @param endDealDateStr
	 *            结束日期字符串 2010-12-08
	 * @param orgAcconutID
	 *            单位ID
	 * @return 时间间隔天数
	 * @throws WorkTimeSetExecption
	 */
	public float differDate(String beginDealDateStr, String endDealDateStr, Long orgAcconutID) throws WorkTimeSetExecption;

	/**
	 * 取得指定单位的指定年，月的每天的工作时间
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param orgAcconutID
	 *            单位id
	 * @param workTimeCurrency
	 *            工作时间设置
	 * @return 每天的工作时间，单位时分钟
	 */
	//public int getEachDayWorkTime(String year, String month, Long orgAcconutID, WorkTimeCurrency workTimeCurrency);

	/**
	 * 取得指定单位的指定年的每天的工作时间。
	 * @param year	 年,如2011
	 * @param orgAcconutID   单位ID
	 * @return 每天的工作时间，单位是分钟  比如 8*60分钟，返回单位ms。
	 */
	public int getEachDayWorkTime(int year, Long orgAcconutID) throws WorkTimeSetExecption;

	public final static String COMPUTE_UNIT_DAY = "day";
	public final static String COMPUTE_UNIT_HOUR = "hour";
	/**
	 * 日期时间与“天”或“小时”的加减运算，结果为工作日
	 * @param beginTimeDate
	 * @param operation "+"/"-"
	 * @param time 非负整数
	 * @param unit day/hour
	 * @param orgAcconutID
	 * @return
	 * @throws WorkTimeSetExecption
	 */
	public  Date getComputeDate(Date beginTimeDate,String operation, long time,String unit, Long orgAcconutID) throws WorkTimeSetExecption;
	/**
	 * 日期与天的加减运算，结果为工作日
	 * @param beginTimeDateStr
	 * @param operation "+"/"-"
	 * @param time 单位“天”，非负整数
	 * @param orgAcconutID
	 * @return
	 * @throws WorkTimeSetExecption
	 */
	public  Date getComputeDate(String beginTimeDateStr, String operation, long time, Long orgAcconutID) throws WorkTimeSetExecption;

	/**
	 * 将自然时间转化为工作时间。比如每天的工作时间为8*60，传入参数为24*60，返回8*60 <BR>
	 * 小于24*80的直接返回，大于24*60的取每天的工作时间相加返回。
	 * @param naturetime
	 * @param orgAccountId
	 * @return
	 */
	public Long convert2WorkTime(Long naturetime,Long orgAccountId);
	
	/**
	 * 将自然时间转化为工作时间。比如每天的工作时间为8*60，传入参数为24*60，返回8*60 <BR>
	 * 小于24*80的直接返回，大于24*60的取每天的工作时间相加返回。
	 * @param naturetime
	 * @param orgAccountId
	 * @param worktimeOfDay  :每天的工作时间。
	 * @return
	 */
	public Long convert2WorkTime(Long naturetime,Long orgAccountId,Integer worktimeOfDay);
}