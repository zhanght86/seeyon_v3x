package com.seeyon.v3x.main.section;


import java.util.Date;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.manager.CalEventManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.MonthCalendarTemplate;
import com.seeyon.v3x.util.Datetimes;

/**
 * @deprecated
 */
public class CalendarMonthSection extends BaseSection {

	private CalEventManager calEventManager;

	public void setCalEventManager(CalEventManager calEventManager) {
		this.calEventManager = calEventManager;
	}
	
	@Override
	public String getIcon() {
        return null;
	}

	@Override
	public String getId() {
		return "calendarMonthSection";
	}
	
	@Override
	public String getBaseName() {
		return "calendar_month";
	}

	@Override
	public String getName(Map<String, String> preference) {
		return "calendar_month";
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		MonthCalendarTemplate mct = new MonthCalendarTemplate();
		
		Date beginDate = Datetimes.getFirstDayInMonth(new Date());
		Date endDate = Datetimes.getLastDayInMonth(new Date());
		Long userId = CurrentUser.get().getId();
		
		
		List<CalEvent> events = null;
		try {
			events = this.calEventManager.getAllEventListByUserId(userId, beginDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(events != null){
			for (CalEvent event : events) {
				mct.addEvent(event.getBeginDate(), event.getSubject());
			}
		}
		
		return mct;
	}

}
