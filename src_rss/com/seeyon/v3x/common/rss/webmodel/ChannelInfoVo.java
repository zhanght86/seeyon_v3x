package com.seeyon.v3x.common.rss.webmodel;

import java.util.Date;


import com.seeyon.v3x.common.rss.domain.RssChannelItems;

public class ChannelInfoVo {
	private RssChannelItems channelItem;
	private boolean isReaded;
	private String pubDate;

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public boolean getIsReaded() {
		return isReaded;
	}

	public void setIsReaded(boolean isReaded) {
		this.isReaded = isReaded;
	}

	public ChannelInfoVo(RssChannelItems channelItem){
		this.channelItem=channelItem;
		if(channelItem.getPubDate() != null)
		pubDate=channelItem.getPubDate().toGMTString();
	}
	
	public ChannelInfoVo(){
		
	}

	public RssChannelItems getChannelItem() {
		return channelItem;
	}

	public void setChannelItem(RssChannelItems channelItem) {
		this.channelItem = channelItem;
	}
}
