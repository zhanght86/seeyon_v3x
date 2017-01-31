package com.seeyon.v3x.news.manager;

import java.util.List;

import com.seeyon.v3x.news.domain.NewsLog;

public interface NewsLogManager {
	public void record(NewsLog log);
	public List<NewsLog> findAll();
	public List<NewsLog> findByExample(NewsLog log);
}
