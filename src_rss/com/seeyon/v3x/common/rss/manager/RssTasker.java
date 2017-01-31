package com.seeyon.v3x.common.rss.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * RSS频道解析任务调度器
 * @author zhangfeng
 *
 */
public class RssTasker implements Runnable {
	private static final Log log = LogFactory.getLog(RssTasker.class);
	private RssChannelManagerImpl rssChannelManager;

	public void run() {
		try {
			rssChannelManager.updateAllChannelInfo();		//更新所有的频道信息,定时更新
		} catch (Exception e) {
			log.warn("更新RSS频道信息时出现错误" + e);			
		}
		
	}
	
	public RssTasker (RssChannelManagerImpl rssChannelManager){
		this.rssChannelManager=rssChannelManager;
	}

}
