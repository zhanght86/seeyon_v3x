/**
 * 
 */
package com.seeyon.v3x.publicManager;
import com.seeyon.v3x.menu.manager.MenuCheck;
import com.seeyon.v3x.news.manager.NewsDataManager;

/**
 * 公共信息的管理按钮权限判断
 * 
 */
public class PublicAccountNewsManageMenuCheckImpl implements MenuCheck {
	
	private NewsDataManager newsDataManager;
	public boolean check(long memberId, long loginAccountId) {	
		/*boolean news = false;
		news = newsDataManager.showManagerMenuOfLoginAccount(memberId);
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
