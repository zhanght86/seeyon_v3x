package com.seeyon.v3x.calendar.manager;

import java.util.List;

import com.seeyon.v3x.calendar.dao.CalCancelDao;
import com.seeyon.v3x.calendar.domain.CalCancel;

/**
 * 对事件进行撤销操作
 * 
 * @author wolf
 * 
 */
@Deprecated
public class CalCancelManagerImpl extends BaseCalendarManager implements
		CalCancelManager {
	private CalCancelDao calCancelDao;

	public void deleteByEventId(Long eventId) {
		// TODO Auto-generated method stub

	}

	public List<CalCancel> getCancelListByEventId(Long eventId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(CalCancel cancel) {
		// TODO Auto-generated method stub

	}

	public CalCancelDao getCalCancelDao() {
		return calCancelDao;
	}

	public void setCalCancelDao(CalCancelDao calCancelDao) {
		this.calCancelDao = calCancelDao;
	}

}