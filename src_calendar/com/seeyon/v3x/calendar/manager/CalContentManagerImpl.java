package com.seeyon.v3x.calendar.manager;

import java.util.List;

import com.seeyon.v3x.calendar.dao.CalContentDao;
import com.seeyon.v3x.calendar.domain.CalContent;

/**
 * 对事件的正文进行操作
 * 
 * @author wolf
 * 
 */
public class CalContentManagerImpl extends BaseCalendarManager implements
		CalContentManager {
	private CalContentDao calContentDao;

	public CalContentDao getCalContentDao() {
		return calContentDao;
	}

	public void setCalContentDao(CalContentDao calContentDao) {
		this.calContentDao = calContentDao;
	}

	/**
	 * 根据主键id删除正文
	 * 
	 * @param id
	 */
	public void deleteById(Long id) {
		this.calContentDao.delete(id.longValue());
	}

	/**
	 * 根据事件id删除正文
	 * 
	 * @param id
	 */
	public void deleteByEventId(Long eventId) {
		String hql = "delete from CalContent where eventId = ?";
		this.calContentDao.bulkUpdate(hql, null,eventId);
	}

	/**
	 * 根据主键id得到正文
	 * 
	 * @param id
	 */
	public CalContent getContentById(Long id) {
		return this.calContentDao.get(id);
	}

	/**
	 * 保存正文
	 * 
	 */
	public Long save(CalContent content) {
		if (content.isNew()) {
			content.setIdIfNew();
			this.calContentDao.save(content);
		} else {
			this.calContentDao.update(content);
		}
		return content.getId();
	}

	/**
	 * 根据事件id得到正文
	 * 
	 * @param id
	 */
	public List<CalContent> getContentByEventId(Long eventId) {
		// TODO Auto-generated method stub
		return this.calContentDao
				.find("from CalContent as content where content.eventId = ?",
						eventId);
	}
	
	/**
	 * 根据事件id获取事件正文(事件与正文为一对一关系，无需取集合后遍历。替代{@link #getContentByEventId})
	 */
	public CalContent getEventContentByEventId(Long eventId) {
		return this.calContentDao.findUniqueBy("eventId", eventId);
	}

}
