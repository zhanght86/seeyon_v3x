package com.seeyon.v3x.calendar.manager;

import java.util.List;

import com.seeyon.v3x.calendar.domain.CalContent;

/**
 * 对事件的正文进行操作
 * 
 * @author wolf
 * 
 */
public interface CalContentManager {

	/**
	 * 保存
	 * 
	 * @param content
	 */
	public Long save(CalContent content);

	/**
	 * 根据主键获取正文
	 * 
	 * @param id
	 * @return
	 */
	public CalContent getContentById(Long id);

	/**
	 * 根据事件id获取正文
	 * 
	 * @param id
	 * @return
	 */
	public List<CalContent> getContentByEventId(Long eventId);
	
	/**
	 * 根据事件id获取事件正文
	 */
	public CalContent getEventContentByEventId(Long eventId);

	/**
	 * 根据主键删除正文
	 * 
	 * @param id
	 */
	public void deleteById(Long id);
	
	/**
	 * 根据事件id删除正文
	 * 
	 * @param id
	 */
	public void deleteByEventId(Long eventId);
	
}