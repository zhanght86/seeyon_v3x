package com.seeyon.v3x.calendar.manager;

import java.util.Date;

import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.util.Constants;
import com.seeyon.v3x.collaboration.Constant;
import com.seeyon.v3x.util.Datetimes;

/**
 * 用于首页个人空间中日程事件栏目中，日程事件日期信息显示
 * @author <a href="mailto:yangm@seeyon.com">Rookie Young</a> 2010-5-10
 * @see com.seeyon.v3x.main.section.CalendarSection#projection(java.util.Map)
 */
public class EventDateRange {
	
	private static final String Time = "HH:mm";
	private static final String DateWithYear = "yyyy-MM-dd";
	private static final String DateWithourYear = "MM-dd";
	
	/**
	 * 通过日程信息获取对应的显示信息
	 * @param event 日程事件，满足一定的约束条件，参见：
	 * @see com.seeyon.v3x.calendar.manager.CalEventManager#getEventList4Section(Long, Boolean, Boolean)
	 * @return 该事件所属的日期范围类型
	 */
	public static Integer setEventDateInfo(CalEvent event) {
		String dateRangeType = "";
		String dateInfo = "";
		Integer rangeSort = 0;

		Date beginDate = event.getBeginDate();
		Date endDate = event.getEndDate();

		String beginDateS = Datetimes.formatDatetime(beginDate);
		String endDateS = Datetimes.formatDatetime(endDate);

		Date todayFirst = Datetimes.getTodayFirstTime();
		Date todayLast = Datetimes.getTodayLastTime();

		String todayFirstS = Datetimes.formatDatetime(todayFirst);
		String todayLastS = Datetimes.formatDatetime(todayLast);

		Date tomorrowFirst = Datetimes.addDate(todayFirst, 1);
		Date tomorrowLast = Datetimes.addDate(todayLast, 1);

		String tomorrowFirstS = Datetimes.formatDatetime(tomorrowFirst);
		String tomorrowLastS = Datetimes.formatDatetime(tomorrowLast);

		// 依次为：今日、跨日、明日、更晚、更早
		if ((beginDate.after(todayFirst) || beginDateS.equals(todayFirstS)) && (endDate.before(todayLast) || endDateS.equals(todayLastS))) {
			dateRangeType = Constant.getMainString("event.today");
			dateInfo = Datetimes.format(beginDate, Time) + "-" + Datetimes.format(endDate, Time);
			rangeSort = Constants.DateRangeType.today.ordinal();
		} else {
			if ((beginDate.before(todayLast) || beginDateS.equals(todayLastS)) && (endDate.after(todayFirst) || endDateS.equals(todayFirstS))) {
				dateRangeType = Constant.getMainString("event.antipodean");
				dateInfo = Datetimes.format(beginDate, DateWithourYear) + " " + Datetimes.format(endDate, DateWithourYear);
				rangeSort = Constants.DateRangeType.antipodean.ordinal();
			} else if (Datetimes.between(beginDate, tomorrowFirst, tomorrowLast, false) || beginDateS.equals(tomorrowFirstS) || beginDateS.equals(tomorrowLastS)) {
				dateRangeType = Constant.getMainString("event.tomorrow");
				dateInfo = Datetimes.format(beginDate, DateWithYear);
				rangeSort = Constants.DateRangeType.tomorrow.ordinal();
			} else if (beginDate.after(tomorrowLast) || beginDateS.equals(tomorrowLastS)) {
				dateRangeType = Constant.getMainString("event.late");
				dateInfo = Datetimes.format(beginDate, DateWithYear);
				rangeSort = Constants.DateRangeType.later.ordinal();
			} else if (endDate.before(todayFirst) || endDateS.equals(todayFirstS)) {
				dateRangeType = Constant.getMainString("event.old");
				dateInfo = Datetimes.format(beginDate, DateWithYear);
				rangeSort = Constants.DateRangeType.earlier.ordinal();
			}
		}

		event.setDateRangeType(dateRangeType);
		event.setDateInfo(dateInfo);
		event.setRangeSort(rangeSort);

		return rangeSort;
	}
	
}
