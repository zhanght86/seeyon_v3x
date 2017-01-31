package com.seeyon.v3x.calendar;

import com.seeyon.v3x.common.exceptions.BusinessException;

/**
 * 日程事件的特定异常
 * 
 * @author wolf
 * 
 */
public class CalendarException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public CalendarException(String errorCode) {
		super(errorCode, new String[] {});
	}

	public CalendarException(String errorCode, String[] errorArgs) {
		super(errorCode, errorArgs);
	}

	public CalendarException(String errorCode, String errorArg) {
		super(errorCode, errorArg);
	}
}
