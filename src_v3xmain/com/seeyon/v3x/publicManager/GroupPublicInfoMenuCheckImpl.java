/**
 * 
 */
package com.seeyon.v3x.publicManager;

import java.util.List;

import com.seeyon.v3x.bbs.domain.V3xBbsBoard;
import com.seeyon.v3x.bbs.manager.BbsBoardManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.inquiry.manager.InquiryManager;
import com.seeyon.v3x.menu.manager.MenuCheck;
import com.seeyon.v3x.news.manager.NewsTypeManager;

/**
 * 集团公共信息的管理按钮权限判断
 * 
 */
public class GroupPublicInfoMenuCheckImpl implements MenuCheck {
    private InquiryManager inquiryManager;

	private BbsBoardManager bbsBoardManager;

	private BulTypeManager bulTypeManager;
	
	private NewsTypeManager newsTypeManager;
	
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

	/*
	 * (non-Javadoc)
	 * 集团公共信息的权限判断
	 * @see com.seeyon.v3x.menu.manager.MenuCheck#check(long) 
	 */
	public boolean check(long memberId, long loginAccountId) {  
		/*boolean isGroupInfoManager = false;
	    try {
	    	boolean bul = bulTypeManager==null ? false:bulTypeManager.isGroupBulTypeManager(memberId);
	    	//原先此处只考虑到了用户具备集团空间新闻管理权时才具有集团公共信息管理权，忽略了用户具有审核权时同样也应显示集团公共信息管理菜单
	    	//modified by Meng Yang at 2009-04-28
	    	boolean news = newsTypeManager==null ? false : (newsTypeManager.isGroupNewsTypeManager(memberId)||newsTypeManager.isGroupNewsTypeAuth(memberId));
	    	boolean inquiry = inquiryManager==null ? false : inquiryManager.hasManageAuthForGroupSpace();
			isGroupInfoManager = bul || news || inquiry;
	    } catch (Exception e) {
			e.printStackTrace();
		}
	    if(!isGroupInfoManager){
	        List<V3xBbsBoard> v3xBbsBoardList =  bbsBoardManager.getAllGroupBbsBoard();
	        for(V3xBbsBoard board : v3xBbsBoardList){
	            if(bbsBoardManager.validUserIsAdmin(board.getId(), memberId)){
	                isGroupInfoManager = true;
	                break;
	            }
	        }
	    }
	    if(isGroupInfoManager){
	    	return true;
	    }
	    else  return false;*/
		return false;
    }

	public BulTypeManager getBulTypeManager() {
		return bulTypeManager;
	}

	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}

	public NewsTypeManager getNewsTypeManager() {
		return newsTypeManager;
	}

	public void setNewsTypeManager(NewsTypeManager newsTypeManager) {
		this.newsTypeManager = newsTypeManager;
	}

}
