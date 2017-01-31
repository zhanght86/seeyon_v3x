package com.seeyon.v3x.common.rss.webmodel;

import java.util.List;

import com.seeyon.v3x.common.rss.domain.RssCategory;
import com.seeyon.v3x.common.rss.domain.RssCategoryChannel;

public class SubscribeVo {
	private RssCategory rssCategory;
	private List<RssCategoryChannel> list;
	private List<RssCategoryChannel> subscribed;
	
	public List<RssCategoryChannel> getSubscribed() {
		return subscribed;
	}
	public void setSubscribed(List<RssCategoryChannel> subscribed) {
		this.subscribed = subscribed;
	}
	public SubscribeVo(RssCategory rssCategory){
		this.rssCategory=rssCategory;
	}
	public SubscribeVo(){
		
	}
	public List<RssCategoryChannel> getList() {
		return list;
	}
	public void setList(List<RssCategoryChannel> list) {
		this.list = list;
	}
	public RssCategory getRssCategory() {
		return rssCategory;
	}
	public void setRssCategory(RssCategory rssCategory) {
		this.rssCategory = rssCategory;
	}
}
