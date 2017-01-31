package com.seeyon.v3x.news.manager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeyon.v3x.news.dao.NewsReadDao;
import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.domain.NewsRead;

public class NewsReadManagerImpl extends BaseNewsManager implements
		NewsReadManager
{
	private NewsReadDao newsReadDao;

	public NewsReadDao getNewsReadDao()
	{
		return newsReadDao;
	}

	public void setNewsReadDao(NewsReadDao newsReadDao)
	{
		this.newsReadDao = newsReadDao;
	}

	@SuppressWarnings("static-access")
	public void configReadByData(NewsData data)
	{
		deleteReadByData(data);

		// String publishScope=data.getPublishScope();
		//		
		// String[]
		// userIds=this.getNewsUtils().getUserIdStrFromTypeIds(publishScope).split(",");
		// for(String userIdStr:userIds){
		// if(StringUtils.isNotBlank(userIdStr)){
		// Long userId=Long.valueOf(userIdStr);
		// NewsRead read=new NewsRead();
		// read.setNews(data);
		// read.setIdIfNew();
		// read.setManagerId(userId);
		// this.save(read);
		// }
		// }
	}

	public NewsRead getReadById(Long id)
	{
		return this.newsReadDao.get(id);
	}

	// public void save(NewsRead read) {
	// read.setAccountId(CurrentUser.get().getLoginAccount());
	// this.newsReadDao.save(read);
	// }

	public NewsRead getReadState(NewsData data, Long userId)
	{
		String hsql = "from NewsRead as read where read.news.id=? and read.managerId=?";
		Object[] values = new Object[] { data.getId(), userId };
		List<NewsRead> list = this.newsReadDao.find(hsql, values);
		if (list.size() > 0)
		{
			return list.get(0);
		}
		return new NewsRead();
	}

	public void setReadState(NewsData data, Long userId)
	{
		String hsql = "from NewsRead as read where read.news.id=? and read.managerId=?";
		Object[] values = new Object[] { data.getId(), userId };
		List<NewsRead> list = this.newsReadDao.find(hsql, values);
		if (list.size() > 0)
		{
			NewsRead read = list.get(0);
			if (read.isReadFlag())
				return;
			read.setReadDate(new Date());
			read.setReadFlag(true);
			this.newsReadDao.update(read);
		} else if (list.size() == 0)
		{
			NewsRead read = new NewsRead();
			read.setIdIfNew();
			read.setNews(data);
			read.setManagerId(userId);
			read.setReadDate(new Date());
			read.setReadFlag(true);
			read.setAccountId(-1l);
			this.newsReadDao.save(read);
		}
	}

	public void deleteReadByData(NewsData data)
	{
		// String hql="from NewsRead as read where read.news.id=?";
		// Object[] values=new Object[]{data.getId()};
		// this.newsReadDao.delete(hql, values);
		Map<String,Object> parameter = new HashMap<String,Object>();
		String hql = "delete from NewsRead where news = :news " ;
		parameter.put("news",data);
		this.newsReadDao.bulkUpdate(hql, parameter);
	}

	public List<NewsRead> getReadListByData(NewsData data)
	{
		String hql = "from NewsRead as read where read.news.id=?";
		Object[] values = new Object[] { data.getId() };
		return this.newsReadDao.find(hql, values);
	}

	public List<NewsRead> getReadListByUser(Long userId)
	{
		String hql = "from NewsRead as read where read.managerId=?";
		Object[] values = new Object[] { userId };
		return this.newsReadDao.find(hql, values);
	}
}
