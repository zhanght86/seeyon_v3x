package com.seeyon.v3x.calendar.manager;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.seeyon.v3x.calendar.dao.CalReplyDao;
import com.seeyon.v3x.calendar.domain.CalReply;

public class CalReplyManagerImpl extends BaseCalendarManager implements
		CalReplyManager {

	private CalReplyDao calReplyDao;

	public CalReplyDao getCalReplyDao() {
		return calReplyDao;
	}

	public void setCalReplyDao(CalReplyDao calReplyDao) {
		this.calReplyDao = calReplyDao;
	}

	/**
	 * 初始化 ---回复记录，设置回复人的用户名称
	 *
	 * @param reply
	 */
	private void initReply(CalReply reply) {
		reply.setReplyUserName(this.getCalendarUtils().getMemberNameByUserId(
				reply.getReplyUserId()));
	}

	/**
	 * 初始 ---化回复记录列表
	 *
	 * @param replyList
	 */
	private void initReplyList(List<CalReply> replyList) {
		for (CalReply reply : replyList) {
			this.initReply(reply);
		}
	}

	/**
	 * 删除 ---按事件ID删除
	 *
	 */
	public void deleteByEventId(Long eventId) {
		this.calReplyDao.delete(new String[] { "eventId" },
				new Object[] { eventId });
	}

	/**
	 * 保存
	 *
	 * @param reply
	 */
	public void save(CalReply reply) {
		if (reply.isNew()) {
			reply.setIdIfNew();
			this.calReplyDao.save(reply);
		} else {
			this.calReplyDao.update(reply);
		}
	}

	/**
	 * 根据事件Id，取出回复列表
	 *
	 * @param eventId
	 */
	@SuppressWarnings("unchecked")
	public List<CalReply> getReplyListByEventId(Long eventId) {

		DetachedCriteria dc = DetachedCriteria.forClass(CalReply.class);
		dc.add(Restrictions.eq("eventId", eventId));
		dc.addOrder(Order.desc("replyDate"));

		List<CalReply> list = this.calReplyDao.executeCriteria(dc, -1, -1);

		initReplyList(list);
		return list;
	}

}
