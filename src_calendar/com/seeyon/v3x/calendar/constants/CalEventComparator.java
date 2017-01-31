package com.seeyon.v3x.calendar.constants;

import java.util.Comparator;

import com.seeyon.v3x.calendar.domain.CalEvent;

public class CalEventComparator implements Comparator<CalEvent>{
	public int compare(CalEvent c1, CalEvent c2) {
		return -(c1.getBeginDate().compareTo(c2.getBeginDate()));
	}
}
