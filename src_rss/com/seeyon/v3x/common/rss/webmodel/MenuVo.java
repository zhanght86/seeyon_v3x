package com.seeyon.v3x.common.rss.webmodel;

import com.seeyon.v3x.common.rss.domain.RssCategory;
import com.seeyon.v3x.common.rss.domain.RssCategoryChannel;

public class MenuVo {
	private RssCategoryChannel rssChannel;
	private RssCategory   rssCategory;
	private String creator;
	
	public MenuVo(RssCategoryChannel rssChannel){
		this.rssChannel=rssChannel;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public RssCategoryChannel getRssChannel() {
		return rssChannel;
	}
	public void setRssChannel(RssCategoryChannel rssChannel) {
		this.rssChannel = rssChannel;
	}
	public RssCategory getRssCategory() {
		return rssCategory;
	}
	public void setRssCategory(RssCategory rssCategory) {
		this.rssCategory = rssCategory;
	}
}
