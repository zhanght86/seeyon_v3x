package com.seeyon.v3x.worktimeset.manager;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.util.Datetimes;
import com.seeyon.v3x.worktimeset.domain.WorkTimeCurrency;
import com.seeyon.v3x.worktimeset.domain.WorkTimeSpecial;
import com.seeyon.v3x.worktimeset.exception.WorkTimeSetExecption;

/**
 * 
 *
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2011-2-16
 */
public class WorkTimeManagerImpl implements WorkTimeManager {
	
	private final static Log log = LogFactory.getLog(WorkTimeManagerImpl.class);
	
	private WorkTimeSetManager workTimeSetManager;
	
	public void setWorkTimeSetManager(WorkTimeSetManager workTimeSetManager) {
		this.workTimeSetManager = workTimeSetManager;
	}

	/*
	 * 根据给定限定时间计算提前提醒时间。不按工作时间，简单地按自然时间计算。
	 * 
	 * @param completeDate 限定时间
	 * 
	 * @param remindTime 提前提醒的分钟数
	 * 
	 * @return 提前提醒的时间
	 * 
	 * @see
	 * com.seeyon.v3x.worktimeset.manager.WorkTimeManager#getRemindDate(java
	 * .util.Date, long)
	 */
	public Date getRemindDate(Date completeDate, long remindTime) {
		if (completeDate == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(completeDate);
		calendar.add(Calendar.MINUTE, (int) -remindTime);
		return calendar.getTime();
	}

	public Date getCompleteDate4Worktime(java.util.Date beginTimeDate, long deadline, Long orgAcconutID) throws WorkTimeSetExecption {
		return this.calculateDate(beginTimeDate, false, deadline, orgAcconutID);
	}

	/*
	 * 对于1天以下、1天及以上处理不同。
	 * 假定周一至周五为工作日，每日9时上班，18时下班，发起时间（beginTimeDate）为周五17时：
	 * 若deadline为2小时（120），则结果应为下周一10时；
	 * 若deadline为1天（1440），则结果应为下周一17时。
	 */
	public Date getCompleteDate4Nature(java.util.Date beginTimeDate, long deadline, Long orgAcconutID) throws WorkTimeSetExecption {
		if (deadline >= 60 * 24) { // 60 * 24 = 1440，1天及以上的
			// 转换成天
			float day = (float) deadline / 1440f;
			// 每一天的工作分钟
			int workMinu = getEachDayWorkTime(beginTimeDate.getYear() + 1900, orgAcconutID);
			// 实际超期的工作分钟
			deadline = (long) (day * (float) workMinu);
		}
		return this.calculateDate(beginTimeDate, false, deadline, orgAcconutID);
	}

	public Date getComputeDate(String beginTimeDateStr, String operation,
			long time, Long orgAcconutID) throws WorkTimeSetExecption {
		if (time <= 0) {
			return Datetimes.parseDate(beginTimeDateStr);
		}
		boolean minusFlag = "-".equals(operation);
		Date beginTimeDate;
		if (!minusFlag) { // 加
			beginTimeDate = Datetimes.parse(beginTimeDateStr + " 23:59:59.999", "yyyy-MM-dd HH:mm:ss.SSS");
		} else { // 减
			beginTimeDate = Datetimes.parse(beginTimeDateStr + " 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS");
		}
		// 每一天的工作分钟
		int workMinu = getEachDayWorkTime(beginTimeDate.getYear() + 1900,
				orgAcconutID);
		// 实际的工作分钟
		long minutes = time * workMinu;
		return calculateDate(beginTimeDate, minusFlag, minutes,
				orgAcconutID);
	}

	/**
	 * 取得计算后的时间 
	 * @param beginTimeDate :开始时间 
	 * @param time :计算时间   :例如(1)
	 * @param unit :计量单位  :(小时/天)
	 * @param operation :操作符号(+/-)
	 * @param orgAcconutID
	 * @return
	 * @throws WorkTimeSetExecption
	 */
	public  Date getComputeDate(java.util.Date beginTimeDate,String operation, long time,String unit, Long orgAcconutID) throws WorkTimeSetExecption {
		if (time <= 0) {
			return beginTimeDate;
		}
		long minutes;
		if (COMPUTE_UNIT_DAY.equals(unit)) { // 按“天”计算
			// 每一天的工作分钟
			int workMinu = getEachDayWorkTime(beginTimeDate.getYear() + 1900, orgAcconutID);
			// 实际超期的工作分钟
			minutes = time * workMinu;
		} else { // 按“小时”计算
			minutes = time * 60;
		}
		return calculateDate(beginTimeDate, "-".equals(operation), minutes, orgAcconutID);
	}

	/**
	 * 日期时间与分钟加减运算
	 * @param beginTimeDate
	 * @param minusFlag false：加，true：减
	 * @param minutes 分钟数，非负整数
	 * @param orgAcconutID
	 * @return 工作时间
	 * @throws WorkTimeSetExecption
	 */
	private Date calculateDate(Date beginTimeDate, boolean minusFlag, long minutes, Long orgAcconutID) throws WorkTimeSetExecption {
		
		boolean isGroupAdmin = false;
		Date returnDate = null;

		Calendar beginTimeDateCalendar = Calendar.getInstance();

		if (beginTimeDate == null) {
			beginTimeDateCalendar.setTime(new java.util.Date());
		} else {
			beginTimeDateCalendar.setTime(beginTimeDate);
		}

		// 分钟转为ms
		long milliseconds = minutes * 60000;
		// 记录使用的工作时间
		long usedTimeValue = 0;
		// 记录一天是否是实际工作日
		boolean is_logical_work_day = false;
		// 是否是特殊设置的日期
		boolean is_special_work_day = false;
		// 记录超出的工作时间
		long overtopWorkTime = 0;
		// 计数变量temp
		int temp = 0;
		// 开始日期所处的年月日
		int year = beginTimeDateCalendar.get(Calendar.YEAR);
		int month = beginTimeDateCalendar.get(Calendar.MONTH) + 1;
		// int day = beginTimeDateCalendar.get(Calendar.DATE);

		// 从beginTimeDate开始循环，直到使用的工作时间等于要求的deadlineValue时，退出
		while (true) {
			int tempWeekNum = beginTimeDateCalendar.get(Calendar.DAY_OF_WEEK) - 1;
			String dateNum = Datetimes.format(beginTimeDateCalendar.getTime(), "yyyy/MM/dd");
			year = beginTimeDateCalendar.get(Calendar.YEAR);
			month = beginTimeDateCalendar.get(Calendar.MONTH) + 1;

			// 获得通用工作日对象集合（周几工作）
			// key "周几"
			Map<String, WorkTimeCurrency> workTimeCurrencies = workTimeSetManager.findComnWorkDaySet(year, orgAcconutID, isGroupAdmin, month);

			// 获得特殊设置的工作日和休息日，取当前日期的上一年，本年，以及下一年的
			// key "2010/10/11"
			Map<String, WorkTimeSpecial> workTimeSpecialMap = workTimeSetManager.findSpicalWorkDayMap(year, orgAcconutID, isGroupAdmin, month);

			if (workTimeCurrencies.containsKey(Integer.toString(tempWeekNum))) {
				// 工作日中包含beginTimeDateCalendar
				WorkTimeSpecial tempwWorkTimeSpecial = workTimeSpecialMap.get(dateNum);
				if (tempwWorkTimeSpecial != null) {
					is_special_work_day = true;
					// 不是特殊设置的休息日
					if (!((tempwWorkTimeSpecial.getIsRest() != null) && (!"0".equals(tempwWorkTimeSpecial.getIsRest())))) {
						// logicalWorkDayCollection.add(beginTimeDateCalendar);
						is_logical_work_day = true;
					}
				} else {
					// 一般工作日
					// logicalWorkDayCollection.add(beginTimeDateCalendar);
					is_logical_work_day = true;
				}
			} else {
				// 工作日中不包含beginTimeDateCalendar
				WorkTimeSpecial tempwWorkTimeSpecial = workTimeSpecialMap.get(dateNum);
				if (tempwWorkTimeSpecial != null) {
					is_special_work_day = true;
					// 是特殊设置的非休息日
					if ((tempwWorkTimeSpecial.getIsRest() != null)
							&& ("0".equals(tempwWorkTimeSpecial.getIsRest()))) {
						// logicalWorkDayCollection.add(beginTimeDateCalendar);
						is_logical_work_day = true;
					}
				}
			}

			WorkTimeCurrency tempWorkTimeCurrency = null;

			String workAmBeginTime = "";
			long workAmBeginTimeValue = 0;
			String workAmEndTime = "";
			long workAmEndTimeValue = 0;

			String workPmBeginTime = "";
			long workPmBeginTimeValue = 0;
			String workPmEndTime = "";
			long workPmEndTimeValue = 0;

			// 是工作日时，计算使用的工作时间
			if (is_logical_work_day) {
				if (is_special_work_day) {
					List<WorkTimeCurrency> workTime = workTimeSetManager.findComnWorkTimeSet(year, orgAcconutID, false, month);
					tempWorkTimeCurrency = workTime.get(0);
				} else {
					tempWorkTimeCurrency = workTimeCurrencies.get(Integer.toString(tempWeekNum));
				}
				if (tempWorkTimeCurrency != null) {
					workAmBeginTime = tempWorkTimeCurrency.getAmWorkTimeBeginTime();
					workAmEndTime = tempWorkTimeCurrency.getAmWorkTimeEndTime();
					workPmBeginTime = tempWorkTimeCurrency.getPmWorkTimeBeginTime();
					workPmEndTime = tempWorkTimeCurrency.getPmWorkTimeEndTime();
				}
				workAmBeginTimeValue = (Datetimes.parse(dateNum + " " + workAmBeginTime, "yyyy/MM/dd HH:mm")).getTime();
				workAmEndTimeValue = (Datetimes.parse(dateNum + " " + workAmEndTime, "yyyy/MM/dd HH:mm")).getTime();
				workPmBeginTimeValue = (Datetimes.parse(dateNum + " " + workPmBeginTime, "yyyy/MM/dd HH:mm")).getTime();
				workPmEndTimeValue = (Datetimes.parse(dateNum + " " + workPmEndTime, "yyyy/MM/dd HH:mm")).getTime();
				long workTimeMilliseconds = (workAmEndTimeValue - workAmBeginTimeValue) + (workPmEndTimeValue - workPmBeginTimeValue);
				if (temp == 0) {
					// 是开始计算的当日
					if ((beginTimeDateCalendar.getTimeInMillis()) <= workAmBeginTimeValue) {
						// 当日工作开始前
						usedTimeValue = usedTimeValue + (workAmEndTimeValue - workAmBeginTimeValue)
								+ (workPmEndTimeValue - workPmBeginTimeValue);
					} else if ((workAmBeginTimeValue < (beginTimeDateCalendar.getTimeInMillis()))
							&& ((beginTimeDateCalendar.getTimeInMillis() <= workAmEndTimeValue))) {
						// 上午工作时间中
						usedTimeValue = usedTimeValue
								+ (workAmEndTimeValue - (beginTimeDateCalendar.getTimeInMillis()))
								+ (workPmEndTimeValue - workPmBeginTimeValue);
					} else if ((beginTimeDateCalendar.getTimeInMillis() > workAmEndTimeValue)
							&& (beginTimeDateCalendar.getTimeInMillis()) <= workPmBeginTimeValue) {
						// 中文工作时间中
						usedTimeValue = usedTimeValue + (workPmEndTimeValue - workPmBeginTimeValue);
					} else if ((workPmBeginTimeValue < (beginTimeDateCalendar.getTimeInMillis()))
							&& ((beginTimeDateCalendar.getTimeInMillis() <= workPmEndTimeValue))) {
						// 下午工作时间中
						usedTimeValue = usedTimeValue + (workPmEndTimeValue - (beginTimeDateCalendar.getTimeInMillis()));
					}
					if (minusFlag) { // 减
						// 当前时刻之前的工作时长
						usedTimeValue = workTimeMilliseconds - usedTimeValue;
					}
				}
				// 开始日之后
				else {
					usedTimeValue = usedTimeValue + (workAmEndTimeValue - workAmBeginTimeValue) + (workPmEndTimeValue - workPmBeginTimeValue);
				}
			}
			if (usedTimeValue >= milliseconds) {
				overtopWorkTime = usedTimeValue - milliseconds;
				long tempEndTimeValue = 0;
				if (!minusFlag) { // 加
					if ((workPmEndTimeValue - overtopWorkTime) >= workPmBeginTimeValue) { // 应在下午
						tempEndTimeValue = workPmEndTimeValue - overtopWorkTime;
					} else { // 应在上午
						tempEndTimeValue = workAmEndTimeValue - (overtopWorkTime - (workPmEndTimeValue - workPmBeginTimeValue));
					}
				} else { // 减
					if ((workAmBeginTimeValue + overtopWorkTime) <= workAmEndTimeValue) { // 应在上午
						tempEndTimeValue = workAmBeginTimeValue + overtopWorkTime;
					} else { // 应在下午
						tempEndTimeValue = workPmBeginTimeValue + (overtopWorkTime - (workAmEndTimeValue - workAmBeginTimeValue));
					}
				}
				returnDate = new Date(tempEndTimeValue);
				break;
			}
			// 自增
			beginTimeDateCalendar.add(Calendar.DAY_OF_MONTH, minusFlag ? -1 : 1);
			temp = temp + 1;
			// 还原变量
			is_logical_work_day = false;
			is_special_work_day = false;

//			if (temp >= 1825) {
//				log.error("系统错误，请联系管理员！");
//				throw new WorkTimeSetExecption(new RuntimeException("系统错误：无限循环。"));
//			}
		}

		return returnDate;
	}

	public long getDealWithTimeValue(java.util.Date beginDealTimeDate,
			java.util.Date endDealTimeDate, Long orgAcconutID) throws WorkTimeSetExecption {
		boolean isGroupAdmin = false;
		Calendar beginTimeDateCalendar = Calendar.getInstance();
		Calendar endTimeDateCalendar = Calendar.getInstance();
		if (beginDealTimeDate == null) {
			beginTimeDateCalendar.setTime(new java.util.Date());
		} else {
			beginTimeDateCalendar.setTime(beginDealTimeDate);
		}

		if (endDealTimeDate != null) {
			endTimeDateCalendar.setTime(endDealTimeDate);
		} else {
			log.error("传入的结束时间为空。");
			throw new WorkTimeSetExecption(new RuntimeException("传入的结束时间为空。"));
		}

		boolean negativeFlag = false;
		if (beginTimeDateCalendar.after(endTimeDateCalendar)) {
			negativeFlag = true;
			Calendar temp = beginTimeDateCalendar;
			beginTimeDateCalendar = endTimeDateCalendar;
			endTimeDateCalendar = temp;
		}

		// 记录使用的工作时间
		long usedTimeValue = 0;
		// 记录超出的工作时间
		long overtopWorkTime = 0;
		// 记录一天是否是实际工作日
		boolean is_logical_work_day = false;
		// 记录一天是否是特殊设置的实际工作日
		boolean is_special_logical_work_day = false;
		// 计数变量temp
		int temp = 0;
		// 开始日期所处的年月日
		int year = beginTimeDateCalendar.get(Calendar.YEAR);
		int month = beginTimeDateCalendar.get(Calendar.MONTH) + 1;

		// 年月格式"yyyy/MM/dd"
		// 年月时间格式"yyyy/MM/dd HH:mm"

		while (true) {
			int tempWeekNum = beginTimeDateCalendar.get(Calendar.DAY_OF_WEEK) - 1;
			String dateNum = Datetimes.format(beginTimeDateCalendar.getTime(), "yyyy/MM/dd");
			year = beginTimeDateCalendar.get(Calendar.YEAR);
			month = beginTimeDateCalendar.get(Calendar.MONTH) + 1;

			// 获得通用工作日对象集合（周几工作）
			// key "周几"
			Map<String, WorkTimeCurrency> workTimeCurrencies = workTimeSetManager
					.findComnWorkDaySet(year, orgAcconutID, isGroupAdmin, month);

			// 获得特殊设置的工作日和休息日，取当前日期的本年的
			// key "2010/10/11"
			Map<String, WorkTimeSpecial> workTimeSpecialMap = workTimeSetManager
					.findSpicalWorkDayMap(year, orgAcconutID, isGroupAdmin, month);
			if (workTimeCurrencies.containsKey(Integer.toString(tempWeekNum))) {
				// 工作日中包含beginTimeDateCalendar
				WorkTimeSpecial tempwWorkTimeSpecial = workTimeSpecialMap.get(dateNum);
				if (tempwWorkTimeSpecial != null) {
					// 不是特殊设置的休息日
					if (!((tempwWorkTimeSpecial.getIsRest() != null) && (!"0"
							.equals(tempwWorkTimeSpecial.getIsRest())))) {
						is_logical_work_day = true;
					}
				} else {
					// 一般工作日
					is_logical_work_day = true;
				}
			} else {
				// 工作日中不包含beginTimeDateCalendar
				WorkTimeSpecial tempwWorkTimeSpecial = workTimeSpecialMap.get(dateNum);
				if (tempwWorkTimeSpecial != null) {
					// 是特殊设置的非休息日
					if ((tempwWorkTimeSpecial.getIsRest() != null)
							&& ("0".equals(tempwWorkTimeSpecial.getIsRest()))) {
						is_logical_work_day = true;
						is_special_logical_work_day = true;
					}
				}
			}

			WorkTimeCurrency tempWorkTimeCurrency = null;

			String workAmBeginTime = "";
			long workAmBeginTimeValue = 0;
			String workAmEndTime = "";
			long workAmEndTimeValue = 0;

			String workPmBeginTime = "";
			long workPmBeginTimeValue = 0;
			String workPmEndTime = "";
			long workPmEndTimeValue = 0;

			// 是工作日时，计算使用的工作时间
			if (is_logical_work_day) {
				if (is_special_logical_work_day) {
					// 是特殊设置的工作日
					for (Iterator iterator = workTimeCurrencies.values().iterator(); iterator.hasNext();) {
						tempWorkTimeCurrency = (WorkTimeCurrency) iterator.next();
						break;
					};
				} else {
					tempWorkTimeCurrency = workTimeCurrencies.get(Integer.toString(tempWeekNum));
				}
				if (tempWorkTimeCurrency != null) {
					workAmBeginTime = tempWorkTimeCurrency.getAmWorkTimeBeginTime();
					workAmEndTime = tempWorkTimeCurrency.getAmWorkTimeEndTime();
					workPmBeginTime = tempWorkTimeCurrency.getPmWorkTimeBeginTime();
					workPmEndTime = tempWorkTimeCurrency.getPmWorkTimeEndTime();
				}
				workAmBeginTimeValue = (Datetimes.parse(dateNum + " " + workAmBeginTime, "yyyy/MM/dd HH:mm")).getTime();
				workAmEndTimeValue = (Datetimes.parse(dateNum + " " + workAmEndTime, "yyyy/MM/dd HH:mm")).getTime();
				workPmBeginTimeValue = (Datetimes.parse(dateNum + " " + workPmBeginTime, "yyyy/MM/dd HH:mm")).getTime();
				workPmEndTimeValue = (Datetimes.parse(dateNum + " " + workPmEndTime, "yyyy/MM/dd HH:mm")).getTime();
				if (temp == 0) {
					// 是开始计算的当日
					if ((beginTimeDateCalendar.getTimeInMillis()) <= workAmBeginTimeValue) {
						usedTimeValue = usedTimeValue
								+ (workAmEndTimeValue - workAmBeginTimeValue)
								+ (workPmEndTimeValue - workPmBeginTimeValue);
					} else if ((workAmBeginTimeValue < (beginTimeDateCalendar.getTimeInMillis()))
							&& ((beginTimeDateCalendar.getTimeInMillis() <= workAmEndTimeValue))) {
						usedTimeValue = usedTimeValue
								+ (workAmEndTimeValue - (beginTimeDateCalendar.getTimeInMillis()))
								+ (workPmEndTimeValue - workPmBeginTimeValue);
					}

					else if ((beginTimeDateCalendar.getTimeInMillis() > workAmEndTimeValue)
							&& (beginTimeDateCalendar.getTimeInMillis()) <= workPmBeginTimeValue) {
						usedTimeValue = usedTimeValue
								+ (workPmEndTimeValue - workPmBeginTimeValue);
					} else if ((workPmBeginTimeValue < (beginTimeDateCalendar.getTimeInMillis()))
							&& ((beginTimeDateCalendar.getTimeInMillis() <= workPmEndTimeValue))) {
						usedTimeValue = usedTimeValue
								+ (workPmEndTimeValue - (beginTimeDateCalendar.getTimeInMillis()));
					}

				}
				// 开始日之后
				else {
					usedTimeValue = usedTimeValue
							+ (workAmEndTimeValue - workAmBeginTimeValue)
							+ (workPmEndTimeValue - workPmBeginTimeValue);
				}
			}

			if (beginTimeDateCalendar.get(Calendar.YEAR) == endTimeDateCalendar
					.get(Calendar.YEAR)
					&& beginTimeDateCalendar.get(Calendar.DAY_OF_YEAR) == endTimeDateCalendar
							.get(Calendar.DAY_OF_YEAR)) {
				if (endTimeDateCalendar.getTimeInMillis() <= workAmBeginTimeValue) {
					// 上午上班前
					usedTimeValue = usedTimeValue
							- (workPmEndTimeValue - workPmBeginTimeValue) // 下午时间
							- (workAmEndTimeValue - workAmBeginTimeValue); // 上午时间
				}
				if ((workAmBeginTimeValue < endTimeDateCalendar.getTimeInMillis())
						&& (endTimeDateCalendar.getTimeInMillis() <= workAmEndTimeValue)) {
					// 上午
					usedTimeValue = usedTimeValue
							- (workPmEndTimeValue - workPmBeginTimeValue) // 下午时间
							- (workAmEndTimeValue - endTimeDateCalendar.getTimeInMillis());
				}
				if ((workAmEndTimeValue < endTimeDateCalendar.getTimeInMillis())
						&& (endTimeDateCalendar.getTimeInMillis() <= workPmBeginTimeValue)) {
					// 中午
					usedTimeValue = usedTimeValue
							- (workPmEndTimeValue - workPmBeginTimeValue); // 下午时间
				}

				if ((workPmBeginTimeValue < endTimeDateCalendar.getTimeInMillis())
						&& (endTimeDateCalendar.getTimeInMillis() <= workPmEndTimeValue)) {
					// 下午
					usedTimeValue = usedTimeValue
							- (workPmEndTimeValue - endTimeDateCalendar.getTimeInMillis());
				}
				break;
			}
			beginTimeDateCalendar.add(Calendar.DAY_OF_MONTH, 1);
			temp = temp + 1;
			is_logical_work_day = false;
			is_special_logical_work_day = false;

//			if (temp >= 1825) {
//				log.error("系统错误，请联系管理员！");
//				throw new WorkTimeSetExecption(new RuntimeException("系统错误：无限循环。"));
//			}
		}

		if (negativeFlag) {
			usedTimeValue = -usedTimeValue;
		}
		return usedTimeValue;
	}
	
	
	/**
	 * 得到两个日期之间相隔的天数，按工作时间计算
	 * @param beginDealTimeDate  ： 开始时间
	 * @param endDealTimeDate ： 结束时间
	 * @param orgAcconutID ：单位ID
	 * @return
	 * @throws WorkTimeSetExecption
	 */
    public int getDifferDaysByWorkTime(java.util.Date beginDealTimeDate,
			java.util.Date endDealTimeDate, Long orgAcconutID)throws WorkTimeSetExecption{
    	boolean isGroupAdmin = false;
		Calendar beginTimeDateCalendar = Calendar.getInstance();
		Calendar endTimeDateCalendar = Calendar.getInstance();
		if (beginDealTimeDate == null) {
			beginTimeDateCalendar.setTime(new java.util.Date());
		} else {
			beginTimeDateCalendar.setTime(beginDealTimeDate);
		}

		if (endDealTimeDate != null) {
			endTimeDateCalendar.setTime(endDealTimeDate);
		} else {
			log.error("传入的结束时间为空。");
			throw new WorkTimeSetExecption(new RuntimeException("传入的结束时间为空。"));
		}

		boolean negativeFlag = false;
		if (beginTimeDateCalendar.after(endTimeDateCalendar)) {
			negativeFlag = true;
			Calendar temp = beginTimeDateCalendar;
			beginTimeDateCalendar = endTimeDateCalendar;
			endTimeDateCalendar = temp;
		}

		// 记录使用的工作时间
		int returnDays = 0;
		// 记录超出的工作时间
	//	long overtopWorkTime = 0;
		// 记录一天是否是实际工作日
		boolean is_logical_work_day = false;
		// 计数变量temp
		int temp = 0;
		// 开始日期所处的年月日
		int year = beginTimeDateCalendar.get(Calendar.YEAR);
		int month = beginTimeDateCalendar.get(Calendar.MONTH) + 1;

		while (true) {
			int tempWeekNum = beginTimeDateCalendar.get(Calendar.DAY_OF_WEEK) - 1;
			String dateNum = Datetimes.format(beginTimeDateCalendar.getTime(), "yyyy/MM/dd");
			year = beginTimeDateCalendar.get(Calendar.YEAR);
			month = beginTimeDateCalendar.get(Calendar.MONTH) + 1;

			// 获得通用工作日对象集合（周几工作）
			// key "周几"
			Map<String, WorkTimeCurrency> workTimeCurrencies = workTimeSetManager
					.findComnWorkDaySet(year, orgAcconutID, isGroupAdmin, month);

			// 获得特殊设置的工作日和休息日，取当前日期的本年的
			// key "2010/10/11"
			Map<String, WorkTimeSpecial> workTimeSpecialMap = workTimeSetManager
					.findSpicalWorkDayMap(year, orgAcconutID, isGroupAdmin, month);
			if (workTimeCurrencies.containsKey(Integer.toString(tempWeekNum))) {
				// 工作日中包含beginTimeDateCalendar
				WorkTimeSpecial tempwWorkTimeSpecial = workTimeSpecialMap.get(dateNum);
				if (tempwWorkTimeSpecial != null) {
					// 不是特殊设置的休息日
					if (!((tempwWorkTimeSpecial.getIsRest() != null) && (!"0"
							.equals(tempwWorkTimeSpecial.getIsRest())))) {
						is_logical_work_day = true;
					}
				} else {
					// 一般工作日
					is_logical_work_day = true;
				}
			} else {
				// 工作日中不包含beginTimeDateCalendar
				WorkTimeSpecial tempwWorkTimeSpecial = workTimeSpecialMap.get(dateNum);
				if (tempwWorkTimeSpecial != null) {
					// 是特殊设置的非休息日
					if ((tempwWorkTimeSpecial.getIsRest() != null)
							&& ("0".equals(tempwWorkTimeSpecial.getIsRest()))) {
						is_logical_work_day = true;
					}
				}
			}
			// 是工作日时，计算使用的工作时间
			if (is_logical_work_day) {
				if (temp > 0) {
					returnDays ++;
				}
			}

			if (beginTimeDateCalendar.get(Calendar.YEAR) == endTimeDateCalendar.get(Calendar.YEAR)
					&& beginTimeDateCalendar.get(Calendar.DAY_OF_YEAR) == endTimeDateCalendar.get(Calendar.DAY_OF_YEAR)) {
				break;
			}
			beginTimeDateCalendar.add(Calendar.DAY_OF_MONTH, 1);
			temp = temp + 1;
			is_logical_work_day = false;
		}

		if (negativeFlag) {
			returnDays = -returnDays;
		}
		return returnDays;
    }
	public float differDateTime(java.util.Date beginDealTimeDate,
			java.util.Date endDealTimeDate, Long orgAcconutID) throws WorkTimeSetExecption {
		long minutes = getDealWithTimeValue(beginDealTimeDate, endDealTimeDate, orgAcconutID) / (1000 * 60);
		// 每一天的工作分钟
		int workMinu = getEachDayWorkTime(beginDealTimeDate.getYear() + 1900, orgAcconutID);
		float diffDays = (float) minutes / (float) workMinu;
		return diffDays;
	}

	/*
	 * 日期差。经4月23日与郭姗姗确认： 为大日期与小日期的差值（工作日），不含小日期当天，含大日期当天；
	 * 或者说，相当于大日期23:59:59.999与小日期23:59:59.999间的日期时间差。
	 * 
	 * @see
	 * com.seeyon.v3x.worktimeset.manager.WorkTimeManager#differDate(java.lang
	 * .String, java.lang.String, java.lang.Long)
	 */
	public float differDate(String beginDealDateStr, String endDealDateStr,
			Long orgAcconutID) throws WorkTimeSetExecption {
		if (beginDealDateStr.equals(endDealDateStr)) {
			return 0;
		}
		//"yyyy-MM-dd HH:mm:ss.SSS"
		int resultDayNum = 0;
		resultDayNum = getDifferDaysByWorkTime(
				Datetimes.parse(beginDealDateStr + " 23:59:59.999", "yyyy-MM-dd HH:mm:ss.SSS"),
				Datetimes.parse(endDealDateStr + " 23:59:59.999", "yyyy-MM-dd HH:mm:ss.SSS"),
				orgAcconutID);
		return resultDayNum;
	}
	
	/**
	 * 两个时间短的分钟数
	 * @param beginMinu 09:05
	 * @param endMinu 11:25
	 * @return
	 */
	private static int getEachDayWorkTime(String beginMinu, String endMinu) {
		Date beginDate = Datetimes.parse(beginMinu, "HH:mm");
		Date endDate = Datetimes.parse(endMinu, "HH:mm");
		
		return (int)(endDate.getTime() - beginDate.getTime()) / 60000;
	}

	public int getEachDayWorkTime(int year, Long orgAcconutID) throws WorkTimeSetExecption {
		String _year = String.valueOf(year);
		WorkTimeCurrency workTimeCurrency = workTimeSetManager.findComnWorkTimeSet(_year, "1", orgAcconutID, false);
		
		// 上午工作时间
		String amBegingWorkTimeStr = workTimeCurrency.getAmWorkTimeBeginTime();
		String amEndWorkTimeStr = workTimeCurrency.getAmWorkTimeEndTime();

		// 下午工作时间
		String pmBegingWorkTimeStr = workTimeCurrency.getPmWorkTimeBeginTime();
		String pmEndWorkTimeStr = workTimeCurrency.getPmWorkTimeEndTime();

		int workTimeNum = getEachDayWorkTime(amBegingWorkTimeStr, amEndWorkTimeStr) + getEachDayWorkTime(pmBegingWorkTimeStr, pmEndWorkTimeStr);
		
		return workTimeNum;
	}
	public Long convert2WorkTime(Long naturetime,Long orgAccountId){
		Integer workTimeOfDay = 0;
		try {
			Calendar cal = Calendar.getInstance();
	    	int year =cal.get(Calendar.YEAR);
			workTimeOfDay = getEachDayWorkTime(year, orgAccountId);
		} catch (WorkTimeSetExecption e1) {
			log.error("",e1);
		}
		return convert2WorkTime(naturetime,orgAccountId,workTimeOfDay);
	}
	public Long convert2WorkTime(Long naturetime,Long orgAccountId,Integer workTimeOfDay){
		if(naturetime == null 
				|| orgAccountId == null
				|| workTimeOfDay == null)
			return null;
    	
		if(naturetime < 24*60){
			return naturetime;
		}else{
			if(naturetime!=null && workTimeOfDay!=0) 
				naturetime = ((Number)(naturetime*(workTimeOfDay/(24*60*1.0)))).longValue(); //按工作时间计算的流程期限。
		}	
		return naturetime;
	}
}
