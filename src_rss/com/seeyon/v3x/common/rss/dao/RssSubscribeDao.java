package com.seeyon.v3x.common.rss.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.common.rss.domain.RssSubscribe;
import com.seeyon.v3x.doc.util.Constants;

public class RssSubscribeDao extends BaseHibernateDao<RssSubscribe> {

	public List findSubscribeByType(String userType,String name,long userId){
		String hsql="from RssSubscribe as rss where rss.userId=? and rss.name=? and rss.userType=? ";
		List list=super.find(hsql,userId,name, userType);
		return list;
	}
	
	//获取最大顺序
	public int getMaxOrder(String userType,long userId){
		String hsql="select max(rss.orderNum) from RssSubscribe as rss where rss.userId=? and rss.userType=? ";
		List list=super.find(hsql,userId, userType);
		int number=0;
		if(list != null && list.isEmpty()==false){
			if(list.get(0)!= null){
				number=(Integer)list.get(0);
				number=number+1;
			}
		}
		return number;
	}
	
	public void deleteSubcribes(String userType,long userId,String deleteIds){
		String hql = "from RssSubscribe as rss where rss.userId=:userid and rss.categoryChannelId in (:delids) and  rss.userType=:ut " ;
		String hsql="delete from RssSubscribe as rss where rss.userId=:userid and rss.categoryChannelId in (:delids) and  rss.userType=:ut ";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", userId);
		map.put("delids", Constants.parseStrings2Longs(deleteIds, ","));
		map.put("ut", userType);
		
		List<RssSubscribe> list=super.find(hql, -1, -1, map);
		if(list != null) {
			List<Long> listIds = new ArrayList<Long>() ;
			for(RssSubscribe rssSubscribe : list){
				listIds.add(rssSubscribe.getId()) ;
			}
			String hsq="delete from RssItemStatus rss where rss.rssSubscribeId in (:ids)" ;
			Map<String ,Object> m = new HashMap<String, Object>(); 
			m.put("ids", listIds) ;
			if(listIds.size()!= 0) {
				super.bulkUpdate(hsq, m) ;
			}	
		}
		
		super.bulkUpdate(hsql, map);

	}
	
	public List<RssSubscribe> getRssSubscribe(String userType,long userId){
		String hsql="from RssSubscribe as rss where rss.userId=? and rss.userType=? ";
		return super.find(hsql,userId,userType);
	}
	
	public List<RssSubscribe> getSubscribed(String userType,long userId,long categoryChannelId){
		String hsql="from RssSubscribe as rss where rss.userId=? and rss.categoryChannelId=? and rss.userType=? ";
		return super.find(hsql,userId, categoryChannelId,userType);
	}
	
	public RssSubscribe findMostNewSubscribe(String userType,long userId){
		StringBuffer buffer=new StringBuffer();
		buffer.append("from RssSubscribe as rss where rss.createDate = (");
		buffer.append("select max(sub.createDate) from RssSubscribe as sub where sub.userId=? and sub.userType=? )");
		List<RssSubscribe> list=super.find(buffer.toString(),userId, userType);
		if(list != null && list.isEmpty()==false){
			return list.get(0);
		}
		return null;
	}
	
	public List findSubscribeByChannelId(String ids){
		Map namedParameterMap = new HashMap() ;		
		String hsql="from RssSubscribe as rss where rss.categoryChannelId in (:ids)";
		namedParameterMap.put("ids", Constants.parseStrings2Longs(ids, ","));
		List list = super.find(hsql, -1, -1, namedParameterMap) ;
		//List list=super.find(hsql, namedParameterMap, null,-1,-1) ;
		return list;
	}
	
}
