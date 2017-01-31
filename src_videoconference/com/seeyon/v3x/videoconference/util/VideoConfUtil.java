package com.seeyon.v3x.videoconference.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * file:DateUtil.java
 * 
 * @author pangj description:This is a simple date handle tool.
 *         postscript:该类没有考虑同步等情况使用中需要注意
 */
public final class VideoConfUtil {

	public static final String FORMATE_STYLE_DATA_SHORT = "yyyy-MM-dd";
	public static final String FORMATE_STYLE_DATA_LONG = "yyyy-MM-dd HH:mm:ss";
	private static VideoConfUtil instance = null;

	public static VideoConfUtil getInstance() {
		if (instance == null) {
			instance = new VideoConfUtil();
		}
		return instance;
	}

	private VideoConfUtil() {
	}


	/**
	 * convert string to date, with default pattern,throws ParseException
	 * 
	 * @param strDate
	 *            the str date
	 * @return date
	 */
	public static Date strToDate(String strDate) {
		return strToDate(strDate, FORMATE_STYLE_DATA_SHORT);
	}

	public static Date strToLongDate(String strDate) {
		return strToDate(strDate, FORMATE_STYLE_DATA_LONG);
	}

	/**
	 * convert string to date ,with custom pattern,throws
	 * IllegalArgumentException
	 * 
	 * @param strDate
	 *            str
	 * @param format
	 *            format style such as yyyy-MM-dd
	 * @return the date.
	 */
	public static Date strToDate(String strDate, String format) {
		if (strDate == null) {
			return null;
		}

		Date result = null;
		try {
			SimpleDateFormat formater = new SimpleDateFormat(format);
			result = formater.parse(strDate);
		} catch (ParseException e) {
			throw new RuntimeException("String To Date Convert Error", e);
		}
		return result;
	}

	// add by alex.su@2008-6-4
	/**
	 * GMT+8 timzone offset value.
	 */
	public static final String CST_TIMEZONE_OFFSET = "-480";

	/**
	 * Convert the date to GMT.
	 * 
	 * @param date
	 *            date.
	 * @return GMT Date.
	 */
	public static Date toGMTDate(Date date, String timeZoneDigital) {
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance(getTimeZone(timeZoneDigital));
		cal.setTime(date);

		cal.add(Calendar.HOUR_OF_DAY, getTimeOffSet(timeZoneDigital));
		// add at 20071015 by Jeecy for the timezone maybe infact minute.
		cal.add(Calendar.MINUTE, getMinuteOffSet(timeZoneDigital));
		return cal.getTime();
	}

	

	/**
	 * Get the timezone according to the timeZone_digital. <br>
	 * If the timeZoneDigital==null, default return GMT+8(CST).
	 * 
	 * @param timeZoneDigital
	 * @return
	 */
	public static TimeZone getTimeZone(String timeZoneDigital) {
		String gmt = "";
		if (timeZoneDigital != null) {
			int length = timeZoneDigital.length();
			String f = "";
			if (length == 1) {
				gmt = "GMT";
			} else if (timeZoneDigital.startsWith("-")) {
				f = timeZoneDigital.substring(1, length);
				gmt = "GMT+" + Integer.parseInt(f) / 60;
			} else {
				gmt = "GMT-" + Integer.parseInt(timeZoneDigital) / 60;
			}

		} else {
			return TimeZone.getTimeZone("GMT+8");
		}
		return TimeZone.getTimeZone(gmt);
	}

	private static int getTimeOffSet(String timeZoneDigital) {
		String localTimeZone = CST_TIMEZONE_OFFSET;
		if (timeZoneDigital != null && timeZoneDigital.trim().length() > 0) {
			localTimeZone = timeZoneDigital;
		}
		return Integer.parseInt(localTimeZone) / 60;
	}

	private static int getMinuteOffSet(String timeZoneDigital) {
		String localTimeZone = CST_TIMEZONE_OFFSET;
		if (timeZoneDigital != null && timeZoneDigital.trim().length() > 0) {
			localTimeZone = timeZoneDigital;
		}
		return (Integer.parseInt(localTimeZone) % 60);
	}
}
