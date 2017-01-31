package com.seeyon.v3x.common.rss.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.rss.domain.RssCategory;

public class RssCategoryDao extends BaseHibernateDao<RssCategory> {
	
	public List<RssCategory> findCategorys(){
		String hsql="from RssCategory as rss order by rss.orderNum asc";
		return super.find(hsql);
	}
	
	public List<RssCategory> findCategoriesByUserId(long userId) {
		String hsql = "select distinct a from RssCategory a,RssCategoryChannel b,RssSubscribe c "
			+ "where a.id=b.categoryId AND c.categoryChannelId = b.id AND c.userId=? order by a.orderNum asc";
		return super.find(hsql, userId);
	}
	
}