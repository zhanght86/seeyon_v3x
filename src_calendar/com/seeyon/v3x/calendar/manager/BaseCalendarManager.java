package com.seeyon.v3x.calendar.manager;

import com.seeyon.v3x.calendar.util.CalendarUtils;

/**
 * 事件管理模块的Manager的基类，主要是为了增加一个工具类<code>CalendarUtils</code>
 * 
 * @author wolf
 * 
 */
public class BaseCalendarManager {
	private CalendarUtils calendarUtils;

	/**
	 * 获取BulletinUtils工具类
	 * 
	 * @return
	 */
	public CalendarUtils getCalendarUtils() {
		return calendarUtils;
	}

	public void setCalendarUtils(CalendarUtils calendarUtils) {
		this.calendarUtils = calendarUtils;
	}

}
