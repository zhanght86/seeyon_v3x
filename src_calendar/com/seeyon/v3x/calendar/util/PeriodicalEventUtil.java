package com.seeyon.v3x.calendar.util;

import java.util.Date;

import com.seeyon.v3x.calendar.domain.PeriodicalCalEvent;
import com.seeyon.v3x.util.Datetimes;

public class PeriodicalEventUtil {
	
	public static Date getPeridicaiCreateDate(PeriodicalCalEvent event,Date beginDate){
		int betweenDay = (int)Datetimes.minusDay(event.getCreateDate(),event.getBeginDate());
		Date createDate = Datetimes.addDate(Datetimes.getTodayFirstTime(beginDate), betweenDay);
		return createDate;
	}
}
