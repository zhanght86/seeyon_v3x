package com.seeyon.v3x.news.manager;

import com.seeyon.v3x.news.util.NewsUtils;

/**
 * 新闻模块的Manager的基类，主要是为了增加一个工具类<code>NewsUtils</code>
 * @author wolf
 *
 */
public class BaseNewsManager {
	private NewsUtils newsUtils;

	/**
	 * 获取NewsUtils工具类
	 * @return
	 */
	public NewsUtils getNewsUtils() {
		return newsUtils;
	}

	public void setNewsUtils(NewsUtils newsUtils) {
		this.newsUtils = newsUtils;
	}
	
	
}
