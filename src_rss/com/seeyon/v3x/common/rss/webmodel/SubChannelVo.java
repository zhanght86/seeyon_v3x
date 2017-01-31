package com.seeyon.v3x.common.rss.webmodel;

import com.seeyon.v3x.common.rss.domain.RssCategoryChannel;

public class SubChannelVo {
	private RssCategoryChannel channel;
	private boolean   subscribed;
	
	public SubChannelVo(RssCategoryChannel channel){
		this.channel=channel;
	}
	
	public SubChannelVo(){
		
	}
	
	public RssCategoryChannel getChannel() {
		return channel;
	}
	public void setChannel(RssCategoryChannel channel) {
		this.channel = channel;
	}
	public boolean isSubscribed() {
		return subscribed;
	}
	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}
}
