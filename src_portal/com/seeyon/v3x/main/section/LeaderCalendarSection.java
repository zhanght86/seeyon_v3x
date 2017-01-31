package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.templete.CalendarFourColumnTemplete;
import com.seeyon.v3x.peoplerelate.RelationType;
import com.seeyon.v3x.peoplerelate.manager.PeopleRelateManager;
import com.seeyon.v3x.util.Datetimes;

/**
 * @deprecated
 */
public class LeaderCalendarSection extends BaseSection {
	
	private static final Log log = LogFactory.getLog(LeaderCalendarSection.class);

	private PeopleRelateManager peopleRelateManager;

	public void setPeopleRelateManager(PeopleRelateManager peopleRelateManager) {
		this.peopleRelateManager = peopleRelateManager;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getId() {
		return "leaderCalendarSection";
	}

	@Override
	public String getBaseName() {
		return "leaderCalendar";
	}

	@Override
	public String getName(Map<String, String> preference) {
		return "leaderCalendar";
	}

	@Override
	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	@Override
	public BaseSectionTemplete projection(Map<String, String> preference) {
		CalendarFourColumnTemplete c = new CalendarFourColumnTemplete();
		List<Long> leaderIdsList = null;
		try {
			leaderIdsList = peopleRelateManager.getAllRelateMembersId(CurrentUser.get().getId()).get(RelationType.leader);
		} catch (Exception e) {
			log.error("", e);
		}

		if (null != leaderIdsList && !leaderIdsList.isEmpty()) {
			List<CalEvent> eventList = null;
			if (eventList != null && !eventList.isEmpty()) {
				if (eventList.size() > 8) {
					eventList = eventList.subList(0, 8);
				}
				for (CalEvent calEvent : eventList) {
					CalendarFourColumnTemplete.Row row = c.addRow();
					row.setSubject(calEvent.getSubject());
					row.setLink("/calEvent.do?method=view&id=" + calEvent.getId());
					row.setHasAttachments((Boolean) calEvent.getAttachmentsFlag());
					row.setBeginDate(Datetimes.format(calEvent.getBeginDate(), "yy/MM/dd").toString());
					row.setEndDate(Datetimes.format(calEvent.getEndDate(), "yy/MM/dd").toString());
					row.setState(calEvent.getCreateUserName());
				}
			}
		}

		c.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/calEvent.do?method=moreEvent&type=leader");
		return c;
	}
	
}