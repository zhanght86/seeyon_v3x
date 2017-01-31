package com.seeyon.v3x.calendar.manager.cap;

import java.util.List;

import com.seeyon.cap.calendar.domain.CalContentCAP;
import com.seeyon.cap.calendar.domain.CalEventCAP;
import com.seeyon.cap.calendar.manager.CalEventManagerCAP;
import com.seeyon.v3x.calendar.domain.CalContent;
import com.seeyon.v3x.calendar.domain.CalEvent;
import com.seeyon.v3x.calendar.manager.CalEventManager;
import com.seeyon.v3x.common.utils.BeanUtils;
import com.seeyon.v3x.index.share.datamodel.IndexInfo;

public class CalEventManagerCAPImpl implements CalEventManagerCAP {

	private CalEventManager calEventManager;

	public void setCalEventManager(CalEventManager calEventManager) {
		this.calEventManager = calEventManager;
	}

	@Override
	public void deleteCalEventFromOtherAppId(Long appId, Integer type, Long createUserId) {
		calEventManager.deleteCalEventFromOtherAppId(appId, type, createUserId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CalEventCAP> getAllCalEventByAppId(Long appId, Integer type) {
		List<CalEvent> list = calEventManager.getAllCalEventByAppId(appId, type);
		if (list == null) {
			return null;
		}
		return (List<CalEventCAP>) BeanUtils.converts(CalEventCAP.class, list);
	}

	@Override
	public IndexInfo getIndexInfo(long id) {
		return calEventManager.getIndexInfo(id);
	}

	@Override
	public CalEventCAP isHasCalEventByAppId(Long appId, Integer type, Long createUserId) {
		CalEvent calEvent = calEventManager.isHasCalEventByAppId(appId, type, createUserId);
		if (calEvent == null) {
			return null;
		}
		CalEventCAP calEventCAP = new CalEventCAP();
		BeanUtils.convert(calEventCAP, calEvent);
		return calEventCAP;
	}

	@Override
	public Long save(CalEventCAP event, boolean isNew) {
		CalEvent calEvent = new CalEvent();
		BeanUtils.convert(calEvent, event);
		return calEventManager.save(calEvent, isNew);
	}

	@Override
	public Long saveOrUpdateCalEventFromOtherApp(CalEventCAP calEvent, CalContentCAP calContent, Long createUserId) {
		CalEvent event = new CalEvent();
		BeanUtils.convert(event, calEvent);
		CalContent content = new CalContent();
		BeanUtils.convert(content, calContent);
		return calEventManager.saveOrUpdateCalEventFromOtherApp(event, content, createUserId);
	}

}