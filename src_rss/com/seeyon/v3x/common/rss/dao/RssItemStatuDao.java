package com.seeyon.v3x.common.rss.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.rss.domain.RssItemStatus;
import com.seeyon.v3x.doc.util.Constants;

public class RssItemStatuDao extends BaseHibernateDao<RssItemStatus> {
	public List<RssItemStatus> getRssItemStatus(long userId,long subId,long itemId){
		String hsql="from RssItemStatus as rss where rss.uesrId=? and rss.rssItemId=? and rss.rssSubscribeId=?";
		return super.find(hsql, userId,subId,itemId);
	}
	
	public List<RssItemStatus> getRssItemStatusById(long userId,long subId){
		String hsql="from RssItemStatus as rss where rss.uesrId=? and rss.rssSubscribeId=?";
		return super.find(hsql, userId,subId);
	}
	
	public List<RssItemStatus> getMyReadItems(long userId) {
		String hsql = "from RssItemStatus as rss where rss.uesrId=?";
		return super.find(hsql, userId);
	}
	
	//根据订阅的条目删除阅读标记
	public void deleteRssItemStatus(){		
//		super.delete("from RssItemStatus");
		super.bulkUpdate("delete from RssItemStatus", null);
	}
	/**
	 * 删除内容
	 * @param id
	 */
	public void deleteRssItemStatus(String deleteIds){		
		String hsql="delete from RssItemStatus rss where rss.rssSubscribeId in(:ids) " ;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ids", Constants.parseStrings2Longs(deleteIds, ","));
		super.bulkUpdate(hsql, map);

	}

}
