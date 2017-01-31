package com.seeyon.v3x.news.manager;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;

import com.seeyon.v3x.news.dao.NewsDataDao;
import com.seeyon.v3x.news.dao.NewsLogDao;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.domain.NewsLog;

public class NewsLogManagerImpl extends BaseNewsManager implements NewsLogManager {
	private NewsLogDao newsLogDao;
	private NewsDataDao newsDataDao;
	
	private List<NewsLog> initList(List<NewsLog> logList){
		for(NewsLog log:logList){
			if(log.getUserId()!=null){
				log.setUserName(this.getNewsUtils().getMemberNameByUserId(log.getUserId()));
			}
			if(log.getRecordId()!=null){
				NewsData data=null;
				try{
					data=this.getNewsDataDao().get(log.getRecordId());
				}catch(Exception e){
					//
				}
				if(data!=null)
					log.setRecordTitle(data.getTitle());
			}
		}
		return logList;
	}
	
	public List<NewsLog> findAll() {
		return initList(newsLogDao.getAll());
	}

	
	@SuppressWarnings("unchecked")
	public List<NewsLog> findByExample(NewsLog log) {
		List<NewsLog> list=null;
		DetachedCriteria dc=DetachedCriteria.forClass(NewsLog.class);
		dc.add(Example.create(log));
		
		dc.addOrder(Order.desc("recordDate"));
		
		list=newsLogDao.paginate(
				dc.getExecutableCriteria(this.newsLogDao.getSessionFactory().getCurrentSession())		
			);
		
		return initList(list);
	}

	public void record(NewsLog log) {
		log.setRecordDate(new Date());
		if(log.isNew()) log.setIdIfNew();
		newsLogDao.save(log);
	}

	public NewsLogDao getNewsLogDao() {
		return newsLogDao;
	}

	public void setNewsLogDao(NewsLogDao newsLogDao) {
		this.newsLogDao = newsLogDao;
	}

	public NewsDataDao getNewsDataDao() {
		return newsDataDao;
	}

	public void setNewsDataDao(NewsDataDao newsDataDao) {
		this.newsDataDao = newsDataDao;
	}

}
