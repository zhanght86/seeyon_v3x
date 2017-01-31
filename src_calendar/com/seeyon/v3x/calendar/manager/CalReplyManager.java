package com.seeyon.v3x.calendar.manager;

import java.util.List;

import com.seeyon.v3x.calendar.domain.CalReply;

/**
 * 对事件进行回复操作
 * 
 * @author wolf
 * 
 */
public interface CalReplyManager {

	/**
	 * 保存
	 * 
	 * @param reply
	 */
	public void save(CalReply reply);

	/**
	 * 获取某个事件的回复列表
	 * 
	 * @param eventId
	 * @return
	 */
	public List<CalReply> getReplyListByEventId(Long eventId);

	/**
	 * 删除某个事件的所有回复记录
	 * 
	 * @param eventId
	 */
	public void deleteByEventId(Long eventId);
}