package com.seeyon.v3x.main.section.templete;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.taglibs.functions.Functions;
import com.seeyon.v3x.util.Datetimes;

/**
 * 月历式栏目
 * 
 * @author <a href="mailto:tanmf@seeyon.com">Tanmf</a>
 * @version 1.0 2007-7-17
 * 
 */
public class MonthCalendarTemplate extends BaseSectionTemplete {

	private static final long serialVersionUID = -1810191988924600397L;

	private Map<String, List<String>> days;

	public Map<String, List<String>> getDays() {
		return days;
	}

	public Date getToday() {
		return new Date();
	}

	public void addEvent(Date date, String... eventSubjects) {
		if (eventSubjects == null) {
			return;
		}

		if (this.days == null) {
			this.days = new HashMap<String, List<String>>();
		}

		String day = Datetimes.formatDate(date);
		List<String> es = this.days.get(day);
		if (es == null) {
			es = new ArrayList<String>();
			this.days.put(day, es);
		}

		for (String eventSubject : eventSubjects) {
			es.add(Functions.toHTMLAlt(eventSubject));
		}
	}

	@Override
	public String getResolveFunction() {
		return "MonthCalendarTemplate";
	}

}