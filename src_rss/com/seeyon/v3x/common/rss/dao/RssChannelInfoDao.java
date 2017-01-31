package com.seeyon.v3x.common.rss.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.rss.domain.RssChannelInfo;
import com.seeyon.v3x.doc.util.Constants;

public class RssChannelInfoDao extends BaseHibernateDao<RssChannelInfo> {
	
	public void deleteChannelInfoById(long categoryChannelId) {
		RssChannelInfo rss = this.findByCategoryChannelId(categoryChannelId);
		if (rss != null) {
			super.delete(rss);// 级联删除ITEM
		}
	}
	
	public void deleteChannelInfoByIds(String categoryChannelIds){
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("ids", Constants.parseStrings2Longs(categoryChannelIds, ","));
		
		super.bulkUpdate("delete from RssChannelItems where channelInfoId in ("
				+ " select id from RssChannelInfo where categoryChannelId in (:ids) )", namedParameters);
		super.bulkUpdate("delete from RssChannelInfo as rss where rss.categoryChannelId in (:ids)", namedParameters);
	}
	
	@SuppressWarnings("unchecked")
	public RssChannelInfo findByCategoryChannelId(long categoryChannelId) {
		String hsql = "from RssChannelInfo as rss where rss.categoryChannelId=?";
		List<RssChannelInfo> list = super.find(hsql, categoryChannelId);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		else {
			return null;
		}
	}
	
	public SessionFactory getSessionFactory1(){
		return super.getSessionFactory();
	}
	
}
