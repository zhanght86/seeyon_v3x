/**
 * 
 */
package com.seeyon.v3x.publicManager;

import com.seeyon.v3x.bulletin.manager.BulDataManager;
import com.seeyon.v3x.bulletin.manager.BulTypeManager;
import com.seeyon.v3x.menu.manager.MenuCheck;

/**
 * 公共信息的管理按钮权限判断
 * 
 */
public class PublicGroupBulltionManageMenuCheckImpl implements MenuCheck {
	private BulDataManager bulDataManager;
	private BulTypeManager bulTypeManager;
	public BulTypeManager getBulTypeManager() {
		return bulTypeManager;
	}

	public void setBulTypeManager(BulTypeManager bulTypeManager) {
		this.bulTypeManager = bulTypeManager;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seeyon.v3x.menu.manager.MenuCheck#check(long) 单位公告的权限判断
	 */
	public boolean check(long memberId, long loginAccountId) {
		/*boolean bul = false;
  		 bul = this.bulTypeManager.isGroupBulTypeManager(memberId);	   		 
   		 if(!bul){
   			 bul = this.bulTypeManager.isGroupBulTypeAuth(memberId);
   		 }
		return bul;*/
		return false;
	}

	public BulDataManager getBulDataManager() {
		return bulDataManager;
	}

	public void setBulDataManager(BulDataManager bulDataManager) {
		this.bulDataManager = bulDataManager;
	}

}
