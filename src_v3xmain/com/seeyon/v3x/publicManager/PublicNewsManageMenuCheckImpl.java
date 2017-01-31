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
public class PublicNewsManageMenuCheckImpl implements MenuCheck {
	
	private NewsDataManager newsDataManager;

	/* (non-Javadoc)
	 * @see com.seeyon.v3x.menu.manager.MenuCheck#check(long)
	 * 公共信息的权限判断
	 */
	public boolean check(long memberId, long loginAccountId) {		
		// 新闻
		/*boolean news = newsDataManager.showManagerMenu(memberId);
		if(news)
			return true;*/
		return false;
	}

	public NewsDataManager getNewsDataManager() {
		return newsDataManager;
	}

	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

}
