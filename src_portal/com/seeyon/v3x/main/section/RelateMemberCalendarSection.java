package com.seeyon.v3x.main.section;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.manager.CalEventManager;
import com.seeyon.v3x.common.authenticate.domain.User;
import com.seeyon.v3x.common.web.login.CurrentUser;
import com.seeyon.v3x.common.web.util.ApplicationContextHolder;
import com.seeyon.v3x.main.section.templete.BaseSectionTemplete;
import com.seeyon.v3x.main.section.util.CalendarSectionUtil;
import com.seeyon.v3x.organization.manager.OrgManager;
import com.seeyon.v3x.peoplerelate.manager.PeopleRelateManager;

public class RelateMemberCalendarSection extends BaseSection {

	private static final Log log = LogFactory.getLog(RelateMemberCalendarSection.class);

	private CalEventManager calEventManager;

	private PeopleRelateManager peopleRelateManager;

	public void setCalEventManager(CalEventManager calEventManager) {
		this.calEventManager = calEventManager;
	}

	public void setPeopleRelateManager(PeopleRelateManager peopleRelateManager) {
		this.peopleRelateManager = peopleRelateManager;
	}

	public String getIcon() {
		return null;
	}

	public String getId() {
		return "relateMemberCalendarSection";
	}
	
	@Override
	public String getBaseName() {
		return "relateMemberCalendar";
	}

	public String getName(Map<String, String> preference) {
		return SectionUtils.getSectionName("relateMemberCalendar", preference);
	}

	public Integer getTotal(Map<String, String> preference) {
		return null;
	}

	public BaseSectionTemplete projection(Map<String, String> preference) {
		int count = SectionUtils.getSectionCount(8, preference);
		if(peopleRelateManager==null)
			peopleRelateManager= (PeopleRelateManager) ApplicationContextHolder.getBean("peoplerelateManager");
		User user = CurrentUser.get();
		List<CalEvent> eventList = null;
		try {
			eventList = this.calEventManager.getOtherEventListByUserIdForFirst(user, peopleRelateManager, null, null);
			if (CollectionUtils.isNotEmpty(eventList)) {
				if (eventList.size() > count) {
					eventList = eventList.subList(0, count);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}

		BaseSectionTemplete t = CalendarSectionUtil.setCalendarSectionData(preference, user.getId(), eventList, false, null);
		t.addBottomButton(BaseSectionTemplete.BOTTOM_BUTTON_LABEL_MORE, "/calEvent.do?method=homeEntry&otherCanlerder=otherCanlerder");
		return t;
	}
	
}