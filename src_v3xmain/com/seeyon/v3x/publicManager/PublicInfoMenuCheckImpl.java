/**
 * 
 */
package com.seeyon.v3x.publicManager;

import java.util.List;

import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.menu.manager.MenuCheck;
import com.seeyon.v3x.news.manager.NewsDataManager;

/**
 * 公共信息的管理按钮权限判断
 * 
 */
public class PublicInfoMenuCheckImpl implements MenuCheck {
    private InquiryManager inquiryManager;
    
    private BbsBoardManager bbsBoardManager;
		
	private BulDataManager bulDataManager;
	
	private NewsDataManager newsDataManager;

	public BbsBoardManager getBbsBoardManager() {
		return bbsBoardManager;
	}

	public void setBbsBoardManager(BbsBoardManager bbsBoardManager) {
		this.bbsBoardManager = bbsBoardManager;
	}

	public InquiryManager getInquiryManager() {
		return inquiryManager;
	}

	public void setInquiryManager(InquiryManager inquiryManager) {
		this.inquiryManager = inquiryManager;
	}



	/**
	 * 判断"公共信息管理"菜单是否出现，只有用户在当前登录的单位中具备权限时，该菜单才会出现，否则用户不能持有对应操作入口
	 */
	public boolean check(long memberId, long loginAccountId) {		
		/*// 公告
		boolean bul = bulDataManager.showManagerMenu(memberId);
		if(bul)
			return true;
		// 新闻
		boolean news = newsDataManager.showManagerMenu(memberId);
		if(news)
			return true;
		// 调查
		try {
			// 需要考虑到切换单位兼职的情况，判断权限时需添加当前登录单位作为参数之一
			boolean inq = inquiryManager.hasManageAuthForAccountSpace(memberId, loginAccountId);
			if(inq)
				return true;
		} catch (Exception e) {
		}
		
		// 讨论
		List<V3xBbsBoard> v3xBbsBoardList =  this.bbsBoardManager.getAllCorporationBbsBoard();
		for(V3xBbsBoard board : v3xBbsBoardList){
			// 还需要求讨论版块所属单位与当前用户登录单位一致，因为涉及到切换单位兼职的情况
			if(board.getAccountId().longValue()==loginAccountId && bbsBoardManager.validUserIsAdmin(board.getId(),memberId)){
				return true;
			}
 		}*/
		
		return false;
	}

	public BulDataManager getBulDataManager() {
		return bulDataManager;
	}

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

	public NewsDataManager getNewsDataManager() {
		return newsDataManager;
	}

	public void setNewsDataManager(NewsDataManager newsDataManager) {
		this.newsDataManager = newsDataManager;
	}

}
