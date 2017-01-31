package com.seeyon.v3x.calendar.manager;

import java.util.List;

import com.seeyon.v3x.calendar.domain.CalCancel;

/**
 * 对事件进行撤销操作
 * 
 * @author wolf
 * 
 */
@Deprecated
public interface CalCancelManager {

	public void save(CalCancel cancel);

	public List<CalCancel> getCancelListByEventId(Long eventId);

	public void deleteByEventId(Long eventId);
}