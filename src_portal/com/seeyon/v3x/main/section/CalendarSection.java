package com.seeyon.v3x.main.section;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.manager.CalEventManager;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.util.CalendarSectionUtil;
import com.seeyon.v3x.util.Datetimes;

public class CalendarSection extends BaseSection {
	
	private static final Log log = LogFactory.getLog(CalendarSection.class);
	
	private String titleId = "calendarSection";

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
		return titleId;
	}
	
	@Override
	public String getBaseName() {
		return "calendar";
	}

	@Override
	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("calendar", preference);
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		String columnsStyle = SectionUtils.getColumnStyle("list", preference);
		int count = SectionUtils.getSectionCount(8, preference);
		String resultValue = StringUtils.defaultIfEmpty(preference.get("resultValue"), "1,2");

		Long userId = CurrentUser.get().getId();
		List<CalEvent> eventList = null;
		try {
			boolean self = resultValue.indexOf("1") != -1;
			boolean arrangedOrConsigned = resultValue.indexOf("2") != -1;
			if ("list".equals(columnsStyle)) {// 列表
				eventList = this.calEventManager.getEventList4Section(userId, self, arrangedOrConsigned, count);
			} else if ("calendar".equals(columnsStyle)) {// 日历
				Date date = new Date();
				Date beginDate = Datetimes.getFirstDayInMonth(date);
				Date endDate = Datetimes.getLastDayInMonth(date);
				eventList = this.calEventManager.getEventList4Section(userId, self, arrangedOrConsigned, beginDate, endDate);
			}
		} catch (Exception e) {
			log.error("", e);
		}
		
		BaseSectionTemplete t = CalendarSectionUtil.setCalendarSectionData(preference, userId, eventList, true, titleId);
		t.addBottomButton("calendar_new", "javascript:openDetailInDlg('/calEvent.do?method=createEvent&from=section', '" + titleId + "', 520, 480)");
		if ("list".equals(columnsStyle)) {// 列表
			t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/calEvent.do?method=homeEntry");
		} else if ("calendar".equals(columnsStyle)) {// 日历
			t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/calEvent.do?method=month");
		}
		return t;
	}
	
}