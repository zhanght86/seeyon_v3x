package com.seeyon.v3x.news.manager;

import java.util.List;

import com.seeyon.v3x.news.domain.NewsData;
import com.seeyon.v3x.news.domain.NewsRead;

public interface NewsReadManager {

	public void configReadByData(NewsData data);
	
	public void deleteReadByData(NewsData data);
	
	public NewsRead getReadById(Long id);
	
//	public void save(NewsRead read);
	
	public void setReadState(NewsData data,Long userId) ;
	
	public NewsRead getReadState(NewsData data,Long userId);
	
	public List<NewsRead> getReadListByData(NewsData data);
	
	public List<NewsRead> getReadListByUser(Long userId);
	
}