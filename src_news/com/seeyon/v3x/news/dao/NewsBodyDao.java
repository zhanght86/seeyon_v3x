package com.seeyon.v3x.news.dao;

import java.util.List;

import com.seeyon.v3x.common.dao.BaseHibernateDao;
import com.seeyon.v3x.news.domain.NewsBody;

public class NewsBodyDao extends BaseHibernateDao<NewsBody> {
	public NewsBody getByDataId(long newsDataId){
		String hql = "from NewsBody where newsDataId = ?";
		List<NewsBody> list = super.find(hql,newsDataId);
		if(list == null || list.size() == 0)
			return new NewsBody(newsDataId);
		else
			return list.get(0);
	}
	
	public void deleteByDataId(long newsDataId){
		//super.bulkUpdate("delete from NewsBody where newsDataId = " + newsDataId, null);
		//HQL语句清理 modified by Meng Yang 2009-05-27
		super.bulkUpdate("delete from NewsBody where newsDataId = ?", null, newsDataId);
	}
	public NewsBody getByFileId(String fileId){
		String hql = "from NewsBody where content like ?";
		List<NewsBody> list = super.find(hql,fileId);
		if(list == null || list.size() == 0)
			return new NewsBody();
		else
			return list.get(0);
	}
}
