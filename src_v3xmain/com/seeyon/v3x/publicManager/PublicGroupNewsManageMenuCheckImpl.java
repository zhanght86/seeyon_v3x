/**
 * 
 */
package com.seeyon.v3x.publicManager;
import com.seeyon.v3x.menu.manager.MenuCheck;
import com.seeyon.v3x.news.manager.NewsDataManager;
import com.seeyon.v3x.news.manager.NewsTypeManager;

/**
 * 公共信息的管理按钮权限判断
 * 
 */
public class PublicGroupNewsManageMenuCheckImpl implements MenuCheck {
	
	private NewsDataManager newsDataManager;
	private NewsTypeManager newsTypeManager;
	public NewsTypeManager getNewsTypeManager() {
		return newsTypeManager;
	}

	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}
	public boolean check(long memberId, long loginAccountId) {	
		/*boolean news = false;
 		news = newsTypeManager.isGroupNewsTypeManager(memberId);
 		if(!news)
 			news = newsTypeManager.isGroupNewsTypeAuth(memberId);
		return news;*/
		return false;
	}

	public NewsDataManager getNewsDataManager() {
		return newsDataManager;
	}

	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

}
