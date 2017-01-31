package com.seeyon.v3x.common.rss.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.dao.paginate.Pagination;
import com.seeyon.v3x.common.rss.domain.RssChannelItems;
import com.seeyon.v3x.doc.util.Constants;

public class RssChannelItemDao extends BaseHibernateDao<RssChannelItems> {
	public List<RssChannelItems> findAllItems(long channelInfoId){
		String hsql="from RssChannelItems as rss where rss.channelInfoId =? order by rss.pubDate desc";
		return super.find(hsql, channelInfoId);
	}
	
	public List<RssChannelItems> getChannelItemsByIds(String channelIds){
		String hsql="from RssChannelItems as rss where rss.categoryChannelId in (:ids) order by rss.pubDate desc";
		Map<String, Object> amap = new HashMap<String, Object>();
		amap.put("ids", Constants.parseStrings2Longs(channelIds, ","));
		return super.find(hsql, -1, -1, amap);
	}
	
	public int getMyRecentlyItemsCount(long userId) {
		String hsql = "select count(a.id) from RssChannelItems a,RssSubscribe b "
			+ "where a.categoryChannelId = b.categoryChannelId and b.userId=?";
		List list = super.find(hsql, userId);
		int count = 0;
		if (list != null && !list.isEmpty()) {
			count = (Integer)list.get(0);
		}		
		return count;
	}
	
	@SuppressWarnings("unchecked")
	public List<RssChannelItems> getMyRecentlyItems(final long userId, int pageNo, int pageSize) {
		final int firstResult = (pageNo-1)*pageSize;
		final int maxResults = pageSize;
    	return (List<RssChannelItems>)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hsql = "select a from RssChannelItems a,RssSubscribe b "
					+ "where a.categoryChannelId = b.categoryChannelId and b.userId=? "
					+ "order by a.pubDate desc";	
				
				return (List<RssChannelItems>)session.createQuery(hsql)
					.setParameter(0, userId)
					.setFirstResult(firstResult)
					.setMaxResults(maxResults)
					.list();
			}
    	});

	}
	
}
