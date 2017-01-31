package com.seeyon.v3x.meeting.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.meeting.domain.MtReply;
import com.seeyon.v3x.meeting.util.Constants;
public class MtReplyDao extends BaseHibernateDao<MtReply> {
	/*
	 * 在发送会议之后，设置代理的情况。也应该按代理的业务流程走。
	 */
	@SuppressWarnings("unchecked")
	public List<MtReply> findByMeetingIdAndUserId(final Long meetingId,final Long userId) {
		return (List<MtReply>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException {
				
				StringBuffer hql = new StringBuffer("select mr from MtReply mr ,V3xAgent a ");
				hql.append("where mr.readDate between a.startDate and a.endDate  and a.cancelFlag=false and a.agentOption like '%6%' ");
				hql.append("and mr.meetingId= :meetingId and mr.userId=a.agentToId and a.agentId= :agentId ");
				
				Query query = session.createQuery(hql.toString());
				query.setLong("meetingId", meetingId);
				query.setLong("agentId", userId);
				return query.list();
			}
		});
	}
	
	/**
	 * 在修改会议时，删除被取消与会对象的会议回执记录
	 * @param meetingId 		会议ID
	 * @param reducedConferees	被取消的与会对象集合
	 */
	public void deleteCanceledRecords(Long meetingId, List<Long> reducedConferees) {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		String hql = "delete from " + MtReply.class.getName() + " as mr where mr.meetingId=:meetingId and mr.userId in (:reducedConferees)";
		parameterMap.put("meetingId", meetingId);
		parameterMap.put("reducedConferees", reducedConferees);
		this.bulkUpdate(hql, parameterMap);
	}

	/**
	 * 获取指定用户对指定会议的回执记录（区分亲自回执和代理人回执）
	 * @param meetingId 会议ID
	 * @param userId  用户ID
	 * @param isAgent 是否由代理人回执
	 */
	@SuppressWarnings("unchecked")
	public List<MtReply> findByMeetingIdAndUserId(Long meetingId, Long userId, Constants.ReplyType replyType) {
		DetachedCriteria dc=DetachedCriteria.forClass(MtReply.class);	
		dc.add(Restrictions.eq("meetingId", meetingId));
		dc.add(Restrictions.eq("userId", userId));
		switch(replyType) {
		case self :
			dc.add(Restrictions.eq("ext1",Constants.Not_Agent));
			break;
		case agent :
			dc.add(Restrictions.eq("ext1",Constants.PASSIVE_AGENT_FLAG));
			break;
		case all :
			break;
		}
		dc.addOrder(Order.desc("readDate"));
		return this.executeCriteria(dc, -1, -1);
	}

}
