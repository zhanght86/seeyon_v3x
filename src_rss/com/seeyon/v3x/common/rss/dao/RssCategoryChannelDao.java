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
import com.seeyon.v3x.common.rss.domain.RssCategoryChannel;
import com.seeyon.v3x.doc.util.Constants;

public  class RssCategoryChannelDao extends BaseHibernateDao<RssCategoryChannel> {
	public List findCategoryChannel(String name,long categoryId){
		String hsql="from RssCategoryChannel as rss where rss.name=? and rss.categoryId=?";
		List list=super.find(hsql,name,categoryId);
		return list;
	}
	
	public int getMaxNumber(){
		String hsql="select max(rss.orderNum) from RssCategoryChannel as rss";
		List list=super.find(hsql);
		int number=0;
		if(list != null && list.isEmpty()==false){
			if(list.get(0)!= null){
				number=(Integer)list.get(0);
				number=number+1;
			}	
		}
		return number;
	}
	
	public void deleteChannels(String deleteIds){
		Map<String, Object> namedParameters = new HashMap<String, Object>();
		namedParameters.put("ids", Constants.parseStrings2Longs(deleteIds, ","));
		super.bulkUpdate("delete from RssCategoryChannel as rss where rss.id in (:ids)", namedParameters);
	}
	
	public List<RssCategoryChannel> getCategoryChannelByIds(String channelIds,long categoryId){
		String hsql="from RssCategoryChannel as rss where rss.categoryId=:aid and rss.id in (:ids)";
		Map<String, Object> amap = new HashMap<String, Object>();
		amap.put("aid", categoryId);
		amap.put("ids", Constants.parseStrings2Longs(channelIds, ","));
		return super.find(hsql,-1, -1, amap);
	}
	
	//时间最新的排在最前
	public List<RssCategoryChannel> getCategoryChannelByIds(String channelIds){
		String hsql="from RssCategoryChannel as rss where  rss.id in (:ids) order by rss.createDate desc";
		Map<String, Object> amap = new HashMap<String, Object>();
		amap.put("ids", Constants.parseStrings2Longs(channelIds, ","));
		return super.find(hsql, -1, -1, amap);
	}
	
	//按分组查询出频道
	public  List<RssCategoryChannel> findCategoryChannelByGrop(){
		String hsql="from RssCategoryChannel as rss order by rss.categoryId ";
		
		return super.find(hsql);
		
	}
	
	public List<RssCategoryChannel> getCategoryChannels(long categoryId){
		String hsql="from RssCategoryChannel as rss where rss.categoryId=? order by rss.orderNum asc";
		return super.find(hsql, categoryId);
	}
	public List<RssCategoryChannel> getCategoryChannelsByPage(final long categoryId){
		List<RssCategoryChannel> ret = (List<RssCategoryChannel>)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hsql="from RssCategoryChannel as rss where rss.categoryId=?";
				String orderStr = " order by rss.createDate desc";
				
				String hql2 = "select count(*) " + hsql;
	        	Query query2 = session.createQuery(hql2).setParameter(0, categoryId);
	    		List list2 = query2.list();	    		
	            Pagination.setRowCount((Integer)(list2.get(0)));
				
				Query query = session.createQuery(hsql + orderStr);
				return query
					.setParameter(0, categoryId)
					.setFirstResult(Pagination.getFirstResult())
					.setMaxResults(Pagination.getMaxResults())
					.list();
			}
    	});
		
		return ret;
		
	}
	
	public List<RssCategoryChannel> getMyCategoryChannels(long userId) {
		String hsql = "select distinct a from RssCategoryChannel a,RssSubscribe b "
			+ "where a.id=b.categoryChannelId AND b.userId=? "
			+ "order by a.categoryId,a.orderNum";		
		return super.find(hsql, userId);
	}
	
	public List<RssCategoryChannel> getAllChannels() {
		String hsql = "from RssCategoryChannel as rss order by rss.categoryId,rss.orderNum";
		return super.find(hsql);
	}
	public List<RssCategoryChannel> getAllChannelsByPage() {
	
		List<RssCategoryChannel> ret = (List<RssCategoryChannel>)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String hsql="from RssCategoryChannel as rss ";
				String orderStr = " order by rss.createDate desc";
				
				String hql2 = "select count(*) " + hsql;
	        	Query query2 = session.createQuery(hql2);
	    		List list2 = query2.list();	    		
	            Pagination.setRowCount((Integer)(list2.get(0)));
				
				Query query = session.createQuery(hsql + orderStr);
				return query.setFirstResult(Pagination.getFirstResult()).setMaxResults(Pagination.getMaxResults()).list();
			}
    	});
		
		return ret;
	}
	
}
